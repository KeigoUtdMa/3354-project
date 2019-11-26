package cn.projects.team.demo;

import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import java.io.IOException;
import cn.projects.team.demo.greendao.DaoMaster;
import cn.projects.team.demo.greendao.DaoSession;


public class App extends Application {

    private static Context context;
    private DaoMaster.DevOpenHelper mHelper;
    private SQLiteDatabase db;
    private DaoMaster mDaoMaster;
    private DaoSession mDaoSession;
    private static App instance;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        instance = this;
        setDatabase();

    }

    public static Context getContext() {
        return context;
    }
    public static App getInstance() {
        return instance;
    }
    /**
     *
     */
    private void setDatabase() {
        mHelper = new DaoMaster.DevOpenHelper(this, "notes-db", null);
        db = mHelper.getWritableDatabase(); 
        mDaoMaster = new DaoMaster(db);
        mDaoSession = mDaoMaster.newSession();
    }


    public DaoSession getDaoSession() {
        return mDaoSession;
    }

    public SQLiteDatabase getDb() {
        return db;
    }
}
