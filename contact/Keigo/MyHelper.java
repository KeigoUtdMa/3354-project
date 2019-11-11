package com.example.contact_cs3354;
//this is a data base helper.
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class  MyHelper extends SQLiteOpenHelper{


    public MyHelper(Context context){
        super(context, "keigo.db", null ,2);
    }
    @Override

    public void onCreate(SQLiteDatabase db){
        db.execSQL("create table person(id integer primary key autoincrement,name varchar(20),phone varchar(20) unique)");
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){

    }
}