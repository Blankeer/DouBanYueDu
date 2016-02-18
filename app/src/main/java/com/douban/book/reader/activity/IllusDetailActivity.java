package com.douban.book.reader.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import com.douban.book.reader.R;
import com.douban.book.reader.constant.Constants;
import com.douban.book.reader.content.Book;
import com.douban.book.reader.content.Book.ImageSize;
import com.douban.book.reader.content.paragraph.IllusParagraph;
import com.douban.book.reader.content.paragraph.Paragraph;
import com.douban.book.reader.manager.WorksManager;
import com.douban.book.reader.manager.exception.DataLoadException;
import com.douban.book.reader.util.Font;
import com.douban.book.reader.util.ImageLoaderUtils;
import com.douban.book.reader.util.Logger;
import com.douban.book.reader.util.ReaderUri;
import com.douban.book.reader.view.ParagraphView;
import com.douban.book.reader.view.TouchImageView;
import com.mcxiaoke.next.ui.widget.AdvancedShareActionProvider;

public class IllusDetailActivity extends BaseBrightnessOverridingActivity {
    private static final String KEY_PANEL_OPEN = "key_panel_open";
    private static final String KEY_PANEL_SHOW = "key_panel_show";
    Button mBtnLegendHide;
    TouchImageView mIvIllus;
    RelativeLayout mLegendSwitchBg;
    int mLineLimit;
    boolean mPanelOpenState;
    boolean mPanelShowState;
    boolean mShowLegend;
    ScrollView mSvPanel;
    ParagraphView mTvLegend;
    TextView mTvLegendSwitch;

    public IllusDetailActivity() {
        this.mPanelOpenState = true;
        this.mPanelShowState = true;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            this.mPanelOpenState = savedInstanceState.getBoolean(KEY_PANEL_OPEN, false);
            this.mPanelShowState = savedInstanceState.getBoolean(KEY_PANEL_SHOW, true);
        }
        setContentView((int) R.layout.act_illus);
        Intent intent = getIntent();
        int bookId = intent.getIntExtra(Constants.KEY_BOOK_ID, 0);
        int packageId = intent.getIntExtra(Constants.KEY_PACKAGE_ID, 0);
        int seq = intent.getIntExtra(Constants.KEY_ILLUS_SEQ, 0);
        int chapter = intent.getIntExtra(Constants.KEY_CHAPTER_INDEX, 0);
        int index = intent.getIntExtra(Constants.KEY_SECTION_INDEX, 0);
        try {
            this.mShowLegend = !WorksManager.getInstance().getWorks(bookId).isGallery();
        } catch (DataLoadException e) {
            Logger.e(this.TAG, e);
        }
        this.mIvIllus = (TouchImageView) findViewById(R.id.image_illus);
        this.mIvIllus.setMaxZoom(4.0f);
        ImageLoaderUtils.displayImageSkippingDiscCache(ReaderUri.illus(bookId, packageId, seq, ImageSize.LARGE).toString(), this.mIvIllus);
        this.mIvIllus.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                IllusDetailActivity.this.finish();
                IllusDetailActivity.this.overridePendingTransition(0, 17432577);
            }
        });
        this.mBtnLegendHide = (Button) findViewById(R.id.text_legend_hide);
        this.mTvLegend = (ParagraphView) findViewById(R.id.text_legend);
        this.mTvLegendSwitch = (TextView) findViewById(R.id.text_legend_switch);
        this.mLegendSwitchBg = (RelativeLayout) findViewById(R.id.text_legend_switch_bg);
        this.mSvPanel = (ScrollView) findViewById(R.id.text_legend_scroller);
        if (this.mShowLegend) {
            this.mBtnLegendHide.setTypeface(Font.SANS_SERIF);
            this.mBtnLegendHide.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    IllusDetailActivity.this.mPanelShowState = !IllusDetailActivity.this.mPanelShowState;
                    IllusDetailActivity.this.updatePanelState();
                }
            });
            if (getResources().getConfiguration().orientation == 2) {
                this.mLineLimit = 2;
            } else {
                this.mLineLimit = 3;
            }
            Paragraph paragraph = Book.get(bookId).getParagraph(chapter, index);
            if (paragraph != null && (paragraph instanceof IllusParagraph)) {
                this.mTvLegend.setParagraphText(((IllusParagraph) paragraph).getPrintableFullLegend());
                if (TextUtils.isEmpty(this.mTvLegend.getText())) {
                    this.mPanelShowState = false;
                    this.mBtnLegendHide.setVisibility(8);
                } else {
                    this.mTvLegend.post(new Runnable() {
                        public void run() {
                            if (IllusDetailActivity.this.mTvLegend.getLineCount() > IllusDetailActivity.this.mLineLimit) {
                                IllusDetailActivity.this.mTvLegendSwitch.setVisibility(0);
                            } else {
                                IllusDetailActivity.this.mTvLegendSwitch.setVisibility(8);
                            }
                        }
                    });
                }
            }
            this.mLegendSwitchBg.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    IllusDetailActivity.this.mPanelOpenState = !IllusDetailActivity.this.mPanelOpenState;
                    IllusDetailActivity.this.updatePanelState();
                }
            });
            this.mTvLegend.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    IllusDetailActivity.this.mPanelOpenState = !IllusDetailActivity.this.mPanelOpenState;
                    IllusDetailActivity.this.updatePanelState();
                }
            });
        } else {
            this.mPanelShowState = false;
            this.mBtnLegendHide.setVisibility(8);
        }
        updatePanelState();
    }

    private void updatePanelState() {
        if (this.mPanelShowState) {
            this.mSvPanel.setVisibility(0);
            this.mBtnLegendHide.setText(R.string.text_hide_panel);
        } else {
            this.mSvPanel.setVisibility(8);
            this.mBtnLegendHide.setText(R.string.text_show_panel);
        }
        if (this.mPanelOpenState) {
            this.mTvLegend.setVisibleLineCount(AdvancedShareActionProvider.WEIGHT_MAX);
            this.mTvLegendSwitch.setText(R.string.text_fold_panel);
            this.mTvLegendSwitch.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_pic_text_close, 0);
            return;
        }
        this.mTvLegend.setVisibleLineCount(this.mLineLimit);
        this.mTvLegendSwitch.setText(R.string.text_unfold_panel);
        this.mTvLegendSwitch.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_pic_text, 0);
    }

    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (this.mShowLegend) {
            outState.putBoolean(KEY_PANEL_OPEN, this.mPanelOpenState);
            outState.putBoolean(KEY_PANEL_SHOW, this.mPanelShowState);
        }
    }

    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, 17432577);
    }
}
