package com.igexin.a.a.b.a.a;

import com.igexin.a.a.b.c;
import com.igexin.a.a.b.d;
import com.igexin.a.a.b.f;
import com.igexin.a.a.c.a;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public final class e extends f {
    static volatile e k;
    Selector e;
    AtomicBoolean f;
    AtomicBoolean g;
    volatile boolean h;
    final long i;
    int j;
    volatile long l;
    volatile long m;
    volatile long n;
    ConcurrentLinkedQueue o;
    List p;
    d q;
    ByteBuffer r;
    boolean s;
    int t;
    final Comparator u;

    public e(int i, String str, c cVar) {
        super(i, str, cVar);
        this.i = 15000;
        this.j = 0;
        this.l = 0;
        this.m = 0;
        this.n = 0;
        this.s = false;
        this.u = new f(this);
        this.f = new AtomicBoolean(false);
        this.g = new AtomicBoolean(true);
        this.o = new ConcurrentLinkedQueue();
        this.p = new ArrayList(16);
        this.r = ByteBuffer.allocate(61440);
    }

    public static e a(String str, c cVar) {
        if (k == null || k.E || k.w) {
            e eVar = new e(-2047, str, cVar);
            k = eVar;
            return eVar;
        } else if (k.a.equals(str)) {
            return k;
        } else {
            throw new IllegalArgumentException();
        }
    }

    public static e h() {
        return k;
    }

    final void a(SocketChannel socketChannel) {
        a.a("niosockettask|toRead");
        int read;
        do {
            try {
                read = socketChannel.read(this.r);
                if (read < 0) {
                    a.a("niosockettask|socketread|-1|");
                    this.t++;
                    if (this.t > 20) {
                        this.t = 0;
                        throw new EOFException("NioConnection Read EOF!");
                    }
                    return;
                } else if (read == 0) {
                    a.a("niosockettask|socketread|0|");
                    return;
                } else {
                    this.j = 0;
                    this.p.clear();
                    this.l = 0;
                    this.r.flip();
                    int remaining = this.r.remaining();
                    d c;
                    if (d.f) {
                        c = d.c();
                        c.d += (long) remaining;
                    } else {
                        c = d.c();
                        c.b += (long) remaining;
                    }
                    if (this.b != null) {
                        this.b.c(this, this.d, this.r);
                    }
                    this.r.clear();
                }
            } catch (EOFException e) {
                a.b("niosockettask|read exception|" + e.getMessage());
                throw e;
            } catch (Exception e2) {
                a.b("niosockettask|read exception|" + e2.getMessage());
                throw new ClosedChannelException();
            }
        } while (read > 0);
    }

    public void a_() {
        super.a_();
        a.a("niosockettask|run");
        if (this.d == null) {
            try {
                if (l() != null) {
                    a.a("niosockettask|wakelock|connect|off");
                    l().release();
                }
                g();
                if (l() != null) {
                    l().acquire();
                    a.a("niosockettask|wakelock|connect|on");
                }
            } catch (Throwable th) {
                if (l() != null) {
                    l().acquire();
                    a.a("niosockettask|wakelock|connect|on");
                }
            }
        } else if (this.f.get() || this.q.b) {
            a.a("niosockettask|close|" + this.f.get() + "|" + this.q.b);
            if (this.f.get()) {
                b bVar = new b();
                bVar.a = 1;
                d.c().a((Object) bVar);
            }
            throw new ClosedChannelException();
        } else {
            if (!this.o.isEmpty()) {
                this.q.b(true);
            }
            if (this.l < 0) {
                this.l = 0;
            }
            this.m = System.currentTimeMillis();
            try {
                int select;
                if (l() != null) {
                    a.a("niosockettask|wakelock|select|off");
                    l().release();
                }
                if (this.l > 0) {
                    P.b((this.m + this.l) + com.igexin.a.a.d.e.z);
                    this.g.set(false);
                    select = this.e.select(this.l);
                    P.f();
                } else {
                    this.g.set(false);
                    select = this.e.select();
                }
                if (l() != null) {
                    l().acquire();
                    a.a("niosockettask|wakelock|select|on");
                }
                this.n = System.currentTimeMillis() - this.m;
                this.j++;
                if (this.j > 30) {
                    a.b("niosockettask|selectorexception");
                    throw new ClosedChannelException();
                }
                a.a("niosockettask|selectedCount|" + select);
                if (select > 0) {
                    Set<SelectionKey> selectedKeys = this.e.selectedKeys();
                    for (SelectionKey selectionKey : selectedKeys) {
                        if (selectionKey.isValid() && selectionKey.isWritable()) {
                            this.l = b((SocketChannel) selectionKey.channel());
                        }
                        if (selectionKey.isValid() && selectionKey.isReadable()) {
                            a((SocketChannel) selectionKey.channel());
                        }
                    }
                    for (SelectionKey selectionKey2 : selectedKeys) {
                        selectedKeys.remove(selectionKey2);
                    }
                }
                if (!this.p.isEmpty()) {
                    long currentTimeMillis = System.currentTimeMillis();
                    g gVar = (g) this.p.get(0);
                    if (gVar.O == null || !gVar.O.b()) {
                        gVar.o();
                    } else if (gVar.O.a(currentTimeMillis, gVar)) {
                        a.b("niosockettask|timerTasks timeout");
                        gVar.o();
                        gVar.O.a(gVar);
                        throw new SocketTimeoutException("SocketTask do timeOut!");
                    } else {
                        this.l = gVar.O.b(currentTimeMillis, gVar);
                        if (this.l < 0) {
                            this.l = 0;
                        }
                    }
                }
            } catch (Throwable th2) {
                if (l() != null) {
                    l().acquire();
                    a.a("niosockettask|wakelock|select|on");
                }
            }
        }
    }

    public final int b() {
        return -2047;
    }

    final long b(SocketChannel socketChannel) {
        int i = 0;
        a.a("niosockettask|toWrite");
        if (this.o.isEmpty()) {
            return -1;
        }
        long currentTimeMillis = System.currentTimeMillis();
        do {
            int i2;
            ByteBuffer byteBuffer;
            g gVar = (g) this.o.peek();
            a.a("niosockettask|toWrite|" + gVar.getClass().getName() + "|" + gVar.hashCode());
            gVar.d = this.d;
            if (gVar.f != null) {
                i2 = i;
                byteBuffer = gVar.f;
            } else {
                ByteBuffer wrap = ByteBuffer.wrap((byte[]) this.b.d(gVar, this.d, gVar.c));
                gVar.f = wrap;
                i2 = i;
                byteBuffer = wrap;
            }
            while (byteBuffer.hasRemaining()) {
                try {
                    int write = socketChannel.write(byteBuffer);
                    this.j = 0;
                } catch (IOException e) {
                    a.b("niosockettask|write exception|" + e.getMessage());
                    write = -1;
                } catch (Exception e2) {
                    a.b("niosockettask|write exception|" + e2.getMessage());
                    write = -1;
                } catch (Throwable th) {
                    Throwable th2 = th;
                    this.q.b(!this.o.isEmpty());
                }
                if (write > 0) {
                    d c;
                    if (d.f) {
                        c = d.c();
                        c.e += (long) write;
                    } else {
                        c = d.c();
                        c.c += (long) write;
                    }
                } else if (write < 0) {
                    a.b("niosockettask|socketwrite|-1|" + byteBuffer.toString());
                    throw new ClosedChannelException();
                } else {
                    if (write == 0) {
                        write = i2 + 1;
                        if (i2 < Header.DATE) {
                            wait(200);
                            a.a("niosockettask|socketwrite|0|" + byteBuffer.toString());
                            i2 = write;
                        }
                    }
                    a.a("niosockettask|socketwrite|-2|" + byteBuffer.toString());
                    throw new SocketTimeoutException("write data error!");
                }
            }
            gVar = (g) this.o.poll();
            if (gVar.J <= 0 || gVar.O == null) {
                gVar.o();
            } else {
                gVar.a(currentTimeMillis);
                if (this.p.isEmpty()) {
                    this.p.add(gVar);
                } else {
                    gVar.o();
                }
            }
            i = 0;
        } while (!this.o.isEmpty());
        long toMillis = this.p.isEmpty() ? -1 : TimeUnit.SECONDS.toMillis((long) ((g) this.p.get(0)).J);
        this.q.b(!this.o.isEmpty());
        return toMillis;
    }

    public final void d() {
        a.a("niosockettask|initTask");
        super.d();
        this.y = true;
        this.z = true;
        this.T = true;
        this.f.set(false);
    }

    protected void e() {
    }

    public void f() {
        Iterator it;
        try {
            this.q.a();
        } catch (Exception e) {
        }
        if (this.e != null) {
            try {
                this.e.selectNow();
                this.e.close();
            } catch (Exception e2) {
            }
        }
        this.e = null;
        this.f = null;
        if (!this.o.isEmpty()) {
            it = this.o.iterator();
            while (it.hasNext()) {
                ((g) it.next()).o();
            }
            this.o.clear();
        }
        this.o = null;
        if (!this.p.isEmpty()) {
            for (g o : this.p) {
                o.o();
            }
            this.p.clear();
        }
        this.r.clear();
        this.r = null;
        this.g = null;
        this.f = null;
        if (k == this) {
            k = null;
        }
        super.f();
    }

    final void g() {
        if (this.e == null) {
            this.e = Selector.open();
        }
        if (this.q == null) {
            this.q = new d(this.e);
        }
        if (!this.q.b()) {
            this.q.a(this.a);
        }
        this.g.set(false);
        if (this.e.select(15000) > 0) {
            Set<SelectionKey> selectedKeys = this.e.selectedKeys();
            for (SelectionKey selectionKey : selectedKeys) {
                selectedKeys.remove(selectionKey);
                if (selectionKey.isValid() && selectionKey.isConnectable()) {
                    SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
                    if (socketChannel.finishConnect()) {
                        this.h = true;
                        this.d = new com.igexin.a.a.b.e();
                        this.d.a(this.q);
                        Socket socket = socketChannel.socket();
                        a.b("niosockettask|connected|" + (socket.getInetAddress() != null ? socketChannel.socket().getInetAddress().getHostAddress() : "0.0.0.0") + ":" + socketChannel.socket().getPort() + "|" + (socket.getLocalAddress() != null ? socketChannel.socket().getLocalAddress().getHostAddress() : "0.0.0.0") + ":" + socketChannel.socket().getLocalPort());
                    }
                }
            }
            if (this.d != null) {
                this.e.selectNow();
                this.e.close();
                this.e = null;
                this.e = Selector.open();
                this.q.a(this.e);
                this.q.e = this.q.c().register(this.e, 1);
            }
        } else if (this.f.get()) {
            a.a("niosockettask|disConnect event");
            b bVar = new b();
            bVar.a = 2;
            d.c().a((Object) bVar);
            throw new ClosedChannelException();
        } else {
            a.b("niosockettask|connect timeout");
            throw new SocketTimeoutException();
        }
    }

    public void i() {
        if (this.e == null) {
            throw new NullPointerException();
        } else if (!this.e.isOpen()) {
            throw new IllegalStateException();
        } else if (this.g.compareAndSet(false, true)) {
            this.e.wakeup();
        }
    }
}
