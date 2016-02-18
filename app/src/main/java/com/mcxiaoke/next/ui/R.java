package com.mcxiaoke.next.ui;

public final class R {

    public static final class attr {
        public static final int ari_stretch = 2130772014;
        public static final int ci_border = 2130772021;
        public static final int ci_border_color = 2130772023;
        public static final int ci_border_width = 2130772022;
        public static final int ci_shadow = 2130772024;
        public static final int font_path = 2130772044;
        public static final int font_use_cache = 2130772045;
        public static final int fri_orientation = 2130772035;
        public static final int ratio = 2130772036;
    }

    public static final class color {
        public static final int spv_text_primary = 2131493033;
        public static final int spv_text_secondary = 2131493034;
    }

    public static final class dimen {
        public static final int spv_font_size = 2131230863;
        public static final int spv_padding = 2131230864;
        public static final int spv_prefered_height = 2131230865;
    }

    public static final class id {
        public static final int horizontal = 2131558433;
        public static final int spv_progress = 2131558408;
        public static final int spv_progress_bar = 2131558409;
        public static final int spv_progress_text = 2131558410;
        public static final int spv_text = 2131558411;
        public static final int vertical = 2131558434;
    }

    public static final class layout {
        public static final int cv_simple_progress = 2130903088;
    }

    public static final class string {
        public static final int share_action_provider_expand_label = 2131099668;
        public static final int share_action_provider_target_not_found = 2131099669;
        public static final int spv_default_progress_text = 2131099670;
        public static final int spv_default_text = 2131099671;
    }

    public static final class style {
        public static final int AppBaseTheme = 2131296396;
        public static final int AppTheme = 2131296398;
    }

    public static final class styleable {
        public static final int[] AspectRatioImageView;
        public static final int AspectRatioImageView_ari_stretch = 0;
        public static final int[] CircularImageView;
        public static final int CircularImageView_ci_border = 0;
        public static final int CircularImageView_ci_border_color = 2;
        public static final int CircularImageView_ci_border_width = 1;
        public static final int CircularImageView_ci_shadow = 3;
        public static final int[] FixedRatioImageView;
        public static final int FixedRatioImageView_fri_orientation = 0;
        public static final int FixedRatioImageView_ratio = 1;
        public static final int[] FontFaceStyle;
        public static final int FontFaceStyle_font_path = 0;
        public static final int FontFaceStyle_font_use_cache = 1;

        static {
            int[] iArr = new int[FontFaceStyle_font_use_cache];
            iArr[FontFaceStyle_font_path] = R.attr.ari_stretch;
            AspectRatioImageView = iArr;
            CircularImageView = new int[]{R.attr.ci_border, R.attr.ci_border_width, R.attr.ci_border_color, R.attr.ci_shadow};
            FixedRatioImageView = new int[]{R.attr.fri_orientation, R.attr.ratio};
            FontFaceStyle = new int[]{R.attr.font_path, R.attr.font_use_cache};
        }
    }
}
