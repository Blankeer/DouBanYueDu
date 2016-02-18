package natalya.util;

import android.widget.ImageView;
import java.util.ArrayList;
import java.util.List;

public class ImageViewUnbinder {
    protected List<ImageView> views;

    public ImageViewUnbinder() {
        this.views = new ArrayList();
    }

    public void register(ImageView v) {
        this.views.add(v);
    }

    public void unbind() {
        for (int i = 0; i < this.views.size(); i++) {
            ImageView v = (ImageView) this.views.get(i);
            if (v != null) {
                v.setImageBitmap(null);
            }
        }
        this.views.clear();
    }
}
