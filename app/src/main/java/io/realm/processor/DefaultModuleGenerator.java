package io.realm.processor;

import io.realm.annotations.RealmModule;
import io.realm.processor.javawriter.JavaWriter;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;

public class DefaultModuleGenerator {
    private final ProcessingEnvironment env;

    public DefaultModuleGenerator(ProcessingEnvironment env) {
        this.env = env;
    }

    public void generate() throws IOException {
        String qualifiedGeneratedClassName = String.format("%s.%s", new Object[]{Constants.REALM_PACKAGE_NAME, Constants.DEFAULT_MODULE_CLASS_NAME});
        JavaWriter writer = new JavaWriter(new BufferedWriter(this.env.getFiler().createSourceFile(qualifiedGeneratedClassName, new Element[0]).openWriter()));
        writer.setIndent("    ");
        writer.emitPackage(Constants.REALM_PACKAGE_NAME);
        writer.emitEmptyLine();
        Map attributes = new HashMap();
        attributes.put("allClasses", Boolean.TRUE);
        writer.emitAnnotation(RealmModule.class, attributes);
        writer.beginType(qualifiedGeneratedClassName, "class", Collections.emptySet(), null, new String[0]);
        writer.emitEmptyLine();
        writer.endType();
        writer.close();
    }
}
