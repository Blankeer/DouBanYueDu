package io.realm.processor;

import com.douban.book.reader.entity.DbCacheEntity;
import com.douban.book.reader.entity.DbCacheEntity.Column;
import com.douban.book.reader.fragment.GiftPackDetailFragment_;
import com.douban.book.reader.helper.AppUri;
import com.google.analytics.tracking.android.HitTypes;
import io.realm.internal.Table;
import io.realm.processor.javawriter.JavaWriter;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

public class RealmProxyClassGenerator {
    private final String className;
    private Elements elementUtils;
    private ClassMetaData metadata;
    private ProcessingEnvironment processingEnvironment;
    private DeclaredType realmList;
    private TypeMirror realmObject;
    private Types typeUtils;

    public RealmProxyClassGenerator(ProcessingEnvironment processingEnvironment, ClassMetaData metadata) {
        this.processingEnvironment = processingEnvironment;
        this.metadata = metadata;
        this.className = metadata.getSimpleClassName();
    }

    public void generate() throws IOException, UnsupportedOperationException {
        this.elementUtils = this.processingEnvironment.getElementUtils();
        this.typeUtils = this.processingEnvironment.getTypeUtils();
        this.realmObject = this.elementUtils.getTypeElement("io.realm.RealmObject").asType();
        this.realmList = this.typeUtils.getDeclaredType(this.elementUtils.getTypeElement("io.realm.RealmList"), new TypeMirror[]{this.typeUtils.getWildcardType(null, null)});
        String qualifiedGeneratedClassName = String.format("%s.%s", new Object[]{Constants.REALM_PACKAGE_NAME, Utils.getProxyClassName(this.className)});
        JavaWriter writer = new JavaWriter(new BufferedWriter(this.processingEnvironment.getFiler().createSourceFile(qualifiedGeneratedClassName, new Element[0]).openWriter()));
        writer.setIndent("    ");
        writer.emitPackage(Constants.REALM_PACKAGE_NAME).emitEmptyLine();
        Collection imports = new ArrayList();
        imports.add("android.util.JsonReader");
        imports.add("android.util.JsonToken");
        imports.add("io.realm.RealmObject");
        imports.add("io.realm.exceptions.RealmException");
        imports.add("io.realm.exceptions.RealmMigrationNeededException");
        imports.add("io.realm.internal.ColumnType");
        imports.add("io.realm.internal.RealmObjectProxy");
        imports.add("io.realm.internal.Table");
        imports.add("io.realm.internal.TableOrView");
        imports.add("io.realm.internal.ImplicitTransaction");
        imports.add("io.realm.internal.LinkView");
        imports.add("io.realm.internal.android.JsonUtils");
        imports.add("java.io.IOException");
        imports.add("java.util.ArrayList");
        imports.add("java.util.Collections");
        imports.add("java.util.List");
        imports.add("java.util.Arrays");
        imports.add("java.util.Date");
        imports.add("java.util.Map");
        imports.add("java.util.HashMap");
        imports.add("org.json.JSONObject");
        imports.add("org.json.JSONException");
        imports.add("org.json.JSONArray");
        imports.add(this.metadata.getFullyQualifiedClassName());
        for (VariableElement field : this.metadata.getFields()) {
            String fieldTypeName = Table.STRING_DEFAULT_VALUE;
            if (this.typeUtils.isAssignable(field.asType(), this.realmObject)) {
                fieldTypeName = field.asType().toString();
            } else if (this.typeUtils.isAssignable(field.asType(), this.realmList)) {
                fieldTypeName = ((TypeMirror) ((DeclaredType) field.asType()).getTypeArguments().get(0)).toString();
            }
            if (!(fieldTypeName.isEmpty() || imports.contains(fieldTypeName))) {
                imports.add(fieldTypeName);
            }
        }
        Collections.sort(imports);
        writer.emitImports(imports);
        writer.emitEmptyLine();
        writer.beginType(qualifiedGeneratedClassName, "class", EnumSet.of(Modifier.PUBLIC), this.className, "RealmObjectProxy").emitEmptyLine();
        emitClassFields(writer);
        emitAccessors(writer);
        emitInitTableMethod(writer);
        emitValidateTableMethod(writer);
        emitGetTableNameMethod(writer);
        emitGetFieldNamesMethod(writer);
        emitGetColumnIndicesMethod(writer);
        emitCreateOrUpdateUsingJsonObject(writer);
        emitCreateUsingJsonStream(writer);
        emitCopyOrUpdateMethod(writer);
        emitCopyMethod(writer);
        emitUpdateMethod(writer);
        emitToStringMethod(writer);
        emitHashcodeMethod(writer);
        emitEqualsMethod(writer);
        writer.endType();
        writer.close();
    }

    private void emitClassFields(JavaWriter writer) throws IOException {
        for (VariableElement variableElement : this.metadata.getFields()) {
            writer.emitField("long", staticFieldIndexVarName(variableElement), EnumSet.of(Modifier.PRIVATE, Modifier.STATIC));
        }
        writer.emitField("Map<String, Long>", "columnIndices", EnumSet.of(Modifier.PRIVATE, Modifier.STATIC));
        writer.emitField("List<String>", "FIELD_NAMES", EnumSet.of(Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL));
        writer.beginInitializer(true);
        writer.emitStatement("List<String> fieldNames = new ArrayList<String>()", new Object[0]);
        Iterator i$ = this.metadata.getFields().iterator();
        while (i$.hasNext()) {
            writer.emitStatement("fieldNames.add(\"%s\")", ((VariableElement) i$.next()).getSimpleName().toString());
        }
        writer.emitStatement("FIELD_NAMES = Collections.unmodifiableList(fieldNames)", new Object[0]);
        writer.endInitializer();
        writer.emitEmptyLine();
    }

    private void emitAccessors(JavaWriter writer) throws IOException {
        for (VariableElement field : this.metadata.getFields()) {
            String fieldName = field.getSimpleName().toString();
            String fieldTypeCanonicalName = field.asType().toString();
            if (Constants.JAVA_TO_REALM_TYPES.containsKey(fieldTypeCanonicalName)) {
                String realmType = (String) Constants.JAVA_TO_REALM_TYPES.get(fieldTypeCanonicalName);
                String castingType = (String) Constants.CASTING_TYPES.get(fieldTypeCanonicalName);
                writer.emitAnnotation("Override");
                writer.beginMethod(fieldTypeCanonicalName, this.metadata.getGetter(fieldName), EnumSet.of(Modifier.PUBLIC), new String[0]);
                writer.emitStatement("realm.checkIfValid()", new Object[0]);
                writer.emitStatement("return (%s) row.get%s(%s)", fieldTypeCanonicalName, realmType, staticFieldIndexVarName(field));
                writer.endMethod();
                writer.emitEmptyLine();
                writer.emitAnnotation("Override");
                writer.beginMethod("void", this.metadata.getSetter(fieldName), EnumSet.of(Modifier.PUBLIC), fieldTypeCanonicalName, Column.VALUE);
                writer.emitStatement("realm.checkIfValid()", new Object[0]);
                writer.emitStatement("row.set%s(%s, (%s) value)", realmType, staticFieldIndexVarName(field), castingType);
                writer.endMethod();
            } else if (this.typeUtils.isAssignable(field.asType(), this.realmObject)) {
                writer.emitAnnotation("Override");
                writer.beginMethod(fieldTypeCanonicalName, this.metadata.getGetter(fieldName), EnumSet.of(Modifier.PUBLIC), new String[0]);
                writer.beginControlFlow("if (row.isNullLink(%s))", staticFieldIndexVarName(field));
                writer.emitStatement("return null", new Object[0]);
                writer.endControlFlow();
                writer.emitStatement("return realm.get(%s.class, row.getLink(%s))", fieldTypeCanonicalName, staticFieldIndexVarName(field));
                writer.endMethod();
                writer.emitEmptyLine();
                writer.emitAnnotation("Override");
                writer.beginMethod("void", this.metadata.getSetter(fieldName), EnumSet.of(Modifier.PUBLIC), fieldTypeCanonicalName, Column.VALUE);
                writer.beginControlFlow("if (value == null)", new Object[0]);
                writer.emitStatement("row.nullifyLink(%s)", staticFieldIndexVarName(field));
                writer.emitStatement("return", new Object[0]);
                writer.endControlFlow();
                writer.emitStatement("row.setLink(%s, value.row.getIndex())", staticFieldIndexVarName(field));
                writer.endMethod();
            } else if (this.typeUtils.isAssignable(field.asType(), this.realmList)) {
                String genericType = Utils.getGenericType(field);
                writer.emitAnnotation("Override");
                writer.beginMethod(fieldTypeCanonicalName, this.metadata.getGetter(fieldName), EnumSet.of(Modifier.PUBLIC), new String[0]);
                writer.emitStatement("return new RealmList<%s>(%s.class, row.getLinkList(%s), realm)", genericType, genericType, staticFieldIndexVarName(field));
                writer.endMethod();
                writer.emitEmptyLine();
                writer.emitAnnotation("Override");
                writer.beginMethod("void", this.metadata.getSetter(fieldName), EnumSet.of(Modifier.PUBLIC), fieldTypeCanonicalName, Column.VALUE);
                writer.emitStatement("LinkView links = row.getLinkList(%s)", staticFieldIndexVarName(field));
                writer.beginControlFlow("if (value == null)", new Object[0]);
                writer.emitStatement("return", new Object[0]);
                writer.endControlFlow();
                writer.emitStatement("links.clear()", new Object[0]);
                writer.beginControlFlow("for (RealmObject linkedObject : (RealmList<? extends RealmObject>) value)", new Object[0]);
                writer.emitStatement("links.add(linkedObject.row.getIndex())", new Object[0]);
                writer.endControlFlow();
                writer.endMethod();
            } else {
                throw new UnsupportedOperationException(String.format("Type %s of field %s is not supported", new Object[]{fieldTypeCanonicalName, fieldName}));
            }
            writer.emitEmptyLine();
        }
    }

    private void emitInitTableMethod(JavaWriter writer) throws IOException {
        writer.beginMethod("Table", "initTable", EnumSet.of(Modifier.PUBLIC, Modifier.STATIC), "ImplicitTransaction", HitTypes.TRANSACTION);
        writer.beginControlFlow("if (!transaction.hasTable(\"class_" + this.className + "\"))", new Object[0]);
        writer.emitStatement("Table table = transaction.getTable(\"%s%s\")", Table.TABLE_PREFIX, this.className);
        for (VariableElement field : this.metadata.getFields()) {
            String fieldName = field.getSimpleName().toString();
            String fieldTypeCanonicalName = field.asType().toString();
            String fieldTypeSimpleName = Utils.getFieldTypeSimpleName(field);
            if (Constants.JAVA_TO_REALM_TYPES.containsKey(fieldTypeCanonicalName)) {
                writer.emitStatement("table.addColumn(%s, \"%s\")", Constants.JAVA_TO_COLUMN_TYPES.get(fieldTypeCanonicalName), fieldName);
            } else if (this.typeUtils.isAssignable(field.asType(), this.realmObject)) {
                writer.beginControlFlow("if (!transaction.hasTable(\"%s%s\"))", Table.TABLE_PREFIX, fieldTypeSimpleName);
                writer.emitStatement("%s%s.initTable(transaction)", fieldTypeSimpleName, Constants.PROXY_SUFFIX);
                writer.endControlFlow();
                writer.emitStatement("table.addColumnLink(ColumnType.LINK, \"%s\", transaction.getTable(\"%s%s\"))", fieldName, Table.TABLE_PREFIX, fieldTypeSimpleName);
            } else if (this.typeUtils.isAssignable(field.asType(), this.realmList)) {
                String genericType = Utils.getGenericType(field);
                writer.beginControlFlow("if (!transaction.hasTable(\"%s%s\"))", Table.TABLE_PREFIX, genericType);
                writer.emitStatement("%s%s.initTable(transaction)", genericType, Constants.PROXY_SUFFIX);
                writer.endControlFlow();
                writer.emitStatement("table.addColumnLink(ColumnType.LINK_LIST, \"%s\", transaction.getTable(\"%s%s\"))", fieldName, Table.TABLE_PREFIX, genericType);
            }
        }
        for (VariableElement field2 : this.metadata.getIndexedFields()) {
            fieldName = field2.getSimpleName().toString();
            writer.emitStatement("table.addSearchIndex(table.getColumnIndex(\"%s\"))", fieldName);
        }
        if (this.metadata.hasPrimaryKey()) {
            fieldName = this.metadata.getPrimaryKey().getSimpleName().toString();
            writer.emitStatement("table.setPrimaryKey(\"%s\")", fieldName);
        } else {
            writer.emitStatement("table.setPrimaryKey(\"\")", new Object[0]);
        }
        writer.emitStatement("return table", new Object[0]);
        writer.endControlFlow();
        writer.emitStatement("return transaction.getTable(\"%s%s\")", Table.TABLE_PREFIX, this.className);
        writer.endMethod();
        writer.emitEmptyLine();
    }

    private void emitValidateTableMethod(JavaWriter writer) throws IOException {
        writer.beginMethod("void", "validateTable", EnumSet.of(Modifier.PUBLIC, Modifier.STATIC), "ImplicitTransaction", HitTypes.TRANSACTION);
        writer.beginControlFlow("if (transaction.hasTable(\"class_" + this.className + "\"))", new Object[0]);
        writer.emitStatement("Table table = transaction.getTable(\"%s%s\")", Table.TABLE_PREFIX, this.className);
        writer.beginControlFlow("if (table.getColumnCount() != " + this.metadata.getFields().size() + ")", new Object[0]);
        writer.emitStatement("throw new RealmMigrationNeededException(transaction.getPath(), \"Field count does not match - expected %d but was \" + table.getColumnCount())", Integer.valueOf(this.metadata.getFields().size()));
        writer.endControlFlow();
        writer.emitStatement("Map<String, ColumnType> columnTypes = new HashMap<String, ColumnType>()", new Object[0]);
        writer.beginControlFlow("for (long i = 0; i < " + this.metadata.getFields().size() + "; i++)", new Object[0]);
        writer.emitStatement("columnTypes.put(table.getColumnName(i), table.getColumnType(i))", new Object[0]);
        writer.endControlFlow();
        writer.emitEmptyLine();
        writer.emitStatement("columnIndices = new HashMap<String, Long>()", new Object[0]);
        writer.beginControlFlow("for (String fieldName : getFieldNames())", new Object[0]).emitStatement("long index = table.getColumnIndex(fieldName)", new Object[0]).beginControlFlow("if (index == -1)", new Object[0]).emitStatement("throw new RealmMigrationNeededException(transaction.getPath(), \"Field '\" + fieldName + \"' not found for type %s\")", this.metadata.getSimpleClassName()).endControlFlow().emitStatement("columnIndices.put(fieldName, index)", new Object[0]).endControlFlow();
        for (VariableElement field : this.metadata.getFields()) {
            writer.emitStatement("%s = table.getColumnIndex(\"%s\")", staticFieldIndexVarName(field), field.getSimpleName().toString());
        }
        writer.emitEmptyLine();
        long fieldIndex = 0;
        for (VariableElement field2 : this.metadata.getFields()) {
            String fieldName = field2.getSimpleName().toString();
            String fieldTypeCanonicalName = field2.asType().toString();
            String fieldTypeSimpleName = Utils.getFieldTypeSimpleName(field2);
            if (Constants.JAVA_TO_REALM_TYPES.containsKey(fieldTypeCanonicalName)) {
                writer.beginControlFlow("if (!columnTypes.containsKey(\"%s\"))", fieldName);
                writer.emitStatement("throw new RealmMigrationNeededException(transaction.getPath(), \"Missing field '%s'\")", fieldName);
                writer.endControlFlow();
                writer.beginControlFlow("if (columnTypes.get(\"%s\") != %s)", fieldName, Constants.JAVA_TO_COLUMN_TYPES.get(fieldTypeCanonicalName));
                writer.emitStatement("throw new RealmMigrationNeededException(transaction.getPath(), \"Invalid type '%s' for field '%s'\")", fieldTypeSimpleName, fieldName);
                writer.endControlFlow();
                if (field2.equals(this.metadata.getPrimaryKey())) {
                    writer.beginControlFlow("if (table.getPrimaryKey() != table.getColumnIndex(\"%s\"))", fieldName);
                    writer.emitStatement("throw new RealmMigrationNeededException(transaction.getPath(), \"Primary key not defined for field '%s'\")", fieldName);
                    writer.endControlFlow();
                }
                if (this.metadata.getIndexedFields().contains(field2)) {
                    writer.beginControlFlow("if (!table.hasSearchIndex(table.getColumnIndex(\"%s\")))", fieldName);
                    writer.emitStatement("throw new RealmMigrationNeededException(transaction.getPath(), \"Index not defined for field '%s'\")", fieldName);
                    writer.endControlFlow();
                }
            } else if (this.typeUtils.isAssignable(field2.asType(), this.realmObject)) {
                writer.beginControlFlow("if (!columnTypes.containsKey(\"%s\"))", fieldName);
                writer.emitStatement("throw new RealmMigrationNeededException(transaction.getPath(), \"Missing field '%s'\")", fieldName);
                writer.endControlFlow();
                writer.beginControlFlow("if (columnTypes.get(\"%s\") != ColumnType.LINK)", fieldName);
                writer.emitStatement("throw new RealmMigrationNeededException(transaction.getPath(), \"Invalid type '%s' for field '%s'\")", fieldTypeSimpleName, fieldName);
                writer.endControlFlow();
                writer.beginControlFlow("if (!transaction.hasTable(\"%s%s\"))", Table.TABLE_PREFIX, fieldTypeSimpleName);
                writer.emitStatement("throw new RealmMigrationNeededException(transaction.getPath(), \"Missing class '%s%s' for field '%s'\")", Table.TABLE_PREFIX, fieldTypeSimpleName, fieldName);
                writer.endControlFlow();
                writer.emitStatement("Table table_%d = transaction.getTable(\"%s%s\")", Long.valueOf(fieldIndex), Table.TABLE_PREFIX, fieldTypeSimpleName);
                writer.beginControlFlow("if (!table.getLinkTarget(%s).hasSameSchema(table_%d))", staticFieldIndexVarName(field2), Long.valueOf(fieldIndex));
                writer.emitStatement("throw new RealmMigrationNeededException(transaction.getPath(), \"Invalid RealmObject for field '%s': '\" + table.getLinkTarget(%s).getName() + \"' expected - was '\" + table_%d.getName() + \"'\")", fieldName, staticFieldIndexVarName(field2), Long.valueOf(fieldIndex));
                writer.endControlFlow();
            } else if (this.typeUtils.isAssignable(field2.asType(), this.realmList)) {
                String genericType = Utils.getGenericType(field2);
                writer.beginControlFlow("if (!columnTypes.containsKey(\"%s\"))", fieldName);
                writer.emitStatement("throw new RealmMigrationNeededException(transaction.getPath(), \"Missing field '%s'\")", fieldName);
                writer.endControlFlow();
                writer.beginControlFlow("if (columnTypes.get(\"%s\") != ColumnType.LINK_LIST)", fieldName);
                writer.emitStatement("throw new RealmMigrationNeededException(transaction.getPath(), \"Invalid type '%s' for field '%s'\")", genericType, fieldName);
                writer.endControlFlow();
                writer.beginControlFlow("if (!transaction.hasTable(\"%s%s\"))", Table.TABLE_PREFIX, genericType);
                writer.emitStatement("throw new RealmMigrationNeededException(transaction.getPath(), \"Missing class '%s%s' for field '%s'\")", Table.TABLE_PREFIX, genericType, fieldName);
                writer.endControlFlow();
                writer.emitStatement("Table table_%d = transaction.getTable(\"%s%s\")", Long.valueOf(fieldIndex), Table.TABLE_PREFIX, genericType);
                writer.beginControlFlow("if (!table.getLinkTarget(%s).hasSameSchema(table_%d))", staticFieldIndexVarName(field2), Long.valueOf(fieldIndex));
                writer.emitStatement("throw new RealmMigrationNeededException(transaction.getPath(), \"Invalid RealmList type for field '%s': '\" + table.getLinkTarget(%s).getName() + \"' expected - was '\" + table_%d.getName() + \"'\")", fieldName, staticFieldIndexVarName(field2), Long.valueOf(fieldIndex));
                writer.endControlFlow();
            }
            fieldIndex++;
        }
        writer.nextControlFlow("else", new Object[0]);
        writer.emitStatement("throw new RealmMigrationNeededException(transaction.getPath(), \"The %s class is missing from the schema for this Realm.\")", this.metadata.getSimpleClassName());
        writer.endControlFlow();
        writer.endMethod();
        writer.emitEmptyLine();
    }

    private void emitGetTableNameMethod(JavaWriter writer) throws IOException {
        writer.beginMethod("String", "getTableName", EnumSet.of(Modifier.PUBLIC, Modifier.STATIC), new String[0]);
        writer.emitStatement("return \"%s%s\"", Table.TABLE_PREFIX, this.className);
        writer.endMethod();
        writer.emitEmptyLine();
    }

    private void emitGetFieldNamesMethod(JavaWriter writer) throws IOException {
        writer.beginMethod("List<String>", "getFieldNames", EnumSet.of(Modifier.PUBLIC, Modifier.STATIC), new String[0]);
        writer.emitStatement("return FIELD_NAMES", new Object[0]);
        writer.endMethod();
        writer.emitEmptyLine();
    }

    private void emitGetColumnIndicesMethod(JavaWriter writer) throws IOException {
        writer.beginMethod("Map<String,Long>", "getColumnIndices", EnumSet.of(Modifier.PUBLIC, Modifier.STATIC), new String[0]);
        writer.emitStatement("return columnIndices", new Object[0]);
        writer.endMethod();
        writer.emitEmptyLine();
    }

    private void emitCopyOrUpdateMethod(JavaWriter writer) throws IOException {
        writer.beginMethod(this.className, "copyOrUpdate", EnumSet.of(Modifier.PUBLIC, Modifier.STATIC), "Realm", "realm", this.className, "object", "boolean", "update", "Map<RealmObject,RealmObjectProxy>", DbCacheEntity.TABLE_NAME);
        writer.beginControlFlow("if (object.realm != null && object.realm.getPath().equals(realm.getPath()))", new Object[0]).emitStatement("return object", new Object[0]).endControlFlow();
        if (this.metadata.hasPrimaryKey()) {
            Object[] objArr = new Object[]{this.className};
            writer.emitStatement("%s realmObject = null", this.className).emitStatement("boolean canUpdate = update", new Object[0]).beginControlFlow("if (canUpdate)", new Object[0]).emitStatement("Table table = realm.getTable(%s.class)", objArr).emitStatement("long pkColumnIndex = table.getPrimaryKey()", new Object[0]);
            if (Utils.isString(this.metadata.getPrimaryKey())) {
                objArr = new Object[]{this.metadata.getPrimaryKeyGetter()};
                writer.beginControlFlow("if (object.%s() == null)", this.metadata.getPrimaryKeyGetter()).emitStatement("throw new IllegalArgumentException(\"Primary key value must not be null.\")", new Object[0]).endControlFlow().emitStatement("long rowIndex = table.findFirstString(pkColumnIndex, object.%s())", objArr);
            } else {
                writer.emitStatement("long rowIndex = table.findFirstLong(pkColumnIndex, object.%s())", this.metadata.getPrimaryKeyGetter());
            }
            writer.beginControlFlow("if (rowIndex != TableOrView.NO_MATCH)", new Object[0]).emitStatement("realmObject = new %s()", Utils.getProxyClassName(this.className)).emitStatement("realmObject.realm = realm", new Object[0]).emitStatement("realmObject.row = table.getUncheckedRow(rowIndex)", new Object[0]).emitStatement("cache.put(object, (RealmObjectProxy) realmObject)", new Object[0]).nextControlFlow("else", new Object[0]).emitStatement("canUpdate = false", new Object[0]).endControlFlow();
            writer.endControlFlow();
            writer.emitEmptyLine().beginControlFlow("if (canUpdate)", new Object[0]).emitStatement("return update(realm, realmObject, object, cache)", new Object[0]).nextControlFlow("else", new Object[0]).emitStatement("return copy(realm, object, update, cache)", new Object[0]).endControlFlow();
        } else {
            writer.emitStatement("return copy(realm, object, update, cache)", new Object[0]);
        }
        writer.endMethod();
        writer.emitEmptyLine();
    }

    private void emitCopyMethod(JavaWriter writer) throws IOException {
        writer.beginMethod(this.className, "copy", EnumSet.of(Modifier.PUBLIC, Modifier.STATIC), "Realm", "realm", this.className, "newObject", "boolean", "update", "Map<RealmObject,RealmObjectProxy>", DbCacheEntity.TABLE_NAME);
        if (this.metadata.hasPrimaryKey()) {
            writer.emitStatement("%s realmObject = realm.createObject(%s.class, newObject.%s())", this.className, this.className, this.metadata.getPrimaryKeyGetter());
        } else {
            writer.emitStatement("%s realmObject = realm.createObject(%s.class)", this.className, this.className);
        }
        writer.emitStatement("cache.put(newObject, (RealmObjectProxy) realmObject)", new Object[0]);
        for (VariableElement field : this.metadata.getFields()) {
            String fieldName = field.getSimpleName().toString();
            String fieldType = field.asType().toString();
            Object[] objArr;
            if (this.typeUtils.isAssignable(field.asType(), this.realmObject)) {
                objArr = new Object[]{fieldName};
                objArr = new Object[]{fieldType, fieldName, fieldType, fieldName};
                objArr = new Object[]{fieldName};
                objArr = new Object[]{this.metadata.getSetter(fieldName), fieldName};
                objArr = new Object[]{this.metadata.getSetter(fieldName), Utils.getProxyClassSimpleName(field), fieldName};
                writer.emitEmptyLine().emitStatement("%s %sObj = newObject.%s()", fieldType, fieldName, this.metadata.getGetter(fieldName)).beginControlFlow("if (%sObj != null)", objArr).emitStatement("%s cache%s = (%s) cache.get(%sObj)", objArr).beginControlFlow("if (cache%s != null)", objArr).emitStatement("realmObject.%s(cache%s)", objArr).nextControlFlow("else", new Object[0]).emitStatement("realmObject.%s(%s.copyOrUpdate(realm, %sObj, update, cache))", objArr).endControlFlow().endControlFlow();
            } else if (this.typeUtils.isAssignable(field.asType(), this.realmList)) {
                objArr = new Object[]{fieldName};
                objArr = new Object[]{Utils.getGenericType(field), fieldName, this.metadata.getGetter(fieldName)};
                objArr = new Object[]{fieldName};
                objArr = new Object[]{Utils.getGenericType(field), fieldName, fieldName};
                objArr = new Object[]{Utils.getGenericType(field), fieldName, Utils.getGenericType(field), fieldName};
                objArr = new Object[]{fieldName};
                objArr = new Object[]{fieldName, fieldName};
                objArr = new Object[]{fieldName, Utils.getProxyClassSimpleName(field), fieldName};
                writer.emitEmptyLine().emitStatement("RealmList<%s> %sList = newObject.%s()", Utils.getGenericType(field), fieldName, this.metadata.getGetter(fieldName)).beginControlFlow("if (%sList != null)", objArr).emitStatement("RealmList<%s> %sRealmList = realmObject.%s()", objArr).beginControlFlow("for (int i = 0; i < %sList.size(); i++)", objArr).emitStatement("%s %sItem = %sList.get(i)", objArr).emitStatement("%s cache%s = (%s) cache.get(%sItem)", objArr).beginControlFlow("if (cache%s != null)", objArr).emitStatement("%sRealmList.add(cache%s)", objArr).nextControlFlow("else", new Object[0]).emitStatement("%sRealmList.add(%s.copyOrUpdate(realm, %sList.get(i), update, cache))", objArr).endControlFlow().endControlFlow().endControlFlow().emitEmptyLine();
            } else if (Constants.NULLABLE_JAVA_TYPES.containsKey(fieldType)) {
                writer.emitStatement("realmObject.%s(newObject.%s() != null ? newObject.%s() : %s)", this.metadata.getSetter(fieldName), this.metadata.getGetter(fieldName), this.metadata.getGetter(fieldName), Constants.NULLABLE_JAVA_TYPES.get(fieldType));
            } else {
                writer.emitStatement("realmObject.%s(newObject.%s())", this.metadata.getSetter(fieldName), this.metadata.getGetter(fieldName));
            }
        }
        writer.emitStatement("return realmObject", new Object[0]);
        writer.endMethod();
        writer.emitEmptyLine();
    }

    private void emitUpdateMethod(JavaWriter writer) throws IOException {
        writer.beginMethod(this.className, "update", EnumSet.of(Modifier.STATIC), "Realm", "realm", this.className, "realmObject", this.className, "newObject", "Map<RealmObject, RealmObjectProxy>", DbCacheEntity.TABLE_NAME);
        for (VariableElement field : this.metadata.getFields()) {
            String fieldName = field.getSimpleName().toString();
            Object[] objArr;
            if (this.typeUtils.isAssignable(field.asType(), this.realmObject)) {
                objArr = new Object[]{fieldName};
                objArr = new Object[]{Utils.getFieldTypeSimpleName(field), fieldName, Utils.getFieldTypeSimpleName(field), fieldName};
                objArr = new Object[]{fieldName};
                objArr = new Object[]{this.metadata.getSetter(fieldName), fieldName};
                objArr = new Object[]{this.metadata.getSetter(fieldName), Utils.getProxyClassSimpleName(field), fieldName, Utils.getFieldTypeSimpleName(field)};
                objArr = new Object[]{this.metadata.getSetter(fieldName)};
                writer.emitStatement("%s %sObj = newObject.%s()", Utils.getFieldTypeSimpleName(field), fieldName, this.metadata.getGetter(fieldName)).beginControlFlow("if (%sObj != null)", objArr).emitStatement("%s cache%s = (%s) cache.get(%sObj)", objArr).beginControlFlow("if (cache%s != null)", objArr).emitStatement("realmObject.%s(cache%s)", objArr).nextControlFlow("else", new Object[0]).emitStatement("realmObject.%s(%s.copyOrUpdate(realm, %sObj, true, cache))", objArr).endControlFlow().nextControlFlow("else", new Object[0]).emitStatement("realmObject.%s(null)", objArr).endControlFlow();
            } else if (this.typeUtils.isAssignable(field.asType(), this.realmList)) {
                objArr = new Object[]{Utils.getGenericType(field), fieldName, this.metadata.getGetter(fieldName)};
                objArr = new Object[]{fieldName};
                objArr = new Object[]{fieldName};
                objArr = new Object[]{fieldName};
                objArr = new Object[]{Utils.getGenericType(field), fieldName, fieldName};
                objArr = new Object[]{Utils.getGenericType(field), fieldName, Utils.getGenericType(field), fieldName};
                objArr = new Object[]{fieldName};
                objArr = new Object[]{fieldName, fieldName};
                objArr = new Object[]{fieldName, Utils.getProxyClassSimpleName(field), fieldName};
                writer.emitStatement("RealmList<%s> %sList = newObject.%s()", Utils.getGenericType(field), fieldName, this.metadata.getGetter(fieldName)).emitStatement("RealmList<%s> %sRealmList = realmObject.%s()", objArr).emitStatement("%sRealmList.clear()", objArr).beginControlFlow("if (%sList != null)", objArr).beginControlFlow("for (int i = 0; i < %sList.size(); i++)", objArr).emitStatement("%s %sItem = %sList.get(i)", objArr).emitStatement("%s cache%s = (%s) cache.get(%sItem)", objArr).beginControlFlow("if (cache%s != null)", objArr).emitStatement("%sRealmList.add(cache%s)", objArr).nextControlFlow("else", new Object[0]).emitStatement("%sRealmList.add(%s.copyOrUpdate(realm, %sList.get(i), true, cache))", objArr).endControlFlow().endControlFlow().endControlFlow();
            } else if (field != this.metadata.getPrimaryKey()) {
                if (Constants.NULLABLE_JAVA_TYPES.containsKey(field.asType().toString())) {
                    writer.emitStatement("realmObject.%s(newObject.%s() != null ? newObject.%s() : %s)", this.metadata.getSetter(fieldName), this.metadata.getGetter(fieldName), this.metadata.getGetter(fieldName), Constants.NULLABLE_JAVA_TYPES.get(field.asType().toString()));
                } else {
                    writer.emitStatement("realmObject.%s(newObject.%s())", this.metadata.getSetter(fieldName), this.metadata.getGetter(fieldName));
                }
            }
        }
        writer.emitStatement("return realmObject", new Object[0]);
        writer.endMethod();
        writer.emitEmptyLine();
    }

    private void emitToStringMethod(JavaWriter writer) throws IOException {
        writer.emitAnnotation("Override");
        writer.beginMethod("String", "toString", EnumSet.of(Modifier.PUBLIC), new String[0]);
        writer.beginControlFlow("if (!isValid())", new Object[0]);
        writer.emitStatement("return \"Invalid object\"", new Object[0]);
        writer.endControlFlow();
        writer.emitStatement("StringBuilder stringBuilder = new StringBuilder(\"%s = [\")", this.className);
        List<VariableElement> fields = this.metadata.getFields();
        for (int i = 0; i < fields.size(); i++) {
            VariableElement field = (VariableElement) fields.get(i);
            String fieldName = field.getSimpleName().toString();
            writer.emitStatement("stringBuilder.append(\"{%s:\")", fieldName);
            if (this.typeUtils.isAssignable(field.asType(), this.realmObject)) {
                String fieldTypeSimpleName = Utils.getFieldTypeSimpleName(field);
                writer.emitStatement("stringBuilder.append(%s() != null ? \"%s\" : \"null\")", this.metadata.getGetter(fieldName), fieldTypeSimpleName);
            } else if (this.typeUtils.isAssignable(field.asType(), this.realmList)) {
                writer.emitStatement("stringBuilder.append(\"RealmList<%s>[\").append(%s().size()).append(\"]\")", Utils.getGenericType(field), this.metadata.getGetter(fieldName));
            } else {
                writer.emitStatement("stringBuilder.append(%s())", this.metadata.getGetter(fieldName));
            }
            writer.emitStatement("stringBuilder.append(\"}\")", new Object[0]);
            if (i < fields.size() - 1) {
                writer.emitStatement("stringBuilder.append(\",\")", new Object[0]);
            }
        }
        writer.emitStatement("stringBuilder.append(\"]\")", new Object[0]);
        writer.emitStatement("return stringBuilder.toString()", new Object[0]);
        writer.endMethod();
        writer.emitEmptyLine();
    }

    private void emitHashcodeMethod(JavaWriter writer) throws IOException {
        writer.emitAnnotation("Override");
        writer.beginMethod("int", GiftPackDetailFragment_.HASH_CODE_ARG, EnumSet.of(Modifier.PUBLIC), new String[0]);
        writer.emitStatement("String realmName = realm.getPath()", new Object[0]);
        writer.emitStatement("String tableName = row.getTable().getName()", new Object[0]);
        writer.emitStatement("long rowIndex = row.getIndex()", new Object[0]);
        writer.emitEmptyLine();
        writer.emitStatement("int result = 17", new Object[0]);
        writer.emitStatement("result = 31 * result + ((realmName != null) ? realmName.hashCode() : 0)", new Object[0]);
        writer.emitStatement("result = 31 * result + ((tableName != null) ? tableName.hashCode() : 0)", new Object[0]);
        writer.emitStatement("result = 31 * result + (int) (rowIndex ^ (rowIndex >>> 32))", new Object[0]);
        writer.emitStatement("return result", new Object[0]);
        writer.endMethod();
        writer.emitEmptyLine();
    }

    private void emitEqualsMethod(JavaWriter writer) throws IOException {
        String proxyClassName = this.className + Constants.PROXY_SUFFIX;
        writer.emitAnnotation("Override");
        writer.beginMethod("boolean", "equals", EnumSet.of(Modifier.PUBLIC), "Object", "o");
        writer.emitStatement("if (this == o) return true", new Object[0]);
        writer.emitStatement("if (o == null || getClass() != o.getClass()) return false", new Object[0]);
        writer.emitStatement("%s a%s = (%s)o", proxyClassName, this.className, proxyClassName);
        writer.emitEmptyLine();
        writer.emitStatement("String path = realm.getPath()", new Object[0]);
        writer.emitStatement("String otherPath = a%s.realm.getPath()", this.className);
        writer.emitStatement("if (path != null ? !path.equals(otherPath) : otherPath != null) return false;", new Object[0]);
        writer.emitEmptyLine();
        writer.emitStatement("String tableName = row.getTable().getName()", new Object[0]);
        writer.emitStatement("String otherTableName = a%s.row.getTable().getName()", this.className);
        writer.emitStatement("if (tableName != null ? !tableName.equals(otherTableName) : otherTableName != null) return false", new Object[0]);
        writer.emitEmptyLine();
        writer.emitStatement("if (row.getIndex() != a%s.row.getIndex()) return false", this.className);
        writer.emitEmptyLine();
        writer.emitStatement("return true", new Object[0]);
        writer.endMethod();
        writer.emitEmptyLine();
    }

    private void emitCreateOrUpdateUsingJsonObject(JavaWriter writer) throws IOException {
        writer.beginMethod(this.className, "createOrUpdateUsingJsonObject", EnumSet.of(Modifier.PUBLIC, Modifier.STATIC), Arrays.asList(new String[]{"Realm", "realm", "JSONObject", "json", "boolean", "update"}), Arrays.asList(new String[]{"JSONException"}));
        if (this.metadata.hasPrimaryKey()) {
            String pkType = Utils.isString(this.metadata.getPrimaryKey()) ? "String" : "Long";
            Object[] objArr = new Object[]{this.metadata.getPrimaryKey().getSimpleName()};
            objArr = new Object[]{pkType, pkType, this.metadata.getPrimaryKey().getSimpleName()};
            objArr = new Object[]{Utils.getProxyClassName(this.className)};
            objArr = new Object[]{this.className};
            writer.emitStatement("%s obj = null", this.className).beginControlFlow("if (update)", new Object[0]).emitStatement("Table table = realm.getTable(%s.class)", this.className).emitStatement("long pkColumnIndex = table.getPrimaryKey()", new Object[0]).beginControlFlow("if (!json.isNull(\"%s\"))", objArr).emitStatement("long rowIndex = table.findFirst%s(pkColumnIndex, json.get%s(\"%s\"))", objArr).beginControlFlow("if (rowIndex != TableOrView.NO_MATCH)", new Object[0]).emitStatement("obj = new %s()", objArr).emitStatement("obj.realm = realm", new Object[0]).emitStatement("obj.row = table.getUncheckedRow(rowIndex)", new Object[0]).endControlFlow().endControlFlow().endControlFlow().beginControlFlow("if (obj == null)", new Object[0]).emitStatement("obj = realm.createObject(%s.class)", objArr).endControlFlow();
        } else {
            writer.emitStatement("%s obj = realm.createObject(%s.class)", this.className, this.className);
        }
        for (VariableElement field : this.metadata.getFields()) {
            String fieldName = field.getSimpleName().toString();
            String qualifiedFieldType = field.asType().toString();
            if (this.typeUtils.isAssignable(field.asType(), this.realmObject)) {
                RealmJsonTypeHelper.emitFillRealmObjectWithJsonValue(this.metadata.getSetter(fieldName), fieldName, qualifiedFieldType, Utils.getProxyClassSimpleName(field), writer);
            } else if (this.typeUtils.isAssignable(field.asType(), this.realmList)) {
                RealmJsonTypeHelper.emitFillRealmListWithJsonValue(this.metadata.getGetter(fieldName), this.metadata.getSetter(fieldName), fieldName, ((TypeMirror) ((DeclaredType) field.asType()).getTypeArguments().get(0)).toString(), Utils.getProxyClassSimpleName(field), writer);
            } else {
                RealmJsonTypeHelper.emitFillJavaTypeWithJsonValue(this.metadata.getSetter(fieldName), fieldName, qualifiedFieldType, writer);
            }
        }
        writer.emitStatement("return obj", new Object[0]);
        writer.endMethod();
        writer.emitEmptyLine();
    }

    private void emitCreateUsingJsonStream(JavaWriter writer) throws IOException {
        Set of = EnumSet.of(Modifier.PUBLIC, Modifier.STATIC);
        List asList = Arrays.asList(new String[]{"Realm", "realm", "JsonReader", AppUri.PATH_READER});
        List asList2 = Arrays.asList(new String[]{"IOException"});
        writer.beginMethod(this.className, "createUsingJsonStream", of, asList, asList2);
        writer.emitStatement("%s obj = realm.createObject(%s.class)", this.className, this.className);
        writer.emitStatement("reader.beginObject()", new Object[0]);
        writer.beginControlFlow("while (reader.hasNext())", new Object[0]);
        writer.emitStatement("String name = reader.nextName()", new Object[0]);
        List<VariableElement> fields = this.metadata.getFields();
        for (int i = 0; i < fields.size(); i++) {
            VariableElement field = (VariableElement) fields.get(i);
            String fieldName = field.getSimpleName().toString();
            String qualifiedFieldType = field.asType().toString();
            if (i == 0) {
                writer.beginControlFlow("if (name.equals(\"%s\") && reader.peek() != JsonToken.NULL)", fieldName);
            } else {
                writer.nextControlFlow("else if (name.equals(\"%s\")  && reader.peek() != JsonToken.NULL)", fieldName);
            }
            if (this.typeUtils.isAssignable(field.asType(), this.realmObject)) {
                RealmJsonTypeHelper.emitFillRealmObjectFromStream(this.metadata.getSetter(fieldName), fieldName, qualifiedFieldType, Utils.getProxyClassSimpleName(field), writer);
            } else if (this.typeUtils.isAssignable(field.asType(), this.realmList)) {
                RealmJsonTypeHelper.emitFillRealmListFromStream(this.metadata.getGetter(fieldName), this.metadata.getSetter(fieldName), ((TypeMirror) ((DeclaredType) field.asType()).getTypeArguments().get(0)).toString(), Utils.getProxyClassSimpleName(field), writer);
            } else {
                RealmJsonTypeHelper.emitFillJavaTypeFromStream(this.metadata.getSetter(fieldName), fieldName, qualifiedFieldType, writer);
            }
        }
        if (fields.size() > 0) {
            writer.nextControlFlow("else", new Object[0]);
            writer.emitStatement("reader.skipValue()", new Object[0]);
            writer.endControlFlow();
        }
        writer.endControlFlow();
        writer.emitStatement("reader.endObject()", new Object[0]);
        writer.emitStatement("return obj", new Object[0]);
        writer.endMethod();
        writer.emitEmptyLine();
    }

    private String staticFieldIndexVarName(VariableElement variableElement) {
        return "INDEX_" + variableElement.getSimpleName().toString().toUpperCase();
    }
}
