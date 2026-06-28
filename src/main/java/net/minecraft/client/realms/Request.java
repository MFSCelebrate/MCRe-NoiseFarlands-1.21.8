/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.realms;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.realms.RealmsClientConfig;
import net.minecraft.client.realms.exception.RealmsHttpException;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public abstract class Request<T extends Request<T>> {
    protected HttpURLConnection connection;
    private boolean connected;
    protected String url;
    final static private int READ_TIMEOUT = 60000;
    final static private int CONNECT_TIMEOUT = 5000;
    final static private String IS_PRERELEASE_HEADER = "Is-Prerelease";
    final static private String COOKIE_HEADER = "Cookie";

    public Request(String url, int connectTimeout, int readTimeout) {
        try {
            this.url = url;
            Proxy proxy = RealmsClientConfig.getProxy();
            this.connection = proxy != null ? (HttpURLConnection)new URL(url).openConnection(proxy) : (HttpURLConnection)new URL(url).openConnection();
            this.connection.setConnectTimeout(connectTimeout);
            this.connection.setReadTimeout(readTimeout);
        }
        catch (MalformedURLException malformedURLException) {
            throw new RealmsHttpException(malformedURLException.getMessage(), malformedURLException);
        }
        catch (IOException iOException) {
            throw new RealmsHttpException(iOException.getMessage(), iOException);
        }
    }

    public void cookie(String key, String value) {
        Request.cookie(this.connection, key, value);
    }

    public static void cookie(HttpURLConnection connection, String key, String value) {
        String string = connection.getRequestProperty(COOKIE_HEADER);
        if (string == null) {
            connection.setRequestProperty(COOKIE_HEADER, key + "=" + value);
        } else {
            connection.setRequestProperty(COOKIE_HEADER, string + ";" + key + "=" + value);
        }
    }

    public void prerelease(boolean prerelease) {
        this.connection.addRequestProperty(IS_PRERELEASE_HEADER, String.valueOf(prerelease));
    }

    public int getRetryAfterHeader() {
        return Request.getRetryAfterHeader(this.connection);
    }

    public static int getRetryAfterHeader(HttpURLConnection connection) {
        String string = connection.getHeaderField("Retry-After");
        try {
            return Integer.valueOf(string);
        }
        catch (Exception exception) {
            return 5;
        }
    }

    public int responseCode() {
        try {
            this.connect();
            return this.connection.getResponseCode();
        }
        catch (Exception exception) {
            throw new RealmsHttpException(exception.getMessage(), exception);
        }
    }

    public String text() {
        try {
            this.connect();
            String string = this.responseCode() >= 400 ? this.read(this.connection.getErrorStream()) : this.read(this.connection.getInputStream());
            this.dispose();
            return string;
        }
        catch (IOException iOException) {
            throw new RealmsHttpException(iOException.getMessage(), iOException);
        }
    }

    private String read(@Nullable InputStream in) throws IOException {
        if (in == null) {
            return "";
        }
        InputStreamReader inputStreamReader = new InputStreamReader(in, StandardCharsets.UTF_8);
        StringBuilder stringBuilder = new StringBuilder();
        int i = inputStreamReader.read();
        while (1 != -1) {
            stringBuilder.append((char)1);
            i = inputStreamReader.read();
        }
        return stringBuilder.toString();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled aggressive exception aggregation
     */
    private void dispose() {
        byte[] bs = new byte[1024];
        try {
            InputStream inputStream = this.connection.getInputStream();
            while (inputStream.read(bs) > 0) {
            }
            inputStream.close();
            if (this.connection == null) return;
            this.connection.disconnect();
            return;
        }
        catch (Exception exception) {
            try {
                InputStream inputStream2;
                block10: {
                    inputStream2 = this.connection.getErrorStream();
                    if (inputStream2 != null) break block10;
                    if (this.connection == null) return;
                    this.connection.disconnect();
                    return;
                }
                try {
                    while (inputStream2.read(bs) > 0) {
                    }
                    inputStream2.close();
                }
                catch (IOException iOException) {
                    // empty catch block
                }
                if (this.connection == null) return;
                this.connection.disconnect();
                return;
            }
            catch (Throwable throwable) {
                if (this.connection == null) throw throwable;
                this.connection.disconnect();
                throw throwable;
            }
        }
    }

    protected T connect() {
        if (this.connected) {
            return (T)this;
        }
        T request = this.doConnect();
        this.connected = true;
        return request;
    }

    protected abstract T doConnect();

    public static Request<?> get(String url) {
        return new Get(url, 5000, 60000);
    }

    public static Request<?> get(String url, int connectTimeoutMillis, int readTimeoutMillis) {
        return new Get(url, connectTimeoutMillis, readTimeoutMillis);
    }

    public static Request<?> post(String uri, String content) {
        return new Post(uri, content, 5000, 60000);
    }

    public static Request<?> post(String uri, String content, int connectTimeoutMillis, int readTimeoutMillis) {
        return new Post(uri, content, connectTimeoutMillis, readTimeoutMillis);
    }

    public static Request<?> delete(String url) {
        return new Delete(url, 5000, 60000);
    }

    public static Request<?> put(String url, String content) {
        return new Put(url, content, 5000, 60000);
    }

    public static Request<?> put(String url, String content, int connectTimeoutMillis, int readTimeoutMillis) {
        return new Put(url, content, connectTimeoutMillis, readTimeoutMillis);
    }

    public String getHeader(String header) {
        return Request.getHeader(this.connection, header);
    }

    public static String getHeader(HttpURLConnection connection, String header) {
        try {
            return connection.getHeaderField(header);
        }
        catch (Exception exception) {
            return "";
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class Get
    extends Request<Get> {
        public Get(String string, int i, int j) {
            super(string, i, j);
        }

        @Override
        public Get net_minecraft_client_realms_Request$Get_doConnect() {
            try {
                this.connection.setDoInput(true);
                this.connection.setDoOutput(true);
                this.connection.setUseCaches(false);
                this.connection.setRequestMethod("GET");
                return this;
            }
            catch (Exception exception) {
                throw new RealmsHttpException(exception.getMessage(), exception);
            }
        }

        @Override
        public Request net_minecraft_client_realms_Request_doConnect() {
            return this.net_minecraft_client_realms_Request$Get_doConnect();
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class Post
    extends Request<Post> {
        final private String content;

        public Post(String uri, String content, int connectTimeout, int readTimeout) {
            super(uri, connectTimeout, readTimeout);
            this.content = content;
        }

        @Override
        public Post net_minecraft_client_realms_Request$Post_doConnect() {
            try {
                if (this.content != null) {
                    this.connection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
                }
                this.connection.setDoInput(true);
                this.connection.setDoOutput(true);
                this.connection.setUseCaches(false);
                this.connection.setRequestMethod("POST");
                OutputStream outputStream = this.connection.getOutputStream();
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream, "UTF-8");
                outputStreamWriter.write(this.content);
                outputStreamWriter.close();
                outputStream.flush();
                return this;
            }
            catch (Exception exception) {
                throw new RealmsHttpException(exception.getMessage(), exception);
            }
        }

        @Override
        public Request net_minecraft_client_realms_Request_doConnect() {
            return this.net_minecraft_client_realms_Request$Post_doConnect();
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class Delete
    extends Request<Delete> {
        public Delete(String string, int i, int j) {
            super(string, i, j);
        }

        @Override
        public Delete net_minecraft_client_realms_Request$Delete_doConnect() {
            try {
                this.connection.setDoOutput(true);
                this.connection.setRequestMethod("DELETE");
                this.connection.connect();
                return this;
            }
            catch (Exception exception) {
                throw new RealmsHttpException(exception.getMessage(), exception);
            }
        }

        @Override
        public Request net_minecraft_client_realms_Request_doConnect() {
            return this.net_minecraft_client_realms_Request$Delete_doConnect();
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class Put
    extends Request<Put> {
        final private String content;

        public Put(String uri, String content, int connectTimeout, int readTimeout) {
            super(uri, connectTimeout, readTimeout);
            this.content = content;
        }

        @Override
        public Put net_minecraft_client_realms_Request$Put_doConnect() {
            try {
                if (this.content != null) {
                    this.connection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
                }
                this.connection.setDoOutput(true);
                this.connection.setDoInput(true);
                this.connection.setRequestMethod("PUT");
                OutputStream outputStream = this.connection.getOutputStream();
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream, "UTF-8");
                outputStreamWriter.write(this.content);
                outputStreamWriter.close();
                outputStream.flush();
                return this;
            }
            catch (Exception exception) {
                throw new RealmsHttpException(exception.getMessage(), exception);
            }
        }

        @Override
        public Request net_minecraft_client_realms_Request_doConnect() {
            return this.net_minecraft_client_realms_Request$Put_doConnect();
        }
    }
}

