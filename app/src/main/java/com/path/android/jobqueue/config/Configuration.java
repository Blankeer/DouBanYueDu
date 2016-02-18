package com.path.android.jobqueue.config;

import android.content.Context;
import com.path.android.jobqueue.JobManager.DefaultQueueFactory;
import com.path.android.jobqueue.QueueFactory;
import com.path.android.jobqueue.di.DependencyInjector;
import com.path.android.jobqueue.log.CustomLogger;
import com.path.android.jobqueue.network.NetworkUtil;
import com.path.android.jobqueue.network.NetworkUtilImpl;
import com.path.android.jobqueue.persistentQueue.sqlite.SqliteJobQueue.JobSerializer;

public class Configuration {
    public static final String DEFAULT_ID = "default_job_manager";
    public static final int DEFAULT_LOAD_FACTOR_PER_CONSUMER = 3;
    public static final int DEFAULT_THREAD_KEEP_ALIVE_SECONDS = 15;
    public static final int MAX_CONSUMER_COUNT = 5;
    public static final int MIN_CONSUMER_COUNT = 0;
    private int consumerKeepAlive;
    private CustomLogger customLogger;
    private DependencyInjector dependencyInjector;
    private String id;
    private int loadFactor;
    private int maxConsumerCount;
    private int minConsumerCount;
    private NetworkUtil networkUtil;
    private QueueFactory queueFactory;

    public static final class Builder {
        private Context appContext;
        private Configuration configuration;

        public Builder(Context context) {
            this.configuration = new Configuration();
            this.appContext = context.getApplicationContext();
        }

        public Builder id(String id) {
            this.configuration.id = id;
            return this;
        }

        public Builder consumerKeepAlive(int keepAlive) {
            this.configuration.consumerKeepAlive = keepAlive;
            return this;
        }

        public Builder queueFactory(QueueFactory queueFactory) {
            if (this.configuration.queueFactory != null) {
                throw new RuntimeException("already set a queue factory. This might happen if you've provided a custom job serializer");
            }
            this.configuration.queueFactory = queueFactory;
            return this;
        }

        public Builder jobSerializer(JobSerializer jobSerializer) {
            this.configuration.queueFactory = new DefaultQueueFactory(jobSerializer);
            return this;
        }

        public Builder networkUtil(NetworkUtil networkUtil) {
            this.configuration.networkUtil = networkUtil;
            return this;
        }

        public Builder injector(DependencyInjector injector) {
            this.configuration.dependencyInjector = injector;
            return this;
        }

        public Builder maxConsumerCount(int count) {
            this.configuration.maxConsumerCount = count;
            return this;
        }

        public Builder minConsumerCount(int count) {
            this.configuration.minConsumerCount = count;
            return this;
        }

        public Builder customLogger(CustomLogger logger) {
            this.configuration.customLogger = logger;
            return this;
        }

        public Builder loadFactor(int loadFactor) {
            this.configuration.loadFactor = loadFactor;
            return this;
        }

        public Configuration build() {
            if (this.configuration.queueFactory == null) {
                this.configuration.queueFactory = new DefaultQueueFactory();
            }
            if (this.configuration.networkUtil == null) {
                this.configuration.networkUtil = new NetworkUtilImpl(this.appContext);
            }
            return this.configuration;
        }
    }

    private Configuration() {
        this.id = DEFAULT_ID;
        this.maxConsumerCount = MAX_CONSUMER_COUNT;
        this.minConsumerCount = 0;
        this.consumerKeepAlive = DEFAULT_THREAD_KEEP_ALIVE_SECONDS;
        this.loadFactor = DEFAULT_LOAD_FACTOR_PER_CONSUMER;
    }

    public String getId() {
        return this.id;
    }

    public QueueFactory getQueueFactory() {
        return this.queueFactory;
    }

    public DependencyInjector getDependencyInjector() {
        return this.dependencyInjector;
    }

    public int getConsumerKeepAlive() {
        return this.consumerKeepAlive;
    }

    public NetworkUtil getNetworkUtil() {
        return this.networkUtil;
    }

    public int getMaxConsumerCount() {
        return this.maxConsumerCount;
    }

    public int getMinConsumerCount() {
        return this.minConsumerCount;
    }

    public CustomLogger getCustomLogger() {
        return this.customLogger;
    }

    public int getLoadFactor() {
        return this.loadFactor;
    }
}
