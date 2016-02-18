package io.realm.processor.javawriter;

import com.alipay.sdk.protocol.h;
import com.douban.book.reader.constant.Char;
import com.douban.book.reader.entity.DbCacheEntity.Column;
import com.j256.ormlite.stmt.query.SimpleComparison;
import io.realm.internal.Table;
import java.io.Closeable;
import java.io.IOException;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.lang.model.element.Modifier;
import se.emilsjolander.stickylistheaders.R;
import u.aly.dx;

public class JavaWriter implements Closeable {
    private static final String INDENT = "  ";
    private static final int MAX_SINGLE_LINE_ATTRIBUTES = 3;
    private static final EnumSet<Scope> METHOD_SCOPES;
    private static final Pattern TYPE_PATTERN;
    private final Map<String, String> importedTypes;
    private String indent;
    private boolean isCompressingTypes;
    private final Writer out;
    private String packagePrefix;
    private final Deque<Scope> scopes;
    private final Deque<String> types;

    private enum Scope {
        TYPE_DECLARATION,
        INTERFACE_DECLARATION,
        ABSTRACT_METHOD,
        NON_ABSTRACT_METHOD,
        CONSTRUCTOR,
        CONTROL_FLOW,
        ANNOTATION_ATTRIBUTE,
        ANNOTATION_ARRAY_VALUE,
        INITIALIZER
    }

    static {
        TYPE_PATTERN = Pattern.compile("(?:[\\w$]+\\.)*([\\w\\.*$]+)");
        METHOD_SCOPES = EnumSet.of(Scope.NON_ABSTRACT_METHOD, Scope.CONSTRUCTOR, Scope.CONTROL_FLOW, Scope.INITIALIZER);
    }

    public JavaWriter(Writer out) {
        this.importedTypes = new LinkedHashMap();
        this.scopes = new ArrayDeque();
        this.types = new ArrayDeque();
        this.isCompressingTypes = true;
        this.indent = INDENT;
        this.out = out;
    }

    public void setCompressingTypes(boolean isCompressingTypes) {
        this.isCompressingTypes = isCompressingTypes;
    }

    public boolean isCompressingTypes() {
        return this.isCompressingTypes;
    }

    public void setIndent(String indent) {
        this.indent = indent;
    }

    public String getIndent() {
        return this.indent;
    }

    public JavaWriter emitPackage(String packageName) throws IOException {
        if (this.packagePrefix != null) {
            throw new IllegalStateException();
        }
        if (packageName.isEmpty()) {
            this.packagePrefix = Table.STRING_DEFAULT_VALUE;
        } else {
            this.out.write("package ");
            this.out.write(packageName);
            this.out.write(";\n\n");
            this.packagePrefix = packageName + ".";
        }
        return this;
    }

    public JavaWriter emitImports(String... types) throws IOException {
        return emitImports(Arrays.asList(types));
    }

    public JavaWriter emitImports(Class<?>... types) throws IOException {
        Collection classNames = new ArrayList(types.length);
        for (Class<?> classToImport : types) {
            classNames.add(classToImport.getName());
        }
        return emitImports(classNames);
    }

    public JavaWriter emitImports(Collection<String> types) throws IOException {
        Iterator it = new TreeSet(types).iterator();
        while (it.hasNext()) {
            String type = (String) it.next();
            Matcher matcher = TYPE_PATTERN.matcher(type);
            if (!matcher.matches()) {
                throw new IllegalArgumentException(type);
            } else if (this.importedTypes.put(type, matcher.group(1)) != null) {
                throw new IllegalArgumentException(type);
            } else {
                this.out.write("import ");
                this.out.write(type);
                this.out.write(";\n");
            }
        }
        return this;
    }

    public JavaWriter emitStaticImports(String... types) throws IOException {
        return emitStaticImports(Arrays.asList(types));
    }

    public JavaWriter emitStaticImports(Collection<String> types) throws IOException {
        Iterator it = new TreeSet(types).iterator();
        while (it.hasNext()) {
            String type = (String) it.next();
            Matcher matcher = TYPE_PATTERN.matcher(type);
            if (!matcher.matches()) {
                throw new IllegalArgumentException(type);
            } else if (this.importedTypes.put(type, matcher.group(1)) != null) {
                throw new IllegalArgumentException(type);
            } else {
                this.out.write("import static ");
                this.out.write(type);
                this.out.write(";\n");
            }
        }
        return this;
    }

    private JavaWriter emitCompressedType(String type) throws IOException {
        if (this.isCompressingTypes) {
            this.out.write(compressType(type));
        } else {
            this.out.write(type);
        }
        return this;
    }

    public String compressType(String type) {
        StringBuilder sb = new StringBuilder();
        if (this.packagePrefix == null) {
            throw new IllegalStateException();
        }
        Matcher m = TYPE_PATTERN.matcher(type);
        int pos = 0;
        while (true) {
            boolean found = m.find(pos);
            sb.append(type, pos, found ? m.start() : type.length());
            if (!found) {
                return sb.toString();
            }
            String name = m.group(0);
            String imported = (String) this.importedTypes.get(name);
            if (imported != null) {
                sb.append(imported);
            } else if (isClassInPackage(name, this.packagePrefix)) {
                String compressed = name.substring(this.packagePrefix.length());
                if (isAmbiguous(compressed)) {
                    sb.append(name);
                } else {
                    sb.append(compressed);
                }
            } else if (isClassInPackage(name, "java.lang.")) {
                sb.append(name.substring("java.lang.".length()));
            } else {
                sb.append(name);
            }
            pos = m.end();
        }
    }

    private static boolean isClassInPackage(String name, String packagePrefix) {
        if (name.startsWith(packagePrefix) && (name.indexOf(46, packagePrefix.length()) == -1 || Character.isUpperCase(name.charAt(packagePrefix.length())))) {
            return true;
        }
        return false;
    }

    private boolean isAmbiguous(String compressed) {
        return this.importedTypes.values().contains(compressed);
    }

    public JavaWriter beginInitializer(boolean isStatic) throws IOException {
        indent();
        if (isStatic) {
            this.out.write("static");
            this.out.write(" {\n");
        } else {
            this.out.write("{\n");
        }
        this.scopes.push(Scope.INITIALIZER);
        return this;
    }

    public JavaWriter endInitializer() throws IOException {
        popScope(Scope.INITIALIZER);
        indent();
        this.out.write("}\n");
        return this;
    }

    public JavaWriter beginType(String type, String kind) throws IOException {
        return beginType(type, kind, EnumSet.noneOf(Modifier.class), null, new String[0]);
    }

    public JavaWriter beginType(String type, String kind, Set<Modifier> modifiers) throws IOException {
        return beginType(type, kind, modifiers, null, new String[0]);
    }

    public JavaWriter beginType(String type, String kind, Set<Modifier> modifiers, String extendsType, String... implementsTypes) throws IOException {
        indent();
        emitModifiers(modifiers);
        this.out.write(kind);
        this.out.write(" ");
        emitCompressedType(type);
        if (extendsType != null) {
            this.out.write(" extends ");
            emitCompressedType(extendsType);
        }
        if (implementsTypes.length > 0) {
            this.out.write("\n");
            indent();
            this.out.write("    implements ");
            for (int i = 0; i < implementsTypes.length; i++) {
                if (i != 0) {
                    this.out.write(", ");
                }
                emitCompressedType(implementsTypes[i]);
            }
        }
        this.out.write(" {\n");
        this.scopes.push("interface".equals(kind) ? Scope.INTERFACE_DECLARATION : Scope.TYPE_DECLARATION);
        this.types.push(type);
        return this;
    }

    public JavaWriter endType() throws IOException {
        popScope(Scope.TYPE_DECLARATION, Scope.INTERFACE_DECLARATION);
        this.types.pop();
        indent();
        this.out.write("}\n");
        return this;
    }

    public JavaWriter emitField(String type, String name) throws IOException {
        return emitField(type, name, EnumSet.noneOf(Modifier.class), null);
    }

    public JavaWriter emitField(String type, String name, Set<Modifier> modifiers) throws IOException {
        return emitField(type, name, modifiers, null);
    }

    public JavaWriter emitField(String type, String name, Set<Modifier> modifiers, String initialValue) throws IOException {
        indent();
        emitModifiers(modifiers);
        emitCompressedType(type);
        this.out.write(" ");
        this.out.write(name);
        if (initialValue != null) {
            this.out.write(" =");
            if (!initialValue.startsWith("\n")) {
                this.out.write(" ");
            }
            String[] lines = initialValue.split("\n", -1);
            this.out.write(lines[0]);
            for (int i = 1; i < lines.length; i++) {
                this.out.write("\n");
                hangingIndent();
                this.out.write(lines[i]);
            }
        }
        this.out.write(";\n");
        return this;
    }

    public JavaWriter beginMethod(String returnType, String name, Set<Modifier> modifiers, String... parameters) throws IOException {
        return beginMethod(returnType, name, modifiers, Arrays.asList(parameters), null);
    }

    public JavaWriter beginMethod(String returnType, String name, Set<Modifier> modifiers, List<String> parameters, List<String> throwsTypes) throws IOException {
        indent();
        emitModifiers(modifiers);
        if (returnType != null) {
            emitCompressedType(returnType);
            this.out.write(" ");
            this.out.write(name);
        } else {
            emitCompressedType(name);
        }
        this.out.write("(");
        if (parameters != null) {
            int p = 0;
            while (p < parameters.size()) {
                if (p != 0) {
                    this.out.write(", ");
                }
                int p2 = p + 1;
                emitCompressedType((String) parameters.get(p));
                this.out.write(" ");
                p = p2 + 1;
                emitCompressedType((String) parameters.get(p2));
            }
        }
        this.out.write(")");
        if (throwsTypes != null && throwsTypes.size() > 0) {
            this.out.write("\n");
            indent();
            this.out.write("    throws ");
            for (int i = 0; i < throwsTypes.size(); i++) {
                if (i != 0) {
                    this.out.write(", ");
                }
                emitCompressedType((String) throwsTypes.get(i));
            }
        }
        if (modifiers.contains(Modifier.ABSTRACT) || Scope.INTERFACE_DECLARATION.equals(this.scopes.peek())) {
            this.out.write(";\n");
            this.scopes.push(Scope.ABSTRACT_METHOD);
        } else {
            this.out.write(" {\n");
            this.scopes.push(returnType == null ? Scope.CONSTRUCTOR : Scope.NON_ABSTRACT_METHOD);
        }
        return this;
    }

    public JavaWriter beginConstructor(Set<Modifier> modifiers, String... parameters) throws IOException {
        beginMethod(null, rawType((String) this.types.peekFirst()), modifiers, parameters);
        return this;
    }

    public JavaWriter beginConstructor(Set<Modifier> modifiers, List<String> parameters, List<String> throwsTypes) throws IOException {
        beginMethod(null, rawType((String) this.types.peekFirst()), modifiers, parameters, throwsTypes);
        return this;
    }

    public JavaWriter emitJavadoc(String javadoc, Object... params) throws IOException {
        String formatted = String.format(javadoc, params);
        indent();
        this.out.write("/**\n");
        for (String line : formatted.split("\n")) {
            indent();
            this.out.write(" *");
            if (!line.isEmpty()) {
                this.out.write(" ");
                this.out.write(line);
            }
            this.out.write("\n");
        }
        indent();
        this.out.write(" */\n");
        return this;
    }

    public JavaWriter emitSingleLineComment(String comment, Object... args) throws IOException {
        indent();
        this.out.write("// ");
        this.out.write(String.format(comment, args));
        this.out.write("\n");
        return this;
    }

    public JavaWriter emitEmptyLine() throws IOException {
        this.out.write("\n");
        return this;
    }

    public JavaWriter emitEnumValue(String name) throws IOException {
        indent();
        this.out.write(name);
        this.out.write(",\n");
        return this;
    }

    public JavaWriter emitEnumValue(String name, boolean isLast) throws IOException {
        return isLast ? emitLastEnumValue(name) : emitEnumValue(name);
    }

    private JavaWriter emitLastEnumValue(String name) throws IOException {
        indent();
        this.out.write(name);
        this.out.write(";\n");
        return this;
    }

    public JavaWriter emitEnumValues(Iterable<String> names) throws IOException {
        Iterator<String> iterator = names.iterator();
        while (iterator.hasNext()) {
            String name = (String) iterator.next();
            if (iterator.hasNext()) {
                emitEnumValue(name);
            } else {
                emitLastEnumValue(name);
            }
        }
        return this;
    }

    public JavaWriter emitAnnotation(String annotation) throws IOException {
        return emitAnnotation(annotation, Collections.emptyMap());
    }

    public JavaWriter emitAnnotation(Class<? extends Annotation> annotationType) throws IOException {
        return emitAnnotation(type(annotationType, new String[0]), Collections.emptyMap());
    }

    public JavaWriter emitAnnotation(Class<? extends Annotation> annotationType, Object value) throws IOException {
        return emitAnnotation(type(annotationType, new String[0]), value);
    }

    public JavaWriter emitAnnotation(String annotation, Object value) throws IOException {
        indent();
        this.out.write("@");
        emitCompressedType(annotation);
        this.out.write("(");
        emitAnnotationValue(value);
        this.out.write(")");
        this.out.write("\n");
        return this;
    }

    public JavaWriter emitAnnotation(Class<? extends Annotation> annotationType, Map<String, ?> attributes) throws IOException {
        return emitAnnotation(type(annotationType, new String[0]), (Map) attributes);
    }

    public JavaWriter emitAnnotation(String annotation, Map<String, ?> attributes) throws IOException {
        indent();
        this.out.write("@");
        emitCompressedType(annotation);
        switch (attributes.size()) {
            case dx.a /*0*/:
                break;
            case dx.b /*1*/:
                Entry<String, ?> onlyEntry = (Entry) attributes.entrySet().iterator().next();
                this.out.write("(");
                if (!Column.VALUE.equals(onlyEntry.getKey())) {
                    this.out.write((String) onlyEntry.getKey());
                    this.out.write(" = ");
                }
                emitAnnotationValue(onlyEntry.getValue());
                this.out.write(")");
                break;
            default:
                boolean split = attributes.size() > MAX_SINGLE_LINE_ATTRIBUTES || containsArray(attributes.values());
                this.out.write("(");
                this.scopes.push(Scope.ANNOTATION_ATTRIBUTE);
                String separator = split ? "\n" : Table.STRING_DEFAULT_VALUE;
                for (Entry<String, ?> entry : attributes.entrySet()) {
                    this.out.write(separator);
                    separator = split ? ",\n" : ", ";
                    if (split) {
                        indent();
                    }
                    this.out.write((String) entry.getKey());
                    this.out.write(" = ");
                    emitAnnotationValue(entry.getValue());
                }
                popScope(Scope.ANNOTATION_ATTRIBUTE);
                if (split) {
                    this.out.write("\n");
                    indent();
                }
                this.out.write(")");
                break;
        }
        this.out.write("\n");
        return this;
    }

    private boolean containsArray(Collection<?> values) {
        for (Object value : values) {
            if (value instanceof Object[]) {
                return true;
            }
        }
        return false;
    }

    private JavaWriter emitAnnotationValue(Object value) throws IOException {
        if (value instanceof Object[]) {
            this.out.write("{");
            boolean firstValue = true;
            this.scopes.push(Scope.ANNOTATION_ARRAY_VALUE);
            for (Object o : (Object[]) value) {
                if (firstValue) {
                    firstValue = false;
                    this.out.write("\n");
                } else {
                    this.out.write(",\n");
                }
                indent();
                this.out.write(o.toString());
            }
            popScope(Scope.ANNOTATION_ARRAY_VALUE);
            this.out.write("\n");
            indent();
            this.out.write("}");
        } else {
            this.out.write(value.toString());
        }
        return this;
    }

    public JavaWriter emitStatement(String pattern, Object... args) throws IOException {
        checkInMethod();
        String[] lines = String.format(pattern, args).split("\n", -1);
        indent();
        this.out.write(lines[0]);
        for (int i = 1; i < lines.length; i++) {
            this.out.write("\n");
            hangingIndent();
            this.out.write(lines[i]);
        }
        this.out.write(";\n");
        return this;
    }

    public JavaWriter beginControlFlow(String controlFlow, Object... args) throws IOException {
        checkInMethod();
        indent();
        this.out.write(String.format(controlFlow, args));
        this.out.write(" {\n");
        this.scopes.push(Scope.CONTROL_FLOW);
        return this;
    }

    public JavaWriter nextControlFlow(String controlFlow, Object... args) throws IOException {
        popScope(Scope.CONTROL_FLOW);
        indent();
        this.scopes.push(Scope.CONTROL_FLOW);
        this.out.write("} ");
        this.out.write(String.format(controlFlow, args));
        this.out.write(" {\n");
        return this;
    }

    public JavaWriter endControlFlow() throws IOException {
        return endControlFlow(null, new Object[0]);
    }

    public JavaWriter endControlFlow(String controlFlow, Object... args) throws IOException {
        popScope(Scope.CONTROL_FLOW);
        indent();
        if (controlFlow != null) {
            this.out.write("} ");
            this.out.write(String.format(controlFlow, args));
            this.out.write(";\n");
        } else {
            this.out.write("}\n");
        }
        return this;
    }

    public JavaWriter endMethod() throws IOException {
        Scope popped = (Scope) this.scopes.pop();
        if (popped == Scope.NON_ABSTRACT_METHOD || popped == Scope.CONSTRUCTOR) {
            indent();
            this.out.write("}\n");
        } else if (popped != Scope.ABSTRACT_METHOD) {
            throw new IllegalStateException();
        }
        return this;
    }

    public JavaWriter endConstructor() throws IOException {
        popScope(Scope.CONSTRUCTOR);
        indent();
        this.out.write("}\n");
        return this;
    }

    public static String stringLiteral(String data) {
        StringBuilder result = new StringBuilder();
        result.append('\"');
        for (int i = 0; i < data.length(); i++) {
            char c = data.charAt(i);
            switch (c) {
                case h.g /*8*/:
                    result.append("\\b");
                    break;
                case h.h /*9*/:
                    result.append("\\t");
                    break;
                case h.i /*10*/:
                    result.append("\\n");
                    break;
                case R.styleable.StickyListHeadersListView_android_scrollingCache /*12*/:
                    result.append("\\f");
                    break;
                case R.styleable.StickyListHeadersListView_android_transcriptMode /*13*/:
                    result.append("\\r");
                    break;
                case HeaderMapDB.HASHER_BYTE_ARRAY /*34*/:
                    result.append("\\\"");
                    break;
                case Header.SHORT_1 /*92*/:
                    result.append("\\\\");
                    break;
                default:
                    if (!Character.isISOControl(c)) {
                        result.append(c);
                        break;
                    }
                    result.append(String.format("\\u%04x", new Object[]{Integer.valueOf(c)}));
                    break;
            }
        }
        result.append('\"');
        return result.toString();
    }

    public static String type(Class<?> raw, String... parameters) {
        if (parameters.length == 0) {
            return raw.getCanonicalName();
        }
        if (raw.getTypeParameters().length != parameters.length) {
            throw new IllegalArgumentException();
        }
        StringBuilder result = new StringBuilder();
        result.append(raw.getCanonicalName());
        result.append(SimpleComparison.LESS_THAN_OPERATION);
        result.append(parameters[0]);
        for (int i = 1; i < parameters.length; i++) {
            result.append(", ");
            result.append(parameters[i]);
        }
        result.append(SimpleComparison.GREATER_THAN_OPERATION);
        return result.toString();
    }

    public static String rawType(String type) {
        int lessThanIndex = type.indexOf(60);
        if (lessThanIndex != -1) {
            return type.substring(0, lessThanIndex);
        }
        return type;
    }

    public void close() throws IOException {
        this.out.close();
    }

    private void emitModifiers(Set<Modifier> modifiers) throws IOException {
        if (!modifiers.isEmpty()) {
            if (!(modifiers instanceof EnumSet)) {
                modifiers = EnumSet.copyOf(modifiers);
            }
            for (Modifier modifier : modifiers) {
                this.out.append(modifier.toString()).append(Char.SPACE);
            }
        }
    }

    private void indent() throws IOException {
        int count = this.scopes.size();
        for (int i = 0; i < count; i++) {
            this.out.write(this.indent);
        }
    }

    private void hangingIndent() throws IOException {
        int count = this.scopes.size() + 2;
        for (int i = 0; i < count; i++) {
            this.out.write(this.indent);
        }
    }

    private void checkInMethod() {
        if (!METHOD_SCOPES.contains(this.scopes.peekFirst())) {
            throw new IllegalArgumentException();
        }
    }

    private void popScope(Scope... expected) {
        if (!EnumSet.copyOf(Arrays.asList(expected)).contains(this.scopes.pop())) {
            throw new IllegalStateException();
        }
    }
}
