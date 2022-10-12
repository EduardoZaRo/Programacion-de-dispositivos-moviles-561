package com.example.proyectofinal1.BasesDeDatos;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.proyectofinal1.Objetos.Tarea;

public class TareasDatabaseHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "Tareas"; // the name of our database
    private static final int DB_VERSION = 2; // the version of the database
    public TareasDatabaseHelper(Context context){
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
            db.execSQL("CREATE TABLE TAREAS (_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "USUARIO TEXT NOT NULL, "
                    + "TITULO TEXT NOT NULL,"
                    + "DESCRIPCION TEXT NOT NULL, "
                    + "FECHA_REGISTRO TEXT NOT NULL, "
                    + "FECHA_ENTREGA TEXT NOT NULL, "
                    + "HORARIO_ENTREGA TEXT NOT NULL);");
        }
    }
}
