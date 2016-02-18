package com.douban.book.reader.helper;

import android.view.Window;
import android.view.WindowManager.LayoutParams;
import com.douban.book.reader.constant.Key;
import com.douban.book.reader.event.BrightnessChangedEvent;
import com.douban.book.reader.event.EventBusUtils;
import com.douban.book.reader.util.Analysis;
import com.douban.book.reader.util.Logger;
import com.douban.book.reader.util.Pref;
import com.douban.book.reader.util.Tag;
import io.fabric.sdk.android.services.settings.SettingsJsonConstants;

public class BrightnessHelper {
    public int getOverriddenBrightness() {
        int brightness = Pref.ofApp().getInt(Key.APP_OVERRIDDEN_BRIGHTNESS, -1);
        Logger.d(Tag.BRIGHTNESS, "getOverriddenBrightness returned: " + brightness, new Object[0]);
        return brightness;
    }

    public void saveOverriddenBrightness(int brightness) {
        Logger.d(Tag.BRIGHTNESS, "saveOverriddenBrightness: " + brightness, new Object[0]);
        Pref.ofApp().set(Key.APP_OVERRIDDEN_BRIGHTNESS, Integer.valueOf(brightness));
        EventBusUtils.post(new BrightnessChangedEvent());
    }

    public void applyBrightness(Window window) {
        applyOverriddenBrightness(window, isUseSystemBrightness() ? -1 : getOverriddenBrightness());
    }

    public void applyOverriddenBrightness(Window window, int brightness) {
        float value = -1.0f;
        if (brightness >= 0 && brightness <= SettingsJsonConstants.SETTINGS_IDENTIFIER_MASK_DEFAULT) {
            value = Math.max(0.05f, ((float) brightness) / 255.0f);
        }
        Logger.d(Tag.BRIGHTNESS, "applyOverriddenBrightness: " + brightness, new Object[0]);
        LayoutParams lp = window.getAttributes();
        lp.screenBrightness = value;
        lp.buttonBrightness = 0.0f;
        window.setAttributes(lp);
    }

    public void setUseSystemBrightness(boolean useSystemBrightness) {
        Pref.ofApp().set(Key.APP_USE_SYSTEM_BRIGHTNESS, Boolean.valueOf(useSystemBrightness));
        EventBusUtils.post(new BrightnessChangedEvent());
        Analysis.sendPrefChangedEvent("use_system_brightness", Boolean.valueOf(useSystemBrightness));
    }

    public boolean isUseSystemBrightness() {
        return Pref.ofApp().getBoolean(Key.APP_USE_SYSTEM_BRIGHTNESS, true);
    }
}
