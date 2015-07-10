package com.consultation.app.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SelectHospitalDB extends SQLiteOpenHelper {

    private static final String DATABASE_NAME="hospitals.db";

    private static final int DATABASE_VERSION=1;

    private static final String TABLE_NAME="Hospitals_table";

    // 表里面的三个内容
    private static final String ID="_id";
    
    private static final String CITY="_city";

    private static final String NAME="_name";

    public SelectHospitalDB(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql=
            "CREATE TABLE " + TABLE_NAME + " (" + ID + " INTEGER primary key autoincrement, " + CITY + " text, " + NAME
                + " text);";
        db.execSQL(sql);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sql="DROP TABLE IF EXISTS " + TABLE_NAME;
        db.execSQL(sql);
        onCreate(db);
    }

    public Cursor selectAll() {
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cursor=db.query(TABLE_NAME, null, null, null, null, null, ID + " ASC");
        return cursor;
    }
    
    public Cursor selectByCity(String city) {
        SQLiteDatabase db=this.getReadableDatabase();
        String[] selectionArgs = {city};  
        Cursor cursor=db.query(TABLE_NAME, null, CITY+"=?", selectionArgs, null, null, ID + " ASC");
        return cursor;
    }
    
    /* 增加操作 */
    public long insert(String city,String name) {
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues cv=new ContentValues();
        cv.put(NAME, name);
        cv.put(CITY, city);
        long row=db.insert(TABLE_NAME, null, cv);
        return row;
    }

    /* 删除操作 */
    public void delete(int id) {
        SQLiteDatabase db=this.getWritableDatabase();
        String where=ID + "=?";
        String[] whereValue={Integer.toString(id)};
        db.delete(TABLE_NAME, where, whereValue);

    }

    /* 修改操作 */
    // id是你要修改的id号，name是新的名字
    public void update(int id, String name) {
        SQLiteDatabase db=this.getWritableDatabase();
        String where=ID + "=?";
        String[] whereValue={Integer.toString(id)};

        ContentValues cv=new ContentValues();
        cv.put(NAME, name);

        db.update(TABLE_NAME, cv, where, whereValue);
    }

}
