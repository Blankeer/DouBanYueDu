package com.douban.book.reader.helper;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore.Images.Media;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.PopupMenu.OnMenuItemClickListener;
import android.view.MenuItem;
import android.view.View;
import com.douban.book.reader.R;
import com.douban.book.reader.app.App;
import com.douban.book.reader.app.PageOpenHelper;
import com.douban.book.reader.fragment.BaseFragment;
import com.douban.book.reader.fragment.BaseFragment.OnActivityResultHandler;
import com.douban.book.reader.util.Logger;
import com.sina.weibo.sdk.component.ShareRequestParam;
import java.io.IOException;

public class SelectPhotoHelper implements OnMenuItemClickListener {
    private static final String TAG;
    private final BaseFragment mFragment;
    private final OnPhotoSelectedListener mListener;

    public interface OnPhotoSelectedListener {
        void onPhotoSelected(Bitmap bitmap);
    }

    static {
        TAG = SelectPhotoHelper.class.getSimpleName();
    }

    public SelectPhotoHelper(BaseFragment fragment, OnPhotoSelectedListener listener) {
        this.mFragment = fragment;
        this.mListener = listener;
    }

    public void performSelect(View clickedView) {
        PopupMenu menu = new PopupMenu(App.getThemed(), clickedView);
        menu.inflate(R.menu.edit_avatar);
        menu.setOnMenuItemClickListener(this);
        menu.show();
    }

    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_take_photo /*2131558981*/:
                PageOpenHelper.from(this.mFragment).onResult(new OnActivityResultHandler() {
                    public void onActivityResultedOk(Intent data) {
                        if (SelectPhotoHelper.this.mListener != null) {
                            SelectPhotoHelper.this.mListener.onPhotoSelected((Bitmap) data.getExtras().get(ShareRequestParam.RESP_UPLOAD_PIC_PARAM_DATA));
                        }
                    }
                }).open(new Intent("android.media.action.IMAGE_CAPTURE"));
                break;
            case R.id.action_select_photo /*2131558982*/:
                Intent photoPickerIntent = new Intent("android.intent.action.PICK");
                photoPickerIntent.setType("image/*");
                PageOpenHelper.from(this.mFragment).onResult(new OnActivityResultHandler() {
                    public void onActivityResultedOk(Intent data) {
                        Uri imageUri = data.getData();
                        try {
                            if (SelectPhotoHelper.this.mListener != null) {
                                SelectPhotoHelper.this.mListener.onPhotoSelected(Media.getBitmap(App.get().getContentResolver(), imageUri));
                            }
                        } catch (IOException e) {
                            Logger.e(SelectPhotoHelper.TAG, e);
                        }
                    }
                }).open(photoPickerIntent);
                break;
        }
        return false;
    }
}
