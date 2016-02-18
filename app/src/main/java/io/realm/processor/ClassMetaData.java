package io.realm.processor;

import com.douban.amonsul.StatConstant;
import io.realm.annotations.Ignore;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;

public class ClassMetaData {
    private String className;
    private final TypeElement classType;
    private Set<String> expectedGetters;
    private Set<String> expectedSetters;
    private List<String> fieldNames;
    private List<VariableElement> fields;
    private Map<String, String> getters;
    private boolean hasDefaultConstructor;
    private List<String> ignoreFieldNames;
    private List<VariableElement> indexedFields;
    private Set<ExecutableElement> methods;
    private String packageName;
    private VariableElement primaryKey;
    private DeclaredType realmList;
    private Map<String, String> setters;
    private final Types typeUtils;
    private final List<TypeMirror> validPrimaryKeyTypes;

    public ClassMetaData(ProcessingEnvironment env, TypeElement clazz) {
        this.fields = new ArrayList();
        this.fieldNames = new ArrayList();
        this.ignoreFieldNames = new ArrayList();
        this.indexedFields = new ArrayList();
        this.expectedGetters = new HashSet();
        this.expectedSetters = new HashSet();
        this.methods = new HashSet();
        this.getters = new HashMap();
        this.setters = new HashMap();
        this.classType = clazz;
        this.className = clazz.getSimpleName().toString();
        this.typeUtils = env.getTypeUtils();
        TypeMirror stringType = env.getElementUtils().getTypeElement("java.lang.String").asType();
        this.realmList = this.typeUtils.getDeclaredType(env.getElementUtils().getTypeElement("io.realm.RealmList"), new TypeMirror[]{this.typeUtils.getWildcardType(null, null)});
        this.validPrimaryKeyTypes = Arrays.asList(new TypeMirror[]{stringType, this.typeUtils.getPrimitiveType(TypeKind.SHORT), this.typeUtils.getPrimitiveType(TypeKind.INT), this.typeUtils.getPrimitiveType(TypeKind.LONG)});
    }

    public boolean generate() {
        Element enclosingElement = this.classType.getEnclosingElement();
        if (!enclosingElement.getKind().equals(ElementKind.PACKAGE)) {
            Utils.error("The RealmClass annotation does not support nested classes", this.classType);
            return false;
        } else if (((TypeElement) Utils.getSuperClass(this.classType)).toString().endsWith(".RealmObject")) {
            this.packageName = ((PackageElement) enclosingElement).getQualifiedName().toString();
            if (categorizeClassElements() && checkListTypes() && checkMethods() && checkDefaultConstructor() && checkRequiredGetters() && checkRequireSetters()) {
                return true;
            }
            return false;
        } else {
            Utils.error("A RealmClass annotated object must be derived from RealmObject", this.classType);
            return false;
        }
    }

    private boolean checkMethods() {
        for (ExecutableElement executableElement : this.methods) {
            String methodName = executableElement.getSimpleName().toString();
            Set<Modifier> modifiers = executableElement.getModifiers();
            if (!modifiers.contains(Modifier.STATIC)) {
                if (!modifiers.contains(Modifier.PUBLIC)) {
                    Utils.error("The methods of the model must be public", executableElement);
                    return false;
                } else if (methodName.startsWith("get") || methodName.startsWith(StatConstant.JSON_KEY_IMSI)) {
                    if (!checkGetterMethod(methodName)) {
                        Utils.error(String.format("Getter %s is not associated to any field", new Object[]{methodName}), executableElement);
                        return false;
                    }
                } else if (!methodName.startsWith("set")) {
                    Utils.error("Only getters and setters should be defined in model classes", executableElement);
                    return false;
                } else if (!checkSetterMethod(methodName)) {
                    Utils.error(String.format("Setter %s is not associated to any field", new Object[]{methodName}), executableElement);
                    return false;
                }
            }
        }
        return true;
    }

    private boolean checkListTypes() {
        for (VariableElement field : this.fields) {
            if (this.typeUtils.isAssignable(field.asType(), this.realmList) && Utils.getGenericType(field) == null) {
                Utils.error("No generic type supplied for field", field);
                return false;
            }
        }
        return true;
    }

    private boolean checkSetterMethod(String methodName) {
        String methodMinusSet = methodName.substring(3);
        String methodMinusSetCapitalised = Utils.lowerFirstChar(methodMinusSet);
        String methodMenusSetPlusIs = StatConstant.JSON_KEY_IMSI + methodMinusSet;
        if (this.fieldNames.contains(methodMinusSet)) {
            this.expectedSetters.remove(methodMinusSet);
            if (!this.ignoreFieldNames.contains(methodMinusSet)) {
                this.setters.put(methodMinusSet, methodName);
            }
            return true;
        } else if (this.fieldNames.contains(methodMinusSetCapitalised)) {
            this.expectedSetters.remove(methodMinusSetCapitalised);
            if (!this.ignoreFieldNames.contains(methodMinusSetCapitalised)) {
                this.setters.put(methodMinusSetCapitalised, methodName);
            }
            return true;
        } else if (!this.fieldNames.contains(methodMenusSetPlusIs)) {
            return false;
        } else {
            this.expectedSetters.remove(methodMenusSetPlusIs);
            if (!this.ignoreFieldNames.contains(methodMenusSetPlusIs)) {
                this.setters.put(methodMenusSetPlusIs, methodName);
            }
            return true;
        }
    }

    private boolean checkGetterMethod(String methodName) {
        boolean found = false;
        if (methodName.startsWith(StatConstant.JSON_KEY_IMSI)) {
            String methodMinusIs = methodName.substring(2);
            String methodMinusIsCapitalised = Utils.lowerFirstChar(methodMinusIs);
            if (this.fieldNames.contains(methodName)) {
                this.expectedGetters.remove(methodName);
                if (!this.ignoreFieldNames.contains(methodName)) {
                    this.getters.put(methodName, methodName);
                }
                found = true;
            } else if (this.fieldNames.contains(methodMinusIs)) {
                this.expectedGetters.remove(methodMinusIs);
                if (!this.ignoreFieldNames.contains(methodMinusIs)) {
                    this.getters.put(methodMinusIs, methodName);
                }
                found = true;
            } else if (this.fieldNames.contains(methodMinusIsCapitalised)) {
                this.expectedGetters.remove(methodMinusIsCapitalised);
                if (!this.ignoreFieldNames.contains(methodMinusIsCapitalised)) {
                    this.getters.put(methodMinusIsCapitalised, methodName);
                }
                found = true;
            }
        }
        if (found || !methodName.startsWith("get")) {
            return found;
        }
        String methodMinusGet = methodName.substring(3);
        String methodMinusGetCapitalised = Utils.lowerFirstChar(methodMinusGet);
        if (this.fieldNames.contains(methodMinusGet)) {
            this.expectedGetters.remove(methodMinusGet);
            if (!this.ignoreFieldNames.contains(methodMinusGet)) {
                this.getters.put(methodMinusGet, methodName);
            }
            return true;
        } else if (!this.fieldNames.contains(methodMinusGetCapitalised)) {
            return found;
        } else {
            this.expectedGetters.remove(methodMinusGetCapitalised);
            if (!this.ignoreFieldNames.contains(methodMinusGetCapitalised)) {
                this.getters.put(methodMinusGetCapitalised, methodName);
            }
            return true;
        }
    }

    private boolean checkRequireSetters() {
        for (String expectedSetter : this.expectedSetters) {
            Utils.error("No setter found for field " + expectedSetter);
        }
        return this.expectedSetters.size() == 0;
    }

    private boolean checkRequiredGetters() {
        for (String expectedGetter : this.expectedGetters) {
            Utils.error("No getter found for field " + expectedGetter);
        }
        return this.expectedGetters.size() == 0;
    }

    private boolean checkDefaultConstructor() {
        if (this.hasDefaultConstructor) {
            return true;
        }
        Utils.error("A default public constructor with no argument must be declared if a custom constructor is declared.");
        return false;
    }

    private boolean categorizeClassElements() {
        for (Element element : this.classType.getEnclosedElements()) {
            ElementKind elementKind = element.getKind();
            if (elementKind.equals(ElementKind.FIELD)) {
                VariableElement variableElement = (VariableElement) element;
                String fieldName = variableElement.getSimpleName().toString();
                if (variableElement.getModifiers().contains(Modifier.STATIC)) {
                    continue;
                } else if (variableElement.getAnnotation(Ignore.class) != null) {
                    String ignoredFieldName = variableElement.getSimpleName().toString();
                    this.fieldNames.add(ignoredFieldName);
                    this.ignoreFieldNames.add(ignoredFieldName);
                } else {
                    if (variableElement.getAnnotation(Index.class) != null) {
                        String columnType = (String) Constants.JAVA_TO_COLUMN_TYPES.get(variableElement.asType().toString());
                        if (columnType == null || !(columnType.equals("ColumnType.STRING") || columnType.equals("ColumnType.DATE") || columnType.equals("ColumnType.INTEGER") || columnType.equals("ColumnType.BOOLEAN"))) {
                            Utils.error("@Index is not applicable to this field " + element + ".");
                            return false;
                        }
                        this.indexedFields.add(variableElement);
                    }
                    if (variableElement.getAnnotation(PrimaryKey.class) != null) {
                        if (this.primaryKey != null) {
                            r14 = new Object[2];
                            r14[0] = this.primaryKey.getSimpleName().toString();
                            r14[1] = variableElement.getSimpleName().toString();
                            Utils.error(String.format("@PrimaryKey cannot be defined more than once. It was found here \"%s\" and here \"%s\"", r14));
                            return false;
                        } else if (isValidPrimaryKeyType(variableElement.asType())) {
                            this.primaryKey = variableElement;
                            if (!this.indexedFields.contains(variableElement)) {
                                this.indexedFields.add(variableElement);
                            }
                        } else {
                            Utils.error("\"" + variableElement.getSimpleName().toString() + "\" is not allowed as primary key. See @PrimaryKey for allowed types.");
                            return false;
                        }
                    }
                    if (variableElement.getModifiers().contains(Modifier.PRIVATE)) {
                        this.fields.add(variableElement);
                        this.expectedGetters.add(fieldName);
                        this.expectedSetters.add(fieldName);
                    } else {
                        Utils.error("The fields of the model must be private", variableElement);
                        return false;
                    }
                }
            } else if (elementKind.equals(ElementKind.CONSTRUCTOR)) {
                boolean z = this.hasDefaultConstructor || Utils.isDefaultConstructor(element);
                this.hasDefaultConstructor = z;
            } else if (elementKind.equals(ElementKind.METHOD)) {
                this.methods.add((ExecutableElement) element);
            }
        }
        for (VariableElement field : this.fields) {
            this.fieldNames.add(field.getSimpleName().toString());
        }
        if (this.fields.size() == 0) {
            Utils.error(this.className + " must contain at least 1 persistable field");
        }
        return true;
    }

    public String getSimpleClassName() {
        return this.className;
    }

    public boolean isModelClass() {
        String type = this.classType.toString();
        if (type.equals("io.realm.dynamic.DynamicRealmObject") || type.endsWith(".RealmObject") || type.endsWith(Constants.PROXY_SUFFIX)) {
            return false;
        }
        return true;
    }

    public String getPackageName() {
        return this.packageName;
    }

    public String getFullyQualifiedClassName() {
        return this.packageName + "." + this.className;
    }

    public List<VariableElement> getFields() {
        return this.fields;
    }

    public String getGetter(String fieldName) {
        return (String) this.getters.get(fieldName);
    }

    public String getSetter(String fieldName) {
        return (String) this.setters.get(fieldName);
    }

    public List<VariableElement> getIndexedFields() {
        return this.indexedFields;
    }

    public boolean hasPrimaryKey() {
        return this.primaryKey != null;
    }

    public VariableElement getPrimaryKey() {
        return this.primaryKey;
    }

    public String getPrimaryKeyGetter() {
        return (String) this.getters.get(this.primaryKey.getSimpleName().toString());
    }

    private boolean isValidPrimaryKeyType(TypeMirror type) {
        for (TypeMirror validType : this.validPrimaryKeyTypes) {
            if (this.typeUtils.isAssignable(type, validType)) {
                return true;
            }
        }
        return false;
    }
}
