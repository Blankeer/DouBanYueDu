package com.douban.book.reader.view.card;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import com.douban.book.reader.R;
import com.douban.book.reader.app.PageOpenHelper;
import com.douban.book.reader.entity.GiftEvent;
import com.douban.book.reader.event.ColorThemeChangedEvent;
import com.douban.book.reader.fragment.GiftPackCreateFragment_;
import com.douban.book.reader.lib.view.CropImageView;
import com.douban.book.reader.theme.Theme;
import com.douban.book.reader.theme.ThemedAttrs;
import com.douban.book.reader.util.ImageLoaderUtils;
import com.douban.book.reader.util.RichText;
import com.douban.book.reader.util.ViewUtils;
import org.androidannotations.annotations.EViewGroup;

@EViewGroup
public class WorksGiftEventCard extends Card<WorksGiftEventCard> {
    private Button mButton;
    private CropImageView mImage;

    /* renamed from: com.douban.book.reader.view.card.WorksGiftEventCard.1 */
    class AnonymousClass1 implements OnClickListener {
        final /* synthetic */ GiftEvent val$event;

        AnonymousClass1(GiftEvent giftEvent) {
            this.val$event = giftEvent;
        }

        public void onClick(View v) {
            PageOpenHelper.from(WorksGiftEventCard.this).preferInternalWebView().open(this.val$event.url);
        }
    }

    /* renamed from: com.douban.book.reader.view.card.WorksGiftEventCard.2 */
    class AnonymousClass2 implements OnClickListener {
        final /* synthetic */ GiftEvent val$event;
        final /* synthetic */ int val$worksId;

        AnonymousClass2(int i, GiftEvent giftEvent) {
            this.val$worksId = i;
            this.val$event = giftEvent;
        }

        public void onClick(View v) {
            GiftPackCreateFragment_.builder().worksId(this.val$worksId).eventId(this.val$event.id).build().showAsActivity(WorksGiftEventCard.this);
        }
    }

    public WorksGiftEventCard(Context context) {
        super(context);
        content((int) R.layout.card_works_gift_event);
        noContentPadding();
        this.mImage = (CropImageView) findViewById(com.wnafee.vector.R.id.image);
        this.mButton = (Button) findViewById(R.id.button);
        this.mImage.setCropAlign(3);
        this.mImage.setCropType(2);
        this.mButton.setText(RichText.textWithIcon((int) R.drawable.v_gift, (int) R.string.select_this_book_to_present));
        ViewUtils.setEventAware(this);
        updateButton();
    }

    public void onEventMainThread(ColorThemeChangedEvent event) {
        updateButton();
    }

    private void updateButton() {
        if (Theme.isNight()) {
            ThemedAttrs.ofView(this.mButton).append(R.attr.backgroundColorArray, Integer.valueOf(R.array.red)).append(R.attr.textColorArray, Integer.valueOf(R.array.invert_text_color)).updateView();
        } else {
            ThemedAttrs.ofView(this.mButton).append(R.attr.backgroundColorArray, Integer.valueOf(R.array.btn_bg_color)).append(R.attr.textColorArray, Integer.valueOf(R.array.red)).updateView();
        }
    }

    public WorksGiftEventCard event(GiftEvent event, int worksId) {
        ImageLoaderUtils.displayImage(event.profileImg, this.mImage);
        this.mImage.setOnClickListener(new AnonymousClass1(event));
        this.mButton.setOnClickListener(new AnonymousClass2(worksId, event));
        return this;
    }
}
