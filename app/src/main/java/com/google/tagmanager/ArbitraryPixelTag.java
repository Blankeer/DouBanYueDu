package com.google.tagmanager;

import android.content.Context;
import android.net.Uri;
import android.net.Uri.Builder;
import com.google.analytics.containertag.common.FunctionType;
import com.google.analytics.containertag.common.Key;
import com.google.analytics.midtier.proto.containertag.TypeSystem.Value;
import com.google.android.gms.common.util.VisibleForTesting;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

class ArbitraryPixelTag extends TrackingTag {
    private static final String ADDITIONAL_PARAMS;
    static final String ARBITRARY_PIXEL_UNREPEATABLE;
    private static final String ID;
    private static final String UNREPEATABLE;
    private static final String URL;
    private static final Set<String> unrepeatableIds;
    private final Context mContext;
    private final HitSenderProvider mHitSenderProvider;

    public interface HitSenderProvider {
        HitSender get();
    }

    /* renamed from: com.google.tagmanager.ArbitraryPixelTag.1 */
    class AnonymousClass1 implements HitSenderProvider {
        final /* synthetic */ Context val$context;

        AnonymousClass1(Context context) {
            this.val$context = context;
        }

        public HitSender get() {
            return DelayedHitSender.getInstance(this.val$context);
        }
    }

    static {
        ID = FunctionType.ARBITRARY_PIXEL.toString();
        URL = Key.URL.toString();
        ADDITIONAL_PARAMS = Key.ADDITIONAL_PARAMS.toString();
        UNREPEATABLE = Key.UNREPEATABLE.toString();
        ARBITRARY_PIXEL_UNREPEATABLE = "gtm_" + ID + "_unrepeatable";
        unrepeatableIds = new HashSet();
    }

    public static String getFunctionId() {
        return ID;
    }

    public ArbitraryPixelTag(Context context) {
        this(context, new AnonymousClass1(context));
    }

    @VisibleForTesting
    ArbitraryPixelTag(Context context, HitSenderProvider hitSenderProvider) {
        super(ID, URL);
        this.mHitSenderProvider = hitSenderProvider;
        this.mContext = context;
    }

    public void evaluateTrackingTag(Map<String, Value> tag) {
        String unrepeatableId;
        if (tag.get(UNREPEATABLE) != null) {
            unrepeatableId = Types.valueToString((Value) tag.get(UNREPEATABLE));
        } else {
            unrepeatableId = null;
        }
        if (unrepeatableId == null || !idProcessed(unrepeatableId)) {
            Builder uriBuilder = Uri.parse(Types.valueToString((Value) tag.get(URL))).buildUpon();
            Value additionalParamsList = (Value) tag.get(ADDITIONAL_PARAMS);
            if (additionalParamsList != null) {
                List<Object> l = Types.valueToObject(additionalParamsList);
                if (l instanceof List) {
                    for (Map<Object, Object> m : l) {
                        if (m instanceof Map) {
                            for (Entry<Object, Object> entry : m.entrySet()) {
                                uriBuilder.appendQueryParameter(entry.getKey().toString(), entry.getValue().toString());
                            }
                        } else {
                            Log.e("ArbitraryPixel: additional params contains non-map: not sending partial hit: " + uriBuilder.build().toString());
                            return;
                        }
                    }
                }
                Log.e("ArbitraryPixel: additional params not a list: not sending partial hit: " + uriBuilder.build().toString());
                return;
            }
            String uri = uriBuilder.build().toString();
            this.mHitSenderProvider.get().sendHit(uri);
            Log.v("ArbitraryPixel: url = " + uri);
            if (unrepeatableId != null) {
                synchronized (ArbitraryPixelTag.class) {
                    unrepeatableIds.add(unrepeatableId);
                    SharedPreferencesUtil.saveAsync(this.mContext, ARBITRARY_PIXEL_UNREPEATABLE, unrepeatableId, "true");
                }
            }
        }
    }

    private synchronized boolean idProcessed(String unrepeatableId) {
        boolean z = true;
        synchronized (this) {
            if (!idInCache(unrepeatableId)) {
                if (idInSharedPreferences(unrepeatableId)) {
                    unrepeatableIds.add(unrepeatableId);
                } else {
                    z = false;
                }
            }
        }
        return z;
    }

    @VisibleForTesting
    boolean idInSharedPreferences(String unrepeatableId) {
        return this.mContext.getSharedPreferences(ARBITRARY_PIXEL_UNREPEATABLE, 0).contains(unrepeatableId);
    }

    @VisibleForTesting
    void clearCache() {
        unrepeatableIds.clear();
    }

    @VisibleForTesting
    boolean idInCache(String unrepeatableId) {
        return unrepeatableIds.contains(unrepeatableId);
    }
}
