package com.douban.book.reader.fragment.share;

import android.os.Build;
import android.os.Build.VERSION;
import android.view.View;
import android.widget.EditText;
import com.douban.book.reader.R;
import com.douban.book.reader.content.paragraph.Paragraph;
import com.douban.book.reader.content.paragraph.RichTextParagraph;
import com.douban.book.reader.content.paragraph.decorator.IconDecorator;
import com.douban.book.reader.entity.DummyEntity;
import com.douban.book.reader.fragment.BaseEditFragment;
import com.douban.book.reader.manager.exception.DataLoadException;
import com.douban.book.reader.network.client.RestClient;
import com.douban.book.reader.network.param.RequestParam;
import com.douban.book.reader.theme.Theme;
import com.douban.book.reader.theme.ThemedAttrs;
import com.douban.book.reader.util.AppInfo;
import com.douban.book.reader.util.CharUtils;
import com.douban.book.reader.util.Logger.Feedback;
import com.douban.book.reader.util.Res;
import com.douban.book.reader.util.ThemedUtils;
import com.douban.book.reader.util.Utils;
import com.douban.book.reader.util.ViewUtils;
import com.douban.book.reader.view.ParagraphView;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.ViewById;

@EFragment
public class FeedbackEditFragment extends BaseEditFragment {
    @FragmentArg
    boolean isReport;
    @ViewById(2131558589)
    View mBottomView;
    private EditText mContactEditor;

    @AfterViews
    void init() {
        setTitle((int) R.string.title_feedback);
        setHint(R.string.hint_input_feedback);
        ThemedAttrs.ofView(this.mBottomView).append(R.attr.backgroundColorArray, Integer.valueOf(R.array.input_bg_color));
        ThemedUtils.updateView(this.mBottomView);
        if (this.isReport) {
            emptyPostAllowed(true);
            setHint(R.string.hint_input_feedback_report);
        }
        hasTextLimit(false);
        addBottomView(createBottomView());
        addBottomView(createInfoBottomView());
    }

    protected View createBottomView() {
        this.mContactEditor = (EditText) View.inflate(getActivity(), R.layout.view_edit_text_single, null);
        this.mContactEditor.setHint(R.string.hint_input_contact);
        ViewUtils.setHorizontalPadding(this.mContactEditor, 0);
        ThemedAttrs.ofView(this.mContactEditor).append(R.attr.backgroundColorArray, Integer.valueOf(R.array.input_multiline_bg_color)).updateView();
        return this.mContactEditor;
    }

    private View createInfoBottomView() {
        ParagraphView textView = new ParagraphView(getActivity());
        ThemedAttrs.ofView(textView).append(R.attr.textColorArray, Integer.valueOf(R.array.secondary_text_color));
        Utils.changeFonts(textView);
        textView.setTextSize(0, Res.getDimension(R.dimen.general_font_size_small));
        Paragraph paragraph = new RichTextParagraph();
        paragraph.setText(Res.getString(R.string.text_guide_to_weixin_account));
        paragraph.setDecorator(new IconDecorator().icon(Theme.isNight() ? R.drawable.v_tips_night : R.drawable.v_tips).marginWithText(CharUtils.FULL_WIDTH_CHAR_OFFSET_ADJUSTMENT_RATIO).iconSizeRatio(1.35f).verticalOffsetRatio(0.05f));
        textView.setParagraph(paragraph);
        return textView;
    }

    protected void postToServer(String content) throws DataLoadException {
        try {
            String clientInfo = "\n\n==== AndroidReader Info ====\nManufacturer    : " + Build.MANUFACTURER + "\nModel           : " + Build.MODEL + "\nAndroid Version : " + VERSION.RELEASE + "\nArk Version     : " + AppInfo.getVersionName() + "\nContact         : " + this.mContactEditor.getText().toString();
            if (this.isReport) {
                content = content + "\n" + Feedback.get();
            }
            new RestClient("/feedback", DummyEntity.class).post(RequestParam.json().append("text", content + clientInfo));
            if (this.isReport) {
                Feedback.clear();
            }
        } catch (Throwable e) {
            throw new DataLoadException(e);
        }
    }

    protected CharSequence getSucceedToastMessage() {
        return Res.getString(R.string.publish_feedback_success_msg);
    }
}
