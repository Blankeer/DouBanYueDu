package com.google.tagmanager;

import com.google.analytics.containertag.proto.Debug.DataLayerEventEvaluationInfo;
import com.google.analytics.containertag.proto.Debug.EventInfo;
import com.google.analytics.containertag.proto.Debug.MacroEvaluationInfo;
import com.google.android.gms.common.util.VisibleForTesting;

class DebugEventInfoBuilder implements EventInfoBuilder {
    private DebugDataLayerEventEvaluationInfoBuilder dataLayerEventBuilder;
    @VisibleForTesting
    EventInfo eventInfoBuilder;
    private DebugInformationHandler handler;
    private DebugMacroEvaluationInfoBuilder macroBuilder;

    public DebugEventInfoBuilder(int eventType, String containerVersion, String containerId, String key, DebugInformationHandler handler) {
        this.eventInfoBuilder = new EventInfo();
        this.eventInfoBuilder.eventType = eventType;
        this.eventInfoBuilder.containerVersion = containerVersion;
        this.eventInfoBuilder.containerId = containerId;
        this.eventInfoBuilder.key = key;
        this.handler = handler;
        if (eventType == 1) {
            this.eventInfoBuilder.dataLayerEventResult = new DataLayerEventEvaluationInfo();
            this.dataLayerEventBuilder = new DebugDataLayerEventEvaluationInfoBuilder(this.eventInfoBuilder.dataLayerEventResult);
            return;
        }
        this.eventInfoBuilder.macroResult = new MacroEvaluationInfo();
        this.macroBuilder = new DebugMacroEvaluationInfoBuilder(this.eventInfoBuilder.macroResult);
    }

    public MacroEvaluationInfoBuilder createMacroEvaluationInfoBuilder() {
        return this.macroBuilder;
    }

    public DataLayerEventEvaluationInfoBuilder createDataLayerEventEvaluationInfoBuilder() {
        return this.dataLayerEventBuilder;
    }

    public void processEventInfo() {
        this.handler.receiveEventInfo(this.eventInfoBuilder);
    }
}
