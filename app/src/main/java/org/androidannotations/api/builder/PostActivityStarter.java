package org.androidannotations.api.builder;

import android.app.Activity;
import android.content.Context;

public final class PostActivityStarter {
    private Context context;

    public PostActivityStarter(Context context) {
        this.context = context;
    }

    public void withAnimation(int enterAnim, int exitAnim) {
        if (this.context instanceof Activity) {
            ((Activity) this.context).overridePendingTransition(enterAnim, exitAnim);
        }
    }
}
