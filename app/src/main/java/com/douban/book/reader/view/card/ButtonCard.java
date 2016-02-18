package com.douban.book.reader.view.card;

import android.content.Context;
import android.view.View.OnClickListener;
import android.widget.Button;
import com.douban.book.reader.R;
import com.douban.book.reader.util.Res;

public class ButtonCard extends Card<ButtonCard> {
    private Button mButton;

    public ButtonCard(Context context) {
        super(context);
        content((int) R.layout.card_button);
        this.mButton = (Button) findViewById(R.id.button);
        contentPaddingVerticalResId(R.dimen.general_subview_vertical_padding_large);
    }

    public ButtonCard text(CharSequence text) {
        if (this.mButton != null) {
            this.mButton.setText(text);
        }
        return this;
    }

    public ButtonCard text(int textResId) {
        return text(Res.getString(textResId));
    }

    public ButtonCard clickListener(OnClickListener listener) {
        if (this.mButton != null) {
            this.mButton.setOnClickListener(listener);
        }
        return this;
    }
}
