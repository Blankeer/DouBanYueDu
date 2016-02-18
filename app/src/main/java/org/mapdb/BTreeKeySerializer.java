package org.mapdb;

import io.fabric.sdk.android.services.network.UrlUtils;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.Comparator;
import org.mapdb.Fun.Tuple2;
import org.mapdb.Fun.Tuple3;
import org.mapdb.Fun.Tuple4;
import org.mapdb.Fun.Tuple5;
import org.mapdb.Fun.Tuple6;

public abstract class BTreeKeySerializer<K> {
    public static final BTreeKeySerializer BASIC;
    public static final BTreeKeySerializer<String> STRING;
    public static final Tuple2KeySerializer TUPLE2;
    public static final Tuple3KeySerializer TUPLE3;
    public static final Tuple4KeySerializer TUPLE4;
    public static final BTreeKeySerializer<Integer> ZERO_OR_POSITIVE_INT;
    public static final BTreeKeySerializer<Long> ZERO_OR_POSITIVE_LONG;

    public static final class BasicKeySerializer extends BTreeKeySerializer<Object> implements Serializable {
        private static final long serialVersionUID = 1654710710946309279L;
        protected final Serializer defaultSerializer;

        public BasicKeySerializer(Serializer defaultSerializer) {
            this.defaultSerializer = defaultSerializer;
        }

        protected BasicKeySerializer(SerializerBase serializerBase, DataInput is, FastArrayList<Object> objectStack) throws IOException {
            objectStack.add(this);
            this.defaultSerializer = (Serializer) serializerBase.deserialize(is, (FastArrayList) objectStack);
        }

        public void serialize(DataOutput out, int start, int end, Object[] keys) throws IOException {
            for (int i = start; i < end; i++) {
                this.defaultSerializer.serialize(out, keys[i]);
            }
        }

        public Object[] deserialize(DataInput in, int start, int end, int size) throws IOException {
            Object[] ret = new Object[size];
            for (int i = start; i < end; i++) {
                ret[i] = this.defaultSerializer.deserialize(in, -1);
            }
            return ret;
        }

        public Comparator<Object> getComparator() {
            return null;
        }
    }

    public static final class Tuple2KeySerializer<A, B> extends BTreeKeySerializer<Tuple2<A, B>> implements Serializable {
        static final /* synthetic */ boolean $assertionsDisabled;
        private static final long serialVersionUID = 2183804367032891772L;
        protected final Comparator<A> aComparator;
        protected final Serializer<A> aSerializer;
        protected final Serializer<B> bSerializer;

        static {
            $assertionsDisabled = !BTreeKeySerializer.class.desiredAssertionStatus() ? true : $assertionsDisabled;
        }

        public Tuple2KeySerializer(Comparator<A> aComparator, Serializer<A> aSerializer, Serializer<B> bSerializer) {
            this.aComparator = aComparator;
            this.aSerializer = aSerializer;
            this.bSerializer = bSerializer;
        }

        Tuple2KeySerializer(SerializerBase serializerBase, DataInput is, FastArrayList<Object> objectStack, int extra) throws IOException {
            objectStack.add(this);
            this.aComparator = (Comparator) serializerBase.deserialize(is, (FastArrayList) objectStack);
            this.aSerializer = (Serializer) serializerBase.deserialize(is, (FastArrayList) objectStack);
            this.bSerializer = (Serializer) serializerBase.deserialize(is, (FastArrayList) objectStack);
        }

        public void serialize(DataOutput out, int start, int end, Object[] keys) throws IOException {
            int acount = 0;
            int i = start;
            while (i < end) {
                Tuple2<A, B> t = keys[i];
                if (acount == 0) {
                    this.aSerializer.serialize(out, t.a);
                    acount = 1;
                    while (i + acount < end && this.aComparator.compare(t.a, ((Tuple2) keys[i + acount]).a) == 0) {
                        acount++;
                    }
                    DataOutput2.packInt(out, acount);
                }
                this.bSerializer.serialize(out, t.b);
                acount--;
                i++;
            }
        }

        public Object[] deserialize(DataInput in, int start, int end, int size) throws IOException {
            Object[] ret = new Object[size];
            A a = null;
            int acount = 0;
            for (int i = start; i < end; i++) {
                if (acount == 0) {
                    a = this.aSerializer.deserialize(in, -1);
                    acount = DataInput2.unpackInt(in);
                }
                ret[i] = Fun.t2(a, this.bSerializer.deserialize(in, -1));
                acount--;
            }
            if ($assertionsDisabled || acount == 0) {
                return ret;
            }
            throw new AssertionError();
        }

        public Comparator<Tuple2<A, B>> getComparator() {
            return BTreeMap.COMPARABLE_COMPARATOR;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return $assertionsDisabled;
            }
            Tuple2KeySerializer t = (Tuple2KeySerializer) o;
            if (Fun.eq(this.aComparator, t.aComparator) && Fun.eq(this.aSerializer, t.aSerializer) && Fun.eq(this.bSerializer, t.bSerializer)) {
                return true;
            }
            return $assertionsDisabled;
        }

        public int hashCode() {
            int result;
            int hashCode;
            int i = 0;
            if (this.aComparator != null) {
                result = this.aComparator.hashCode();
            } else {
                result = 0;
            }
            int i2 = result * 31;
            if (this.aSerializer != null) {
                hashCode = this.aSerializer.hashCode();
            } else {
                hashCode = 0;
            }
            hashCode = (i2 + hashCode) * 31;
            if (this.bSerializer != null) {
                i = this.bSerializer.hashCode();
            }
            return hashCode + i;
        }
    }

    public static class Tuple3KeySerializer<A, B, C> extends BTreeKeySerializer<Tuple3<A, B, C>> implements Serializable {
        static final /* synthetic */ boolean $assertionsDisabled;
        private static final long serialVersionUID = 2932442956138713885L;
        protected final Comparator<A> aComparator;
        protected final Serializer<A> aSerializer;
        protected final Comparator<B> bComparator;
        protected final Serializer<B> bSerializer;
        protected final Serializer<C> cSerializer;

        static {
            $assertionsDisabled = !BTreeKeySerializer.class.desiredAssertionStatus() ? true : $assertionsDisabled;
        }

        public Tuple3KeySerializer(Comparator<A> aComparator, Comparator<B> bComparator, Serializer<A> aSerializer, Serializer<B> bSerializer, Serializer<C> cSerializer) {
            this.aComparator = aComparator;
            this.bComparator = bComparator;
            this.aSerializer = aSerializer;
            this.bSerializer = bSerializer;
            this.cSerializer = cSerializer;
        }

        Tuple3KeySerializer(SerializerBase serializerBase, DataInput is, FastArrayList<Object> objectStack) throws IOException {
            objectStack.add(this);
            this.aComparator = (Comparator) serializerBase.deserialize(is, (FastArrayList) objectStack);
            this.bComparator = (Comparator) serializerBase.deserialize(is, (FastArrayList) objectStack);
            this.aSerializer = (Serializer) serializerBase.deserialize(is, (FastArrayList) objectStack);
            this.bSerializer = (Serializer) serializerBase.deserialize(is, (FastArrayList) objectStack);
            this.cSerializer = (Serializer) serializerBase.deserialize(is, (FastArrayList) objectStack);
        }

        public void serialize(DataOutput out, int start, int end, Object[] keys) throws IOException {
            int acount = 0;
            int bcount = 0;
            int i = start;
            while (i < end) {
                Tuple3<A, B, C> t = keys[i];
                if (acount == 0) {
                    this.aSerializer.serialize(out, t.a);
                    acount = 1;
                    while (i + acount < end && this.aComparator.compare(t.a, ((Tuple3) keys[i + acount]).a) == 0) {
                        acount++;
                    }
                    DataOutput2.packInt(out, acount);
                }
                if (bcount == 0) {
                    this.bSerializer.serialize(out, t.b);
                    bcount = 1;
                    while (i + bcount < end && this.bComparator.compare(t.b, ((Tuple3) keys[i + bcount]).b) == 0) {
                        bcount++;
                    }
                    DataOutput2.packInt(out, bcount);
                }
                this.cSerializer.serialize(out, t.c);
                acount--;
                bcount--;
                i++;
            }
        }

        public Object[] deserialize(DataInput in, int start, int end, int size) throws IOException {
            Object[] ret = new Object[size];
            A a = null;
            int acount = 0;
            B b = null;
            int bcount = 0;
            for (int i = start; i < end; i++) {
                if (acount == 0) {
                    a = this.aSerializer.deserialize(in, -1);
                    acount = DataInput2.unpackInt(in);
                }
                if (bcount == 0) {
                    b = this.bSerializer.deserialize(in, -1);
                    bcount = DataInput2.unpackInt(in);
                }
                ret[i] = Fun.t3(a, b, this.cSerializer.deserialize(in, -1));
                acount--;
                bcount--;
            }
            if (!$assertionsDisabled && acount != 0) {
                throw new AssertionError();
            } else if ($assertionsDisabled || bcount == 0) {
                return ret;
            } else {
                throw new AssertionError();
            }
        }

        public Comparator<Tuple3<A, B, C>> getComparator() {
            return BTreeMap.COMPARABLE_COMPARATOR;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return $assertionsDisabled;
            }
            Tuple3KeySerializer t = (Tuple3KeySerializer) o;
            if (Fun.eq(this.aComparator, t.aComparator) && Fun.eq(this.bComparator, t.bComparator) && Fun.eq(this.aSerializer, t.aSerializer) && Fun.eq(this.bSerializer, t.bSerializer) && Fun.eq(this.cSerializer, t.cSerializer)) {
                return true;
            }
            return $assertionsDisabled;
        }

        public int hashCode() {
            int result;
            int hashCode;
            int i = 0;
            if (this.aComparator != null) {
                result = this.aComparator.hashCode();
            } else {
                result = 0;
            }
            int i2 = result * 31;
            if (this.bComparator != null) {
                hashCode = this.bComparator.hashCode();
            } else {
                hashCode = 0;
            }
            i2 = (i2 + hashCode) * 31;
            if (this.aSerializer != null) {
                hashCode = this.aSerializer.hashCode();
            } else {
                hashCode = 0;
            }
            i2 = (i2 + hashCode) * 31;
            if (this.bSerializer != null) {
                hashCode = this.bSerializer.hashCode();
            } else {
                hashCode = 0;
            }
            hashCode = (i2 + hashCode) * 31;
            if (this.cSerializer != null) {
                i = this.cSerializer.hashCode();
            }
            return hashCode + i;
        }
    }

    public static class Tuple4KeySerializer<A, B, C, D> extends BTreeKeySerializer<Tuple4<A, B, C, D>> implements Serializable {
        static final /* synthetic */ boolean $assertionsDisabled;
        private static final long serialVersionUID = -1835761249723528530L;
        protected final Comparator<A> aComparator;
        protected final Serializer<A> aSerializer;
        protected final Comparator<B> bComparator;
        protected final Serializer<B> bSerializer;
        protected final Comparator<C> cComparator;
        protected final Serializer<C> cSerializer;
        protected final Serializer<D> dSerializer;

        static {
            $assertionsDisabled = !BTreeKeySerializer.class.desiredAssertionStatus() ? true : $assertionsDisabled;
        }

        public Tuple4KeySerializer(Comparator<A> aComparator, Comparator<B> bComparator, Comparator<C> cComparator, Serializer<A> aSerializer, Serializer<B> bSerializer, Serializer<C> cSerializer, Serializer<D> dSerializer) {
            this.aComparator = aComparator;
            this.bComparator = bComparator;
            this.cComparator = cComparator;
            this.aSerializer = aSerializer;
            this.bSerializer = bSerializer;
            this.cSerializer = cSerializer;
            this.dSerializer = dSerializer;
        }

        Tuple4KeySerializer(SerializerBase serializerBase, DataInput is, FastArrayList<Object> objectStack) throws IOException {
            objectStack.add(this);
            this.aComparator = (Comparator) serializerBase.deserialize(is, (FastArrayList) objectStack);
            this.bComparator = (Comparator) serializerBase.deserialize(is, (FastArrayList) objectStack);
            this.cComparator = (Comparator) serializerBase.deserialize(is, (FastArrayList) objectStack);
            this.aSerializer = (Serializer) serializerBase.deserialize(is, (FastArrayList) objectStack);
            this.bSerializer = (Serializer) serializerBase.deserialize(is, (FastArrayList) objectStack);
            this.cSerializer = (Serializer) serializerBase.deserialize(is, (FastArrayList) objectStack);
            this.dSerializer = (Serializer) serializerBase.deserialize(is, (FastArrayList) objectStack);
        }

        public void serialize(DataOutput out, int start, int end, Object[] keys) throws IOException {
            int acount = 0;
            int bcount = 0;
            int ccount = 0;
            int i = start;
            while (i < end) {
                Tuple4<A, B, C, D> t = keys[i];
                if (acount == 0) {
                    this.aSerializer.serialize(out, t.a);
                    acount = 1;
                    while (i + acount < end && this.aComparator.compare(t.a, ((Tuple4) keys[i + acount]).a) == 0) {
                        acount++;
                    }
                    DataOutput2.packInt(out, acount);
                }
                if (bcount == 0) {
                    this.bSerializer.serialize(out, t.b);
                    bcount = 1;
                    while (i + bcount < end && this.bComparator.compare(t.b, ((Tuple4) keys[i + bcount]).b) == 0) {
                        bcount++;
                    }
                    DataOutput2.packInt(out, bcount);
                }
                if (ccount == 0) {
                    this.cSerializer.serialize(out, t.c);
                    ccount = 1;
                    while (i + ccount < end && this.cComparator.compare(t.c, ((Tuple4) keys[i + ccount]).c) == 0) {
                        ccount++;
                    }
                    DataOutput2.packInt(out, ccount);
                }
                this.dSerializer.serialize(out, t.d);
                acount--;
                bcount--;
                ccount--;
                i++;
            }
        }

        public Object[] deserialize(DataInput in, int start, int end, int size) throws IOException {
            Object[] ret = new Object[size];
            A a = null;
            int acount = 0;
            B b = null;
            int bcount = 0;
            C c = null;
            int ccount = 0;
            for (int i = start; i < end; i++) {
                if (acount == 0) {
                    a = this.aSerializer.deserialize(in, -1);
                    acount = DataInput2.unpackInt(in);
                }
                if (bcount == 0) {
                    b = this.bSerializer.deserialize(in, -1);
                    bcount = DataInput2.unpackInt(in);
                }
                if (ccount == 0) {
                    c = this.cSerializer.deserialize(in, -1);
                    ccount = DataInput2.unpackInt(in);
                }
                ret[i] = Fun.t4(a, b, c, this.dSerializer.deserialize(in, -1));
                acount--;
                bcount--;
                ccount--;
            }
            if (!$assertionsDisabled && acount != 0) {
                throw new AssertionError();
            } else if (!$assertionsDisabled && bcount != 0) {
                throw new AssertionError();
            } else if ($assertionsDisabled || ccount == 0) {
                return ret;
            } else {
                throw new AssertionError();
            }
        }

        public Comparator<Tuple4<A, B, C, D>> getComparator() {
            return BTreeMap.COMPARABLE_COMPARATOR;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return $assertionsDisabled;
            }
            Tuple4KeySerializer t = (Tuple4KeySerializer) o;
            if (Fun.eq(this.aComparator, t.aComparator) && Fun.eq(this.bComparator, t.bComparator) && Fun.eq(this.cComparator, t.cComparator) && Fun.eq(this.aSerializer, t.aSerializer) && Fun.eq(this.bSerializer, t.bSerializer) && Fun.eq(this.cSerializer, t.cSerializer) && Fun.eq(this.dSerializer, t.dSerializer)) {
                return true;
            }
            return $assertionsDisabled;
        }

        public int hashCode() {
            int result;
            int hashCode;
            int i = 0;
            if (this.aComparator != null) {
                result = this.aComparator.hashCode();
            } else {
                result = 0;
            }
            int i2 = result * 31;
            if (this.bComparator != null) {
                hashCode = this.bComparator.hashCode();
            } else {
                hashCode = 0;
            }
            i2 = (i2 + hashCode) * 31;
            if (this.cComparator != null) {
                hashCode = this.cComparator.hashCode();
            } else {
                hashCode = 0;
            }
            i2 = (i2 + hashCode) * 31;
            if (this.aSerializer != null) {
                hashCode = this.aSerializer.hashCode();
            } else {
                hashCode = 0;
            }
            i2 = (i2 + hashCode) * 31;
            if (this.bSerializer != null) {
                hashCode = this.bSerializer.hashCode();
            } else {
                hashCode = 0;
            }
            i2 = (i2 + hashCode) * 31;
            if (this.cSerializer != null) {
                hashCode = this.cSerializer.hashCode();
            } else {
                hashCode = 0;
            }
            hashCode = (i2 + hashCode) * 31;
            if (this.dSerializer != null) {
                i = this.dSerializer.hashCode();
            }
            return hashCode + i;
        }
    }

    public static class Tuple5KeySerializer<A, B, C, D, E> extends BTreeKeySerializer<Tuple5<A, B, C, D, E>> implements Serializable {
        static final /* synthetic */ boolean $assertionsDisabled;
        private static final long serialVersionUID = 8607477718850453705L;
        protected final Comparator<A> aComparator;
        protected final Serializer<A> aSerializer;
        protected final Comparator<B> bComparator;
        protected final Serializer<B> bSerializer;
        protected final Comparator<C> cComparator;
        protected final Serializer<C> cSerializer;
        protected final Comparator<D> dComparator;
        protected final Serializer<D> dSerializer;
        protected final Serializer<E> eSerializer;

        static {
            $assertionsDisabled = !BTreeKeySerializer.class.desiredAssertionStatus() ? true : $assertionsDisabled;
        }

        public Tuple5KeySerializer(Comparator<A> aComparator, Comparator<B> bComparator, Comparator<C> cComparator, Comparator<D> dComparator, Serializer<A> aSerializer, Serializer<B> bSerializer, Serializer<C> cSerializer, Serializer<D> dSerializer, Serializer<E> eSerializer) {
            this.aComparator = aComparator;
            this.bComparator = bComparator;
            this.cComparator = cComparator;
            this.dComparator = dComparator;
            this.aSerializer = aSerializer;
            this.bSerializer = bSerializer;
            this.cSerializer = cSerializer;
            this.dSerializer = dSerializer;
            this.eSerializer = eSerializer;
        }

        Tuple5KeySerializer(SerializerBase serializerBase, DataInput is, FastArrayList<Object> objectStack) throws IOException {
            objectStack.add(this);
            this.aComparator = (Comparator) serializerBase.deserialize(is, (FastArrayList) objectStack);
            this.bComparator = (Comparator) serializerBase.deserialize(is, (FastArrayList) objectStack);
            this.cComparator = (Comparator) serializerBase.deserialize(is, (FastArrayList) objectStack);
            this.dComparator = (Comparator) serializerBase.deserialize(is, (FastArrayList) objectStack);
            this.aSerializer = (Serializer) serializerBase.deserialize(is, (FastArrayList) objectStack);
            this.bSerializer = (Serializer) serializerBase.deserialize(is, (FastArrayList) objectStack);
            this.cSerializer = (Serializer) serializerBase.deserialize(is, (FastArrayList) objectStack);
            this.dSerializer = (Serializer) serializerBase.deserialize(is, (FastArrayList) objectStack);
            this.eSerializer = (Serializer) serializerBase.deserialize(is, (FastArrayList) objectStack);
        }

        public void serialize(DataOutput out, int start, int end, Object[] keys) throws IOException {
            int acount = 0;
            int bcount = 0;
            int ccount = 0;
            int dcount = 0;
            int i = start;
            while (i < end) {
                Tuple5<A, B, C, D, E> t = keys[i];
                if (acount == 0) {
                    this.aSerializer.serialize(out, t.a);
                    acount = 1;
                    while (i + acount < end && this.aComparator.compare(t.a, ((Tuple5) keys[i + acount]).a) == 0) {
                        acount++;
                    }
                    DataOutput2.packInt(out, acount);
                }
                if (bcount == 0) {
                    this.bSerializer.serialize(out, t.b);
                    bcount = 1;
                    while (i + bcount < end && this.bComparator.compare(t.b, ((Tuple5) keys[i + bcount]).b) == 0) {
                        bcount++;
                    }
                    DataOutput2.packInt(out, bcount);
                }
                if (ccount == 0) {
                    this.cSerializer.serialize(out, t.c);
                    ccount = 1;
                    while (i + ccount < end && this.cComparator.compare(t.c, ((Tuple5) keys[i + ccount]).c) == 0) {
                        ccount++;
                    }
                    DataOutput2.packInt(out, ccount);
                }
                if (dcount == 0) {
                    this.dSerializer.serialize(out, t.d);
                    dcount = 1;
                    while (i + dcount < end && this.dComparator.compare(t.d, ((Tuple5) keys[i + dcount]).d) == 0) {
                        dcount++;
                    }
                    DataOutput2.packInt(out, dcount);
                }
                this.eSerializer.serialize(out, t.e);
                acount--;
                bcount--;
                ccount--;
                dcount--;
                i++;
            }
        }

        public Object[] deserialize(DataInput in, int start, int end, int size) throws IOException {
            Object[] ret = new Object[size];
            A a = null;
            int acount = 0;
            B b = null;
            int bcount = 0;
            C c = null;
            int ccount = 0;
            D d = null;
            int dcount = 0;
            for (int i = start; i < end; i++) {
                if (acount == 0) {
                    a = this.aSerializer.deserialize(in, -1);
                    acount = DataInput2.unpackInt(in);
                }
                if (bcount == 0) {
                    b = this.bSerializer.deserialize(in, -1);
                    bcount = DataInput2.unpackInt(in);
                }
                if (ccount == 0) {
                    c = this.cSerializer.deserialize(in, -1);
                    ccount = DataInput2.unpackInt(in);
                }
                if (dcount == 0) {
                    d = this.dSerializer.deserialize(in, -1);
                    dcount = DataInput2.unpackInt(in);
                }
                ret[i] = Fun.t5(a, b, c, d, this.eSerializer.deserialize(in, -1));
                acount--;
                bcount--;
                ccount--;
                dcount--;
            }
            if (!$assertionsDisabled && acount != 0) {
                throw new AssertionError();
            } else if (!$assertionsDisabled && bcount != 0) {
                throw new AssertionError();
            } else if (!$assertionsDisabled && ccount != 0) {
                throw new AssertionError();
            } else if ($assertionsDisabled || dcount == 0) {
                return ret;
            } else {
                throw new AssertionError();
            }
        }

        public Comparator<Tuple5<A, B, C, D, E>> getComparator() {
            return BTreeMap.COMPARABLE_COMPARATOR;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return $assertionsDisabled;
            }
            Tuple5KeySerializer t = (Tuple5KeySerializer) o;
            if (Fun.eq(this.aComparator, t.aComparator) && Fun.eq(this.bComparator, t.bComparator) && Fun.eq(this.cComparator, t.cComparator) && Fun.eq(this.dComparator, t.dComparator) && Fun.eq(this.aSerializer, t.aSerializer) && Fun.eq(this.bSerializer, t.bSerializer) && Fun.eq(this.cSerializer, t.cSerializer) && Fun.eq(this.dSerializer, t.dSerializer) && Fun.eq(this.eSerializer, t.eSerializer)) {
                return true;
            }
            return $assertionsDisabled;
        }

        public int hashCode() {
            int result;
            int hashCode;
            int i = 0;
            if (this.aComparator != null) {
                result = this.aComparator.hashCode();
            } else {
                result = 0;
            }
            int i2 = result * 31;
            if (this.bComparator != null) {
                hashCode = this.bComparator.hashCode();
            } else {
                hashCode = 0;
            }
            i2 = (i2 + hashCode) * 31;
            if (this.cComparator != null) {
                hashCode = this.cComparator.hashCode();
            } else {
                hashCode = 0;
            }
            i2 = (i2 + hashCode) * 31;
            if (this.dComparator != null) {
                hashCode = this.dComparator.hashCode();
            } else {
                hashCode = 0;
            }
            i2 = (i2 + hashCode) * 31;
            if (this.aSerializer != null) {
                hashCode = this.aSerializer.hashCode();
            } else {
                hashCode = 0;
            }
            i2 = (i2 + hashCode) * 31;
            if (this.bSerializer != null) {
                hashCode = this.bSerializer.hashCode();
            } else {
                hashCode = 0;
            }
            i2 = (i2 + hashCode) * 31;
            if (this.cSerializer != null) {
                hashCode = this.cSerializer.hashCode();
            } else {
                hashCode = 0;
            }
            i2 = (i2 + hashCode) * 31;
            if (this.dSerializer != null) {
                hashCode = this.dSerializer.hashCode();
            } else {
                hashCode = 0;
            }
            hashCode = (i2 + hashCode) * 31;
            if (this.eSerializer != null) {
                i = this.eSerializer.hashCode();
            }
            return hashCode + i;
        }
    }

    public static class Tuple6KeySerializer<A, B, C, D, E, F> extends BTreeKeySerializer<Tuple6<A, B, C, D, E, F>> implements Serializable {
        static final /* synthetic */ boolean $assertionsDisabled;
        private static final long serialVersionUID = 3666600849149868404L;
        protected final Comparator<A> aComparator;
        protected final Serializer<A> aSerializer;
        protected final Comparator<B> bComparator;
        protected final Serializer<B> bSerializer;
        protected final Comparator<C> cComparator;
        protected final Serializer<C> cSerializer;
        protected final Comparator<D> dComparator;
        protected final Serializer<D> dSerializer;
        protected final Comparator<E> eComparator;
        protected final Serializer<E> eSerializer;
        protected final Serializer<F> fSerializer;

        static {
            $assertionsDisabled = !BTreeKeySerializer.class.desiredAssertionStatus() ? true : $assertionsDisabled;
        }

        public Tuple6KeySerializer(Comparator<A> aComparator, Comparator<B> bComparator, Comparator<C> cComparator, Comparator<D> dComparator, Comparator<E> eComparator, Serializer<A> aSerializer, Serializer<B> bSerializer, Serializer<C> cSerializer, Serializer<D> dSerializer, Serializer<E> eSerializer, Serializer<F> fSerializer) {
            this.aComparator = aComparator;
            this.bComparator = bComparator;
            this.cComparator = cComparator;
            this.dComparator = dComparator;
            this.eComparator = eComparator;
            this.aSerializer = aSerializer;
            this.bSerializer = bSerializer;
            this.cSerializer = cSerializer;
            this.dSerializer = dSerializer;
            this.eSerializer = eSerializer;
            this.fSerializer = fSerializer;
        }

        Tuple6KeySerializer(SerializerBase serializerBase, DataInput is, FastArrayList<Object> objectStack) throws IOException {
            objectStack.add(this);
            this.aComparator = (Comparator) serializerBase.deserialize(is, (FastArrayList) objectStack);
            this.bComparator = (Comparator) serializerBase.deserialize(is, (FastArrayList) objectStack);
            this.cComparator = (Comparator) serializerBase.deserialize(is, (FastArrayList) objectStack);
            this.dComparator = (Comparator) serializerBase.deserialize(is, (FastArrayList) objectStack);
            this.eComparator = (Comparator) serializerBase.deserialize(is, (FastArrayList) objectStack);
            this.aSerializer = (Serializer) serializerBase.deserialize(is, (FastArrayList) objectStack);
            this.bSerializer = (Serializer) serializerBase.deserialize(is, (FastArrayList) objectStack);
            this.cSerializer = (Serializer) serializerBase.deserialize(is, (FastArrayList) objectStack);
            this.dSerializer = (Serializer) serializerBase.deserialize(is, (FastArrayList) objectStack);
            this.eSerializer = (Serializer) serializerBase.deserialize(is, (FastArrayList) objectStack);
            this.fSerializer = (Serializer) serializerBase.deserialize(is, (FastArrayList) objectStack);
        }

        public void serialize(DataOutput out, int start, int end, Object[] keys) throws IOException {
            int acount = 0;
            int bcount = 0;
            int ccount = 0;
            int dcount = 0;
            int ecount = 0;
            int i = start;
            while (i < end) {
                Tuple6<A, B, C, D, E, F> t = keys[i];
                if (acount == 0) {
                    this.aSerializer.serialize(out, t.a);
                    acount = 1;
                    while (i + acount < end && this.aComparator.compare(t.a, ((Tuple6) keys[i + acount]).a) == 0) {
                        acount++;
                    }
                    DataOutput2.packInt(out, acount);
                }
                if (bcount == 0) {
                    this.bSerializer.serialize(out, t.b);
                    bcount = 1;
                    while (i + bcount < end && this.bComparator.compare(t.b, ((Tuple6) keys[i + bcount]).b) == 0) {
                        bcount++;
                    }
                    DataOutput2.packInt(out, bcount);
                }
                if (ccount == 0) {
                    this.cSerializer.serialize(out, t.c);
                    ccount = 1;
                    while (i + ccount < end && this.cComparator.compare(t.c, ((Tuple6) keys[i + ccount]).c) == 0) {
                        ccount++;
                    }
                    DataOutput2.packInt(out, ccount);
                }
                if (dcount == 0) {
                    this.dSerializer.serialize(out, t.d);
                    dcount = 1;
                    while (i + dcount < end && this.dComparator.compare(t.d, ((Tuple6) keys[i + dcount]).d) == 0) {
                        dcount++;
                    }
                    DataOutput2.packInt(out, dcount);
                }
                if (ecount == 0) {
                    this.eSerializer.serialize(out, t.e);
                    ecount = 1;
                    while (i + ecount < end && this.eComparator.compare(t.e, ((Tuple6) keys[i + ecount]).e) == 0) {
                        ecount++;
                    }
                    DataOutput2.packInt(out, ecount);
                }
                this.fSerializer.serialize(out, t.f);
                acount--;
                bcount--;
                ccount--;
                dcount--;
                ecount--;
                i++;
            }
        }

        public Object[] deserialize(DataInput in, int start, int end, int size) throws IOException {
            Object[] ret = new Object[size];
            int acount = 0;
            int bcount = 0;
            int ccount = 0;
            int dcount = 0;
            int ecount = 0;
            Object obj = null;
            Object obj2 = null;
            Object obj3 = null;
            Object obj4 = null;
            Object obj5 = null;
            for (int i = start; i < end; i++) {
                if (acount == 0) {
                    A a = this.aSerializer.deserialize(in, -1);
                    acount = DataInput2.unpackInt(in);
                    obj5 = a;
                }
                if (bcount == 0) {
                    B b = this.bSerializer.deserialize(in, -1);
                    bcount = DataInput2.unpackInt(in);
                    obj4 = b;
                }
                if (ccount == 0) {
                    C c = this.cSerializer.deserialize(in, -1);
                    ccount = DataInput2.unpackInt(in);
                    obj3 = c;
                }
                if (dcount == 0) {
                    D d = this.dSerializer.deserialize(in, -1);
                    dcount = DataInput2.unpackInt(in);
                    obj2 = d;
                }
                if (ecount == 0) {
                    E e = this.eSerializer.deserialize(in, -1);
                    ecount = DataInput2.unpackInt(in);
                    obj = e;
                }
                ret[i] = Fun.t6(obj5, obj4, obj3, obj2, obj, this.fSerializer.deserialize(in, -1));
                acount--;
                bcount--;
                ccount--;
                dcount--;
                ecount--;
            }
            if (!$assertionsDisabled && acount != 0) {
                throw new AssertionError();
            } else if (!$assertionsDisabled && bcount != 0) {
                throw new AssertionError();
            } else if (!$assertionsDisabled && ccount != 0) {
                throw new AssertionError();
            } else if (!$assertionsDisabled && dcount != 0) {
                throw new AssertionError();
            } else if ($assertionsDisabled || ecount == 0) {
                return ret;
            } else {
                throw new AssertionError();
            }
        }

        public Comparator<Tuple6<A, B, C, D, E, F>> getComparator() {
            return BTreeMap.COMPARABLE_COMPARATOR;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return $assertionsDisabled;
            }
            Tuple6KeySerializer t = (Tuple6KeySerializer) o;
            if (Fun.eq(this.aComparator, t.aComparator) && Fun.eq(this.bComparator, t.bComparator) && Fun.eq(this.cComparator, t.cComparator) && Fun.eq(this.dComparator, t.dComparator) && Fun.eq(this.eComparator, t.eComparator) && Fun.eq(this.aSerializer, t.aSerializer) && Fun.eq(this.bSerializer, t.bSerializer) && Fun.eq(this.cSerializer, t.cSerializer) && Fun.eq(this.dSerializer, t.dSerializer) && Fun.eq(this.eSerializer, t.eSerializer) && Fun.eq(this.fSerializer, t.fSerializer)) {
                return true;
            }
            return $assertionsDisabled;
        }

        public int hashCode() {
            int result;
            int hashCode;
            int i = 0;
            if (this.aComparator != null) {
                result = this.aComparator.hashCode();
            } else {
                result = 0;
            }
            int i2 = result * 31;
            if (this.bComparator != null) {
                hashCode = this.bComparator.hashCode();
            } else {
                hashCode = 0;
            }
            i2 = (i2 + hashCode) * 31;
            if (this.cComparator != null) {
                hashCode = this.cComparator.hashCode();
            } else {
                hashCode = 0;
            }
            i2 = (i2 + hashCode) * 31;
            if (this.dComparator != null) {
                hashCode = this.dComparator.hashCode();
            } else {
                hashCode = 0;
            }
            i2 = (i2 + hashCode) * 31;
            if (this.eComparator != null) {
                hashCode = this.eComparator.hashCode();
            } else {
                hashCode = 0;
            }
            i2 = (i2 + hashCode) * 31;
            if (this.aSerializer != null) {
                hashCode = this.aSerializer.hashCode();
            } else {
                hashCode = 0;
            }
            i2 = (i2 + hashCode) * 31;
            if (this.bSerializer != null) {
                hashCode = this.bSerializer.hashCode();
            } else {
                hashCode = 0;
            }
            i2 = (i2 + hashCode) * 31;
            if (this.cSerializer != null) {
                hashCode = this.cSerializer.hashCode();
            } else {
                hashCode = 0;
            }
            i2 = (i2 + hashCode) * 31;
            if (this.dSerializer != null) {
                hashCode = this.dSerializer.hashCode();
            } else {
                hashCode = 0;
            }
            i2 = (i2 + hashCode) * 31;
            if (this.eSerializer != null) {
                hashCode = this.eSerializer.hashCode();
            } else {
                hashCode = 0;
            }
            hashCode = (i2 + hashCode) * 31;
            if (this.fSerializer != null) {
                i = this.fSerializer.hashCode();
            }
            return hashCode + i;
        }
    }

    public abstract Object[] deserialize(DataInput dataInput, int i, int i2, int i3) throws IOException;

    public abstract Comparator<K> getComparator();

    public abstract void serialize(DataOutput dataOutput, int i, int i2, Object[] objArr) throws IOException;

    static {
        BASIC = new BasicKeySerializer(Serializer.BASIC);
        ZERO_OR_POSITIVE_LONG = new BTreeKeySerializer<Long>() {
            public void serialize(DataOutput out, int start, int end, Object[] keys) throws IOException {
                if (start < end) {
                    long prev = ((Long) keys[start]).longValue();
                    DataOutput2.packLong(out, prev);
                    for (int i = start + 1; i < end; i++) {
                        long curr = ((Long) keys[i]).longValue();
                        DataOutput2.packLong(out, curr - prev);
                        prev = curr;
                    }
                }
            }

            public Object[] deserialize(DataInput in, int start, int end, int size) throws IOException {
                Object[] ret = new Long[size];
                long prev = 0;
                for (int i = start; i < end; i++) {
                    prev += DataInput2.unpackLong(in);
                    ret[i] = Long.valueOf(prev);
                }
                return ret;
            }

            public Comparator<Long> getComparator() {
                return BTreeMap.COMPARABLE_COMPARATOR;
            }
        };
        ZERO_OR_POSITIVE_INT = new BTreeKeySerializer<Integer>() {
            public void serialize(DataOutput out, int start, int end, Object[] keys) throws IOException {
                if (start < end) {
                    int prev = ((Integer) keys[start]).intValue();
                    DataOutput2.packLong(out, (long) prev);
                    for (int i = start + 1; i < end; i++) {
                        int curr = ((Integer) keys[i]).intValue();
                        DataOutput2.packInt(out, curr - prev);
                        prev = curr;
                    }
                }
            }

            public Object[] deserialize(DataInput in, int start, int end, int size) throws IOException {
                Object[] ret = new Integer[size];
                int prev = 0;
                for (int i = start; i < end; i++) {
                    prev += DataInput2.unpackInt(in);
                    ret[i] = Integer.valueOf(prev);
                }
                return ret;
            }

            public Comparator<Integer> getComparator() {
                return BTreeMap.COMPARABLE_COMPARATOR;
            }
        };
        STRING = new BTreeKeySerializer<String>() {
            private final Charset UTF8_CHARSET;

            {
                this.UTF8_CHARSET = Charset.forName(UrlUtils.UTF8);
            }

            public void serialize(DataOutput out, int start, int end, Object[] keys) throws IOException {
                byte[] previous = null;
                for (int i = start; i < end; i++) {
                    byte[] b = ((String) keys[i]).getBytes(this.UTF8_CHARSET);
                    BTreeKeySerializer.leadingValuePackWrite(out, b, previous, 0);
                    previous = b;
                }
            }

            public Object[] deserialize(DataInput in, int start, int end, int size) throws IOException {
                Object[] ret = new Object[size];
                byte[] previous = null;
                for (int i = start; i < end; i++) {
                    byte[] b = BTreeKeySerializer.leadingValuePackRead(in, previous, 0);
                    if (b != null) {
                        ret[i] = new String(b, this.UTF8_CHARSET);
                        previous = b;
                    }
                }
                return ret;
            }

            public Comparator<String> getComparator() {
                return BTreeMap.COMPARABLE_COMPARATOR;
            }
        };
        TUPLE2 = new Tuple2KeySerializer(null, null, null);
        TUPLE3 = new Tuple3KeySerializer(null, null, null, null, null);
        TUPLE4 = new Tuple4KeySerializer(null, null, null, null, null, null, null);
    }

    public static byte[] leadingValuePackRead(DataInput in, byte[] previous, int ignoreLeadingCount) throws IOException {
        int len = DataInput2.unpackInt(in) - 1;
        if (len == -1) {
            return null;
        }
        int actualCommon = DataInput2.unpackInt(in);
        byte[] buf = new byte[len];
        if (previous == null) {
            actualCommon = 0;
        }
        if (actualCommon > 0) {
            in.readFully(buf, 0, ignoreLeadingCount);
            System.arraycopy(previous, ignoreLeadingCount, buf, ignoreLeadingCount, actualCommon - ignoreLeadingCount);
        }
        in.readFully(buf, actualCommon, len - actualCommon);
        return buf;
    }

    public static void leadingValuePackWrite(DataOutput out, byte[] buf, byte[] previous, int ignoreLeadingCount) throws IOException {
        if (buf == null) {
            DataOutput2.packInt(out, 0);
            return;
        }
        int actualCommon = ignoreLeadingCount;
        if (previous != null) {
            int maxCommon = buf.length > previous.length ? previous.length : buf.length;
            if (maxCommon > 32767) {
                maxCommon = 32767;
            }
            while (actualCommon < maxCommon && buf[actualCommon] == previous[actualCommon]) {
                actualCommon++;
            }
        }
        DataOutput2.packInt(out, buf.length + 1);
        DataOutput2.packInt(out, actualCommon);
        out.write(buf, 0, ignoreLeadingCount);
        out.write(buf, actualCommon, buf.length - actualCommon);
    }
}
