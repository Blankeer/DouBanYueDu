package io.realm.processor;

import io.realm.annotations.RealmClass;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;

@SupportedAnnotationTypes({"io.realm.annotations.RealmClass", "io.realm.annotations.Ignore", "io.realm.annotations.Index", "io.realm.annotations.PrimaryKey", "io.realm.annotations.RealmModule"})
public class RealmProcessor extends AbstractProcessor {
    Set<ClassMetaData> classesToValidate;
    private boolean hasProcessedModules;

    public RealmProcessor() {
        this.classesToValidate = new HashSet();
        this.hasProcessedModules = false;
    }

    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnv) {
        if (this.hasProcessedModules) {
            return true;
        }
        RealmVersionChecker.getInstance(this.processingEnv).executeRealmVersionUpdate();
        Utils.initialize(this.processingEnv);
        Set<String> packages = new TreeSet();
        for (Element classElement : roundEnv.getElementsAnnotatedWith(RealmClass.class)) {
            if (!classElement.getKind().equals(ElementKind.CLASS)) {
                Utils.error("The RealmClass annotation can only be applied to classes", classElement);
            }
            ClassMetaData metadata = new ClassMetaData(this.processingEnv, (TypeElement) classElement);
            if (metadata.isModelClass()) {
                Utils.note("Processing class " + metadata.getSimpleClassName());
                if (!metadata.generate()) {
                    return true;
                }
                this.classesToValidate.add(metadata);
                packages.add(metadata.getPackageName());
                try {
                    new RealmProxyClassGenerator(this.processingEnv, metadata).generate();
                } catch (IOException e) {
                    Utils.error(e.getMessage(), classElement);
                } catch (UnsupportedOperationException e2) {
                    Utils.error(e2.getMessage(), classElement);
                }
            }
        }
        RealmAnalytics.getInstance(packages).execute();
        this.hasProcessedModules = true;
        return processModules(roundEnv);
    }

    private boolean processModules(RoundEnvironment roundEnv) {
        ModuleMetaData moduleMetaData = new ModuleMetaData(roundEnv, this.classesToValidate);
        if (!moduleMetaData.generate(this.processingEnv)) {
            return false;
        }
        if (moduleMetaData.shouldCreateDefaultModule() && !createDefaultModule()) {
            return false;
        }
        for (Entry<String, Set<ClassMetaData>> module : moduleMetaData.getAllModules().entrySet()) {
            if (!createMediator(Utils.stripPackage((String) module.getKey()), (Set) module.getValue())) {
                return false;
            }
        }
        return true;
    }

    private boolean createDefaultModule() {
        Utils.note("Creating DefaultRealmModule");
        try {
            new DefaultModuleGenerator(this.processingEnv).generate();
            return true;
        } catch (IOException e) {
            Utils.error(e.getMessage());
            return false;
        }
    }

    private boolean createMediator(String simpleModuleName, Set<ClassMetaData> moduleClasses) {
        try {
            new RealmProxyMediatorGenerator(this.processingEnv, simpleModuleName, moduleClasses).generate();
            return true;
        } catch (IOException e) {
            Utils.error(e.getMessage());
            return false;
        }
    }
}
