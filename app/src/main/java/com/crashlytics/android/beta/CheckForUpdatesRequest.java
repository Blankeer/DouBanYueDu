package com.crashlytics.android.beta;

import io.fabric.sdk.android.Fabric;
import io.fabric.sdk.android.Kit;
import io.fabric.sdk.android.services.common.AbstractSpiCall;
import io.fabric.sdk.android.services.network.HttpMethod;
import io.fabric.sdk.android.services.network.HttpRequest;
import io.fabric.sdk.android.services.network.HttpRequestFactory;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONObject;

class CheckForUpdatesRequest extends AbstractSpiCall {
    static final String BETA_SOURCE = "3";
    static final String BUILD_VERSION = "build_version";
    static final String DISPLAY_VERSION = "display_version";
    static final String HEADER_BETA_TOKEN = "X-CRASHLYTICS-BETA-TOKEN";
    static final String INSTANCE = "instance";
    static final String SDK_ANDROID_DIR_TOKEN_TYPE = "3";
    static final String SOURCE = "source";
    private final CheckForUpdatesResponseTransform responseTransform;

    static String createBetaTokenHeaderValueFor(String betaDeviceToken) {
        return "3:" + betaDeviceToken;
    }

    public CheckForUpdatesRequest(Kit kit, String protocolAndHostOverride, String url, HttpRequestFactory requestFactory, CheckForUpdatesResponseTransform responseTransform) {
        super(kit, protocolAndHostOverride, url, requestFactory, HttpMethod.GET);
        this.responseTransform = responseTransform;
    }

    public CheckForUpdatesResponse invoke(String apiKey, String betaDeviceToken, BuildProperties buildProps) {
        HttpRequest httpRequest = null;
        try {
            Map<String, String> queryParams = getQueryParamsFor(buildProps);
            httpRequest = applyHeadersTo(getHttpRequest(queryParams), apiKey, betaDeviceToken);
            Fabric.getLogger().d(Beta.TAG, "Checking for updates from " + getUrl());
            Fabric.getLogger().d(Beta.TAG, "Checking for updates query params are: " + queryParams);
            if (httpRequest.ok()) {
                Fabric.getLogger().d(Beta.TAG, "Checking for updates was successful");
                CheckForUpdatesResponse fromJson = this.responseTransform.fromJson(new JSONObject(httpRequest.body()));
                if (httpRequest == null) {
                    return fromJson;
                }
                Fabric.getLogger().d(Fabric.TAG, "Checking for updates request ID: " + httpRequest.header(AbstractSpiCall.HEADER_REQUEST_ID));
                return fromJson;
            }
            Fabric.getLogger().e(Beta.TAG, "Checking for updates failed. Response code: " + httpRequest.code());
            if (httpRequest != null) {
                Fabric.getLogger().d(Fabric.TAG, "Checking for updates request ID: " + httpRequest.header(AbstractSpiCall.HEADER_REQUEST_ID));
            }
            return null;
        } catch (Exception e) {
            Fabric.getLogger().e(Beta.TAG, "Error while checking for updates from " + getUrl(), e);
            if (httpRequest != null) {
                Fabric.getLogger().d(Fabric.TAG, "Checking for updates request ID: " + httpRequest.header(AbstractSpiCall.HEADER_REQUEST_ID));
            }
        } catch (Throwable th) {
            if (httpRequest != null) {
                Fabric.getLogger().d(Fabric.TAG, "Checking for updates request ID: " + httpRequest.header(AbstractSpiCall.HEADER_REQUEST_ID));
            }
        }
    }

    private HttpRequest applyHeadersTo(HttpRequest request, String apiKey, String betaDeviceToken) {
        return request.header(HttpRequest.HEADER_ACCEPT, HttpRequest.CONTENT_TYPE_JSON).header(HttpRequest.HEADER_USER_AGENT, AbstractSpiCall.CRASHLYTICS_USER_AGENT + this.kit.getVersion()).header(AbstractSpiCall.HEADER_DEVELOPER_TOKEN, AbstractSpiCall.CLS_ANDROID_SDK_DEVELOPER_TOKEN).header(AbstractSpiCall.HEADER_CLIENT_TYPE, AbstractSpiCall.ANDROID_CLIENT_TYPE).header(AbstractSpiCall.HEADER_CLIENT_VERSION, this.kit.getVersion()).header(AbstractSpiCall.HEADER_API_KEY, apiKey).header(HEADER_BETA_TOKEN, createBetaTokenHeaderValueFor(betaDeviceToken));
    }

    private Map<String, String> getQueryParamsFor(BuildProperties buildProps) {
        Map<String, String> queryParams = new HashMap();
        queryParams.put(BUILD_VERSION, buildProps.versionCode);
        queryParams.put(DISPLAY_VERSION, buildProps.versionName);
        queryParams.put(INSTANCE, buildProps.buildId);
        queryParams.put(SOURCE, SDK_ANDROID_DIR_TOKEN_TYPE);
        return queryParams;
    }
}
