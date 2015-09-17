package com.consultation.app.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

public class FileUtil {
	private static final String TAG = FileUtil.class.getSimpleName();
	private Context context;

	public FileUtil(Context context) {
		this.context = context;
	}

	/* Checks if external storage is available for read and write */
	public boolean isExternalStorageWritable() {
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			return true;
		}
		return false;
	}

	/**
	 * 保存图片到制定路径
	 * 
	 * @param filepath
	 * @param bitmap
	 */
	public void saveBitmap(String filepath, Bitmap bitmap) {
		if (!isExternalStorageWritable()) {
			Log.i(TAG, "SD卡不可用，保存失败");
			return;
		}

		if (bitmap == null) {
			return;
		}

		try {
			File file = new File(filepath);
			FileOutputStream outputstream = new FileOutputStream(file);
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputstream);
			outputstream.flush();
			outputstream.close();
		} catch (FileNotFoundException e) {
			Log.i(TAG, e.getMessage());
		} catch (IOException e) {
			Log.i(TAG, e.getMessage());
		}
	}

	/**
	 * 返回当前应用 SD 卡的绝对路径 like
	 * /storage/sdcard0/Android/data/com.example.test/files
	 */
	public String getAbsolutePath() {
		File root = context.getExternalFilesDir(null);

		if (root != null) {
			return root.getAbsolutePath();
		}

		return null;
	}

	public boolean isBitmapExists(String filename) {
		File dir = context.getExternalFilesDir(null);
		File file = new File(dir, filename);

		return file.exists();
	}
}
