package com.douban.book.reader.service;

import android.content.Context;
import org.androidannotations.api.builder.ServiceIntentBuilder;

public final class ExecutePendingRequestsService_ extends ExecutePendingRequestsService {

    public static class IntentBuilder_ extends ServiceIntentBuilder<IntentBuilder_> {
        public IntentBuilder_(Context context) {
            super(context, ExecutePendingRequestsService_.class);
        }
    }

    public static IntentBuilder_ intent(Context context) {
        return new IntentBuilder_(context);
    }
}
