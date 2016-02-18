package com.douban.book.reader.network;

import android.net.Uri;
import android.view.View;
import android.view.View.OnClickListener;
import com.crashlytics.android.Crashlytics;
import com.douban.book.reader.R;
import com.douban.book.reader.app.PageOpenHelper;
import com.douban.book.reader.constant.Char;
import com.douban.book.reader.constant.Key;
import com.douban.book.reader.constant.OAuthError;
import com.douban.book.reader.entity.DummyEntity;
import com.douban.book.reader.fragment.GiftMessageEditFragment_;
import com.douban.book.reader.fragment.LoginFragment_;
import com.douban.book.reader.manager.UserManager;
import com.douban.book.reader.network.exception.ArkBizException;
import com.douban.book.reader.network.exception.BadRequestException;
import com.douban.book.reader.network.exception.RestParseException;
import com.douban.book.reader.network.exception.RestServerException;
import com.douban.book.reader.network.param.MultiPartRequestParam;
import com.douban.book.reader.network.param.RequestParam;
import com.douban.book.reader.network.param.RequestParam.Type;
import com.douban.book.reader.util.DebugSwitch;
import com.douban.book.reader.util.IOUtils;
import com.douban.book.reader.util.JsonUtils;
import com.douban.book.reader.util.Logger;
import com.douban.book.reader.util.NetworkUtils;
import com.douban.book.reader.util.RichText;
import com.douban.book.reader.util.StringUtils;
import com.douban.book.reader.util.Tag;
import com.douban.book.reader.util.ToastBuilder;
import com.douban.book.reader.util.UriUtils;
import com.google.gson.stream.JsonReader;
import com.sina.weibo.sdk.register.mobile.SelectCountryActivity;
import com.tencent.open.SocialConstants;
import io.fabric.sdk.android.services.network.HttpRequest;
import io.realm.internal.Table;
import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.json.JSONException;
import org.json.JSONObject;
import org.mapdb.SerializerBase;

public class Request {
    private Map<String, String> mHeaderMap;
    private JsonReader mJsonReader;
    private final Method mMethod;
    private RequestParam<?> mParam;
    private final String mParamString;
    private final Type mParamType;
    private String mResponseData;
    private int mRetries;
    private final Uri mUri;

    public enum Method {
        GET,
        POST,
        PUT,
        DELETE
    }

    public Request(Method method, Uri uri) {
        this(method, uri, null);
    }

    public Request(Method method, Uri uri, RequestParam<?> param) {
        this.mRetries = 3;
        this.mMethod = method;
        this.mUri = uri;
        this.mParam = param;
        this.mParamString = StringUtils.toStr(param);
        this.mParamType = param != null ? param.getType() : null;
    }

    public Request(Method method, Uri uri, String param, Type paramType) {
        this.mRetries = 3;
        if (paramType == Type.MULTI_PART) {
            IllegalArgumentException e = new IllegalArgumentException("paramType cannot be MULTI_PART while param is String");
            Logger.ec(Tag.NETWORK, e);
            throw e;
        }
        this.mMethod = method;
        this.mUri = uri;
        this.mParamString = param;
        this.mParamType = paramType;
    }

    public void addHeader(String key, String value) {
        if (this.mHeaderMap == null) {
            this.mHeaderMap = new HashMap();
        }
        this.mHeaderMap.put(key, value);
    }

    public void addHeaders(Map<String, String> headers) {
        if (headers != null) {
            if (this.mHeaderMap == null) {
                this.mHeaderMap = new HashMap();
            }
            this.mHeaderMap.putAll(headers);
        }
    }

    public HttpURLConnection openConnection() throws IOException {
        Throwable th;
        long before;
        long after;
        String str;
        Object[] objArr;
        CharSequence formatHeaders;
        Uri uri = this.mUri;
        if (this.mParamType == Type.QUERY_STRING) {
            CharSequence[] charSequenceArr;
            charSequenceArr = new CharSequence[1];
            charSequenceArr[0] = this.mParamString;
            if (StringUtils.isNotEmpty(charSequenceArr)) {
                Uri uri2 = this.mUri;
                uri = r0.buildUpon().encodedQuery(this.mParamString).build();
            }
        }
        if (uri == null) {
            throw new IllegalArgumentException("uri cannot be null");
        }
        String format;
        CharSequence formatHeaders2;
        HttpURLConnection conn = (HttpURLConnection) new URL(uri.toString()).openConnection();
        if (UriUtils.isInDoubanDomain(uri)) {
            ConnectionUtils.addAccessToken(conn);
        }
        if (UriUtils.isArkApiV2Uri(uri)) {
            ConnectionUtils.configURLConnection(conn);
        }
        if (this.mHeaderMap != null) {
            for (Entry<String, String> entry : this.mHeaderMap.entrySet()) {
                conn.addRequestProperty((String) entry.getKey(), (String) entry.getValue());
            }
        }
        conn.setRequestMethod(this.mMethod.toString());
        if (this.mParamType == Type.JSON) {
            conn.addRequestProperty(HttpRequest.HEADER_CONTENT_TYPE, HttpRequest.CONTENT_TYPE_JSON);
        } else {
            if (this.mParamType == Type.MULTI_PART) {
                conn.addRequestProperty(HttpRequest.HEADER_CONTENT_TYPE, String.format("multipart/form-data; boundary=%s", new Object[]{MultiPartRequestParam.BOUNDARY}));
            }
        }
        String str2 = Tag.NETWORK;
        String str3 = "%n>>> %s %s%s%s";
        Object[] objArr2 = new Object[4];
        objArr2[0] = this.mMethod;
        objArr2[1] = uri;
        charSequenceArr = new CharSequence[1];
        charSequenceArr[0] = this.mParamString;
        if (StringUtils.isNotEmpty(charSequenceArr)) {
            Object[] objArr3 = new Object[1];
            objArr3[0] = this.mParamString;
            format = String.format("%n>>> %s", objArr3);
        } else {
            format = Table.STRING_DEFAULT_VALUE;
        }
        objArr2[2] = format;
        if (DebugSwitch.on(Key.APP_DEBUG_PRINT_NETWORK_HEADER)) {
            formatHeaders2 = formatHeaders(conn.getRequestProperties());
        } else {
            formatHeaders2 = Table.STRING_DEFAULT_VALUE;
        }
        objArr2[3] = formatHeaders2;
        Logger.d(str2, str3, objArr2);
        if (this.mMethod == Method.POST || this.mMethod == Method.PUT) {
            charSequenceArr = new CharSequence[1];
            charSequenceArr[0] = this.mParamString;
            if (StringUtils.isNotEmpty(charSequenceArr)) {
                OutputStream outputStream = null;
                BufferedWriter writer = null;
                try {
                    conn.setDoOutput(true);
                    outputStream = conn.getOutputStream();
                    if (this.mParam instanceof MultiPartRequestParam) {
                        byte[] bytes = ((MultiPartRequestParam) this.mParam).getBytes();
                        Object[] objArr4 = new Object[1];
                        objArr4[0] = Integer.valueOf(bytes.length);
                        Logger.d(Tag.NETWORK, ">>> [%s bytes]", objArr4);
                        outputStream.write(bytes);
                    } else {
                        BufferedWriter writer2 = new BufferedWriter(new OutputStreamWriter(outputStream, HttpRequest.CHARSET_UTF8));
                        try {
                            writer2.write(this.mParamString);
                            writer = writer2;
                        } catch (Throwable th2) {
                            th = th2;
                            writer = writer2;
                            IOUtils.closeSilently(writer);
                            IOUtils.closeSilently(outputStream);
                            throw th;
                        }
                    }
                    IOUtils.closeSilently(writer);
                    IOUtils.closeSilently(outputStream);
                } catch (Throwable th3) {
                    th = th3;
                    IOUtils.closeSilently(writer);
                    IOUtils.closeSilently(outputStream);
                    throw th;
                }
            }
        }
        this.mResponseData = null;
        int responseCode = conn.getResponseCode();
        if (responseCode < 200 || responseCode > 299) {
            Object[] objArr5;
            try {
                this.mResponseData = IOUtils.streamToString(conn.getErrorStream());
                IOException exception = new RestServerException(conn, this.mResponseData, this.mParamString);
                if (responseCode == 400) {
                    JSONObject jSONObject = new JSONObject(this.mResponseData);
                    if (StringUtils.equals(jSONObject.optString(SocialConstants.PARAM_TYPE), (CharSequence) "ark_biz_error")) {
                        exception = new ArkBizException(jSONObject.optString(GiftMessageEditFragment_.MESSAGE_ARG), exception);
                    } else {
                        int errorCode = jSONObject.optInt(SelectCountryActivity.EXTRA_COUNTRY_CODE);
                        String errorMessage = jSONObject.optString(SocialConstants.PARAM_SEND_MSG);
                        if (handleBadRequest(errorCode)) {
                            objArr4 = new Object[1];
                            objArr4[0] = Integer.valueOf(this.mRetries);
                            Logger.d(Tag.NETWORK, "%n>>> Retrying (%d) ...", objArr4);
                            conn.disconnect();
                            HttpURLConnection openConnection = openConnection();
                            try {
                                before = StringUtils.toLong(conn.getHeaderField("X-Android-Sent-Millis"));
                                after = StringUtils.toLong(conn.getHeaderField("X-Android-Received-Millis"));
                                str3 = Tag.NETWORK;
                                str = "%n<<< %s %s%n<<< %s %s (%.3fs)%s%s";
                                objArr = new Object[7];
                                objArr[4] = Float.valueOf(((float) (after - before)) / 1000.0f);
                                if (DebugSwitch.on(Key.APP_DEBUG_PRINT_NETWORK_HEADER)) {
                                    formatHeaders = formatHeaders(conn.getHeaderFields());
                                } else {
                                    formatHeaders = Table.STRING_DEFAULT_VALUE;
                                }
                                objArr[5] = formatHeaders;
                                if (DebugSwitch.on(Key.APP_DEBUG_PRINT_NETWORK_RESPONSE)) {
                                    r27 = new CharSequence[1];
                                    r27[0] = this.mResponseData;
                                    if (StringUtils.isNotEmpty(r27)) {
                                        objArr5 = new Object[1];
                                        objArr5[0] = this.mResponseData;
                                        str2 = String.format("%n<<< %s", objArr5);
                                        objArr[6] = str2;
                                        Logger.d(str3, str, objArr);
                                        return openConnection;
                                    }
                                }
                                str2 = Table.STRING_DEFAULT_VALUE;
                                objArr[6] = str2;
                                Logger.d(str3, str, objArr);
                            } catch (Throwable e) {
                                Logger.e(Tag.NETWORK, "%n!!! Error while formatting response data. %s", e);
                            }
                            return openConnection;
                        }
                        exception = new BadRequestException(errorCode, errorMessage, exception);
                    }
                }
                if (shouldLogNetworkInfo(responseCode, exception)) {
                    Logger.dc(Tag.NETWORK, "NetworkInfo: %s", NetworkUtils.getPublicNetworkInfo());
                }
                if (!shouldSkipRestServerExceptionLogging(responseCode, exception)) {
                    Crashlytics.logException(exception);
                }
                conn.disconnect();
                throw exception;
            } catch (Throwable e2) {
                throw new RestParseException(e2);
            } catch (Throwable e22) {
                Logger.e(Tag.NETWORK, "%n!!! Error while formatting response data. %s", e22);
            }
        } else {
            handleResponse(conn);
            try {
                before = StringUtils.toLong(conn.getHeaderField("X-Android-Sent-Millis"));
                after = StringUtils.toLong(conn.getHeaderField("X-Android-Received-Millis"));
                str2 = Tag.NETWORK;
                str3 = "%n<<< %s %s%n<<< %s %s (%.3fs)%s%s";
                objArr2 = new Object[7];
                objArr2[4] = Float.valueOf(((float) (after - before)) / 1000.0f);
                if (DebugSwitch.on(Key.APP_DEBUG_PRINT_NETWORK_HEADER)) {
                    formatHeaders2 = formatHeaders(conn.getHeaderFields());
                } else {
                    formatHeaders2 = Table.STRING_DEFAULT_VALUE;
                }
                objArr2[5] = formatHeaders2;
                if (DebugSwitch.on(Key.APP_DEBUG_PRINT_NETWORK_RESPONSE)) {
                    charSequenceArr = new CharSequence[1];
                    charSequenceArr[0] = this.mResponseData;
                    if (StringUtils.isNotEmpty(charSequenceArr)) {
                        objArr3 = new Object[1];
                        objArr3[0] = this.mResponseData;
                        format = String.format("%n<<< %s", objArr3);
                        objArr2[6] = format;
                        Logger.d(str2, str3, objArr2);
                        return conn;
                    }
                }
                format = Table.STRING_DEFAULT_VALUE;
                objArr2[6] = format;
                Logger.d(str2, str3, objArr2);
                return conn;
            } catch (Throwable e222) {
                Logger.e(Tag.NETWORK, "%n!!! Error while formatting response data. %s", e222);
                return conn;
            }
        }
    }

    private boolean shouldLogNetworkInfo(int responseCode, Exception exception) {
        if (exception instanceof BadRequestException) {
            int errorCode = ((BadRequestException) exception).getErrorCode();
            if (errorCode == Header.ARRAY_SHORT || errorCode == OAuthError.UNKNOWN) {
                return true;
            }
        }
        return false;
    }

    private boolean shouldSkipRestServerExceptionLogging(int responseCode, Exception exception) {
        if (!UserManager.getInstance().hasAccessToken() && responseCode == 404) {
            return true;
        }
        if (this.mMethod == Method.GET && responseCode == 404 && StringUtils.equalsIgnoreCase(UriUtils.getLastPathSegment(this.mUri), NotificationCompatApi21.CATEGORY_PROGRESS) && UriUtils.isArkApiV2Uri(this.mUri)) {
            return true;
        }
        if ((exception instanceof BadRequestException) && ((BadRequestException) exception).getErrorCode() == Header.ARRAY_LONG_BYTE) {
            return true;
        }
        return false;
    }

    public String toString() {
        return String.format("%s %s", new Object[]{this.mMethod, this.mUri});
    }

    public JsonReader getResponseDataReader() throws IOException {
        if (this.mJsonReader == null) {
            this.mJsonReader = new JsonReader(new StringReader(getResponseData()));
        }
        return this.mJsonReader;
    }

    public String getResponseData() {
        return this.mResponseData;
    }

    public <T> T parseResponseAs(Class<T> cls) throws IOException, JSONException {
        if (cls == String.class) {
            return getResponseData();
        }
        if (cls == DummyEntity.class) {
            return null;
        }
        if (cls == JSONObject.class) {
            return JsonUtils.readJSONObject(getResponseDataReader());
        }
        return JsonUtils.fromJson(getResponseDataReader(), (Class) cls);
    }

    private void handleResponse(HttpURLConnection conn) throws IOException {
        String contentType = conn.getHeaderField(HttpRequest.HEADER_CONTENT_TYPE);
        if ((this.mMethod == Method.GET || this.mMethod == Method.POST) && conn.getResponseCode() != 204) {
            InputStream stream = new BufferedInputStream(conn.getInputStream());
            if (contentType == null || !contentType.contains(HttpRequest.CONTENT_TYPE_JSON)) {
                this.mResponseData = IOUtils.streamToString(stream);
                return;
            }
            this.mJsonReader = new JsonReader(new InputStreamReader(stream, HttpRequest.CHARSET_UTF8));
            if (DebugSwitch.on(Key.APP_DEBUG_PRINT_NETWORK_RESPONSE)) {
                try {
                    this.mResponseData = JsonUtils.readJSONBlock(this.mJsonReader);
                    this.mJsonReader = new JsonReader(new StringReader(this.mResponseData));
                } catch (Throwable e) {
                    this.mResponseData = String.format("!!! Error occurred while parsing response: %s", new Object[]{e});
                }
            }
        }
    }

    private boolean handleBadRequest(int errorCode) throws IOException {
        switch (errorCode) {
            case SerializerBase.Header.DOUBLE_0 /*103*/:
                break;
            case Header.DOUBLE_SHORT /*106*/:
            case Header.ARRAY_LONG_INT /*123*/:
                UserManager.getInstance().refreshLogin();
                int i = this.mRetries - 1;
                this.mRetries = i;
                if (i >= 0) {
                    return true;
                }
                break;
            default:
                return false;
        }
        new ToastBuilder().autoClose(false).message((int) R.string.toast_api_error_token_expired).click(new OnClickListener() {
            public void onClick(View v) {
                LoginFragment_.builder().build().showAsActivity(PageOpenHelper.fromApp("AccessTokenExpiredPrompt"));
            }
        }).show();
        return false;
    }

    private CharSequence formatHeaders(Map<String, List<String>> headers) {
        RichText result = new RichText();
        for (CharSequence key : headers.keySet()) {
            for (CharSequence value : (List) headers.get(key)) {
                result.append(Char.CRLF).append((CharSequence) "--- ");
                if (StringUtils.isNotEmpty(key)) {
                    result.append(key).append((CharSequence) ": ");
                }
                result.append(value);
            }
        }
        return result.toString();
    }
}
