package com.douban.book.reader.fragment.share;

import android.net.Uri;
import android.net.Uri.Builder;
import android.view.View;
import com.douban.book.reader.R;
import com.douban.book.reader.constant.Char;
import com.douban.book.reader.entity.Gift;
import com.douban.book.reader.fragment.BaseShareEditFragment;
import com.douban.book.reader.helper.StoreUriHelper;
import com.douban.book.reader.manager.GiftManager;
import com.douban.book.reader.manager.exception.DataLoadException;
import com.douban.book.reader.util.Res;
import com.douban.book.reader.util.RichText;
import com.douban.book.reader.util.StringUtils;
import com.douban.book.reader.view.ShareGiftPackInfoView_;
import com.sina.weibo.sdk.constant.WBConstants;
import java.util.UUID;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;

@EFragment
public class ShareGiftEditFragment extends BaseShareEditFragment {
    private Gift mGift;
    @Bean
    GiftManager mGiftManager;
    @FragmentArg
    UUID uuid;

    protected void initData() throws DataLoadException {
        this.mGift = this.mGiftManager.getGift(this.uuid);
    }

    protected String getContentType() {
        return BaseShareEditFragment.CONTENT_TYPE_GIFT;
    }

    protected Object getContentId() {
        return this.uuid;
    }

    protected String getContentTitle() {
        return new RichText().append(Res.getString(R.string.title_for_shared_gift, this.mGift.giver.name, this.mGift.getGiftAlias(), this.mGift.works.title)).appendIf(StringUtils.isNotEmpty(this.mGift.works.author), Character.valueOf(Char.SPACE), this.mGift.works.author, Character.valueOf(Char.SPACE), Res.getString(R.string.authored)).appendIf(StringUtils.isNotEmpty(this.mGift.works.translator), Character.valueOf(Char.SPACE), this.mGift.works.translator, Character.valueOf(Char.SPACE), Res.getString(R.string.translated)).toString();
    }

    protected String getComplicatedContentTitle() {
        return new RichText().append(getContentTitle()).append((CharSequence) " | ").append(Res.getString(R.string.app_name)).toString();
    }

    protected String getContentDescription() {
        return this.mGift.message;
    }

    protected CharSequence getWeiboDefaultShareText() {
        return new RichText().append(Res.getString(R.string.weibo_default_message_for_shared_gift, this.mGift.giver.name, this.mGift.getGiftAlias(), this.mGift.works.title)).appendIfNotEmpty(this.mGift.getWeiboHashTag());
    }

    protected String getContentThumbnailUri() {
        return new Builder().scheme("assets").authority(WBConstants.GAME_PARAMS_GAME_IMAGE_URL).appendPath("thumbnail_for_gift_share.png").build().toString();
    }

    protected Uri getContentUri() {
        return StoreUriHelper.gift(this.mGift.uuid);
    }

    protected Object getRelatedWorksId() {
        return Integer.valueOf(this.mGift.works.id);
    }

    protected String getRelatedWorksTitle() {
        return this.mGift.works.title;
    }

    protected void setupViews() {
        setTitle((int) R.string.title_share_gift);
        addBottomView(createBottomView());
    }

    protected void postToServer(String content) throws DataLoadException {
        this.mGiftManager.share(this.uuid, getShareTo(), content);
    }

    private View createBottomView() {
        return ShareGiftPackInfoView_.build(getActivity()).setGift(this.mGift);
    }
}
