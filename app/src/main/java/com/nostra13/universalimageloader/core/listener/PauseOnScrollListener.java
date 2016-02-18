package com.nostra13.universalimageloader.core.listener;

import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import com.nostra13.universalimageloader.core.ImageLoader;
import u.aly.dx;

public class PauseOnScrollListener implements OnScrollListener {
    private final OnScrollListener externalListener;
    private ImageLoader imageLoader;
    private final boolean pauseOnFling;
    private final boolean pauseOnScroll;

    public PauseOnScrollListener(ImageLoader imageLoader, boolean pauseOnScroll, boolean pauseOnFling) {
        this(imageLoader, pauseOnScroll, pauseOnFling, null);
    }

    public PauseOnScrollListener(ImageLoader imageLoader, boolean pauseOnScroll, boolean pauseOnFling, OnScrollListener customListener) {
        this.imageLoader = imageLoader;
        this.pauseOnScroll = pauseOnScroll;
        this.pauseOnFling = pauseOnFling;
        this.externalListener = customListener;
    }

    public void onScrollStateChanged(AbsListView view, int scrollState) {
        switch (scrollState) {
            case dx.a /*0*/:
                this.imageLoader.resume();
                break;
            case dx.b /*1*/:
                if (this.pauseOnScroll) {
                    this.imageLoader.pause();
                    break;
                }
                break;
            case dx.c /*2*/:
                if (this.pauseOnFling) {
                    this.imageLoader.pause();
                    break;
                }
                break;
        }
        if (this.externalListener != null) {
            this.externalListener.onScrollStateChanged(view, scrollState);
        }
    }

    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (this.externalListener != null) {
            this.externalListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
        }
    }
}
