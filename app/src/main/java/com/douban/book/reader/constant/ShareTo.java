package com.douban.book.reader.constant;

import com.douban.book.reader.R;
import com.douban.book.reader.util.RichText;

public enum ShareTo {
    DOUBAN(RichText.textWithOriginalColoredIcon(R.drawable.v_douban, R.string.share_channel_douban)),
    WEIXIN(RichText.textWithOriginalColoredIcon(R.drawable.v_weixin, R.string.share_channel_weixin)),
    MOMENTS(RichText.textWithOriginalColoredIcon(R.drawable.v_moments, R.string.share_channel_weixin_timeline)),
    WEIBO(RichText.textWithOriginalColoredIcon(R.drawable.v_weibo, R.string.share_channel_weibo)),
    OTHER_APPS(RichText.textWithColoredIcon((int) R.drawable.v_more, (int) R.array.blue, (int) R.string.share_channel_other_app));
    
    private final CharSequence mDisplayText;

    private ShareTo(CharSequence displayText) {
        this.mDisplayText = displayText;
    }

    public CharSequence getDisplayText() {
        return this.mDisplayText;
    }
}
