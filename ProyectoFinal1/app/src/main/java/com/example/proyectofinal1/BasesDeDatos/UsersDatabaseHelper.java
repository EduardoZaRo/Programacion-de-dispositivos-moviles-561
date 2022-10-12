package com.example.proyectofinal1.BasesDeDatos;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;

public class UsersDatabaseHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "Usuarios"; // the name of our database
    private static final int DB_VERSION = 2; // the version of the database
    public UsersDatabaseHelper(Context context){
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
            /*
                        db.execSQL("CREATE TABLE USUARIOS (_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "NOMBRE TEXT NOT NULL, "
                    + "CONTRASENA TEXT NOT NULL);");

             */
            db.execSQL("CREATE TABLE USUARIOS (_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "NOMBRE TEXT NOT NULL, "
                    + "CONTRASENA TEXT NOT NULL, "
                    + "SESION_INICIADA TEXT NOT NULL);");
        }

    }
    private static void insertUser(SQLiteDatabase db, String nombre,
                                      String contrasena) {
        ContentValues usuariosValues = new ContentValues();
        usuariosValues.put("NOMBRE", nombre);
        usuariosValues.put("CONTRASENA", contrasena);
        db.insert("USUARIOS", null, usuariosValues);
    }



}