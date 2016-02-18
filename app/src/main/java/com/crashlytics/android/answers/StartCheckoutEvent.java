package com.crashlytics.android.answers;

import com.path.android.jobqueue.JobManager;
import java.math.BigDecimal;
import java.util.Currency;

public class StartCheckoutEvent extends PredefinedEvent<StartCheckoutEvent> {
    static final String CURRENCY_ATTRIBUTE = "currency";
    static final String ITEM_COUNT_ATTRIBUTE = "itemCount";
    static final BigDecimal MICRO_CONSTANT;
    static final String TOTAL_PRICE_ATTRIBUTE = "totalPrice";
    static final String TYPE = "startCheckout";

    static {
        MICRO_CONSTANT = BigDecimal.valueOf(JobManager.NS_PER_MS);
    }

    public StartCheckoutEvent putItemCount(int itemCount) {
        this.predefinedAttributes.put(ITEM_COUNT_ATTRIBUTE, Integer.valueOf(itemCount));
        return this;
    }

    public StartCheckoutEvent putTotalPrice(BigDecimal totalPrice) {
        if (!this.validator.isNull(totalPrice, TOTAL_PRICE_ATTRIBUTE)) {
            this.predefinedAttributes.put(TOTAL_PRICE_ATTRIBUTE, Long.valueOf(priceToMicros(totalPrice)));
        }
        return this;
    }

    public StartCheckoutEvent putCurrency(Currency currency) {
        if (!this.validator.isNull(currency, CURRENCY_ATTRIBUTE)) {
            this.predefinedAttributes.put(CURRENCY_ATTRIBUTE, currency.getCurrencyCode());
        }
        return this;
    }

    long priceToMicros(BigDecimal decimal) {
        return MICRO_CONSTANT.multiply(decimal).longValue();
    }

    String getPredefinedType() {
        return TYPE;
    }
}
