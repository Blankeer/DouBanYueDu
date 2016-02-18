package com.path.android.jobqueue.di;

import com.path.android.jobqueue.BaseJob;

public interface DependencyInjector {
    void inject(BaseJob baseJob);
}
