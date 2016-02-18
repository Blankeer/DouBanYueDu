package com.mcxiaoke.next.ui.dialog;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;

@TargetApi(14)
public class ProgressDialogFragment extends DialogFragment {
    private static final String TAG;
    private boolean mActivityReady;
    private boolean mAllowStateLoss;
    private boolean mCalledSuperDismiss;
    private CharSequence mMessage;
    private Dialog mOldDialog;
    private CharSequence mTitle;

    public ProgressDialogFragment() {
        this.mActivityReady = false;
        this.mCalledSuperDismiss = false;
    }

    static {
        TAG = ProgressDialogFragment.class.getSimpleName();
    }

    public static ProgressDialogFragment show(FragmentManager fragmentManager, CharSequence title, CharSequence message, boolean cancelable) {
        ProgressDialogFragment dialogFragment = new ProgressDialogFragment();
        dialogFragment.mTitle = title;
        dialogFragment.mMessage = message;
        dialogFragment.setCancelable(cancelable);
        dialogFragment.show(fragmentManager, TAG);
        return dialogFragment;
    }

    public static ProgressDialogFragment show(FragmentManager fragmentManager, CharSequence title, CharSequence message) {
        return show(fragmentManager, title, message, false);
    }

    public static ProgressDialogFragment create(CharSequence title, CharSequence message, boolean cancelable) {
        ProgressDialogFragment dialogFragment = new ProgressDialogFragment();
        dialogFragment.mTitle = title;
        dialogFragment.mMessage = message;
        dialogFragment.setCancelable(cancelable);
        return dialogFragment;
    }

    public static ProgressDialogFragment create(CharSequence title, CharSequence message) {
        return create(title, message, false);
    }

    public void show(FragmentManager manager) {
        show(manager, TAG);
    }

    public void show(FragmentManager manager, String tag) {
        super.show(manager, tag);
    }

    public int show(FragmentTransaction transaction, String tag) {
        return super.show(transaction, tag);
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setStyle(2, 0);
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        ProgressDialog dialog = new ProgressDialog(getActivity());
        dialog.setIndeterminate(true);
        dialog.setIndeterminateDrawable(null);
        dialog.setTitle(this.mTitle);
        dialog.setMessage(this.mMessage);
        return dialog;
    }

    public void onStart() {
        super.onStart();
        this.mActivityReady = true;
        if (this.mCalledSuperDismiss) {
            superDismiss();
        }
    }

    public void onStop() {
        super.onStop();
        this.mActivityReady = false;
    }

    public void onDismiss(DialogInterface dialog) {
        if (this.mOldDialog == null || this.mOldDialog != dialog) {
            super.onDismiss(dialog);
        }
    }

    public void onDestroyView() {
        this.mOldDialog = getDialog();
        super.onDestroyView();
    }

    public void dismiss() {
        this.mAllowStateLoss = false;
        superDismiss();
    }

    public void dismissAllowingStateLoss() {
        this.mAllowStateLoss = true;
        superDismiss();
    }

    private void superDismiss() {
        this.mCalledSuperDismiss = true;
        if (!this.mActivityReady) {
            return;
        }
        if (this.mAllowStateLoss) {
            super.dismissAllowingStateLoss();
        } else {
            super.dismiss();
        }
    }
}
