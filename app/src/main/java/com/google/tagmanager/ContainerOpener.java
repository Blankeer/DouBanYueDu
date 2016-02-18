package com.google.tagmanager;

import com.google.tagmanager.Container.Callback;
import com.google.tagmanager.Container.RefreshFailure;
import com.google.tagmanager.Container.RefreshType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Semaphore;

public class ContainerOpener {
    public static final long DEFAULT_TIMEOUT_IN_MILLIS = 2000;
    private static final Map<String, List<Notifier>> mContainerIdNotifiersMap;
    private Clock mClock;
    private volatile Container mContainer;
    private final String mContainerId;
    private boolean mHaveNotified;
    private Notifier mNotifier;
    private final TagManager mTagManager;
    private final long mTimeoutInMillis;

    public interface ContainerFuture {
        Container get();

        boolean isDone();
    }

    public interface Notifier {
        void containerAvailable(Container container);
    }

    public enum OpenType {
        PREFER_NON_DEFAULT,
        PREFER_FRESH
    }

    /* renamed from: com.google.tagmanager.ContainerOpener.2 */
    static class AnonymousClass2 implements Notifier {
        final /* synthetic */ ContainerFutureImpl val$future;

        AnonymousClass2(ContainerFutureImpl containerFutureImpl) {
            this.val$future = containerFutureImpl;
        }

        public void containerAvailable(Container container) {
            this.val$future.setContainer(container);
        }
    }

    private static class ContainerFutureImpl implements ContainerFuture {
        private volatile Container mContainer;
        private Semaphore mContainerIsReady;
        private volatile boolean mHaveGotten;

        private ContainerFutureImpl() {
            this.mContainerIsReady = new Semaphore(0);
        }

        public Container get() {
            if (this.mHaveGotten) {
                return this.mContainer;
            }
            try {
                this.mContainerIsReady.acquire();
            } catch (InterruptedException e) {
            }
            this.mHaveGotten = true;
            return this.mContainer;
        }

        public void setContainer(Container container) {
            this.mContainer = container;
            this.mContainerIsReady.release();
        }

        public boolean isDone() {
            return this.mHaveGotten || this.mContainerIsReady.availablePermits() > 0;
        }
    }

    private class WaitForFresh implements Callback {
        private final long mOldestTimeToBeFresh;

        public WaitForFresh(long oldestTimeToBeFresh) {
            this.mOldestTimeToBeFresh = oldestTimeToBeFresh;
        }

        public void containerRefreshBegin(Container container, RefreshType refreshType) {
        }

        public void containerRefreshSuccess(Container container, RefreshType refreshType) {
            if (refreshType == RefreshType.NETWORK || isFresh()) {
                ContainerOpener.this.callNotifiers(container);
            }
        }

        public void containerRefreshFailure(Container container, RefreshType refreshType, RefreshFailure refreshFailure) {
            if (refreshType == RefreshType.NETWORK) {
                ContainerOpener.this.callNotifiers(container);
            }
        }

        private boolean isFresh() {
            return this.mOldestTimeToBeFresh < ContainerOpener.this.mContainer.getLastRefreshTime();
        }
    }

    private class WaitForNonDefaultRefresh implements Callback {
        public void containerRefreshBegin(Container container, RefreshType refreshType) {
        }

        public void containerRefreshSuccess(Container container, RefreshType refreshType) {
            ContainerOpener.this.callNotifiers(container);
        }

        public void containerRefreshFailure(Container container, RefreshType refreshType, RefreshFailure refreshFailure) {
            if (refreshType == RefreshType.NETWORK) {
                ContainerOpener.this.callNotifiers(container);
            }
        }
    }

    static {
        mContainerIdNotifiersMap = new HashMap();
    }

    private ContainerOpener(TagManager tagManager, String containerId, Long timeoutInMillis, Notifier notifier) {
        this.mClock = new Clock() {
            public long currentTimeMillis() {
                return System.currentTimeMillis();
            }
        };
        this.mTagManager = tagManager;
        this.mContainerId = containerId;
        this.mTimeoutInMillis = timeoutInMillis != null ? Math.max(1, timeoutInMillis.longValue()) : DEFAULT_TIMEOUT_IN_MILLIS;
        this.mNotifier = notifier;
    }

    public static void openContainer(TagManager tagManager, String containerId, OpenType openType, Long timeoutInMillis, Notifier notifier) {
        if (tagManager == null) {
            throw new NullPointerException("TagManager cannot be null.");
        } else if (containerId == null) {
            throw new NullPointerException("ContainerId cannot be null.");
        } else if (openType == null) {
            throw new NullPointerException("OpenType cannot be null.");
        } else if (notifier == null) {
            throw new NullPointerException("Notifier cannot be null.");
        } else {
            new ContainerOpener(tagManager, containerId, timeoutInMillis, notifier).open(openType == OpenType.PREFER_FRESH ? RefreshType.NETWORK : RefreshType.SAVED);
        }
    }

    public static ContainerFuture openContainer(TagManager tagManager, String containerId, OpenType openType, Long timeoutInMillis) {
        ContainerFutureImpl future = new ContainerFutureImpl();
        openContainer(tagManager, containerId, openType, timeoutInMillis, new AnonymousClass2(future));
        return future;
    }

    private void open(RefreshType refreshType) {
        long loadStartTime = this.mClock.currentTimeMillis();
        boolean callNotifierImmediately = false;
        synchronized (ContainerOpener.class) {
            this.mContainer = this.mTagManager.getContainer(this.mContainerId);
            List<Notifier> notifiers;
            if (this.mContainer == null) {
                notifiers = new ArrayList();
                notifiers.add(this.mNotifier);
                this.mNotifier = null;
                mContainerIdNotifiersMap.put(this.mContainerId, notifiers);
                this.mContainer = this.mTagManager.openContainer(this.mContainerId, refreshType == RefreshType.SAVED ? new WaitForNonDefaultRefresh() : new WaitForFresh(loadStartTime - 43200000));
            } else {
                notifiers = (List) mContainerIdNotifiersMap.get(this.mContainerId);
                if (notifiers == null) {
                    callNotifierImmediately = true;
                } else {
                    notifiers.add(this.mNotifier);
                    this.mNotifier = null;
                    return;
                }
            }
            if (callNotifierImmediately) {
                this.mNotifier.containerAvailable(this.mContainer);
                this.mNotifier = null;
                return;
            }
            setTimer(Math.max(1, this.mTimeoutInMillis - (this.mClock.currentTimeMillis() - loadStartTime)));
        }
    }

    private void setTimer(long timeoutInMillis) {
        new Timer("ContainerOpener").schedule(new TimerTask() {
            public void run() {
                Log.i("Timer expired.");
                ContainerOpener.this.callNotifiers(ContainerOpener.this.mContainer);
            }
        }, timeoutInMillis);
    }

    private synchronized void callNotifiers(Container container) {
        if (!this.mHaveNotified) {
            List<Notifier> notifiers;
            synchronized (ContainerOpener.class) {
                notifiers = (List) mContainerIdNotifiersMap.remove(this.mContainerId);
            }
            if (notifiers != null) {
                for (Notifier notifier : notifiers) {
                    notifier.containerAvailable(container);
                }
            }
            this.mHaveNotified = true;
        }
    }
}
