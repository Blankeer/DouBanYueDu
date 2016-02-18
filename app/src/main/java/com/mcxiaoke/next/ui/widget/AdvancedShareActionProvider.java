package com.mcxiaoke.next.ui.widget;

import android.annotation.TargetApi;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.util.Log;
import android.view.ActionProvider;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.SubMenu;
import android.view.View;
import android.widget.Toast;
import com.mcxiaoke.next.ui.R;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@TargetApi(14)
public class AdvancedShareActionProvider extends ActionProvider implements OnMenuItemClickListener {
    public static final boolean DEBUG = false;
    public static final int DEFAULT_LIST_LENGTH = 4;
    public static final String TAG;
    public static final int WEIGHT_DEFAULT = 0;
    public static final int WEIGHT_MAX = Integer.MAX_VALUE;
    private Context mContext;
    private int mDefaultLength;
    private CharSequence mExpandLabel;
    private List<String> mExtraPackages;
    private List<ShareTarget> mExtraTargets;
    private Intent mIntent;
    private final Object mLock;
    private OnMenuItemClickListener mOnMenuItemClickListener;
    private PackageManager mPackageManager;
    private List<ShareTarget> mShareTargets;
    private List<String> mToRemovePackages;
    private volatile int mWeightCounter;

    static {
        TAG = AdvancedShareActionProvider.class.getSimpleName();
    }

    public AdvancedShareActionProvider(Context context) {
        super(context);
        this.mLock = new Object();
        this.mExtraPackages = new ArrayList();
        this.mToRemovePackages = new ArrayList();
        this.mExtraTargets = new ArrayList();
        this.mShareTargets = new ArrayList();
        this.mContext = context;
        this.mPackageManager = context.getPackageManager();
        this.mWeightCounter = WEIGHT_MAX;
        this.mDefaultLength = DEFAULT_LIST_LENGTH;
        this.mExpandLabel = this.mContext.getString(R.string.share_action_provider_expand_label);
    }

    public void setOnMenuItemClickListener(OnMenuItemClickListener listener) {
        this.mOnMenuItemClickListener = listener;
    }

    public void addCustomPackage(String pkg) {
        if (!this.mExtraPackages.contains(pkg)) {
            this.mExtraPackages.add(pkg);
        }
    }

    public void addCustomPackages(Collection<String> pkgs) {
        for (String pkg : pkgs) {
            addCustomPackage(pkg);
        }
    }

    public void clearCustomPackages() {
        this.mExtraPackages.clear();
    }

    public void removePackage(String pkg) {
        this.mToRemovePackages.add(pkg);
    }

    public void addShareTarget(ShareTarget target) {
        int i = this.mWeightCounter - 1;
        this.mWeightCounter = i;
        target.weight = i;
        this.mExtraTargets.add(target);
    }

    public void setDefaultLength(int length) {
        this.mDefaultLength = length;
    }

    public int getDefaultLength() {
        return this.mDefaultLength;
    }

    public void setExpandLabel(CharSequence label) {
        this.mExpandLabel = label;
    }

    public void setShareIntent(Intent intent) {
        this.mIntent = intent;
        reloadActivities();
    }

    public void setIntentExtras(Bundle extras) {
        this.mIntent.replaceExtras(extras);
    }

    public void addIntentExtras(Bundle extras) {
        this.mIntent.putExtras(extras);
    }

    public void setIntentExtras(String subject, String text) {
        setIntentExtras(subject, text, null);
    }

    public void setIntentExtras(Uri imageUri) {
        setIntentExtras(null, null, imageUri);
    }

    public void setIntentExtras(String subject, String text, Uri imageUri) {
        this.mIntent.putExtra("android.intent.extra.SUBJECT", subject);
        this.mIntent.putExtra("android.intent.extra.TEXT", text);
        if (imageUri != null) {
            this.mIntent.putExtra("android.intent.extra.STREAM", imageUri);
        }
    }

    public List<ShareTarget> getShareTargets() {
        return this.mShareTargets;
    }

    public List<ShareTarget> getDefaultShareTargets() {
        return this.mShareTargets.subList(WEIGHT_DEFAULT, Math.min(this.mDefaultLength, this.mShareTargets.size()));
    }

    private void reloadActivities() {
        loadShareTargets();
        sortShareTargets();
    }

    private void loadShareTargets() {
        if (this.mIntent != null) {
            this.mShareTargets.clear();
            List<ResolveInfo> activities = this.mPackageManager.queryIntentActivities(this.mIntent, AccessibilityNodeInfoCompat.ACTION_CUT);
            if (activities != null && !activities.isEmpty()) {
                for (ResolveInfo resolveInfo : activities) {
                    this.mShareTargets.add(toShareTarget(resolveInfo));
                }
            }
        }
    }

    private void sortShareTargets() {
        if (this.mShareTargets.size() > 0) {
            ShareTarget target;
            for (String pkg : this.mExtraPackages) {
                target = findShareTarget(pkg);
                if (target != null) {
                    int i = this.mWeightCounter - 1;
                    this.mWeightCounter = i;
                    target.weight = i;
                }
            }
            for (String pkg2 : this.mToRemovePackages) {
                target = findShareTarget(pkg2);
                if (target != null) {
                    this.mShareTargets.remove(target);
                }
            }
            this.mShareTargets.addAll(this.mExtraTargets);
            Collections.sort(this.mShareTargets);
            this.mExtraTargets.clear();
            this.mExtraPackages.clear();
            this.mToRemovePackages.clear();
            int size = this.mShareTargets.size();
            for (int i2 = WEIGHT_DEFAULT; i2 < size; i2++) {
                ((ShareTarget) this.mShareTargets.get(i2)).id = i2;
            }
        }
    }

    private ShareTarget findShareTarget(String pkg) {
        for (ShareTarget target : this.mShareTargets) {
            if (pkg.equals(target.packageName)) {
                return target;
            }
        }
        return null;
    }

    private ShareTarget toShareTarget(ResolveInfo resolveInfo) {
        if (resolveInfo == null || resolveInfo.activityInfo == null) {
            return null;
        }
        ActivityInfo info = resolveInfo.activityInfo;
        ShareTarget target = new ShareTarget(info.loadLabel(this.mPackageManager), info.loadIcon(this.mPackageManager), null);
        target.packageName = info.packageName;
        target.className = info.name;
        return target;
    }

    public View onCreateActionView() {
        return null;
    }

    public boolean hasSubMenu() {
        return true;
    }

    public void onPrepareSubMenu(SubMenu subMenu) {
        int i;
        subMenu.clear();
        int length = Math.min(this.mDefaultLength, this.mShareTargets.size());
        Resources res = this.mContext.getResources();
        for (i = WEIGHT_DEFAULT; i < length; i++) {
            ShareTarget target = (ShareTarget) this.mShareTargets.get(i);
            subMenu.add(WEIGHT_DEFAULT, i, i, target.title).setIcon(target.icon).setOnMenuItemClickListener(this);
        }
        if (this.mDefaultLength < this.mShareTargets.size()) {
            subMenu = subMenu.addSubMenu(WEIGHT_DEFAULT, this.mDefaultLength, this.mDefaultLength, this.mExpandLabel);
            for (i = WEIGHT_DEFAULT; i < this.mShareTargets.size(); i++) {
                target = (ShareTarget) this.mShareTargets.get(i);
                subMenu.add(WEIGHT_DEFAULT, i, i, target.title).setIcon(target.icon).setOnMenuItemClickListener(this);
            }
        }
    }

    public boolean onMenuItemClick(MenuItem item) {
        boolean handled = DEBUG;
        ShareTarget target = (ShareTarget) this.mShareTargets.get(item.getItemId());
        if (target.listener != null) {
            handled = target.listener.onMenuItemClick(item);
        }
        if (!handled) {
            if (this.mOnMenuItemClickListener != null) {
                handled = this.mOnMenuItemClickListener.onMenuItemClick(item);
            }
            if (!(handled || target.packageName == null || target.className == null)) {
                ComponentName chosenName = new ComponentName(target.packageName, target.className);
                Intent intent = new Intent(this.mIntent);
                intent.addFlags(268435456);
                intent.setComponent(chosenName);
                try {
                    this.mContext.startActivity(intent);
                } catch (Exception e) {
                    Log.e(TAG, "onMenuItemClick() error: " + e);
                    Toast.makeText(this.mContext, R.string.share_action_provider_target_not_found, WEIGHT_DEFAULT).show();
                }
            }
        }
        return true;
    }
}
