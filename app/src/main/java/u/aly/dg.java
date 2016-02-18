package u.aly;

import com.j256.ormlite.stmt.query.SimpleComparison;
import io.realm.internal.Table;

/* compiled from: TMessage */
public final class dg {
    public final String a;
    public final byte b;
    public final int c;

    public dg() {
        this(Table.STRING_DEFAULT_VALUE, (byte) 0, 0);
    }

    public dg(String str, byte b, int i) {
        this.a = str;
        this.b = b;
        this.c = i;
    }

    public String toString() {
        return "<TMessage name:'" + this.a + "' type: " + this.b + " seqid:" + this.c + SimpleComparison.GREATER_THAN_OPERATION;
    }

    public boolean equals(Object obj) {
        if (obj instanceof dg) {
            return a((dg) obj);
        }
        return false;
    }

    public boolean a(dg dgVar) {
        return this.a.equals(dgVar.a) && this.b == dgVar.b && this.c == dgVar.c;
    }
}
