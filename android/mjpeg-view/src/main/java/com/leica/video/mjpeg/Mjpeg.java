package com.leica.video.mjpeg;

import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.Authenticator;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * A library wrapper for handle mjpeg streams.
 *
 * @see <ul>
 *     <li><a href="https://bitbucket.org/neuralassembly/simplemjpegview">simplemjpegview</a></li>
 *     <li><a href="https://code.google.com/archive/p/android-camera-axis">android-camera-axis</a></li>
 * </ul>
 */
public class Mjpeg {
    private static final String TAG = Mjpeg.class.getSimpleName();

    private static java.net.CookieManager msCookieManager = new java.net.CookieManager();
    private final Type type;
    private boolean sendConnectionCloseHeader = false;

    private Mjpeg(Type type) {
        if (type == null) {
            throw new IllegalArgumentException("null type not allowed");
        }
        this.type = type;
    }

    /**
     * Uses {@link Type#DEFAULT} implementation.
     *
     * @return Mjpeg instance
     */
    public static Mjpeg newInstance() {
        return new Mjpeg(Type.DEFAULT);
    }

    /**
     *
     * @return Mjpeg instance
     */
    public static Mjpeg newInstance(Type type) {
        return new Mjpeg(type);
    }

    /**
     * Configure authentication.
     *
     * @param username credential
     * @param password credential
     * @return Mjpeg instance
     */
    public Mjpeg credential(String username, String password) {
        if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password)) {
            Authenticator.setDefault(new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, password.toCharArray());
                }
            });
        }
        return this;
    }

    /**
     * Configure cookies.
     *
     * @param cookie cookie string
     * @return Mjpeg instance
     */
    public Mjpeg addCookie(String cookie) {
        if (!TextUtils.isEmpty(cookie)) {
            msCookieManager.getCookieStore().add(null, HttpCookie.parse(cookie).get(0));
        }
        return this;
    }

    /**
     * Send a "Connection: close" header to fix
     * <code>java.net.ProtocolException: Unexpected status line</code>
     *
     * @return Observable Mjpeg stream
     */
    public Mjpeg sendConnectionCloseHeader() {
        sendConnectionCloseHeader = true;
        return this;
    }
    HttpURLConnection urlConnection;
    public InputStream inputStream;
    @NonNull
    private Observable<MjpegInputStream> connect(String url) {
        return Observable.defer(() -> {
            try {
                urlConnection = (HttpURLConnection) new URL(url).openConnection();
                loadConnectionProperties(urlConnection);
                inputStream = urlConnection.getInputStream();
                switch (type) {
                    // handle multiple implementations
                    case DEFAULT:
                        return Observable.just(new MjpegInputStreamDefault(inputStream));
                    case NATIVE:
                        return Observable.just(new MjpegInputStreamNative(inputStream));
                }
                throw new IllegalStateException("invalid type");
            } catch (IOException e) {
                Log.e(TAG, "error during connection", e);
                return Observable.error(e);
            }
        });
    }

    /**
     * Connect to a Mjpeg stream.
     *
     * @param url source
     * @return Observable Mjpeg stream
     */
    public Observable<MjpegInputStream> open(String url) {
        return connect(url)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * Connect to a Mjpeg stream.
     *
     * @param url     source
     * @param timeout in seconds
     * @return Observable Mjpeg stream
     */
    public Observable<MjpegInputStream> open(String url, int timeout) {
        return connect(url)
                .timeout(timeout, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * Configure request properties
     *
     * @param urlConnection the url connection to add properties and cookies to
     */
    private void loadConnectionProperties(HttpURLConnection urlConnection) {
//        urlConnection.setRequestProperty("Cache-Control", "no-cache");
        if (sendConnectionCloseHeader) {
            urlConnection.setRequestProperty("Connection", "close");
        }

        if (!msCookieManager.getCookieStore().getCookies().isEmpty()) {
            urlConnection.setRequestProperty("Cookie",
                    TextUtils.join(";", msCookieManager.getCookieStore().getCookies()));
        }
    }
    public void closeStream(){
        if(urlConnection!=null) urlConnection.disconnect();
        if(inputStream!=null) {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            inputStream=null;
        }
    }
    
    /**
     * Library implementation type
     */
    public enum Type {
        DEFAULT, NATIVE
    }
}
