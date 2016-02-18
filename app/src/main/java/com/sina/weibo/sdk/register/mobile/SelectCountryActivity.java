package com.sina.weibo.sdk.register.mobile;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import com.sina.weibo.sdk.component.view.TitleBar;
import com.sina.weibo.sdk.component.view.TitleBar.ListenerOnTitleBtnClicked;
import com.sina.weibo.sdk.register.mobile.LetterIndexBar.OnIndexChangeListener;
import com.sina.weibo.sdk.utils.ResourceManager;
import io.realm.internal.Table;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SelectCountryActivity extends Activity implements OnIndexChangeListener {
    private static final String CHINA_CN = "\u4e2d\u56fd";
    private static final String CHINA_EN = "China";
    private static final String CHINA_TW = "\u4e2d\u570b";
    public static final String EXTRA_COUNTRY_CODE = "code";
    public static final String EXTRA_COUNTRY_NAME = "name";
    private static final String INFO_CN = "\u5e38\u7528";
    private static final String INFO_EN = "Common";
    private static final String INFO_TW = "\u5e38\u7528";
    private static final String SELECT_COUNTRY_EN = "Region";
    private static final String SELECT_COUNTRY_ZH_CN = "\u9009\u62e9\u56fd\u5bb6";
    private static final String SELECT_COUNTRY_ZH_TW = "\u9078\u64c7\u570b\u5bb6";
    private List<Country>[] arrSubCountry;
    String countryStr;
    private List<IndexCountry> indexCountries;
    private CountryAdapter mAdapter;
    private List<Country> mCountries;
    private FrameLayout mFrameLayout;
    private LetterIndexBar mLetterIndexBar;
    private ListView mListView;
    private RelativeLayout mMainLayout;
    private CountryList result;

    private class CountryAdapter extends BaseAdapter {
        private CountryAdapter() {
        }

        public int getCount() {
            if (SelectCountryActivity.this.indexCountries != null) {
                return SelectCountryActivity.this.indexCountries.size();
            }
            return 0;
        }

        public Object getItem(int position) {
            if (SelectCountryActivity.this.indexCountries == null || SelectCountryActivity.this.indexCountries.isEmpty() || position == SelectCountryActivity.this.indexCountries.size()) {
                return null;
            }
            IndexCountry indexCountry = (IndexCountry) SelectCountryActivity.this.indexCountries.get(position);
            if (indexCountry.indexInList != -1) {
                return SelectCountryActivity.this.arrSubCountry[indexCountry.indexInListArray].get(indexCountry.indexInList);
            }
            return null;
        }

        public long getItemId(int position) {
            return 0;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            IndexCountry indexCountry = (IndexCountry) SelectCountryActivity.this.indexCountries.get(position);
            Country coutry;
            if (convertView != null) {
                if (indexCountry.indexInList != -1) {
                    coutry = (Country) SelectCountryActivity.this.arrSubCountry[indexCountry.indexInListArray].get(indexCountry.indexInList);
                    if (convertView instanceof SelectCountryTitleView) {
                        convertView = new SelectCountryItemView(SelectCountryActivity.this, coutry.getName(), coutry.getCode());
                    } else {
                        ((SelectCountryItemView) convertView).updateContent(coutry.getName(), coutry.getCode());
                    }
                } else if (!(convertView instanceof SelectCountryTitleView)) {
                    convertView = createTitleView(indexCountry.indexInListArray);
                } else if (indexCountry.indexInListArray == 0) {
                    ((SelectCountryTitleView) convertView).update(ResourceManager.getString(SelectCountryActivity.this, SelectCountryActivity.INFO_EN, SelectCountryActivity.INFO_TW, SelectCountryActivity.INFO_TW));
                } else {
                    convertView = createTitleView(indexCountry.indexInListArray);
                }
                return convertView;
            } else if (indexCountry.indexInList == -1) {
                return createTitleView(indexCountry.indexInListArray);
            } else {
                coutry = (Country) SelectCountryActivity.this.arrSubCountry[indexCountry.indexInListArray].get(indexCountry.indexInList);
                return new SelectCountryItemView(SelectCountryActivity.this, coutry.getName(), coutry.getCode());
            }
        }

        private SelectCountryTitleView createTitleView(int indexInListArray) {
            SelectCountryTitleView titleView = new SelectCountryTitleView(SelectCountryActivity.this.getApplicationContext());
            if (indexInListArray == 0) {
                titleView.setTitle(ResourceManager.getString(SelectCountryActivity.this, SelectCountryActivity.INFO_EN, SelectCountryActivity.INFO_TW, SelectCountryActivity.INFO_TW));
            } else {
                titleView.setTitle(String.valueOf((char) ((indexInListArray + 65) - 1)));
            }
            return titleView;
        }
    }

    private class IndexCountry {
        int indexInList;
        int indexInListArray;

        IndexCountry(int indexInListArray, int indexInList) {
            this.indexInListArray = indexInListArray;
            this.indexInList = indexInList;
        }

        public boolean equals(Object o) {
            if (!(o instanceof IndexCountry) || this.indexInList != -1) {
                return false;
            }
            IndexCountry another = (IndexCountry) o;
            if (this.indexInListArray == another.indexInListArray && this.indexInList == another.indexInList) {
                return true;
            }
            return false;
        }
    }

    public SelectCountryActivity() {
        this.countryStr = Table.STRING_DEFAULT_VALUE;
        this.indexCountries = new ArrayList();
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    private void initView() {
        this.mMainLayout = new RelativeLayout(this);
        this.mMainLayout.setLayoutParams(new LayoutParams(-1, -1));
        TitleBar titleBar = new TitleBar(this);
        titleBar.setId(1);
        titleBar.setLeftBtnBg(ResourceManager.createStateListDrawable(this, "weibosdk_navigationbar_back.png", "weibosdk_navigationbar_back_highlighted.png"));
        titleBar.setTitleBarText(ResourceManager.getString(this, SELECT_COUNTRY_EN, SELECT_COUNTRY_ZH_CN, SELECT_COUNTRY_ZH_TW));
        titleBar.setTitleBarClickListener(new ListenerOnTitleBtnClicked() {
            public void onLeftBtnClicked() {
                SelectCountryActivity.this.setResult(0);
                SelectCountryActivity.this.finish();
            }
        });
        this.mMainLayout.addView(titleBar);
        this.mFrameLayout = new FrameLayout(this);
        LayoutParams mFrameLp = new LayoutParams(-1, -1);
        mFrameLp.addRule(3, titleBar.getId());
        this.mFrameLayout.setLayoutParams(mFrameLp);
        this.mMainLayout.addView(this.mFrameLayout);
        this.mListView = new ListView(this);
        this.mListView.setLayoutParams(new AbsListView.LayoutParams(-1, -1));
        this.mListView.setFadingEdgeLength(0);
        this.mListView.setSelector(new ColorDrawable(0));
        this.mListView.setDividerHeight(ResourceManager.dp2px(this, 1));
        this.mListView.setCacheColorHint(0);
        this.mListView.setDrawSelectorOnTop(false);
        this.mListView.setScrollingCacheEnabled(false);
        this.mListView.setScrollbarFadingEnabled(false);
        this.mListView.setVerticalScrollBarEnabled(false);
        this.mListView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Country country = (Country) SelectCountryActivity.this.mAdapter.getItem(position);
                if (country != null) {
                    Intent intent = new Intent();
                    intent.putExtra(SelectCountryActivity.EXTRA_COUNTRY_CODE, country.getCode());
                    intent.putExtra(SelectCountryActivity.EXTRA_COUNTRY_NAME, country.getName());
                    SelectCountryActivity.this.setResult(-1, intent);
                    SelectCountryActivity.this.finish();
                }
            }
        });
        this.mFrameLayout.addView(this.mListView);
        this.mAdapter = new CountryAdapter();
        this.mListView.setAdapter(this.mAdapter);
        this.mLetterIndexBar = new LetterIndexBar(this);
        this.mLetterIndexBar.setIndexChangeListener(this);
        FrameLayout.LayoutParams mLetterIndexBarLp = new FrameLayout.LayoutParams(-2, -1);
        mLetterIndexBarLp.gravity = 5;
        this.mLetterIndexBar.setLayoutParams(mLetterIndexBarLp);
        this.mFrameLayout.addView(this.mLetterIndexBar);
        PinyinUtils.getInstance(this);
        Locale locale = ResourceManager.getLanguage();
        if (Locale.SIMPLIFIED_CHINESE.equals(locale)) {
            this.countryStr = ResourceManager.readCountryFromAsset(this, "countryCode.txt");
        } else if (Locale.TRADITIONAL_CHINESE.equals(locale)) {
            this.countryStr = ResourceManager.readCountryFromAsset(this, "countryCodeTw.txt");
        } else {
            this.countryStr = ResourceManager.readCountryFromAsset(this, "countryCodeEn.txt");
        }
        this.result = new CountryList(this.countryStr);
        this.mCountries = this.result.countries;
        this.arrSubCountry = subCountries(this.mCountries);
        this.indexCountries = compose(this.arrSubCountry);
        this.mAdapter.notifyDataSetChanged();
        setContentView(this.mMainLayout);
    }

    protected void onPause() {
        super.onPause();
    }

    public void onIndexChange(int index) {
        if (this.arrSubCountry != null && index < this.arrSubCountry.length && this.arrSubCountry[index] != null) {
            this.mListView.setSelection(this.indexCountries.indexOf(new IndexCountry(index, -1)));
        }
    }

    private List<Country>[] subCountries(List<Country> countries) {
        List[] arr = new ArrayList[27];
        Country commonCountry = new Country();
        commonCountry.setCode(Country.CHINA_CODE);
        commonCountry.setName(ResourceManager.getString(this, CHINA_EN, CHINA_CN, CHINA_TW));
        arr[0] = new ArrayList();
        arr[0].add(commonCountry);
        for (int i = 0; i < countries.size(); i++) {
            Country country = (Country) countries.get(i);
            if (country.getCode().equals("00852") || country.getCode().equals("00853") || country.getCode().equals("00886")) {
                arr[0].add(country);
            } else {
                int index = (country.getPinyin().charAt(0) - 97) + 1;
                if (arr[index] == null) {
                    arr[index] = new ArrayList();
                }
                arr[index].add(country);
            }
        }
        return arr;
    }

    private List<IndexCountry> compose(List<Country>[] arrSubCountry) {
        List<IndexCountry> indexFollows = new ArrayList();
        if (arrSubCountry != null) {
            for (int i = 0; i < arrSubCountry.length; i++) {
                List<Country> list = arrSubCountry[i];
                if (list != null && list.size() > 0) {
                    for (int j = 0; j < list.size(); j++) {
                        if (j == 0) {
                            indexFollows.add(new IndexCountry(i, -1));
                        }
                        indexFollows.add(new IndexCountry(i, j));
                    }
                }
            }
        }
        return indexFollows;
    }

    protected void onDestroy() {
        super.onDestroy();
    }
}
