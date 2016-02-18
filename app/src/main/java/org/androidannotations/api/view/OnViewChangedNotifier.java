package org.androidannotations.api.view;

import java.util.LinkedHashSet;
import java.util.Set;

public class OnViewChangedNotifier {
    private static OnViewChangedNotifier currentNotifier;
    private final Set<OnViewChangedListener> listeners;

    public OnViewChangedNotifier() {
        this.listeners = new LinkedHashSet();
    }

    public static OnViewChangedNotifier replaceNotifier(OnViewChangedNotifier notifier) {
        OnViewChangedNotifier previousNotifier = currentNotifier;
        currentNotifier = notifier;
        return previousNotifier;
    }

    public static void registerOnViewChangedListener(OnViewChangedListener listener) {
        if (currentNotifier != null) {
            currentNotifier.listeners.add(listener);
        }
    }

    public void notifyViewChanged(HasViews hasViews) {
        for (OnViewChangedListener listener : this.listeners) {
            listener.onViewChanged(hasViews);
        }
    }
}
