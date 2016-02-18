package com.douban.book.reader.fragment;

import android.net.Uri;
import android.support.v4.app.Fragment;
import com.douban.book.reader.adapter.BaseArrayAdapter;
import com.douban.book.reader.adapter.SimpleStringAdapter;
import com.douban.book.reader.util.FileUtils;
import io.realm.internal.Table;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.ItemClick;

@EFragment
public class FileListFragment extends BaseListFragment<File> {
    private File mCurrentFile;
    @FragmentArg
    File path;

    public List<File> onLoadData() {
        return loadData(this.path);
    }

    public BaseArrayAdapter<File> onCreateAdapter() {
        return new SimpleStringAdapter<File>() {
            protected String strValueOf(File item) {
                String str = "%s%s";
                Object[] objArr = new Object[2];
                objArr[0] = item.getName();
                objArr[1] = item.isDirectory() ? "/" : Table.STRING_DEFAULT_VALUE;
                return String.format(str, objArr);
            }
        };
    }

    @ItemClick({2131558593})
    void onItemClick(File file) {
        if (file.isDirectory()) {
            updateList(file);
        } else {
            GeneralWebFragment_.builder().url(Uri.fromFile(file).toString()).build().setTitle(file.getName()).showAsActivity((Fragment) this);
        }
    }

    public void onBackPressed() {
        if (this.mCurrentFile.equals(this.path)) {
            super.onBackPressed();
        } else {
            updateList(this.mCurrentFile.getParentFile());
        }
    }

    private void updateList(File file) {
        this.mAdapter.replace(loadData(file));
    }

    private List<File> loadData(File file) {
        setTitle(FileUtils.formatRelativePath(file, this.path));
        this.mCurrentFile = file;
        if (file.isDirectory()) {
            return Arrays.asList(file.listFiles());
        }
        return new ArrayList();
    }
}
