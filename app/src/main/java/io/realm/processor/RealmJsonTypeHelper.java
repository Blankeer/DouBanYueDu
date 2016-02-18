package io.realm.processor;

import io.realm.processor.javawriter.JavaWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RealmJsonTypeHelper {
    private static final Map<String, JsonToRealmTypeConverter> JAVA_TO_JSON_TYPES;

    private interface JsonToRealmTypeConverter {
        void emitStreamTypeConversion(String str, String str2, String str3, JavaWriter javaWriter) throws IOException;

        void emitTypeConversion(String str, String str2, String str3, JavaWriter javaWriter) throws IOException;
    }

    private static class SimpleTypeConverter implements JsonToRealmTypeConverter {
        private final String castType;
        private final String jsonType;

        private SimpleTypeConverter(String castType, String jsonType) {
            this.castType = castType;
            this.jsonType = jsonType;
        }

        public void emitTypeConversion(String setter, String fieldName, String fieldType, JavaWriter writer) throws IOException {
            Object[] objArr = new Object[]{setter, this.castType, this.jsonType, fieldName};
            writer.beginControlFlow("if (!json.isNull(\"%s\"))", fieldName).emitStatement("obj.%s((%s) json.get%s(\"%s\"))", objArr).endControlFlow();
        }

        public void emitStreamTypeConversion(String setter, String fieldName, String fieldType, JavaWriter writer) throws IOException {
            writer.emitStatement("obj.%s((%s) reader.next%s())", setter, this.castType, this.jsonType);
        }
    }

    static {
        JAVA_TO_JSON_TYPES = new HashMap();
        JAVA_TO_JSON_TYPES.put("byte", new SimpleTypeConverter("Int", null));
        JAVA_TO_JSON_TYPES.put("short", new SimpleTypeConverter("Int", null));
        JAVA_TO_JSON_TYPES.put("int", new SimpleTypeConverter("Int", null));
        JAVA_TO_JSON_TYPES.put("long", new SimpleTypeConverter("Long", null));
        JAVA_TO_JSON_TYPES.put("float", new SimpleTypeConverter("Double", null));
        JAVA_TO_JSON_TYPES.put("double", new SimpleTypeConverter("Double", null));
        JAVA_TO_JSON_TYPES.put("boolean", new SimpleTypeConverter("Boolean", null));
        JAVA_TO_JSON_TYPES.put("Byte", new SimpleTypeConverter("Int", null));
        JAVA_TO_JSON_TYPES.put("Short", new SimpleTypeConverter("Int", null));
        JAVA_TO_JSON_TYPES.put("Integer", new SimpleTypeConverter("Int", null));
        JAVA_TO_JSON_TYPES.put("Long", new SimpleTypeConverter("Long", null));
        JAVA_TO_JSON_TYPES.put("Float", new SimpleTypeConverter("Double", null));
        JAVA_TO_JSON_TYPES.put("Double", new SimpleTypeConverter("Double", null));
        JAVA_TO_JSON_TYPES.put("Boolean", new SimpleTypeConverter("Boolean", null));
        JAVA_TO_JSON_TYPES.put("java.lang.String", new SimpleTypeConverter("String", null));
        JAVA_TO_JSON_TYPES.put("java.util.Date", new JsonToRealmTypeConverter() {
            public void emitTypeConversion(String setter, String fieldName, String fieldType, JavaWriter writer) throws IOException {
                Object[] objArr = new Object[]{fieldName};
                objArr = new Object[]{setter};
                objArr = new Object[]{setter, fieldName};
                writer.beginControlFlow("if (!json.isNull(\"%s\"))", fieldName).emitStatement("Object timestamp = json.get(\"%s\")", objArr).beginControlFlow("if (timestamp instanceof String)", new Object[0]).emitStatement("obj.%s(JsonUtils.stringToDate((String) timestamp))", objArr).nextControlFlow("else", new Object[0]).emitStatement("obj.%s(new Date(json.getLong(\"%s\")))", objArr).endControlFlow().endControlFlow();
            }

            public void emitStreamTypeConversion(String setter, String fieldName, String fieldType, JavaWriter writer) throws IOException {
                Object[] objArr = new Object[]{setter};
                objArr = new Object[]{setter};
                writer.beginControlFlow("if (reader.peek() == JsonToken.NUMBER)", new Object[0]).emitStatement("long timestamp = reader.nextLong()", fieldName).beginControlFlow("if (timestamp > -1)", new Object[0]).emitStatement("obj.%s(new Date(timestamp))", objArr).endControlFlow().nextControlFlow("else", new Object[0]).emitStatement("obj.%s(JsonUtils.stringToDate(reader.nextString()))", objArr).endControlFlow();
            }
        });
        JAVA_TO_JSON_TYPES.put("byte[]", new JsonToRealmTypeConverter() {
            public void emitTypeConversion(String setter, String fieldName, String fieldType, JavaWriter writer) throws IOException {
                Object[] objArr = new Object[]{setter, fieldName};
                writer.beginControlFlow("if (!json.isNull(\"%s\"))", fieldName).emitStatement("obj.%s(JsonUtils.stringToBytes(json.getString(\"%s\")))", objArr).endControlFlow();
            }

            public void emitStreamTypeConversion(String setter, String fieldName, String fieldType, JavaWriter writer) throws IOException {
                writer.emitStatement("obj.%s(JsonUtils.stringToBytes(reader.nextString()))", setter);
            }
        });
    }

    public static void emitFillJavaTypeWithJsonValue(String setter, String fieldName, String qualifiedFieldType, JavaWriter writer) throws IOException {
        JsonToRealmTypeConverter typeEmitter = (JsonToRealmTypeConverter) JAVA_TO_JSON_TYPES.get(qualifiedFieldType);
        if (typeEmitter != null) {
            typeEmitter.emitTypeConversion(setter, fieldName, qualifiedFieldType, writer);
        }
    }

    public static void emitFillRealmObjectWithJsonValue(String setter, String fieldName, String qualifiedFieldType, String proxyClass, JavaWriter writer) throws IOException {
        Object[] objArr = new Object[]{qualifiedFieldType, fieldName, proxyClass, fieldName};
        objArr = new Object[]{setter, fieldName};
        writer.beginControlFlow("if (!json.isNull(\"%s\"))", fieldName).emitStatement("%s %sObj = %s.createOrUpdateUsingJsonObject(realm, json.getJSONObject(\"%s\"), update)", objArr).emitStatement("obj.%s(%sObj)", objArr).endControlFlow();
    }

    public static void emitFillRealmListWithJsonValue(String getter, String setter, String fieldName, String fieldTypeCanonicalName, String proxyClass, JavaWriter writer) throws IOException {
        Object[] objArr = new Object[]{getter};
        objArr = new Object[]{fieldName};
        objArr = new Object[]{fieldTypeCanonicalName, proxyClass, fieldTypeCanonicalName};
        objArr = new Object[]{getter};
        writer.beginControlFlow("if (!json.isNull(\"%s\"))", fieldName).emitStatement("obj.%s().clear()", objArr).emitStatement("JSONArray array = json.getJSONArray(\"%s\")", objArr).beginControlFlow("for (int i = 0; i < array.length(); i++)", new Object[0]).emitStatement("%s item = %s.createOrUpdateUsingJsonObject(realm, array.getJSONObject(i), update)", objArr).emitStatement("obj.%s().add(item)", objArr).endControlFlow().endControlFlow();
    }

    public static void emitFillJavaTypeFromStream(String setter, String fieldName, String fieldType, JavaWriter writer) throws IOException {
        if (JAVA_TO_JSON_TYPES.containsKey(fieldType)) {
            ((JsonToRealmTypeConverter) JAVA_TO_JSON_TYPES.get(fieldType)).emitStreamTypeConversion(setter, fieldName, fieldType, writer);
        }
    }

    public static void emitFillRealmObjectFromStream(String setter, String fieldName, String fieldTypeCanonicalName, String proxyClass, JavaWriter writer) throws IOException {
        Object[] objArr = new Object[]{setter, fieldName};
        writer.emitStatement("%s %sObj = %s.createUsingJsonStream(realm, reader)", fieldTypeCanonicalName, fieldName, proxyClass).emitStatement("obj.%s(%sObj)", objArr);
    }

    public static void emitFillRealmListFromStream(String getter, String setter, String fieldTypeCanonicalName, String proxyClass, JavaWriter writer) throws IOException {
        Object[] objArr = new Object[]{getter};
        writer.emitStatement("reader.beginArray()", new Object[0]).beginControlFlow("while (reader.hasNext())", new Object[0]).emitStatement("%s item = %s.createUsingJsonStream(realm, reader)", fieldTypeCanonicalName, proxyClass).emitStatement("obj.%s().add(item)", objArr).endControlFlow().emitStatement("reader.endArray()", new Object[0]);
    }
}
