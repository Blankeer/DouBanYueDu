package com.douban.book.reader.fragment;

import android.app.Activity;
import android.content.Intent;
import com.douban.book.reader.R;
import com.douban.book.reader.manager.GiftPackManager;
import com.douban.book.reader.manager.exception.DataLoadException;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;

@EFragment
public class GiftMessageEditFragment extends BaseEditFragment {
    public static final String KEY_RESULT_MESSAGE = "key_result_message";
    @Bean
    GiftPackManager mGiftPackManager;
    @FragmentArg
    String message;
    @FragmentArg
    int packId;
    @FragmentArg
    boolean selected;

    @AfterViews
    void init() {
        setTitle(this.selected ? R.string.title_gift_message_create : R.string.title_gift_message_edit);
        setHint(R.string.hint_gift_message_edit);
        emptyPostAllowed(false);
        hasTextLimit(true);
    }

    protected void initData() throws DataLoadException {
        super.initData();
        if (this.packId > 0) {
            this.message = this.mGiftPackManager.getGiftPack(this.packId).message;
        }
    }

    protected void onDataReady() {
        super.onDataReady();
        setContent(this.message);
        if (this.selected) {
            setSelected();
        }
    }

    protected void postToServer(String content) throws DataLoadException {
        if (this.packId > 0) {
            this.mGiftPackManager.updateGiftNote(this.packId, content);
            return;
        }
        Activity activity = getActivity();
        if (activity != null) {
            Intent data = new Intent();
            data.putExtra(KEY_RESULT_MESSAGE, content);
            activity.setResult(-1, data);
        }
    }
}
