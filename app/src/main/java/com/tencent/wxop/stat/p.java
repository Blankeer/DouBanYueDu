package com.tencent.wxop.stat;

import android.content.Context;
import com.douban.book.reader.helper.AppUri;
import com.tencent.wxop.stat.a.d;
import com.tencent.wxop.stat.b.l;
import com.tencent.wxop.stat.b.q;
import u.aly.ci;
import u.aly.dj;
import u.aly.dx;

final class p {
    private static volatile long bU;
    private d bP;
    private d bQ;
    private boolean bR;
    private Context bS;
    private long bT;

    static {
        bU = 0;
    }

    public p(d dVar) {
        this.bQ = null;
        this.bR = false;
        this.bS = null;
        this.bT = System.currentTimeMillis();
        this.bP = dVar;
        this.bQ = c.j();
        this.bR = dVar.X();
        this.bS = dVar.J();
    }

    private void H() {
        if (t.ai().aI <= 0 || !c.ax) {
            a(new s(this));
            return;
        }
        t.ai().b(this.bP, null, this.bR, true);
        t.ai().b(-1);
    }

    private void a(aj ajVar) {
        ak.Z(e.aY).a(this.bP, ajVar);
    }

    public final void ah() {
        boolean z;
        long u;
        if (c.ae > 0) {
            if (this.bT > e.aO) {
                e.aN.clear();
                e.aO = this.bT + c.af;
                if (c.k()) {
                    e.aV.b("clear methodsCalledLimitMap, nextLimitCallClearTime=" + e.aO);
                }
            }
            Integer valueOf = Integer.valueOf(this.bP.ac().r());
            Integer num = (Integer) e.aN.get(valueOf);
            if (num != null) {
                e.aN.put(valueOf, Integer.valueOf(num.intValue() + 1));
                if (num.intValue() > c.ae) {
                    if (c.k()) {
                        e.aV.d("event " + this.bP.af() + " was discard, cause of called limit, current:" + num + ", limit:" + c.ae + ", period:" + c.af + " ms");
                    }
                    z = true;
                    if (z) {
                        if (c.ay > 0 && this.bT >= bU) {
                            e.p(this.bS);
                            bU = this.bT + c.az;
                            if (c.k()) {
                                e.aV.b("nextFlushTime=" + bU);
                            }
                        }
                        if (g.r(this.bS).X()) {
                            t.s(this.bS).b(this.bP, null, this.bR, false);
                            return;
                        }
                        if (c.k()) {
                            e.aV.b("sendFailedCount=" + e.aI);
                        }
                        if (e.a()) {
                            if (this.bP.ae() != null && this.bP.ae().R()) {
                                this.bQ = d.INSTANT;
                            }
                            if (c.ah && g.r(e.aY).W()) {
                                this.bQ = d.INSTANT;
                            }
                            if (c.k()) {
                                e.aV.b("strategy=" + this.bQ.name());
                            }
                            switch (j.bL[this.bQ.ordinal()]) {
                                case dx.b /*1*/:
                                    H();
                                case dx.c /*2*/:
                                    t.s(this.bS).b(this.bP, null, this.bR, false);
                                    if (c.k()) {
                                        e.aV.b("PERIOD currTime=" + this.bT + ",nextPeriodSendTs=" + e.aZ + ",difftime=" + (e.aZ - this.bT));
                                    }
                                    if (e.aZ == 0) {
                                        e.aZ = q.f(this.bS, "last_period_ts");
                                        if (this.bT > e.aZ) {
                                            e.q(this.bS);
                                        }
                                        u = this.bT + ((long) ((c.u() * 60) * AppUri.OPEN_URL));
                                        if (e.aZ > u) {
                                            e.aZ = u;
                                        }
                                        af.Y(this.bS).ah();
                                    }
                                    if (c.k()) {
                                        e.aV.b("PERIOD currTime=" + this.bT + ",nextPeriodSendTs=" + e.aZ + ",difftime=" + (e.aZ - this.bT));
                                    }
                                    if (this.bT > e.aZ) {
                                        e.q(this.bS);
                                    }
                                case dx.d /*3*/:
                                case dx.e /*4*/:
                                    t.s(this.bS).b(this.bP, null, this.bR, false);
                                case dj.f /*5*/:
                                    t.s(this.bS).b(this.bP, new q(this), this.bR, true);
                                case ci.g /*6*/:
                                    if (g.r(e.aY).D() != 1) {
                                        H();
                                    } else {
                                        t.s(this.bS).b(this.bP, null, this.bR, false);
                                    }
                                case ci.h /*7*/:
                                    if (l.y(this.bS)) {
                                        a(new r(this));
                                    }
                                default:
                                    e.aV.error("Invalid stat strategy:" + c.j());
                            }
                        }
                        t.s(this.bS).b(this.bP, null, this.bR, false);
                        if (this.bT - e.aX > 1800000) {
                            e.n(this.bS);
                            return;
                        }
                        return;
                    }
                }
            }
            e.aN.put(valueOf, Integer.valueOf(1));
        }
        z = false;
        if (z) {
            e.p(this.bS);
            bU = this.bT + c.az;
            if (c.k()) {
                e.aV.b("nextFlushTime=" + bU);
            }
            if (g.r(this.bS).X()) {
                t.s(this.bS).b(this.bP, null, this.bR, false);
                return;
            }
            if (c.k()) {
                e.aV.b("sendFailedCount=" + e.aI);
            }
            if (e.a()) {
                t.s(this.bS).b(this.bP, null, this.bR, false);
                if (this.bT - e.aX > 1800000) {
                    e.n(this.bS);
                    return;
                }
                return;
            }
            this.bQ = d.INSTANT;
            this.bQ = d.INSTANT;
            if (c.k()) {
                e.aV.b("strategy=" + this.bQ.name());
            }
            switch (j.bL[this.bQ.ordinal()]) {
                case dx.b /*1*/:
                    H();
                case dx.c /*2*/:
                    t.s(this.bS).b(this.bP, null, this.bR, false);
                    if (c.k()) {
                        e.aV.b("PERIOD currTime=" + this.bT + ",nextPeriodSendTs=" + e.aZ + ",difftime=" + (e.aZ - this.bT));
                    }
                    if (e.aZ == 0) {
                        e.aZ = q.f(this.bS, "last_period_ts");
                        if (this.bT > e.aZ) {
                            e.q(this.bS);
                        }
                        u = this.bT + ((long) ((c.u() * 60) * AppUri.OPEN_URL));
                        if (e.aZ > u) {
                            e.aZ = u;
                        }
                        af.Y(this.bS).ah();
                    }
                    if (c.k()) {
                        e.aV.b("PERIOD currTime=" + this.bT + ",nextPeriodSendTs=" + e.aZ + ",difftime=" + (e.aZ - this.bT));
                    }
                    if (this.bT > e.aZ) {
                        e.q(this.bS);
                    }
                case dx.d /*3*/:
                case dx.e /*4*/:
                    t.s(this.bS).b(this.bP, null, this.bR, false);
                case dj.f /*5*/:
                    t.s(this.bS).b(this.bP, new q(this), this.bR, true);
                case ci.g /*6*/:
                    if (g.r(e.aY).D() != 1) {
                        t.s(this.bS).b(this.bP, null, this.bR, false);
                    } else {
                        H();
                    }
                case ci.h /*7*/:
                    if (l.y(this.bS)) {
                        a(new r(this));
                    }
                default:
                    e.aV.error("Invalid stat strategy:" + c.j());
            }
        }
    }
}
