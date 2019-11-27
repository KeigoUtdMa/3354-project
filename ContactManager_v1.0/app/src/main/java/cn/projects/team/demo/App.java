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
        //ILFactory.getLoader().init(context);
//        XApi.registerProvider(new NetProvider() {
//
//            @Override
//            public Interceptor[] configInterceptors() {
//               /* String token = SharedPref.getInstance(context).getString("token", "");
//                Interceptor mTokenInterceptor = new Interceptor() {
//                    @Override
//                    public Response intercept(Chain chain) throws IOException {
//                        Request originalRequest = chain.request();
//                        originalRequest.newBuilder()
//                      .addHeader("Authorization", "Tokens "+token);
//                        return chain.proceed(originalRequest);
//                    }
//                };
//                Interceptor arr[] = new Interceptor[]{mTokenInterceptor};*/
//                return null;
//            }
//
//            @Override
//            public void configHttps(OkHttpClient.Builder builder) {
//
//            }
//
//            @Override
//            public CookieJar configCookie() {
//                return null;
//            }
//
//            @Override
//            public RequestHandler configHandler() {
//                return null;
//            }
//
//            @Override
//            public long configConnectTimeoutMills() {
//                return 0;
//            }
//
//            @Override
//            public long configReadTimeoutMills() {
//                return 0;
//            }
//
//            @Override
//            public boolean configLogEnable() {
//                return true;
//            }
//
//            @Override
//            public boolean handleError(NetError error) {
//                return false;
//            }
//
//            @Override
//            public boolean dispatchProgressEnable() {
//                return false;
//            }
//        });
    }

    public static Context getContext() {
        return context;
    }
    public static App getInstance() {
        return instance;
    }
    /**
     * 设置greenDao
     */
    private void setDatabase() { // 通过 DaoMaster 的内部类 DevOpenHelper，你可以得到一个便利的 SQLiteOpenHelper 对象。
        // 可能你已经注意到了，你并不需要去编写「CREATE TABLE」这样的 SQL 语句，因为 greenDAO 已经帮你做了。
        // 注意：默认的 DaoMaster.DevOpenHelper 会在数据库升级时，删除所有的表，意味着这将导致数据的丢失。
        // 所以，在正式的项目中，你还应该做一层封装，来实现数据库的安全升级。
        mHelper = new DaoMaster.DevOpenHelper(this, "notes-db", null);
        db = mHelper.getWritableDatabase(); // 注意：该数据库连接属于 DaoMaster，所以多个 Session 指的是相同的数据库连接。
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
