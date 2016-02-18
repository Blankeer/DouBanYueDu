package com.google.tagmanager;

import android.content.Context;
import com.google.analytics.containertag.common.Key;
import com.google.analytics.containertag.proto.Serving.Supplemental;
import com.google.analytics.midtier.proto.containertag.TypeSystem.Value;
import com.google.android.gms.common.util.VisibleForTesting;
import com.google.tagmanager.CacheFactory.CacheSizeManager;
import com.google.tagmanager.CustomFunctionCall.CustomEvaluator;
import com.google.tagmanager.ResourceUtil.ExpandedFunctionCall;
import com.google.tagmanager.ResourceUtil.ExpandedResource;
import com.google.tagmanager.ResourceUtil.ExpandedRule;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import u.aly.ci;
import u.aly.dx;

class Runtime {
    static final String DEFAULT_RULE_NAME = "Unknown";
    private static final ObjectAndStatic<Value> DEFAULT_VALUE_AND_STATIC;
    static final String EXPERIMENT_SUPPLEMENTAL_NAME_PREFIX = "gaExperiment:";
    private static final int MAX_CACHE_SIZE = 1048576;
    private final EventInfoDistributor eventInfoDistributor;
    private volatile String mCurrentEventName;
    private final DataLayer mDataLayer;
    private final Cache<ExpandedFunctionCall, ObjectAndStatic<Value>> mFunctionCallCache;
    private final Cache<String, CachedMacro> mMacroEvaluationCache;
    private final Map<String, MacroInfo> mMacroLookup;
    private final Map<String, FunctionCallImplementation> mMacroMap;
    private final Map<String, FunctionCallImplementation> mPredicateMap;
    private final ExpandedResource mResource;
    private final Set<ExpandedRule> mRules;
    private final Map<String, FunctionCallImplementation> mTrackingTagMap;

    interface AddRemoveSetPopulator {
        void rulePassed(ExpandedRule expandedRule, Set<ExpandedFunctionCall> set, Set<ExpandedFunctionCall> set2, ResolvedRuleBuilder resolvedRuleBuilder);
    }

    private static class CachedMacro {
        private ObjectAndStatic<Value> mObjectAndStatic;
        private Value mPushAfterEvaluate;

        public CachedMacro(ObjectAndStatic<Value> objectAndStatic) {
            this(objectAndStatic, null);
        }

        public CachedMacro(ObjectAndStatic<Value> objectAndStatic, Value pushAfterEvaluate) {
            this.mObjectAndStatic = objectAndStatic;
            this.mPushAfterEvaluate = pushAfterEvaluate;
        }

        public ObjectAndStatic<Value> getObjectAndStatic() {
            return this.mObjectAndStatic;
        }

        public Value getPushAfterEvaluate() {
            return this.mPushAfterEvaluate;
        }

        public int getSize() {
            return (this.mPushAfterEvaluate == null ? 0 : this.mPushAfterEvaluate.getCachedSize()) + ((Value) this.mObjectAndStatic.getObject()).getCachedSize();
        }
    }

    private static class MacroInfo {
        private final Map<ExpandedRule, List<String>> mAddMacroNames;
        private final Map<ExpandedRule, List<ExpandedFunctionCall>> mAddMacros;
        private ExpandedFunctionCall mDefault;
        private final Map<ExpandedRule, List<String>> mRemoveMacroNames;
        private final Map<ExpandedRule, List<ExpandedFunctionCall>> mRemoveMacros;
        private final Set<ExpandedRule> mRules;

        public MacroInfo() {
            this.mRules = new HashSet();
            this.mAddMacros = new HashMap();
            this.mAddMacroNames = new HashMap();
            this.mRemoveMacros = new HashMap();
            this.mRemoveMacroNames = new HashMap();
        }

        public Set<ExpandedRule> getRules() {
            return this.mRules;
        }

        public void addRule(ExpandedRule rule) {
            this.mRules.add(rule);
        }

        public Map<ExpandedRule, List<ExpandedFunctionCall>> getAddMacros() {
            return this.mAddMacros;
        }

        public Map<ExpandedRule, List<String>> getAddMacroRuleNames() {
            return this.mAddMacroNames;
        }

        public Map<ExpandedRule, List<String>> getRemoveMacroRuleNames() {
            return this.mRemoveMacroNames;
        }

        public void addAddMacroForRule(ExpandedRule rule, ExpandedFunctionCall function) {
            List<ExpandedFunctionCall> result = (List) this.mAddMacros.get(rule);
            if (result == null) {
                result = new ArrayList();
                this.mAddMacros.put(rule, result);
            }
            result.add(function);
        }

        public void addAddMacroRuleNameForRule(ExpandedRule rule, String ruleName) {
            List<String> result = (List) this.mAddMacroNames.get(rule);
            if (result == null) {
                result = new ArrayList();
                this.mAddMacroNames.put(rule, result);
            }
            result.add(ruleName);
        }

        public Map<ExpandedRule, List<ExpandedFunctionCall>> getRemoveMacros() {
            return this.mRemoveMacros;
        }

        public void addRemoveMacroForRule(ExpandedRule rule, ExpandedFunctionCall function) {
            List<ExpandedFunctionCall> result = (List) this.mRemoveMacros.get(rule);
            if (result == null) {
                result = new ArrayList();
                this.mRemoveMacros.put(rule, result);
            }
            result.add(function);
        }

        public void addRemoveMacroRuleNameForRule(ExpandedRule rule, String ruleName) {
            List<String> result = (List) this.mRemoveMacroNames.get(rule);
            if (result == null) {
                result = new ArrayList();
                this.mRemoveMacroNames.put(rule, result);
            }
            result.add(ruleName);
        }

        public ExpandedFunctionCall getDefault() {
            return this.mDefault;
        }

        public void setDefault(ExpandedFunctionCall def) {
            this.mDefault = def;
        }
    }

    /* renamed from: com.google.tagmanager.Runtime.3 */
    class AnonymousClass3 implements AddRemoveSetPopulator {
        final /* synthetic */ Map val$addMacroRuleNames;
        final /* synthetic */ Map val$addMacros;
        final /* synthetic */ Map val$removeMacroRuleNames;
        final /* synthetic */ Map val$removeMacros;

        AnonymousClass3(Map map, Map map2, Map map3, Map map4) {
            this.val$addMacros = map;
            this.val$addMacroRuleNames = map2;
            this.val$removeMacros = map3;
            this.val$removeMacroRuleNames = map4;
        }

        public void rulePassed(ExpandedRule rule, Set<ExpandedFunctionCall> add, Set<ExpandedFunctionCall> remove, ResolvedRuleBuilder debugRuleBuilder) {
            List<ExpandedFunctionCall> thisAddMacro = (List) this.val$addMacros.get(rule);
            List<String> thisMacroEnablingRuleNames = (List) this.val$addMacroRuleNames.get(rule);
            if (thisAddMacro != null) {
                add.addAll(thisAddMacro);
                debugRuleBuilder.getAddedMacroFunctions().translateAndAddAll(thisAddMacro, thisMacroEnablingRuleNames);
            }
            List<ExpandedFunctionCall> thisRemoveMacro = (List) this.val$removeMacros.get(rule);
            List<String> thisRemoveMacroRuleNames = (List) this.val$removeMacroRuleNames.get(rule);
            if (thisRemoveMacro != null) {
                remove.addAll(thisRemoveMacro);
                debugRuleBuilder.getRemovedMacroFunctions().translateAndAddAll(thisRemoveMacro, thisRemoveMacroRuleNames);
            }
        }
    }

    static {
        DEFAULT_VALUE_AND_STATIC = new ObjectAndStatic(Types.getDefaultValue(), true);
    }

    public Runtime(Context context, ExpandedResource resource, DataLayer dataLayer, CustomEvaluator functionCallMacroEvaluator, CustomEvaluator functionCallTagEvaluator, EventInfoDistributor eventInfoDistributor) {
        if (resource == null) {
            throw new NullPointerException("resource cannot be null");
        }
        this.mResource = resource;
        this.mRules = new HashSet(resource.getRules());
        this.mDataLayer = dataLayer;
        this.eventInfoDistributor = eventInfoDistributor;
        this.mFunctionCallCache = new CacheFactory().createCache(MAX_CACHE_SIZE, new CacheSizeManager<ExpandedFunctionCall, ObjectAndStatic<Value>>() {
            public int sizeOf(ExpandedFunctionCall key, ObjectAndStatic<Value> value) {
                return ((Value) value.getObject()).getCachedSize();
            }
        });
        this.mMacroEvaluationCache = new CacheFactory().createCache(MAX_CACHE_SIZE, new CacheSizeManager<String, CachedMacro>() {
            public int sizeOf(String key, CachedMacro value) {
                return key.length() + value.getSize();
            }
        });
        this.mTrackingTagMap = new HashMap();
        addTrackingTag(new ArbitraryPixelTag(context));
        addTrackingTag(new CustomFunctionCall(functionCallTagEvaluator));
        addTrackingTag(new DataLayerWriteTag(dataLayer));
        addTrackingTag(new UniversalAnalyticsTag(context, dataLayer));
        this.mPredicateMap = new HashMap();
        addPredicate(new ContainsPredicate());
        addPredicate(new EndsWithPredicate());
        addPredicate(new EqualsPredicate());
        addPredicate(new GreaterEqualsPredicate());
        addPredicate(new GreaterThanPredicate());
        addPredicate(new LessEqualsPredicate());
        addPredicate(new LessThanPredicate());
        addPredicate(new RegexPredicate());
        addPredicate(new StartsWithPredicate());
        this.mMacroMap = new HashMap();
        addMacro(new AdvertiserIdMacro(context));
        addMacro(new AdvertisingTrackingEnabledMacro());
        addMacro(new AdwordsClickReferrerMacro(context));
        addMacro(new AppIdMacro(context));
        addMacro(new AppNameMacro(context));
        addMacro(new AppVersionMacro(context));
        addMacro(new ConstantMacro());
        addMacro(new ContainerVersionMacro(this));
        addMacro(new CustomFunctionCall(functionCallMacroEvaluator));
        addMacro(new DataLayerMacro(dataLayer));
        addMacro(new DeviceIdMacro(context));
        addMacro(new DeviceNameMacro());
        addMacro(new EncodeMacro());
        addMacro(new EventMacro(this));
        addMacro(new GtmVersionMacro());
        addMacro(new HashMacro());
        addMacro(new InstallReferrerMacro(context));
        addMacro(new JoinerMacro());
        addMacro(new LanguageMacro());
        addMacro(new MobileAdwordsUniqueIdMacro(context));
        addMacro(new OsVersionMacro());
        addMacro(new PlatformMacro());
        addMacro(new RandomMacro());
        addMacro(new RegexGroupMacro());
        addMacro(new ResolutionMacro(context));
        addMacro(new RuntimeVersionMacro());
        addMacro(new SdkVersionMacro());
        addMacro(new TimeMacro());
        this.mMacroLookup = new HashMap();
        for (ExpandedRule rule : this.mRules) {
            if (eventInfoDistributor.debugMode()) {
                verifyFunctionAndNameListSizes(rule.getAddMacros(), rule.getAddMacroRuleNames(), "add macro");
                verifyFunctionAndNameListSizes(rule.getRemoveMacros(), rule.getRemoveMacroRuleNames(), "remove macro");
                verifyFunctionAndNameListSizes(rule.getAddTags(), rule.getAddTagRuleNames(), "add tag");
                verifyFunctionAndNameListSizes(rule.getRemoveTags(), rule.getRemoveTagRuleNames(), "remove tag");
            }
            int i = 0;
            while (i < rule.getAddMacros().size()) {
                ExpandedFunctionCall function = (ExpandedFunctionCall) rule.getAddMacros().get(i);
                String ruleName = DEFAULT_RULE_NAME;
                if (eventInfoDistributor.debugMode() && i < rule.getAddMacroRuleNames().size()) {
                    ruleName = (String) rule.getAddMacroRuleNames().get(i);
                }
                MacroInfo info = getOrAddMacroInfo(this.mMacroLookup, getFunctionName(function));
                info.addRule(rule);
                info.addAddMacroForRule(rule, function);
                info.addAddMacroRuleNameForRule(rule, ruleName);
                i++;
            }
            i = 0;
            while (i < rule.getRemoveMacros().size()) {
                function = (ExpandedFunctionCall) rule.getRemoveMacros().get(i);
                ruleName = DEFAULT_RULE_NAME;
                if (eventInfoDistributor.debugMode() && i < rule.getRemoveMacroRuleNames().size()) {
                    ruleName = (String) rule.getRemoveMacroRuleNames().get(i);
                }
                info = getOrAddMacroInfo(this.mMacroLookup, getFunctionName(function));
                info.addRule(rule);
                info.addRemoveMacroForRule(rule, function);
                info.addRemoveMacroRuleNameForRule(rule, ruleName);
                i++;
            }
        }
        for (Entry<String, List<ExpandedFunctionCall>> ent : this.mResource.getAllMacros().entrySet()) {
            for (ExpandedFunctionCall function2 : (List) ent.getValue()) {
                if (!Types.valueToBoolean((Value) function2.getProperties().get(Key.NOT_DEFAULT_MACRO.toString())).booleanValue()) {
                    getOrAddMacroInfo(this.mMacroLookup, (String) ent.getKey()).setDefault(function2);
                }
            }
        }
    }

    public Runtime(Context context, ExpandedResource resource, DataLayer dataLayer, CustomEvaluator functionCallMacroEvaluator, CustomEvaluator functionCallTagEvaluator) {
        this(context, resource, dataLayer, functionCallMacroEvaluator, functionCallTagEvaluator, new NoopEventInfoDistributor());
    }

    public ExpandedResource getResource() {
        return this.mResource;
    }

    public synchronized void setSupplementals(List<Supplemental> supplementals) {
        for (Supplemental supplemental : supplementals) {
            if (supplemental.name == null || !supplemental.name.startsWith(EXPERIMENT_SUPPLEMENTAL_NAME_PREFIX)) {
                Log.v("Ignored supplemental: " + supplemental);
            } else {
                ExperimentMacroHelper.handleExperimentSupplemental(this.mDataLayer, supplemental);
            }
        }
    }

    public synchronized void evaluateTags(String currentEventName) {
        setCurrentEventName(currentEventName);
        EventInfoBuilder eventInfoBuilder = this.eventInfoDistributor.createDataLayerEventEvaluationEventInfo(currentEventName);
        DataLayerEventEvaluationInfoBuilder debugDataLayerBuilder = eventInfoBuilder.createDataLayerEventEvaluationInfoBuilder();
        for (ExpandedFunctionCall tag : (Set) calculateTagsToRun(this.mRules, debugDataLayerBuilder.createRulesEvaluation()).getObject()) {
            executeFunction(this.mTrackingTagMap, tag, new HashSet(), debugDataLayerBuilder.createAndAddResult());
        }
        eventInfoBuilder.processEventInfo();
        setCurrentEventName(null);
    }

    public ObjectAndStatic<Value> evaluateMacroReference(String macroName) {
        EventInfoBuilder eventInfoBuilder = this.eventInfoDistributor.createMacroEvalutionEventInfo(macroName);
        ObjectAndStatic<Value> result = evaluateMacroReferenceCycleDetection(macroName, new HashSet(), eventInfoBuilder.createMacroEvaluationInfoBuilder());
        eventInfoBuilder.processEventInfo();
        return result;
    }

    @VisibleForTesting
    synchronized void setCurrentEventName(String currentEventName) {
        this.mCurrentEventName = currentEventName;
    }

    synchronized String getCurrentEventName() {
        return this.mCurrentEventName;
    }

    @VisibleForTesting
    ObjectAndStatic<Set<ExpandedFunctionCall>> calculateMacrosToRun(String macroName, Set<ExpandedRule> rules, Map<ExpandedRule, List<ExpandedFunctionCall>> addMacros, Map<ExpandedRule, List<String>> addMacroRuleNames, Map<ExpandedRule, List<ExpandedFunctionCall>> removeMacros, Map<ExpandedRule, List<String>> removeMacroRuleNames, Set<String> pendingMacroExpansions, RuleEvaluationStepInfoBuilder debugRulesEvaluation) {
        return calculateGenericToRun(rules, pendingMacroExpansions, new AnonymousClass3(addMacros, addMacroRuleNames, removeMacros, removeMacroRuleNames), debugRulesEvaluation);
    }

    @VisibleForTesting
    ObjectAndStatic<Set<ExpandedFunctionCall>> calculateTagsToRun(Set<ExpandedRule> rules, RuleEvaluationStepInfoBuilder debugRulesEvaluation) {
        return calculateGenericToRun(rules, new HashSet(), new AddRemoveSetPopulator() {
            public void rulePassed(ExpandedRule rule, Set<ExpandedFunctionCall> add, Set<ExpandedFunctionCall> remove, ResolvedRuleBuilder debugRuleBuilder) {
                add.addAll(rule.getAddTags());
                remove.addAll(rule.getRemoveTags());
                debugRuleBuilder.getAddedTagFunctions().translateAndAddAll(rule.getAddTags(), rule.getAddTagRuleNames());
                debugRuleBuilder.getRemovedTagFunctions().translateAndAddAll(rule.getRemoveTags(), rule.getRemoveTagRuleNames());
            }
        }, debugRulesEvaluation);
    }

    private static MacroInfo getOrAddMacroInfo(Map<String, MacroInfo> macroLookup, String key) {
        MacroInfo result = (MacroInfo) macroLookup.get(key);
        if (result != null) {
            return result;
        }
        result = new MacroInfo();
        macroLookup.put(key, result);
        return result;
    }

    private ObjectAndStatic<Set<ExpandedFunctionCall>> calculateGenericToRun(Set<ExpandedRule> rules, Set<String> pendingMacroExpansions, AddRemoveSetPopulator setPopulator, RuleEvaluationStepInfoBuilder debugRulesEvaluation) {
        Set<ExpandedFunctionCall> add = new HashSet();
        Set<ExpandedFunctionCall> remove = new HashSet();
        boolean allStatic = true;
        for (ExpandedRule rule : rules) {
            ResolvedRuleBuilder debugRuleBuilder = debugRulesEvaluation.createResolvedRuleBuilder();
            ObjectAndStatic<Boolean> result = evaluatePredicatesInRule(rule, pendingMacroExpansions, debugRuleBuilder);
            if (((Boolean) result.getObject()).booleanValue()) {
                setPopulator.rulePassed(rule, add, remove, debugRuleBuilder);
            }
            allStatic = allStatic && result.isStatic();
        }
        add.removeAll(remove);
        debugRulesEvaluation.setEnabledFunctions(add);
        return new ObjectAndStatic(add, allStatic);
    }

    private static String getFunctionName(ExpandedFunctionCall functionCall) {
        return Types.valueToString((Value) functionCall.getProperties().get(Key.INSTANCE_NAME.toString()));
    }

    private static void addFunctionImplToMap(Map<String, FunctionCallImplementation> addTo, FunctionCallImplementation impl) {
        if (addTo.containsKey(impl.getInstanceFunctionId())) {
            throw new IllegalArgumentException("Duplicate function type name: " + impl.getInstanceFunctionId());
        }
        addTo.put(impl.getInstanceFunctionId(), impl);
    }

    @VisibleForTesting
    void addMacro(FunctionCallImplementation macro) {
        addFunctionImplToMap(this.mMacroMap, macro);
    }

    @VisibleForTesting
    void addTrackingTag(FunctionCallImplementation tag) {
        addFunctionImplToMap(this.mTrackingTagMap, tag);
    }

    @VisibleForTesting
    void addPredicate(FunctionCallImplementation predicate) {
        addFunctionImplToMap(this.mPredicateMap, predicate);
    }

    @VisibleForTesting
    ObjectAndStatic<Boolean> evaluatePredicate(ExpandedFunctionCall predicate, Set<String> pendingMacroExpansions, ResolvedFunctionCallBuilder debugFunctionCall) {
        ObjectAndStatic<Value> result = executeFunction(this.mPredicateMap, predicate, pendingMacroExpansions, debugFunctionCall);
        Boolean predicateResult = Types.valueToBoolean((Value) result.getObject());
        debugFunctionCall.setFunctionResult(Types.objectToValue(predicateResult));
        return new ObjectAndStatic(predicateResult, result.isStatic());
    }

    @VisibleForTesting
    ObjectAndStatic<Boolean> evaluatePredicatesInRule(ExpandedRule rule, Set<String> pendingMacroExpansions, ResolvedRuleBuilder debugRuleBuilder) {
        boolean allStatic = true;
        for (ExpandedFunctionCall f : rule.getNegativePredicates()) {
            ObjectAndStatic<Boolean> result = evaluatePredicate(f, pendingMacroExpansions, debugRuleBuilder.createNegativePredicate());
            if (((Boolean) result.getObject()).booleanValue()) {
                debugRuleBuilder.setValue(Types.objectToValue(Boolean.valueOf(false)));
                return new ObjectAndStatic(Boolean.valueOf(false), result.isStatic());
            } else if (allStatic && result.isStatic()) {
                allStatic = true;
            } else {
                allStatic = false;
            }
        }
        for (ExpandedFunctionCall f2 : rule.getPositivePredicates()) {
            result = evaluatePredicate(f2, pendingMacroExpansions, debugRuleBuilder.createPositivePredicate());
            if (!((Boolean) result.getObject()).booleanValue()) {
                debugRuleBuilder.setValue(Types.objectToValue(Boolean.valueOf(false)));
                return new ObjectAndStatic(Boolean.valueOf(false), result.isStatic());
            } else if (allStatic && result.isStatic()) {
                allStatic = true;
            } else {
                allStatic = false;
            }
        }
        debugRuleBuilder.setValue(Types.objectToValue(Boolean.valueOf(true)));
        return new ObjectAndStatic(Boolean.valueOf(true), allStatic);
    }

    private ObjectAndStatic<Value> evaluateMacroReferenceCycleDetection(String macroName, Set<String> pendingMacroExpansions, MacroEvaluationInfoBuilder debugMacroEvaluation) {
        CachedMacro cachedResult = (CachedMacro) this.mMacroEvaluationCache.get(macroName);
        if (cachedResult == null || this.eventInfoDistributor.debugMode()) {
            MacroInfo macroInfo = (MacroInfo) this.mMacroLookup.get(macroName);
            if (macroInfo == null) {
                Log.e("Invalid macro: " + macroName);
                return DEFAULT_VALUE_AND_STATIC;
            }
            ExpandedFunctionCall macro;
            ObjectAndStatic<Set<ExpandedFunctionCall>> macrosToRun = calculateMacrosToRun(macroName, macroInfo.getRules(), macroInfo.getAddMacros(), macroInfo.getAddMacroRuleNames(), macroInfo.getRemoveMacros(), macroInfo.getRemoveMacroRuleNames(), pendingMacroExpansions, debugMacroEvaluation.createRulesEvaluation());
            if (((Set) macrosToRun.getObject()).isEmpty()) {
                macro = macroInfo.getDefault();
            } else {
                if (((Set) macrosToRun.getObject()).size() > 1) {
                    Log.w("Multiple macros active for macroName " + macroName);
                }
                macro = (ExpandedFunctionCall) ((Set) macrosToRun.getObject()).iterator().next();
            }
            if (macro == null) {
                return DEFAULT_VALUE_AND_STATIC;
            }
            ObjectAndStatic<Value> toReturn;
            ObjectAndStatic<Value> macroResult = executeFunction(this.mMacroMap, macro, pendingMacroExpansions, debugMacroEvaluation.createResult());
            boolean isStatic = macrosToRun.isStatic() && macroResult.isStatic();
            if (macroResult == DEFAULT_VALUE_AND_STATIC) {
                toReturn = DEFAULT_VALUE_AND_STATIC;
            } else {
                ObjectAndStatic<Value> objectAndStatic = new ObjectAndStatic(macroResult.getObject(), isStatic);
            }
            Value pushAfterEvaluate = macro.getPushAfterEvaluate();
            if (toReturn.isStatic()) {
                this.mMacroEvaluationCache.put(macroName, new CachedMacro(toReturn, pushAfterEvaluate));
            }
            pushUnevaluatedValueToDataLayer(pushAfterEvaluate, pendingMacroExpansions);
            return toReturn;
        }
        pushUnevaluatedValueToDataLayer(cachedResult.getPushAfterEvaluate(), pendingMacroExpansions);
        return cachedResult.getObjectAndStatic();
    }

    private void pushUnevaluatedValueToDataLayer(Value pushAfterEvaluate, Set<String> pendingMacroExpansions) {
        if (pushAfterEvaluate != null) {
            ObjectAndStatic<Value> evaluatedPush = macroExpandValue(pushAfterEvaluate, pendingMacroExpansions, new NoopValueBuilder());
            if (evaluatedPush != DEFAULT_VALUE_AND_STATIC) {
                Map<Object, Object> pushValueAsObject = Types.valueToObject((Value) evaluatedPush.getObject());
                if (pushValueAsObject instanceof Map) {
                    this.mDataLayer.push(pushValueAsObject);
                } else if (pushValueAsObject instanceof List) {
                    for (Object item : (List) pushValueAsObject) {
                        if (item instanceof Map) {
                            this.mDataLayer.push((Map) item);
                        } else {
                            Log.w("pushAfterEvaluate: value not a Map");
                        }
                    }
                } else {
                    Log.w("pushAfterEvaluate: value not a Map or List");
                }
            }
        }
    }

    private ObjectAndStatic<Value> macroExpandValue(Value value, Set<String> pendingMacroExpansions, ValueBuilder debugValueBuilder) {
        if (!value.containsReferences) {
            return new ObjectAndStatic(value, true);
        }
        Value newValue;
        int i;
        ObjectAndStatic<Value> macroExpand;
        switch (value.type) {
            case dx.c /*2*/:
                newValue = ResourceUtil.newValueBasedOnValue(value);
                newValue.listItem = new Value[value.listItem.length];
                for (i = 0; i < value.listItem.length; i++) {
                    macroExpand = macroExpandValue(value.listItem[i], pendingMacroExpansions, debugValueBuilder.getListItem(i));
                    if (macroExpand == DEFAULT_VALUE_AND_STATIC) {
                        return DEFAULT_VALUE_AND_STATIC;
                    }
                    newValue.listItem[i] = (Value) macroExpand.getObject();
                }
                return new ObjectAndStatic(newValue, false);
            case dx.d /*3*/:
                newValue = ResourceUtil.newValueBasedOnValue(value);
                if (value.mapKey.length != value.mapValue.length) {
                    Log.e("Invalid serving value: " + value.toString());
                    return DEFAULT_VALUE_AND_STATIC;
                }
                newValue.mapKey = new Value[value.mapKey.length];
                newValue.mapValue = new Value[value.mapKey.length];
                for (i = 0; i < value.mapKey.length; i++) {
                    ObjectAndStatic<Value> macroExpandKey = macroExpandValue(value.mapKey[i], pendingMacroExpansions, debugValueBuilder.getMapKey(i));
                    ObjectAndStatic<Value> macroExpandValue = macroExpandValue(value.mapValue[i], pendingMacroExpansions, debugValueBuilder.getMapValue(i));
                    if (macroExpandKey == DEFAULT_VALUE_AND_STATIC || macroExpandValue == DEFAULT_VALUE_AND_STATIC) {
                        return DEFAULT_VALUE_AND_STATIC;
                    }
                    newValue.mapKey[i] = (Value) macroExpandKey.getObject();
                    newValue.mapValue[i] = (Value) macroExpandValue.getObject();
                }
                return new ObjectAndStatic(newValue, false);
            case dx.e /*4*/:
                if (pendingMacroExpansions.contains(value.macroReference)) {
                    Log.e("Macro cycle detected.  Current macro reference: " + value.macroReference + "." + "  Previous macro references: " + pendingMacroExpansions.toString() + ".");
                    return DEFAULT_VALUE_AND_STATIC;
                }
                pendingMacroExpansions.add(value.macroReference);
                ObjectAndStatic<Value> result = ValueEscapeUtil.applyEscapings(evaluateMacroReferenceCycleDetection(value.macroReference, pendingMacroExpansions, debugValueBuilder.createValueMacroEvaluationInfoExtension()), value.escaping);
                pendingMacroExpansions.remove(value.macroReference);
                return result;
            case ci.h /*7*/:
                newValue = ResourceUtil.newValueBasedOnValue(value);
                newValue.templateToken = new Value[value.templateToken.length];
                for (i = 0; i < value.templateToken.length; i++) {
                    macroExpand = macroExpandValue(value.templateToken[i], pendingMacroExpansions, debugValueBuilder.getTemplateToken(i));
                    if (macroExpand == DEFAULT_VALUE_AND_STATIC) {
                        return DEFAULT_VALUE_AND_STATIC;
                    }
                    newValue.templateToken[i] = (Value) macroExpand.getObject();
                }
                return new ObjectAndStatic(newValue, false);
            default:
                Log.e("Unknown type: " + value.type);
                return DEFAULT_VALUE_AND_STATIC;
        }
    }

    private ObjectAndStatic<Value> executeFunction(Map<String, FunctionCallImplementation> implMap, ExpandedFunctionCall functionCall, Set<String> pendingMacroExpansions, ResolvedFunctionCallBuilder debugFunctionCall) {
        Value functionIdValue = (Value) functionCall.getProperties().get(Key.FUNCTION.toString());
        if (functionIdValue == null) {
            Log.e("No function id in properties");
            return DEFAULT_VALUE_AND_STATIC;
        }
        String functionId = functionIdValue.functionId;
        FunctionCallImplementation impl = (FunctionCallImplementation) implMap.get(functionId);
        if (impl == null) {
            Log.e(functionId + " has no backing implementation.");
            return DEFAULT_VALUE_AND_STATIC;
        }
        ObjectAndStatic<Value> cachedResult = (ObjectAndStatic) this.mFunctionCallCache.get(functionCall);
        if (cachedResult != null && !this.eventInfoDistributor.debugMode()) {
            return cachedResult;
        }
        Map<String, Value> expandedParams = new HashMap();
        boolean allParamsStatic = true;
        for (Entry<String, Value> originalParam : functionCall.getProperties().entrySet()) {
            ObjectAndStatic<Value> result = macroExpandValue((Value) originalParam.getValue(), pendingMacroExpansions, debugFunctionCall.createResolvedPropertyBuilder((String) originalParam.getKey()).createPropertyValueBuilder((Value) originalParam.getValue()));
            if (result == DEFAULT_VALUE_AND_STATIC) {
                return DEFAULT_VALUE_AND_STATIC;
            }
            if (result.isStatic()) {
                functionCall.updateCacheableProperty((String) originalParam.getKey(), (Value) result.getObject());
            } else {
                allParamsStatic = false;
            }
            expandedParams.put(originalParam.getKey(), result.getObject());
        }
        if (impl.hasRequiredKeys(expandedParams.keySet())) {
            boolean cacheable = allParamsStatic && impl.isCacheable();
            result = new ObjectAndStatic(impl.evaluate(expandedParams), cacheable);
            if (cacheable) {
                this.mFunctionCallCache.put(functionCall, result);
            }
            debugFunctionCall.setFunctionResult((Value) result.getObject());
            return result;
        }
        Log.e("Incorrect keys for function " + functionId + " required " + impl.getRequiredKeys() + " had " + expandedParams.keySet());
        return DEFAULT_VALUE_AND_STATIC;
    }

    private static void verifyFunctionAndNameListSizes(List<ExpandedFunctionCall> functionList, List<String> ruleNameList, String operation) {
        if (functionList.size() != ruleNameList.size()) {
            Log.i("Invalid resource: imbalance of rule names of functions for " + operation + " operation. Using default rule name instead");
        }
    }
}
