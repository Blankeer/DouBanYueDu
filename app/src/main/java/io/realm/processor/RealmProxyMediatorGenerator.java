package io.realm.processor;

import com.douban.book.reader.entity.DbCacheEntity;
import com.douban.book.reader.helper.AppUri;
import com.google.analytics.tracking.android.HitTypes;
import io.realm.annotations.RealmModule;
import io.realm.processor.javawriter.JavaWriter;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;

public class RealmProxyMediatorGenerator {
    private static final String REALM_PACKAGE_NAME = "io.realm";
    private final String className;
    private ProcessingEnvironment processingEnvironment;
    private List<String> proxyClasses;
    private List<String> qualifiedModelClasses;
    private List<String> simpleModelClasses;

    private interface ProxySwitchStatement {
        void emitStatement(int i, JavaWriter javaWriter) throws IOException;
    }

    public RealmProxyMediatorGenerator(ProcessingEnvironment processingEnvironment, String className, Set<ClassMetaData> classesToValidate) {
        this.qualifiedModelClasses = new ArrayList();
        this.simpleModelClasses = new ArrayList();
        this.proxyClasses = new ArrayList();
        this.processingEnvironment = processingEnvironment;
        this.className = className;
        for (ClassMetaData metadata : classesToValidate) {
            String simpleName = metadata.getSimpleClassName();
            this.qualifiedModelClasses.add(metadata.getFullyQualifiedClassName());
            this.simpleModelClasses.add(simpleName);
            this.proxyClasses.add(getProxyClassName(simpleName));
        }
    }

    public void generate() throws IOException {
        String qualifiedGeneratedClassName = String.format("%s.%sMediator", new Object[]{REALM_PACKAGE_NAME, this.className});
        JavaWriter writer = new JavaWriter(new BufferedWriter(this.processingEnvironment.getFiler().createSourceFile(qualifiedGeneratedClassName, new Element[0]).openWriter()));
        writer.setIndent("    ");
        writer.emitPackage(REALM_PACKAGE_NAME);
        writer.emitEmptyLine();
        writer.emitImports("android.util.JsonReader", "java.io.IOException", "java.util.ArrayList", "java.util.Collections", "java.util.List", "java.util.Map", "io.realm.exceptions.RealmException", "io.realm.internal.ImplicitTransaction", "io.realm.internal.RealmObjectProxy", "io.realm.internal.RealmProxyMediator", "io.realm.internal.Table", "org.json.JSONException", "org.json.JSONObject");
        writer.emitImports(this.qualifiedModelClasses);
        writer.emitEmptyLine();
        writer.emitAnnotation(RealmModule.class);
        writer.beginType(qualifiedGeneratedClassName, "class", Collections.emptySet(), "RealmProxyMediator", new String[0]);
        writer.emitEmptyLine();
        emitFields(writer);
        emitCreateTableMethod(writer);
        emitValidateTableMethod(writer);
        emitGetFieldNamesMethod(writer);
        emitGetTableNameMethod(writer);
        emitNewInstanceMethod(writer);
        emitGetClassModelList(writer);
        emitGetColumnIndices(writer);
        emitCopyToRealmMethod(writer);
        emitCreteOrUpdateUsingJsonObject(writer);
        emitCreateUsingJsonStream(writer);
        writer.endType();
        writer.close();
    }

    private void emitFields(JavaWriter writer) throws IOException {
        writer.emitField("List<Class<? extends RealmObject>>", "MODEL_CLASSES", EnumSet.of(Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL));
        writer.beginInitializer(true);
        writer.emitStatement("List<Class<? extends RealmObject>> modelClasses = new ArrayList<Class<? extends RealmObject>>()", new Object[0]);
        Iterator i$ = this.simpleModelClasses.iterator();
        while (i$.hasNext()) {
            writer.emitStatement("modelClasses.add(%s.class)", (String) i$.next());
        }
        writer.emitStatement("MODEL_CLASSES = Collections.unmodifiableList(modelClasses)", new Object[0]);
        writer.endInitializer();
        writer.emitEmptyLine();
    }

    private void emitCreateTableMethod(JavaWriter writer) throws IOException {
        writer.emitAnnotation("Override");
        writer.beginMethod("Table", "createTable", EnumSet.of(Modifier.PUBLIC), "Class<? extends RealmObject>", "clazz", "ImplicitTransaction", HitTypes.TRANSACTION);
        emitMediatorSwitch(new ProxySwitchStatement() {
            public void emitStatement(int i, JavaWriter writer) throws IOException {
                writer.emitStatement("return %s.initTable(transaction)", RealmProxyMediatorGenerator.this.proxyClasses.get(i));
            }
        }, writer);
        writer.endMethod();
        writer.emitEmptyLine();
    }

    private void emitValidateTableMethod(JavaWriter writer) throws IOException {
        writer.emitAnnotation("Override");
        writer.beginMethod("void", "validateTable", EnumSet.of(Modifier.PUBLIC), "Class<? extends RealmObject>", "clazz", "ImplicitTransaction", HitTypes.TRANSACTION);
        emitMediatorSwitch(new ProxySwitchStatement() {
            public void emitStatement(int i, JavaWriter writer) throws IOException {
                writer.emitStatement("%s.validateTable(transaction)", RealmProxyMediatorGenerator.this.proxyClasses.get(i));
            }
        }, writer);
        writer.endMethod();
        writer.emitEmptyLine();
    }

    private void emitGetFieldNamesMethod(JavaWriter writer) throws IOException {
        writer.emitAnnotation("Override");
        writer.beginMethod("List<String>", "getFieldNames", EnumSet.of(Modifier.PUBLIC), "Class<? extends RealmObject>", "clazz");
        emitMediatorSwitch(new ProxySwitchStatement() {
            public void emitStatement(int i, JavaWriter writer) throws IOException {
                writer.emitStatement("return %s.getFieldNames()", RealmProxyMediatorGenerator.this.proxyClasses.get(i));
            }
        }, writer);
        writer.endMethod();
        writer.emitEmptyLine();
    }

    private void emitGetTableNameMethod(JavaWriter writer) throws IOException {
        writer.emitAnnotation("Override");
        writer.beginMethod("String", "getTableName", EnumSet.of(Modifier.PUBLIC), "Class<? extends RealmObject>", "clazz");
        emitMediatorSwitch(new ProxySwitchStatement() {
            public void emitStatement(int i, JavaWriter writer) throws IOException {
                writer.emitStatement("return %s.getTableName()", RealmProxyMediatorGenerator.this.proxyClasses.get(i));
            }
        }, writer);
        writer.endMethod();
        writer.emitEmptyLine();
    }

    private void emitNewInstanceMethod(JavaWriter writer) throws IOException {
        writer.emitAnnotation("Override");
        writer.beginMethod("<E extends RealmObject> E", "newInstance", EnumSet.of(Modifier.PUBLIC), "Class<E>", "clazz");
        emitMediatorSwitch(new ProxySwitchStatement() {
            public void emitStatement(int i, JavaWriter writer) throws IOException {
                writer.emitStatement("return clazz.cast(new %s())", RealmProxyMediatorGenerator.this.proxyClasses.get(i));
            }
        }, writer);
        writer.endMethod();
        writer.emitEmptyLine();
    }

    private void emitGetClassModelList(JavaWriter writer) throws IOException {
        writer.emitAnnotation("Override");
        writer.beginMethod("List<Class<? extends RealmObject>>", "getModelClasses", EnumSet.of(Modifier.PUBLIC), new String[0]);
        writer.emitStatement("return MODEL_CLASSES", new Object[0]);
        writer.endMethod();
        writer.emitEmptyLine();
    }

    private void emitGetColumnIndices(JavaWriter writer) throws IOException {
        writer.emitAnnotation("Override");
        writer.beginMethod("Map<String, Long>", "getColumnIndices", EnumSet.of(Modifier.PUBLIC), "Class<? extends RealmObject>", "clazz");
        emitMediatorSwitch(new ProxySwitchStatement() {
            public void emitStatement(int i, JavaWriter writer) throws IOException {
                writer.emitStatement("return %s.getColumnIndices()", RealmProxyMediatorGenerator.this.proxyClasses.get(i));
            }
        }, writer, true);
        writer.endMethod();
        writer.emitEmptyLine();
    }

    private void emitCopyToRealmMethod(JavaWriter writer) throws IOException {
        writer.emitAnnotation("Override");
        writer.beginMethod("<E extends RealmObject> E", "copyOrUpdate", EnumSet.of(Modifier.PUBLIC), "Realm", "realm", "E", "obj", "boolean", "update", "Map<RealmObject, RealmObjectProxy>", DbCacheEntity.TABLE_NAME);
        writer.emitSingleLineComment("This cast is correct because obj is either ", new Object[0]);
        writer.emitSingleLineComment("generated by RealmProxy or the original type extending directly from RealmObject", new Object[0]);
        writer.emitStatement("@SuppressWarnings(\"unchecked\") Class<E> clazz = (Class<E>) ((obj instanceof RealmObjectProxy) ? obj.getClass().getSuperclass() : obj.getClass())", new Object[0]);
        writer.emitEmptyLine();
        emitMediatorSwitch(new ProxySwitchStatement() {
            public void emitStatement(int i, JavaWriter writer) throws IOException {
                writer.emitStatement("return clazz.cast(%s.copyOrUpdate(realm, (%s) obj, update, cache))", RealmProxyMediatorGenerator.this.proxyClasses.get(i), RealmProxyMediatorGenerator.this.simpleModelClasses.get(i));
            }
        }, writer, false);
        writer.endMethod();
        writer.emitEmptyLine();
    }

    private void emitCreteOrUpdateUsingJsonObject(JavaWriter writer) throws IOException {
        writer.emitAnnotation("Override");
        Set of = EnumSet.of(Modifier.PUBLIC);
        List asList = Arrays.asList(new String[]{"Class<E>", "clazz", "Realm", "realm", "JSONObject", "json", "boolean", "update"});
        List asList2 = Arrays.asList(new String[]{"JSONException"});
        writer.beginMethod("<E extends RealmObject> E", "createOrUpdateUsingJsonObject", of, asList, asList2);
        emitMediatorSwitch(new ProxySwitchStatement() {
            public void emitStatement(int i, JavaWriter writer) throws IOException {
                writer.emitStatement("return clazz.cast(%s.createOrUpdateUsingJsonObject(realm, json, update))", RealmProxyMediatorGenerator.this.proxyClasses.get(i));
            }
        }, writer);
        writer.endMethod();
        writer.emitEmptyLine();
    }

    private void emitCreateUsingJsonStream(JavaWriter writer) throws IOException {
        writer.emitAnnotation("Override");
        Set of = EnumSet.of(Modifier.PUBLIC);
        List asList = Arrays.asList(new String[]{"Class<E>", "clazz", "Realm", "realm", "JsonReader", AppUri.PATH_READER});
        List asList2 = Arrays.asList(new String[]{"java.io.IOException"});
        writer.beginMethod("<E extends RealmObject> E", "createUsingJsonStream", of, asList, asList2);
        emitMediatorSwitch(new ProxySwitchStatement() {
            public void emitStatement(int i, JavaWriter writer) throws IOException {
                writer.emitStatement("return clazz.cast(%s.createUsingJsonStream(realm, reader))", RealmProxyMediatorGenerator.this.proxyClasses.get(i));
            }
        }, writer);
        writer.endMethod();
        writer.emitEmptyLine();
    }

    private void emitMediatorSwitch(ProxySwitchStatement statement, JavaWriter writer) throws IOException {
        emitMediatorSwitch(statement, writer, true);
    }

    private void emitMediatorSwitch(ProxySwitchStatement statement, JavaWriter writer, boolean nullPointerCheck) throws IOException {
        if (nullPointerCheck) {
            writer.emitStatement("checkClass(clazz)", new Object[0]);
            writer.emitEmptyLine();
        }
        if (this.simpleModelClasses.size() == 0) {
            writer.emitStatement("throw getMissingProxyClassException(clazz)", new Object[0]);
            return;
        }
        writer.beginControlFlow("if (clazz.equals(%s.class))", this.simpleModelClasses.get(0));
        statement.emitStatement(0, writer);
        for (int i = 1; i < this.simpleModelClasses.size(); i++) {
            writer.nextControlFlow("else if (clazz.equals(%s.class))", this.simpleModelClasses.get(i));
            statement.emitStatement(i, writer);
        }
        writer.nextControlFlow("else", new Object[0]);
        writer.emitStatement("throw getMissingProxyClassException(clazz)", new Object[0]);
        writer.endControlFlow();
    }

    private String getProxyClassName(String clazz) {
        return clazz + Constants.PROXY_SUFFIX;
    }
}
