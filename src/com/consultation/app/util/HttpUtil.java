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

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.util.Log;

public class HttpUtil {

    private static final String CHAR_SET="utf-8";
    
    private static HttpUtil httpUtil;

    public static HttpUtil getInstance(Context context) {
        if(null == httpUtil) {
            httpUtil=new HttpUtil();
        }
        return httpUtil;
    }
    
    public void uploadFiles(final String httpUrl, final ConsultationCallbackHandler callbackHandler, final File[] files){
        new Thread(new Runnable() {
            
            @Override
            public void run() {
                try {                
                    long  boundary =  System.currentTimeMillis();
                    String prefix = "-----------------------------" , line_end = "\r\n"; 
                    int timeout=60000 *3;// 3分钟
                    try {
                        URL url = new URL(httpUrl);
                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        conn.setReadTimeout(timeout);
                        conn.setConnectTimeout(timeout);
                        conn.setDoInput(true); 
                        conn.setDoOutput(true); 
                        conn.setUseCaches(false); 
                        conn.setRequestMethod("POST"); 
                        conn.setRequestProperty("Charset", CHAR_SET); 
                        conn.setRequestProperty("connection", "keep-alive");   
                        conn.setRequestProperty("Content-Type",  "multipart/form-data;boundary=" + boundary); 
                        OutputStream outputSteam=conn.getOutputStream();
                        DataOutputStream dos = new DataOutputStream(outputSteam);
//                        sb.append(prefix);
//                        sb.append(boundary);
//                        sb.append(line_end);
//                        sb.append("Content-Disposition: form-data; name=\"parms\""); 
//                        sb.append("Content-Type: text/plain ; charset="+CHAR_SET+line_end);
//                        dos.write(sb.toString().getBytes());
//                        String str ="sssssss";
//                        dos.write(str.getBytes());
//                        String splitStr=prefix + boundary;
//                        splitStr += line_end;
//                        dos.write(splitStr);
                        for(int i=0; i<files.length; i++){
                            File file = files[i];
                            if(file!=null){
                                StringBuffer sb = new StringBuffer();
                                sb.append(prefix);
                                sb.append(boundary);
                                sb.append(line_end);
                                sb.append("Content-Disposition: form-data; name=\"img\"; filename=\""+file.getName()+"\""+line_end); 
                                sb.append("Content-Type: application/octet-stream; charset="+CHAR_SET+line_end);
                                sb.append(line_end);
                                dos.write(sb.toString().getBytes());
                                InputStream is = new FileInputStream(file);
                                byte[] bytes = new byte[1024];
                                int len = 0;
                                while((len=is.read(bytes))!=-1){
                                    dos.write(bytes, 0, len);
                                }
                                is.close();
                                dos.write(line_end.getBytes());
                                String splitStr=prefix + boundary;
                                if(i==files.length-1){
                                    splitStr += prefix;
                                }
                                splitStr += line_end;
                                byte[] end_data = splitStr.getBytes();
                                dos.write(end_data);
                                dos.flush();
                            }
                        }
                        if(HttpURLConnection.HTTP_OK == conn.getResponseCode()) {
                            byte[] buffer=new byte[1024];
                            int len2=-1;
                            InputStream in = new BufferedInputStream(conn.getInputStream());
                            ByteArrayOutputStream bos=new ByteArrayOutputStream();
                            while((len2=in.read(buffer)) != -1) {
                                bos.write(buffer, 0, len2);
                            }
                            String res=bos.toString(CHAR_SET);
                            in.close();
                            bos.close();
                            callbackHandler.onSuccess(res, ConsultionStatusCode.SUCCESS);
                        }else{
                            callbackHandler.onFailure(new ConsultationCallbackException(ConsultionStatusCode.FAIL, "网络请求异常"));
                        }
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                        callbackHandler.onFailure(new ConsultationCallbackException(ConsultionStatusCode.FAIL, "网络请求异常"));
                    } catch (IOException e) {
                        e.printStackTrace();
                        callbackHandler.onFailure(new ConsultationCallbackException(ConsultionStatusCode.FAIL, "网络请求异常"));
                    }
                } catch(Exception e) {
                    e.printStackTrace();
                    callbackHandler.onFailure(new ConsultationCallbackException(ConsultionStatusCode.FAIL, "网络请求异常"));
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

    // public void uploadFiles(final String path, final Map<String, String> params, final FormFile[] files, final
    // ConsultationCallbackHandler callbackHandler) {
    // new Thread(new Runnable() {
    //
    // @Override
    // public void run() {
    // try {
    // final String BOUNDARY="---------------------------7da2137580612"; // 数据分隔线
    // final String endline="--" + BOUNDARY + "--\r\n";// 数据结束标志
    // int fileDataLength=0;
    // for(FormFile uploadFile: files) {// 得到文件类型数据的总长度
    // StringBuilder fileExplain=new StringBuilder();
    // fileExplain.append("--");
    // fileExplain.append(BOUNDARY);
    // fileExplain.append("\r\n");
    // fileExplain.append("Content-Disposition: form-data;name=\"" + uploadFile.getParameterName()
    // + "\";filename=\"" + uploadFile.getFilname() + "\"\r\n");
    // fileExplain.append("Content-Type: " + uploadFile.getContentType() + "\r\n\r\n");
    // fileExplain.append("\r\n");
    // fileDataLength+=fileExplain.length();
    // if(uploadFile.getInStream() != null) {
    // fileDataLength+=uploadFile.getFile().length();
    // } else {
    // fileDataLength+=uploadFile.getData().length;
    // }
    // }
    // StringBuilder textEntity=new StringBuilder();
    // for(Map.Entry<String, String> entry: params.entrySet()) {// 构造文本类型参数的实体数据
    // textEntity.append("--");
    // textEntity.append(BOUNDARY);
    // textEntity.append("\r\n");
    // textEntity.append("Content-Disposition: form-data; name=\"" + entry.getKey() + "\"\r\n\r\n");
    // textEntity.append(entry.getValue());
    // textEntity.append("\r\n");
    // }
    // // 计算传输给服务器的实体数据总长度
    // int dataLength=textEntity.toString().getBytes().length + fileDataLength + endline.getBytes().length;
    //
    // URL url=new URL(path);
    //
    // int port=url.getPort() == -1 ? 80 : url.getPort();
    // Socket socket=new Socket(InetAddress.getByName(url.getHost()), port);
    // OutputStream outStream=socket.getOutputStream();
    // // 下面完成HTTP请求头的发送
    // String requestmethod="POST " + url.getPath() + " HTTP/1.1\r\n";
    // outStream.write(requestmethod.getBytes());
    // String accept=
    // "Accept: image/gif, image/jpeg, image/jpg, image/pjpeg, application/x-shockwave-flash, application/xaml+xml, application/vnd.ms-xpsdocument, application/x-ms-xbap, application/x-ms-application, application/vnd.ms-excel, application/vnd.ms-powerpoint, application/msword, */*\r\n";
    // outStream.write(accept.getBytes());
    // String language="Accept-Language: zh-CN\r\n";
    // outStream.write(language.getBytes());
    // String contenttype="Content-Type: multipart/form-data; boundary=" + BOUNDARY + "\r\n";
    // outStream.write(contenttype.getBytes());
    // String contentlength="Content-Length: " + dataLength + "\r\n";
    // outStream.write(contentlength.getBytes());
    // String alive="Connection: Keep-Alive\r\n";
    // outStream.write(alive.getBytes());
    // String host="Host: " + url.getHost() + ":" + port + "\r\n";
    // outStream.write(host.getBytes());
    // // 写完HTTP请求头后根据HTTP协议再写一个回车换行
    // outStream.write("\r\n".getBytes());
    // // 把所有文本类型的实体数据发送出来
    // outStream.write(textEntity.toString().getBytes());
    // // 把所有文件类型的实体数据发送出来
    // for(FormFile uploadFile: files) {
    // StringBuilder fileEntity=new StringBuilder();
    // fileEntity.append("--");
    // fileEntity.append(BOUNDARY);
    // fileEntity.append("\r\n");
    // fileEntity.append("Content-Disposition: form-data;name=\"" + uploadFile.getParameterName()
    // + "\";filename=\"" + uploadFile.getFilname() + "\"\r\n");
    // fileEntity.append("Content-Type: " + uploadFile.getContentType() + "\r\n\r\n");
    // outStream.write(fileEntity.toString().getBytes());
    // if(uploadFile.getInStream() != null) {
    // byte[] buffer=new byte[1024];
    // int len=0;
    // while((len=uploadFile.getInStream().read(buffer, 0, 1024)) != -1) {
    // outStream.write(buffer, 0, len);
    // }
    // uploadFile.getInStream().close();
    // } else {
    // outStream.write(uploadFile.getData(), 0, uploadFile.getData().length);
    // }
    // outStream.write("\r\n".getBytes());
    // }
    // // 下面发送数据结束标志，表示数据已经结束
    // outStream.write(endline.getBytes());
    //
    // BufferedReader reader=new BufferedReader(new InputStreamReader(socket.getInputStream()));
    // if(reader.readLine().indexOf("200") == -1) {// 读取web服务器返回的数据，判断请求码是否为200，如果不是200，代表请求失败
    // callbackHandler.onSuccess("失败", -1);
    // System.out.println("shibai");
    // } else {
    // System.out.println(reader.readLine());
    // System.out.println("chengc");
    // callbackHandler.onSuccess("成功", 1);
    // }
    // outStream.flush();
    // outStream.close();
    // reader.close();
    // socket.close();
    // } catch(MalformedURLException e) {
    // e.printStackTrace();
    // System.out.println("yichang1");
    // callbackHandler.onFailure(new ConsultationCallbackException(0, "文件上传失败"));
    // } catch(UnknownHostException e) {
    // e.printStackTrace();
    // System.out.println("yichang2");
    // } catch(IOException e) {
    // e.printStackTrace();
    // System.out.println("yichang3");
    // }
    // }
    // }).start();
    // }

    // public void uploadFile(String urlString, String imagePath, ConsultationCallbackHandler handler) {
    // File file=new File(imagePath);
    // String BOUNDARY=UUID.randomUUID().toString(); // 边界标识 随机生成
    // String PREFIX="--", LINE_END="\r\n";
    // String CONTENT_TYPE="multipart/form-data"; // 内容类型
    // try {
    // URL url=new URL(urlString);
    // HttpURLConnection conn=(HttpURLConnection)url.openConnection();
    // conn.setReadTimeout(10 * 10000000);
    // conn.setConnectTimeout(10 * 10000000);
    // conn.setDoInput(true); // 允许输入流
    // conn.setDoOutput(true); // 允许输出流
    // conn.setUseCaches(false); // 不允许使用缓存
    // conn.setRequestMethod("POST"); // 请求方式
    // conn.setRequestProperty("Charset", "utf-8");
    // // 设置编码
    // conn.setRequestProperty("connection", "keep-alive");
    // conn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary=" + BOUNDARY);
    // if(file != null) {
    // /** * 当文件不为空，把文件包装并且上传 */
    // OutputStream outputSteam=conn.getOutputStream();
    // DataOutputStream dos=new DataOutputStream(outputSteam);
    // StringBuffer sb=new StringBuffer();
    // sb.append(PREFIX);
    // sb.append(BOUNDARY);
    // sb.append(LINE_END);
    // /**
    // * 这里重点注意： name里面的值为服务器端需要key 只有这个key 才可以得到对应的文件 filename是文件的名字，包含后缀名的 比如:abc.png
    // */
    // sb.append("Content-Disposition: form-data; name=\"img\"; filename=\"" + file.getName() + "\"" + LINE_END);
    // sb.append("Content-Type: application/octet-stream; charset=utf-8" + LINE_END);
    // sb.append(LINE_END);
    // dos.write(sb.toString().getBytes());
    // InputStream is=new FileInputStream(file);
    // byte[] bytes=new byte[1024];
    // int len=0;
    // while((len=is.read(bytes)) != -1) {
    // dos.write(bytes, 0, len);
    // }
    // is.close();
    // dos.write(LINE_END.getBytes());
    // byte[] end_data=(PREFIX + BOUNDARY + PREFIX + LINE_END).getBytes();
    // dos.write(end_data);
    // dos.flush();
    // /**
    // * 获取响应码 200=成功 当响应成功，获取响应的流
    // */
    // int res=conn.getResponseCode();
    // if(res == 200) {
    // handler.onSuccess("", ConsultionStatusCode.SUCCESS);
    // } else {
    // handler.onSuccess("", ConsultionStatusCode.FAIL);
    // }
    // }
    // } catch(MalformedURLException e) {
    // e.printStackTrace();
    // } catch(IOException e) {
    // e.printStackTrace();
    // }
    // }
}
