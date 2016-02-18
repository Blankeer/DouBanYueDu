package com.douban.book.reader.util;

import com.douban.book.reader.network.client.JsonClient;
import com.douban.book.reader.network.client.StringClient;
import com.douban.book.reader.network.exception.RestException;
import com.douban.book.reader.network.param.RequestParam;
import java.util.Arrays;
import java.util.List;
import org.json.JSONObject;

public class NetworkUtils {
    public static String getPublicNetworkInfo() {
        try {
            return String.valueOf((JSONObject) new JsonClient("http://wtfismyip.com/json").getEntity());
        } catch (RestException e) {
            return String.format("Error while getting public IP info (%s)", new Object[]{e});
        }
    }

    public static List<String> getIpForDomain(String domain) {
        try {
            return Arrays.asList(((String) new StringClient("http://119.29.29.29/d").get(RequestParam.queryString().append("dn", domain))).split(";"));
        } catch (RestException e) {
            return null;
        }
    }
}
