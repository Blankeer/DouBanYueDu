package com.crashlytics.android.core;

import io.fabric.sdk.android.Fabric;
import io.fabric.sdk.android.Kit;
import io.fabric.sdk.android.services.common.AbstractSpiCall;
import io.fabric.sdk.android.services.common.ResponseParser;
import io.fabric.sdk.android.services.network.HttpMethod;
import io.fabric.sdk.android.services.network.HttpRequest;
import io.fabric.sdk.android.services.network.HttpRequestFactory;
import java.util.Map.Entry;

class DefaultCreateReportSpiCall extends AbstractSpiCall implements CreateReportSpiCall {
    static final String FILE_CONTENT_TYPE = "application/octet-stream";
    static final String FILE_PARAM = "report[file]";
    static final String IDENTIFIER_PARAM = "report[identifier]";

    public DefaultCreateReportSpiCall(Kit kit, String protocolAndHostOverride, String url, HttpRequestFactory requestFactory) {
        super(kit, protocolAndHostOverride, url, requestFactory, HttpMethod.POST);
    }

    DefaultCreateReportSpiCall(Kit kit, String protocolAndHostOverride, String url, HttpRequestFactory requestFactory, HttpMethod method) {
        super(kit, protocolAndHostOverride, url, requestFactory, method);
    }

    public boolean invoke(CreateReportRequest requestData) {
        HttpRequest httpRequest = applyMultipartDataTo(applyHeadersTo(getHttpRequest(), requestData), requestData);
        Fabric.getLogger().d(CrashlyticsCore.TAG, "Sending report to: " + getUrl());
        int statusCode = httpRequest.code();
        Fabric.getLogger().d(CrashlyticsCore.TAG, "Create report request ID: " + httpRequest.header(AbstractSpiCall.HEADER_REQUEST_ID));
        Fabric.getLogger().d(CrashlyticsCore.TAG, "Result was: " + statusCode);
        return ResponseParser.parse(statusCode) == 0;
    }

    private HttpRequest applyHeadersTo(HttpRequest request, CreateReportRequest requestData) {
        request = request.header(AbstractSpiCall.HEADER_API_KEY, requestData.apiKey).header(AbstractSpiCall.HEADER_CLIENT_TYPE, AbstractSpiCall.ANDROID_CLIENT_TYPE).header(AbstractSpiCall.HEADER_CLIENT_VERSION, CrashlyticsCore.getInstance().getVersion());
        for (Entry entry : requestData.report.getCustomHeaders().entrySet()) {
            request = request.header(entry);
        }
        return request;
    }

    private HttpRequest applyMultipartDataTo(HttpRequest request, CreateReportRequest requestData) {
        Report report = requestData.report;
        return request.part(FILE_PARAM, report.getFileName(), FILE_CONTENT_TYPE, report.getFile()).part(IDENTIFIER_PARAM, report.getIdentifier());
    }
}
