package com.douban.book.reader.fragment.share;

import android.net.Uri;
import android.net.Uri.Builder;
import android.view.View;
import com.douban.book.reader.R;
import com.douban.book.reader.constant.Char;
import com.douban.book.reader.entity.GiftPack;
import com.douban.book.reader.fragment.BaseShareEditFragment;
import com.douban.book.reader.helper.StoreUriHelper;
import com.douban.book.reader.manager.GiftPackManager;
import com.douban.book.reader.manager.exception.DataLoadException;
import com.douban.book.reader.util.Res;
import com.douban.book.reader.util.RichText;
import com.douban.book.reader.util.StringUtils;
import com.douban.book.reader.view.ShareGiftPackInfoView_;
import com.sina.weibo.sdk.constant.WBConstants;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;

@EFragment
public class ShareGiftPackEditFragment extends BaseShareEditFragment {
    private GiftPack mGiftPack;
    @Bean
    GiftPackManager mGiftPackManager;
    @FragmentArg
    int packId;

    protected void initData() throws DataLoadException {
        this.mGiftPack = (GiftPack) this.mGiftPackManager.get((Object) Integer.valueOf(this.packId));
    }

    protected String getContentType() {
        return BaseShareEditFragment.CONTENT_TYPE_GIFT_PACK;
    }

    protected Object getContentId() {
        return Integer.valueOf(this.packId);
    }

    protected String getContentTitle() {
        return new RichText().append(Res.getString(R.string.title_for_shared_gift_pack, this.mGiftPack.giver.name, Integer.valueOf(this.mGiftPack.quantity), this.mGiftPack.getGiftAlias(), this.mGiftPack.works.title)).appendIf(StringUtils.isNotEmpty(this.mGiftPack.works.author), Character.valueOf(Char.SPACE), this.mGiftPack.works.author, Character.valueOf(Char.SPACE), Res.getString(R.string.authored)).appendIf(StringUtils.isNotEmpty(this.mGiftPack.works.translator), Character.valueOf(Char.SPACE), this.mGiftPack.works.translator, Character.valueOf(Char.SPACE), Res.getString(R.string.translated)).toString();
    }

    protected String getComplicatedContentTitle() {
        return new RichText().append(getContentTitle()).append((CharSequence) " | ").append(Res.getString(R.string.app_name)).toString();
    }

    protected String getContentDescription() {
        return this.mGiftPack.message;
    }

    protected CharSequence getWeiboDefaultShareText() {
        return new RichText().append(Res.getString(R.string.weibo_default_message_for_shared_gift_pack, Integer.valueOf(this.mGiftPack.quantity), this.mGiftPack.getGiftAlias(), this.mGiftPack.works.title, this.mGiftPack.message)).appendIfNotEmpty(this.mGiftPack.getWeiboHashTag());
    }

    protected String getContentThumbnailUri() {
        return new Builder().scheme("assets").authority(WBConstants.GAME_PARAMS_GAME_IMAGE_URL).appendPath("thumbnail_for_gift_share.png").build().toString();
    }

    protected Uri getContentUri() {
        return StoreUriHelper.giftPack(this.mGiftPack.hashCode);
    }

    protected Object getRelatedWorksId() {
        return Integer.valueOf(this.mGiftPack.works.id);
    }

    protected String getRelatedWorksTitle() {
        return this.mGiftPack.works.title;
    }

    protected void setupViews() {
        setTitle((int) R.string.title_share_gift_pack);
        addBottomView(createBottomView());
    }

    protected void postToServer(String content) throws DataLoadException {
        this.mGiftPackManager.share(this.packId, getShareTo(), content);
    }

    protected CharSequence getSucceedToastMessage() {
        return Res.getString(R.string.toast_gift_send_succeed);
    }

    private View createBottomView() {
        return ShareGiftPackInfoView_.build(getActivity()).setGiftPack(this.mGiftPack);
    }
}
