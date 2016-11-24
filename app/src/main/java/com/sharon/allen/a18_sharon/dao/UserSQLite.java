package com.sharon.allen.a18_sharon.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Allen on 2016/6/6.
 */
public class UserSQLite extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "userdata1.db";
    private static final int DATABASE_VERSION = 4;
    private static UserSQLite instance;
    private  Context mContext;

    public UserSQLite(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }

    //单例模式
    public static UserSQLite getInstance(Context context) {
        if (instance == null) {
            instance = new UserSQLite(context);
        }
        return instance;
    }

    public static final String CREATE_USER ="create table person("
            + "id integer primary key autoincrement,"
            + "username text,"
            + "password text,"
            + "headurl text,"
            + "phone text,"
            + "sex text,"
            + "address text,"
            + "money integer,"
            + "signa text,"
            + "messagenum text)";

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_USER);
//        Toast.makeText(mContext,"创建数据库成功",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//        Toast.makeText(mContext,"数据库升级成功",Toast.LENGTH_SHORT).show();
    }
}
