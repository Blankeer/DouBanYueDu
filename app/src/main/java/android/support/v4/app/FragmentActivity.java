package android.support.v4.app;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat.OnRequestPermissionsResultCallback;
import android.support.v4.app.ActivityCompatApi23.RequestPermissionsRequestCodeValidator;
import android.support.v4.internal.view.SupportMenu;
import android.support.v4.media.TransportMediator;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.util.SimpleArrayMap;
import android.support.v4.view.InputDeviceCompat;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import com.alipay.sdk.protocol.h;
import com.douban.book.reader.constant.Char;
import io.fabric.sdk.android.services.common.AbstractSpiCall;
import io.fabric.sdk.android.services.settings.SettingsJsonConstants;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import u.aly.ci;
import u.aly.dx;

public class FragmentActivity extends BaseFragmentActivityHoneycomb implements OnRequestPermissionsResultCallback, RequestPermissionsRequestCodeValidator {
    static final String FRAGMENTS_TAG = "android:support:fragments";
    private static final int HONEYCOMB = 11;
    static final int MSG_REALLY_STOPPED = 1;
    static final int MSG_RESUME_PENDING = 2;
    private static final String TAG = "FragmentActivity";
    boolean mCreated;
    final FragmentController mFragments;
    final Handler mHandler;
    MediaControllerCompat mMediaController;
    boolean mOptionsMenuInvalidated;
    boolean mReallyStopped;
    boolean mRequestedPermissionsFromFragment;
    boolean mResumed;
    boolean mRetaining;
    boolean mStopped;

    static final class NonConfigurationInstances {
        Object custom;
        List<Fragment> fragments;
        SimpleArrayMap<String, LoaderManager> loaders;

        NonConfigurationInstances() {
        }
    }

    class HostCallbacks extends FragmentHostCallback<FragmentActivity> {
        public HostCallbacks() {
            super(FragmentActivity.this);
        }

        public void onDump(String prefix, FileDescriptor fd, PrintWriter writer, String[] args) {
            FragmentActivity.this.dump(prefix, fd, writer, args);
        }

        public boolean onShouldSaveFragmentState(Fragment fragment) {
            return !FragmentActivity.this.isFinishing();
        }

        public LayoutInflater onGetLayoutInflater() {
            return FragmentActivity.this.getLayoutInflater().cloneInContext(FragmentActivity.this);
        }

        public FragmentActivity onGetHost() {
            return FragmentActivity.this;
        }

        public void onSupportInvalidateOptionsMenu() {
            FragmentActivity.this.supportInvalidateOptionsMenu();
        }

        public void onStartActivityFromFragment(Fragment fragment, Intent intent, int requestCode) {
            FragmentActivity.this.startActivityFromFragment(fragment, intent, requestCode);
        }

        public void onRequestPermissionsFromFragment(@NonNull Fragment fragment, @NonNull String[] permissions, int requestCode) {
            FragmentActivity.this.requestPermissionsFromFragment(fragment, permissions, requestCode);
        }

        public boolean onShouldShowRequestPermissionRationale(@NonNull String permission) {
            return ActivityCompat.shouldShowRequestPermissionRationale(FragmentActivity.this, permission);
        }

        public boolean onHasWindowAnimations() {
            return FragmentActivity.this.getWindow() != null;
        }

        public int onGetWindowAnimations() {
            Window w = FragmentActivity.this.getWindow();
            return w == null ? 0 : w.getAttributes().windowAnimations;
        }

        public void onAttachFragment(Fragment fragment) {
            FragmentActivity.this.onAttachFragment(fragment);
        }

        @Nullable
        public View onFindViewById(int id) {
            return FragmentActivity.this.findViewById(id);
        }

        public boolean onHasView() {
            Window w = FragmentActivity.this.getWindow();
            return (w == null || w.peekDecorView() == null) ? false : true;
        }
    }

    public FragmentActivity() {
        this.mHandler = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case FragmentActivity.MSG_REALLY_STOPPED /*1*/:
                        if (FragmentActivity.this.mStopped) {
                            FragmentActivity.this.doReallyStop(false);
                        }
                    case FragmentActivity.MSG_RESUME_PENDING /*2*/:
                        FragmentActivity.this.onResumeFragments();
                        FragmentActivity.this.mFragments.execPendingActions();
                    default:
                        super.handleMessage(msg);
                }
            }
        };
        this.mFragments = FragmentController.createController(new HostCallbacks());
    }

    public /* bridge */ /* synthetic */ View onCreateView(View x0, String x1, Context x2, AttributeSet x3) {
        return super.onCreateView(x0, x1, x2, x3);
    }

    public /* bridge */ /* synthetic */ View onCreateView(String x0, Context x1, AttributeSet x2) {
        return super.onCreateView(x0, x1, x2);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        this.mFragments.noteStateNotSaved();
        int index = requestCode >> 16;
        if (index != 0) {
            index--;
            int activeFragmentsCount = this.mFragments.getActiveFragmentsCount();
            if (activeFragmentsCount == 0 || index < 0 || index >= activeFragmentsCount) {
                Log.w(TAG, "Activity result fragment index out of range: 0x" + Integer.toHexString(requestCode));
                return;
            }
            Fragment frag = (Fragment) this.mFragments.getActiveFragments(new ArrayList(activeFragmentsCount)).get(index);
            if (frag == null) {
                Log.w(TAG, "Activity result no fragment exists for index: 0x" + Integer.toHexString(requestCode));
                return;
            } else {
                frag.onActivityResult(SupportMenu.USER_MASK & requestCode, resultCode, data);
                return;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void onBackPressed() {
        if (!this.mFragments.getSupportFragmentManager().popBackStackImmediate()) {
            supportFinishAfterTransition();
        }
    }

    public final void setSupportMediaController(MediaControllerCompat mediaController) {
        this.mMediaController = mediaController;
        if (VERSION.SDK_INT >= 21) {
            ActivityCompat21.setMediaController(this, mediaController.getMediaController());
        }
    }

    public final MediaControllerCompat getSupportMediaController() {
        return this.mMediaController;
    }

    public void supportFinishAfterTransition() {
        ActivityCompat.finishAfterTransition(this);
    }

    public void setEnterSharedElementCallback(SharedElementCallback callback) {
        ActivityCompat.setEnterSharedElementCallback(this, callback);
    }

    public void setExitSharedElementCallback(SharedElementCallback listener) {
        ActivityCompat.setExitSharedElementCallback(this, listener);
    }

    public void supportPostponeEnterTransition() {
        ActivityCompat.postponeEnterTransition(this);
    }

    public void supportStartPostponedEnterTransition() {
        ActivityCompat.startPostponedEnterTransition(this);
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        this.mFragments.dispatchConfigurationChanged(newConfig);
    }

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        List list = null;
        this.mFragments.attachHost(null);
        super.onCreate(savedInstanceState);
        NonConfigurationInstances nc = (NonConfigurationInstances) getLastNonConfigurationInstance();
        if (nc != null) {
            this.mFragments.restoreLoaderNonConfig(nc.loaders);
        }
        if (savedInstanceState != null) {
            Parcelable p = savedInstanceState.getParcelable(FRAGMENTS_TAG);
            FragmentController fragmentController = this.mFragments;
            if (nc != null) {
                list = nc.fragments;
            }
            fragmentController.restoreAllState(p, list);
        }
        this.mFragments.dispatchCreate();
    }

    public boolean onCreatePanelMenu(int featureId, Menu menu) {
        if (featureId != 0) {
            return super.onCreatePanelMenu(featureId, menu);
        }
        boolean show = super.onCreatePanelMenu(featureId, menu) | this.mFragments.dispatchCreateOptionsMenu(menu, getMenuInflater());
        if (VERSION.SDK_INT >= HONEYCOMB) {
            return show;
        }
        return true;
    }

    final View dispatchFragmentsOnCreateView(View parent, String name, Context context, AttributeSet attrs) {
        return this.mFragments.onCreateView(parent, name, context, attrs);
    }

    protected void onDestroy() {
        super.onDestroy();
        doReallyStop(false);
        this.mFragments.dispatchDestroy();
        this.mFragments.doLoaderDestroy();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (VERSION.SDK_INT >= 5 || keyCode != 4 || event.getRepeatCount() != 0) {
            return super.onKeyDown(keyCode, event);
        }
        onBackPressed();
        return true;
    }

    public void onLowMemory() {
        super.onLowMemory();
        this.mFragments.dispatchLowMemory();
    }

    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        if (super.onMenuItemSelected(featureId, item)) {
            return true;
        }
        switch (featureId) {
            case dx.a /*0*/:
                return this.mFragments.dispatchOptionsItemSelected(item);
            case ci.g /*6*/:
                return this.mFragments.dispatchContextItemSelected(item);
            default:
                return false;
        }
    }

    public void onPanelClosed(int featureId, Menu menu) {
        switch (featureId) {
            case dx.a /*0*/:
                this.mFragments.dispatchOptionsMenuClosed(menu);
                break;
        }
        super.onPanelClosed(featureId, menu);
    }

    protected void onPause() {
        super.onPause();
        this.mResumed = false;
        if (this.mHandler.hasMessages(MSG_RESUME_PENDING)) {
            this.mHandler.removeMessages(MSG_RESUME_PENDING);
            onResumeFragments();
        }
        this.mFragments.dispatchPause();
    }

    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        this.mFragments.noteStateNotSaved();
    }

    public void onStateNotSaved() {
        this.mFragments.noteStateNotSaved();
    }

    protected void onResume() {
        super.onResume();
        this.mHandler.sendEmptyMessage(MSG_RESUME_PENDING);
        this.mResumed = true;
        this.mFragments.execPendingActions();
    }

    protected void onPostResume() {
        super.onPostResume();
        this.mHandler.removeMessages(MSG_RESUME_PENDING);
        onResumeFragments();
        this.mFragments.execPendingActions();
    }

    protected void onResumeFragments() {
        this.mFragments.dispatchResume();
    }

    public boolean onPreparePanel(int featureId, View view, Menu menu) {
        if (featureId != 0 || menu == null) {
            return super.onPreparePanel(featureId, view, menu);
        }
        if (this.mOptionsMenuInvalidated) {
            this.mOptionsMenuInvalidated = false;
            menu.clear();
            onCreatePanelMenu(featureId, menu);
        }
        return onPrepareOptionsPanel(view, menu) | this.mFragments.dispatchPrepareOptionsMenu(menu);
    }

    protected boolean onPrepareOptionsPanel(View view, Menu menu) {
        return super.onPreparePanel(0, view, menu);
    }

    public final Object onRetainNonConfigurationInstance() {
        if (this.mStopped) {
            doReallyStop(true);
        }
        Object custom = onRetainCustomNonConfigurationInstance();
        List<Fragment> fragments = this.mFragments.retainNonConfig();
        SimpleArrayMap<String, LoaderManager> loaders = this.mFragments.retainLoaderNonConfig();
        if (fragments == null && loaders == null && custom == null) {
            return null;
        }
        Object nci = new NonConfigurationInstances();
        nci.custom = custom;
        nci.fragments = fragments;
        nci.loaders = loaders;
        return nci;
    }

    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Parcelable p = this.mFragments.saveAllState();
        if (p != null) {
            outState.putParcelable(FRAGMENTS_TAG, p);
        }
    }

    protected void onStart() {
        super.onStart();
        this.mStopped = false;
        this.mReallyStopped = false;
        this.mHandler.removeMessages(MSG_REALLY_STOPPED);
        if (!this.mCreated) {
            this.mCreated = true;
            this.mFragments.dispatchActivityCreated();
        }
        this.mFragments.noteStateNotSaved();
        this.mFragments.execPendingActions();
        this.mFragments.doLoaderStart();
        this.mFragments.dispatchStart();
        this.mFragments.reportLoaderStart();
    }

    protected void onStop() {
        super.onStop();
        this.mStopped = true;
        this.mHandler.sendEmptyMessage(MSG_REALLY_STOPPED);
        this.mFragments.dispatchStop();
    }

    public Object onRetainCustomNonConfigurationInstance() {
        return null;
    }

    public Object getLastCustomNonConfigurationInstance() {
        NonConfigurationInstances nc = (NonConfigurationInstances) getLastNonConfigurationInstance();
        return nc != null ? nc.custom : null;
    }

    public void supportInvalidateOptionsMenu() {
        if (VERSION.SDK_INT >= HONEYCOMB) {
            ActivityCompatHoneycomb.invalidateOptionsMenu(this);
        } else {
            this.mOptionsMenuInvalidated = true;
        }
    }

    public void dump(String prefix, FileDescriptor fd, PrintWriter writer, String[] args) {
        String innerPrefix;
        if (VERSION.SDK_INT >= HONEYCOMB) {
            writer.print(prefix);
            writer.print("Local FragmentActivity ");
            writer.print(Integer.toHexString(System.identityHashCode(this)));
            writer.println(" State:");
            innerPrefix = prefix + "  ";
            writer.print(innerPrefix);
            writer.print("mCreated=");
            writer.print(this.mCreated);
            writer.print("mResumed=");
            writer.print(this.mResumed);
            writer.print(" mStopped=");
            writer.print(this.mStopped);
            writer.print(" mReallyStopped=");
            writer.println(this.mReallyStopped);
            this.mFragments.dumpLoaders(innerPrefix, fd, writer, args);
            this.mFragments.getSupportFragmentManager().dump(prefix, fd, writer, args);
            writer.print(prefix);
            writer.println("View Hierarchy:");
            dumpViewHierarchy(prefix + "  ", writer, getWindow().getDecorView());
        } else {
            writer.print(prefix);
            writer.print("Local FragmentActivity ");
            writer.print(Integer.toHexString(System.identityHashCode(this)));
            writer.println(" State:");
            innerPrefix = prefix + "  ";
            writer.print(innerPrefix);
            writer.print("mCreated=");
            writer.print(this.mCreated);
            writer.print("mResumed=");
            writer.print(this.mResumed);
            writer.print(" mStopped=");
            writer.print(this.mStopped);
            writer.print(" mReallyStopped=");
            writer.println(this.mReallyStopped);
            this.mFragments.dumpLoaders(innerPrefix, fd, writer, args);
            this.mFragments.getSupportFragmentManager().dump(prefix, fd, writer, args);
            writer.print(prefix);
            writer.println("View Hierarchy:");
            dumpViewHierarchy(prefix + "  ", writer, getWindow().getDecorView());
        }
    }

    private static String viewToString(View view) {
        char c;
        char c2 = 'F';
        char c3 = Char.DOT;
        StringBuilder out = new StringBuilder(TransportMediator.FLAG_KEY_MEDIA_NEXT);
        out.append(view.getClass().getName());
        out.append('{');
        out.append(Integer.toHexString(System.identityHashCode(view)));
        out.append(Char.SPACE);
        switch (view.getVisibility()) {
            case dx.a /*0*/:
                out.append('V');
                break;
            case dx.e /*4*/:
                out.append('I');
                break;
            case h.g /*8*/:
                out.append('G');
                break;
            default:
                out.append(Char.DOT);
                break;
        }
        if (view.isFocusable()) {
            c = 'F';
        } else {
            c = Char.DOT;
        }
        out.append(c);
        if (view.isEnabled()) {
            c = 'E';
        } else {
            c = Char.DOT;
        }
        out.append(c);
        out.append(view.willNotDraw() ? Char.DOT : 'D');
        if (view.isHorizontalScrollBarEnabled()) {
            c = 'H';
        } else {
            c = Char.DOT;
        }
        out.append(c);
        if (view.isVerticalScrollBarEnabled()) {
            c = 'V';
        } else {
            c = Char.DOT;
        }
        out.append(c);
        if (view.isClickable()) {
            c = 'C';
        } else {
            c = Char.DOT;
        }
        out.append(c);
        if (view.isLongClickable()) {
            c = 'L';
        } else {
            c = Char.DOT;
        }
        out.append(c);
        out.append(Char.SPACE);
        if (!view.isFocused()) {
            c2 = Char.DOT;
        }
        out.append(c2);
        if (view.isSelected()) {
            c = 'S';
        } else {
            c = Char.DOT;
        }
        out.append(c);
        if (view.isPressed()) {
            c3 = 'P';
        }
        out.append(c3);
        out.append(Char.SPACE);
        out.append(view.getLeft());
        out.append(',');
        out.append(view.getTop());
        out.append(Char.HYPHEN);
        out.append(view.getRight());
        out.append(',');
        out.append(view.getBottom());
        int id = view.getId();
        if (id != -1) {
            out.append(" #");
            out.append(Integer.toHexString(id));
            Resources r = view.getResources();
            if (!(id == 0 || r == null)) {
                String pkgname;
                switch (ViewCompat.MEASURED_STATE_MASK & id) {
                    case ViewCompat.MEASURED_STATE_TOO_SMALL /*16777216*/:
                        pkgname = AbstractSpiCall.ANDROID_CLIENT_TYPE;
                        break;
                    case 2130706432:
                        pkgname = SettingsJsonConstants.APP_KEY;
                        break;
                    default:
                        try {
                            pkgname = r.getResourcePackageName(id);
                            break;
                        } catch (NotFoundException e) {
                            break;
                        }
                }
                String typename = r.getResourceTypeName(id);
                String entryname = r.getResourceEntryName(id);
                out.append(" ");
                out.append(pkgname);
                out.append(":");
                out.append(typename);
                out.append("/");
                out.append(entryname);
            }
        }
        out.append("}");
        return out.toString();
    }

    private void dumpViewHierarchy(String prefix, PrintWriter writer, View view) {
        writer.print(prefix);
        if (view == null) {
            writer.println("null");
            return;
        }
        writer.println(viewToString(view));
        if (view instanceof ViewGroup) {
            ViewGroup grp = (ViewGroup) view;
            int N = grp.getChildCount();
            if (N > 0) {
                prefix = prefix + "  ";
                for (int i = 0; i < N; i += MSG_REALLY_STOPPED) {
                    dumpViewHierarchy(prefix, writer, grp.getChildAt(i));
                }
            }
        }
    }

    void doReallyStop(boolean retaining) {
        if (!this.mReallyStopped) {
            this.mReallyStopped = true;
            this.mRetaining = retaining;
            this.mHandler.removeMessages(MSG_REALLY_STOPPED);
            onReallyStop();
        }
    }

    void onReallyStop() {
        this.mFragments.doLoaderStop(this.mRetaining);
        this.mFragments.dispatchReallyStop();
    }

    public void onAttachFragment(Fragment fragment) {
    }

    public FragmentManager getSupportFragmentManager() {
        return this.mFragments.getSupportFragmentManager();
    }

    public LoaderManager getSupportLoaderManager() {
        return this.mFragments.getSupportLoaderManager();
    }

    public void startActivityForResult(Intent intent, int requestCode) {
        if (requestCode == -1 || (SupportMenu.CATEGORY_MASK & requestCode) == 0) {
            super.startActivityForResult(intent, requestCode);
            return;
        }
        throw new IllegalArgumentException("Can only use lower 16 bits for requestCode");
    }

    public final void validateRequestPermissionsRequestCode(int requestCode) {
        if (this.mRequestedPermissionsFromFragment) {
            this.mRequestedPermissionsFromFragment = false;
        } else if ((requestCode & InputDeviceCompat.SOURCE_ANY) != 0) {
            throw new IllegalArgumentException("Can only use lower 8 bits for requestCode");
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        int index = (requestCode >> 8) & SettingsJsonConstants.SETTINGS_IDENTIFIER_MASK_DEFAULT;
        if (index != 0) {
            index--;
            int activeFragmentsCount = this.mFragments.getActiveFragmentsCount();
            if (activeFragmentsCount == 0 || index < 0 || index >= activeFragmentsCount) {
                Log.w(TAG, "Activity result fragment index out of range: 0x" + Integer.toHexString(requestCode));
                return;
            }
            Fragment frag = (Fragment) this.mFragments.getActiveFragments(new ArrayList(activeFragmentsCount)).get(index);
            if (frag == null) {
                Log.w(TAG, "Activity result no fragment exists for index: 0x" + Integer.toHexString(requestCode));
            } else {
                frag.onRequestPermissionsResult(requestCode & SettingsJsonConstants.SETTINGS_IDENTIFIER_MASK_DEFAULT, permissions, grantResults);
            }
        }
    }

    public void startActivityFromFragment(Fragment fragment, Intent intent, int requestCode) {
        if (requestCode == -1) {
            super.startActivityForResult(intent, -1);
        } else if ((SupportMenu.CATEGORY_MASK & requestCode) != 0) {
            throw new IllegalArgumentException("Can only use lower 16 bits for requestCode");
        } else {
            super.startActivityForResult(intent, ((fragment.mIndex + MSG_REALLY_STOPPED) << 16) + (SupportMenu.USER_MASK & requestCode));
        }
    }

    private void requestPermissionsFromFragment(Fragment fragment, String[] permissions, int requestCode) {
        if (requestCode == -1) {
            ActivityCompat.requestPermissions(this, permissions, requestCode);
        } else if ((requestCode & InputDeviceCompat.SOURCE_ANY) != 0) {
            throw new IllegalArgumentException("Can only use lower 8 bits for requestCode");
        } else {
            this.mRequestedPermissionsFromFragment = true;
            ActivityCompat.requestPermissions(this, permissions, ((fragment.mIndex + MSG_REALLY_STOPPED) << 8) + (requestCode & SettingsJsonConstants.SETTINGS_IDENTIFIER_MASK_DEFAULT));
        }
    }
}
