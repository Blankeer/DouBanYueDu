package com.douban.book.reader.view.panel;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.SwitchCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import com.douban.book.reader.R;
import com.douban.book.reader.activity.ReaderActivity;
import com.douban.book.reader.content.PageMetrics;
import com.douban.book.reader.content.pack.WorksData;
import com.douban.book.reader.helper.BrightnessHelper;
import com.douban.book.reader.manager.FontScaleManager;
import com.douban.book.reader.manager.WorksManager;
import com.douban.book.reader.task.PagingTaskManager;
import com.douban.book.reader.theme.Theme;
import com.douban.book.reader.util.Res;
import com.douban.book.reader.util.Utils;
import com.douban.book.reader.util.ViewUtils;
import com.douban.book.reader.view.IndexedSeekBar;
import io.fabric.sdk.android.services.settings.SettingsJsonConstants;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

@EViewGroup(2130903138)
public class ReaderSettingView extends LinearLayout {
    private static final int[] FONT_BUTTON_IDS;
    private static final String TAG;
    private BrightnessHelper mBrightnessHelper;
    @ViewById(2131558820)
    IndexedSeekBar mBrightnessSeekBar;
    @ViewById(2131558826)
    RadioGroup mFontGroup;
    @Bean
    FontScaleManager mFontScaleManager;
    @ViewById(2131558821)
    SwitchCompat mSwitchUseSystemBrightness;
    @ViewById(2131558825)
    View mTextThemeDisabledForGallery;
    @ViewById(2131558822)
    RadioGroup mThemeGroup;
    private int mWorksId;
    @Bean
    WorksManager mWorksManager;

    static {
        TAG = ReaderSettingView.class.getSimpleName();
        FONT_BUTTON_IDS = new int[]{R.id.btn_font_small, R.id.btn_font_medium, R.id.btn_font_large, R.id.btn_font_huge};
    }

    public ReaderSettingView(Context context) {
        super(context);
    }

    public ReaderSettingView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ReaderSettingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void worksId(int worksId) {
        this.mWorksId = worksId;
        initBrightness();
        initThemeGroup();
        initFontGroup();
    }

    private void initFontGroup() {
        this.mFontGroup.check(FONT_BUTTON_IDS[this.mFontScaleManager.getScale()]);
        TypedArray array = Res.get().obtainTypedArray(R.array.font_size_content);
        if (array != null) {
            for (int i = 0; i < this.mFontScaleManager.getScaleCount(); i++) {
                ((TextView) this.mFontGroup.findViewById(FONT_BUTTON_IDS[i])).setTextSize(0, array.getDimension(i, (float) Utils.dp2pixel(20.0f)));
            }
            array.recycle();
        }
        this.mFontGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                ReaderSettingView.this.mFontScaleManager.setScale(ReaderSettingView.this.getFontBtnIndex(checkedId));
                if (WorksData.get(ReaderSettingView.this.mWorksId).isReady()) {
                    Activity activity = ViewUtils.getAttachedActivity(ReaderSettingView.this);
                    if (activity instanceof ReaderActivity) {
                        ((ReaderActivity) activity).resetStartPageMethod();
                        PagingTaskManager.foregroundPaging(ReaderSettingView.this.mWorksId, PageMetrics.getFromActivity(activity));
                    }
                }
            }
        });
    }

    private void initThemeGroup() {
        int theme = Theme.getCurrentTheme();
        if (theme == 0) {
            this.mThemeGroup.check(R.id.btn_theme_day);
        } else if (theme == 1) {
            this.mThemeGroup.check(R.id.btn_theme_night);
        }
        this.mThemeGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int checkedTheme = -1;
                if (checkedId == R.id.btn_theme_day) {
                    checkedTheme = 0;
                } else if (checkedId == R.id.btn_theme_night) {
                    checkedTheme = 1;
                }
                if (checkedTheme > -1) {
                    Theme.setColorTheme(checkedTheme);
                }
            }
        });
    }

    private void initBrightness() {
        this.mBrightnessHelper = new BrightnessHelper();
        this.mBrightnessSeekBar.setThumbSize(Utils.dp2pixel(30.0f));
        this.mBrightnessSeekBar.setSliderBgHeight(Utils.dp2pixel(2.0f));
        this.mBrightnessSeekBar.setThumbTextVisible(false);
        this.mBrightnessSeekBar.setMax(SettingsJsonConstants.SETTINGS_IDENTIFIER_MASK_DEFAULT);
        this.mBrightnessSeekBar.setProgress(this.mBrightnessHelper.getOverriddenBrightness());
        this.mBrightnessSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    Activity activity = ViewUtils.getAttachedActivity(ReaderSettingView.this);
                    if (activity != null) {
                        ReaderSettingView.this.mBrightnessHelper.applyOverriddenBrightness(activity.getWindow(), seekBar.getProgress());
                    }
                }
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                ReaderSettingView.this.mSwitchUseSystemBrightness.setChecked(false);
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                ReaderSettingView.this.mBrightnessHelper.saveOverriddenBrightness(seekBar.getProgress());
            }
        });
        this.mSwitchUseSystemBrightness.setChecked(this.mBrightnessHelper.isUseSystemBrightness());
        this.mSwitchUseSystemBrightness.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ReaderSettingView.this.mBrightnessHelper.setUseSystemBrightness(isChecked);
            }
        });
    }

    private int getFontBtnIndex(int btnId) {
        for (int index = 0; index < FONT_BUTTON_IDS.length; index++) {
            if (FONT_BUTTON_IDS[index] == btnId) {
                return index;
            }
        }
        return 0;
    }
}
