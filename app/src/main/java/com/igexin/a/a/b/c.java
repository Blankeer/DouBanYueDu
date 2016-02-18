package com.igexin.a.a.b;

public abstract class c {
    protected String a;
    protected c b;
    protected c c;
    protected boolean d;

    public c(String str, boolean z) {
        this.a = str;
        this.d = z;
    }

    public abstract Object a(f fVar, e eVar, Object obj);

    protected final void a(c cVar) {
        if (cVar != null) {
            c cVar2 = cVar.b;
            cVar.b = this;
            this.c = cVar;
            this.b = cVar2;
        }
    }

    public void a(boolean z) {
        if (!this.d || z) {
            while (this.b != null) {
                c cVar = this.b.b;
                this.b.b = null;
                this.b = cVar;
            }
        }
    }

    public abstract Object c(f fVar, e eVar, Object obj);

    public final Object d(f fVar, e eVar, Object obj) {
        if (obj == null) {
            throw new NullPointerException("Nothing to encode!");
        }
        if (this.b != null) {
            obj = this.b.d(fVar, eVar, obj);
        }
        return a(fVar, eVar, obj);
    }
}
