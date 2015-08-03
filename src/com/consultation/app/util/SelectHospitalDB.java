package com.consultation.app.util;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

@SuppressLint("SdCardPath")
public class SelectHospitalDB extends SQLiteOpenHelper {

    // private static final String DATABASE_NAME="hospitals.db";
    //
    // private static final int DATABASE_VERSION=1;
    //
    // private static final String TABLE_NAME="Hospitals_table";
    //
    // // 表里面的三个内容
    // private static final String ID="_id";
    //
    // private static final String PROVINCE = "_province";
    //
    // private static final String CITY="_city";
    //
    // private static final String NAME="_name";
    //
    // public SelectHospitalDB(Context context){
    // super(context, DATABASE_NAME, null, DATABASE_VERSION);
    // }
    //
    // @Override
    // public void onCreate(SQLiteDatabase db) {
    // String sql=
    // "CREATE TABLE " + TABLE_NAME + " (" + ID + " INTEGER primary key autoincrement, " + PROVINCE + " text, " + CITY + " text, " +
    // NAME
    // + " text);";
    // db.execSQL(sql);
    //
    // }
    //
    // @Override
    // public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    // String sql="DROP TABLE IF EXISTS " + TABLE_NAME;
    // db.execSQL(sql);
    // onCreate(db);
    // }
    //
    // public Cursor selectAll() {
    // SQLiteDatabase db=this.getReadableDatabase();
    // Cursor cursor=db.query(TABLE_NAME, null, null, null, null, null, ID + " ASC");
    // return cursor;
    // }
    //
    // public Cursor selectByCity(String city) {
    // SQLiteDatabase db=this.getReadableDatabase();
    // String[] selectionArgs = {city};
    // Cursor cursor=db.query(TABLE_NAME, null, CITY+"=?", selectionArgs, null, null, ID + " ASC");
    // return cursor;
    // }
    //
    // public Cursor selectByProvince(String province) {
    // SQLiteDatabase db=this.getReadableDatabase();
    // String[] selectionArgs = {province};
    // Cursor cursor=db.query(TABLE_NAME, null, PROVINCE+"=?", selectionArgs, null, null, ID + " ASC");
    // return cursor;
    // }
    //
    // /* 增加操作 */
    // public long insert(String province, String city,String name) {
    // SQLiteDatabase db=this.getWritableDatabase();
    // ContentValues cv=new ContentValues();
    // cv.put(NAME, name);
    // cv.put(CITY, city);
    // cv.put(PROVINCE, province);
    // long row=db.insert(TABLE_NAME, null, cv);
    // return row;
    // }
    //
    // /* 删除操作 */
    // public void delete(int id) {
    // SQLiteDatabase db=this.getWritableDatabase();
    // String where=ID + "=?";
    // String[] whereValue={Integer.toString(id)};
    // db.delete(TABLE_NAME, where, whereValue);
    // }
    //
    // public void delete() {
    // SQLiteDatabase db=this.getWritableDatabase();
    // db.delete(TABLE_NAME, null, null);
    // }
    //
    // /* 修改操作 */
    // // id是你要修改的id号，name是新的名字
    // public void update(int id, String name) {
    // SQLiteDatabase db=this.getWritableDatabase();
    // String where=ID + "=?";
    // String[] whereValue={Integer.toString(id)};
    // ContentValues cv=new ContentValues();
    // cv.put(NAME, name);
    // db.update(TABLE_NAME, cv, where, whereValue);
    // }

    private SQLiteDatabase mDataBase;

    private final Context mContext;

    private static final String DATABASE_PATH="/data/data/com.consultation.app/databases/";

    private static final String DATABASE_NAME="huizhen.db";

    private static final int DATABASE_VERSION=1;

    public SelectHospitalDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.mContext=context;
    }

    public SelectHospitalDB(Context context, String name, CursorFactory factory, int version) {
        super(context, name, factory, version);
        this.mContext=context;
    }

    public void createDataBase() throws IOException {
        boolean dbExist=checkDataBase();
        if(dbExist) {
        } else {
            this.getReadableDatabase();
            try {
                copyDataBase();
            } catch(IOException e) {
                throw new Error("Error copying database");
            }
        }
    }

    private boolean checkDataBase() {
        SQLiteDatabase checkDB=null;
        try {
            String myPath=DATABASE_PATH + DATABASE_NAME;
            checkDB=SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
        } catch(SQLiteException e) {
        }

        if(checkDB != null) {
            checkDB.close();
        }
        return checkDB != null ? true : false;
    }

    private void copyDataBase() throws IOException {
        InputStream myInput=mContext.getAssets().open(DATABASE_NAME);

        String outFileName=DATABASE_PATH + DATABASE_NAME;

        OutputStream myOutput=new FileOutputStream(outFileName);

        byte[] buffer=new byte[1024];
        int length;
        while((length=myInput.read(buffer)) > 0) {
            myOutput.write(buffer, 0, length);
        }
        myOutput.flush();
        myOutput.close();
        myInput.close();
    }

    public void openDataBase() throws SQLException {
        String myPath=DATABASE_PATH + DATABASE_NAME;
        mDataBase=SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
    }

    @Override
    public synchronized void close() {
        if(mDataBase != null)
            mDataBase.close();
        super.close();

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

}
