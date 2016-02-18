package com.douban.book.reader.fragment;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.douban.book.reader.R;
import com.douban.book.reader.app.App;
import com.douban.book.reader.controller.TaskController;
import com.douban.book.reader.fragment.AlertDialogFragment.Builder;
import com.douban.book.reader.manager.exception.DataLoadException;
import com.douban.book.reader.util.ExceptionUtils;
import com.douban.book.reader.util.Logger;
import com.douban.book.reader.util.Res;
import com.douban.book.reader.util.StringUtils;
import com.douban.book.reader.util.ToastUtils;
import com.douban.book.reader.util.ViewUtils;
import com.mcxiaoke.next.ui.widget.AdvancedShareActionProvider;
import java.util.concurrent.Callable;
import natalya.os.TaskExecutor.TaskCallback;

public abstract class BaseEditFragment extends BaseFragment {
    private static final int SHOW_INPUT_NUM_LIMIT = 30;
    private static final int TEXT_LIMIT = 140;
    private boolean mAllowEmptyPost;
    private LinearLayout mBottomView;
    private TextView mCharCount;
    private EditText mEditor;
    private CharSequence mInitialTextInEditor;
    private MenuItem mMenuItemSubmit;
    private int mTextLimit;
    private LinearLayout mTopView;

    /* renamed from: com.douban.book.reader.fragment.BaseEditFragment.4 */
    class AnonymousClass4 implements Callable<Object> {
        final /* synthetic */ String val$content;

        AnonymousClass4(String str) {
            this.val$content = str;
        }

        public Object call() throws Exception {
            BaseEditFragment.this.postToServer(this.val$content);
            return null;
        }
    }

    protected abstract void postToServer(String str) throws DataLoadException;

    protected BaseEditFragment() {
        this.mTextLimit = AdvancedShareActionProvider.WEIGHT_MAX;
        setDrawerEnabled(false);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.frag_base_edit, container, false);
        this.mTopView = (LinearLayout) view.findViewById(R.id.top_view);
        this.mBottomView = (LinearLayout) view.findViewById(R.id.bottom_view);
        this.mEditor = (EditText) view.findViewById(R.id.edit);
        this.mCharCount = (TextView) view.findViewById(R.id.char_count);
        this.mEditor.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            public void afterTextChanged(Editable s) {
                int available = BaseEditFragment.this.mTextLimit - s.length();
                ViewUtils.showTextIf(available <= BaseEditFragment.SHOW_INPUT_NUM_LIMIT, BaseEditFragment.this.mCharCount, String.valueOf(available));
                BaseEditFragment.this.mCharCount.setTextColor(Res.getColor(available < 0 ? R.array.red : R.array.green));
            }
        });
        return view;
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        showLoadingDialog();
        TaskController.run(new Runnable() {
            public void run() {
                try {
                    BaseEditFragment.this.initData();
                    App.get().runOnUiThread(new Runnable() {
                        public void run() {
                            BaseEditFragment.this.onDataReady();
                        }
                    });
                } catch (Throwable e) {
                    Logger.e(BaseEditFragment.this.TAG, e);
                    ToastUtils.showToast(e, (int) R.string.toast_general_load_failed);
                    BaseEditFragment.this.finishSkippingCheck();
                } finally {
                    BaseEditFragment.this.dismissLoadingDialog();
                }
            }
        });
    }

    protected void hideEditor() {
        ViewUtils.gone(this.mEditor, this.mCharCount);
    }

    protected void onDataReady() {
    }

    protected void addTopView(View view) {
        if (view != null && this.mTopView != null) {
            decorateTopBottomView(view);
            this.mTopView.addView(view);
            this.mTopView.addView(ViewUtils.createDivider());
        }
    }

    protected void addBottomView(View view) {
        if (view != null && this.mBottomView != null) {
            decorateTopBottomView(view);
            this.mBottomView.addView(ViewUtils.createDivider());
            this.mBottomView.addView(view);
        }
    }

    protected void decorateTopBottomView(View view) {
        ViewUtils.setHorizontalPaddingResId(view, R.dimen.page_horizontal_padding);
        ViewUtils.setVerticalPaddingResId(view, R.dimen.general_subview_vertical_padding_normal);
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.editor, menu);
        this.mMenuItemSubmit = menu.findItem(R.id.action_submit);
        super.onCreateOptionsMenu(menu, inflater);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_submit /*2131558983*/:
                if (shouldSubmit()) {
                    submit();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    protected boolean shouldSubmit() {
        CharSequence content = this.mEditor.getText();
        if (!this.mAllowEmptyPost && StringUtils.isEmpty(StringUtils.trimWhitespace(content))) {
            ToastUtils.showToast((int) R.string.toast_input_empty);
            return false;
        } else if (content.length() <= this.mTextLimit) {
            return true;
        } else {
            ToastUtils.showToast(Res.getString(R.string.toast_input_exceeded_limit, Integer.valueOf(this.mTextLimit)));
            return false;
        }
    }

    protected CharSequence getSucceedToastMessage() {
        return null;
    }

    protected CharSequence getFailedToastMessage(Throwable e) {
        return ExceptionUtils.getHumanReadableMessage(e, (int) R.string.toast_general_op_failed);
    }

    protected void initData() throws DataLoadException {
    }

    public BaseEditFragment emptyPostAllowed(boolean allowEmptyPost) {
        this.mAllowEmptyPost = allowEmptyPost;
        return this;
    }

    public BaseEditFragment hasTextLimit(boolean hasTextLimit) {
        this.mTextLimit = hasTextLimit ? TEXT_LIMIT : AdvancedShareActionProvider.WEIGHT_MAX;
        return this;
    }

    public void setContent(CharSequence content) {
        this.mEditor.setText(content);
        this.mInitialTextInEditor = content;
    }

    public void setSelected() {
        this.mEditor.selectAll();
    }

    public void setHint(int resId) {
        this.mEditor.setHint(resId);
    }

    protected boolean shouldBeConsideredAsSucceed(Throwable e) {
        return false;
    }

    protected void onPostSucceed() {
        enableMenuItems(this.mMenuItemSubmit);
        dismissLoadingDialog();
        if (StringUtils.isNotEmpty(getSucceedToastMessage())) {
            ToastUtils.showToast(getSucceedToastMessage());
        }
        finishSkippingCheck();
    }

    protected void onPostFailure(Throwable e) {
        enableMenuItems(this.mMenuItemSubmit);
        dismissLoadingDialog();
        if (StringUtils.isNotEmpty(getFailedToastMessage(e))) {
            ToastUtils.showToast(getFailedToastMessage(e));
        }
    }

    public boolean shouldFinish() {
        if (StringUtils.equals(this.mInitialTextInEditor, this.mEditor.getText())) {
            return super.shouldFinish();
        }
        new Builder().setMessage((int) R.string.dialog_message_discard_content_confirm).setPositiveButton((int) R.string.dialog_button_discard_content, new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                BaseEditFragment.this.finishSkippingCheck();
            }
        }).setNegativeButton((int) R.string.dialog_button_return_to_edit, null).create().show();
        return false;
    }

    private void submit() {
        String content = this.mEditor.getText().toString();
        disableMenuItems(this.mMenuItemSubmit);
        showBlockingLoadingDialog();
        TaskController.getInstance().execute(new AnonymousClass4(content), new TaskCallback<Object>() {
            public void onTaskSuccess(Object o, Bundle extras, Object object) {
                BaseEditFragment.this.onPostSucceed();
            }

            public void onTaskFailure(Throwable e, Bundle extras) {
                if (BaseEditFragment.this.shouldBeConsideredAsSucceed(e)) {
                    BaseEditFragment.this.onPostSucceed();
                } else {
                    BaseEditFragment.this.onPostFailure(e);
                }
            }
        }, this);
    }
}
