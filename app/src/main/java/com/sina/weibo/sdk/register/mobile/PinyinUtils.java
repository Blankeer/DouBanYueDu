package com.sina.weibo.sdk.register.mobile;

import android.content.Context;
import android.text.TextUtils;
import com.douban.amonsul.StatConstant;
import com.douban.book.reader.constant.DeviceType;
import com.igexin.sdk.PushBuildConfig;
import io.realm.internal.Table;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

public class PinyinUtils {
    private static final int DISTINGUISH_LEN = 10;
    private static final char FIRST_CHINA = '\u4e00';
    private static final char LAST_CHINA = '\u9fa5';
    private static final String[] PINYIN;
    private static final char SPECIAL_HANZI = '\u3007';
    private static final String SPECIAL_HANZI_PINYIN = "LING";
    private static volatile boolean isLoad;
    private static PinyinUtils sInstance;
    private static short[] sPinyinIndex;

    public static class MatchedResult {
        public int end;
        public int start;

        public MatchedResult() {
            this.start = -1;
            this.end = -1;
        }
    }

    static {
        PINYIN = new String[]{DeviceType.IPAD, StatConstant.KEY_APP, "an", "ang", "ao", "ba", "bai", "ban", "bang", "bao", "bei", "ben", "beng", "bi", "bian", "biao", "bie", "bin", "bing", "bo", "bu", "ca", "cai", "can", "cang", "cao", "ce", "cen", "ceng", "cha", "chai", "chan", "chang", "chao", "che", "chen", "cheng", "chi", "chong", "chou", "chu", "chuai", "chuan", "chuang", "chui", "chun", "chuo", "ci", "cong", "cou", "cu", "cuan", "cui", "cun", "cuo", "da", "dai", "dan", "dang", "dao", "de", "deng", StatConstant.KEY_DEVICE, "dia", "dian", "diao", "die", "ding", "diu", "dong", "dou", "du", "duan", "dui", "dun", "duo", "e", "ei", "en", "er", "fa", "fan", "fang", "fei", "fen", "feng", "fo", "fou", "fu", "ga", "gai", "gan", "gang", "gao", "ge", "gei", "gen", "geng", "gong", "gou", "gu", "gua", "guai", "guan", "guang", "gui", "gun", "guo", "ha", "hai", "han", "hang", "hao", "he", "hei", "hen", "heng", "hong", "hou", "hu", "hua", "huai", "huan", "huang", "hui", "hun", "huo", "ji", "jia", "jian", "jiang", "jiao", "jie", "jin", "jing", "jiong", "jiu", "ju", "juan", "jue", "jun", "ka", "kai", "kan", "kang", "kao", "ke", "ken", "keng", "kong", "kou", "ku", "kua", "kuai", "kuan", "kuang", "kui", "kun", "kuo", "la", "lai", "lan", "lang", "lao", "le", "lei", "leng", "li", "lia", "lian", "liang", "liao", "lie", "lin", "ling", "liu", "long", "lou", StatConstant.JSON_KEY_LANGUAGE, "luan", "lun", "luo", "lv", "lve", "m", "ma", "mai", "man", "mang", "mao", "me", "mei", "men", "meng", "mi", "mian", "miao", "mie", "min", "ming", "miu", "mo", "mou", "mu", "na", "nai", "nan", "nang", "nao", "ne", "nei", "nen", "neng", "ng", "ni", "nian", "niang", "niao", "nie", "nin", "ning", "niu", PushBuildConfig.sdk_conf_debug_level, "nong", "nou", "nu", "nuan", "nuo", "nv", "nve", "o", "ou", "pa", "pai", "pan", "pang", "pao", "pei", "pen", "peng", "pi", "pian", "piao", "pie", "pin", "ping", "po", "pou", "pu", "qi", "qia", "qian", "qiang", "qiao", "qie", "qin", "qing", "qiong", "qiu", "qu", "quan", "que", "qun", "ran", "rang", "rao", "re", "ren", "reng", "ri", "rong", "rou", "ru", "ruan", "rui", "run", "ruo", "sa", "sai", "san", "sang", "sao", "se", "sen", "seng", "sha", "shai", "shan", "shang", "shao", "she", "shei", "shen", "sheng", "shi", "shou", "shu", "shua", "shuai", "shuan", "shuang", "shui", "shun", "shuo", "si", "song", "sou", "su", "suan", "sui", "sun", "suo", "ta", "tai", "tan", "tang", "tao", "te", "teng", "ti", "tian", "tiao", "tie", "ting", "tong", "tou", "tu", "tuan", "tui", "tun", "tuo", "wa", "wai", "wan", "wang", "wei", "wen", "weng", "wo", "wu", "xi", "xia", "xian", "xiang", "xiao", "xie", "xin", "xing", "xiong", "xiu", "xu", "xuan", "xue", "xun", "ya", "yan", "yang", "yao", "ye", "yi", "yiao", "yin", "ying", "yo", "yong", "you", "yu", "yuan", "yue", "yun", "za", "zai", "zan", "zang", "zao", "ze", "zei", "zen", "zeng", "zha", "zhai", "zhan", "zhang", "zhao", "zhe", "zhei", "zhen", "zheng", "zhi", "zhong", "zhou", "zhu", "zhua", "zhuai", "zhuan", "zhuang", "zhui", "zhun", "zhuo", "zi", "zong", "zou", "zu", "zuan", "zui", "zun", "zuo"};
        isLoad = false;
    }

    private PinyinUtils() {
    }

    public static synchronized PinyinUtils getInstance(Context ctx) {
        PinyinUtils pinyinUtils;
        synchronized (PinyinUtils.class) {
            if (sInstance == null) {
                sInstance = new PinyinUtils();
            }
            loadData(ctx);
            pinyinUtils = sInstance;
        }
        return pinyinUtils;
    }

    private static void loadData(Context ctx) {
        Throwable th;
        InputStream input = null;
        DataInputStream dataInput = null;
        try {
            if (isLoad) {
                if (dataInput != null) {
                    try {
                        dataInput.close();
                    } catch (IOException e) {
                        return;
                    }
                }
                if (input != null) {
                    input.close();
                    return;
                }
                return;
            }
            input = ctx.getAssets().open("pinyinindex");
            DataInputStream dataInput2 = new DataInputStream(input);
            try {
                sPinyinIndex = new short[((int) ((long) (dataInput2.available() >> 1)))];
                for (int i = 0; i < sPinyinIndex.length; i++) {
                    sPinyinIndex[i] = dataInput2.readShort();
                }
                isLoad = true;
                if (dataInput2 != null) {
                    try {
                        dataInput2.close();
                    } catch (IOException e2) {
                        dataInput = dataInput2;
                        return;
                    }
                }
                if (input != null) {
                    input.close();
                    dataInput = dataInput2;
                    return;
                }
            } catch (IOException e3) {
                dataInput = dataInput2;
            } catch (Exception e4) {
                dataInput = dataInput2;
            } catch (Throwable th2) {
                th = th2;
                dataInput = dataInput2;
            }
        } catch (IOException e5) {
            try {
                isLoad = false;
                if (dataInput != null) {
                    try {
                        dataInput.close();
                    } catch (IOException e6) {
                        return;
                    }
                }
                if (input != null) {
                    input.close();
                }
            } catch (Throwable th3) {
                th = th3;
                if (dataInput != null) {
                    try {
                        dataInput.close();
                    } catch (IOException e7) {
                        throw th;
                    }
                }
                if (input != null) {
                    input.close();
                }
                throw th;
            }
        } catch (Exception e8) {
            isLoad = false;
            if (dataInput != null) {
                try {
                    dataInput.close();
                } catch (IOException e9) {
                    return;
                }
            }
            if (input != null) {
                input.close();
            }
        }
    }

    private String getPinyin(char ch) {
        if (!isLoad) {
            return Table.STRING_DEFAULT_VALUE;
        }
        String pinyin = Table.STRING_DEFAULT_VALUE;
        if (ch == SPECIAL_HANZI) {
            return SPECIAL_HANZI_PINYIN;
        }
        if (ch < FIRST_CHINA || ch > LAST_CHINA) {
            return String.valueOf(ch);
        }
        pinyin = PINYIN[sPinyinIndex[ch - 19968]];
        if (pinyin == null) {
            return Table.STRING_DEFAULT_VALUE;
        }
        return pinyin;
    }

    public String getPinyin(String s) {
        if (TextUtils.isEmpty(s)) {
            return Table.STRING_DEFAULT_VALUE;
        }
        if (!isLoad) {
            return Table.STRING_DEFAULT_VALUE;
        }
        StringBuilder sb = new StringBuilder();
        int len = s.length();
        for (int i = 0; i < len; i++) {
            sb.append(getPinyin(s.charAt(i)));
        }
        return sb.toString();
    }

    public MatchedResult getMatchedResult(String src, String input) {
        MatchedResult result = new MatchedResult();
        result.start = -1;
        result.end = -1;
        if (!(!isLoad || TextUtils.isEmpty(src) || TextUtils.isEmpty(input))) {
            int i;
            src = src.toUpperCase();
            input = input.toUpperCase();
            if (Math.min(src.length(), input.length()) > DISTINGUISH_LEN) {
                src = src.substring(0, DISTINGUISH_LEN);
                input = input.substring(0, DISTINGUISH_LEN);
            }
            int index = src.indexOf(input);
            if (index >= 0) {
                result.start = index;
                result.end = (input.length() + index) - 1;
            }
            char[] search = new char[input.length()];
            for (i = 0; i < input.length(); i++) {
                search[i] = input.charAt(i);
            }
            char[] org = new char[src.length()];
            String[] fullPinyin = new String[src.length()];
            int srcLen = src.length();
            for (i = 0; i < srcLen; i++) {
                char ch = src.charAt(i);
                org[i] = ch;
                String pinyinCache = getPinyin(ch);
                if (TextUtils.isEmpty(pinyinCache)) {
                    fullPinyin[i] = new StringBuilder(String.valueOf(ch)).toString();
                } else {
                    fullPinyin[i] = pinyinCache.toUpperCase();
                }
            }
            char firstSearch = search[0];
            for (i = 0; i < fullPinyin.length; i++) {
                char ch1 = fullPinyin[i].charAt(0);
                char ch2 = org[i];
                if (ch1 == firstSearch || ch2 == firstSearch) {
                    int pos = distinguish(search, 0, subCharRangeArray(org, i, org.length - 1), subStringRangeArray(fullPinyin, i, fullPinyin.length - 1), 0, 0);
                    if (pos != -1) {
                        result.start = i;
                        result.end = i + pos;
                        break;
                    }
                }
            }
        }
        return result;
    }

    public int distinguish(char[] search, int searchIndex, char[] src, String[] pinyin, int wordIndex, int wordStart) {
        if (searchIndex == 0 && (search[0] == src[0] || search[0] == pinyin[0].charAt(0))) {
            if (search.length != 1) {
                return distinguish(search, 1, src, pinyin, 0, 1);
            }
            return 0;
        } else if (pinyin[wordIndex].length() <= wordStart || searchIndex >= search.length || !(search[searchIndex] == src[wordIndex] || search[searchIndex] == pinyin[wordIndex].charAt(wordStart))) {
            if (pinyin.length <= wordIndex + 1 || searchIndex >= search.length || !(search[searchIndex] == src[wordIndex + 1] || search[searchIndex] == pinyin[wordIndex + 1].charAt(0))) {
                if (pinyin.length > wordIndex + 1) {
                    for (int i = 1; i < searchIndex; i++) {
                        if (distinguish(search, searchIndex - i, src, pinyin, wordIndex + 1, 0) != -1) {
                            return wordIndex + 1;
                        }
                    }
                }
                return -1;
            } else if (searchIndex != search.length - 1) {
                return distinguish(search, searchIndex + 1, src, pinyin, wordIndex + 1, 1);
            } else if (distinguish(search, src, pinyin, wordIndex)) {
                return wordIndex + 1;
            } else {
                return -1;
            }
        } else if (searchIndex != search.length - 1) {
            return distinguish(search, searchIndex + 1, src, pinyin, wordIndex, wordStart + 1);
        } else if (distinguish(search, src, pinyin, wordIndex)) {
            return wordIndex;
        } else {
            return -1;
        }
    }

    private boolean distinguish(char[] search, char[] src, String[] pinyin, int wordIndex) {
        String searchString = new String(search);
        int lastIndex = 0;
        for (int i = 0; i < wordIndex; i++) {
            lastIndex = searchString.indexOf(pinyin[i].charAt(0), lastIndex);
            if (lastIndex == -1) {
                lastIndex = searchString.indexOf(src[i], lastIndex);
            }
            if (lastIndex == -1) {
                return false;
            }
            lastIndex++;
        }
        return true;
    }

    private char[] subCharRangeArray(char[] org, int start, int end) {
        char[] ret = new char[((end - start) + 1)];
        int i = start;
        int j = 0;
        while (i <= end) {
            ret[j] = org[i];
            i++;
            j++;
        }
        return ret;
    }

    private String[] subStringRangeArray(String[] org, int start, int end) {
        String[] ret = new String[((end - start) + 1)];
        int i = start;
        int j = 0;
        while (i <= end) {
            ret[j] = org[i];
            i++;
            j++;
        }
        return ret;
    }

    public static PinyinUtils getObject() {
        return sInstance;
    }
}
