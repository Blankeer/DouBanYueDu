package de.greenrobot.event;

class AsyncPoster implements Runnable {
    private final EventBus eventBus;
    private final PendingPostQueue queue;

    AsyncPoster(EventBus eventBus) {
        this.eventBus = eventBus;
        this.queue = new PendingPostQueue();
    }

    public void enqueue(Subscription subscription, Object event) {
        this.queue.enqueue(PendingPost.obtainPendingPost(subscription, event));
        this.eventBus.getExecutorService().execute(this);
    }

    public void run() {
        PendingPost pendingPost = this.queue.poll();
        if (pendingPost == null) {
            throw new IllegalStateException("No pending post available");
        }
        this.eventBus.invokeSubscriber(pendingPost);
    }
}
