package com.douban.book.reader.fragment;

import android.support.v4.app.Fragment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.douban.book.reader.R;
import com.douban.book.reader.app.App;
import com.douban.book.reader.theme.ThemedAttrs;
import com.douban.book.reader.util.StringUtils;
import com.douban.book.reader.util.Utils;
import com.douban.book.reader.util.ViewUtils;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

@EFragment(2130903110)
public class TestWorksFragment extends BaseFragment {
    @ViewById(2131558655)
    EditText mEditWorksId;
    @ViewById(2131558654)
    LinearLayout mLayoutBase;

    /* renamed from: com.douban.book.reader.fragment.TestWorksFragment.1 */
    class AnonymousClass1 implements OnClickListener {
        final /* synthetic */ int val$worksId;

        AnonymousClass1(int i) {
            this.val$worksId = i;
        }

        public void onClick(View v) {
            TestWorksFragment.this.openWorks(this.val$worksId);
        }
    }

    @AfterViews
    void init() {
        addWorks(130429, "\u6d4b\u8bd5\u6587\u7ae0", "\u5404\u79cdcase");
        addWorks(4420029, "\u653e\u5f00\u90a3\u4e2aCatalan\u6570", "\u6d4b\u8bd5\u4e13\u680f");
        addWorks(237278, "\u601d\u8003\u7684\u4e50\u8da3", "\u516c\u5f0f");
        addWorks(4101791, "\u5e1d\u90fd\u672c\u683c\u65e5\u672c\u6599\u7406", "\u5df2\u5b8c\u7ed3\u4e13\u680f");
        addWorks(4134961, "\u65c5\u884c\u624b\u7ed8\u5c0f\u8bfe\u5802", "\u672a\u5b8c\u7ed3\u4e13\u680f");
        addWorks(14732532, "\u5927\u660e\u98ce\u6708", "\u8fde\u8f7d");
        addWorks(4356, "A Room of One's Own", "\u82f1\u6587");
        addWorks(1239860, "\u7231\u5fb7\u534e\u4e09\u4e16", "\u82f1\u6587");
        addWorks(4081549, "Java\u7ebf\u7a0b", "\u4ee3\u7801");
        addWorks(1499455, "Python\u6e90\u7801\u5256\u6790", "\u4ee3\u7801");
        addWorks(2118038, "\u524d\u9014", "\u753b\u518c\u6a21\u5f0f1\uff0c\u65e0\u56fe\u6ce8");
        addWorks(9540543, "\u7126\u8651\u7684\u65f6\u5019\u53ef\u4ee5\u753b\u753b", "\u753b\u518c\u6a21\u5f0f1\uff0c\u6709\u56fe\u6ce8");
        addWorks(1872918, "\u4e00\u4e2a\u4eba", "\u753b\u518c\u6a21\u5f0f2\uff0c\u6709\u56fe\u6ce8");
        addWorks(5243699, "Yoga", "\u753b\u518c\u6a21\u5f0f2\uff0c\u90e8\u5206\u9875\u9762\u65e0\u56fe\u6ce8");
        addWorks(958945, "\u4e09\u4f53\u5168\u96c6", "\u957f\u6587");
        addWorks(4929800, "\u5b89\u73c0\u5fd7\u5168\u96c6", "\u957f\u6587");
    }

    @Click({2131558656})
    void onBtnOpenWorksClicked() {
        openWorks(StringUtils.toInt(this.mEditWorksId.getText().toString()));
    }

    @Click({2131558657})
    void onBtnOpenColumnClicked() {
        openColumn(StringUtils.toInt(this.mEditWorksId.getText().toString()));
    }

    private void addWorks(int worksId, String title, String description) {
        TextView textView = new TextView(App.get());
        textView.setText(String.format("%s %s\uff08%s\uff09", new Object[]{Integer.valueOf(worksId), title, description}));
        textView.setOnClickListener(new AnonymousClass1(worksId));
        ThemedAttrs.ofView(textView).append(R.attr.textColorArray, Integer.valueOf(R.array.content_text_color)).updateView();
        ViewUtils.setVerticalPaddingResId(textView, R.dimen.general_subview_vertical_padding_normal);
        Utils.changeFonts(textView);
        this.mLayoutBase.addView(textView);
    }

    private void openWorks(int worksId) {
        WorksProfileFragment_.builder().worksId(worksId).build().showAsActivity((Fragment) this);
    }

    private void openColumn(int columnId) {
        WorksProfileFragment_.builder().legacyColumnId(columnId).build().showAsActivity((Fragment) this);
    }
}
