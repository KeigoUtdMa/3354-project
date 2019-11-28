package cn.projects.team.demo;

import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;



import java.io.IOException;

//import cn.droidlover.xdroidmvp.cache.SharedPref;
//import cn.droidlover.xdroidmvp.imageloader.ILFactory;
//import cn.droidlover.xdroidmvp.net.NetError;
//import cn.droidlover.xdroidmvp.net.NetProvider;
//import cn.droidlover.xdroidmvp.net.RequestHandler;
//import cn.droidlover.xdroidmvp.net.XApi;
import cn.projects.team.demo.greendao.DaoMaster;
import cn.projects.team.demo.greendao.DaoSession;
import okhttp3.CookieJar;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


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
