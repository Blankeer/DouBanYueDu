package io.realm.processor;

import io.realm.annotations.RealmModule;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;

public class ModuleMetaData {
    private final Set<ClassMetaData> availableClasses;
    private Map<String, ClassMetaData> classMetaData;
    private final RoundEnvironment env;
    private Map<String, Set<ClassMetaData>> libraryModules;
    private Map<String, Set<ClassMetaData>> modules;
    private boolean shouldCreateDefaultModule;

    public ModuleMetaData(RoundEnvironment env, Set<ClassMetaData> availableClasses) {
        this.modules = new HashMap();
        this.libraryModules = new HashMap();
        this.classMetaData = new HashMap();
        this.env = env;
        this.availableClasses = availableClasses;
        for (ClassMetaData classMetaData : availableClasses) {
            this.classMetaData.put(classMetaData.getFullyQualifiedClassName(), classMetaData);
        }
    }

    public boolean generate(ProcessingEnvironment processingEnv) {
        for (Element classElement : this.env.getElementsAnnotatedWith(RealmModule.class)) {
            String classSimpleName = classElement.getSimpleName().toString();
            if (classElement.getKind().equals(ElementKind.CLASS)) {
                RealmModule module = (RealmModule) classElement.getAnnotation(RealmModule.class);
                Utils.note("Processing module " + classSimpleName);
                if (module.allClasses() && hasCustomClassList(classElement)) {
                    Utils.error("Setting @RealmModule(allClasses=true) will override @RealmModule(classes={...}) in " + classSimpleName);
                    return false;
                }
                Set<ClassMetaData> classes;
                String qualifiedName = ((TypeElement) classElement).getQualifiedName().toString();
                if (module.allClasses()) {
                    classes = this.availableClasses;
                } else {
                    classes = new HashSet();
                    for (String fullyQualifiedClassName : getClassMetaDataFromModule(classElement)) {
                        ClassMetaData metadata = (ClassMetaData) this.classMetaData.get(fullyQualifiedClassName);
                        if (metadata == null) {
                            Utils.error(Utils.stripPackage(fullyQualifiedClassName) + " could not be added to the module. " + "Only classes extending RealmObject, which are part of this project, can be added.");
                            return false;
                        }
                        classes.add(metadata);
                    }
                }
                if (module.library()) {
                    this.libraryModules.put(qualifiedName, classes);
                } else {
                    this.modules.put(qualifiedName, classes);
                }
            } else {
                Utils.error("The RealmModule annotation can only be applied to classes", classElement);
                return false;
            }
        }
        if (this.modules.size() <= 0 || this.libraryModules.size() <= 0) {
            if (this.libraryModules.size() == 0) {
                this.shouldCreateDefaultModule = true;
                this.modules.put("io.realm.DefaultRealmModule", this.availableClasses);
            }
            return true;
        }
        Utils.error("Normal modules and library modules cannot be mixed in the same project");
        return false;
    }

    private Set<String> getClassMetaDataFromModule(Element classElement) {
        AnnotationValue annotationValue = getAnnotationValue(getAnnotationMirror(classElement));
        Set<String> classes = new HashSet();
        for (AnnotationValue classMirror : (List) annotationValue.getValue()) {
            classes.add(classMirror.getValue().toString());
        }
        return classes;
    }

    private boolean hasCustomClassList(Element classElement) {
        AnnotationValue annotationValue = getAnnotationValue(getAnnotationMirror(classElement));
        if (annotationValue != null && ((List) annotationValue.getValue()).size() > 0) {
            return true;
        }
        return false;
    }

    private AnnotationMirror getAnnotationMirror(Element classElement) {
        for (AnnotationMirror am : classElement.getAnnotationMirrors()) {
            if (am.getAnnotationType().toString().equals(RealmModule.class.getCanonicalName())) {
                return am;
            }
        }
        return null;
    }

    private AnnotationValue getAnnotationValue(AnnotationMirror annotationMirror) {
        if (annotationMirror == null) {
            return null;
        }
        for (Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : annotationMirror.getElementValues().entrySet()) {
            if (((ExecutableElement) entry.getKey()).getSimpleName().toString().equals("classes")) {
                return (AnnotationValue) entry.getValue();
            }
        }
        return null;
    }

    public Map<String, Set<ClassMetaData>> getAllModules() {
        Map<String, Set<ClassMetaData>> allModules = new HashMap();
        allModules.putAll(this.modules);
        allModules.putAll(this.libraryModules);
        return allModules;
    }

    public boolean shouldCreateDefaultModule() {
        return this.shouldCreateDefaultModule;
    }
}
