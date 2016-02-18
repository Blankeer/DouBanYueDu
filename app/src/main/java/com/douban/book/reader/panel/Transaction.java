package com.douban.book.reader.panel;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import com.douban.book.reader.util.Logger;
import com.douban.book.reader.util.Tag;
import com.douban.book.reader.util.ViewUtils;
import java.util.HashMap;
import java.util.Map;

public class Transaction {
    private static Map<String, FragmentData> sFragmentDataMap;
    private int mLayoutRes;
    private FragmentManager mManager;
    private FragmentTransaction mTransaction;

    private static final class FragmentData {
        int enterAnim;
        int exitAnim;

        private FragmentData() {
        }
    }

    static {
        sFragmentDataMap = new HashMap();
    }

    public static Transaction begin(Fragment fragment, int attachTo) {
        return begin(fragment.getChildFragmentManager(), attachTo);
    }

    public static Transaction begin(FragmentActivity activity) {
        return begin(activity, 16908290);
    }

    public static Transaction begin(View view, int attachTo) {
        Activity activity = ViewUtils.getAttachedActivity(view);
        if (activity instanceof FragmentActivity) {
            return begin((FragmentActivity) activity, attachTo);
        }
        throw new IllegalStateException("View not attached to Activity, or Activity is not supported for attaching Fragment");
    }

    public static Transaction begin(FragmentActivity activity, int attachTo) {
        return begin(activity.getSupportFragmentManager(), attachTo);
    }

    public static Transaction begin(FragmentManager manager, int attachTo) {
        return new Transaction(manager, attachTo);
    }

    private Transaction(FragmentManager manager, int attachTo) {
        this.mManager = manager;
        this.mLayoutRes = attachTo;
        this.mTransaction = this.mManager.beginTransaction();
    }

    public Transaction show(Class<? extends Fragment> cls) {
        return show(cls, null);
    }

    public Transaction show(Class<? extends Fragment> cls, Bundle args) {
        Fragment fragment = getOrCreateFragment(cls, args);
        if (fragment != null) {
            appendAnimation(cls);
            this.mManager.executePendingTransactions();
            if (!fragment.isAdded()) {
                this.mTransaction.add(this.mLayoutRes, fragment, getTag((Class) cls));
            } else if (!fragment.isVisible()) {
                this.mTransaction.show(fragment);
            }
        }
        return this;
    }

    public Transaction show(Fragment fragment) {
        if (fragment != null) {
            appendAnimation(fragment.getClass());
            this.mManager.executePendingTransactions();
            if (!fragment.isAdded()) {
                this.mTransaction.add(this.mLayoutRes, fragment, getTag(fragment.getClass()));
            } else if (!fragment.isVisible()) {
                this.mTransaction.show(fragment);
            }
        }
        return this;
    }

    public Transaction hide(Class<? extends Fragment> cls) {
        Fragment fragment = getFragment(cls);
        if (fragment != null) {
            appendAnimation(cls);
            if (fragment.isAdded()) {
                this.mTransaction.remove(fragment);
            }
        }
        return this;
    }

    public Transaction replace(Class<? extends Fragment> cls) {
        return replace(cls, null);
    }

    public Transaction replace(Class<? extends Fragment> cls, Bundle args) {
        Fragment fragment = getOrCreateFragment(cls, args);
        if (fragment != null) {
            appendAnimation(cls);
            this.mTransaction.replace(this.mLayoutRes, fragment, getTag((Class) cls));
        }
        return this;
    }

    public Transaction replace(Fragment fragment) {
        if (fragment != null) {
            Fragment oldFragment = getFragment(fragment.getClass());
            if (oldFragment == null || !oldFragment.isVisible()) {
                this.mTransaction.replace(this.mLayoutRes, fragment, getTag(fragment));
            }
        }
        return this;
    }

    public Transaction forceReplace(Fragment fragment) {
        if (fragment != null) {
            this.mTransaction.replace(this.mLayoutRes, fragment, getTag(fragment));
        }
        return this;
    }

    public void commit() {
        this.mTransaction.commit();
    }

    public void commitAllowingStateLoss() {
        this.mTransaction.commitAllowingStateLoss();
    }

    public static String getTag(Fragment fragment) {
        return fragment.getClass().getSimpleName();
    }

    public static String getTag(Class<? extends Fragment> cls) {
        return cls.getSimpleName();
    }

    private Transaction appendAnimation(Class<? extends Fragment> cls) {
        FragmentData data = (FragmentData) sFragmentDataMap.get(getTag((Class) cls));
        if (data != null) {
            this.mTransaction.setCustomAnimations(data.enterAnim, data.exitAnim);
        }
        return this;
    }

    private Fragment getFragment(Class<? extends Fragment> cls) {
        return this.mManager.findFragmentByTag(getTag((Class) cls));
    }

    private Fragment getOrCreateFragment(Class<? extends Fragment> cls, Bundle args) {
        Fragment fragment = getFragment(cls);
        if (fragment == null) {
            try {
                fragment = (Fragment) cls.newInstance();
                if (args != null) {
                    fragment.setArguments(args);
                }
            } catch (InstantiationException e) {
                Logger.e(Tag.TRANSACTION, e);
            } catch (IllegalAccessException e2) {
                Logger.e(Tag.TRANSACTION, e2);
            }
        }
        return fragment;
    }

    private static void newFragmentData(Class<? extends Fragment> cls, int enterAnim, int exitAnim) {
        FragmentData data = new FragmentData();
        data.enterAnim = enterAnim;
        data.exitAnim = exitAnim;
        sFragmentDataMap.put(getTag((Class) cls), data);
    }
}
