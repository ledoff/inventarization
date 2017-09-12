package com.example.adrogozhilov.bonus;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by a.drogozhilov on 29.11.2016.
 */
class DBHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 2;
    public DBHelper(Context context) {
        // конструктор суперкласса
        super(context, "myDB2", null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("LOG_DB", "--- onCreate database ---");
        // создаем таблицу с полями
        db.execSQL("create table mytable ("
                + "id integer primary key autoincrement,"
                + "name text,"
                + "kod  text,"
                + "inum  text,"
                + "kol integer,"
                + "name2 text" + ");");

        db.execSQL("create table mytabletmp ("
                + "id integer primary key autoincrement,"
                + "name text,"
                + "kod  text,"
                + "kol integer" + ");");
        db.execSQL("create table mytablebase ("
                + "id integer primary key autoincrement,"
                + "name text,"
                + "kod  text,"
                + "inum  text,"
                + "kol integer,"
                + "name2 text" + ");");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d("LOG_DELDB", "--- onCreate database ---");
        if (oldVersion == 1) {
            db.execSQL("DROP TABLE IF EXISTS mytable" );
            db.execSQL("DROP TABLE IF EXISTS mytabletmp" );
            db.execSQL("DROP TABLE IF EXISTS mytablebase" );
            onCreate(db);
        }

    }
}
