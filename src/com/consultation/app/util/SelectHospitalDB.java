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

    private SQLiteDatabase mDataBase;

    private final Context mContext;

    private static final String DATABASE_PATH="/data/data/com.consultation.app/databases/";

    private static final int DATABASE_VERSION=1;

    private static final String DATABASE_NAME="huizhen"+DATABASE_VERSION+".db";

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
