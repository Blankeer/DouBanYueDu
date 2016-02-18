package com.google.tagmanager;

import com.google.analytics.containertag.proto.Debug.ResolvedRule;
import com.google.analytics.containertag.proto.Debug.RuleEvaluationStepInfo;
import com.google.tagmanager.ResourceUtil.ExpandedFunctionCall;
import java.util.Set;

class DebugRuleEvaluationStepInfoBuilder implements RuleEvaluationStepInfoBuilder {
    private RuleEvaluationStepInfo ruleEvaluationStepInfo;

    public DebugRuleEvaluationStepInfoBuilder(RuleEvaluationStepInfo ruleEvaluationStepInfo) {
        this.ruleEvaluationStepInfo = ruleEvaluationStepInfo;
    }

    public void setEnabledFunctions(Set<ExpandedFunctionCall> enabledFunctions) {
        for (ExpandedFunctionCall enabledFunction : enabledFunctions) {
            this.ruleEvaluationStepInfo.enabledFunctions = ArrayUtils.appendToArray(this.ruleEvaluationStepInfo.enabledFunctions, DebugResolvedRuleBuilder.translateExpandedFunctionCall(enabledFunction));
        }
    }

    public ResolvedRuleBuilder createResolvedRuleBuilder() {
        ResolvedRule rule = new ResolvedRule();
        this.ruleEvaluationStepInfo.rules = ArrayUtils.appendToArray(this.ruleEvaluationStepInfo.rules, rule);
        return new DebugResolvedRuleBuilder(rule);
    }
}
