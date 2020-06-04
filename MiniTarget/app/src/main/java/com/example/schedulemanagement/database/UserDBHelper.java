package com.example.schedulemanagement.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.schedulemanagement.bean.UserInfo;

import java.util.ArrayList;

public class UserDBHelper extends SQLiteOpenHelper {
    private static final String TAG = "UserDBHelper";
    private static final String DB_NAME = "user.db"; // 数据库的名称
    private static final int DB_VERSION = 1; // 数据库的版本号
    private static UserDBHelper mHelper = null; // 数据库帮助器的实例
    private SQLiteDatabase mDB = null; // 数据库的实例
    public static final String TABLE_NAME = "user_info"; // 表的名称
    //构造器
    private UserDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }
    //构造器
    private UserDBHelper(Context context, int version) {
        super(context, DB_NAME, null, version);
    }

    // 利用单例模式获取数据库帮助器的唯一实例
    public static UserDBHelper getInstance(Context context, int version) {
        if (version > 0 && mHelper == null) {
            mHelper = new UserDBHelper(context, version);
        } else if (mHelper == null) {
            mHelper = new UserDBHelper(context);
        }
        return mHelper;
    }

    // 打开数据库的读连接
    public SQLiteDatabase openReadLink() {
        if (mDB == null || !mDB.isOpen()) {
            mDB = mHelper.getReadableDatabase();
        }
        return mDB;
    }

    // 打开数据库的写连接
    public void openWriteLink() {
        if (mDB == null || !mDB.isOpen()) {
            mDB = mHelper.getWritableDatabase();
        }
    }

    // 关闭数据库连接
    public void closeLink() {
        if (mDB != null && mDB.isOpen()) {
            mDB.close();
            mDB = null;
        }
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "onCreate");
        String drop_sql = "DROP TABLE IF EXISTS " + TABLE_NAME + ";";
        Log.d(TAG, "drop_sql:" + drop_sql);
        db.execSQL(drop_sql);
        String create_sql = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ("
                + "_id INTEGER PRIMARY KEY  AUTOINCREMENT NOT NULL,"
                + "username VARCHAR NOT NULL"
                //演示数据库升级时要先把下面这行注释
                + ",phone_numbers VARCHAR" + ",password VARCHAR"
                + ");";
        Log.d(TAG, "create_sql:" + create_sql);
        db.execSQL(create_sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    // 往该表添加一条记录
    public long insert(UserInfo info) {
        ArrayList<UserInfo> infoArray = new ArrayList<>();
        infoArray.add(info);
        return insert(infoArray);
    }

    // 往该表添加多条记录
    public long insert(ArrayList<UserInfo> infoArray) {
        long result = -1;
        for (int i = 0; i < infoArray.size(); i++) {
            UserInfo info = infoArray.get(i);
            ArrayList<UserInfo> tempArray;
            // 注意条件语句的等号后面要用单引号括起来
            // 如果存在同样的手机号码，则更新记录
            if (info.phone_number != null && info.phone_number.length() > 0) {
                String condition = String.format("phone_numbers='%s'", info.phone_number);
                tempArray = query(condition);
                if (tempArray.size() > 0) {
                    update(info, condition);
                    result = tempArray.get(0).row_id;
                    continue;
                }
            }
            // 不存在唯一性重复的记录，则插入新记录
            ContentValues cv = new ContentValues();
            cv.put("username", info.username);
            cv.put("phone_numbers", info.phone_number);
            cv.put("password", info.password);
            // 执行插入记录动作，该语句返回插入记录的行号
            result = mDB.insert(TABLE_NAME, "", cv);
            // 添加成功后返回行号，失败后返回-1
            if (result == -1) {
                Log.d(TAG, "insert: " + "error");
                return result;
            }
        }
        return result;
    }

    // 根据条件更新指定的表记录
    public int update(UserInfo info, String condition) {
        ContentValues cv = new ContentValues();
        cv.put("username", info.username);
        cv.put("phone_numbers", info.phone_number);
        cv.put("password", info.password);
        // 执行更新记录动作，该语句返回记录更新的数目
        return mDB.update(TABLE_NAME, cv, condition, null);
    }

    public int update(UserInfo info) {
        // 执行更新记录动作，该语句返回记录更新的数目
        return update(info, "rowid=" + info.row_id);
    }

    // 根据指定条件查询记录，并返回结果数据队列
    public ArrayList<UserInfo> query(String condition) {
        String sql = String.format("select rowid,_id,username," +
                "phone_numbers,password from %s where %s;", TABLE_NAME, condition);
        Log.d(TAG, "query sql: " + sql);
        ArrayList<UserInfo> infoArray = new ArrayList<>();
        // 执行记录查询动作，该语句返回结果集的游标
        Cursor cursor = mDB.rawQuery(sql, null);
        // 循环取出游标指向的每条记录
        while (cursor.moveToNext()) {
            UserInfo info = new UserInfo();
            info.row_id = cursor.getLong(0); // 取出长整型数
            info.username = cursor.getString(2); // 取出字符串
            info.phone_number = cursor.getString(3);
            info.password = cursor.getString(4);
            infoArray.add(info);
        }
        cursor.close(); // 查询完毕，关闭游标
        return infoArray;
    }

    // 根据手机号码查询指定记录
    public UserInfo queryByPhone(String phone) {
        UserInfo info = null;
        ArrayList<UserInfo> infoArray = query(String.format("phone_numbers='%s'", phone));
        if (infoArray.size() > 0) {
            info = infoArray.get(0);
        }
        return info;
    }
}
