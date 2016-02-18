package com.douban.book.reader.fragment;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.TextView;
import com.douban.book.reader.R;
import com.douban.book.reader.adapter.BaseArrayAdapter;
import com.douban.book.reader.adapter.SimpleStringAdapter;
import com.douban.book.reader.app.App;
import com.douban.book.reader.event.ArkEvent;
import com.douban.book.reader.manager.SearchHistoryManager;
import com.douban.book.reader.util.StringUtils;
import java.util.List;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ItemClick;

@EFragment
public class SearchHistoryFragment extends BaseListFragment<String> {
    private onItemClickListener mItemClickListener;
    @Bean
    SearchHistoryManager mSearchHistoryManager;

    public interface onItemClickListener {
        void onItemClicked(String str);
    }

    public BaseArrayAdapter<String> onCreateAdapter() {
        return new SimpleStringAdapter();
    }

    public List<String> onLoadData() {
        return this.mSearchHistoryManager.getQueryList();
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setListTitle(R.string.title_search_history);
    }

    protected void onListViewCreated(ListView listView) {
        View clearBtnView = View.inflate(App.get(), R.layout.view_btn_clear_history, null);
        TextView clearBtn = (TextView) clearBtnView.findViewById(R.id.btn_clear);
        clearBtn.setText(R.string.btn_clear_history);
        clearBtn.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                SearchHistoryFragment.this.mSearchHistoryManager.clear();
            }
        });
        listView.addFooterView(clearBtnView);
    }

    public void setListItemClickListener(onItemClickListener itemClickListener) {
        this.mItemClickListener = itemClickListener;
    }

    @ItemClick({2131558593})
    void onListItemClicked(String item) {
        if (StringUtils.isNotEmpty(item) && this.mItemClickListener != null) {
            this.mItemClickListener.onItemClicked(item);
        }
    }

    public void onEventMainThread(ArkEvent event) {
        if (event == ArkEvent.SEARCH_HISTORY_CHANGED) {
            this.mAdapter.clear();
            this.mAdapter.addAll(this.mSearchHistoryManager.getQueryList());
        }
    }
}
