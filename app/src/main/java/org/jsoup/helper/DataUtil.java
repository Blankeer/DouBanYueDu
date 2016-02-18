package org.jsoup.helper;

import com.sina.weibo.sdk.component.WidgetRequestParam;
import io.fabric.sdk.android.services.network.HttpRequest;
import io.realm.internal.Table;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.util.Locale;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;

public final class DataUtil {
    private static final int UNICODE_BOM = 65279;
    static final int boundaryLength = 32;
    private static final int bufferSize = 131072;
    private static final Pattern charsetPattern;
    static final String defaultCharset = "UTF-8";
    private static final char[] mimeBoundaryChars;

    static {
        charsetPattern = Pattern.compile("(?i)\\bcharset=\\s*(?:\"|')?([^\\s,;\"']*)");
        mimeBoundaryChars = "-_1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
    }

    private DataUtil() {
    }

    public static Document load(File in, String charsetName, String baseUri) throws IOException {
        return parseByteData(readFileToByteBuffer(in), charsetName, baseUri, Parser.htmlParser());
    }

    public static Document load(InputStream in, String charsetName, String baseUri) throws IOException {
        return parseByteData(readToByteBuffer(in), charsetName, baseUri, Parser.htmlParser());
    }

    public static Document load(InputStream in, String charsetName, String baseUri, Parser parser) throws IOException {
        return parseByteData(readToByteBuffer(in), charsetName, baseUri, parser);
    }

    static void crossStreams(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[bufferSize];
        while (true) {
            int len = in.read(buffer);
            if (len != -1) {
                out.write(buffer, 0, len);
            } else {
                return;
            }
        }
    }

    static Document parseByteData(ByteBuffer byteData, String charsetName, String baseUri, Parser parser) {
        String docData;
        Document doc = null;
        if (charsetName == null) {
            docData = Charset.forName(defaultCharset).decode(byteData).toString();
            doc = parser.parseInput(docData, baseUri);
            Element meta = doc.select("meta[http-equiv=content-type], meta[charset]").first();
            if (meta != null) {
                String foundCharset = null;
                if (meta.hasAttr("http-equiv")) {
                    foundCharset = getCharsetFromContentType(meta.attr(WidgetRequestParam.REQ_PARAM_COMMENT_CONTENT));
                }
                if (foundCharset == null && meta.hasAttr(HttpRequest.PARAM_CHARSET)) {
                    try {
                        if (Charset.isSupported(meta.attr(HttpRequest.PARAM_CHARSET))) {
                            foundCharset = meta.attr(HttpRequest.PARAM_CHARSET);
                        }
                    } catch (IllegalCharsetNameException e) {
                        foundCharset = null;
                    }
                }
                if (!(foundCharset == null || foundCharset.length() == 0 || foundCharset.equals(defaultCharset))) {
                    foundCharset = foundCharset.trim().replaceAll("[\"']", Table.STRING_DEFAULT_VALUE);
                    charsetName = foundCharset;
                    byteData.rewind();
                    docData = Charset.forName(foundCharset).decode(byteData).toString();
                    doc = null;
                }
            }
        } else {
            Validate.notEmpty(charsetName, "Must set charset arg to character set of file to parse. Set to null to attempt to detect from HTML");
            docData = Charset.forName(charsetName).decode(byteData).toString();
        }
        if (docData.length() > 0 && docData.charAt(0) == '\ufeff') {
            byteData.rewind();
            docData = Charset.forName(defaultCharset).decode(byteData).toString().substring(1);
            charsetName = defaultCharset;
            doc = null;
        }
        if (doc != null) {
            return doc;
        }
        doc = parser.parseInput(docData, baseUri);
        doc.outputSettings().charset(charsetName);
        return doc;
    }

    static ByteBuffer readToByteBuffer(InputStream inStream, int maxSize) throws IOException {
        boolean z;
        boolean capped;
        if (maxSize >= 0) {
            z = true;
        } else {
            z = false;
        }
        Validate.isTrue(z, "maxSize must be 0 (unlimited) or larger");
        if (maxSize > 0) {
            capped = true;
        } else {
            capped = false;
        }
        byte[] buffer = new byte[bufferSize];
        ByteArrayOutputStream outStream = new ByteArrayOutputStream(bufferSize);
        int remaining = maxSize;
        while (true) {
            int read = inStream.read(buffer);
            if (read == -1) {
                break;
            }
            if (capped) {
                if (read > remaining) {
                    break;
                }
                remaining -= read;
            }
            outStream.write(buffer, 0, read);
        }
        outStream.write(buffer, 0, remaining);
        return ByteBuffer.wrap(outStream.toByteArray());
    }

    static ByteBuffer readToByteBuffer(InputStream inStream) throws IOException {
        return readToByteBuffer(inStream, 0);
    }

    static ByteBuffer readFileToByteBuffer(File file) throws IOException {
        Throwable th;
        RandomAccessFile randomAccessFile = null;
        try {
            RandomAccessFile randomAccessFile2 = new RandomAccessFile(file, "r");
            try {
                byte[] bytes = new byte[((int) randomAccessFile2.length())];
                randomAccessFile2.readFully(bytes);
                ByteBuffer wrap = ByteBuffer.wrap(bytes);
                if (randomAccessFile2 != null) {
                    randomAccessFile2.close();
                }
                return wrap;
            } catch (Throwable th2) {
                th = th2;
                randomAccessFile = randomAccessFile2;
                if (randomAccessFile != null) {
                    randomAccessFile.close();
                }
                throw th;
            }
        } catch (Throwable th3) {
            th = th3;
            if (randomAccessFile != null) {
                randomAccessFile.close();
            }
            throw th;
        }
    }

    static ByteBuffer emptyByteBuffer() {
        return ByteBuffer.allocate(0);
    }

    static String getCharsetFromContentType(String contentType) {
        if (contentType == null) {
            return null;
        }
        Matcher m = charsetPattern.matcher(contentType);
        if (m.find()) {
            String charset = m.group(1).trim().replace("charset=", Table.STRING_DEFAULT_VALUE);
            if (charset.length() == 0) {
                return null;
            }
            try {
                if (Charset.isSupported(charset)) {
                    return charset;
                }
                charset = charset.toUpperCase(Locale.ENGLISH);
                if (Charset.isSupported(charset)) {
                    return charset;
                }
            } catch (IllegalCharsetNameException e) {
                return null;
            }
        }
        return null;
    }

    static String mimeBoundary() {
        StringBuilder mime = new StringBuilder(boundaryLength);
        Random rand = new Random();
        for (int i = 0; i < boundaryLength; i++) {
            mime.append(mimeBoundaryChars[rand.nextInt(mimeBoundaryChars.length)]);
        }
        return mime.toString();
    }
}
