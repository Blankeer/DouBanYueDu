package com.igexin.a.a.b.a.a;

import com.igexin.a.a.b.b;
import com.igexin.a.a.b.g;
import com.igexin.a.a.d.a.a;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

public final class d implements b, a {
    SocketChannel a;
    boolean b;
    long c;
    SelectionKey d;
    SelectionKey e;
    Selector f;

    public d(Selector selector) {
        this.f = selector;
        System.setProperty("java.net.preferIPv6Addresses", "false");
    }

    public void a() {
        try {
            a(false);
        } catch (Exception e) {
        }
        this.a = null;
        this.f = null;
    }

    final void a(Selector selector) {
        this.f = selector;
    }

    public final void a(boolean z) {
        if (z) {
            this.b = true;
            return;
        }
        try {
            if (this.a != null) {
                this.a.close();
            }
            if (this.d != null) {
                this.d.cancel();
                this.d.attach(null);
            }
            this.d = null;
            if (this.e != null) {
                this.e.cancel();
                this.e.attach(null);
            }
            this.e = null;
        } catch (Throwable th) {
            if (this.d != null) {
                this.d.cancel();
                this.d.attach(null);
            }
            this.d = null;
            if (this.e != null) {
                this.e.cancel();
                this.e.attach(null);
            }
            this.e = null;
        }
    }

    public final boolean a(String str) {
        if (this.b) {
            throw new IllegalStateException();
        }
        SocketChannel c = c();
        String[] a = g.a(str);
        SocketAddress inetSocketAddress = new InetSocketAddress(a[1], Integer.parseInt(a[2]));
        this.d = c.register(this.f, 8);
        this.c = System.currentTimeMillis();
        c.connect(inetSocketAddress);
        Socket socket = c.socket();
        InetAddress localAddress = socket.getLocalAddress();
        com.igexin.a.a.c.a.b("niosockettask|connecting|" + inetSocketAddress.getAddress().getHostAddress() + ":" + inetSocketAddress.getPort() + "|" + (localAddress != null ? localAddress.getHostAddress() : "0.0.0.0") + ":" + socket.getLocalPort());
        this.a = c;
        return true;
    }

    final void b(boolean z) {
        if (this.e != null && this.e.isValid()) {
            int interestOps = this.e.interestOps();
            this.e.interestOps(z ? interestOps | 4 : interestOps & -5);
        }
    }

    public final boolean b() {
        return (this.a == null || this.b || !this.a.isOpen()) ? false : this.a.isConnected() || this.a.isConnectionPending();
    }

    SocketChannel c() {
        if (this.a != null && this.a.isOpen()) {
            return this.a;
        }
        SocketChannel open = SocketChannel.open();
        open.configureBlocking(false);
        Socket socket = open.socket();
        socket.setTcpNoDelay(true);
        socket.setSoLinger(true, 0);
        socket.setSoTimeout(15000);
        return open;
    }
}
