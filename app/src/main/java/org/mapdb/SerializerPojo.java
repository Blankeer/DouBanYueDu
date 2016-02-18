package org.mapdb;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.Externalizable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InvalidClassException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.io.ObjectStreamField;
import java.io.OutputStream;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.mapdb.Fun.Function1;

public class SerializerPojo extends SerializerBase implements Serializable {
    static final /* synthetic */ boolean $assertionsDisabled;
    protected static Method androidConstructor = null;
    private static Method androidConstructorGinger = null;
    protected static Map<Class<?>, Constructor<?>> class2constuctor = null;
    private static Object constructorId = null;
    private static final long serialVersionUID = 3181417366609199703L;
    protected static final Serializer<CopyOnWriteArrayList<ClassInfo>> serializer;
    protected static Method sunConstructor;
    protected static Object sunReflFac;
    protected Map<Class<?>, Integer> class2classId;
    protected Map<Integer, Class<?>> classId2class;
    protected DB db;
    protected final ReentrantReadWriteLock lock;
    protected int oldSize;
    protected CopyOnWriteArrayList<ClassInfo> registered;
    protected CopyOnWriteArrayList<Function1> serializationTransformsDeserialize;
    protected CopyOnWriteArrayList<Function1> serializationTransformsSerialize;

    protected static final class ClassInfo {
        protected final List<FieldInfo> fields;
        protected final boolean isEnum;
        protected final String name;
        protected final Map<String, Integer> name2fieldId;
        protected final Map<String, FieldInfo> name2fieldInfo;
        protected ObjectStreamField[] objectStreamFields;
        protected final boolean useObjectStream;

        public ClassInfo(String name, FieldInfo[] fields, boolean isEnum, boolean isExternalizable) {
            this.fields = new ArrayList();
            this.name2fieldInfo = new HashMap();
            this.name2fieldId = new HashMap();
            this.name = name;
            this.isEnum = isEnum;
            this.useObjectStream = isExternalizable;
            for (FieldInfo f : fields) {
                this.name2fieldId.put(f.name, Integer.valueOf(this.fields.size()));
                this.fields.add(f);
                this.name2fieldInfo.put(f.name, f);
            }
        }

        public int getFieldId(String name) {
            Integer fieldId = (Integer) this.name2fieldId.get(name);
            if (fieldId != null) {
                return fieldId.intValue();
            }
            return -1;
        }

        public int addFieldInfo(FieldInfo field) {
            this.name2fieldId.put(field.name, Integer.valueOf(this.fields.size()));
            this.name2fieldInfo.put(field.name, field);
            this.fields.add(field);
            return this.fields.size() - 1;
        }

        public ObjectStreamField[] getObjectStreamFields() {
            return this.objectStreamFields;
        }

        public void setObjectStreamFields(ObjectStreamField[] objectStreamFields) {
            this.objectStreamFields = objectStreamFields;
        }

        public String toString() {
            return super.toString() + "[" + this.name + "]";
        }
    }

    protected static class FieldInfo {
        protected final Class<?> clazz;
        protected Field field;
        protected final String name;
        protected final boolean primitive;
        protected final String type;
        protected Class<?> typeClass;

        public FieldInfo(String name, boolean primitive, String type, Class<?> clazz) {
            this.name = name;
            this.primitive = primitive;
            this.type = type;
            this.clazz = clazz;
            this.typeClass = primitive ? null : SerializerPojo.classForName(type);
            Class<?> aClazz = clazz;
            while (aClazz != Object.class) {
                try {
                    Field f = aClazz.getDeclaredField(name);
                    if (!f.isAccessible()) {
                        f.setAccessible(true);
                    }
                    this.field = f;
                    return;
                } catch (NoSuchFieldException e) {
                    aClazz = aClazz.getSuperclass();
                }
            }
            throw new RuntimeException("Could not set field value: " + name + " - " + clazz.toString());
        }

        public FieldInfo(ObjectStreamField sf, Class<?> clazz) {
            this(sf.getName(), sf.isPrimitive(), sf.getType().getName(), clazz);
        }
    }

    protected final class ObjectInputStream2 extends ObjectInputStream {
        protected ObjectInputStream2(InputStream in) throws IOException, SecurityException {
            super(in);
        }

        protected ObjectStreamClass readClassDescriptor() throws IOException, ClassNotFoundException {
            return ObjectStreamClass.lookup((Class) SerializerPojo.this.classId2class.get(Integer.valueOf(DataInput2.unpackInt(this))));
        }

        protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException {
            Class clazz = Class.forName(desc.getName(), SerializerPojo.$assertionsDisabled, Thread.currentThread().getContextClassLoader());
            return clazz != null ? clazz : super.resolveClass(desc);
        }
    }

    protected final class ObjectOutputStream2 extends ObjectOutputStream {
        protected ObjectOutputStream2(OutputStream out) throws IOException, SecurityException {
            super(out);
        }

        protected void writeClassDescriptor(ObjectStreamClass desc) throws IOException {
            Integer classId = (Integer) SerializerPojo.this.class2classId.get(desc.forClass());
            if (classId == null) {
                SerializerPojo.this.registerClass(desc.forClass());
                classId = (Integer) SerializerPojo.this.class2classId.get(desc.forClass());
            }
            DataOutput2.packInt(this, classId.intValue());
        }
    }

    static {
        boolean z = true;
        if (SerializerPojo.class.desiredAssertionStatus()) {
            z = $assertionsDisabled;
        }
        $assertionsDisabled = z;
        serializer = new Serializer<CopyOnWriteArrayList<ClassInfo>>() {
            public void serialize(DataOutput out, CopyOnWriteArrayList<ClassInfo> obj) throws IOException {
                DataOutput2.packInt(out, obj.size());
                Iterator it = obj.iterator();
                while (it.hasNext()) {
                    ClassInfo ci = (ClassInfo) it.next();
                    out.writeUTF(ci.name);
                    out.writeBoolean(ci.isEnum);
                    out.writeBoolean(ci.useObjectStream);
                    if (!ci.useObjectStream) {
                        DataOutput2.packInt(out, ci.fields.size());
                        for (FieldInfo fi : ci.fields) {
                            out.writeUTF(fi.name);
                            out.writeBoolean(fi.primitive);
                            out.writeUTF(fi.type);
                        }
                    }
                }
            }

            public CopyOnWriteArrayList<ClassInfo> deserialize(DataInput in, int available) throws IOException {
                if (available == 0) {
                    return new CopyOnWriteArrayList();
                }
                int size = DataInput2.unpackInt(in);
                ArrayList<ClassInfo> ret = new ArrayList(size);
                for (int i = 0; i < size; i++) {
                    String className = in.readUTF();
                    boolean isEnum = in.readBoolean();
                    boolean isExternalizable = in.readBoolean();
                    int fieldsNum = isExternalizable ? 0 : DataInput2.unpackInt(in);
                    FieldInfo[] fields = new FieldInfo[fieldsNum];
                    for (int j = 0; j < fieldsNum; j++) {
                        fields[j] = new FieldInfo(in.readUTF(), in.readBoolean(), in.readUTF(), SerializerPojo.classForName(className));
                    }
                    ret.add(new ClassInfo(className, fields, isEnum, isExternalizable));
                }
                return new CopyOnWriteArrayList(ret);
            }

            public int fixedSize() {
                return -1;
            }
        };
        sunConstructor = null;
        sunReflFac = null;
        androidConstructor = null;
        androidConstructorGinger = null;
        try {
            Class clazz = classForName("sun.reflect.ReflectionFactory");
            if (clazz != null) {
                sunReflFac = clazz.getMethod("getReflectionFactory", new Class[0]).invoke(null, new Object[0]);
                sunConstructor = clazz.getMethod("newConstructorForSerialization", new Class[]{Class.class, Constructor.class});
            }
        } catch (Exception e) {
        }
        if (sunConstructor == null) {
            try {
                Method newInstance = ObjectInputStream.class.getDeclaredMethod("newInstance", new Class[]{Class.class, Class.class});
                newInstance.setAccessible(true);
                androidConstructor = newInstance;
            } catch (Exception e2) {
            }
        }
        if (sunConstructor == null && androidConstructor == null) {
            try {
                Method getConstructorId = ObjectStreamClass.class.getDeclaredMethod("getConstructorId", new Class[]{Class.class});
                getConstructorId.setAccessible(true);
                constructorId = getConstructorId.invoke(null, new Object[]{Object.class});
                newInstance = ObjectStreamClass.class.getDeclaredMethod("newInstance", new Class[]{Class.class, getConstructorId.getReturnType()});
                newInstance.setAccessible(true);
                androidConstructorGinger = newInstance;
            } catch (Exception e3) {
            }
        }
        class2constuctor = new ConcurrentHashMap();
    }

    protected static Class<?> classForName(String className) {
        try {
            return Class.forName(className, true, Thread.currentThread().getContextClassLoader());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public SerializerPojo(CopyOnWriteArrayList<ClassInfo> registered) {
        this.lock = new ReentrantReadWriteLock($assertionsDisabled);
        this.class2classId = new HashMap();
        this.classId2class = new HashMap();
        if (registered == null) {
            registered = new CopyOnWriteArrayList();
        }
        this.registered = registered;
        this.oldSize = registered.size();
        for (int i = 0; i < registered.size(); i++) {
            Class clazz = classForName(((ClassInfo) registered.get(i)).name);
            this.class2classId.put(clazz, Integer.valueOf(i));
            this.classId2class.put(Integer.valueOf(i), clazz);
        }
    }

    protected void setDb(DB db) {
        this.db = db;
    }

    public void registerClass(Class<?> clazz) throws IOException {
        if (!containsClass(clazz)) {
            if ($assertionsDisabled || this.lock.isWriteLockedByCurrentThread()) {
                boolean advancedSer = usesAdvancedSerialization(clazz);
                ObjectStreamField[] streamFields = advancedSer ? new ObjectStreamField[0] : getFields(clazz);
                FieldInfo[] fields = new FieldInfo[streamFields.length];
                for (int i = 0; i < fields.length; i++) {
                    fields[i] = new FieldInfo(streamFields[i], clazz);
                }
                ClassInfo i2 = new ClassInfo(clazz.getName(), fields, clazz.isEnum(), advancedSer);
                this.class2classId.put(clazz, Integer.valueOf(this.registered.size()));
                this.classId2class.put(Integer.valueOf(this.registered.size()), clazz);
                this.registered.add(i2);
                saveClassInfo();
                return;
            }
            throw new AssertionError();
        }
    }

    protected boolean usesAdvancedSerialization(Class<?> clazz) {
        if (Externalizable.class.isAssignableFrom(clazz)) {
            return true;
        }
        try {
            if (clazz.getDeclaredMethod("readObject", new Class[]{ObjectInputStream.class}) != null) {
                return true;
            }
        } catch (NoSuchMethodException e) {
        }
        try {
            if (clazz.getDeclaredMethod("writeObject", new Class[]{ObjectOutputStream.class}) != null) {
                return true;
            }
        } catch (NoSuchMethodException e2) {
        }
        try {
            if (clazz.getDeclaredMethod("writeReplace", new Class[0]) != null) {
                return true;
            }
        } catch (NoSuchMethodException e3) {
        }
        return $assertionsDisabled;
    }

    protected void saveClassInfo() {
    }

    protected ObjectStreamField[] getFields(Class<?> clazz) {
        ObjectStreamField[] fields = null;
        ClassInfo classInfo = null;
        Integer classId = (Integer) this.class2classId.get(clazz);
        if (classId != null) {
            classInfo = (ClassInfo) this.registered.get(classId.intValue());
            fields = classInfo.getObjectStreamFields();
        }
        if (fields == null) {
            ObjectStreamClass streamClass = ObjectStreamClass.lookup(clazz);
            FastArrayList<ObjectStreamField> fieldsList = new FastArrayList();
            while (streamClass != null) {
                for (ObjectStreamField f : streamClass.getFields()) {
                    fieldsList.add(f);
                }
                clazz = clazz.getSuperclass();
                streamClass = ObjectStreamClass.lookup(clazz);
            }
            fields = new ObjectStreamField[fieldsList.size];
            System.arraycopy(fieldsList.data, 0, fields, 0, fields.length);
            if (classInfo != null) {
                classInfo.setObjectStreamFields(fields);
            }
        }
        return fields;
    }

    protected void assertClassSerializable(Class<?> clazz) throws NotSerializableException, InvalidClassException {
        if (!containsClass(clazz) && !Serializable.class.isAssignableFrom(clazz)) {
            throw new NotSerializableException(clazz.getName());
        }
    }

    public Object getFieldValue(FieldInfo fieldInfo, Object object) {
        if (fieldInfo.field == null) {
            throw new NoSuchFieldError(object.getClass() + "." + fieldInfo.name);
        }
        try {
            return fieldInfo.field.get(object);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Could not get value from field", e);
        }
    }

    public void setFieldValue(FieldInfo fieldInfo, Object object, Object value) {
        if (fieldInfo.field == null) {
            throw new NoSuchFieldError(object.getClass() + "." + fieldInfo.name);
        }
        try {
            fieldInfo.field.set(object, value);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Could not set field value: ", e);
        }
    }

    public boolean containsClass(Class<?> clazz) {
        return this.class2classId.get(clazz) != null ? true : $assertionsDisabled;
    }

    public int getClassId(Class<?> clazz) {
        Integer classId = (Integer) this.class2classId.get(clazz);
        if (classId != null) {
            return classId.intValue();
        }
        throw new AssertionError("Class is not registered: " + clazz);
    }

    protected Engine getEngine() {
        return this.db.getEngine();
    }

    protected void serializeUnknownObject(DataOutput out, Object obj, FastArrayList<Object> objectStack) throws IOException {
        if (this.db != null) {
            String name = this.db.getNameForObject(obj);
            if (name != null) {
                out.write(Header.NAMED);
                out.writeUTF(name);
                return;
            }
        }
        out.write(Header.POJO);
        this.lock.writeLock().lock();
        try {
            Class<?> clazz = obj.getClass();
            if (!(clazz.isEnum() || clazz.getSuperclass() == null || !clazz.getSuperclass().isEnum())) {
                clazz = clazz.getSuperclass();
            }
            if (clazz != Object.class) {
                assertClassSerializable(clazz);
            }
            registerClass(clazz);
            int classId = getClassId(clazz);
            DataOutput2.packInt(out, classId);
            ClassInfo classInfo = (ClassInfo) this.registered.get(classId);
            if (classInfo.useObjectStream) {
                new ObjectOutputStream2((OutputStream) out).writeObject(obj);
                return;
            }
            if (classInfo.isEnum) {
                DataOutput2.packInt(out, ((Enum) obj).ordinal());
            }
            ObjectStreamField[] fields = getFields(clazz);
            DataOutput2.packInt(out, fields.length);
            for (ObjectStreamField f : fields) {
                int fieldId = classInfo.getFieldId(f.getName());
                if (fieldId == -1) {
                    fieldId = classInfo.addFieldInfo(new FieldInfo(f, clazz));
                    saveClassInfo();
                }
                DataOutput2.packInt(out, fieldId);
                serialize(out, getFieldValue((FieldInfo) classInfo.fields.get(fieldId), obj), objectStack);
            }
            this.lock.writeLock().unlock();
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    protected Object deserializeUnknownHeader(DataInput in, int head, FastArrayList<Object> objectStack) throws IOException {
        Object o;
        if (head == 175) {
            String name = in.readUTF();
            o = this.db.get(name);
            if (o == null) {
                throw new AssertionError("Named object was not found: " + name);
            }
            objectStack.add(o);
        } else if (head != 173) {
            throw new AssertionError();
        } else {
            this.lock.readLock().lock();
            try {
                int classId = DataInput2.unpackInt(in);
                ClassInfo classInfo = (ClassInfo) this.registered.get(classId);
                Class<?> clazz = (Class) this.classId2class.get(Integer.valueOf(classId));
                if (clazz == null) {
                    clazz = classForName(classInfo.name);
                }
                assertClassSerializable(clazz);
                if (classInfo.useObjectStream) {
                    o = new ObjectInputStream2((InputStream) in).readObject();
                } else if (classInfo.isEnum) {
                    o = clazz.getEnumConstants()[DataInput2.unpackInt(in)];
                } else {
                    o = createInstanceSkippinkConstructor(clazz);
                }
                objectStack.add(o);
                if (!classInfo.useObjectStream) {
                    int fieldCount = DataInput2.unpackInt(in);
                    for (int i = 0; i < fieldCount; i++) {
                        int fieldId = DataInput2.unpackInt(in);
                        setFieldValue((FieldInfo) classInfo.fields.get(fieldId), o, deserialize(in, (FastArrayList) objectStack));
                    }
                }
                this.lock.readLock().unlock();
            } catch (Exception e) {
                throw new RuntimeException("Could not instantiate class", e);
            } catch (Throwable th) {
                this.lock.readLock().unlock();
            }
        }
        return o;
    }

    protected <T> T createInstanceSkippinkConstructor(Class<T> clazz) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException {
        if (sunConstructor != null) {
            Constructor<?> intConstr = (Constructor) class2constuctor.get(clazz);
            if (intConstr == null) {
                Constructor<?> objDef = Object.class.getDeclaredConstructor(new Class[0]);
                intConstr = (Constructor) sunConstructor.invoke(sunReflFac, new Object[]{clazz, objDef});
                class2constuctor.put(clazz, intConstr);
            }
            return intConstr.newInstance(new Object[0]);
        } else if (androidConstructor != null) {
            return androidConstructor.invoke(null, new Object[]{clazz, Object.class});
        } else if (androidConstructorGinger != null) {
            return androidConstructorGinger.invoke(null, new Object[]{clazz, constructorId});
        } else {
            Constructor<?> c = (Constructor) class2constuctor.get(clazz);
            if (c == null) {
                c = clazz.getConstructor(new Class[0]);
                if (!c.isAccessible()) {
                    c.setAccessible(true);
                }
                class2constuctor.put(clazz, c);
            }
            return c.newInstance(new Object[0]);
        }
    }

    public boolean hasUnsavedChanges() {
        return this.oldSize != this.registered.size() ? true : $assertionsDisabled;
    }

    public void save(Engine e) {
        e.update(2, this.registered, serializer);
        this.oldSize = this.registered.size();
    }

    public <A, R> void serializerTransformAdd(Function1<A, R> beforeSerialization, Function1<R, A> afterDeserialization) {
        this.lock.writeLock().lock();
        try {
            if (this.serializationTransformsSerialize == null) {
                this.serializationTransformsSerialize = new CopyOnWriteArrayList();
                this.serializationTransformsDeserialize = new CopyOnWriteArrayList();
            }
            this.serializationTransformsSerialize.add(beforeSerialization);
            this.serializationTransformsDeserialize.add(afterDeserialization);
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    public <A, R> void serializerTransformRemove(Function1<A, R> beforeSerialization, Function1<R, A> afterDeserialization) {
        this.lock.writeLock().lock();
        try {
            if (this.serializationTransformsSerialize != null) {
                this.serializationTransformsSerialize.remove(beforeSerialization);
                this.serializationTransformsDeserialize.remove(afterDeserialization);
                this.lock.writeLock().unlock();
            }
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    public void serialize(DataOutput out, Object obj) throws IOException {
        if (this.serializationTransformsSerialize != null) {
            Iterator i$ = this.serializationTransformsSerialize.iterator();
            while (i$.hasNext()) {
                obj = ((Function1) i$.next()).run(obj);
            }
        }
        super.serialize(out, obj);
    }

    public Object deserialize(DataInput is, int capacity) throws IOException {
        Object obj = super.deserialize(is, capacity);
        if (this.serializationTransformsDeserialize != null) {
            Iterator i$ = this.serializationTransformsDeserialize.iterator();
            while (i$.hasNext()) {
                obj = ((Function1) i$.next()).run(obj);
            }
        }
        return obj;
    }
}
