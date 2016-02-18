package com.j256.ormlite.misc;

import com.douban.book.reader.entity.DbCacheEntity.Column;
import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.db.DatabaseType;
import com.j256.ormlite.field.DataPersisterManager;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseFieldConfig;
import com.sina.weibo.sdk.register.mobile.SelectCountryActivity;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.Collection;

public class JavaxPersistence {
    public static DatabaseFieldConfig createFieldConfig(DatabaseType databaseType, Field field) throws SQLException {
        Annotation columnAnnotation = null;
        Annotation basicAnnotation = null;
        Annotation idAnnotation = null;
        Annotation generatedValueAnnotation = null;
        Annotation oneToOneAnnotation = null;
        Annotation manyToOneAnnotation = null;
        Annotation joinColumnAnnotation = null;
        Annotation enumeratedAnnotation = null;
        Annotation versionAnnotation = null;
        for (Annotation annotation : field.getAnnotations()) {
            Class<?> annotationClass = annotation.annotationType();
            if (annotationClass.getName().equals("javax.persistence.Column")) {
                columnAnnotation = annotation;
            }
            if (annotationClass.getName().equals("javax.persistence.Basic")) {
                basicAnnotation = annotation;
            }
            if (annotationClass.getName().equals("javax.persistence.Id")) {
                idAnnotation = annotation;
            }
            if (annotationClass.getName().equals("javax.persistence.GeneratedValue")) {
                generatedValueAnnotation = annotation;
            }
            if (annotationClass.getName().equals("javax.persistence.OneToOne")) {
                oneToOneAnnotation = annotation;
            }
            if (annotationClass.getName().equals("javax.persistence.ManyToOne")) {
                manyToOneAnnotation = annotation;
            }
            if (annotationClass.getName().equals("javax.persistence.JoinColumn")) {
                joinColumnAnnotation = annotation;
            }
            if (annotationClass.getName().equals("javax.persistence.Enumerated")) {
                enumeratedAnnotation = annotation;
            }
            if (annotationClass.getName().equals("javax.persistence.Version")) {
                versionAnnotation = annotation;
            }
        }
        if (columnAnnotation == null && basicAnnotation == null && idAnnotation == null && oneToOneAnnotation == null && manyToOneAnnotation == null && enumeratedAnnotation == null && versionAnnotation == null) {
            return null;
        }
        String name;
        Boolean nullable;
        Boolean unique;
        boolean z;
        DatabaseFieldConfig config = new DatabaseFieldConfig();
        String fieldName = field.getName();
        if (databaseType.isEntityNamesMustBeUpCase()) {
            fieldName = fieldName.toUpperCase();
        }
        config.setFieldName(fieldName);
        if (columnAnnotation != null) {
            try {
                name = (String) columnAnnotation.getClass().getMethod(SelectCountryActivity.EXTRA_COUNTRY_NAME, new Class[0]).invoke(columnAnnotation, new Object[0]);
                if (name != null && name.length() > 0) {
                    config.setColumnName(name);
                }
                String columnDefinition = (String) columnAnnotation.getClass().getMethod("columnDefinition", new Class[0]).invoke(columnAnnotation, new Object[0]);
                if (columnDefinition != null && columnDefinition.length() > 0) {
                    config.setColumnDefinition(columnDefinition);
                }
                config.setWidth(((Integer) columnAnnotation.getClass().getMethod("length", new Class[0]).invoke(columnAnnotation, new Object[0])).intValue());
                nullable = (Boolean) columnAnnotation.getClass().getMethod("nullable", new Class[0]).invoke(columnAnnotation, new Object[0]);
                if (nullable != null) {
                    config.setCanBeNull(nullable.booleanValue());
                }
                unique = (Boolean) columnAnnotation.getClass().getMethod("unique", new Class[0]).invoke(columnAnnotation, new Object[0]);
                if (unique != null) {
                    config.setUnique(unique.booleanValue());
                }
            } catch (Exception e) {
                throw SqlExceptionUtil.create("Problem accessing fields from the @Column annotation for field " + field, e);
            }
        }
        if (basicAnnotation != null) {
            try {
                Boolean optional = (Boolean) basicAnnotation.getClass().getMethod("optional", new Class[0]).invoke(basicAnnotation, new Object[0]);
                if (optional == null) {
                    config.setCanBeNull(true);
                } else {
                    config.setCanBeNull(optional.booleanValue());
                }
            } catch (Exception e2) {
                throw SqlExceptionUtil.create("Problem accessing fields from the @Basic annotation for field " + field, e2);
            }
        }
        if (idAnnotation != null) {
            if (generatedValueAnnotation == null) {
                config.setId(true);
            } else {
                config.setGeneratedId(true);
            }
        }
        if (!(oneToOneAnnotation == null && manyToOneAnnotation == null)) {
            if (Collection.class.isAssignableFrom(field.getType()) || ForeignCollection.class.isAssignableFrom(field.getType())) {
                config.setForeignCollection(true);
                if (joinColumnAnnotation != null) {
                    try {
                        name = (String) joinColumnAnnotation.getClass().getMethod(SelectCountryActivity.EXTRA_COUNTRY_NAME, new Class[0]).invoke(joinColumnAnnotation, new Object[0]);
                        if (name != null && name.length() > 0) {
                            config.setForeignCollectionColumnName(name);
                        }
                        Object fetchType = joinColumnAnnotation.getClass().getMethod("fetch", new Class[0]).invoke(joinColumnAnnotation, new Object[0]);
                        if (fetchType != null && fetchType.toString().equals("EAGER")) {
                            config.setForeignCollectionEager(true);
                        }
                    } catch (Exception e22) {
                        throw SqlExceptionUtil.create("Problem accessing fields from the @JoinColumn annotation for field " + field, e22);
                    }
                }
            }
            config.setForeign(true);
            if (joinColumnAnnotation != null) {
                try {
                    name = (String) joinColumnAnnotation.getClass().getMethod(SelectCountryActivity.EXTRA_COUNTRY_NAME, new Class[0]).invoke(joinColumnAnnotation, new Object[0]);
                    if (name != null && name.length() > 0) {
                        config.setColumnName(name);
                    }
                    nullable = (Boolean) joinColumnAnnotation.getClass().getMethod("nullable", new Class[0]).invoke(joinColumnAnnotation, new Object[0]);
                    if (nullable != null) {
                        config.setCanBeNull(nullable.booleanValue());
                    }
                    unique = (Boolean) joinColumnAnnotation.getClass().getMethod("unique", new Class[0]).invoke(joinColumnAnnotation, new Object[0]);
                    if (unique != null) {
                        config.setUnique(unique.booleanValue());
                    }
                } catch (Exception e222) {
                    throw SqlExceptionUtil.create("Problem accessing fields from the @JoinColumn annotation for field " + field, e222);
                }
            }
        }
        if (enumeratedAnnotation != null) {
            try {
                Object typeValue = enumeratedAnnotation.getClass().getMethod(Column.VALUE, new Class[0]).invoke(enumeratedAnnotation, new Object[0]);
                if (typeValue == null || !typeValue.toString().equals("STRING")) {
                    config.setDataType(DataType.ENUM_INTEGER);
                } else {
                    config.setDataType(DataType.ENUM_STRING);
                }
            } catch (Exception e2222) {
                throw SqlExceptionUtil.create("Problem accessing fields from the @Enumerated annotation for field " + field, e2222);
            }
        }
        if (versionAnnotation != null) {
            config.setVersion(true);
        }
        if (config.getDataPersister() == null) {
            config.setDataPersister(DataPersisterManager.lookupForField(field));
        }
        if (DatabaseFieldConfig.findGetMethod(field, false) == null || DatabaseFieldConfig.findSetMethod(field, false) == null) {
            z = false;
        } else {
            z = true;
        }
        config.setUseGetSet(z);
        return config;
    }

    public static String getEntityName(Class<?> clazz) {
        Annotation entityAnnotation = null;
        for (Annotation annotation : clazz.getAnnotations()) {
            if (annotation.annotationType().getName().equals("javax.persistence.Entity")) {
                entityAnnotation = annotation;
            }
        }
        if (entityAnnotation == null) {
            return null;
        }
        try {
            String name = (String) entityAnnotation.getClass().getMethod(SelectCountryActivity.EXTRA_COUNTRY_NAME, new Class[0]).invoke(entityAnnotation, new Object[0]);
            if (name == null || name.length() <= 0) {
                return null;
            }
            return name;
        } catch (Exception e) {
            throw new IllegalStateException("Could not get entity name from class " + clazz, e);
        }
    }
}
