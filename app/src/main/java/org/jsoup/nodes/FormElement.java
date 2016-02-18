package org.jsoup.nodes;

import com.douban.book.reader.fragment.DoubanAccountOperationFragment_;
import com.sina.weibo.sdk.register.mobile.SelectCountryActivity;
import com.tencent.open.SocialConstants;
import io.fabric.sdk.android.services.network.HttpRequest;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.jsoup.Connection;
import org.jsoup.Connection.KeyVal;
import org.jsoup.Connection.Method;
import org.jsoup.Jsoup;
import org.jsoup.helper.HttpConnection;
import org.jsoup.helper.Validate;
import org.jsoup.parser.Tag;
import org.jsoup.select.Elements;

public class FormElement extends Element {
    private final Elements elements;

    public FormElement(Tag tag, String baseUri, Attributes attributes) {
        super(tag, baseUri, attributes);
        this.elements = new Elements();
    }

    public Elements elements() {
        return this.elements;
    }

    public FormElement addElement(Element element) {
        this.elements.add(element);
        return this;
    }

    public Connection submit() {
        String action = hasAttr(DoubanAccountOperationFragment_.ACTION_ARG) ? absUrl(DoubanAccountOperationFragment_.ACTION_ARG) : baseUri();
        Validate.notEmpty(action, "Could not determine a form action URL for submit. Ensure you set a base URI when parsing.");
        return Jsoup.connect(action).data(formData()).method(attr("method").toUpperCase().equals(HttpRequest.METHOD_POST) ? Method.POST : Method.GET);
    }

    public List<KeyVal> formData() {
        ArrayList<KeyVal> data = new ArrayList();
        Iterator it = this.elements.iterator();
        while (it.hasNext()) {
            Element el = (Element) it.next();
            if (el.tag().isFormSubmittable() && !el.hasAttr("disabled")) {
                String name = el.attr(SelectCountryActivity.EXTRA_COUNTRY_NAME);
                if (name.length() != 0) {
                    String type = el.attr(SocialConstants.PARAM_TYPE);
                    if ("select".equals(el.tagName())) {
                        boolean set = false;
                        Iterator it2 = el.select("option[selected]").iterator();
                        while (it2.hasNext()) {
                            data.add(HttpConnection.KeyVal.create(name, ((Element) it2.next()).val()));
                            set = true;
                        }
                        if (!set) {
                            Element option = el.select("option").first();
                            if (option != null) {
                                data.add(HttpConnection.KeyVal.create(name, option.val()));
                            }
                        }
                    } else if (!"checkbox".equalsIgnoreCase(type) && !"radio".equalsIgnoreCase(type)) {
                        data.add(HttpConnection.KeyVal.create(name, el.val()));
                    } else if (el.hasAttr("checked")) {
                        data.add(HttpConnection.KeyVal.create(name, el.val().length() > 0 ? el.val() : "on"));
                    }
                }
            }
        }
        return data;
    }
}
