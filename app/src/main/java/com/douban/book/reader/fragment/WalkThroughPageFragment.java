package com.douban.book.reader.fragment;

import android.widget.Button;
import android.widget.ImageView;
import com.douban.book.reader.util.ViewUtils;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.ViewById;

@EFragment(2130903114)
public class WalkThroughPageFragment extends BaseFragment {
    @ViewById(2131558668)
    Button mBtnClose;
    private Listener mListener;
    @ViewById(2131558667)
    ImageView mPageImg;
    @FragmentArg
    int pageImgResId;
    @FragmentArg
    boolean showCloseBtn;

    public interface Listener {
        void onClosed();
    }

    public WalkThroughPageFragment() {
        this.showCloseBtn = false;
    }

    public WalkThroughPageFragment listener(Listener listener) {
        this.mListener = listener;
        return this;
    }

    @AfterViews
    void init() {
        ViewUtils.setImageResource(this.mPageImg, this.pageImgResId);
        ViewUtils.showIf(this.showCloseBtn, this.mBtnClose);
    }

    @Click({2131558668})
    void onCloseBtnClicked() {
        if (this.mListener != null) {
            this.mListener.onClosed();
        }
    }
}
