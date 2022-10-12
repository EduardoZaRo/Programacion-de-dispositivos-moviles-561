package com.example.proyectofinal1.BasesDeDatos;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class FotosDatabaseHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "Fotos"; // the name of our database
    private static final int DB_VERSION = 2; // the version of the database
    public FotosDatabaseHelper(Context context){
        super(context, DB_NAME, null, DB_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db){
        updateMyDatabase(db, 0, DB_VERSION);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        updateMyDatabase(db, oldVersion, newVersion);
    }
    private void updateMyDatabase(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 1) {
            db.execSQL("CREATE TABLE FOTOS (_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "USUARIO TEXT NOT NULL, "
                    + "FOTO TEXT NOT NULL);");
        }
    }
}
