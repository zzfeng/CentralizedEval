package com.sundyn.centralizedeval.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.sundyn.centralizedeval.commen.CommenUnit;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

/**
 * Created by Administrator on 2017/2/21.
 */

public class DatabaseUtil {
    private final String TAG = "DatabaseManager";
    private SQLiteDatabase mDatabase;
    private Context mContext;

    public DatabaseUtil(Context context) {
        mContext = context;
    }

    /**
     * 打开或创建数据库
     *
     * @param database
     *            数据库路径
     * @return 成功返回true，失败返回false
     */
    public boolean openOrCreate(String database) {
        try {
            mDatabase = mContext.openOrCreateDatabase(database,
                    Context.MODE_PRIVATE, null);
        } catch (Exception e) {
            Log.e(TAG, "数据库打开失败" + database);
            return false;
        }

        return true;
    }

    /**
     * 关闭数据库
     */
    public void close() {
        if (mDatabase != null) {
            mDatabase.close();
            mDatabase = null;
        }
    }

    /**
     * 获取数据库对象，进行其他操作
     */
    public SQLiteDatabase getDatabase() {
        if (mDatabase == null) {
            return null;
        }
        return mDatabase;
    }

	/*
	 * public void setDbVersion(int version){ if (mDatabase != null) {
	 * mDatabase.setVersion(version); } }
	 */
    // /**
    // * 获取数据库版本
    // *
    // * @return 数据库版本
    // */
    // public int getDbVersion() {
    // if (mDatabase == null) {
    // return 0;
    // }
    // return mDatabase.getVersion();
    // }

    // /**
    // * 创建表
    // *
    // * @param tableName
    // * 表名称
    // * @param columnName
    // * 表的列结构名
    // * @param columnType
    // * 表的列结构类型
    // * @return 成功返回true，失败返回false
    // */
    // public boolean createTable(String tableName, String columnName[],
    // String columnType[]) {
    // if (mDatabase == null) {
    // Log.e(TAG, "数据库未打开," + tableName + "建表失败");
    // return false;
    // }
    // // SQL:create table EvalData(id integer primary key, data text)
    // StringBuffer sb = new StringBuffer("create table ").append(tableName)
    // .append("(").append(columnName[0]).append(" ")
    // .append(columnType[0]);
    // for (int i = 1; i < columnName.length; i++) {
    // sb.append(", ").append(columnName[i]).append(" ")
    // .append(columnType[i]);
    // }
    // sb.append(")");
    // mDatabase.execSQL(sb.toString());
    //
    // return true;
    // }

    /**
     * 插入数据
     *
     * @param table
     *            表名称
     * @param column
     *            插入的列名称
     * @param value
     *            插入的列数据
     * @return 成功返回true，失败返回false
     */
    public boolean insert(String table, String column[], String value[]) {
        if (mDatabase == null) {
            Log.e(TAG, "数据库未打开,插入数据失败");
            return false;
        }
        try {
            // Cursor cursor = mDatabase.rawQuery("select * from " + table,
            // null);
            // cursor.close();
            // } catch (Exception e) {
            // Log.i(TAG, "未找到表，无法插入数据" + table);
            // return false;
            // }
            // try {
            ContentValues cv = new ContentValues();
            for (int i = 0; i < column.length; i++) {
                cv.put(column[i], value[i]);
            }
            long ret = mDatabase.insert(table, null, cv);
            if (ret == -1) {
                upgrade();
                return false;
            }
        } catch (Exception e) {
            upgrade();
            return false;
        }

        return true;
    }

    /**
     * 删除数据
     *
     * @param table
     *            表名称
     * @param column
     *            删除行对应的列名称
     * @param value
     *            删除行对应的列数据
     * @return 成功返回true，失败返回false
     */
    public boolean delete(String table, String column, String value) {
        if (mDatabase == null) {
            Log.e(TAG, "数据库未打开,删除数据失败");
            return false;
        }
        try {
            // try {
            // Cursor cursor = mDatabase.rawQuery("select * from " + table,
            // null);
            // cursor.close();
            // } catch (Exception e) {
            // Log.i(TAG, "未找到表，无法删除数据" + table);
            // return false;
            // }
            mDatabase.delete(table, column + "='" + value + "'", null);

        } catch (Exception e) {
            upgrade();
            return false;
        }
        return true;
    }

    /**
     * 更新数据
     *
     * @param table
     *            表名称
     * @param where
     *            查询条件
     * @param column
     *            更新的列名称
     * @param value
     *            更新的数据
     * @return 成功返回true，失败返回false
     */
    public boolean update(String table, String where, String column[],
                          String value[]) {
        if (mDatabase == null) {
            Log.e(TAG, "数据库未打开,修改数据失败");
            return false;
        }
        // try {
        // Cursor cursor = mDatabase.rawQuery("select * from " + table, null);
        // cursor.close();
        // } catch (Exception e) {
        // Log.i(TAG, "未找到表，无法修改数据" + table);
        // return false;
        // }
        try {
            ContentValues cv = new ContentValues();
            for (int i = 0; i < column.length; i++) {
                cv.put(column[i], value[i]);
            }
            mDatabase.update(table, cv, where, null);

        } catch (Exception e) {
            upgrade();
            return false;
        }
        return true;
    }

    /**
     * 最基本的查询数据操作
     *
     * @param table
     *            表名称
     * @param where
     *            查询条件
     * @param column
     *            查询列名称
     * @param value
     *            查询结果
     * @return 成功返回true，失败返回false
     */
    public boolean select(String table, String where, String column,
                          ArrayList<String> value) {
        if (mDatabase == null) {
            Log.e(TAG, "数据库未打开,查询数据失败");
            return false;
        }
        try {
            Cursor cur = mDatabase.rawQuery("select * from " + table + " where "
                    + where, null);
            if (cur.moveToFirst()) {
                int col = cur.getColumnIndex(column);
                do {
                    value.add(cur.getString(col));
                } while (cur.moveToNext());
            } else {
                Log.i(TAG, "未找到查询项" + table);
                cur.close();
                return false;
            }
            cur.close();

        } catch (Exception e) {
            upgrade();
            return false;
        }
        return true;
    }

    // /**
    // * 新版本要大于老版本才更新，无用参数传null
    // * @param newVersion 新版本
    // * @param type 0 创建表，1 插入，2 更新，3 删除
    // * @param tableName 表名
    // * @param where 条件
    // * @param key 字段
    // * @param value 列对表值
    // */
    // public void onUpgrade(int newVersion, int type, String tableName,
    // String where, String key[], String value[]) {
    // if (mDatabase == null) {
    // Log.e(TAG, "数据库未打开,查询数据失败");
    // return;
    // }
    // int oldVersion = mDatabase.getVersion();
    // if (oldVersion >= newVersion) {
    // Log.e(TAG, "数据库不用更新");
    // return;
    // }
    // if (type == 0) {
    // createTable(tableName, key, value);
    // } else if (type == 1) {
    // insert(tableName, key, value);
    // } else if (type == 2) {
    // update(tableName, where, key, value);
    // } else if (type == 3) {
    // delete(tableName, key[0], value[0]);
    // } else {
    //
    // }
    // mDatabase.setVersion(newVersion);
    // }

    /**
     * 数据库操作异常，释放数据库模板覆盖本地数据库
     */
    void upgrade() {
        InputStream is;
        try {
            is = mContext.getAssets().open("data.db");
            OutputStream op = new FileOutputStream(CommenUnit.WORK_DIR + "data");
            BufferedInputStream bis = new BufferedInputStream(is);
            BufferedOutputStream bos = new BufferedOutputStream(op);
            byte[] bt = new byte[8192];
            int len = bis.read(bt);
            while (len != -1) {
                bos.write(bt, 0, len);
                len = bis.read(bt);
            }
            bis.close();
            bos.close();

            // 还原滞留评价数据
            mDatabase.execSQL("attach '" + CommenUnit.WORK_DIR
                    + "data' as copy");
            mDatabase
                    .execSQL("insert into copy.DirectTable select * from DirectTable");
            mDatabase
                    .execSQL("insert into copy.IndirectTable select * from IndirectTable");
            // 替换旧数据库
            File oldDb = new File(CommenUnit.WORK_DIR + "data.db");
            File newDb = new File(CommenUnit.WORK_DIR + "data");
            if (oldDb.exists() && newDb.exists()) {
                close();
                oldDb.delete();
            }
            if (newDb.exists()) {
                newDb.renameTo(oldDb);
            }
            Looper lp = Looper.myLooper();
            if (lp == null) {
                Looper.prepare();
            }
            Toast.makeText(mContext, "更新数据库成功，3秒后自动重启", Toast.LENGTH_LONG)
                    .show();
            new Thread(new Runnable() {

                @Override
                public void run() {
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    CommenUnit.restartApk(mContext);
                }
            }).start();
            if (lp == null) {
                Looper.loop();
            }

        } catch (IOException e) {
            Log.e(TAG, TAG + ":数据库升级出错");
        }
    }
}
