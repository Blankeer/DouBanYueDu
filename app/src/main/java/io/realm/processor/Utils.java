package io.realm.processor;

import com.douban.book.reader.util.WorksIdentity;
import io.fabric.sdk.android.services.network.HttpRequest;
import io.fabric.sdk.android.services.settings.SettingsJsonConstants;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic.Kind;
import javax.xml.bind.DatatypeConverter;

public class Utils {
    private static Messager messager;
    private static DeclaredType realmList;
    public static Types typeUtils;

    public static void initialize(ProcessingEnvironment env) {
        typeUtils = env.getTypeUtils();
        messager = env.getMessager();
        realmList = typeUtils.getDeclaredType(env.getElementUtils().getTypeElement("io.realm.RealmList"), new TypeMirror[]{typeUtils.getWildcardType(null, null)});
    }

    public static boolean isDefaultConstructor(Element constructor) {
        if (constructor.getModifiers().contains(Modifier.PUBLIC)) {
            return ((ExecutableElement) constructor).getParameters().isEmpty();
        }
        return false;
    }

    public static String lowerFirstChar(String input) {
        return input.substring(0, 1).toLowerCase() + input.substring(1);
    }

    public static String getProxyClassSimpleName(VariableElement field) {
        if (typeUtils.isAssignable(field.asType(), realmList)) {
            return getProxyClassName(getGenericType(field));
        }
        return getProxyClassName(getFieldTypeSimpleName(field));
    }

    public static String getProxyClassName(String clazz) {
        return clazz + Constants.PROXY_SUFFIX;
    }

    public static boolean isString(VariableElement field) {
        if (field == null) {
            return false;
        }
        return getFieldTypeSimpleName(field).equals("String");
    }

    public static String getFieldTypeSimpleName(VariableElement field) {
        String fieldTypeCanonicalName = field.asType().toString();
        if (fieldTypeCanonicalName.contains(".")) {
            return fieldTypeCanonicalName.substring(fieldTypeCanonicalName.lastIndexOf(46) + 1);
        }
        return fieldTypeCanonicalName;
    }

    public static String getGenericType(VariableElement field) {
        List<? extends TypeMirror> typeArguments = ((DeclaredType) field.asType()).getTypeArguments();
        if (typeArguments.size() == 0) {
            return null;
        }
        String genericCanonicalType = ((TypeMirror) typeArguments.get(0)).toString();
        if (genericCanonicalType.contains(".")) {
            return genericCanonicalType.substring(genericCanonicalType.lastIndexOf(46) + 1);
        }
        return genericCanonicalType;
    }

    public static String stripPackage(String fullyQualifiedClassName) {
        String[] parts = fullyQualifiedClassName.split("\\.");
        if (parts.length > 0) {
            return parts[parts.length - 1];
        }
        return fullyQualifiedClassName;
    }

    public static void error(String message, Element element) {
        messager.printMessage(Kind.ERROR, message, element);
    }

    public static void error(String message) {
        messager.printMessage(Kind.ERROR, message);
    }

    public static void note(String message) {
        messager.printMessage(Kind.NOTE, message);
    }

    public static Element getSuperClass(TypeElement classType) {
        return typeUtils.asElement(classType.getSuperclass());
    }

    public static String base64Encode(String data) throws UnsupportedEncodingException {
        return DatatypeConverter.printBase64Binary(data.getBytes(HttpRequest.CHARSET_UTF8));
    }

    public static byte[] sha256Hash(byte[] data) throws NoSuchAlgorithmException {
        return MessageDigest.getInstance("SHA-256").digest(data);
    }

    public static String hexStringify(byte[] data) {
        StringBuilder stringBuilder = new StringBuilder();
        for (byte singleByte : data) {
            stringBuilder.append(Integer.toString((singleByte & SettingsJsonConstants.SETTINGS_IDENTIFIER_MASK_DEFAULT) + WorksIdentity.ID_BIT_FINALIZE, 16).substring(1));
        }
        return stringBuilder.toString();
    }
}
