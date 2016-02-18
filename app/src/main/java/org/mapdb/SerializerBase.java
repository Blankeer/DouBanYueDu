package org.mapdb;

import android.support.v4.media.TransportMediator;
import android.support.v4.widget.ExploreByTouchHelper;
import com.alipay.sdk.protocol.h;
import com.douban.book.reader.event.PageFlipEvent;
import com.mcxiaoke.next.ui.widget.AdvancedShareActionProvider;
import com.tencent.connect.common.Constants;
import com.tencent.connect.share.QQShare;
import com.tencent.mm.sdk.modelbase.BaseResp.ErrCode;
import io.fabric.sdk.android.services.settings.SettingsJsonConstants;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.EOFException;
import java.io.IOError;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.UUID;
import org.mapdb.Atomic.Boolean;
import org.mapdb.Atomic.Integer;
import org.mapdb.Atomic.Long;
import org.mapdb.Atomic.String;
import org.mapdb.Atomic.Var;
import org.mapdb.BTreeKeySerializer.BasicKeySerializer;
import org.mapdb.BTreeKeySerializer.Tuple2KeySerializer;
import org.mapdb.BTreeKeySerializer.Tuple3KeySerializer;
import org.mapdb.BTreeKeySerializer.Tuple4KeySerializer;
import org.mapdb.BTreeKeySerializer.Tuple5KeySerializer;
import org.mapdb.BTreeKeySerializer.Tuple6KeySerializer;
import org.mapdb.Fun.ArrayComparator;
import org.mapdb.Fun.Tuple2;
import org.mapdb.Fun.Tuple2Comparator;
import org.mapdb.Fun.Tuple3;
import org.mapdb.Fun.Tuple3Comparator;
import org.mapdb.Fun.Tuple4;
import org.mapdb.Fun.Tuple4Comparator;
import org.mapdb.Fun.Tuple5;
import org.mapdb.Fun.Tuple5Comparator;
import org.mapdb.Fun.Tuple6;
import org.mapdb.Fun.Tuple6Comparator;
import org.mapdb.Serializer.CompressionWrapper;
import se.emilsjolander.stickylistheaders.R;
import u.aly.ci;
import u.aly.dj;
import u.aly.dx;

public class SerializerBase implements Serializer<Object> {
    static final /* synthetic */ boolean $assertionsDisabled;
    protected static final String EMPTY_STRING = "";

    protected static final class FastArrayList<K> {
        public K[] data;
        public boolean forwardRefs;
        public int size;

        public FastArrayList() {
            this.forwardRefs = SerializerBase.$assertionsDisabled;
            this.size = 0;
            this.data = new Object[1];
        }

        public void add(K o) {
            if (this.data.length == this.size) {
                this.data = Arrays.copyOf(this.data, this.data.length * 2);
            }
            this.data[this.size] = o;
            this.size++;
        }

        public int identityIndexOf(Object obj) {
            for (int i = 0; i < this.size; i++) {
                if (obj == this.data[i]) {
                    this.forwardRefs = true;
                    return i;
                }
            }
            return -1;
        }
    }

    protected interface Header {
        public static final int ARRAYLIST = 163;
        public static final int ARRAYLIST_PACKED_LONG = 160;
        public static final int ARRAY_BOOLEAN = 111;
        public static final int ARRAY_BYTE = 109;
        public static final int ARRAY_BYTE_ALL_EQUAL = 110;
        public static final int ARRAY_CHAR = 113;
        public static final int ARRAY_DOUBLE = 115;
        public static final int ARRAY_FLOAT = 114;
        public static final int ARRAY_INT = 119;
        public static final int ARRAY_INT_BYTE = 116;
        public static final int ARRAY_INT_PACKED = 118;
        public static final int ARRAY_INT_SHORT = 117;
        public static final int ARRAY_LONG = 124;
        public static final int ARRAY_LONG_BYTE = 120;
        public static final int ARRAY_LONG_INT = 123;
        public static final int ARRAY_LONG_PACKED = 122;
        public static final int ARRAY_LONG_SHORT = 121;
        public static final int ARRAY_OBJECT = 158;
        public static final int ARRAY_OBJECT_ALL_NULL = 161;
        public static final int ARRAY_OBJECT_NO_REFS = 162;
        public static final int ARRAY_OBJECT_PACKED_LONG = 159;
        public static final int ARRAY_SHORT = 112;
        public static final int BIGDECIMAL = 137;
        public static final int BIGINTEGER = 138;
        public static final int BOOLEAN_FALSE = 3;
        public static final int BOOLEAN_TRUE = 2;
        public static final int BYTE = 85;
        public static final int BYTE_0 = 83;
        public static final int BYTE_1 = 84;
        public static final int BYTE_M1 = 82;
        public static final int CHAR = 89;
        public static final int CHAR_0 = 86;
        public static final int CHAR_1 = 87;
        public static final int CHAR_255 = 88;
        public static final int CLASS = 139;
        public static final int DATE = 140;
        public static final int DOUBLE = 108;
        public static final int DOUBLE_0 = 103;
        public static final int DOUBLE_1 = 104;
        public static final int DOUBLE_255 = 105;
        public static final int DOUBLE_INT = 107;
        public static final int DOUBLE_M1 = 102;
        public static final int DOUBLE_SHORT = 106;
        public static final int FLOAT = 101;
        public static final int FLOAT_0 = 97;
        public static final int FLOAT_1 = 98;
        public static final int FLOAT_255 = 99;
        public static final int FLOAT_M1 = 96;
        public static final int FLOAT_SHORT = 100;
        public static final int FUN_HI = 141;
        public static final int HASHMAP = 165;
        public static final int HASHSET = 168;
        public static final int INT = 38;
        public static final int INT_0 = 13;
        public static final int INT_1 = 14;
        public static final int INT_10 = 23;
        public static final int INT_11 = 24;
        public static final int INT_12 = 25;
        public static final int INT_13 = 26;
        public static final int INT_14 = 27;
        public static final int INT_15 = 28;
        public static final int INT_16 = 29;
        public static final int INT_2 = 15;
        public static final int INT_3 = 16;
        public static final int INT_4 = 17;
        public static final int INT_5 = 18;
        public static final int INT_6 = 19;
        public static final int INT_7 = 20;
        public static final int INT_8 = 21;
        public static final int INT_9 = 22;
        public static final int INT_F1 = 33;
        public static final int INT_F2 = 35;
        public static final int INT_F3 = 37;
        public static final int INT_M1 = 12;
        public static final int INT_M2 = 11;
        public static final int INT_M3 = 10;
        public static final int INT_M4 = 9;
        public static final int INT_M5 = 8;
        public static final int INT_M6 = 7;
        public static final int INT_M7 = 6;
        public static final int INT_M8 = 5;
        public static final int INT_M9 = 4;
        public static final int INT_MAX_VALUE = 31;
        public static final int INT_MF1 = 32;
        public static final int INT_MF2 = 34;
        public static final int INT_MF3 = 36;
        public static final int INT_MIN_VALUE = 30;
        public static final int JAVA_SERIALIZATION = 172;
        public static final int LINKEDHASHMAP = 166;
        public static final int LINKEDHASHSET = 169;
        public static final int LINKEDLIST = 170;
        public static final int LONG = 81;
        public static final int LONG_0 = 48;
        public static final int LONG_1 = 49;
        public static final int LONG_10 = 58;
        public static final int LONG_11 = 59;
        public static final int LONG_12 = 60;
        public static final int LONG_13 = 61;
        public static final int LONG_14 = 62;
        public static final int LONG_15 = 63;
        public static final int LONG_16 = 64;
        public static final int LONG_2 = 50;
        public static final int LONG_3 = 51;
        public static final int LONG_4 = 52;
        public static final int LONG_5 = 53;
        public static final int LONG_6 = 54;
        public static final int LONG_7 = 55;
        public static final int LONG_8 = 56;
        public static final int LONG_9 = 57;
        public static final int LONG_F1 = 68;
        public static final int LONG_F2 = 70;
        public static final int LONG_F3 = 72;
        public static final int LONG_F4 = 74;
        public static final int LONG_F5 = 76;
        public static final int LONG_F6 = 78;
        public static final int LONG_F7 = 80;
        public static final int LONG_M1 = 47;
        public static final int LONG_M2 = 46;
        public static final int LONG_M3 = 45;
        public static final int LONG_M4 = 44;
        public static final int LONG_M5 = 43;
        public static final int LONG_M6 = 42;
        public static final int LONG_M7 = 41;
        public static final int LONG_M8 = 40;
        public static final int LONG_M9 = 39;
        public static final int LONG_MAX_VALUE = 66;
        public static final int LONG_MF1 = 67;
        public static final int LONG_MF2 = 69;
        public static final int LONG_MF3 = 71;
        public static final int LONG_MF4 = 73;
        public static final int LONG_MF5 = 75;
        public static final int LONG_MF6 = 77;
        public static final int LONG_MF7 = 79;
        public static final int LONG_MIN_VALUE = 65;
        public static final int MAPDB = 150;
        public static final int MA_BOOL = 178;
        public static final int MA_INT = 177;
        public static final int MA_LONG = 176;
        public static final int MA_STRING = 179;
        public static final int MA_VAR = 180;
        public static final int NAMED = 175;
        public static final int NULL = 1;
        public static final int OBJECT_STACK = 174;
        public static final int POJO = 173;
        public static final int PROPERTIES = 171;
        public static final int SHORT = 95;
        public static final int SHORT_0 = 91;
        public static final int SHORT_1 = 92;
        public static final int SHORT_255 = 93;
        public static final int SHORT_M1 = 90;
        public static final int SHORT_M255 = 94;
        public static final int STRING = 136;
        public static final int STRING_0 = 125;
        public static final int STRING_1 = 126;
        public static final int STRING_10 = 135;
        public static final int STRING_2 = 127;
        public static final int STRING_3 = 128;
        public static final int STRING_4 = 129;
        public static final int STRING_5 = 130;
        public static final int STRING_6 = 131;
        public static final int STRING_7 = 132;
        public static final int STRING_8 = 133;
        public static final int STRING_9 = 134;
        public static final int TREEMAP = 164;
        public static final int TREESET = 167;
        public static final int TUPLE2 = 151;
        public static final int TUPLE3 = 152;
        public static final int TUPLE4 = 153;
        public static final int TUPLE5 = 154;
        public static final int TUPLE6 = 155;
        public static final int TUPLE7 = 156;
        public static final int TUPLE8 = 157;
        public static final int UUID = 142;
        public static final int ZERO_FAIL = 0;
    }

    protected interface HeaderMapDB {
        public static final int BYTE_ARRAY_SERIALIZER = 21;
        public static final int B_TREE_BASIC_KEY_SERIALIZER = 15;
        public static final int B_TREE_COMPRESSION_SERIALIZER = 48;
        public static final int B_TREE_SERIALIZER_POS_INT = 3;
        public static final int B_TREE_SERIALIZER_POS_LONG = 1;
        public static final int B_TREE_SERIALIZER_STRING = 2;
        public static final int COMPARABLE_COMPARATOR = 11;
        public static final int COMPARATOR_ARRAY = 45;
        public static final int COMPARATOR_BYTE_ARRAY = 39;
        public static final int COMPARATOR_CHAR_ARRAY = 40;
        public static final int COMPARATOR_COMPARABLE_ARRAY = 44;
        public static final int COMPARATOR_DOUBLE_ARRAY = 43;
        public static final int COMPARATOR_INT_ARRAY = 41;
        public static final int COMPARATOR_LONG_ARRAY = 42;
        public static final int FUN_COMPARATOR = 10;
        public static final int FUN_COMPARATOR_REVERSE = 28;
        public static final int FUN_EMPTY_ITERATOR = 50;
        public static final int HASHER_ARRAY = 57;
        public static final int HASHER_BASIC = 33;
        public static final int HASHER_BYTE_ARRAY = 34;
        public static final int HASHER_CHAR_ARRAY = 35;
        public static final int HASHER_DOUBLE_ARRAY = 38;
        public static final int HASHER_INT_ARRAY = 36;
        public static final int HASHER_LONG_ARRAY = 37;
        public static final int SERIALIZER_BASIC = 13;
        public static final int SERIALIZER_BOOLEAN = 16;
        public static final int SERIALIZER_BYTE_ARRAY_NOSIZE = 17;
        public static final int SERIALIZER_CHAR_ARRAY = 29;
        public static final int SERIALIZER_COMPRESSION_WRAPPER = 47;
        public static final int SERIALIZER_DOUBLE_ARRAY = 32;
        public static final int SERIALIZER_ILLEGAL_ACCESS = 6;
        public static final int SERIALIZER_INT = 5;
        public static final int SERIALIZER_INT_ARRAY = 30;
        public static final int SERIALIZER_JAVA = 18;
        public static final int SERIALIZER_KEY_TUPLE2 = 7;
        public static final int SERIALIZER_KEY_TUPLE3 = 8;
        public static final int SERIALIZER_KEY_TUPLE4 = 9;
        public static final int SERIALIZER_KEY_TUPLE5 = 55;
        public static final int SERIALIZER_KEY_TUPLE6 = 56;
        public static final int SERIALIZER_LONG = 4;
        public static final int SERIALIZER_LONG_ARRAY = 31;
        public static final int SERIALIZER_STRING = 20;
        public static final int SERIALIZER_STRING_ASCII = 46;
        public static final int SERIALIZER_STRING_INTERN = 49;
        public static final int SERIALIZER_STRING_NOSIZE = 14;
        public static final int SERIALIZER_UUID = 19;
        public static final int THIS_SERIALIZER = 12;
        public static final int TUPLE2_COMPARATOR = 22;
        public static final int TUPLE2_COMPARATOR_STATIC = 25;
        public static final int TUPLE3_COMPARATOR = 23;
        public static final int TUPLE3_COMPARATOR_STATIC = 26;
        public static final int TUPLE4_COMPARATOR = 24;
        public static final int TUPLE4_COMPARATOR_STATIC = 27;
        public static final int TUPLE5_COMPARATOR = 51;
        public static final int TUPLE5_COMPARATOR_STATIC = 53;
        public static final int TUPLE6_COMPARATOR = 52;
        public static final int TUPLE6_COMPARATOR_STATIC = 54;
    }

    protected static final class singletons {
        static final Map<Object, Integer> all;
        static final LongHashMap<Object> reverse;

        protected singletons() {
        }

        static {
            all = new IdentityHashMap();
            reverse = new LongHashMap();
            all.put(BTreeKeySerializer.STRING, Integer.valueOf(2));
            all.put(BTreeKeySerializer.ZERO_OR_POSITIVE_LONG, Integer.valueOf(1));
            all.put(BTreeKeySerializer.ZERO_OR_POSITIVE_INT, Integer.valueOf(3));
            all.put(BTreeMap.COMPARABLE_COMPARATOR, Integer.valueOf(11));
            all.put(Fun.COMPARATOR, Integer.valueOf(10));
            all.put(Fun.REVERSE_COMPARATOR, Integer.valueOf(28));
            all.put(Fun.EMPTY_ITERATOR, Integer.valueOf(50));
            all.put(Fun.TUPLE2_COMPARATOR, Integer.valueOf(25));
            all.put(Fun.TUPLE3_COMPARATOR, Integer.valueOf(26));
            all.put(Fun.TUPLE4_COMPARATOR, Integer.valueOf(27));
            all.put(Fun.TUPLE5_COMPARATOR, Integer.valueOf(53));
            all.put(Fun.TUPLE6_COMPARATOR, Integer.valueOf(54));
            all.put(Serializer.STRING_NOSIZE, Integer.valueOf(14));
            all.put(Serializer.STRING_ASCII, Integer.valueOf(46));
            all.put(Serializer.STRING_INTERN, Integer.valueOf(49));
            all.put(Serializer.LONG, Integer.valueOf(4));
            all.put(Serializer.INTEGER, Integer.valueOf(5));
            all.put(Serializer.ILLEGAL_ACCESS, Integer.valueOf(6));
            all.put(Serializer.BASIC, Integer.valueOf(13));
            all.put(Serializer.BOOLEAN, Integer.valueOf(16));
            all.put(Serializer.BYTE_ARRAY_NOSIZE, Integer.valueOf(17));
            all.put(Serializer.BYTE_ARRAY, Integer.valueOf(21));
            all.put(Serializer.JAVA, Integer.valueOf(18));
            all.put(Serializer.UUID, Integer.valueOf(19));
            all.put(Serializer.STRING, Integer.valueOf(20));
            all.put(Serializer.CHAR_ARRAY, Integer.valueOf(29));
            all.put(Serializer.INT_ARRAY, Integer.valueOf(30));
            all.put(Serializer.LONG_ARRAY, Integer.valueOf(31));
            all.put(Serializer.DOUBLE_ARRAY, Integer.valueOf(32));
            all.put(Hasher.BASIC, Integer.valueOf(33));
            all.put(Hasher.BYTE_ARRAY, Integer.valueOf(34));
            all.put(Hasher.CHAR_ARRAY, Integer.valueOf(35));
            all.put(Hasher.INT_ARRAY, Integer.valueOf(36));
            all.put(Hasher.LONG_ARRAY, Integer.valueOf(37));
            all.put(Hasher.DOUBLE_ARRAY, Integer.valueOf(38));
            all.put(Hasher.ARRAY, Integer.valueOf(57));
            all.put(Fun.BYTE_ARRAY_COMPARATOR, Integer.valueOf(39));
            all.put(Fun.CHAR_ARRAY_COMPARATOR, Integer.valueOf(40));
            all.put(Fun.INT_ARRAY_COMPARATOR, Integer.valueOf(41));
            all.put(Fun.LONG_ARRAY_COMPARATOR, Integer.valueOf(42));
            all.put(Fun.DOUBLE_ARRAY_COMPARATOR, Integer.valueOf(43));
            all.put(Fun.COMPARABLE_ARRAY_COMPARATOR, Integer.valueOf(44));
            all.put(Fun.HI, Integer.valueOf(ExploreByTouchHelper.INVALID_ID));
            for (Entry<Object, Integer> e : all.entrySet()) {
                reverse.put((long) ((Integer) e.getValue()).intValue(), e.getKey());
            }
        }
    }

    static {
        $assertionsDisabled = !SerializerBase.class.desiredAssertionStatus() ? true : $assertionsDisabled;
    }

    public void serialize(DataOutput out, Object obj) throws IOException {
        serialize(out, obj, null);
    }

    public void serialize(DataOutput out, Object obj, FastArrayList<Object> objectStack) throws IOException {
        if (objectStack != null) {
            int indexInObjectStack = objectStack.identityIndexOf(obj);
            if (indexInObjectStack != -1) {
                out.write(Header.OBJECT_STACK);
                DataOutput2.packInt(out, indexInObjectStack);
                return;
            }
            objectStack.add(obj);
        }
        if (obj == null) {
            out.write(1);
            return;
        }
        Class clazz = obj.getClass();
        if (clazz == Integer.class) {
            serializeInt(out, obj);
        } else if (clazz == Long.class) {
            serializeLong(out, obj);
        } else if (clazz == String.class) {
            serializeString(out, obj);
        } else if (clazz == Boolean.class) {
            out.write(((Boolean) obj).booleanValue() ? 2 : 3);
        } else if (clazz == Byte.class) {
            serializeByte(out, obj);
        } else if (clazz == Character.class) {
            serializerChar(out, obj);
        } else if (clazz == Short.class) {
            serializeShort(out, obj);
        } else if (clazz == Float.class) {
            serializeFloat(out, obj);
        } else if (clazz == Double.class) {
            serializeDouble(out, obj);
        } else {
            serialize2(out, obj, objectStack, clazz);
        }
    }

    private void serialize2(DataOutput out, Object obj, FastArrayList<Object> objectStack, Class<?> clazz) throws IOException {
        if (obj instanceof byte[]) {
            serializeByteArray(out, (byte[]) obj);
        } else if (obj instanceof boolean[]) {
            out.write(Header.ARRAY_BOOLEAN);
            boolean[] a_bool = (boolean[]) obj;
            DataOutput2.packInt(out, a_bool.length);
            out.write(booleanToByteArray(a_bool));
        } else if (obj instanceof short[]) {
            out.write(Header.ARRAY_SHORT);
            short[] a = (short[]) obj;
            DataOutput2.packInt(out, a.length);
            for (short writeShort : a) {
                out.writeShort(writeShort);
            }
        } else if (obj instanceof char[]) {
            out.write(Header.ARRAY_CHAR);
            char[] a2 = (char[]) obj;
            DataOutput2.packInt(out, a2.length);
            for (char writeChar : a2) {
                out.writeChar(writeChar);
            }
        } else if (obj instanceof float[]) {
            out.write(Header.ARRAY_FLOAT);
            float[] a3 = (float[]) obj;
            DataOutput2.packInt(out, a3.length);
            for (float writeFloat : a3) {
                out.writeFloat(writeFloat);
            }
        } else if (obj instanceof double[]) {
            out.write(Header.ARRAY_DOUBLE);
            double[] a4 = (double[]) obj;
            DataOutput2.packInt(out, a4.length);
            for (double writeDouble : a4) {
                out.writeDouble(writeDouble);
            }
        } else if (obj instanceof int[]) {
            serializeIntArray(out, (int[]) obj);
        } else if (obj instanceof long[]) {
            serializeLongArray(out, (long[]) obj);
        } else if (clazz == BigInteger.class) {
            out.write(Header.BIGINTEGER);
            buf = ((BigInteger) obj).toByteArray();
            DataOutput2.packInt(out, buf.length);
            out.write(buf);
        } else if (clazz == BigDecimal.class) {
            out.write(Header.BIGDECIMAL);
            BigDecimal d = (BigDecimal) obj;
            buf = d.unscaledValue().toByteArray();
            DataOutput2.packInt(out, buf.length);
            out.write(buf);
            DataOutput2.packInt(out, d.scale());
        } else if (obj instanceof Class) {
            out.write(Header.CLASS);
            serializeClass(out, (Class) obj);
        } else if (clazz == Date.class) {
            out.write(Header.DATE);
            out.writeLong(((Date) obj).getTime());
        } else if (clazz == UUID.class) {
            out.write(Header.UUID);
            out.writeLong(((UUID) obj).getMostSignificantBits());
            out.writeLong(((UUID) obj).getLeastSignificantBits());
        } else if (obj == Fun.HI) {
            out.write(Header.FUN_HI);
        } else {
            Integer mapdbSingletonHeader = (Integer) singletons.all.get(obj);
            if (mapdbSingletonHeader != null) {
                out.write(Header.MAPDB);
                DataOutput2.packInt(out, mapdbSingletonHeader.intValue());
            } else if (obj instanceof Long) {
                out.write(Header.MA_LONG);
                DataOutput2.packLong(out, ((Long) obj).recid);
            } else if (obj instanceof Integer) {
                out.write(Header.MA_INT);
                DataOutput2.packLong(out, ((Integer) obj).recid);
            } else if (obj instanceof Boolean) {
                out.write(Header.MA_BOOL);
                DataOutput2.packLong(out, ((Boolean) obj).recid);
            } else if (obj instanceof String) {
                out.write(Header.MA_STRING);
                DataOutput2.packLong(out, ((String) obj).recid);
            } else if (obj == this) {
                out.write(Header.MAPDB);
                DataOutput2.packInt(out, 12);
            } else {
                if (objectStack == null) {
                    objectStack = new FastArrayList();
                    objectStack.add(obj);
                }
                boolean packableLongs;
                Object o;
                if (obj instanceof Object[]) {
                    Object[] b = (Object[]) obj;
                    int length = b.length;
                    packableLongs = r0 <= 255 ? true : $assertionsDisabled;
                    boolean allNull = true;
                    if (packableLongs) {
                        for (Object o2 : b) {
                            if (o2 != null) {
                                allNull = $assertionsDisabled;
                                if (o2.getClass() != Long.class || (((Long) o2).longValue() < 0 && ((Long) o2).longValue() != Long.MAX_VALUE)) {
                                    packableLongs = $assertionsDisabled;
                                }
                            }
                            if (!packableLongs && !allNull) {
                                break;
                            }
                        }
                    } else {
                        for (Object o22 : b) {
                            if (o22 != null) {
                                allNull = $assertionsDisabled;
                                break;
                            }
                        }
                    }
                    if (allNull) {
                        out.write(Header.ARRAY_OBJECT_ALL_NULL);
                        DataOutput2.packInt(out, b.length);
                        serializeClass(out, obj.getClass().getComponentType());
                    } else if (packableLongs) {
                        out.write(Header.ARRAY_OBJECT_PACKED_LONG);
                        out.write(b.length);
                        for (Object o222 : b) {
                            if (o222 == null) {
                                DataOutput2.packLong(out, 0);
                            } else {
                                DataOutput2.packLong(out, ((Long) o222).longValue() + 1);
                            }
                        }
                    } else {
                        out.write(Header.ARRAY_OBJECT);
                        DataOutput2.packInt(out, b.length);
                        serializeClass(out, obj.getClass().getComponentType());
                        for (Object serialize : b) {
                            serialize(out, serialize, objectStack);
                        }
                    }
                } else if (clazz == ArrayList.class) {
                    ArrayList l = (ArrayList) obj;
                    packableLongs = l.size() < 255 ? true : $assertionsDisabled;
                    if (packableLongs) {
                        i$ = l.iterator();
                        while (i$.hasNext()) {
                            o222 = i$.next();
                            if (o222 != null && (o222.getClass() != Long.class || (((Long) o222).longValue() < 0 && ((Long) o222).longValue() != Long.MAX_VALUE))) {
                                packableLongs = $assertionsDisabled;
                                break;
                            }
                        }
                    }
                    if (packableLongs) {
                        out.write(Header.ARRAYLIST_PACKED_LONG);
                        out.write(l.size());
                        i$ = l.iterator();
                        while (i$.hasNext()) {
                            o222 = i$.next();
                            if (o222 == null) {
                                DataOutput2.packLong(out, 0);
                            } else {
                                DataOutput2.packLong(out, ((Long) o222).longValue() + 1);
                            }
                        }
                        return;
                    }
                    serializeCollection(Header.ARRAYLIST, out, obj, objectStack);
                } else if (clazz == LinkedList.class) {
                    serializeCollection(Header.LINKEDLIST, out, obj, objectStack);
                } else if (clazz == TreeSet.class) {
                    TreeSet l2 = (TreeSet) obj;
                    out.write(Header.TREESET);
                    DataOutput2.packInt(out, l2.size());
                    serialize(out, l2.comparator(), objectStack);
                    i$ = l2.iterator();
                    while (i$.hasNext()) {
                        serialize(out, i$.next(), objectStack);
                    }
                } else if (clazz == HashSet.class) {
                    serializeCollection(Header.HASHSET, out, obj, objectStack);
                } else if (clazz == LinkedHashSet.class) {
                    serializeCollection(Header.LINKEDHASHSET, out, obj, objectStack);
                } else if (clazz == TreeMap.class) {
                    TreeMap l3 = (TreeMap) obj;
                    out.write(Header.TREEMAP);
                    DataOutput2.packInt(out, l3.size());
                    serialize(out, l3.comparator(), objectStack);
                    for (Object o2222 : l3.keySet()) {
                        serialize(out, o2222, objectStack);
                        serialize(out, l3.get(o2222), objectStack);
                    }
                } else if (clazz == HashMap.class) {
                    serializeMap(Header.HASHMAP, out, obj, objectStack);
                } else if (clazz == LinkedHashMap.class) {
                    serializeMap(Header.LINKEDHASHMAP, out, obj, objectStack);
                } else if (clazz == Properties.class) {
                    serializeMap(Header.PROPERTIES, out, obj, objectStack);
                } else if (clazz == Tuple2.class) {
                    out.write(Header.TUPLE2);
                    Tuple2 t = (Tuple2) obj;
                    serialize(out, t.a, objectStack);
                    serialize(out, t.b, objectStack);
                } else if (clazz == Tuple3.class) {
                    out.write(Header.TUPLE3);
                    Tuple3 t2 = (Tuple3) obj;
                    serialize(out, t2.a, objectStack);
                    serialize(out, t2.b, objectStack);
                    serialize(out, t2.c, objectStack);
                } else if (clazz == Tuple4.class) {
                    out.write(Header.TUPLE4);
                    Tuple4 t3 = (Tuple4) obj;
                    serialize(out, t3.a, objectStack);
                    serialize(out, t3.b, objectStack);
                    serialize(out, t3.c, objectStack);
                    serialize(out, t3.d, objectStack);
                } else if (clazz == Tuple5.class) {
                    out.write(Header.TUPLE5);
                    Tuple5 t4 = (Tuple5) obj;
                    serialize(out, t4.a, objectStack);
                    serialize(out, t4.b, objectStack);
                    serialize(out, t4.c, objectStack);
                    serialize(out, t4.d, objectStack);
                    serialize(out, t4.e, objectStack);
                } else if (clazz == Tuple6.class) {
                    out.write(Header.TUPLE6);
                    Tuple6 t5 = (Tuple6) obj;
                    serialize(out, t5.a, objectStack);
                    serialize(out, t5.b, objectStack);
                    serialize(out, t5.c, objectStack);
                    serialize(out, t5.d, objectStack);
                    serialize(out, t5.e, objectStack);
                    serialize(out, t5.f, objectStack);
                } else if (clazz == Tuple2KeySerializer.class) {
                    out.write(Header.MAPDB);
                    DataOutput2.packInt(out, 7);
                    Tuple2KeySerializer s = (Tuple2KeySerializer) obj;
                    serialize(out, s.aComparator, objectStack);
                    serialize(out, s.aSerializer, objectStack);
                    serialize(out, s.bSerializer, objectStack);
                } else if (clazz == Tuple3KeySerializer.class) {
                    out.write(Header.MAPDB);
                    DataOutput2.packInt(out, 8);
                    Tuple3KeySerializer s2 = (Tuple3KeySerializer) obj;
                    serialize(out, s2.aComparator, objectStack);
                    serialize(out, s2.bComparator, objectStack);
                    serialize(out, s2.aSerializer, objectStack);
                    serialize(out, s2.bSerializer, objectStack);
                    serialize(out, s2.cSerializer, objectStack);
                } else if (clazz == Tuple4KeySerializer.class) {
                    out.write(Header.MAPDB);
                    DataOutput2.packInt(out, 9);
                    Tuple4KeySerializer s3 = (Tuple4KeySerializer) obj;
                    serialize(out, s3.aComparator, objectStack);
                    serialize(out, s3.bComparator, objectStack);
                    serialize(out, s3.cComparator, objectStack);
                    serialize(out, s3.aSerializer, objectStack);
                    serialize(out, s3.bSerializer, objectStack);
                    serialize(out, s3.cSerializer, objectStack);
                    serialize(out, s3.dSerializer, objectStack);
                } else if (clazz == Tuple5KeySerializer.class) {
                    out.write(Header.MAPDB);
                    DataOutput2.packInt(out, 55);
                    Tuple5KeySerializer s4 = (Tuple5KeySerializer) obj;
                    serialize(out, s4.aComparator, objectStack);
                    serialize(out, s4.bComparator, objectStack);
                    serialize(out, s4.cComparator, objectStack);
                    serialize(out, s4.dComparator, objectStack);
                    serialize(out, s4.aSerializer, objectStack);
                    serialize(out, s4.bSerializer, objectStack);
                    serialize(out, s4.cSerializer, objectStack);
                    serialize(out, s4.dSerializer, objectStack);
                    serialize(out, s4.eSerializer, objectStack);
                } else if (clazz == Tuple6KeySerializer.class) {
                    out.write(Header.MAPDB);
                    DataOutput2.packInt(out, 56);
                    Tuple6KeySerializer s5 = (Tuple6KeySerializer) obj;
                    serialize(out, s5.aComparator, objectStack);
                    serialize(out, s5.bComparator, objectStack);
                    serialize(out, s5.cComparator, objectStack);
                    serialize(out, s5.dComparator, objectStack);
                    serialize(out, s5.eComparator, objectStack);
                    serialize(out, s5.aSerializer, objectStack);
                    serialize(out, s5.bSerializer, objectStack);
                    serialize(out, s5.cSerializer, objectStack);
                    serialize(out, s5.dSerializer, objectStack);
                    serialize(out, s5.eSerializer, objectStack);
                    serialize(out, s5.fSerializer, objectStack);
                } else if (clazz == BasicKeySerializer.class) {
                    out.write(Header.MAPDB);
                    DataOutput2.packInt(out, 15);
                    serialize(out, ((BasicKeySerializer) obj).defaultSerializer, objectStack);
                } else if (clazz == ArrayComparator.class) {
                    out.write(Header.MAPDB);
                    DataOutput2.packInt(out, 45);
                    serialize(out, ((ArrayComparator) obj).comparators, objectStack);
                } else if (clazz == CompressionWrapper.class) {
                    out.write(Header.MAPDB);
                    DataOutput2.packInt(out, 47);
                    serialize(out, ((CompressionWrapper) obj).serializer, objectStack);
                } else if (obj instanceof Tuple2Comparator) {
                    out.write(Header.MAPDB);
                    DataOutput2.packInt(out, 22);
                    Tuple2Comparator c = (Tuple2Comparator) obj;
                    serialize(out, c.a, objectStack);
                    serialize(out, c.b, objectStack);
                } else if (obj instanceof Tuple3Comparator) {
                    out.write(Header.MAPDB);
                    DataOutput2.packInt(out, 23);
                    Tuple3Comparator c2 = (Tuple3Comparator) obj;
                    serialize(out, c2.a, objectStack);
                    serialize(out, c2.b, objectStack);
                    serialize(out, c2.c, objectStack);
                } else if (obj instanceof Tuple4Comparator) {
                    out.write(Header.MAPDB);
                    DataOutput2.packInt(out, 24);
                    Tuple4Comparator c3 = (Tuple4Comparator) obj;
                    serialize(out, c3.a, objectStack);
                    serialize(out, c3.b, objectStack);
                    serialize(out, c3.c, objectStack);
                    serialize(out, c3.d, objectStack);
                } else if (obj instanceof Tuple5Comparator) {
                    out.write(Header.MAPDB);
                    DataOutput2.packInt(out, 51);
                    Tuple5Comparator c4 = (Tuple5Comparator) obj;
                    serialize(out, c4.a, objectStack);
                    serialize(out, c4.b, objectStack);
                    serialize(out, c4.c, objectStack);
                    serialize(out, c4.d, objectStack);
                    serialize(out, c4.e, objectStack);
                } else if (obj instanceof Tuple6Comparator) {
                    out.write(Header.MAPDB);
                    DataOutput2.packInt(out, 52);
                    Tuple6Comparator c5 = (Tuple6Comparator) obj;
                    serialize(out, c5.a, objectStack);
                    serialize(out, c5.b, objectStack);
                    serialize(out, c5.c, objectStack);
                    serialize(out, c5.d, objectStack);
                    serialize(out, c5.e, objectStack);
                    serialize(out, c5.f, objectStack);
                } else if (obj instanceof Var) {
                    out.write(Header.MA_VAR);
                    Var v = (Var) obj;
                    DataOutput2.packLong(out, v.recid);
                    serialize(out, v.serializer, objectStack);
                } else {
                    serializeUnknownObject(out, obj, objectStack);
                }
            }
        }
    }

    private void serializeString(DataOutput out, Object obj) throws IOException {
        int len = ((String) obj).length();
        if (len == 0) {
            out.write(Header.STRING_0);
            return;
        }
        if (len <= 10) {
            out.write(len + Header.STRING_0);
        } else {
            out.write(Header.STRING);
            DataOutput2.packInt(out, len);
        }
        for (int i = 0; i < len; i++) {
            DataOutput2.packInt(out, ((String) obj).charAt(i));
        }
    }

    private void serializeLongArray(DataOutput out, long[] val) throws IOException {
        long max = Long.MIN_VALUE;
        long min = Long.MAX_VALUE;
        for (long i : val) {
            max = Math.max(max, i);
            min = Math.min(min, i);
        }
        if (-128 <= min && max <= 127) {
            out.write(Header.ARRAY_LONG_BYTE);
            DataOutput2.packInt(out, val.length);
            for (long i2 : val) {
                out.write((int) i2);
            }
        } else if (-32768 <= min && max <= 32767) {
            out.write(Header.ARRAY_LONG_SHORT);
            DataOutput2.packInt(out, val.length);
            for (long i22 : val) {
                out.writeShort((int) i22);
            }
        } else if (0 <= min) {
            out.write(Header.ARRAY_LONG_PACKED);
            DataOutput2.packInt(out, val.length);
            for (long l : val) {
                DataOutput2.packLong(out, l);
            }
        } else if (-2147483648L > min || max > 2147483647L) {
            out.write(Header.ARRAY_LONG);
            DataOutput2.packInt(out, val.length);
            for (long i222 : val) {
                out.writeLong(i222);
            }
        } else {
            out.write(Header.ARRAY_LONG_INT);
            DataOutput2.packInt(out, val.length);
            for (long i2222 : val) {
                out.writeInt((int) i2222);
            }
        }
    }

    private void serializeIntArray(DataOutput out, int[] val) throws IOException {
        int max = ExploreByTouchHelper.INVALID_ID;
        int min = AdvancedShareActionProvider.WEIGHT_MAX;
        for (int i : val) {
            max = Math.max(max, i);
            min = Math.min(min, i);
        }
        if (-128 <= min && max <= TransportMediator.KEYCODE_MEDIA_PAUSE) {
            out.write(Header.ARRAY_INT_BYTE);
            DataOutput2.packInt(out, val.length);
            for (int i2 : val) {
                out.write(i2);
            }
        } else if (-32768 <= min && max <= 32767) {
            out.write(Header.ARRAY_INT_SHORT);
            DataOutput2.packInt(out, val.length);
            for (int i22 : val) {
                out.writeShort(i22);
            }
        } else if (min >= 0) {
            out.write(Header.ARRAY_INT_PACKED);
            DataOutput2.packInt(out, val.length);
            for (int l : val) {
                DataOutput2.packInt(out, l);
            }
        } else {
            out.write(Header.ARRAY_INT);
            DataOutput2.packInt(out, val.length);
            for (int i222 : val) {
                out.writeInt(i222);
            }
        }
    }

    private void serializeDouble(DataOutput out, Object obj) throws IOException {
        double v = ((Double) obj).doubleValue();
        if (v == -1.0d) {
            out.write(Header.DOUBLE_M1);
        } else if (v == 0.0d) {
            out.write(Header.DOUBLE_0);
        } else if (v == 1.0d) {
            out.write(Header.DOUBLE_1);
        } else if (v >= 0.0d && v <= 255.0d && ((double) ((int) v)) == v) {
            out.write(Header.DOUBLE_255);
            out.write((int) v);
        } else if (v >= -32768.0d && v <= 32767.0d && ((double) ((short) ((int) v))) == v) {
            out.write(Header.DOUBLE_SHORT);
            out.writeShort((int) v);
        } else if (v < -2.147483648E9d || v > 2.147483647E9d || ((double) ((int) v)) != v) {
            out.write(Header.DOUBLE);
            out.writeDouble(v);
        } else {
            out.write(Header.DOUBLE_INT);
            out.writeInt((int) v);
        }
    }

    private void serializeFloat(DataOutput out, Object obj) throws IOException {
        float v = ((Float) obj).floatValue();
        if (v == -1.0f) {
            out.write(96);
        } else if (v == 0.0f) {
            out.write(97);
        } else if (v == 1.0f) {
            out.write(98);
        } else if (v >= 0.0f && v <= 255.0f && ((float) ((int) v)) == v) {
            out.write(99);
            out.write((int) v);
        } else if (v < -32768.0f || v > 32767.0f || ((float) ((short) ((int) v))) != v) {
            out.write(Header.FLOAT);
            out.writeFloat(v);
        } else {
            out.write(100);
            out.writeShort((int) v);
        }
    }

    private void serializeShort(DataOutput out, Object obj) throws IOException {
        short val = ((Short) obj).shortValue();
        if (val == (short) -1) {
            out.write(90);
        } else if (val == (short) 0) {
            out.write(91);
        } else if (val == (short) 1) {
            out.write(92);
        } else if (val > (short) 0 && val < (short) 255) {
            out.write(93);
            out.write(val);
        } else if (val >= (short) 0 || val <= (short) -255) {
            out.write(95);
            out.writeShort(val);
        } else {
            out.write(94);
            out.write(-val);
        }
    }

    private void serializerChar(DataOutput out, Object obj) throws IOException {
        char val = ((Character) obj).charValue();
        if (val == '\u0000') {
            out.write(86);
        } else if (val == '\u0001') {
            out.write(87);
        } else if (val <= '\u00ff') {
            out.write(88);
            out.write(val);
        } else {
            out.write(89);
            out.writeChar(((Character) obj).charValue());
        }
    }

    private void serializeByte(DataOutput out, Object obj) throws IOException {
        byte val = ((Byte) obj).byteValue();
        if (val == -1) {
            out.write(82);
        } else if (val == null) {
            out.write(83);
        } else if (val == 1) {
            out.write(84);
        } else {
            out.write(85);
            out.writeByte(val);
        }
    }

    private void serializeLong(DataOutput out, Object obj) throws IOException {
        long val = ((Long) obj).longValue();
        if (val >= -9 && val <= 16) {
            out.write((int) (39 + (9 + val)));
        } else if (val == Long.MIN_VALUE) {
            out.write(65);
        } else if (val == Long.MAX_VALUE) {
            out.write(66);
        } else if (((Math.abs(val) >>> 56) & 255) != 0) {
            out.write(81);
            out.writeLong(val);
        } else {
            int neg = 0;
            if (val < 0) {
                neg = -1;
                val = -val;
            }
            int size = 48;
            while (((val >> size) & 255) == 0) {
                size -= 8;
            }
            out.write((((size / 8) * 2) + 68) + neg);
            while (size >= 0) {
                out.write((int) ((val >> size) & 255));
                size -= 8;
            }
        }
    }

    private void serializeInt(DataOutput out, Object obj) throws IOException {
        int val = ((Integer) obj).intValue();
        switch (val) {
            case ExploreByTouchHelper.INVALID_ID /*-2147483648*/:
                out.write(30);
            case Constants.ERROR_HTTPSTATUS_ERROR /*-9*/:
            case Constants.ERROR_SOCKETTIMEOUT /*-8*/:
            case Constants.ERROR_CONNECTTIMEOUT /*-7*/:
            case Constants.ERROR_UNKNOWN /*-6*/:
            case ErrCode.ERR_UNSUPPORT /*-5*/:
            case ErrCode.ERR_AUTH_DENIED /*-4*/:
            case ErrCode.ERR_SENT_FAILED /*-3*/:
            case ErrCode.ERR_USER_CANCEL /*-2*/:
            case PageFlipEvent.FLIP_TO_PREV /*-1*/:
            case dx.a /*0*/:
            case dx.b /*1*/:
            case dx.c /*2*/:
            case dx.d /*3*/:
            case dx.e /*4*/:
            case dj.f /*5*/:
            case ci.g /*6*/:
            case ci.h /*7*/:
            case h.g /*8*/:
            case h.h /*9*/:
            case h.i /*10*/:
            case R.styleable.StickyListHeadersListView_android_stackFromBottom /*11*/:
            case R.styleable.StickyListHeadersListView_android_scrollingCache /*12*/:
            case R.styleable.StickyListHeadersListView_android_transcriptMode /*13*/:
            case R.styleable.StickyListHeadersListView_android_cacheColorHint /*14*/:
            case R.styleable.StickyListHeadersListView_android_divider /*15*/:
            case TransportMediator.FLAG_KEY_MEDIA_PAUSE /*16*/:
                out.write((val + 9) + 4);
            case AdvancedShareActionProvider.WEIGHT_MAX /*2147483647*/:
                out.write(31);
            default:
                if (((Math.abs(val) >>> 24) & SettingsJsonConstants.SETTINGS_IDENTIFIER_MASK_DEFAULT) != 0) {
                    out.write(38);
                    out.writeInt(val);
                    return;
                }
                int neg = 0;
                if (val < 0) {
                    neg = -1;
                    val = -val;
                }
                int size = 24;
                while ((((long) (val >> size)) & 255) == 0) {
                    size -= 8;
                }
                out.write((((size / 8) * 2) + 33) + neg);
                while (size >= 0) {
                    out.write((int) (((long) (val >> size)) & 255));
                    size -= 8;
                }
        }
    }

    protected void serializeClass(DataOutput out, Class clazz) throws IOException {
        out.writeUTF(clazz.getName());
    }

    private void serializeMap(int header, DataOutput out, Object obj, FastArrayList<Object> objectStack) throws IOException {
        Map l = (Map) obj;
        out.write(header);
        DataOutput2.packInt(out, l.size());
        for (Object o : l.keySet()) {
            serialize(out, o, objectStack);
            serialize(out, l.get(o), objectStack);
        }
    }

    private void serializeCollection(int header, DataOutput out, Object obj, FastArrayList<Object> objectStack) throws IOException {
        Collection<Object> l = (Collection) obj;
        out.write(header);
        DataOutput2.packInt(out, l.size());
        for (Object o : l) {
            serialize(out, o, objectStack);
        }
    }

    private void serializeByteArray(DataOutput out, byte[] b) throws IOException {
        boolean allEqual;
        if (b.length > 0) {
            allEqual = true;
        } else {
            allEqual = $assertionsDisabled;
        }
        for (int i = 1; i < b.length; i++) {
            if (b[i - 1] != b[i]) {
                allEqual = $assertionsDisabled;
                break;
            }
        }
        if (allEqual) {
            out.write(Header.ARRAY_BYTE_ALL_EQUAL);
            DataOutput2.packInt(out, b.length);
            out.write(b[0]);
            return;
        }
        out.write(Header.ARRAY_BYTE);
        DataOutput2.packInt(out, b.length);
        out.write(b);
    }

    static String deserializeString(DataInput buf, int len) throws IOException {
        char[] b = new char[len];
        for (int i = 0; i < len; i++) {
            b[i] = (char) DataInput2.unpackInt(buf);
        }
        return new String(b);
    }

    public Object deserialize(DataInput is, int capacity) throws IOException {
        if (capacity == 0) {
            return null;
        }
        return deserialize(is, null);
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.lang.Object deserialize(java.io.DataInput r13, org.mapdb.SerializerBase.FastArrayList<java.lang.Object> r14) throws java.io.IOException {
        /*
        r12 = this;
        r5 = 0;
        r1 = 0;
        r2 = 0;
        r0 = r13.readUnsignedByte();
        switch(r0) {
            case -1: goto L_0x020b;
            case 0: goto L_0x001c;
            case 1: goto L_0x000b;
            case 2: goto L_0x0029;
            case 3: goto L_0x002c;
            case 4: goto L_0x002f;
            case 5: goto L_0x002f;
            case 6: goto L_0x002f;
            case 7: goto L_0x002f;
            case 8: goto L_0x002f;
            case 9: goto L_0x002f;
            case 10: goto L_0x002f;
            case 11: goto L_0x002f;
            case 12: goto L_0x002f;
            case 13: goto L_0x002f;
            case 14: goto L_0x002f;
            case 15: goto L_0x002f;
            case 16: goto L_0x002f;
            case 17: goto L_0x002f;
            case 18: goto L_0x002f;
            case 19: goto L_0x002f;
            case 20: goto L_0x002f;
            case 21: goto L_0x002f;
            case 22: goto L_0x002f;
            case 23: goto L_0x002f;
            case 24: goto L_0x002f;
            case 25: goto L_0x002f;
            case 26: goto L_0x002f;
            case 27: goto L_0x002f;
            case 28: goto L_0x002f;
            case 29: goto L_0x002f;
            case 30: goto L_0x0038;
            case 31: goto L_0x003f;
            case 32: goto L_0x0057;
            case 33: goto L_0x0057;
            case 34: goto L_0x004d;
            case 35: goto L_0x004d;
            case 36: goto L_0x0047;
            case 37: goto L_0x0047;
            case 38: goto L_0x006b;
            case 39: goto L_0x0074;
            case 40: goto L_0x0074;
            case 41: goto L_0x0074;
            case 42: goto L_0x0074;
            case 43: goto L_0x0074;
            case 44: goto L_0x0074;
            case 45: goto L_0x0074;
            case 46: goto L_0x0074;
            case 47: goto L_0x0074;
            case 48: goto L_0x0074;
            case 49: goto L_0x0074;
            case 50: goto L_0x0074;
            case 51: goto L_0x0074;
            case 52: goto L_0x0074;
            case 53: goto L_0x0074;
            case 54: goto L_0x0074;
            case 55: goto L_0x0074;
            case 56: goto L_0x0074;
            case 57: goto L_0x0074;
            case 58: goto L_0x0074;
            case 59: goto L_0x0074;
            case 60: goto L_0x0074;
            case 61: goto L_0x0074;
            case 62: goto L_0x0074;
            case 63: goto L_0x0074;
            case 64: goto L_0x0074;
            case 65: goto L_0x007e;
            case 66: goto L_0x0085;
            case 67: goto L_0x00df;
            case 68: goto L_0x00df;
            case 69: goto L_0x00d1;
            case 70: goto L_0x00d1;
            case 71: goto L_0x00c3;
            case 72: goto L_0x00c3;
            case 73: goto L_0x00b5;
            case 74: goto L_0x00b5;
            case 75: goto L_0x00a7;
            case 76: goto L_0x00a7;
            case 77: goto L_0x0099;
            case 78: goto L_0x0099;
            case 79: goto L_0x0090;
            case 80: goto L_0x0090;
            case 81: goto L_0x00f9;
            case 82: goto L_0x0103;
            case 83: goto L_0x010a;
            case 84: goto L_0x0111;
            case 85: goto L_0x0118;
            case 86: goto L_0x0122;
            case 87: goto L_0x0129;
            case 88: goto L_0x0130;
            case 89: goto L_0x013b;
            case 90: goto L_0x0145;
            case 91: goto L_0x014c;
            case 92: goto L_0x0153;
            case 93: goto L_0x015a;
            case 94: goto L_0x0165;
            case 95: goto L_0x0171;
            case 96: goto L_0x017b;
            case 97: goto L_0x0183;
            case 98: goto L_0x018a;
            case 99: goto L_0x0192;
            case 100: goto L_0x019d;
            case 101: goto L_0x01a8;
            case 102: goto L_0x01b2;
            case 103: goto L_0x01ba;
            case 104: goto L_0x01c2;
            case 105: goto L_0x01ca;
            case 106: goto L_0x01d5;
            case 107: goto L_0x01e0;
            case 108: goto L_0x01eb;
            case 109: goto L_0x000b;
            case 110: goto L_0x000b;
            case 111: goto L_0x000b;
            case 112: goto L_0x000b;
            case 113: goto L_0x000b;
            case 114: goto L_0x000b;
            case 115: goto L_0x000b;
            case 116: goto L_0x000b;
            case 117: goto L_0x000b;
            case 118: goto L_0x000b;
            case 119: goto L_0x000b;
            case 120: goto L_0x000b;
            case 121: goto L_0x000b;
            case 122: goto L_0x000b;
            case 123: goto L_0x000b;
            case 124: goto L_0x000b;
            case 125: goto L_0x01ff;
            case 126: goto L_0x0203;
            case 127: goto L_0x0203;
            case 128: goto L_0x0203;
            case 129: goto L_0x0203;
            case 130: goto L_0x0203;
            case 131: goto L_0x0203;
            case 132: goto L_0x0203;
            case 133: goto L_0x0203;
            case 134: goto L_0x0203;
            case 135: goto L_0x0203;
            case 136: goto L_0x01f5;
            default: goto L_0x000b;
        };
    L_0x000b:
        if (r5 != 0) goto L_0x0011;
    L_0x000d:
        r5 = r12.deserialize2(r0, r13);
    L_0x0011:
        if (r5 != 0) goto L_0x0016;
    L_0x0013:
        r6 = 1;
        if (r0 != r6) goto L_0x0211;
    L_0x0016:
        if (r14 == 0) goto L_0x001b;
    L_0x0018:
        r14.add(r5);
    L_0x001b:
        return r5;
    L_0x001c:
        r6 = new java.io.IOError;
        r7 = new java.io.IOException;
        r8 = "Zero Header, data corrupted";
        r7.<init>(r8);
        r6.<init>(r7);
        throw r6;
    L_0x0029:
        r5 = java.lang.Boolean.TRUE;
        goto L_0x000b;
    L_0x002c:
        r5 = java.lang.Boolean.FALSE;
        goto L_0x000b;
    L_0x002f:
        r6 = r0 + -4;
        r6 = r6 + -9;
        r5 = java.lang.Integer.valueOf(r6);
        goto L_0x000b;
    L_0x0038:
        r6 = -2147483648; // 0xffffffff80000000 float:-0.0 double:NaN;
        r5 = java.lang.Integer.valueOf(r6);
        goto L_0x000b;
    L_0x003f:
        r6 = 2147483647; // 0x7fffffff float:NaN double:1.060997895E-314;
        r5 = java.lang.Integer.valueOf(r6);
        goto L_0x000b;
    L_0x0047:
        r6 = r13.readUnsignedByte();
        r1 = r6 & 255;
    L_0x004d:
        r6 = r1 << 8;
        r7 = r13.readUnsignedByte();
        r7 = r7 & 255;
        r1 = r6 | r7;
    L_0x0057:
        r6 = r1 << 8;
        r7 = r13.readUnsignedByte();
        r7 = r7 & 255;
        r1 = r6 | r7;
        r6 = r0 % 2;
        if (r6 != 0) goto L_0x0066;
    L_0x0065:
        r1 = -r1;
    L_0x0066:
        r5 = java.lang.Integer.valueOf(r1);
        goto L_0x000b;
    L_0x006b:
        r6 = r13.readInt();
        r5 = java.lang.Integer.valueOf(r6);
        goto L_0x000b;
    L_0x0074:
        r6 = r0 + -39;
        r6 = r6 + -9;
        r6 = (long) r6;
        r5 = java.lang.Long.valueOf(r6);
        goto L_0x000b;
    L_0x007e:
        r6 = -9223372036854775808;
        r5 = java.lang.Long.valueOf(r6);
        goto L_0x000b;
    L_0x0085:
        r6 = 9223372036854775807; // 0x7fffffffffffffff float:NaN double:NaN;
        r5 = java.lang.Long.valueOf(r6);
        goto L_0x000b;
    L_0x0090:
        r6 = r13.readUnsignedByte();
        r6 = (long) r6;
        r8 = 255; // 0xff float:3.57E-43 double:1.26E-321;
        r2 = r6 & r8;
    L_0x0099:
        r6 = 8;
        r6 = r2 << r6;
        r8 = r13.readUnsignedByte();
        r8 = (long) r8;
        r10 = 255; // 0xff float:3.57E-43 double:1.26E-321;
        r8 = r8 & r10;
        r2 = r6 | r8;
    L_0x00a7:
        r6 = 8;
        r6 = r2 << r6;
        r8 = r13.readUnsignedByte();
        r8 = (long) r8;
        r10 = 255; // 0xff float:3.57E-43 double:1.26E-321;
        r8 = r8 & r10;
        r2 = r6 | r8;
    L_0x00b5:
        r6 = 8;
        r6 = r2 << r6;
        r8 = r13.readUnsignedByte();
        r8 = (long) r8;
        r10 = 255; // 0xff float:3.57E-43 double:1.26E-321;
        r8 = r8 & r10;
        r2 = r6 | r8;
    L_0x00c3:
        r6 = 8;
        r6 = r2 << r6;
        r8 = r13.readUnsignedByte();
        r8 = (long) r8;
        r10 = 255; // 0xff float:3.57E-43 double:1.26E-321;
        r8 = r8 & r10;
        r2 = r6 | r8;
    L_0x00d1:
        r6 = 8;
        r6 = r2 << r6;
        r8 = r13.readUnsignedByte();
        r8 = (long) r8;
        r10 = 255; // 0xff float:3.57E-43 double:1.26E-321;
        r8 = r8 & r10;
        r2 = r6 | r8;
    L_0x00df:
        r6 = 8;
        r6 = r2 << r6;
        r8 = r13.readUnsignedByte();
        r8 = (long) r8;
        r10 = 255; // 0xff float:3.57E-43 double:1.26E-321;
        r8 = r8 & r10;
        r2 = r6 | r8;
        r6 = r0 % 2;
        r7 = 1;
        if (r6 != r7) goto L_0x00f3;
    L_0x00f2:
        r2 = -r2;
    L_0x00f3:
        r5 = java.lang.Long.valueOf(r2);
        goto L_0x000b;
    L_0x00f9:
        r6 = r13.readLong();
        r5 = java.lang.Long.valueOf(r6);
        goto L_0x000b;
    L_0x0103:
        r6 = -1;
        r5 = java.lang.Byte.valueOf(r6);
        goto L_0x000b;
    L_0x010a:
        r6 = 0;
        r5 = java.lang.Byte.valueOf(r6);
        goto L_0x000b;
    L_0x0111:
        r6 = 1;
        r5 = java.lang.Byte.valueOf(r6);
        goto L_0x000b;
    L_0x0118:
        r6 = r13.readByte();
        r5 = java.lang.Byte.valueOf(r6);
        goto L_0x000b;
    L_0x0122:
        r6 = 0;
        r5 = java.lang.Character.valueOf(r6);
        goto L_0x000b;
    L_0x0129:
        r6 = 1;
        r5 = java.lang.Character.valueOf(r6);
        goto L_0x000b;
    L_0x0130:
        r6 = r13.readUnsignedByte();
        r6 = (char) r6;
        r5 = java.lang.Character.valueOf(r6);
        goto L_0x000b;
    L_0x013b:
        r6 = r13.readChar();
        r5 = java.lang.Character.valueOf(r6);
        goto L_0x000b;
    L_0x0145:
        r6 = -1;
        r5 = java.lang.Short.valueOf(r6);
        goto L_0x000b;
    L_0x014c:
        r6 = 0;
        r5 = java.lang.Short.valueOf(r6);
        goto L_0x000b;
    L_0x0153:
        r6 = 1;
        r5 = java.lang.Short.valueOf(r6);
        goto L_0x000b;
    L_0x015a:
        r6 = r13.readUnsignedByte();
        r6 = (short) r6;
        r5 = java.lang.Short.valueOf(r6);
        goto L_0x000b;
    L_0x0165:
        r6 = r13.readUnsignedByte();
        r6 = -r6;
        r6 = (short) r6;
        r5 = java.lang.Short.valueOf(r6);
        goto L_0x000b;
    L_0x0171:
        r6 = r13.readShort();
        r5 = java.lang.Short.valueOf(r6);
        goto L_0x000b;
    L_0x017b:
        r6 = -1082130432; // 0xffffffffbf800000 float:-1.0 double:NaN;
        r5 = java.lang.Float.valueOf(r6);
        goto L_0x000b;
    L_0x0183:
        r6 = 0;
        r5 = java.lang.Float.valueOf(r6);
        goto L_0x000b;
    L_0x018a:
        r6 = 1065353216; // 0x3f800000 float:1.0 double:5.263544247E-315;
        r5 = java.lang.Float.valueOf(r6);
        goto L_0x000b;
    L_0x0192:
        r6 = r13.readUnsignedByte();
        r6 = (float) r6;
        r5 = java.lang.Float.valueOf(r6);
        goto L_0x000b;
    L_0x019d:
        r6 = r13.readShort();
        r6 = (float) r6;
        r5 = java.lang.Float.valueOf(r6);
        goto L_0x000b;
    L_0x01a8:
        r6 = r13.readFloat();
        r5 = java.lang.Float.valueOf(r6);
        goto L_0x000b;
    L_0x01b2:
        r6 = -4616189618054758400; // 0xbff0000000000000 float:0.0 double:-1.0;
        r5 = java.lang.Double.valueOf(r6);
        goto L_0x000b;
    L_0x01ba:
        r6 = 0;
        r5 = java.lang.Double.valueOf(r6);
        goto L_0x000b;
    L_0x01c2:
        r6 = 4607182418800017408; // 0x3ff0000000000000 float:0.0 double:1.0;
        r5 = java.lang.Double.valueOf(r6);
        goto L_0x000b;
    L_0x01ca:
        r6 = r13.readUnsignedByte();
        r6 = (double) r6;
        r5 = java.lang.Double.valueOf(r6);
        goto L_0x000b;
    L_0x01d5:
        r6 = r13.readShort();
        r6 = (double) r6;
        r5 = java.lang.Double.valueOf(r6);
        goto L_0x000b;
    L_0x01e0:
        r6 = r13.readInt();
        r6 = (double) r6;
        r5 = java.lang.Double.valueOf(r6);
        goto L_0x000b;
    L_0x01eb:
        r6 = r13.readDouble();
        r5 = java.lang.Double.valueOf(r6);
        goto L_0x000b;
    L_0x01f5:
        r6 = org.mapdb.DataInput2.unpackInt(r13);
        r5 = deserializeString(r13, r6);
        goto L_0x000b;
    L_0x01ff:
        r5 = "";
        goto L_0x000b;
    L_0x0203:
        r6 = r0 + -125;
        r5 = deserializeString(r13, r6);
        goto L_0x000b;
    L_0x020b:
        r6 = new java.io.EOFException;
        r6.<init>();
        throw r6;
    L_0x0211:
        if (r14 != 0) goto L_0x0218;
    L_0x0213:
        r14 = new org.mapdb.SerializerBase$FastArrayList;
        r14.<init>();
    L_0x0218:
        r4 = r14.size;
        r5 = r12.deserialize3(r13, r14, r0);
        r6 = 174; // 0xae float:2.44E-43 double:8.6E-322;
        if (r0 == r6) goto L_0x001b;
    L_0x0222:
        r6 = r14.size;
        if (r6 != r4) goto L_0x001b;
    L_0x0226:
        r14.add(r5);
        goto L_0x001b;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.mapdb.SerializerBase.deserialize(java.io.DataInput, org.mapdb.SerializerBase$FastArrayList):java.lang.Object");
    }

    private Object deserialize3(DataInput is, FastArrayList<Object> objectStack, int head) throws IOException {
        switch (head) {
            case Header.MAPDB /*150*/:
                return deserializeMapDB(is, objectStack);
            case Header.TUPLE2 /*151*/:
                return new Tuple2(this, is, objectStack);
            case Header.TUPLE3 /*152*/:
                return new Tuple3(this, is, objectStack, 0);
            case Header.TUPLE4 /*153*/:
                return new Tuple4(this, is, objectStack);
            case Header.TUPLE5 /*154*/:
                return new Tuple5(this, is, objectStack);
            case Header.TUPLE6 /*155*/:
                return new Tuple6(this, is, objectStack);
            case Header.ARRAY_OBJECT /*158*/:
                return deserializeArrayObject(is, objectStack);
            case Header.ARRAYLIST /*163*/:
                return deserializeArrayList(is, objectStack);
            case Header.TREEMAP /*164*/:
                return deserializeTreeMap(is, objectStack);
            case Header.HASHMAP /*165*/:
                return deserializeHashMap(is, objectStack);
            case Header.LINKEDHASHMAP /*166*/:
                return deserializeLinkedHashMap(is, objectStack);
            case Header.TREESET /*167*/:
                return deserializeTreeSet(is, objectStack);
            case Header.HASHSET /*168*/:
                return deserializeHashSet(is, objectStack);
            case Header.LINKEDHASHSET /*169*/:
                return deserializeLinkedHashSet(is, objectStack);
            case Header.LINKEDLIST /*170*/:
                return deserializeLinkedList(is, objectStack);
            case Header.PROPERTIES /*171*/:
                return deserializeProperties(is, objectStack);
            case Header.OBJECT_STACK /*174*/:
                return objectStack.data[DataInput2.unpackInt(is)];
            case Header.MA_LONG /*176*/:
                return new Long(getEngine(), DataInput2.unpackLong(is));
            case Header.MA_INT /*177*/:
                return new Integer(getEngine(), DataInput2.unpackLong(is));
            case Header.MA_BOOL /*178*/:
                return new Boolean(getEngine(), DataInput2.unpackLong(is));
            case Header.MA_STRING /*179*/:
                return new String(getEngine(), DataInput2.unpackLong(is));
            case Header.MA_VAR /*180*/:
                return new Var(getEngine(), this, is, objectStack);
            default:
                return deserializeUnknownHeader(is, head, objectStack);
        }
    }

    private Object deserialize2(int head, DataInput is) throws IOException {
        int size;
        Object ret;
        int i;
        switch (head) {
            case Header.ARRAY_BYTE /*109*/:
                return deserializeArrayByte(is);
            case Header.ARRAY_BYTE_ALL_EQUAL /*110*/:
                Object b = new byte[DataInput2.unpackInt(is)];
                Arrays.fill(b, is.readByte());
                return b;
            case Header.ARRAY_BOOLEAN /*111*/:
                return readBooleanArray(DataInput2.unpackInt(is), is);
            case Header.ARRAY_SHORT /*112*/:
                size = DataInput2.unpackInt(is);
                ret = new short[size];
                for (i = 0; i < size; i++) {
                    ((short[]) ret)[i] = is.readShort();
                }
                return ret;
            case Header.ARRAY_CHAR /*113*/:
                size = DataInput2.unpackInt(is);
                ret = new char[size];
                for (i = 0; i < size; i++) {
                    ((char[]) ret)[i] = is.readChar();
                }
                return ret;
            case Header.ARRAY_FLOAT /*114*/:
                size = DataInput2.unpackInt(is);
                ret = new float[size];
                for (i = 0; i < size; i++) {
                    ((float[]) ret)[i] = is.readFloat();
                }
                return ret;
            case Header.ARRAY_DOUBLE /*115*/:
                size = DataInput2.unpackInt(is);
                ret = new double[size];
                for (i = 0; i < size; i++) {
                    ((double[]) ret)[i] = is.readDouble();
                }
                return ret;
            case Header.ARRAY_INT_BYTE /*116*/:
                size = DataInput2.unpackInt(is);
                ret = new int[size];
                for (i = 0; i < size; i++) {
                    ((int[]) ret)[i] = is.readByte();
                }
                return ret;
            case Header.ARRAY_INT_SHORT /*117*/:
                size = DataInput2.unpackInt(is);
                ret = new int[size];
                for (i = 0; i < size; i++) {
                    ((int[]) ret)[i] = is.readShort();
                }
                return ret;
            case Header.ARRAY_INT_PACKED /*118*/:
                size = DataInput2.unpackInt(is);
                ret = new int[size];
                for (i = 0; i < size; i++) {
                    ((int[]) ret)[i] = DataInput2.unpackInt(is);
                }
                return ret;
            case Header.ARRAY_INT /*119*/:
                size = DataInput2.unpackInt(is);
                ret = new int[size];
                for (i = 0; i < size; i++) {
                    ((int[]) ret)[i] = is.readInt();
                }
                return ret;
            case Header.ARRAY_LONG_BYTE /*120*/:
                size = DataInput2.unpackInt(is);
                ret = new long[size];
                for (i = 0; i < size; i++) {
                    ((long[]) ret)[i] = (long) is.readByte();
                }
                return ret;
            case Header.ARRAY_LONG_SHORT /*121*/:
                size = DataInput2.unpackInt(is);
                ret = new long[size];
                for (i = 0; i < size; i++) {
                    ((long[]) ret)[i] = (long) is.readShort();
                }
                return ret;
            case Header.ARRAY_LONG_PACKED /*122*/:
                size = DataInput2.unpackInt(is);
                ret = new long[size];
                for (i = 0; i < size; i++) {
                    ((long[]) ret)[i] = DataInput2.unpackLong(is);
                }
                return ret;
            case Header.ARRAY_LONG_INT /*123*/:
                size = DataInput2.unpackInt(is);
                ret = new long[size];
                for (i = 0; i < size; i++) {
                    ((long[]) ret)[i] = (long) is.readInt();
                }
                return ret;
            case Header.ARRAY_LONG /*124*/:
                size = DataInput2.unpackInt(is);
                ret = new long[size];
                for (i = 0; i < size; i++) {
                    ((long[]) ret)[i] = is.readLong();
                }
                return ret;
            case Header.BIGDECIMAL /*137*/:
                return new BigDecimal(new BigInteger(deserializeArrayByte(is)), DataInput2.unpackInt(is));
            case Header.BIGINTEGER /*138*/:
                return new BigInteger(deserializeArrayByte(is));
            case Header.CLASS /*139*/:
                return deserializeClass(is);
            case Header.DATE /*140*/:
                return new Date(is.readLong());
            case Header.FUN_HI /*141*/:
                return Fun.HI;
            case Header.UUID /*142*/:
                return new UUID(is.readLong(), is.readLong());
            case Header.ARRAY_OBJECT_PACKED_LONG /*159*/:
                return deserializeArrayObjectPackedLong(is);
            case Header.ARRAYLIST_PACKED_LONG /*160*/:
                return deserializeArrayListPackedLong(is);
            case Header.ARRAY_OBJECT_ALL_NULL /*161*/:
                return deserializeArrayObjectAllNull(is);
            case Header.ARRAY_OBJECT_NO_REFS /*162*/:
                return deserializeArrayObjectNoRefs(is);
            case Header.JAVA_SERIALIZATION /*172*/:
                throw new AssertionError("Wrong header, data were probably serialized with java.lang.ObjectOutputStream, not with MapDB serialization");
            default:
                return null;
        }
    }

    public static void assertSerializable(Object o) {
        if (o != null && !(o instanceof Serializable) && !singletons.all.containsKey(o)) {
            throw new IllegalArgumentException("Not serializable: " + o.getClass());
        }
    }

    protected Object deserializeMapDB(DataInput is, FastArrayList<Object> objectStack) throws IOException {
        int head = DataInput2.unpackInt(is);
        Object singleton = singletons.reverse.get((long) head);
        if (singleton != null) {
            return singleton;
        }
        if ($assertionsDisabled || objectStack != null) {
            switch (head) {
                case ci.h /*7*/:
                    return new Tuple2KeySerializer(this, is, objectStack, 0);
                case h.g /*8*/:
                    return new Tuple3KeySerializer(this, is, objectStack);
                case h.h /*9*/:
                    return new Tuple4KeySerializer(this, is, objectStack);
                case R.styleable.StickyListHeadersListView_android_scrollingCache /*12*/:
                    return this;
                case R.styleable.StickyListHeadersListView_android_divider /*15*/:
                    return new BasicKeySerializer(this, is, objectStack);
                case R.styleable.StickyListHeadersListView_stickyListHeadersListViewStyle /*22*/:
                    return new Tuple2Comparator(this, is, objectStack);
                case R.styleable.StickyListHeadersListView_hasStickyHeaders /*23*/:
                    return new Tuple3Comparator(this, is, objectStack, 0);
                case R.styleable.StickyListHeadersListView_isDrawingListUnderStickyHeader /*24*/:
                    return new Tuple4Comparator(this, is, objectStack);
                case QQShare.QQ_SHARE_TITLE_MAX_LENGTH /*45*/:
                    return new ArrayComparator(this, is, objectStack);
                case HeaderMapDB.SERIALIZER_COMPRESSION_WRAPPER /*47*/:
                    return new CompressionWrapper(this, is, objectStack);
                case HeaderMapDB.TUPLE5_COMPARATOR /*51*/:
                    return new Tuple5Comparator(this, is, objectStack);
                case HeaderMapDB.TUPLE6_COMPARATOR /*52*/:
                    return new Tuple6Comparator(this, is, objectStack);
                case HeaderMapDB.SERIALIZER_KEY_TUPLE5 /*55*/:
                    return new Tuple5KeySerializer(this, is, objectStack);
                case HeaderMapDB.SERIALIZER_KEY_TUPLE6 /*56*/:
                    return new Tuple6KeySerializer(this, is, objectStack);
                default:
                    throw new IOError(new IOException("Unknown header byte, data corrupted"));
            }
        }
        throw new AssertionError();
    }

    protected Engine getEngine() {
        throw new UnsupportedOperationException();
    }

    protected Class deserializeClass(DataInput is) throws IOException {
        return SerializerPojo.classForName(is.readUTF());
    }

    private byte[] deserializeArrayByte(DataInput is) throws IOException {
        byte[] bb = new byte[DataInput2.unpackInt(is)];
        is.readFully(bb);
        return bb;
    }

    private Object[] deserializeArrayObject(DataInput is, FastArrayList<Object> objectStack) throws IOException {
        int size = DataInput2.unpackInt(is);
        Object[] s = (Object[]) Array.newInstance(deserializeClass(is), size);
        objectStack.add(s);
        for (int i = 0; i < size; i++) {
            s[i] = deserialize(is, (FastArrayList) objectStack);
        }
        return s;
    }

    private Object[] deserializeArrayObjectNoRefs(DataInput is) throws IOException {
        int size = DataInput2.unpackInt(is);
        Object[] s = (Object[]) Array.newInstance(deserializeClass(is), size);
        for (int i = 0; i < size; i++) {
            s[i] = deserialize(is, null);
        }
        return s;
    }

    private Object[] deserializeArrayObjectAllNull(DataInput is) throws IOException {
        return (Object[]) Array.newInstance(deserializeClass(is), DataInput2.unpackInt(is));
    }

    private Object[] deserializeArrayObjectPackedLong(DataInput is) throws IOException {
        int size = is.readUnsignedByte();
        Object[] s = new Object[size];
        for (int i = 0; i < size; i++) {
            long l = DataInput2.unpackLong(is);
            if (l == 0) {
                s[i] = null;
            } else {
                s[i] = Long.valueOf(l - 1);
            }
        }
        return s;
    }

    private ArrayList<Object> deserializeArrayList(DataInput is, FastArrayList<Object> objectStack) throws IOException {
        int size = DataInput2.unpackInt(is);
        ArrayList<Object> s = new ArrayList(size);
        objectStack.add(s);
        for (int i = 0; i < size; i++) {
            s.add(deserialize(is, (FastArrayList) objectStack));
        }
        return s;
    }

    private ArrayList<Object> deserializeArrayListPackedLong(DataInput is) throws IOException {
        int size = is.readUnsignedByte();
        if (size < 0) {
            throw new EOFException();
        }
        ArrayList<Object> s = new ArrayList(size);
        for (int i = 0; i < size; i++) {
            long l = DataInput2.unpackLong(is);
            if (l == 0) {
                s.add(null);
            } else {
                s.add(Long.valueOf(l - 1));
            }
        }
        return s;
    }

    private LinkedList deserializeLinkedList(DataInput is, FastArrayList<Object> objectStack) throws IOException {
        int size = DataInput2.unpackInt(is);
        LinkedList s = new LinkedList();
        objectStack.add(s);
        for (int i = 0; i < size; i++) {
            s.add(deserialize(is, (FastArrayList) objectStack));
        }
        return s;
    }

    private HashSet<Object> deserializeHashSet(DataInput is, FastArrayList<Object> objectStack) throws IOException {
        int size = DataInput2.unpackInt(is);
        HashSet<Object> s = new HashSet(size);
        objectStack.add(s);
        for (int i = 0; i < size; i++) {
            s.add(deserialize(is, (FastArrayList) objectStack));
        }
        return s;
    }

    private LinkedHashSet<Object> deserializeLinkedHashSet(DataInput is, FastArrayList<Object> objectStack) throws IOException {
        int size = DataInput2.unpackInt(is);
        LinkedHashSet<Object> s = new LinkedHashSet(size);
        objectStack.add(s);
        for (int i = 0; i < size; i++) {
            s.add(deserialize(is, (FastArrayList) objectStack));
        }
        return s;
    }

    private TreeSet<Object> deserializeTreeSet(DataInput is, FastArrayList<Object> objectStack) throws IOException {
        int size = DataInput2.unpackInt(is);
        TreeSet<Object> s = new TreeSet();
        objectStack.add(s);
        Comparator comparator = (Comparator) deserialize(is, (FastArrayList) objectStack);
        if (comparator != null) {
            s = new TreeSet(comparator);
        }
        for (int i = 0; i < size; i++) {
            s.add(deserialize(is, (FastArrayList) objectStack));
        }
        return s;
    }

    private TreeMap<Object, Object> deserializeTreeMap(DataInput is, FastArrayList<Object> objectStack) throws IOException {
        int size = DataInput2.unpackInt(is);
        TreeMap<Object, Object> s = new TreeMap();
        objectStack.add(s);
        Comparator comparator = (Comparator) deserialize(is, (FastArrayList) objectStack);
        if (comparator != null) {
            s = new TreeMap(comparator);
        }
        for (int i = 0; i < size; i++) {
            s.put(deserialize(is, (FastArrayList) objectStack), deserialize(is, (FastArrayList) objectStack));
        }
        return s;
    }

    private HashMap<Object, Object> deserializeHashMap(DataInput is, FastArrayList<Object> objectStack) throws IOException {
        int size = DataInput2.unpackInt(is);
        HashMap<Object, Object> s = new HashMap(size);
        objectStack.add(s);
        for (int i = 0; i < size; i++) {
            s.put(deserialize(is, (FastArrayList) objectStack), deserialize(is, (FastArrayList) objectStack));
        }
        return s;
    }

    private LinkedHashMap<Object, Object> deserializeLinkedHashMap(DataInput is, FastArrayList<Object> objectStack) throws IOException {
        int size = DataInput2.unpackInt(is);
        LinkedHashMap<Object, Object> s = new LinkedHashMap(size);
        objectStack.add(s);
        for (int i = 0; i < size; i++) {
            s.put(deserialize(is, (FastArrayList) objectStack), deserialize(is, (FastArrayList) objectStack));
        }
        return s;
    }

    private Properties deserializeProperties(DataInput is, FastArrayList<Object> objectStack) throws IOException {
        int size = DataInput2.unpackInt(is);
        Properties s = new Properties();
        objectStack.add(s);
        for (int i = 0; i < size; i++) {
            s.put(deserialize(is, (FastArrayList) objectStack), deserialize(is, (FastArrayList) objectStack));
        }
        return s;
    }

    protected void serializeUnknownObject(DataOutput out, Object obj, FastArrayList<Object> fastArrayList) throws IOException {
        throw new AssertionError("Could not serialize unknown object: " + obj.getClass().getName());
    }

    protected Object deserializeUnknownHeader(DataInput is, int head, FastArrayList<Object> fastArrayList) throws IOException {
        throw new AssertionError("Unknown serialization header: " + head);
    }

    protected static byte[] booleanToByteArray(boolean[] bool) {
        int i;
        boolean isFlushWith8;
        int i2 = 1;
        int boolLen = bool.length;
        int mod8 = boolLen % 8;
        int i3 = boolLen / 8;
        if (boolLen % 8 == 0) {
            i = 0;
        } else {
            i = 1;
        }
        byte[] boolBytes = new byte[(i + i3)];
        if (mod8 == 0) {
            isFlushWith8 = true;
        } else {
            isFlushWith8 = $assertionsDisabled;
        }
        int length = isFlushWith8 ? boolBytes.length : boolBytes.length - 1;
        int boolByteIndex = 0;
        int x = 0;
        while (boolByteIndex < length) {
            int x2 = x + 1;
            if (bool[x]) {
                i = 1;
            } else {
                i = 0;
            }
            x = x2 + 1;
            i3 = (i << 0) | ((bool[x2] ? 1 : 0) << 1);
            x2 = x + 1;
            i3 |= (bool[x] ? 1 : 0) << 2;
            x = x2 + 1;
            i3 |= (bool[x2] ? 1 : 0) << 3;
            x2 = x + 1;
            i3 |= (bool[x] ? 1 : 0) << 4;
            x = x2 + 1;
            i3 |= (bool[x2] ? 1 : 0) << 5;
            x2 = x + 1;
            i3 |= (bool[x] ? 1 : 0) << 6;
            x = x2 + 1;
            int boolByteIndex2 = boolByteIndex + 1;
            boolBytes[boolByteIndex] = (byte) (((bool[x2] ? 1 : 0) << 7) | i3);
            boolByteIndex = boolByteIndex2;
        }
        if (isFlushWith8) {
            boolByteIndex2 = boolByteIndex;
            x2 = x;
        } else {
            byte b = (byte) 0;
            switch (mod8) {
                case dx.b /*1*/:
                    x2 = x + 1;
                    if (!bool[x]) {
                        i2 = 0;
                    }
                    b = (byte) ((i2 << 0) | 0);
                    break;
                case dx.c /*2*/:
                    x2 = x + 1;
                    if (bool[x]) {
                        i = 1;
                    } else {
                        i = 0;
                    }
                    i <<= 0;
                    x = x2 + 1;
                    if (!bool[x2]) {
                        i2 = 0;
                    }
                    b = (byte) ((i | (i2 << 1)) | 0);
                    x2 = x;
                    break;
                case dx.d /*3*/:
                    x2 = x + 1;
                    if (bool[x]) {
                        i = 1;
                    } else {
                        i = 0;
                    }
                    x = x2 + 1;
                    i = ((bool[x2] ? 1 : 0) << 1) | (i << 0);
                    x2 = x + 1;
                    if (!bool[x]) {
                        i2 = 0;
                    }
                    b = (byte) ((i | (i2 << 2)) | 0);
                    break;
                case dx.e /*4*/:
                    x2 = x + 1;
                    if (bool[x]) {
                        i = 1;
                    } else {
                        i = 0;
                    }
                    x = x2 + 1;
                    i3 = (i << 0) | ((bool[x2] ? 1 : 0) << 1);
                    x2 = x + 1;
                    i = ((bool[x] ? 1 : 0) << 2) | i3;
                    x = x2 + 1;
                    if (!bool[x2]) {
                        i2 = 0;
                    }
                    b = (byte) ((i | (i2 << 3)) | 0);
                    x2 = x;
                    break;
                case dj.f /*5*/:
                    x2 = x + 1;
                    i3 = (bool[x] ? 1 : 0) << 0;
                    x = x2 + 1;
                    i3 |= (bool[x2] ? 1 : 0) << 1;
                    x2 = x + 1;
                    i3 |= (bool[x] ? 1 : 0) << 2;
                    x = x2 + 1;
                    i = ((bool[x2] ? 1 : 0) << 3) | i3;
                    x2 = x + 1;
                    if (!bool[x]) {
                        i2 = 0;
                    }
                    b = (byte) ((i | (i2 << 4)) | 0);
                    break;
                case ci.g /*6*/:
                    x2 = x + 1;
                    i3 = (bool[x] ? 1 : 0) << 0;
                    x = x2 + 1;
                    i3 |= (bool[x2] ? 1 : 0) << 1;
                    x2 = x + 1;
                    i3 |= (bool[x] ? 1 : 0) << 2;
                    x = x2 + 1;
                    i3 |= (bool[x2] ? 1 : 0) << 3;
                    x2 = x + 1;
                    i = ((bool[x] ? 1 : 0) << 4) | i3;
                    x = x2 + 1;
                    if (!bool[x2]) {
                        i2 = 0;
                    }
                    b = (byte) ((i | (i2 << 5)) | 0);
                    x2 = x;
                    break;
                case ci.h /*7*/:
                    x2 = x + 1;
                    i3 = (bool[x] ? 1 : 0) << 0;
                    x = x2 + 1;
                    i3 |= (bool[x2] ? 1 : 0) << 1;
                    x2 = x + 1;
                    i3 |= (bool[x] ? 1 : 0) << 2;
                    x = x2 + 1;
                    i3 |= (bool[x2] ? 1 : 0) << 3;
                    x2 = x + 1;
                    i3 |= (bool[x] ? 1 : 0) << 4;
                    x = x2 + 1;
                    i = ((bool[x2] ? 1 : 0) << 5) | i3;
                    x2 = x + 1;
                    if (!bool[x]) {
                        i2 = 0;
                    }
                    b = (byte) ((i | (i2 << 6)) | 0);
                    break;
                case h.g /*8*/:
                    x2 = x + 1;
                    i3 = (bool[x] ? 1 : 0) << 0;
                    x = x2 + 1;
                    i3 |= (bool[x2] ? 1 : 0) << 1;
                    x2 = x + 1;
                    i3 |= (bool[x] ? 1 : 0) << 2;
                    x = x2 + 1;
                    i3 |= (bool[x2] ? 1 : 0) << 3;
                    x2 = x + 1;
                    i3 |= (bool[x] ? 1 : 0) << 4;
                    x = x2 + 1;
                    i3 |= (bool[x2] ? 1 : 0) << 5;
                    x2 = x + 1;
                    i = ((bool[x] ? 1 : 0) << 6) | i3;
                    x = x2 + 1;
                    if (!bool[x2]) {
                        i2 = 0;
                    }
                    b = (byte) ((i | (i2 << 7)) | 0);
                    x2 = x;
                    break;
                default:
                    x2 = x;
                    break;
            }
            boolByteIndex2 = boolByteIndex + 1;
            boolBytes[boolByteIndex] = b;
        }
        return boolBytes;
    }

    protected static boolean[] readBooleanArray(int numBools, DataInput is) throws IOException {
        byte[] boolBytes = new byte[((numBools / 8) + (numBools % 8 == 0 ? 0 : 1))];
        is.readFully(boolBytes);
        boolean[] tmp = new boolean[(boolBytes.length * 8)];
        int len = boolBytes.length;
        int boolIndex = 0;
        byte[] arr$ = boolBytes;
        int len$ = arr$.length;
        int i$ = 0;
        while (i$ < len$) {
            byte boolByte = arr$[i$];
            int y = 0;
            int boolIndex2 = boolIndex;
            while (y < 8) {
                boolIndex = boolIndex2 + 1;
                tmp[boolIndex2] = ((1 << y) & boolByte) != 0 ? true : $assertionsDisabled;
                y++;
                boolIndex2 = boolIndex;
            }
            i$++;
            boolIndex = boolIndex2;
        }
        boolean[] finalBoolArray = new boolean[numBools];
        System.arraycopy(tmp, 0, finalBoolArray, 0, numBools);
        return finalBoolArray;
    }

    public int fixedSize() {
        return -1;
    }
}
