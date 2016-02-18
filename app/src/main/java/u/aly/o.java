package u.aly;

import android.content.Context;
import android.support.v4.media.TransportMediator;
import com.douban.book.reader.util.WorksIdentity;
import com.umeng.analytics.AnalyticsConfig;
import com.umeng.analytics.a;
import io.realm.internal.Table;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/* compiled from: EventTracker */
public class o {
    private final int a;
    private final int b;
    private m c;
    private Context d;
    private l e;

    public o(Context context) {
        this.a = TransportMediator.FLAG_KEY_MEDIA_NEXT;
        this.b = WorksIdentity.ID_BIT_FINALIZE;
        if (context == null) {
            throw new RuntimeException("Context is null, can't track event");
        }
        this.d = context.getApplicationContext();
        this.c = new m(this.d);
        this.c.a(!AnalyticsConfig.ENABLE_MEMORY_BUFFER);
        this.e = l.a(this.d);
    }

    public void a(String str, Map<String, Object> map, long j) {
        try {
            if (a(str) && a((Map) map)) {
                this.e.a(new af(str, map, j, -1));
            }
        } catch (Exception e) {
            bt.b(a.e, "Exception occurred in Mobclick.onEvent(). ", e);
        }
    }

    public void a(String str, String str2, long j, int i) {
        if (a(str) && b(str2)) {
            Map hashMap = new HashMap();
            if (str2 == null) {
                str2 = Table.STRING_DEFAULT_VALUE;
            }
            hashMap.put(str, str2);
            this.e.a(new af(str, hashMap, j, i));
        }
    }

    public void a(String str, Map<String, Object> map) {
        try {
            if (a(str)) {
                this.e.a(new ah(str, map));
            }
        } catch (Exception e) {
            bt.b(a.e, "Exception occurred in Mobclick.onEvent(). ", e);
        }
    }

    public void a(String str, String str2) {
        if (a(str) && b(str2)) {
            this.c.a(af.b(str, str2, null), af.a(str, str2, null));
        }
    }

    public void b(String str, String str2) {
        if (a(str) && b(str2)) {
            ae b = this.c.b(af.b(str, str2, null));
            if (b != null) {
                a(str, str2, (long) ((int) (System.currentTimeMillis() - b.a)), 0);
            }
        }
    }

    public void a(String str, Map<String, Object> map, String str2) {
        if (a(str) && a((Map) map)) {
            this.c.a(af.b(str, str2, map), af.a(str, str2, map));
        }
    }

    public void c(String str, String str2) {
        if (a(str)) {
            ae b = this.c.b(af.b(str, str2, null));
            if (b != null) {
                a(str, b.d, (long) ((int) (System.currentTimeMillis() - b.a)));
            }
        }
    }

    private boolean a(String str) {
        if (str != null) {
            int length = str.trim().getBytes().length;
            if (length > 0 && length <= TransportMediator.FLAG_KEY_MEDIA_NEXT) {
                return true;
            }
        }
        bt.b(a.e, "Event id is empty or too long in tracking Event");
        return false;
    }

    private boolean b(String str) {
        if (str == null || str.trim().getBytes().length <= WorksIdentity.ID_BIT_FINALIZE) {
            return true;
        }
        bt.b(a.e, "Event label or value is empty or too long in tracking Event");
        return false;
    }

    private boolean a(Map<String, Object> map) {
        if (map == null || map.isEmpty()) {
            bt.b(a.e, "map is null or empty in onEvent");
            return false;
        }
        for (Entry entry : map.entrySet()) {
            if (!a((String) entry.getKey())) {
                return false;
            }
            if (entry.getValue() == null) {
                return false;
            }
            if ((entry.getValue() instanceof String) && !b(entry.getValue().toString())) {
                return false;
            }
        }
        return true;
    }
}
