package com.douban.book.reader.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.douban.book.reader.R;
import com.douban.book.reader.view.card.Card;
import com.douban.book.reader.view.card.CardView;
import com.douban.book.reader.view.card.CardView.CardFactory;
import com.douban.book.reader.view.card.GeneralViewCard;

public class BaseCardFragment extends BaseRefreshFragment {
    CardView mCardView;

    protected View onCreateContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_base_card, container, false);
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.mCardView = (CardView) view.findViewById(R.id.root);
        enablePullToRefresh(false);
    }

    public <T extends Card<T>> T addCard(T card) {
        this.mCardView.addCard(card);
        this.mCardView.invalidate();
        return card;
    }

    public <T extends Card<T>> T addCardIf(boolean condition, T card) {
        if (condition) {
            addCard(card);
        }
        return card;
    }

    public <T extends View> Card<GeneralViewCard> addView(T view) {
        GeneralViewCard card = new GeneralViewCard(view.getContext());
        card.content((View) view);
        addCard(card);
        return card;
    }

    @Nullable
    public <T extends View> Card<GeneralViewCard> addViewIf(boolean condition, T view) {
        if (condition) {
            return addView(view);
        }
        return null;
    }

    public void removeAllCards() {
        this.mCardView.removeAllViews();
    }

    public void setCardFactory(CardFactory cardFactory) {
        this.mCardView.setCardFactory(cardFactory);
    }
}
