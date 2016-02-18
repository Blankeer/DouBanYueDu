package io.realm;

import android.content.Context;
import io.realm.annotations.RealmModule;
import io.realm.exceptions.RealmException;
import io.realm.internal.RealmProxyMediator;
import io.realm.internal.SharedGroup.Durability;
import io.realm.internal.modules.CompositeMediator;
import io.realm.internal.modules.FilterableMediator;
import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class RealmConfiguration {
    private static final Object DEFAULT_MODULE;
    private static final RealmProxyMediator DEFAULT_MODULE_MEDIATOR;
    public static final int KEY_LENGTH = 64;
    private final String canonicalPath;
    private final boolean deleteRealmIfMigrationNeeded;
    private final Durability durability;
    private final byte[] key;
    private final RealmMigration migration;
    private final String realmFileName;
    private final File realmFolder;
    private final RealmProxyMediator schemaMediator;
    private final long schemaVersion;

    public static class Builder {
        private HashSet<Class<? extends RealmObject>> debugSchema;
        private boolean deleteRealmIfMigrationNeeded;
        private Durability durability;
        private String fileName;
        private File folder;
        private byte[] key;
        private RealmMigration migration;
        private HashSet<Object> modules;
        private long schemaVersion;

        public Builder(File folder) {
            this.modules = new HashSet();
            this.debugSchema = new HashSet();
            initializeBuilder(folder);
        }

        public Builder(Context context) {
            this.modules = new HashSet();
            this.debugSchema = new HashSet();
            if (context == null) {
                throw new IllegalArgumentException("A non-null Context must be provided");
            }
            initializeBuilder(context.getFilesDir());
        }

        private void initializeBuilder(File folder) {
            if (folder == null || !folder.isDirectory()) {
                throw new IllegalArgumentException("An existing folder must be provided. Yours was " + (folder != null ? folder.getAbsolutePath() : "null"));
            } else if (folder.canWrite()) {
                this.folder = folder;
                this.fileName = Realm.DEFAULT_REALM_NAME;
                this.key = null;
                this.schemaVersion = 0;
                this.migration = null;
                this.deleteRealmIfMigrationNeeded = false;
                this.durability = Durability.FULL;
                if (RealmConfiguration.DEFAULT_MODULE != null) {
                    this.modules.add(RealmConfiguration.DEFAULT_MODULE);
                }
            } else {
                throw new IllegalArgumentException("Folder is not writable: " + folder.getAbsolutePath());
            }
        }

        public Builder name(String filename) {
            if (filename == null || filename.isEmpty()) {
                throw new IllegalArgumentException("A non-empty filename must be provided");
            }
            this.fileName = filename;
            return this;
        }

        public Builder encryptionKey(byte[] key) {
            if (key == null) {
                throw new IllegalArgumentException("A non-null key must be provided");
            } else if (key.length != RealmConfiguration.KEY_LENGTH) {
                throw new IllegalArgumentException(String.format("The provided key must be %s bytes. Yours was: %s", new Object[]{Integer.valueOf(RealmConfiguration.KEY_LENGTH), Integer.valueOf(key.length)}));
            } else {
                this.key = key;
                return this;
            }
        }

        public Builder schemaVersion(long schemaVersion) {
            if (schemaVersion < 0) {
                throw new IllegalArgumentException("Realm schema version numbers must be 0 (zero) or higher. Yours was: " + schemaVersion);
            }
            this.schemaVersion = schemaVersion;
            return this;
        }

        public Builder migration(RealmMigration migration) {
            if (migration == null) {
                throw new IllegalArgumentException("A non-null migration must be provided");
            }
            this.migration = migration;
            return this;
        }

        public Builder deleteRealmIfMigrationNeeded() {
            this.deleteRealmIfMigrationNeeded = true;
            return this;
        }

        public Builder inMemory() {
            this.durability = Durability.MEM_ONLY;
            return this;
        }

        public Builder setModules(Object baseModule, Object... additionalModules) {
            this.modules.clear();
            addModule(baseModule);
            if (additionalModules != null) {
                for (Object module : additionalModules) {
                    addModule(module);
                }
            }
            return this;
        }

        private void addModule(Object module) {
            if (module != null) {
                checkModule(module);
                this.modules.add(module);
            }
        }

        Builder schema(Class<? extends RealmObject> firstClass, Class<? extends RealmObject>... additionalClasses) {
            if (firstClass == null) {
                throw new IllegalArgumentException("A non-null class must be provided");
            }
            this.modules.clear();
            this.modules.add(RealmConfiguration.DEFAULT_MODULE_MEDIATOR);
            this.debugSchema.add(firstClass);
            if (additionalClasses != null) {
                Collections.addAll(this.debugSchema, additionalClasses);
            }
            return this;
        }

        public RealmConfiguration build() {
            return new RealmConfiguration();
        }

        private void checkModule(Object module) {
            if (!module.getClass().isAnnotationPresent(RealmModule.class)) {
                throw new IllegalArgumentException(module.getClass().getCanonicalName() + " is not a RealmModule. " + "Add @RealmModule to the class definition.");
            }
        }
    }

    static {
        DEFAULT_MODULE = Realm.getDefaultModule();
        if (DEFAULT_MODULE != null) {
            DEFAULT_MODULE_MEDIATOR = getModuleMediator(DEFAULT_MODULE.getClass().getCanonicalName());
        } else {
            DEFAULT_MODULE_MEDIATOR = null;
        }
    }

    private RealmConfiguration(Builder builder) {
        this.realmFolder = builder.folder;
        this.realmFileName = builder.fileName;
        this.canonicalPath = Realm.getCanonicalPath(new File(this.realmFolder, this.realmFileName));
        this.key = builder.key;
        this.schemaVersion = builder.schemaVersion;
        this.deleteRealmIfMigrationNeeded = builder.deleteRealmIfMigrationNeeded;
        this.migration = builder.migration;
        this.durability = builder.durability;
        this.schemaMediator = createSchemaMediator(builder);
    }

    public File getRealmFolder() {
        return this.realmFolder;
    }

    public String getRealmFileName() {
        return this.realmFileName;
    }

    public byte[] getEncryptionKey() {
        return this.key;
    }

    public long getSchemaVersion() {
        return this.schemaVersion;
    }

    public RealmMigration getMigration() {
        return this.migration;
    }

    public boolean shouldDeleteRealmIfMigrationNeeded() {
        return this.deleteRealmIfMigrationNeeded;
    }

    public Durability getDurability() {
        return this.durability;
    }

    public RealmProxyMediator getSchemaMediator() {
        return this.schemaMediator;
    }

    public String getPath() {
        return this.canonicalPath;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        RealmConfiguration that = (RealmConfiguration) obj;
        if (this.schemaVersion != that.schemaVersion || this.deleteRealmIfMigrationNeeded != that.deleteRealmIfMigrationNeeded || !this.realmFolder.equals(that.realmFolder) || !this.realmFileName.equals(that.realmFileName) || !this.canonicalPath.equals(that.canonicalPath) || !Arrays.equals(this.key, that.key) || !this.durability.equals(that.durability)) {
            return false;
        }
        if (this.migration != null) {
            if (!this.migration.equals(that.migration)) {
                return false;
            }
        } else if (that.migration != null) {
            return false;
        }
        return this.schemaMediator.equals(that.schemaMediator);
    }

    public int hashCode() {
        int hashCode;
        int i = 0;
        int hashCode2 = ((((((((this.realmFolder.hashCode() * 31) + this.realmFileName.hashCode()) * 31) + this.canonicalPath.hashCode()) * 31) + (this.key != null ? Arrays.hashCode(this.key) : 0)) * 31) + ((int) this.schemaVersion)) * 31;
        if (this.migration != null) {
            hashCode = this.migration.hashCode();
        } else {
            hashCode = 0;
        }
        hashCode = (hashCode2 + hashCode) * 31;
        if (this.deleteRealmIfMigrationNeeded) {
            i = 1;
        }
        return ((((hashCode + i) * 31) + this.schemaMediator.hashCode()) * 31) + this.durability.hashCode();
    }

    private RealmProxyMediator createSchemaMediator(Builder builder) {
        Set<Object> modules = builder.modules;
        Set<Class<? extends RealmObject>> debugSchema = builder.debugSchema;
        if (debugSchema.size() > 0) {
            return new FilterableMediator(DEFAULT_MODULE_MEDIATOR, debugSchema);
        }
        if (modules.size() == 1) {
            return getModuleMediator(modules.iterator().next().getClass().getCanonicalName());
        }
        RealmProxyMediator mediator = new CompositeMediator();
        for (Object module : modules) {
            mediator.addMediator(getModuleMediator(module.getClass().getCanonicalName()));
        }
        return mediator;
    }

    private static RealmProxyMediator getModuleMediator(String fullyQualifiedModuleClassName) {
        String[] moduleNameParts = fullyQualifiedModuleClassName.split("\\.");
        String moduleSimpleName = moduleNameParts[moduleNameParts.length - 1];
        String mediatorName = String.format("io.realm.%s%s", new Object[]{moduleSimpleName, "Mediator"});
        try {
            Constructor<?> constructor = Class.forName(mediatorName).getDeclaredConstructors()[0];
            constructor.setAccessible(true);
            return (RealmProxyMediator) constructor.newInstance(new Object[0]);
        } catch (ClassNotFoundException e) {
            throw new RealmException("Could not find " + mediatorName, e);
        } catch (InvocationTargetException e2) {
            throw new RealmException("Could not create an instance of " + mediatorName, e2);
        } catch (InstantiationException e3) {
            throw new RealmException("Could not create an instance of " + mediatorName, e3);
        } catch (IllegalAccessException e4) {
            throw new RealmException("Could not create an instance of " + mediatorName, e4);
        }
    }
}
