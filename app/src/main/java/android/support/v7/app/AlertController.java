package android.support.v7.app;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.DialogInterface.OnKeyListener;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.NestedScrollView.OnScrollChangeListener;
import android.support.v7.appcompat.R;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewParent;
import android.view.ViewStub;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.CursorAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import com.douban.book.reader.event.PageFlipEvent;
import com.tencent.mm.sdk.modelbase.BaseResp.ErrCode;
import java.lang.ref.WeakReference;

class AlertController {
    private ListAdapter mAdapter;
    private int mAlertDialogLayout;
    private final OnClickListener mButtonHandler;
    private Button mButtonNegative;
    private Message mButtonNegativeMessage;
    private CharSequence mButtonNegativeText;
    private Button mButtonNeutral;
    private Message mButtonNeutralMessage;
    private CharSequence mButtonNeutralText;
    private int mButtonPanelLayoutHint;
    private int mButtonPanelSideLayout;
    private Button mButtonPositive;
    private Message mButtonPositiveMessage;
    private CharSequence mButtonPositiveText;
    private int mCheckedItem;
    private final Context mContext;
    private View mCustomTitleView;
    private final AppCompatDialog mDialog;
    private Handler mHandler;
    private Drawable mIcon;
    private int mIconId;
    private ImageView mIconView;
    private int mListItemLayout;
    private int mListLayout;
    private ListView mListView;
    private CharSequence mMessage;
    private TextView mMessageView;
    private int mMultiChoiceItemLayout;
    private NestedScrollView mScrollView;
    private int mSingleChoiceItemLayout;
    private CharSequence mTitle;
    private TextView mTitleView;
    private View mView;
    private int mViewLayoutResId;
    private int mViewSpacingBottom;
    private int mViewSpacingLeft;
    private int mViewSpacingRight;
    private boolean mViewSpacingSpecified;
    private int mViewSpacingTop;
    private final Window mWindow;

    /* renamed from: android.support.v7.app.AlertController.3 */
    class AnonymousClass3 implements Runnable {
        final /* synthetic */ View val$bottom;
        final /* synthetic */ View val$top;

        AnonymousClass3(View view, View view2) {
            this.val$top = view;
            this.val$bottom = view2;
        }

        public void run() {
            AlertController.manageScrollIndicators(AlertController.this.mScrollView, this.val$top, this.val$bottom);
        }
    }

    /* renamed from: android.support.v7.app.AlertController.4 */
    class AnonymousClass4 implements OnScrollListener {
        final /* synthetic */ View val$bottom;
        final /* synthetic */ View val$top;

        AnonymousClass4(View view, View view2) {
            this.val$top = view;
            this.val$bottom = view2;
        }

        public void onScrollStateChanged(AbsListView view, int scrollState) {
        }

        public void onScroll(AbsListView v, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            AlertController.manageScrollIndicators(v, this.val$top, this.val$bottom);
        }
    }

    /* renamed from: android.support.v7.app.AlertController.5 */
    class AnonymousClass5 implements Runnable {
        final /* synthetic */ View val$bottom;
        final /* synthetic */ View val$top;

        AnonymousClass5(View view, View view2) {
            this.val$top = view;
            this.val$bottom = view2;
        }

        public void run() {
            AlertController.manageScrollIndicators(AlertController.this.mListView, this.val$top, this.val$bottom);
        }
    }

    public static class AlertParams {
        public ListAdapter mAdapter;
        public boolean mCancelable;
        public int mCheckedItem;
        public boolean[] mCheckedItems;
        public final Context mContext;
        public Cursor mCursor;
        public View mCustomTitleView;
        public boolean mForceInverseBackground;
        public Drawable mIcon;
        public int mIconAttrId;
        public int mIconId;
        public final LayoutInflater mInflater;
        public String mIsCheckedColumn;
        public boolean mIsMultiChoice;
        public boolean mIsSingleChoice;
        public CharSequence[] mItems;
        public String mLabelColumn;
        public CharSequence mMessage;
        public DialogInterface.OnClickListener mNegativeButtonListener;
        public CharSequence mNegativeButtonText;
        public DialogInterface.OnClickListener mNeutralButtonListener;
        public CharSequence mNeutralButtonText;
        public OnCancelListener mOnCancelListener;
        public OnMultiChoiceClickListener mOnCheckboxClickListener;
        public DialogInterface.OnClickListener mOnClickListener;
        public OnDismissListener mOnDismissListener;
        public OnItemSelectedListener mOnItemSelectedListener;
        public OnKeyListener mOnKeyListener;
        public OnPrepareListViewListener mOnPrepareListViewListener;
        public DialogInterface.OnClickListener mPositiveButtonListener;
        public CharSequence mPositiveButtonText;
        public boolean mRecycleOnMeasure;
        public CharSequence mTitle;
        public View mView;
        public int mViewLayoutResId;
        public int mViewSpacingBottom;
        public int mViewSpacingLeft;
        public int mViewSpacingRight;
        public boolean mViewSpacingSpecified;
        public int mViewSpacingTop;

        /* renamed from: android.support.v7.app.AlertController.AlertParams.1 */
        class AnonymousClass1 extends ArrayAdapter<CharSequence> {
            final /* synthetic */ ListView val$listView;

            AnonymousClass1(Context x0, int x1, int x2, CharSequence[] x3, ListView listView) {
                this.val$listView = listView;
                super(x0, x1, x2, x3);
            }

            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                if (AlertParams.this.mCheckedItems != null && AlertParams.this.mCheckedItems[position]) {
                    this.val$listView.setItemChecked(position, true);
                }
                return view;
            }
        }

        /* renamed from: android.support.v7.app.AlertController.AlertParams.2 */
        class AnonymousClass2 extends CursorAdapter {
            private final int mIsCheckedIndex;
            private final int mLabelIndex;
            final /* synthetic */ AlertController val$dialog;
            final /* synthetic */ ListView val$listView;

            AnonymousClass2(Context x0, Cursor x1, boolean x2, ListView listView, AlertController alertController) {
                this.val$listView = listView;
                this.val$dialog = alertController;
                super(x0, x1, x2);
                Cursor cursor = getCursor();
                this.mLabelIndex = cursor.getColumnIndexOrThrow(AlertParams.this.mLabelColumn);
                this.mIsCheckedIndex = cursor.getColumnIndexOrThrow(AlertParams.this.mIsCheckedColumn);
            }

            public void bindView(View view, Context context, Cursor cursor) {
                boolean z = true;
                ((CheckedTextView) view.findViewById(16908308)).setText(cursor.getString(this.mLabelIndex));
                ListView listView = this.val$listView;
                int position = cursor.getPosition();
                if (cursor.getInt(this.mIsCheckedIndex) != 1) {
                    z = false;
                }
                listView.setItemChecked(position, z);
            }

            public View newView(Context context, Cursor cursor, ViewGroup parent) {
                return AlertParams.this.mInflater.inflate(this.val$dialog.mMultiChoiceItemLayout, parent, false);
            }
        }

        /* renamed from: android.support.v7.app.AlertController.AlertParams.3 */
        class AnonymousClass3 implements OnItemClickListener {
            final /* synthetic */ AlertController val$dialog;

            AnonymousClass3(AlertController alertController) {
                this.val$dialog = alertController;
            }

            public void onItemClick(AdapterView<?> adapterView, View v, int position, long id) {
                AlertParams.this.mOnClickListener.onClick(this.val$dialog.mDialog, position);
                if (!AlertParams.this.mIsSingleChoice) {
                    this.val$dialog.mDialog.dismiss();
                }
            }
        }

        /* renamed from: android.support.v7.app.AlertController.AlertParams.4 */
        class AnonymousClass4 implements OnItemClickListener {
            final /* synthetic */ AlertController val$dialog;
            final /* synthetic */ ListView val$listView;

            AnonymousClass4(ListView listView, AlertController alertController) {
                this.val$listView = listView;
                this.val$dialog = alertController;
            }

            public void onItemClick(AdapterView<?> adapterView, View v, int position, long id) {
                if (AlertParams.this.mCheckedItems != null) {
                    AlertParams.this.mCheckedItems[position] = this.val$listView.isItemChecked(position);
                }
                AlertParams.this.mOnCheckboxClickListener.onClick(this.val$dialog.mDialog, position, this.val$listView.isItemChecked(position));
            }
        }

        public interface OnPrepareListViewListener {
            void onPrepareListView(ListView listView);
        }

        public AlertParams(Context context) {
            this.mIconId = 0;
            this.mIconAttrId = 0;
            this.mViewSpacingSpecified = false;
            this.mCheckedItem = -1;
            this.mRecycleOnMeasure = true;
            this.mContext = context;
            this.mCancelable = true;
            this.mInflater = (LayoutInflater) context.getSystemService("layout_inflater");
        }

        public void apply(AlertController dialog) {
            if (this.mCustomTitleView != null) {
                dialog.setCustomTitle(this.mCustomTitleView);
            } else {
                if (this.mTitle != null) {
                    dialog.setTitle(this.mTitle);
                }
                if (this.mIcon != null) {
                    dialog.setIcon(this.mIcon);
                }
                if (this.mIconId != 0) {
                    dialog.setIcon(this.mIconId);
                }
                if (this.mIconAttrId != 0) {
                    dialog.setIcon(dialog.getIconAttributeResId(this.mIconAttrId));
                }
            }
            if (this.mMessage != null) {
                dialog.setMessage(this.mMessage);
            }
            if (this.mPositiveButtonText != null) {
                dialog.setButton(-1, this.mPositiveButtonText, this.mPositiveButtonListener, null);
            }
            if (this.mNegativeButtonText != null) {
                dialog.setButton(-2, this.mNegativeButtonText, this.mNegativeButtonListener, null);
            }
            if (this.mNeutralButtonText != null) {
                dialog.setButton(-3, this.mNeutralButtonText, this.mNeutralButtonListener, null);
            }
            if (!(this.mItems == null && this.mCursor == null && this.mAdapter == null)) {
                createListView(dialog);
            }
            if (this.mView != null) {
                if (this.mViewSpacingSpecified) {
                    dialog.setView(this.mView, this.mViewSpacingLeft, this.mViewSpacingTop, this.mViewSpacingRight, this.mViewSpacingBottom);
                    return;
                }
                dialog.setView(this.mView);
            } else if (this.mViewLayoutResId != 0) {
                dialog.setView(this.mViewLayoutResId);
            }
        }

        private void createListView(AlertController dialog) {
            ListAdapter adapter;
            ListView listView = (ListView) this.mInflater.inflate(dialog.mListLayout, null);
            if (!this.mIsMultiChoice) {
                int layout;
                if (this.mIsSingleChoice) {
                    layout = dialog.mSingleChoiceItemLayout;
                } else {
                    layout = dialog.mListItemLayout;
                }
                if (this.mCursor != null) {
                    adapter = new SimpleCursorAdapter(this.mContext, layout, this.mCursor, new String[]{this.mLabelColumn}, new int[]{16908308});
                } else if (this.mAdapter != null) {
                    adapter = this.mAdapter;
                } else {
                    adapter = new CheckedItemAdapter(this.mContext, layout, 16908308, this.mItems);
                }
            } else if (this.mCursor == null) {
                adapter = new AnonymousClass1(this.mContext, dialog.mMultiChoiceItemLayout, 16908308, this.mItems, listView);
            } else {
                ListAdapter anonymousClass2 = new AnonymousClass2(this.mContext, this.mCursor, false, listView, dialog);
            }
            if (this.mOnPrepareListViewListener != null) {
                this.mOnPrepareListViewListener.onPrepareListView(listView);
            }
            dialog.mAdapter = adapter;
            dialog.mCheckedItem = this.mCheckedItem;
            if (this.mOnClickListener != null) {
                listView.setOnItemClickListener(new AnonymousClass3(dialog));
            } else if (this.mOnCheckboxClickListener != null) {
                listView.setOnItemClickListener(new AnonymousClass4(listView, dialog));
            }
            if (this.mOnItemSelectedListener != null) {
                listView.setOnItemSelectedListener(this.mOnItemSelectedListener);
            }
            if (this.mIsSingleChoice) {
                listView.setChoiceMode(1);
            } else if (this.mIsMultiChoice) {
                listView.setChoiceMode(2);
            }
            dialog.mListView = listView;
        }
    }

    private static final class ButtonHandler extends Handler {
        private static final int MSG_DISMISS_DIALOG = 1;
        private WeakReference<DialogInterface> mDialog;

        public ButtonHandler(DialogInterface dialog) {
            this.mDialog = new WeakReference(dialog);
        }

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case ErrCode.ERR_SENT_FAILED /*-3*/:
                case ErrCode.ERR_USER_CANCEL /*-2*/:
                case PageFlipEvent.FLIP_TO_PREV /*-1*/:
                    ((DialogInterface.OnClickListener) msg.obj).onClick((DialogInterface) this.mDialog.get(), msg.what);
                case MSG_DISMISS_DIALOG /*1*/:
                    ((DialogInterface) msg.obj).dismiss();
                default:
            }
        }
    }

    private static class CheckedItemAdapter extends ArrayAdapter<CharSequence> {
        public CheckedItemAdapter(Context context, int resource, int textViewResourceId, CharSequence[] objects) {
            super(context, resource, textViewResourceId, objects);
        }

        public boolean hasStableIds() {
            return true;
        }

        public long getItemId(int position) {
            return (long) position;
        }
    }

    /* renamed from: android.support.v7.app.AlertController.2 */
    class AnonymousClass2 implements OnScrollChangeListener {
        final /* synthetic */ View val$bottom;
        final /* synthetic */ View val$top;

        AnonymousClass2(View view, View view2) {
            this.val$top = view;
            this.val$bottom = view2;
        }

        public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
            AlertController.manageScrollIndicators(v, this.val$top, this.val$bottom);
        }
    }

    public AlertController(Context context, AppCompatDialog di, Window window) {
        this.mViewSpacingSpecified = false;
        this.mIconId = 0;
        this.mCheckedItem = -1;
        this.mButtonPanelLayoutHint = 0;
        this.mButtonHandler = new OnClickListener() {
            public void onClick(View v) {
                Message m;
                if (v == AlertController.this.mButtonPositive && AlertController.this.mButtonPositiveMessage != null) {
                    m = Message.obtain(AlertController.this.mButtonPositiveMessage);
                } else if (v == AlertController.this.mButtonNegative && AlertController.this.mButtonNegativeMessage != null) {
                    m = Message.obtain(AlertController.this.mButtonNegativeMessage);
                } else if (v != AlertController.this.mButtonNeutral || AlertController.this.mButtonNeutralMessage == null) {
                    m = null;
                } else {
                    m = Message.obtain(AlertController.this.mButtonNeutralMessage);
                }
                if (m != null) {
                    m.sendToTarget();
                }
                AlertController.this.mHandler.obtainMessage(1, AlertController.this.mDialog).sendToTarget();
            }
        };
        this.mContext = context;
        this.mDialog = di;
        this.mWindow = window;
        this.mHandler = new ButtonHandler(di);
        TypedArray a = context.obtainStyledAttributes(null, R.styleable.AlertDialog, R.attr.alertDialogStyle, 0);
        this.mAlertDialogLayout = a.getResourceId(R.styleable.AlertDialog_android_layout, 0);
        this.mButtonPanelSideLayout = a.getResourceId(R.styleable.AlertDialog_buttonPanelSideLayout, 0);
        this.mListLayout = a.getResourceId(R.styleable.AlertDialog_listLayout, 0);
        this.mMultiChoiceItemLayout = a.getResourceId(R.styleable.AlertDialog_multiChoiceItemLayout, 0);
        this.mSingleChoiceItemLayout = a.getResourceId(R.styleable.AlertDialog_singleChoiceItemLayout, 0);
        this.mListItemLayout = a.getResourceId(R.styleable.AlertDialog_listItemLayout, 0);
        a.recycle();
    }

    static boolean canTextInput(View v) {
        if (v.onCheckIsTextEditor()) {
            return true;
        }
        if (!(v instanceof ViewGroup)) {
            return false;
        }
        ViewGroup vg = (ViewGroup) v;
        int i = vg.getChildCount();
        while (i > 0) {
            i--;
            if (canTextInput(vg.getChildAt(i))) {
                return true;
            }
        }
        return false;
    }

    public void installContent() {
        this.mDialog.supportRequestWindowFeature(1);
        this.mDialog.setContentView(selectContentView());
        setupView();
    }

    private int selectContentView() {
        if (this.mButtonPanelSideLayout == 0) {
            return this.mAlertDialogLayout;
        }
        if (this.mButtonPanelLayoutHint == 1) {
            return this.mButtonPanelSideLayout;
        }
        return this.mAlertDialogLayout;
    }

    public void setTitle(CharSequence title) {
        this.mTitle = title;
        if (this.mTitleView != null) {
            this.mTitleView.setText(title);
        }
    }

    public void setCustomTitle(View customTitleView) {
        this.mCustomTitleView = customTitleView;
    }

    public void setMessage(CharSequence message) {
        this.mMessage = message;
        if (this.mMessageView != null) {
            this.mMessageView.setText(message);
        }
    }

    public void setView(int layoutResId) {
        this.mView = null;
        this.mViewLayoutResId = layoutResId;
        this.mViewSpacingSpecified = false;
    }

    public void setView(View view) {
        this.mView = view;
        this.mViewLayoutResId = 0;
        this.mViewSpacingSpecified = false;
    }

    public void setView(View view, int viewSpacingLeft, int viewSpacingTop, int viewSpacingRight, int viewSpacingBottom) {
        this.mView = view;
        this.mViewLayoutResId = 0;
        this.mViewSpacingSpecified = true;
        this.mViewSpacingLeft = viewSpacingLeft;
        this.mViewSpacingTop = viewSpacingTop;
        this.mViewSpacingRight = viewSpacingRight;
        this.mViewSpacingBottom = viewSpacingBottom;
    }

    public void setButtonPanelLayoutHint(int layoutHint) {
        this.mButtonPanelLayoutHint = layoutHint;
    }

    public void setButton(int whichButton, CharSequence text, DialogInterface.OnClickListener listener, Message msg) {
        if (msg == null && listener != null) {
            msg = this.mHandler.obtainMessage(whichButton, listener);
        }
        switch (whichButton) {
            case ErrCode.ERR_SENT_FAILED /*-3*/:
                this.mButtonNeutralText = text;
                this.mButtonNeutralMessage = msg;
            case ErrCode.ERR_USER_CANCEL /*-2*/:
                this.mButtonNegativeText = text;
                this.mButtonNegativeMessage = msg;
            case PageFlipEvent.FLIP_TO_PREV /*-1*/:
                this.mButtonPositiveText = text;
                this.mButtonPositiveMessage = msg;
            default:
                throw new IllegalArgumentException("Button does not exist");
        }
    }

    public void setIcon(int resId) {
        this.mIcon = null;
        this.mIconId = resId;
        if (this.mIconView == null) {
            return;
        }
        if (resId != 0) {
            this.mIconView.setImageResource(this.mIconId);
        } else {
            this.mIconView.setVisibility(8);
        }
    }

    public void setIcon(Drawable icon) {
        this.mIcon = icon;
        this.mIconId = 0;
        if (this.mIconView == null) {
            return;
        }
        if (icon != null) {
            this.mIconView.setImageDrawable(icon);
        } else {
            this.mIconView.setVisibility(8);
        }
    }

    public int getIconAttributeResId(int attrId) {
        TypedValue out = new TypedValue();
        this.mContext.getTheme().resolveAttribute(attrId, out, true);
        return out.resourceId;
    }

    public ListView getListView() {
        return this.mListView;
    }

    public Button getButton(int whichButton) {
        switch (whichButton) {
            case ErrCode.ERR_SENT_FAILED /*-3*/:
                return this.mButtonNeutral;
            case ErrCode.ERR_USER_CANCEL /*-2*/:
                return this.mButtonNegative;
            case PageFlipEvent.FLIP_TO_PREV /*-1*/:
                return this.mButtonPositive;
            default:
                return null;
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return this.mScrollView != null && this.mScrollView.executeKeyEvent(event);
    }

    public boolean onKeyUp(int keyCode, KeyEvent event) {
        return this.mScrollView != null && this.mScrollView.executeKeyEvent(event);
    }

    @Nullable
    private ViewGroup resolvePanel(@Nullable View customPanel, @Nullable View defaultPanel) {
        if (customPanel == null) {
            if (defaultPanel instanceof ViewStub) {
                defaultPanel = ((ViewStub) defaultPanel).inflate();
            }
            return (ViewGroup) defaultPanel;
        }
        if (defaultPanel != null) {
            ViewParent parent = defaultPanel.getParent();
            if (parent instanceof ViewGroup) {
                ((ViewGroup) parent).removeView(defaultPanel);
            }
        }
        if (customPanel instanceof ViewStub) {
            customPanel = ((ViewStub) customPanel).inflate();
        }
        return (ViewGroup) customPanel;
    }

    private void setupView() {
        View parentPanel = this.mWindow.findViewById(R.id.parentPanel);
        View defaultTopPanel = parentPanel.findViewById(R.id.topPanel);
        View defaultContentPanel = parentPanel.findViewById(R.id.contentPanel);
        View defaultButtonPanel = parentPanel.findViewById(R.id.buttonPanel);
        ViewGroup customPanel = (ViewGroup) parentPanel.findViewById(R.id.customPanel);
        setupCustomContent(customPanel);
        View customTopPanel = customPanel.findViewById(R.id.topPanel);
        View customContentPanel = customPanel.findViewById(R.id.contentPanel);
        View customButtonPanel = customPanel.findViewById(R.id.buttonPanel);
        ViewGroup topPanel = resolvePanel(customTopPanel, defaultTopPanel);
        ViewGroup contentPanel = resolvePanel(customContentPanel, defaultContentPanel);
        ViewGroup buttonPanel = resolvePanel(customButtonPanel, defaultButtonPanel);
        setupContent(contentPanel);
        setupButtons(buttonPanel);
        setupTitle(topPanel);
        boolean hasCustomPanel = (customPanel == null || customPanel.getVisibility() == 8) ? false : true;
        boolean hasTopPanel = (topPanel == null || topPanel.getVisibility() == 8) ? false : true;
        boolean hasButtonPanel = (buttonPanel == null || buttonPanel.getVisibility() == 8) ? false : true;
        if (!(hasButtonPanel || contentPanel == null)) {
            View spacer = contentPanel.findViewById(R.id.textSpacerNoButtons);
            if (spacer != null) {
                spacer.setVisibility(0);
            }
        }
        if (hasTopPanel && this.mScrollView != null) {
            this.mScrollView.setClipToPadding(true);
        }
        if (!hasCustomPanel) {
            View content = this.mListView != null ? this.mListView : this.mScrollView;
            if (content != null) {
                setScrollIndicators(contentPanel, content, (hasTopPanel ? 1 : 0) | (hasButtonPanel ? 2 : 0), 3);
            }
        }
        ListView listView = this.mListView;
        if (listView != null && this.mAdapter != null) {
            listView.setAdapter(this.mAdapter);
            int checkedItem = this.mCheckedItem;
            if (checkedItem > -1) {
                listView.setItemChecked(checkedItem, true);
                listView.setSelection(checkedItem);
            }
        }
    }

    private void setScrollIndicators(ViewGroup contentPanel, View content, int indicators, int mask) {
        View indicatorUp = this.mWindow.findViewById(R.id.scrollIndicatorUp);
        View indicatorDown = this.mWindow.findViewById(R.id.scrollIndicatorDown);
        if (VERSION.SDK_INT >= 23) {
            ViewCompat.setScrollIndicators(content, indicators, mask);
            if (indicatorUp != null) {
                contentPanel.removeView(indicatorUp);
            }
            if (indicatorDown != null) {
                contentPanel.removeView(indicatorDown);
                return;
            }
            return;
        }
        if (indicatorUp != null && (indicators & 1) == 0) {
            contentPanel.removeView(indicatorUp);
            indicatorUp = null;
        }
        if (indicatorDown != null && (indicators & 2) == 0) {
            contentPanel.removeView(indicatorDown);
            indicatorDown = null;
        }
        if (indicatorUp != null || indicatorDown != null) {
            View top = indicatorUp;
            View bottom = indicatorDown;
            if (this.mMessage != null) {
                this.mScrollView.setOnScrollChangeListener(new AnonymousClass2(top, bottom));
                this.mScrollView.post(new AnonymousClass3(top, bottom));
            } else if (this.mListView != null) {
                this.mListView.setOnScrollListener(new AnonymousClass4(top, bottom));
                this.mListView.post(new AnonymousClass5(top, bottom));
            } else {
                if (top != null) {
                    contentPanel.removeView(top);
                }
                if (bottom != null) {
                    contentPanel.removeView(bottom);
                }
            }
        }
    }

    private void setupCustomContent(ViewGroup customPanel) {
        View customView;
        boolean hasCustomView = false;
        if (this.mView != null) {
            customView = this.mView;
        } else if (this.mViewLayoutResId != 0) {
            customView = LayoutInflater.from(this.mContext).inflate(this.mViewLayoutResId, customPanel, false);
        } else {
            customView = null;
        }
        if (customView != null) {
            hasCustomView = true;
        }
        if (!(hasCustomView && canTextInput(customView))) {
            this.mWindow.setFlags(AccessibilityNodeInfoCompat.ACTION_SET_SELECTION, AccessibilityNodeInfoCompat.ACTION_SET_SELECTION);
        }
        if (hasCustomView) {
            FrameLayout custom = (FrameLayout) this.mWindow.findViewById(R.id.custom);
            custom.addView(customView, new LayoutParams(-1, -1));
            if (this.mViewSpacingSpecified) {
                custom.setPadding(this.mViewSpacingLeft, this.mViewSpacingTop, this.mViewSpacingRight, this.mViewSpacingBottom);
            }
            if (this.mListView != null) {
                ((LinearLayout.LayoutParams) customPanel.getLayoutParams()).weight = 0.0f;
                return;
            }
            return;
        }
        customPanel.setVisibility(8);
    }

    private void setupTitle(ViewGroup topPanel) {
        boolean hasTextTitle = false;
        if (this.mCustomTitleView != null) {
            topPanel.addView(this.mCustomTitleView, 0, new LayoutParams(-1, -2));
            this.mWindow.findViewById(R.id.title_template).setVisibility(8);
            return;
        }
        this.mIconView = (ImageView) this.mWindow.findViewById(16908294);
        if (!TextUtils.isEmpty(this.mTitle)) {
            hasTextTitle = true;
        }
        if (hasTextTitle) {
            this.mTitleView = (TextView) this.mWindow.findViewById(R.id.alertTitle);
            this.mTitleView.setText(this.mTitle);
            if (this.mIconId != 0) {
                this.mIconView.setImageResource(this.mIconId);
                return;
            } else if (this.mIcon != null) {
                this.mIconView.setImageDrawable(this.mIcon);
                return;
            } else {
                this.mTitleView.setPadding(this.mIconView.getPaddingLeft(), this.mIconView.getPaddingTop(), this.mIconView.getPaddingRight(), this.mIconView.getPaddingBottom());
                this.mIconView.setVisibility(8);
                return;
            }
        }
        this.mWindow.findViewById(R.id.title_template).setVisibility(8);
        this.mIconView.setVisibility(8);
        topPanel.setVisibility(8);
    }

    private void setupContent(ViewGroup contentPanel) {
        this.mScrollView = (NestedScrollView) this.mWindow.findViewById(R.id.scrollView);
        this.mScrollView.setFocusable(false);
        this.mScrollView.setNestedScrollingEnabled(false);
        this.mMessageView = (TextView) contentPanel.findViewById(16908299);
        if (this.mMessageView != null) {
            if (this.mMessage != null) {
                this.mMessageView.setText(this.mMessage);
                return;
            }
            this.mMessageView.setVisibility(8);
            this.mScrollView.removeView(this.mMessageView);
            if (this.mListView != null) {
                ViewGroup scrollParent = (ViewGroup) this.mScrollView.getParent();
                int childIndex = scrollParent.indexOfChild(this.mScrollView);
                scrollParent.removeViewAt(childIndex);
                scrollParent.addView(this.mListView, childIndex, new LayoutParams(-1, -1));
                return;
            }
            contentPanel.setVisibility(8);
        }
    }

    private static void manageScrollIndicators(View v, View upIndicator, View downIndicator) {
        int i = 0;
        if (upIndicator != null) {
            upIndicator.setVisibility(ViewCompat.canScrollVertically(v, -1) ? 0 : 4);
        }
        if (downIndicator != null) {
            if (!ViewCompat.canScrollVertically(v, 1)) {
                i = 4;
            }
            downIndicator.setVisibility(i);
        }
    }

    private void setupButtons(ViewGroup buttonPanel) {
        boolean hasButtons = false;
        int whichButtons = 0;
        this.mButtonPositive = (Button) buttonPanel.findViewById(16908313);
        this.mButtonPositive.setOnClickListener(this.mButtonHandler);
        if (TextUtils.isEmpty(this.mButtonPositiveText)) {
            this.mButtonPositive.setVisibility(8);
        } else {
            this.mButtonPositive.setText(this.mButtonPositiveText);
            this.mButtonPositive.setVisibility(0);
            whichButtons = 0 | 1;
        }
        this.mButtonNegative = (Button) buttonPanel.findViewById(16908314);
        this.mButtonNegative.setOnClickListener(this.mButtonHandler);
        if (TextUtils.isEmpty(this.mButtonNegativeText)) {
            this.mButtonNegative.setVisibility(8);
        } else {
            this.mButtonNegative.setText(this.mButtonNegativeText);
            this.mButtonNegative.setVisibility(0);
            whichButtons |= 2;
        }
        this.mButtonNeutral = (Button) buttonPanel.findViewById(16908315);
        this.mButtonNeutral.setOnClickListener(this.mButtonHandler);
        if (TextUtils.isEmpty(this.mButtonNeutralText)) {
            this.mButtonNeutral.setVisibility(8);
        } else {
            this.mButtonNeutral.setText(this.mButtonNeutralText);
            this.mButtonNeutral.setVisibility(0);
            whichButtons |= 4;
        }
        if (whichButtons != 0) {
            hasButtons = true;
        }
        if (!hasButtons) {
            buttonPanel.setVisibility(8);
        }
    }
}
