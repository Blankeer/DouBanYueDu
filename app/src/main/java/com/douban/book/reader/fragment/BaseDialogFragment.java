package com.douban.book.reader.fragment;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.douban.book.reader.activity.BaseActivity;
import com.douban.book.reader.activity.GeneralDialogFragmentActivity;
import com.douban.book.reader.activity.GeneralFragmentActivity;
import com.douban.book.reader.app.App;
import com.douban.book.reader.app.PageOpenHelper;
import com.douban.book.reader.event.EventBusUtils;
import com.douban.book.reader.util.FragmentInstanceCache;
import com.douban.book.reader.util.Logger;
import com.douban.book.reader.util.Tag;
import com.douban.book.reader.util.ToastUtils;
import com.douban.book.reader.util.Utils;
import com.douban.book.reader.util.WindowActivityCache;

public class BaseDialogFragment extends DialogFragment {
    private static final int MAX_DIALOG_WIDTH_IN_DP = 400;
    protected final String TAG;
    protected BaseActivity mActivity;

    public BaseDialogFragment() {
        this.TAG = getClass().getSimpleName();
    }

    public void onAttach(Activity activity) {
        super.onAttach(activity);
        lifeCycleLog("onAttach");
        EventBusUtils.register(this);
        this.mActivity = (BaseActivity) activity;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        lifeCycleLog("onCreate");
        setStyle(1, 0);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        lifeCycleLog("onCreateView");
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        lifeCycleLog("onViewCreated");
        Utils.changeFonts(view);
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        lifeCycleLog("onActivityCreated");
    }

    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        lifeCycleLog("onViewStateRestored");
    }

    public void onStart() {
        super.onStart();
        lifeCycleLog("onStart");
        if (getDialog() != null) {
            getDialog().getWindow().setLayout(Math.min(App.get().getPageWidth(), Utils.dp2pixel(400.0f)), -2);
        }
    }

    public void onResume() {
        super.onResume();
        lifeCycleLog("onResume");
    }

    public void onPause() {
        super.onPause();
        lifeCycleLog("onPause");
    }

    public void onStop() {
        super.onStop();
        lifeCycleLog("onStop");
    }

    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        Activity activity = getActivity();
        if (activity instanceof GeneralDialogFragmentActivity) {
            activity.finish();
        }
    }

    public void onDestroyView() {
        super.onDestroyView();
        lifeCycleLog("onDestroyView");
    }

    public void onDestroy() {
        super.onDestroy();
        lifeCycleLog("onDestroy");
        FragmentInstanceCache.remove(this);
    }

    public void onDetach() {
        super.onDetach();
        lifeCycleLog("onDetach");
        EventBusUtils.unregister(this);
    }

    public void show(View view) {
        IBinder token = view.getWindowToken();
        if (token != null) {
            Activity activity = WindowActivityCache.get(token.hashCode());
            if (activity != null) {
                show(activity);
            }
        }
    }

    public void show(Fragment fragment) {
        if (fragment != null) {
            Activity activity = fragment.getActivity();
            if (activity != null) {
                show(activity);
            }
        }
    }

    public void show(Activity activity) {
        if (activity instanceof FragmentActivity) {
            show(((FragmentActivity) activity).getSupportFragmentManager(), this.TAG);
            return;
        }
        throw new IllegalArgumentException("Must be subclass of FragmentActivity");
    }

    public void show() {
        Intent intent = new Intent(App.get(), GeneralDialogFragmentActivity.class);
        intent.putExtra(GeneralFragmentActivity.KEY_FRAGMENT, FragmentInstanceCache.push(this));
        intent.addFlags(268435456);
        App.get().startActivity(intent);
    }

    public void show(PageOpenHelper helper) {
        Intent intent = new Intent(App.get(), GeneralDialogFragmentActivity.class);
        intent.putExtra(GeneralFragmentActivity.KEY_FRAGMENT, FragmentInstanceCache.push(this));
        helper.open(intent);
    }

    @Deprecated
    protected void showToast(int resId) {
        ToastUtils.showToast(resId);
    }

    @Deprecated
    protected void showToast(CharSequence message) {
        ToastUtils.showToast(message);
    }

    private void lifeCycleLog(String event) {
        Logger.d(Tag.LIFECYCLE, "%n     ---- %s@0x%x %s ----", getClass().getSimpleName(), Integer.valueOf(hashCode()), event);
    }
}
