package com.douban.book.reader.fragment;

import android.support.v4.app.Fragment;
import android.widget.ListView;
import android.widget.TextView;
import com.douban.book.reader.R;
import com.douban.book.reader.adapter.BaseArrayAdapter;
import com.douban.book.reader.adapter.ViewBinderAdapter;
import com.douban.book.reader.app.App;
import com.douban.book.reader.app.PageOpenHelper;
import com.douban.book.reader.entity.WorksKind;
import com.douban.book.reader.manager.WorksKindManager_;
import com.douban.book.reader.util.Logger;
import com.douban.book.reader.util.Res;
import com.douban.book.reader.util.ViewUtils;
import com.douban.book.reader.view.item.WorksKindItemView_;
import java.util.List;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.ItemClick;

@EFragment
public class WorksKindTabFragment extends BaseListFragment<WorksKind> {
    @FragmentArg
    int rootKindId;

    public BaseArrayAdapter<WorksKind> onCreateAdapter() {
        return new ViewBinderAdapter(WorksKindItemView_.class);
    }

    public List<WorksKind> onLoadData() {
        try {
            return WorksKindManager_.getInstance_(App.get()).getChildList(this.rootKindId);
        } catch (Exception e) {
            Logger.e(this.TAG, e);
            return null;
        }
    }

    protected void onListViewCreated(ListView listView) {
        TextView footerView = new TextView(App.get());
        ViewUtils.of(footerView).width(-1).height(Res.getDimensionPixelSize(R.dimen.general_subview_vertical_padding_medium)).commit();
        listView.addFooterView(footerView);
        listView.setFooterDividersEnabled(false);
        listView.setDivider(null);
    }

    @ItemClick({2131558593})
    void onItemClicked(WorksKind worksKind) {
        if (worksKind != null) {
            PageOpenHelper.from((Fragment) this).open(worksKind.uri);
        }
    }
}
