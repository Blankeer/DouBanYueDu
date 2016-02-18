package com.douban.book.reader.view;

import android.content.Context;
import android.support.v7.view.CollapsibleActionView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import com.douban.book.reader.R;
import com.douban.book.reader.app.App;
import com.douban.book.reader.util.StringUtils;
import com.douban.book.reader.util.ViewUtils;
import io.realm.internal.Table;

public class SpinnerSearchView extends LinearLayout implements CollapsibleActionView {
    private ImageView mBtnClose;
    private onCloseListener mOnCloseListener;
    private OnQueryTextListener mOnQueryTextListener;
    private EditText mSearchInput;

    public interface OnQueryTextListener {
        boolean onQueryTextChanged(String str);

        boolean onQueryTextSubmit(String str);
    }

    public interface onCloseListener {
        boolean onClose();
    }

    public SpinnerSearchView(Context context) {
        super(context);
        init();
    }

    public SpinnerSearchView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SpinnerSearchView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        ((LayoutInflater) App.get().getSystemService("layout_inflater")).inflate(R.layout.view_spinner_search, this, true);
        this.mSearchInput = (EditText) findViewById(R.id.search_input);
        this.mBtnClose = (ImageView) findViewById(R.id.btn_close);
        this.mSearchInput.setOnEditorActionListener(new OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId != 3) {
                    return false;
                }
                SpinnerSearchView.this.onQuerySubmit(SpinnerSearchView.this.mSearchInput.getText().toString());
                return true;
            }
        });
        this.mSearchInput.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            public void afterTextChanged(Editable s) {
                ViewUtils.visibleIf(StringUtils.isNotEmpty(s.toString()), SpinnerSearchView.this.mBtnClose);
                SpinnerSearchView.this.onQueryChanged(s.toString());
            }
        });
        this.mBtnClose.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (SpinnerSearchView.this.mOnCloseListener != null) {
                    SpinnerSearchView.this.mOnCloseListener.onClose();
                } else {
                    SpinnerSearchView.this.mSearchInput.setText(Table.STRING_DEFAULT_VALUE);
                }
            }
        });
    }

    public String getQuery() {
        return this.mSearchInput.getText().toString();
    }

    public void setQuery(CharSequence query, boolean submit) {
        this.mSearchInput.setText(query);
        if (StringUtils.isNotEmpty(query)) {
            this.mSearchInput.setSelection(this.mSearchInput.length());
        }
        if (submit) {
            onQuerySubmit(query.toString());
        }
    }

    public void setQueryHint(String hint) {
        this.mSearchInput.setHint(hint);
    }

    public void setOnQueryTextListener(OnQueryTextListener queryTextListener) {
        this.mOnQueryTextListener = queryTextListener;
    }

    public void setOnCloseListener(onCloseListener onCloseListener) {
        this.mOnCloseListener = onCloseListener;
    }

    private void onQuerySubmit(String query) {
        if (this.mOnQueryTextListener != null) {
            this.mOnQueryTextListener.onQueryTextSubmit(query);
        }
    }

    private void onQueryChanged(String query) {
        if (this.mOnQueryTextListener != null) {
            this.mOnQueryTextListener.onQueryTextChanged(query);
        }
    }

    public void onActionViewExpanded() {
    }

    public void onActionViewCollapsed() {
    }
}
