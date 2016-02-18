package com.douban.book.reader.view.store.card;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import com.douban.book.reader.app.PageOpenHelper;
import com.douban.book.reader.entity.store.NewWorksStoreWidgetEntity;
import com.douban.book.reader.view.store.card.content.NewWorksWidgetContentView;
import org.androidannotations.annotations.EViewGroup;

@EViewGroup
public class NewWorksWidgetCard extends BaseWidgetCard<NewWorksStoreWidgetEntity> {

    /* renamed from: com.douban.book.reader.view.store.card.NewWorksWidgetCard.1 */
    class AnonymousClass1 implements OnClickListener {
        final /* synthetic */ NewWorksStoreWidgetEntity val$entity;

        AnonymousClass1(NewWorksStoreWidgetEntity newWorksStoreWidgetEntity) {
            this.val$entity = newWorksStoreWidgetEntity;
        }

        public void onClick(View v) {
            PageOpenHelper.from(NewWorksWidgetCard.this).open(this.val$entity.payload.uri);
        }
    }

    public NewWorksWidgetCard(Context context) {
        super(context);
    }

    protected void onEntityBound(NewWorksStoreWidgetEntity entity) {
        if (entity.payload != null) {
            moreBtnVisible(true);
            clickListener(new AnonymousClass1(entity));
            if (entity.payload.worksList == null || entity.payload.worksList.isEmpty()) {
                hide();
                return;
            }
            setVisibility(0);
            ((NewWorksWidgetContentView) getOrCreateContentView(NewWorksWidgetContentView.class)).setWorksList(entity.payload.worksList);
            return;
        }
        hide();
    }
}
