package com.douban.book.reader.view.card;

import android.content.Context;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import com.douban.book.reader.R;
import com.douban.book.reader.adapter.BaseArrayAdapter;
import com.douban.book.reader.util.Res;
import com.douban.book.reader.util.ThemedUtils;
import com.douban.book.reader.util.ViewUtils;

public class CardView extends ListView {
    private Adapter mAdapter;

    public interface CardFactory {
        int cardCount();

        Card createCard(int i, View view);
    }

    public class Adapter extends BaseArrayAdapter {
        private CardFactory mCardFactory;
        private SparseArray<Card> mCardPool;

        public Adapter() {
            this.mCardPool = new SparseArray();
            this.mCardFactory = null;
        }

        public void addCard(Card card) {
            this.mCardPool.put(this.mCardPool.size(), card);
            notifyDataSetChanged();
        }

        public void clear() {
            this.mCardPool.clear();
            notifyDataSetChanged();
        }

        public void setCardFactory(CardFactory cardFactory) {
            this.mCardFactory = cardFactory;
        }

        public int getCount() {
            if (this.mCardFactory != null) {
                return this.mCardFactory.cardCount();
            }
            return this.mCardPool.size();
        }

        public Object getItem(int position) {
            return null;
        }

        public long getItemId(int position) {
            return (long) position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            Card card = null;
            if (this.mCardFactory != null) {
                card = this.mCardFactory.createCard(position, convertView);
            }
            if (card == null) {
                card = (Card) this.mCardPool.get(position);
            }
            ThemedUtils.updateViewTreeIfThemeChanged(card);
            return card;
        }
    }

    public CardView(Context context) {
        super(context);
        this.mAdapter = new Adapter();
        init();
    }

    public CardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mAdapter = new Adapter();
        init();
    }

    public CardView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mAdapter = new Adapter();
        init();
    }

    private void init() {
        setDivider(null);
        setAdapter(this.mAdapter);
        setSelector(Res.getColorDrawable(R.color.transparent));
        setBackgroundColor(Res.getColor(R.array.page_bg_color));
        ViewUtils.setEventAware(this);
    }

    public void addCard(Card card) {
        this.mAdapter.addCard(card);
    }

    public void removeAllViews() {
        this.mAdapter.clear();
    }

    public void setCardFactory(CardFactory cardFactory) {
        this.mAdapter.setCardFactory(cardFactory);
    }
}
