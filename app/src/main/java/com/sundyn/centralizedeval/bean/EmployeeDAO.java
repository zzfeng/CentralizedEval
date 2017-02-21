package com.sundyn.centralizedeval.bean;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/2/21.
 */

public class EmployeeDAO {
    private  MySqliteOpenHelper myHelper ;

    public final static String TAG = "EmployeeDAO" ;

    private static final int DB_VER = 1;
    private static final String DB_NAME = "employees.db" ;

    public static final String TABLE_NAME = "employee" ;
    public static final String COLUMN_USERNAME = "username" ;
    public static final String COLUMN_PASSWORD = "password" ;
    public static final String COLUMN_CARD = "cardNum" ;

    public EmployeeDAO(Context ctx) {
        myHelper = new MySqliteOpenHelper(ctx, DB_NAME, null, DB_VER) ;
    }

    public void insertOrUpdate(ArrayList<Employee> allEmp ) {
        if(allEmp == null || allEmp.size() == 0)
            return ;

        if(count() - allEmp.size() > 80) {
            deleteAll() ;//冗余数据超过80条时删除全部数据重新插入

            for(Employee emp : allEmp) {
                insert(emp);

            }

        } else {
            for(Employee emp : allEmp) {
                int result = login(emp) ;
                if(result == -1) {
                    //登陆失败，可能未插入或用户密码已经改变
                    insert(emp);
                }

            }
        }


    }

    private int count( ) { //计算当前有多少条数据
        SQLiteDatabase db = myHelper.getWritableDatabase() ;
        Cursor c = db.rawQuery(" SELECT count(*) FROM " + TABLE_NAME, null) ;
        if(c.moveToNext())
            return c.getInt(0);
        else return -1 ;

    }

    private void deleteAll() {
        SQLiteDatabase db = myHelper.getWritableDatabase() ;
        db.execSQL(" DELETE FROM " + TABLE_NAME ) ;
    }

    private void insert(Employee emp) {
        SQLiteDatabase db = myHelper.getWritableDatabase() ;
        //db.beginTransaction();
        db.execSQL(" DELETE FROM " + TABLE_NAME + " WHERE " + COLUMN_USERNAME + " MATCH '" + emp.ext2  + "'") ;
        ContentValues cv = new ContentValues() ;
        cv.put(COLUMN_USERNAME, emp.ext2);
        cv.put(COLUMN_PASSWORD, emp.PassWord);
        cv.put(COLUMN_CARD, emp.cardNum);
        long id = db.insert(TABLE_NAME, null, cv) ;


        if(id == -1)
            Log.e(TAG, "插入员工登陆表失败");
        else {
            Log.e(TAG, "插入员工登陆表成功");
        }
        //db.endTransaction();
        //return (int) id;
    }

    /**
     * 本地离线登陆
     * @param emp
     * @return 成功返回卡号，否则返回-1
     */
    public int login(Employee emp) {
        SQLiteDatabase db = myHelper.getWritableDatabase() ;
        db.beginTransaction();

        String sql = " SELECT * "  + " FROM " + TABLE_NAME + " WHERE "
                + COLUMN_USERNAME + " MATCH '" + COLUMN_PASSWORD + ":" + emp.PassWord + "  " + emp.ext2 + "'" ;
        Log.i(TAG, "login sql = " + sql) ;
        Cursor c = db.rawQuery(sql ,null);

        if(c.getCount() == 0) {
            Log.e(TAG, "未找到员工，用户名密码错误");
            db.endTransaction();
            c.close();
            return -1;
        } else {
            Log.e(TAG, " 找到员工 ");
            if(c.moveToFirst()) {
                int cardNum = c.getInt(c.getColumnIndex(COLUMN_CARD)) ;
                db.endTransaction();
                c.close();
                return cardNum ;
            }

            db.endTransaction();
            c.close();
            return -1 ;
        }
    }

    private static class MySqliteOpenHelper extends SQLiteOpenHelper {

        private   final String CREATE_TABLE_SQL = " CREATE VIRTUAL TABLE "
                + TABLE_NAME + " USING FTS4 ( "
                + COLUMN_USERNAME + " , " + COLUMN_PASSWORD + "," + COLUMN_CARD + ") ;";

        public MySqliteOpenHelper(Context context, String name,
                                  SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_TABLE_SQL);
            //System.out.println(CREATE_TABLE_SQL);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.e(TAG, "升级数据表");
            db.execSQL(" DROP TABLE " + TABLE_NAME  );
            onCreate(db);
        }

    }
}
