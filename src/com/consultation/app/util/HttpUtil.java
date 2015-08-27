package com.consultation.app.util;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyStore;
import java.util.Iterator;
import java.util.Map;

import org.apache.http.HttpHost;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnRouteParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

import com.consultation.app.ConsultionStatusCode;
import com.consultation.app.exception.ConsultationCallbackException;
import com.consultation.app.listener.ConsultationCallbackHandler;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

@SuppressLint("HandlerLeak")
public class HttpUtil {
    
    private ConsultationCallbackHandler consultationCallbackHandler;

    private static final String CHAR_SET="utf-8";

    private static HttpUtil httpUtil;

    public static HttpUtil getInstance(Context context) {
        if(null == httpUtil) {
            httpUtil=new HttpUtil();
        }
        return httpUtil;
    }
    
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch(msg.what) {
                case 0:
                    consultationCallbackHandler.onSuccess((String)msg.obj, ConsultionStatusCode.SUCCESS);
                    break;
                case 1:
                    consultationCallbackHandler.onFailure(new ConsultationCallbackException(ConsultionStatusCode.FAIL, "网络请求异常"));
                    break;
                default:
                    break;
            }
        }
    };

    public void uploadFiles(final String httpUrl, ConsultationCallbackHandler callbackHandler, final File[] files,
        final Map<String, String> params) {
        consultationCallbackHandler = callbackHandler;
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    long boundary=System.currentTimeMillis();
                    String prefix="--", line_end="\r\n";
                    int timeout=60000 * 3;// 3分钟
                    try {
                        URL url=new URL(httpUrl);
                        HttpURLConnection conn=(HttpURLConnection)url.openConnection();
                        conn.setReadTimeout(timeout);
                        conn.setConnectTimeout(timeout);
                        conn.setDoInput(true);
                        conn.setDoOutput(true);
                        conn.setUseCaches(false);
                        conn.setRequestMethod("POST");
                        conn.setRequestProperty("Charset", CHAR_SET);
                        conn.setRequestProperty("connection", "keep-alive");
                        conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                        OutputStream outputSteam=conn.getOutputStream();
                        DataOutputStream dos=new DataOutputStream(outputSteam);
                        StringBuffer paramsStringBuffer=null;
                        String paramsString="";

                        /***
                         * 以下是用于上传参数
                         */
                        if(params != null && params.size() > 0) {
                            Iterator<String> it=params.keySet().iterator();
                            while(it.hasNext()) {
                                paramsStringBuffer=null;
                                paramsStringBuffer=new StringBuffer();
                                String key=it.next();
                                String value=params.get(key);
                                paramsStringBuffer.append(prefix).append(boundary).append(line_end);
                                paramsStringBuffer.append("Content-Disposition: form-data; name=\"").append(key).append("\"")
                                    .append(line_end).append(line_end);
                                paramsStringBuffer.append(value).append(line_end);
                                paramsString=paramsStringBuffer.toString();
                                dos.write(paramsString.getBytes());
                                dos.flush();
                            }
                        }
                        for(int i=0; i < files.length; i++) {
                            File file=files[i];
                            if(file != null) {
                                StringBuffer sb=new StringBuffer();
                                sb.append(prefix);
                                sb.append(boundary);
                                sb.append(line_end);
                                sb.append("Content-Disposition: form-data; name=\"img\"; filename=\"" + file.getName() + "\""
                                    + line_end);
                                sb.append("Content-Type: application/octet-stream; charset=" + CHAR_SET + line_end);
                                sb.append(line_end);
                                dos.write(sb.toString().getBytes());
                                InputStream is=new FileInputStream(file);
                                byte[] bytes=new byte[1024];
                                int len=0;
                                while((len=is.read(bytes)) != -1) {
                                    dos.write(bytes, 0, len);
                                }
                                is.close();
                                dos.write(line_end.getBytes());
                                String splitStr=prefix + boundary;
                                if(i == files.length - 1) {
                                    splitStr+=prefix;
                                }
                                splitStr+=line_end;
                                byte[] end_data=splitStr.getBytes();
                                dos.write(end_data);
                                dos.flush();
                            }
                        }
                        if(HttpURLConnection.HTTP_OK == conn.getResponseCode()) {
                            byte[] buffer=new byte[1024];
                            int len2=-1;
                            InputStream in=new BufferedInputStream(conn.getInputStream());
                            ByteArrayOutputStream bos=new ByteArrayOutputStream();
                            while((len2=in.read(buffer)) != -1) {
                                bos.write(buffer, 0, len2);
                            }
                            String res=bos.toString(CHAR_SET);
                            in.close();
                            bos.close();
                            Message message = new Message();
                            message.obj=res;
                            message.what=0;
                            handler.sendMessage(message);
                            
                        } else {
                            Message message = new Message();
                            message.what=1;
                            handler.sendMessage(message);
                        }
                    } catch(MalformedURLException e) {
                        e.printStackTrace();
                        Message message = new Message();
                        message.what=1;
                        handler.sendMessage(message);
                    } catch(IOException e) {
                        e.printStackTrace();
                        Message message = new Message();
                        message.what=1;
                        handler.sendMessage(message);
                    }
                } catch(Exception e) {
                    e.printStackTrace();
                    Message message = new Message();
                    message.what=1;
                    handler.sendMessage(message);
                }
            }
        }).start();
    }

    public static HttpClient getNewHttpClient(Context context) {
        try {
            KeyStore trustStore=KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null, null);

            SSLSocketFactory sf=new EasySSLSocketFactory(trustStore);
            sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

            HttpParams params=new BasicHttpParams();

            HttpConnectionParams.setConnectionTimeout(params, 10000);
            HttpConnectionParams.setSoTimeout(params, 10000);

            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
            HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

            SchemeRegistry registry=new SchemeRegistry();
            registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
            registry.register(new Scheme("https", sf, 443));

            ClientConnectionManager ccm=new ThreadSafeClientConnManager(params, registry);

            HttpClient client=new DefaultHttpClient(ccm, params);

            WifiManager wifiManager=(WifiManager)context.getSystemService(Context.WIFI_SERVICE);
            if(!wifiManager.isWifiEnabled()) {
                // 获取当前正在使用的APN接入点
                Uri uri=Uri.parse("content://telephony/carriers/preferapn");
                Cursor mCursor=context.getContentResolver().query(uri, null, null, null, null);
                if(mCursor != null && mCursor.moveToFirst()) {
                    // 游标移至第一条记录，当然也只有一条
                    String proxyStr=mCursor.getString(mCursor.getColumnIndex("proxy"));
                    if(proxyStr != null && proxyStr.trim().length() > 0) {
                        HttpHost proxy=new HttpHost(proxyStr, 80);
                        client.getParams().setParameter(ConnRouteParams.DEFAULT_PROXY, proxy);
                    }
                    mCursor.close();
                }
            }
            return client;
        } catch(Exception e) {
            e.printStackTrace();
            Log.e("HttpUtils", "exception");
            return new DefaultHttpClient();
        }
    }
}
