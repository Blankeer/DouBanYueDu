package com.igexin.push.core.d;

import com.douban.book.reader.fragment.DoubanAccountOperationFragment_;
import com.douban.book.reader.helper.WorksListUri;
import com.igexin.push.c.c.a;
import com.igexin.push.c.c.e;
import com.igexin.push.core.bean.PushTaskBean;
import com.igexin.push.core.f;
import com.igexin.push.core.g;
import com.igexin.push.e.a.b;
import java.util.Timer;
import org.json.JSONException;
import org.json.JSONObject;

public class c extends b {
    private String a;
    private String b;
    private a c;
    private PushTaskBean d;

    public c(String str, a aVar, PushTaskBean pushTaskBean) {
        super(str);
        this.b = str;
        this.a = pushTaskBean.getMessageId();
        this.c = aVar;
        this.d = pushTaskBean;
    }

    protected void a(PushTaskBean pushTaskBean, a aVar) {
        e cVar = new com.igexin.push.c.c.c();
        cVar.a();
        cVar.c = "RTV" + pushTaskBean.getMessageId() + "@" + pushTaskBean.getTaskId();
        cVar.d = g.s;
        cVar.a = (int) System.currentTimeMillis();
        f.a().g().a("C-" + g.s, cVar);
        com.igexin.a.a.c.a.b("cdnRetrieve|" + pushTaskBean.getMessageId() + "|" + pushTaskBean.getTaskId());
        if (aVar.c() < 2) {
            long k = com.igexin.push.core.a.e.a().k();
            Timer timer = new Timer();
            timer.schedule(new e(this, pushTaskBean, aVar), k);
            g.ak.put(pushTaskBean.getTaskId(), timer);
        }
    }

    public void a(Exception exception) {
        if (this.c.a() < 2) {
            new Timer().schedule(new d(this), com.igexin.push.core.a.e.a().k());
            return;
        }
        a(this.d, this.c);
    }

    public void a(byte[] bArr) {
        if (bArr != null) {
            byte[] b = com.igexin.a.a.b.g.b(com.igexin.a.a.a.a.a(bArr, g.c));
            if (b != null) {
                JSONObject jSONObject = new JSONObject(new String(b, "utf-8"));
                jSONObject.put(WorksListUri.KEY_ID, this.a);
                jSONObject.put("messageid", this.a);
                jSONObject.put("cdnType", true);
                b = null;
                try {
                    if ("pushmessage".equals(jSONObject.getString(DoubanAccountOperationFragment_.ACTION_ARG))) {
                        if (jSONObject.has("extraData")) {
                            b = jSONObject.getString("extraData").getBytes();
                        }
                        com.igexin.push.core.a.e.a().a(jSONObject, b, true);
                        return;
                    }
                    return;
                } catch (JSONException e) {
                    e.printStackTrace();
                    return;
                }
            }
            throw new Exception("Get error CDNData, can not UnGzip it...");
        }
    }

    public int b() {
        return 0;
    }
}
