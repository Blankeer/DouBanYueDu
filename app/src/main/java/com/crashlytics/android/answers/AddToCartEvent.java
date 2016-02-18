package com.crashlytics.android.answers;

import com.path.android.jobqueue.JobManager;
import java.math.BigDecimal;
import java.util.Currency;

public class AddToCartEvent extends PredefinedEvent<AddToCartEvent> {
    static final String CURRENCY_ATTRIBUTE = "currency";
    static final String ITEM_ID_ATTRIBUTE = "itemId";
    static final String ITEM_NAME_ATTRIBUTE = "itemName";
    static final String ITEM_PRICE_ATTRIBUTE = "itemPrice";
    static final String ITEM_TYPE_ATTRIBUTE = "itemType";
    static final BigDecimal MICRO_CONSTANT;
    static final String TYPE = "addToCart";

    static {
        MICRO_CONSTANT = BigDecimal.valueOf(JobManager.NS_PER_MS);
    }

    public AddToCartEvent putItemId(String itemId) {
        this.predefinedAttributes.put(ITEM_ID_ATTRIBUTE, itemId);
        return this;
    }

    public AddToCartEvent putItemName(String itemName) {
        this.predefinedAttributes.put(ITEM_NAME_ATTRIBUTE, itemName);
        return this;
    }

    public AddToCartEvent putItemType(String itemType) {
        this.predefinedAttributes.put(ITEM_TYPE_ATTRIBUTE, itemType);
        return this;
    }

    public AddToCartEvent putItemPrice(BigDecimal itemPrice) {
        if (!this.validator.isNull(itemPrice, ITEM_PRICE_ATTRIBUTE)) {
            this.predefinedAttributes.put(ITEM_PRICE_ATTRIBUTE, Long.valueOf(priceToMicros(itemPrice)));
        }
        return this;
    }

    public AddToCartEvent putCurrency(Currency currency) {
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
