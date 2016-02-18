package com.douban.book.reader.fragment.share;

import android.view.View;
import com.douban.book.reader.R;
import com.douban.book.reader.content.page.Range;
import com.douban.book.reader.fragment.BaseEditFragment;
import com.douban.book.reader.manager.WorksManager;
import com.douban.book.reader.manager.exception.DataLoadException;
import com.douban.book.reader.util.Res;
import com.douban.book.reader.view.ShareSelectionInfoView;
import com.douban.book.reader.view.ShareSelectionInfoView_;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;

@EFragment
public class CorrectRangeEditFragment extends BaseEditFragment {
    @Bean
    WorksManager mWorksManager;
    @FragmentArg
    Range range;
    @FragmentArg
    int worksId;

    @AfterViews
    void init() {
        setTitle((int) R.string.title_correct_range);
        setHint(R.string.hint_correct);
    }

    protected void onDataReady() {
        addBottomView(createBottomView());
    }

    protected void postToServer(String content) throws DataLoadException {
        this.mWorksManager.correctRange(this.worksId, this.range, content);
    }

    protected CharSequence getSucceedToastMessage() {
        return Res.getString(R.string.toast_text_correct);
    }

    private View createBottomView() {
        ShareSelectionInfoView bottomView = ShareSelectionInfoView_.build(getActivity());
        bottomView.setQuoteLineColor(R.array.blue);
        bottomView.setData(this.worksId, this.range);
        return bottomView;
    }
}
