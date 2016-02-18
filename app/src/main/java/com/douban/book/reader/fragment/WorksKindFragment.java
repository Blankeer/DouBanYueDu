package com.douban.book.reader.fragment;

import com.douban.book.reader.R;
import com.douban.book.reader.entity.WorksKind;
import com.douban.book.reader.manager.WorksKindManager;
import com.douban.book.reader.util.Logger;
import com.douban.book.reader.util.StringUtils;
import com.douban.book.reader.util.ToastUtils;
import java.util.List;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.UiThread;

@EFragment
public class WorksKindFragment extends TabFragment {
    @FragmentArg
    String defaultTabTitle;
    @Bean
    WorksKindManager mWorksKindManager;

    @AfterViews
    void init() {
        setTitle((int) R.string.title_works_kind);
        showLoadingDialog();
        loadRootWorksKind();
    }

    @Background
    void loadRootWorksKind() {
        try {
            updateTabs(this.mWorksKindManager.getRootKinds());
        } catch (Exception e) {
            Logger.e(this.TAG, e);
            onLoadFailed();
        } finally {
            dismissLoadingDialog();
        }
    }

    @UiThread
    void updateTabs(List<WorksKind> rootKindList) {
        if (rootKindList != null) {
            int defaultTabPos = 0;
            int index = 0;
            for (WorksKind kind : rootKindList) {
                appendTab(WorksKindTabFragment_.builder().rootKindId(kind.id).build().setTitle(kind.name));
                if (StringUtils.equals(this.defaultTabTitle, kind.name)) {
                    defaultTabPos = index;
                }
                index++;
            }
            setDefaultPage(defaultTabPos);
        }
    }

    @UiThread
    void onLoadFailed() {
        ToastUtils.showToast((int) R.string.toast_general_load_failed);
    }
}
