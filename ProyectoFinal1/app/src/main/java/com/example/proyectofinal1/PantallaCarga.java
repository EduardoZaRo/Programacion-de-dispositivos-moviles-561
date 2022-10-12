package com.example.proyectofinal1;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.work.BackoffPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.proyectofinal1.BasesDeDatos.FotosDatabaseHelper;
import com.example.proyectofinal1.BasesDeDatos.UsersDatabaseHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.TimeUnit;

public class PantallaCarga extends AppCompatActivity {
    public static final String MESSAGE_STATUS = "message_status";
    public static final ArrayList<String> fechasNotificadas = new ArrayList<String>();
    public static boolean iniciado = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_carga);
        int tiempo = 3000;
        if(savedInstanceState!=null){
            iniciado = savedInstanceState.getBoolean("iniciado");
        }
        if(!isMyServiceRunning(FechaServicio.class)){
            iniciado = true;

            new notificacionesTareas().execute();

        }
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run(){
                /*
                startActivity(new Intent(PantallaCarga.this, Login.class));
                finish();
                */
                 new buscarSesionIniciada().execute();
            }
        },tiempo);
    }

    /**
     * @param serviceClass
     * @return True if service is running
     */
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("iniciado", iniciado);
        String fechaStr = fechasNotificadas.toString().substring(1);
        fechaStr = fechaStr.substring(0, fechaStr.length() - 1);
        outState.putString("fechas", fechaStr);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    private class notificacionesTareas extends AsyncTask<Void, Void, Boolean>{
        protected Boolean doInBackground(Void... lol) {
            Intent intent = new Intent(PantallaCarga.this, FechaServicio.class);
            intent.putExtra(FechaServicio.EXTRA_MESSAGE,"Holi");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(intent);
            } else {
                startService(intent);
            }
            return true;
        }

    }




    private class buscarSesionIniciada extends AsyncTask<Void, Void, Boolean>{

        protected Boolean doInBackground(Void... lol) {
            SQLiteDatabase db;
            SQLiteOpenHelper usuariosDatabaseHelper = new UsersDatabaseHelper(PantallaCarga.this);
            db = usuariosDatabaseHelper.getReadableDatabase();
            Cursor fila=db.rawQuery("select NOMBRE, CONTRASENA, SESION_INICIADA from USUARIOS where SESION_INICIADA=?",new String[] {"SI"});
            try {
                if(fila.moveToFirst()){
                    String sesion=fila.getString(2);
                        Log.i("SESION INICIADA", sesion);
                        Intent intent = new Intent(PantallaCarga.this,  MenuPrincipal.class);
                        intent.putExtra("usuario", fila.getString(0));
                        intent.putExtra("contrasena", fila.getString(1));
                        startActivity(intent);
                        finish();


                }
                else{
                    //Ir al login
                    startActivity(new Intent(PantallaCarga.this, Login.class));
                    finish();
                }

            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
            return true;
        }
    }
}