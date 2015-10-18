package com.consultation.app.util;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

/**
 * 常用工具类
 */
@SuppressLint({"WorldWriteableFiles", "WorldReadableFiles"})
public class CommonUtil {

    private static ProgressDialog mProgressDialog;

    private static ProgressDialog AlipayProgressDialog;

    public static AlertDialog.Builder showAlertDialog(Context context, String strTitle, String strMessage,
        DialogInterface.OnClickListener posListener, DialogInterface.OnClickListener negListener) {
        String submitTxt=context.getString(CommonUtil.getResourceId(context, "string", "submit_button_txt"));
        String cacelTxt=context.getString(CommonUtil.getResourceId(context, "string", "cancel_button_txt"));
        return showAlertDialog(context, strTitle, strMessage, submitTxt, posListener, cacelTxt, negListener, null);
    }

    public static AlertDialog.Builder showAlertDialog(Context context, String strTitle, String strMessage, String strPositive,
        DialogInterface.OnClickListener posListener, String strNegative, DialogInterface.OnClickListener negListener,
        View customView) {
        if(context == null) {
            return null;
        }
        try {
            AlertDialog.Builder anAlert=new AlertDialog.Builder(context);
            anAlert.setTitle(strTitle);
            if(strMessage != null && strMessage.length() > 0) {
                anAlert.setMessage(strMessage);
            }
            anAlert.setPositiveButton(strPositive, posListener);
            anAlert.setNegativeButton(strNegative, negListener);
            if(customView != null) {
                anAlert.setView(customView);
            }
            anAlert.create();
            anAlert.show();
            return anAlert;
        } catch(Exception e) {
        }
        return null;
    }

    /**
     * 弹出等待对话框
     * @param context
     * @param str
     */
    public static void showLoadingDialog(Context context, String str) {
        try {
            closeLodingDialog();
            if(context instanceof Activity) {
                if(mProgressDialog == null) {
                    mProgressDialog=new ProgressDialog(context);
                    mProgressDialog.setMessage(str);
                    mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    mProgressDialog.setCanceledOnTouchOutside(false);
                }
            }
            if(!mProgressDialog.isShowing()) {
                mProgressDialog.show();
            }
        } catch(Exception e) {
        }
    }

    /**
     * 弹出等支付宝待对话框
     * @param context
     * @param str
     */
    public static void showAlipayLoadingDialog(Context context) {
        try {
            if(context instanceof Activity) {
                AlipayProgressDialog=new ProgressDialog(context);
                AlipayProgressDialog.setMessage("Loading...");
                AlipayProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                AlipayProgressDialog.setCanceledOnTouchOutside(false);
            }
            if(!AlipayProgressDialog.isShowing()) {
                AlipayProgressDialog.show();
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 关闭支付宝等待框
     */
    public static void closeAlipayLodingDialog() {
        if(AlipayProgressDialog != null && AlipayProgressDialog.isShowing()) {
            AlipayProgressDialog.dismiss();
            AlipayProgressDialog=null;
        }
    }

    public static void showLoadingDialog(Context context) {
        showLoadingDialog(context, "数据加载中...");
    }

    /**
     * 关闭等待框
     */
    public static void closeLodingDialog() {
        if(mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
            mProgressDialog=null;
        }
    }

    /**
     * 弹出提示框
     * @param context
     * @param str
     */
    public static void showWaningToast(Context context, String str) {
        if(context == null)
            return;
        try {
            Toast.makeText(context, str, Toast.LENGTH_LONG).show();
        } catch(Exception e) {
        }
    }

    public static void killApplication() {
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    /**
     * 查看app是否已经安装
     * @param context
     * @param packageName
     * @return
     */
    public static boolean checkAppExist(Context context, String packageName) {
        PackageInfo packageInfo;
        try {
            packageInfo=context.getPackageManager().getPackageInfo(packageName, 0);

        } catch(NameNotFoundException e) {
            packageInfo=null;
        }
        if(packageInfo == null) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 返回屏幕的方向 Configuration.ORIENTATION_LANDSCAPE 或者 Configuration.ORIENTATION_PORTRAIT
     * @param context
     * @return
     */
    public static int getSceneOrientation(Context context) {
        return context.getResources().getConfiguration().orientation;
    }

    // 检查是否含有scard卡
    public static boolean hasSdcard() {
        String state=Environment.getExternalStorageState();
        if(state.equals(Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }

    // 图片圆角
    public static Bitmap toRoundCorner(Bitmap bitmap, int pixels) {

        Bitmap output=Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
        Canvas canvas=new Canvas(output);

        final int color=0xff424242;
        final Paint paint=new Paint();
        final Rect rect=new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF=new RectF(rect);
        final float roundPx=pixels;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    // file转换成bitmap
    public static Bitmap fileToBitmap(String filename) {
        try {
            File f=new File(filename);
            if(!f.exists()) {
                return null;
            }
            Bitmap tmp=BitmapFactory.decodeFile(filename);
            return tmp;
        } catch(Exception e) {
            return null;
        }
    }

    // 将图片保存到/data/data/packagename/files下
    @SuppressLint("WorldReadableFiles")
    public static String bitmapToFile(Context ctx, String imageName, Bitmap mBitmap) throws IOException {
        FileOutputStream fOut=null;
        try {
            String filePath=ctx.getApplicationContext().getFilesDir().getAbsolutePath() + File.separator + imageName;
            fOut=ctx.openFileOutput(imageName, Context.MODE_WORLD_READABLE);
            mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
            return filePath;
        } catch(IOException e) {
            e.printStackTrace();
        } finally {
            fOut.flush();
            fOut.close();
        }
        return null;
    }

    /**
     * 现在图片到package目录
     * @param ctx
     * @param path
     * @param apkname
     * @return
     * @throws IOException
     */
    public static boolean downloadApktoappDir(Context ctx, String path, String apkname) throws IOException {
        URL url;
        FileOutputStream fos=null;
        BufferedInputStream bis=null;
        InputStream is=null;
        try {
            url=new URL(path);
            HttpURLConnection conn=(HttpURLConnection)url.openConnection();
            conn.setConnectTimeout(5000);
            is=conn.getInputStream();
            fos=ctx.openFileOutput(apkname, Context.MODE_WORLD_READABLE + Context.MODE_WORLD_WRITEABLE);
            bis=new BufferedInputStream(is);
            byte[] buffer=new byte[1024];
            int len;
            while((len=bis.read(buffer)) != -1) {
                fos.write(buffer, 0, len);
            }
        } catch(Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if(fos != null) {
                fos.close();
                bis.close();
                is.close();
            }
        }
        return true;
    }

    /**
     * 获取文件名
     * @param url
     * @return
     */
    public static String getFilename(String url) {
        String filename="";
        int startid=0;
        int endid=0;
        startid=url.lastIndexOf("/") + 1;
        endid=url.length();
        filename=url.substring(startid, endid);
        return filename;
    }

    public static Drawable getConvertDrawable(String filepath) {
        Bitmap bitmap=BitmapFactory.decodeFile(filepath);
        Drawable drawable=new BitmapDrawable(bitmap);
        return drawable;
    }

    /**
     * 根据图片路径获取Bitmap对象
     * @param filepath
     * @return
     */
    public static Bitmap getConvertBitmap(String filepath) {
        Bitmap bitmap=BitmapFactory.decodeFile(filepath);
        return bitmap;
    }

    public static int getFontSize(Context context, int textSize) {
        DisplayMetrics dm=new DisplayMetrics();
        WindowManager windowManager=(WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(dm);
        int screenHeight=dm.heightPixels;
        // screenWidth = screenWidth > screenHeight ? screenWidth : screenHeight;
        int rate=(int)(textSize * (float)screenHeight / 1280);
        return rate;
    }

    public static Bitmap readBitMap(int widths, String filepath) {
        BitmapFactory.Options opts=new BitmapFactory.Options();
        // 设置为ture只获取图片大小
        opts.inJustDecodeBounds=true;
        opts.inPreferredConfig=Bitmap.Config.ARGB_8888;
        // 返回为空
        BitmapFactory.decodeFile(filepath, opts);
        int width=opts.outWidth;
        int height=opts.outHeight;
        float scaleWidth=0.f, scaleHeight=0.f;
        int heights=(height * widths) / width;
        if(width > widths || height > heights) {
            // 缩放
            scaleWidth=((float)width) / widths;
            scaleHeight=((float)height) / heights;
        }
        opts.inJustDecodeBounds=false;
        float scale=Math.max(scaleWidth, scaleHeight);
        opts.inSampleSize=(int)scale;
        WeakReference<Bitmap> weak=new WeakReference<Bitmap>(BitmapFactory.decodeFile(filepath, opts));
        return Bitmap.createScaledBitmap(weak.get(), widths, heights, true);
    }

    /**
     * 获取资源id
     */
    public static int getResourceId(Context context, String type, String name) {
        try {
            int resID=context.getResources().getIdentifier(name, type, context.getPackageName());
            return resID;
        } catch(Exception e) {
            new StringBuilder("uiType: ").append(type).append(" idName: ").append(name).toString();
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 加载本地图片
     * @param path
     * @return
     */
    public static Bitmap getLoacalBitmap(String path) {
        try {
            FileInputStream fis=new FileInputStream(path);
            BitmapFactory.Options options=new BitmapFactory.Options();
            options.inTempStorage=new byte[100 * 1024];
            // 3.设置位图颜色显示优化方式
            // ALPHA_8：每个像素占用1byte内存（8位）
            // ARGB_4444:每个像素占用2byte内存（16位）
            // ARGB_8888:每个像素占用4byte内存（32位）
            // RGB_565:每个像素占用2byte内存（16位）
            // Android默认的颜色模式为ARGB_8888，这个颜色模式色彩最细腻，显示质量最高。但同样的，占用的内存//也最大。也就意味着一个像素点占用4个字节的内存。我们来做一个简单的计算题：3200*2400*4
            // bytes //=30M。如此惊人的数字！哪怕生命周期超不过10s，Android也不会答应的。
            options.inPreferredConfig=Bitmap.Config.RGB_565;
            // 4.设置图片可以被回收，创建Bitmap用于存储Pixel的内存空间在系统内存不足时可以被回收
            options.inPurgeable=true;
            // 5.设置位图缩放比例
            // width，hight设为原来的四分一（该参数请使用2的整数倍）,这也减小了位图占用的内存大小；例如，一张//分辨率为2048*1536px的图像使用inSampleSize值为4的设置来解码，产生的Bitmap大小约为//512*384px。相较于完整图片占用12M的内存，这种方式只需0.75M内存(假设Bitmap配置为//ARGB_8888)。
            options.inSampleSize=1;
            // 6.设置解码位图的尺寸信息
            options.inInputShareable=true;
            // 7.解码位图
            return BitmapFactory.decodeStream(fis, null, options);
            // return BitmapFactory.decodeStream(fis); // /把流转化为Bitmap图片
        } catch(FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 把图片转换成字符串
     * @param path
     * @return
     */
    public static String bitmapToString(Bitmap bitmap) {
        int rate=100;
        ByteArrayOutputStream os=new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, rate, os);
        while(os.size() > 200 * 1024) {
            os.reset();
            rate=rate - 10;
            if(rate < 0) {
                return null;
            } else {
                bitmap.compress(Bitmap.CompressFormat.JPEG, rate, os);
            }
        }
        return new String(Base64Coder.encodeLines(os.toByteArray()));
    }

    public static Bitmap drawableToRoundBitmap(Drawable drawable, int pixels) {
        int width=drawable.getIntrinsicWidth();
        int height=drawable.getIntrinsicHeight();
        Bitmap bitmap=
            Bitmap.createBitmap(width, height, drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                : Bitmap.Config.RGB_565);
        Canvas canvas=new Canvas(bitmap);
        drawable.setBounds(0, 0, width, height);
        drawable.draw(canvas);
        return toRoundCorner(bitmap, pixels);
    }

    public static void appendToFile(String content, File file) {
        File dir=file.getParentFile();
        if(!dir.exists()) {
            dir.mkdirs();
        }
        OutputStream fos=null;
        try {
            fos=new FileOutputStream(file, true);
            OutputStreamWriter write=new OutputStreamWriter(fos, "UTF-8");
            BufferedWriter writer=new BufferedWriter(write);
            writer.write(content);
            writer.close();
        } catch(FileNotFoundException e) {
            e.printStackTrace();
        } catch(IOException e) {
            e.printStackTrace();
        } finally {
            if(null != fos) {
                try {
                    fos.close();
                } catch(IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
