package com.douban.book.reader.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.DialogInterface.OnKeyListener;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ListAdapter;
import com.douban.book.reader.app.App;
import com.douban.book.reader.theme.Theme;
import java.util.Arrays;

public class AlertDialogFragment extends BaseDialogFragment {
    public static final boolean DEBUG = false;
    public static final String TAG;
    private Params mParams;

    public static class Builder {
        private Context mContext;
        private Params mParams;

        public Builder(Builder builder) {
            this.mContext = App.get();
            this.mParams = new Params();
            this.mContext = builder.mContext;
            this.mParams = builder.mParams;
        }

        public Builder() {
            this(0);
        }

        public Builder(int theme) {
            this.mContext = App.get();
            this.mParams = new Params();
            this.mParams.mTheme = theme;
        }

        public Builder setTheme(int mTheme) {
            this.mParams.mTheme = mTheme;
            return this;
        }

        public int getTheme() {
            return this.mParams.mTheme;
        }

        public Builder setWindowNoTitle(boolean noTitle) {
            this.mParams.mWindowNoTitle = noTitle;
            return this;
        }

        public boolean isWindowNoTitle() {
            return this.mParams.mWindowNoTitle;
        }

        public Builder setTitle(int titleId) {
            this.mParams.mTitle = this.mContext.getText(titleId);
            return this;
        }

        public Builder setTitle(CharSequence title) {
            this.mParams.mTitle = title;
            return this;
        }

        public Builder setCustomTitle(View customTitleView) {
            this.mParams.mCustomTitleView = customTitleView;
            return this;
        }

        public Builder setMessage(int messageId) {
            this.mParams.mMessage = this.mContext.getText(messageId);
            return this;
        }

        public Builder setMessage(CharSequence message) {
            this.mParams.mMessage = message;
            return this;
        }

        public Builder setIcon(int iconId) {
            this.mParams.mIconId = iconId;
            return this;
        }

        public Builder setIcon(Drawable icon) {
            this.mParams.mIcon = icon;
            return this;
        }

        public Builder setPositiveButton(int textId, OnClickListener listener) {
            this.mParams.mPositiveButtonText = this.mContext.getText(textId);
            this.mParams.mPositiveButtonListener = listener;
            return this;
        }

        public Builder setPositiveButton(CharSequence text, OnClickListener listener) {
            this.mParams.mPositiveButtonText = text;
            this.mParams.mPositiveButtonListener = listener;
            return this;
        }

        public Builder setNegativeButton(int textId, OnClickListener listener) {
            this.mParams.mNegativeButtonText = this.mContext.getText(textId);
            this.mParams.mNegativeButtonListener = listener;
            return this;
        }

        public Builder setNegativeButton(CharSequence text, OnClickListener listener) {
            this.mParams.mNegativeButtonText = text;
            this.mParams.mNegativeButtonListener = listener;
            return this;
        }

        public Builder setNeutralButton(int textId, OnClickListener listener) {
            this.mParams.mNeutralButtonText = this.mContext.getText(textId);
            this.mParams.mNeutralButtonListener = listener;
            return this;
        }

        public Builder setNeutralButton(CharSequence text, OnClickListener listener) {
            this.mParams.mNeutralButtonText = text;
            this.mParams.mNeutralButtonListener = listener;
            return this;
        }

        public Builder setCancelable(boolean cancelable) {
            this.mParams.mCancelable = cancelable;
            return this;
        }

        public Builder setCanceledOnTouchOutside(boolean canceledOnTouchOutside) {
            this.mParams.mCanceledOnTouchOutside = canceledOnTouchOutside;
            return this;
        }

        public Builder setOnCancelListener(OnCancelListener onCancelListener) {
            this.mParams.mOnCancelListener = onCancelListener;
            return this;
        }

        public Builder setOnDismissListener(OnDismissListener onDismissListener) {
            this.mParams.mOnDismissListener = onDismissListener;
            return this;
        }

        public Builder setOnKeyListener(OnKeyListener onKeyListener) {
            this.mParams.mOnKeyListener = onKeyListener;
            return this;
        }

        public Builder setItems(int itemsId, OnClickListener listener) {
            this.mParams.mItems = this.mContext.getResources().getTextArray(itemsId);
            this.mParams.mOnClickListener = listener;
            return this;
        }

        public Builder setItems(CharSequence[] items, OnClickListener listener) {
            this.mParams.mItems = items;
            this.mParams.mOnClickListener = listener;
            return this;
        }

        public Builder setAdapter(ListAdapter adapter, OnClickListener listener) {
            this.mParams.mAdapter = adapter;
            this.mParams.mOnClickListener = listener;
            return this;
        }

        public Builder setMultiChoiceItems(int itemsId, boolean[] checkedItems, OnMultiChoiceClickListener listener) {
            this.mParams.mItems = this.mContext.getResources().getTextArray(itemsId);
            this.mParams.mOnCheckboxClickListener = listener;
            this.mParams.mCheckedItems = checkedItems;
            this.mParams.mIsMultiChoice = true;
            return this;
        }

        public Builder setMultiChoiceItems(CharSequence[] items, boolean[] checkedItems, OnMultiChoiceClickListener listener) {
            this.mParams.mItems = items;
            this.mParams.mOnCheckboxClickListener = listener;
            this.mParams.mCheckedItems = checkedItems;
            this.mParams.mIsMultiChoice = true;
            return this;
        }

        public Builder setSingleChoiceItems(int itemsId, int checkedItem, OnClickListener listener) {
            this.mParams.mItems = this.mContext.getResources().getTextArray(itemsId);
            this.mParams.mOnClickListener = listener;
            this.mParams.mCheckedItem = checkedItem;
            this.mParams.mIsSingleChoice = true;
            return this;
        }

        public Builder setSingleChoiceItems(CharSequence[] items, int checkedItem, OnClickListener listener) {
            this.mParams.mItems = items;
            this.mParams.mOnClickListener = listener;
            this.mParams.mCheckedItem = checkedItem;
            this.mParams.mIsSingleChoice = true;
            return this;
        }

        public Builder setSingleChoiceItems(ListAdapter adapter, int checkedItem, OnClickListener listener) {
            this.mParams.mAdapter = adapter;
            this.mParams.mOnClickListener = listener;
            this.mParams.mCheckedItem = checkedItem;
            this.mParams.mIsSingleChoice = true;
            return this;
        }

        public Builder setOnItemSelectedListener(OnItemSelectedListener listener) {
            this.mParams.mOnItemSelectedListener = listener;
            return this;
        }

        public Builder setView(View view) {
            this.mParams.mView = view;
            return this;
        }

        public AlertDialogFragment create() {
            AlertDialogFragment fragment = new AlertDialogFragment();
            fragment.setParams(this.mParams);
            return fragment;
        }
    }

    static class Params {
        public ListAdapter mAdapter;
        public boolean mCancelable;
        public boolean mCanceledOnTouchOutside;
        public int mCheckedItem;
        public boolean[] mCheckedItems;
        public View mCustomTitleView;
        public Drawable mIcon;
        public int mIconId;
        public boolean mIsMultiChoice;
        public boolean mIsSingleChoice;
        public CharSequence[] mItems;
        public CharSequence mMessage;
        public OnClickListener mNegativeButtonListener;
        public CharSequence mNegativeButtonText;
        public OnClickListener mNeutralButtonListener;
        public CharSequence mNeutralButtonText;
        public OnCancelListener mOnCancelListener;
        public OnMultiChoiceClickListener mOnCheckboxClickListener;
        public OnClickListener mOnClickListener;
        public OnDismissListener mOnDismissListener;
        public OnItemSelectedListener mOnItemSelectedListener;
        public OnKeyListener mOnKeyListener;
        public OnClickListener mPositiveButtonListener;
        public CharSequence mPositiveButtonText;
        public int mTheme;
        public CharSequence mTitle;
        public View mView;
        public boolean mWindowNoTitle;

        Params() {
            this.mCancelable = true;
            this.mCanceledOnTouchOutside = true;
            this.mCheckedItem = -1;
        }

        public String toString() {
            return "Params{mTheme=" + this.mTheme + ", mWindowNoTitle=" + this.mWindowNoTitle + ", mIconId=" + this.mIconId + ", mIcon=" + this.mIcon + ", mTitle=" + this.mTitle + ", mCustomTitleView=" + this.mCustomTitleView + ", mMessage=" + this.mMessage + ", mPositiveButtonText=" + this.mPositiveButtonText + ", mPositiveButtonListener=" + this.mPositiveButtonListener + ", mNegativeButtonText=" + this.mNegativeButtonText + ", mNegativeButtonListener=" + this.mNegativeButtonListener + ", mNeutralButtonText=" + this.mNeutralButtonText + ", mNeutralButtonListener=" + this.mNeutralButtonListener + ", mCancelable=" + this.mCancelable + ", mCanceledOnTouchOutside=" + this.mCanceledOnTouchOutside + ", mOnCancelListener=" + this.mOnCancelListener + ", mOnDismissListener=" + this.mOnDismissListener + ", mOnKeyListener=" + this.mOnKeyListener + ", mItems=" + Arrays.toString(this.mItems) + ", mAdapter=" + this.mAdapter + ", mOnClickListener=" + this.mOnClickListener + ", mView=" + this.mView + '}';
        }
    }

    static {
        TAG = AlertDialogFragment.class.getSimpleName();
    }

    void setParams(Params params) {
        this.mParams = params;
    }

    public void setTitle(CharSequence title) {
        this.mParams.mTitle = title;
    }

    public void setMessage(CharSequence message) {
        this.mParams.mMessage = message;
    }

    public void setIcon(int iconId) {
        this.mParams.mIconId = iconId;
    }

    public void setIcon(Drawable icon) {
        this.mParams.mIcon = icon;
    }

    public void setPositiveButton(CharSequence text, OnClickListener listener) {
        this.mParams.mPositiveButtonText = text;
        this.mParams.mPositiveButtonListener = listener;
    }

    public void setNegativeButton(CharSequence text, OnClickListener listener) {
        this.mParams.mNegativeButtonText = text;
        this.mParams.mNegativeButtonListener = listener;
    }

    public void setNeutralButton(CharSequence text, OnClickListener listener) {
        this.mParams.mNeutralButtonText = text;
        this.mParams.mNeutralButtonListener = listener;
    }

    public void setCancelable(boolean cancelable) {
        this.mParams.mCancelable = cancelable;
    }

    public void setCanceledOnTouchOutside(boolean canceledOnTouchOutside) {
        this.mParams.mCanceledOnTouchOutside = canceledOnTouchOutside;
    }

    public void setOnCancelListener(OnCancelListener onCancelListener) {
        this.mParams.mOnCancelListener = onCancelListener;
    }

    public void setOnDismissListener(OnDismissListener onDismissListener) {
        this.mParams.mOnDismissListener = onDismissListener;
    }

    public void setOnKeyListener(OnKeyListener onKeyListener) {
        this.mParams.mOnKeyListener = onKeyListener;
    }

    public void setItems(CharSequence[] items, OnClickListener listener) {
        this.mParams.mItems = items;
        this.mParams.mOnClickListener = listener;
    }

    public void setAdapter(ListAdapter adapter, OnClickListener listener) {
        this.mParams.mAdapter = adapter;
        this.mParams.mOnClickListener = listener;
    }

    public void setView(View view) {
        this.mParams.mView = view;
    }

    public Dialog onCreateDialog(Bundle bundle) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity(), Theme.isNight() ? 4 : 5);
        if (this.mParams == null) {
            this.mParams = new Params();
        }
        if (this.mParams.mIconId > 0) {
            builder.setIcon(this.mParams.mIconId);
        } else if (this.mParams.mIcon != null) {
            builder.setIcon(this.mParams.mIcon);
        }
        if (this.mParams.mTitle != null) {
            builder.setTitle(this.mParams.mTitle);
        }
        if (this.mParams.mMessage != null) {
            builder.setMessage(this.mParams.mMessage);
        }
        if (this.mParams.mPositiveButtonText != null) {
            builder.setPositiveButton(this.mParams.mPositiveButtonText, this.mParams.mPositiveButtonListener);
        }
        if (this.mParams.mNegativeButtonText != null) {
            builder.setNegativeButton(this.mParams.mNegativeButtonText, this.mParams.mNegativeButtonListener);
        }
        if (this.mParams.mNeutralButtonText != null) {
            builder.setNeutralButton(this.mParams.mNeutralButtonText, this.mParams.mNeutralButtonListener);
        }
        if (this.mParams.mCustomTitleView != null) {
            builder.setCustomTitle(this.mParams.mCustomTitleView);
        }
        if (this.mParams.mView != null) {
            builder.setView(this.mParams.mView);
        }
        if (this.mParams.mIsSingleChoice) {
            if (this.mParams.mItems != null) {
                builder.setSingleChoiceItems(this.mParams.mItems, this.mParams.mCheckedItem, this.mParams.mOnClickListener);
            } else if (this.mParams.mAdapter != null) {
                builder.setSingleChoiceItems(this.mParams.mAdapter, this.mParams.mCheckedItem, this.mParams.mOnClickListener);
            }
        } else if (this.mParams.mIsMultiChoice) {
            if (this.mParams.mItems != null) {
                builder.setMultiChoiceItems(this.mParams.mItems, this.mParams.mCheckedItems, this.mParams.mOnCheckboxClickListener);
            }
        } else if (this.mParams.mItems != null) {
            builder.setItems(this.mParams.mItems, this.mParams.mOnClickListener);
        } else if (this.mParams.mAdapter != null) {
            builder.setAdapter(this.mParams.mAdapter, this.mParams.mOnClickListener);
        }
        builder.setCancelable(this.mParams.mCancelable);
        builder.setOnKeyListener(this.mParams.mOnKeyListener);
        AlertDialog dialog = builder.create();
        if (this.mParams.mWindowNoTitle) {
            dialog.requestWindowFeature(1);
        }
        dialog.setCanceledOnTouchOutside(this.mParams.mCanceledOnTouchOutside);
        return dialog;
    }

    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (this.mParams.mOnDismissListener != null) {
            this.mParams.mOnDismissListener.onDismiss(dialog);
        }
    }

    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        if (this.mParams.mOnCancelListener != null) {
            this.mParams.mOnCancelListener.onCancel(dialog);
        }
    }

    public void onDestroy() {
        super.onDestroy();
    }

    public void dismiss() {
        super.dismiss();
    }

    public void dismissAllowingStateLoss() {
        super.dismissAllowingStateLoss();
    }
}
