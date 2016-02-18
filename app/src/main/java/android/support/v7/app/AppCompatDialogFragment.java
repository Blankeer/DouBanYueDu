package android.support.v7.app;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import u.aly.dx;

public class AppCompatDialogFragment extends DialogFragment {
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AppCompatDialog(getActivity(), getTheme());
    }

    public void setupDialog(Dialog dialog, int style) {
        if (dialog instanceof AppCompatDialog) {
            AppCompatDialog acd = (AppCompatDialog) dialog;
            switch (style) {
                case dx.b /*1*/:
                case dx.c /*2*/:
                    break;
                case dx.d /*3*/:
                    dialog.getWindow().addFlags(24);
                    break;
                default:
                    return;
            }
            acd.supportRequestWindowFeature(1);
            return;
        }
        super.setupDialog(dialog, style);
    }
}
