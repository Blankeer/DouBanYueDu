package org.jsoup.examples;

import com.sina.weibo.sdk.component.ShareRequestParam;
import io.fabric.sdk.android.services.settings.SettingsJsonConstants;
import java.io.IOException;
import java.util.Iterator;
import org.jsoup.Jsoup;
import org.jsoup.helper.Validate;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class ListLinks {
    public static void main(String[] args) throws IOException {
        Validate.isTrue(args.length == 1, "usage: supply url to fetch");
        print("Fetching %s...", args[0]);
        Document doc = Jsoup.connect(url).get();
        Elements links = doc.select("a[href]");
        Elements media = doc.select("[src]");
        Elements imports = doc.select("link[href]");
        print("\nMedia: (%d)", Integer.valueOf(media.size()));
        Iterator it = media.iterator();
        while (it.hasNext()) {
            if (((Element) it.next()).tagName().equals(ShareRequestParam.REQ_UPLOAD_PIC_PARAM_IMG)) {
                print(" * %s: <%s> %sx%s (%s)", ((Element) it.next()).tagName(), ((Element) it.next()).attr("abs:src"), ((Element) it.next()).attr(SettingsJsonConstants.ICON_WIDTH_KEY), ((Element) it.next()).attr(SettingsJsonConstants.ICON_HEIGHT_KEY), trim(((Element) it.next()).attr("alt"), 20));
            } else {
                print(" * %s: <%s>", ((Element) it.next()).tagName(), ((Element) it.next()).attr("abs:src"));
            }
        }
        print("\nImports: (%d)", Integer.valueOf(imports.size()));
        it = imports.iterator();
        while (it.hasNext()) {
            Element link = (Element) it.next();
            print(" * %s <%s> (%s)", link.tagName(), link.attr("abs:href"), link.attr("rel"));
        }
        print("\nLinks: (%d)", Integer.valueOf(links.size()));
        it = links.iterator();
        while (it.hasNext()) {
            link = (Element) it.next();
            print(" * a: <%s>  (%s)", link.attr("abs:href"), trim(link.text(), 35));
        }
    }

    private static void print(String msg, Object... args) {
        System.out.println(String.format(msg, args));
    }

    private static String trim(String s, int width) {
        if (s.length() > width) {
            return s.substring(0, width - 1) + ".";
        }
        return s;
    }
}
