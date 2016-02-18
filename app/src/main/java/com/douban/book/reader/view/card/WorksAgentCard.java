package com.douban.book.reader.view.card;

import android.content.Context;
import android.support.annotation.DimenRes;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import com.douban.book.reader.R;
import com.douban.book.reader.entity.WorksAgent;
import com.douban.book.reader.fragment.WorksAgentFragment_;
import com.douban.book.reader.manager.WorksAgentManager;
import com.douban.book.reader.manager.exception.DataLoadException;
import com.douban.book.reader.util.ImageLoaderUtils;
import com.douban.book.reader.util.Logger;
import com.douban.book.reader.util.Res;
import com.douban.book.reader.util.ViewUtils;
import com.douban.book.reader.view.ParagraphView;
import com.douban.book.reader.view.ParagraphView.Indent;
import com.douban.book.reader.view.ParagraphView.OnExpandStatusChangedListener;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.UiThread;

@EViewGroup
public class WorksAgentCard extends Card<WorksAgentCard> {
    private static final int COLLAPSED_LINE_COUNT = 5;
    private int mAgentId;
    private ImageView mAvatar;
    private ParagraphView mDescription;
    private ImageView mExpandHandle;
    private ImageView mIcArkUser;
    private boolean mLinkEnabled;
    private TextView mName;
    private TextView mPublishInfo;
    private boolean mShowTitle;
    private View mUserInfoLayout;
    @Bean
    WorksAgentManager mWorksAgentManager;

    public WorksAgentCard(Context context) {
        super(context);
        this.mShowTitle = true;
        this.mLinkEnabled = true;
        init();
    }

    private void init() {
        super.content((int) R.layout.card_content_works_agent);
        title((int) R.string.card_title_author);
        this.mUserInfoLayout = findViewById(R.id.user_info_layout);
        this.mAvatar = (ImageView) findViewById(R.id.avatar);
        this.mName = (TextView) findViewById(R.id.name);
        this.mPublishInfo = (TextView) findViewById(R.id.publish_info);
        this.mDescription = (ParagraphView) findViewById(R.id.description);
        this.mExpandHandle = (ImageView) findViewById(R.id.expand_handle);
        this.mIcArkUser = (ImageView) findViewById(R.id.ic_ark_author);
        this.mDescription.setFirstLineIndent(Indent.ALL);
        this.mDescription.setVisibleLineCount(COLLAPSED_LINE_COUNT);
        this.mDescription.setOnExpandStatusChangedListener(new OnExpandStatusChangedListener() {
            public void onExpandedStatusChanged(boolean isExpanded) {
                WorksAgentCard.this.mExpandHandle.setImageResource(isExpanded ? R.drawable.ic_collapse : R.drawable.ic_expand);
            }

            public void onExpandNeededChanged(boolean expandNeeded) {
                ViewUtils.showIf(expandNeeded, WorksAgentCard.this.mExpandHandle);
            }
        });
        this.mDescription.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                WorksAgentCard.this.mDescription.toggleExpandedStatus();
            }
        });
        this.mExpandHandle.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                WorksAgentCard.this.mDescription.toggleExpandedStatus();
            }
        });
    }

    public WorksAgentCard agentId(int agentId) {
        this.mAgentId = agentId;
        loadData();
        return this;
    }

    public WorksAgentCard showTitle(boolean show) {
        this.mShowTitle = show;
        return this;
    }

    public WorksAgentCard linkEnabled(boolean enabled) {
        this.mLinkEnabled = enabled;
        return this;
    }

    public WorksAgentCard topPaddingResId(@DimenRes int resId) {
        ViewUtils.of(this.mUserInfoLayout).topMarginResId(resId).commit();
        return this;
    }

    @Background
    void loadData() {
        try {
            refreshViews((WorksAgent) this.mWorksAgentManager.get((Object) Integer.valueOf(this.mAgentId)));
        } catch (DataLoadException e) {
            Logger.e(this.TAG, e);
            onLoadFailed();
        }
    }

    @UiThread
    void onLoadFailed() {
        hide();
    }

    @UiThread
    void refreshViews(WorksAgent worksAgent) {
        if (worksAgent != null) {
            boolean z;
            ImageLoaderUtils.displayImage(worksAgent.avatar, this.mAvatar);
            if (this.mShowTitle) {
                title(worksAgent.getTitle());
            } else {
                noTitle();
            }
            if (worksAgent.isAgency) {
                z = false;
            } else {
                z = true;
            }
            ViewUtils.showIf(z, this.mIcArkUser);
            this.mName.setText(worksAgent.name);
            this.mPublishInfo.setText(Res.getString(R.string.agent_publish_info, Integer.valueOf(worksAgent.worksCount)));
            if (this.mLinkEnabled) {
                this.mUserInfoLayout.setOnClickListener(new OnClickListener() {
                    public void onClick(View v) {
                        WorksAgentFragment_.builder().agentId(WorksAgentCard.this.mAgentId).build().showAsActivity(WorksAgentCard.this);
                    }
                });
                ViewUtils.setTextAppearance(getContext(), this.mPublishInfo, R.style.AppWidget_Text_Link_Medium);
            } else {
                ViewUtils.setTextAppearance(getContext(), this.mPublishInfo, R.style.AppWidget_Text_Secondary);
            }
            this.mDescription.setParagraphText(worksAgent.description);
            return;
        }
        onLoadFailed();
    }
}
