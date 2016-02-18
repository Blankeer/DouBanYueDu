package com.douban.book.reader.fragment;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.support.annotation.StringRes;
import com.douban.book.reader.R;
import com.douban.book.reader.app.App;
import com.douban.book.reader.constant.Constants;
import com.douban.book.reader.constant.Key;
import com.douban.book.reader.controller.TaskController;
import com.douban.book.reader.database.DatabaseHelper;
import com.douban.book.reader.database.DatabaseHelper.StorageTarget;
import com.douban.book.reader.event.ApiConfigChangedEvent;
import com.douban.book.reader.event.EventBusUtils;
import com.douban.book.reader.fragment.AlertDialogFragment.Builder;
import com.douban.book.reader.helper.LogoutHelper;
import com.douban.book.reader.manager.SessionManager;
import com.douban.book.reader.manager.UserManager;
import com.douban.book.reader.util.FilePath;
import com.douban.book.reader.util.IOUtils;
import com.douban.book.reader.util.Logger;
import com.douban.book.reader.util.Pref;
import com.douban.book.reader.util.Res;
import com.douban.book.reader.util.StringUtils;
import com.douban.book.reader.util.ToastUtils;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;

@EFragment
public class DebugSwitchFragment extends BasePreferenceFragment {
    @Bean
    SessionManager mSessionManager;
    @Bean
    UserManager mUserManager;

    /* renamed from: com.douban.book.reader.fragment.DebugSwitchFragment.3 */
    class AnonymousClass3 implements OnPreferenceClickListener {
        final /* synthetic */ Preference val$userInfoPref;

        AnonymousClass3(Preference preference) {
            this.val$userInfoPref = preference;
        }

        public boolean onPreferenceClick(Preference preference) {
            new Builder().setMessage((int) R.string.dialog_message_clear_token_confirm).setPositiveButton((int) R.string.dialog_button_ok, new OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    DebugSwitchFragment.this.showBlockingLoadingDialog();
                    new LogoutHelper(DebugSwitchFragment.this).doLogout();
                    DebugSwitchFragment.this.dismissLoadingDialog();
                    DebugSwitchFragment.this.updateUserInfoPref(AnonymousClass3.this.val$userInfoPref);
                }
            }).setNegativeButton((int) R.string.dialog_button_cancel, null).create().show();
            return true;
        }
    }

    /* renamed from: com.douban.book.reader.fragment.DebugSwitchFragment.4 */
    class AnonymousClass4 implements OnPreferenceClickListener {
        final /* synthetic */ Preference val$userInfoPref;

        AnonymousClass4(Preference preference) {
            this.val$userInfoPref = preference;
        }

        public boolean onPreferenceClick(Preference preference) {
            DebugSwitchFragment.this.refreshLogin(this.val$userInfoPref);
            return true;
        }
    }

    /* renamed from: com.douban.book.reader.fragment.DebugSwitchFragment.5 */
    class AnonymousClass5 implements OnPreferenceClickListener {
        final /* synthetic */ Preference val$userInfoPref;

        AnonymousClass5(Preference preference) {
            this.val$userInfoPref = preference;
        }

        public boolean onPreferenceClick(Preference preference) {
            new LogoutHelper(DebugSwitchFragment.this).logout();
            DebugSwitchFragment.this.updateUserInfoPref(this.val$userInfoPref);
            return true;
        }
    }

    /* renamed from: com.douban.book.reader.fragment.DebugSwitchFragment.6 */
    class AnonymousClass6 implements Runnable {
        final /* synthetic */ ListPreference val$apiConfPref;

        AnonymousClass6(ListPreference listPreference) {
            this.val$apiConfPref = listPreference;
        }

        public void run() {
            DebugSwitchFragment.this.fillDataToListPreference(this.val$apiConfPref, DebugSwitchFragment.this.getApiConfList());
        }
    }

    /* renamed from: com.douban.book.reader.fragment.DebugSwitchFragment.9 */
    class AnonymousClass9 implements Runnable {
        final /* synthetic */ List val$dataList;
        final /* synthetic */ ListPreference val$preference;

        AnonymousClass9(List list, ListPreference listPreference) {
            this.val$dataList = list;
            this.val$preference = listPreference;
        }

        public void run() {
            try {
                CharSequence[] dataArray = (CharSequence[]) this.val$dataList.toArray(new CharSequence[0]);
                this.val$preference.setEntries(dataArray);
                this.val$preference.setEntryValues(dataArray);
            } catch (Exception e) {
            }
        }
    }

    public void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        setTitle((int) R.string.title_debug_switch);
        PreferenceScreen preferenceScreen = getPreferenceManager().createPreferenceScreen(App.get());
        setPreferenceScreen(preferenceScreen);
        PreferenceCategory categoryInfo = new PreferenceCategory(App.get());
        categoryInfo.setTitle(R.string.setting_category_info);
        preferenceScreen.addPreference(categoryInfo);
        categoryInfo.addPreference(createDebugSwitchPref(Key.APP_DEBUG_SHOW_BOOK_IDS, R.string.setting_item_show_book_ids));
        categoryInfo.addPreference(createDebugSwitchPref(Key.APP_DEBUG_SHOW_PARAGRAPH_IDS, R.string.setting_item_show_paragraph_ids));
        categoryInfo.addPreference(createDebugSwitchPref(Key.APP_DEBUG_SHOW_NOTE_IDS, R.string.setting_item_show_note_ids));
        categoryInfo.addPreference(createDebugSwitchPref(Key.APP_DEBUG_SHOW_TEST_FIELD, R.string.setting_item_show_test_field));
        categoryInfo.addPreference(createDebugSwitchPref(Key.APP_DEBUG_SHOW_TEST_WORKS, R.string.setting_item_show_test_works));
        categoryInfo.addPreference(createDebugSwitchPref(Key.APP_DEBUG_SHOW_SVG_BORDER, R.string.setting_item_show_svg_border));
        PreferenceCategory categoryLayout = new PreferenceCategory(App.get());
        categoryLayout.setTitle(R.string.setting_category_layout);
        preferenceScreen.addPreference(categoryLayout);
        categoryLayout.addPreference(createDebugSwitchPref(Key.APP_DEBUG_SHOW_PAGE_GRID_LINES, R.string.setting_item_show_page_grid_lines));
        categoryLayout.addPreference(createDebugSwitchPref(Key.APP_DEBUG_SHOW_PAGE_MARGINS, R.string.setting_item_show_page_margins));
        categoryLayout.addPreference(createDebugSwitchPref(Key.APP_DEBUG_SHOW_PARAGRAPH_MARGINS, R.string.setting_item_show_paragraph_margins));
        categoryLayout.addPreference(createDebugSwitchPref(Key.APP_DEBUG_SHOW_ILLUS_MARGINS, R.string.setting_item_show_illus_margins));
        categoryLayout.addPreference(createDebugSwitchPref(Key.APP_DEBUG_SHOW_LINE_STRETCHES, R.string.setting_item_show_line_stretches));
        categoryLayout.addPreference(createDebugSwitchPref(Key.APP_DEBUG_SHOW_LINE_BASELINE, R.string.setting_item_show_line_baseline));
        categoryLayout.addPreference(createDebugSwitchPref(Key.APP_DEBUG_SHOW_SELECTION_RANGE_INFO, R.string.setting_item_show_selection_range_info));
        categoryLayout.addPreference(createDebugSwitchPref(Key.APP_DEBUG_SHOW_MAGNIFIER_FOCUS, R.string.setting_item_show_magnifier_focus));
        categoryLayout.addPreference(createDebugSwitchPref(Key.APP_DEBUG_SHOW_TOUCHABLE, R.string.setting_item_show_touchable));
        categoryLayout.addPreference(createDebugSwitchPref(Key.APP_DEBUG_SHOW_PAGE_FLIP_AREA, R.string.setting_item_show_page_flip_area));
        PreferenceCategory categoryLog = new PreferenceCategory(App.get());
        categoryLog.setTitle(R.string.setting_category_log);
        preferenceScreen.addPreference(categoryLog);
        categoryLog.addPreference(createDebugSwitchPref(Key.APP_DEBUG_PRINT_NETWORK_HEADER, R.string.setting_item_print_network_header));
        categoryLog.addPreference(createDebugSwitchPref(Key.APP_DEBUG_PRINT_NETWORK_RESPONSE, R.string.setting_item_print_network_response));
        PreferenceCategory categoryFeature = new PreferenceCategory(App.get());
        categoryFeature.setTitle(R.string.setting_category_feature);
        preferenceScreen.addPreference(categoryFeature);
        categoryFeature.addPreference(createDebugSwitchPref(Key.APP_DEBUG_ENABLE_QQ_LOGIN_AND_BIND, R.string.setting_item_enable_qq_login_and_bind));
        categoryFeature.addPreference(createDebugSwitchPref(Key.APP_DEBUG_USE_WEB_HERMES, R.string.setting_item_use_web_hermes));
        PreferenceCategory categoryOthers = new PreferenceCategory(App.get());
        categoryOthers.setTitle(R.string.setting_category_others);
        preferenceScreen.addPreference(categoryOthers);
        categoryOthers.addPreference(createDebugSwitchPref(Key.APP_DEBUG_SHOW_WEBVIEW_URL, R.string.setting_item_show_webview_url));
        categoryOthers.addPreference(createDebugSwitchPref(Key.APP_DEBUG_ALLOW_WEBVIEW_DEBUGGING, R.string.setting_item_allow_webview_debugging));
        categoryOthers.addPreference(createDebugSwitchPref(Key.APP_DEBUG_DECIPHER_PACKAGE_ENTRIES, R.string.setting_item_decipher_zip_entries));
        categoryOthers.addPreference(createMoveDbToSdPref());
        categoryOthers.addPreference(createDebugSwitchPref(Key.APP_DEBUG_MONEY_SAVING_MODE, R.string.setting_item_money_saving_mode));
        categoryOthers.addPreference(createDebugSwitchPref(Key.APP_DEBUG_SKIP_PAGING_CACHE, R.string.setting_item_skip_paging_cache));
        PreferenceCategory categoryFile = new PreferenceCategory(App.get());
        categoryFile.setTitle(R.string.setting_category_file);
        preferenceScreen.addPreference(categoryFile);
        categoryFile.addPreference(createFileListPref(FilePath.internalStorageRoot(), R.string.setting_item_internal_storage));
        categoryFile.addPreference(createFileListPref(FilePath.externalStorageRoot(), R.string.setting_item_external_storage));
        PreferenceCategory categoryApi = new PreferenceCategory(App.get());
        categoryApi.setTitle(R.string.setting_category_api);
        preferenceScreen.addPreference(categoryApi);
        categoryApi.addPreference(createApiConfPref());
        PreferenceCategory categoryUser = new PreferenceCategory(App.get());
        categoryUser.setTitle(R.string.setting_category_user);
        preferenceScreen.addPreference(categoryUser);
        Preference userInfoPref = createUserInfoPref();
        categoryUser.addPreference(userInfoPref);
        categoryUser.addPreference(createClearTokenPref(userInfoPref));
        categoryUser.addPreference(createLoginWithRefreshTokenPref(userInfoPref));
        categoryUser.addPreference(createLogoutPref(userInfoPref));
    }

    private Preference createMoveDbToSdPref() {
        Preference dbPref = createDebugSwitchPref(Key.APP_DEBUG_SAVE_DATABASE_TO_SD_CARD, R.string.setting_item_save_database_to_sd_card);
        dbPref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                try {
                    DatabaseHelper.getInstance().moveTo(((Boolean) newValue).booleanValue() ? StorageTarget.FORCE_EXTERNAL : StorageTarget.FORCE_INTERNAL);
                    return true;
                } catch (IOException e) {
                    ToastUtils.showToast((int) R.string.toast_copy_db_file_to_sd_card_failed);
                    Logger.e(DebugSwitchFragment.this.TAG, e);
                    return false;
                }
            }
        });
        return dbPref;
    }

    private Preference createFileListPref(File path, @StringRes int nameResId) {
        Preference preference = new Preference(App.get());
        preference.setTitle(nameResId);
        preference.setSummary(path.getAbsolutePath());
        preference.setOnPreferenceClickListener(new OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                FileListFragment_.builder().path(new File(preference.getSummary().toString())).build().showAsActivity(DebugSwitchFragment.this);
                return true;
            }
        });
        return preference;
    }

    private Preference createUserInfoPref() {
        Preference preference = new Preference(App.get());
        updateUserInfoPref(preference);
        return preference;
    }

    @UiThread
    void updateUserInfoPref(Preference preference) {
        preference.setTitle(Res.getString(R.string.setting_item_user_info, Integer.valueOf(this.mUserManager.getUserId()), this.mUserManager.getUserTypeName()));
        if (this.mSessionManager.getSession() != null) {
            preference.setSummary(String.format("AccessToken: %s%nRefreshToken: %s", new Object[]{this.mSessionManager.getSession().accessToken, this.mSessionManager.getSession().refreshToken}));
            return;
        }
        preference.setSummary(null);
    }

    private Preference createClearTokenPref(Preference userInfoPref) {
        Preference preference = new Preference(App.get());
        preference.setTitle(R.string.setting_item_clear_token);
        preference.setOnPreferenceClickListener(new AnonymousClass3(userInfoPref));
        return preference;
    }

    private Preference createLoginWithRefreshTokenPref(Preference userInfoPref) {
        Preference preference = new Preference(App.get());
        preference.setTitle(R.string.setting_item_login_with_refresh_token);
        preference.setOnPreferenceClickListener(new AnonymousClass4(userInfoPref));
        return preference;
    }

    @Background
    void refreshLogin(Preference userInfoPref) {
        try {
            showBlockingLoadingDialog();
            this.mUserManager.refreshLogin();
            updateUserInfoPref(userInfoPref);
        } catch (Throwable e) {
            ToastUtils.showToast(e);
        } finally {
            dismissLoadingDialog();
        }
    }

    private Preference createLogoutPref(Preference userInfoPref) {
        Preference preference = new Preference(App.get());
        preference.setTitle(R.string.setting_item_logout);
        preference.setOnPreferenceClickListener(new AnonymousClass5(userInfoPref));
        return preference;
    }

    private Preference createApiConfPref() {
        ListPreference apiConfPref = new ListPreference(getActivity());
        apiConfPref.setKey(Key.APP_DEBUG_ACTIVE_API_CONF);
        apiConfPref.setTitle(R.string.setting_item_api_conf);
        apiConfPref.setSummary(Pref.ofApp().getString(Key.APP_DEBUG_ACTIVE_API_CONF, (int) R.string.setting_summary_api_online));
        TaskController.run(new AnonymousClass6(apiConfPref));
        apiConfPref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if (newValue instanceof CharSequence) {
                    try {
                        DebugSwitchFragment.this.applyApiConf((CharSequence) newValue);
                        preference.setSummary((CharSequence) newValue);
                        return true;
                    } catch (IOException e) {
                        Logger.e(DebugSwitchFragment.this.TAG, e);
                    }
                }
                return false;
            }
        });
        return apiConfPref;
    }

    private Preference createDebugSwitchPref(String key, int title) {
        CheckBoxPreference pref = new CheckBoxPreference(App.get());
        pref.setTitle(title);
        pref.setKey(key);
        pref.setDefaultValue(Boolean.FALSE);
        return pref;
    }

    private List<CharSequence> getApiConfList() {
        int i = 0;
        ArrayList<CharSequence> confArray = new ArrayList();
        confArray.add(Res.getString(R.string.setting_summary_api_online));
        File confFolder = App.get().getExternalFilesDir(null);
        Logger.d(this.TAG, " confFolder " + confFolder.getAbsolutePath(), new Object[0]);
        if (confFolder != null && confFolder.exists() && confFolder.isDirectory()) {
            File[] confs = confFolder.listFiles(new FilenameFilter() {
                public boolean accept(File file, String s) {
                    return s.endsWith(".conf");
                }
            });
            int length = confs.length;
            while (i < length) {
                confArray.add(confs[i].getName());
                i++;
            }
        }
        return confArray;
    }

    private void fillDataToListPreference(ListPreference preference, List<CharSequence> dataList) {
        App.get().runOnUiThread(new AnonymousClass9(dataList, preference));
    }

    private void applyApiConf(CharSequence confName) throws IOException {
        Throwable th;
        App app = App.get();
        if (confName.equals(Res.getString(R.string.setting_summary_api_online))) {
            if (!StringUtils.equals(Pref.ofApp().getString(Key.APP_CLIENT_ID), Constants.APP_KEY)) {
                UserManager.getInstance().clearSession();
                UserManager.getInstance().clearUserInfo();
            }
            Pref.ofApp().remove(Key.APP_API_SCHEME);
            Pref.ofApp().remove(Key.APP_OAUTH_BASE_URL);
            Pref.ofApp().remove(Key.APP_API_HOST);
            Pref.ofApp().remove(Key.ARK_HOST);
            Pref.ofApp().remove(Key.APP_CLIENT_ID);
            Pref.ofApp().remove(Key.APP_CLIENT_SECRET);
        } else {
            CharSequence oldClientId = Pref.ofApp().getString(Key.APP_CLIENT_ID);
            File confFolder = app.getExternalFilesDir(null);
            if (confFolder != null && confFolder.exists() && confFolder.isDirectory()) {
                InputStream in = null;
                try {
                    InputStream in2 = new BufferedInputStream(new FileInputStream(new File(confFolder, confName.toString())));
                    try {
                        Properties apiProp = new Properties();
                        apiProp.load(in2);
                        for (Entry<Object, Object> entry : apiProp.entrySet()) {
                            Pref.ofApp().set((String) entry.getKey(), (String) entry.getValue());
                        }
                        IOUtils.closeSilently(in2);
                    } catch (Throwable th2) {
                        th = th2;
                        in = in2;
                        IOUtils.closeSilently(in);
                        throw th;
                    }
                } catch (Throwable th3) {
                    th = th3;
                    IOUtils.closeSilently(in);
                    throw th;
                }
            }
            if (!StringUtils.equals(oldClientId, Pref.ofApp().getString(Key.APP_CLIENT_ID))) {
                UserManager.getInstance().clearSession();
                UserManager.getInstance().clearUserInfo();
            }
        }
        EventBusUtils.post(new ApiConfigChangedEvent());
    }
}
