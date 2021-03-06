package de.greenrobot.event;

import io.fabric.sdk.android.services.common.AbstractSpiCall;
import java.util.ArrayList;
import java.util.List;

final class PendingPost {
    private static final List<PendingPost> pendingPostPool;
    Object event;
    PendingPost next;
    Subscription subscription;

    static {
        pendingPostPool = new ArrayList();
    }

    private PendingPost(Object event, Subscription subscription) {
        this.event = event;
        this.subscription = subscription;
    }

    static PendingPost obtainPendingPost(Subscription subscription, Object event) {
        synchronized (pendingPostPool) {
            int size = pendingPostPool.size();
            if (size > 0) {
                PendingPost pendingPost = (PendingPost) pendingPostPool.remove(size - 1);
                pendingPost.event = event;
                pendingPost.subscription = subscription;
                pendingPost.next = null;
                return pendingPost;
            }
            return new PendingPost(event, subscription);
        }
    }

    static void releasePendingPost(PendingPost pendingPost) {
        pendingPost.event = null;
        pendingPost.subscription = null;
        pendingPost.next = null;
        synchronized (pendingPostPool) {
            if (pendingPostPool.size() < AbstractSpiCall.DEFAULT_TIMEOUT) {
                pendingPostPool.add(pendingPost);
            }
        }
    }
}
