package com.sina.weibo.sdk.register.mobile;

import com.sina.weibo.sdk.exception.WeiboException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CountryList implements Serializable {
    private static final long serialVersionUID = 1;
    public List<Country> countries;

    public CountryList(String jsonStr) {
        initFromJsonStr(jsonStr);
    }

    private void initFromJsonStr(String jsonStr) {
        try {
            initFromJsonObject(new JSONObject(jsonStr));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void initFromJsonObject(JSONObject jsonObj) throws WeiboException {
        try {
            this.countries = new ArrayList();
            Iterator<String> keys = jsonObj.keys();
            while (keys.hasNext()) {
                String countryName = (String) keys.next();
                JSONObject jsonCountry = jsonObj.optJSONObject(countryName);
                String countryCode = jsonCountry.getString(SelectCountryActivity.EXTRA_COUNTRY_CODE);
                JSONArray mcc = jsonCountry.optJSONObject("rule").optJSONArray("mcc");
                String[] mccArray = new String[mcc.length()];
                for (int i = 0; i < mcc.length(); i++) {
                    mccArray[i] = mcc.getString(i);
                }
                Country country = new Country(countryName, countryCode);
                country.setMccs(mccArray);
                this.countries.add(country);
            }
        } catch (Throwable e1) {
            e1.printStackTrace();
            throw new WeiboException(e1);
        }
    }
}
