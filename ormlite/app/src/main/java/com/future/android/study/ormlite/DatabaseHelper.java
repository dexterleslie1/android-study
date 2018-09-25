package com.future.android.study.ormlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

/**
 * @author Dexterleslie.Chan
 */
public class DatabaseHelper extends OrmLiteSqliteOpenHelper {
    private final static String TAG= DatabaseHelper.class.getSimpleName();

    private static final String DatabaseNamePrefix="ormlite";
    private static final int DATABASE_VERSION = 2;

    /**
     *
     * @param context
     */
    public DatabaseHelper(Context context,int userId){
        super(context, DatabaseNamePrefix+(userId>0?userId:""), null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, User.class);
        } catch (SQLException e) {
            Log.e(TAG,e.getMessage(),e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
//        try {
//            TableUtils.dropTable(connectionSource, User.class, true);
//            onCreate(database, connectionSource);
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
        if(newVersion==2) {
            database.execSQL("create unique index uniqueUserLoginname on t_user(loginname)");
        }
    }

    /**
     * 必须重写downgrade并且不能调用super.downgrade方法才能避免数据库降级时抛出错误
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(TAG,"Database downgrade from version "+newVersion+" to "+oldVersion);
    }
}
