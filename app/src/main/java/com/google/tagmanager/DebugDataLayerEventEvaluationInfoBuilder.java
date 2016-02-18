package com.google.tagmanager;

import com.google.analytics.containertag.proto.Debug.DataLayerEventEvaluationInfo;
import com.google.analytics.containertag.proto.Debug.ResolvedFunctionCall;
import com.google.analytics.containertag.proto.Debug.RuleEvaluationStepInfo;

class DebugDataLayerEventEvaluationInfoBuilder implements DataLayerEventEvaluationInfoBuilder {
    private DataLayerEventEvaluationInfo dataLayerEvent;

    public DebugDataLayerEventEvaluationInfoBuilder(DataLayerEventEvaluationInfo dataLayerEvent) {
        this.dataLayerEvent = dataLayerEvent;
    }

    public ResolvedFunctionCallBuilder createAndAddResult() {
        ResolvedFunctionCall result = new ResolvedFunctionCall();
        this.dataLayerEvent.results = ArrayUtils.appendToArray(this.dataLayerEvent.results, result);
        return new DebugResolvedFunctionCallBuilder(result);
    }

    public RuleEvaluationStepInfoBuilder createRulesEvaluation() {
        this.dataLayerEvent.rulesEvaluation = new RuleEvaluationStepInfo();
        return new DebugRuleEvaluationStepInfoBuilder(this.dataLayerEvent.rulesEvaluation);
    }
}
