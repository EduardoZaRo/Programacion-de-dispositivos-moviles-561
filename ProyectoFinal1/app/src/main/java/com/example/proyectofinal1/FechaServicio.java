package com.example.proyectofinal1;

import static com.example.proyectofinal1.PantallaCarga.fechasNotificadas;
import static java.lang.Thread.sleep;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.proyectofinal1.AgregarTarea.AgregarTarea;
import com.example.proyectofinal1.BasesDeDatos.TareasDatabaseHelper;
import com.example.proyectofinal1.BasesDeDatos.UsersDatabaseHelper;
import com.example.proyectofinal1.ListarComponentes.ListarTareas;
public class FechaServicio extends Service {


    public static final String EXTRA_MESSAGE = "message";
    public int NOTIFICATION_ID = 0;
    private final IBinder binder = new FechaBinder();

    public class FechaBinder extends Binder{
        FechaServicio getFecha(){
            return FechaServicio.this;
        }
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    private Thread repeatTaskThread;
    @Override
    public void onCreate() {
        super.onCreate();
        repeatTaskThread = new Thread()
        {
            public void run()
            {
                while (true)
                {
                    comprobarFechas(":)");
                    try {
                        sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            };
        };
        repeatTaskThread.start();

    }



    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("Irvin", "Irvin", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }
        NotificationCompat.Builder notification = new NotificationCompat.Builder(getApplicationContext(), "Irvin")
                .setContentTitle("Agenda de tareas")
                .setContentText("Checando fechas...")
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.icono_notificacion);

        startForeground(2001,notification.build());
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
    private void comprobarFechas(String text) {
        SharedPreferences prefe=getSharedPreferences("datos",MODE_PRIVATE);
        String fechaStr0 = prefe.getString("fechas","");
        fechaStr0 = fechaStr0.replace(" ","");


        
        if(fechaStr0.length() > 0){
            ArrayList<String> myList = new ArrayList<String>(Arrays.asList(fechaStr0.split(",")));
            fechasNotificadas.clear();
            for(String stringValue : myList) {
                try {
                    //Convert String to Integer, and store it into integer array list.
                    fechasNotificadas.add(stringValue);
                } catch(NumberFormatException nfe) {
                    //System.out.println("Could not parse " + nfe);
                    Log.w("NumberFormat", "Parsing failed! " + stringValue + " can not be an integer");
                }
            }
        }

        String fecha_1, fecha_2;
        Date d1 = null, d2 = null;

        int i = 0, alarma;
        SQLiteDatabase db;
        TareasDatabaseHelper tareaDatabaseHelper = new TareasDatabaseHelper(getApplicationContext());
        db = tareaDatabaseHelper.getReadableDatabase();
        Cursor fila=db.rawQuery("select FECHA_ENTREGA, TITULO, USUARIO, HORARIO_ENTREGA from TAREAS",null);
        int success = 0;


        Set<String> hashSet = new HashSet<String>(fechasNotificadas);
        fechasNotificadas.clear();
        fechasNotificadas.addAll(hashSet);


        try {
            fila.moveToFirst();
            do{
                fecha_1=fila.getString(0);
                fecha_1 = fecha_1 + " " + fila.getString(3);
                fecha_2 = obtenerFechaActual();
                try {
                    d1 = new SimpleDateFormat("dd/MM/yyyy hh:mm").parse(fecha_1);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                try {
                    d2 = new SimpleDateFormat("dd/MM/yyyy hh:mm").parse(fecha_2);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Log.i("fecha_1", fecha_1);
                Log.i("fecha_2", fecha_2);
                float diff = d1.getTime() - d2.getTime();
                TimeUnit time = TimeUnit.DAYS;
                float diffrence = time.convert((long)diff, TimeUnit.MILLISECONDS);
                diffrence = (diff/ (1000*60*60*24)%365);
                Log.i("diffrence", ""+(diff/ (1000*60*60*24)%365));

                if (diffrence <= 1 && diffrence >=0) {

                    if(fechasNotificadas.contains(fila.getString(1).replace(" ","")) == false){
                        displayNotification(fila.getString(2));
                        sleep(1000);
                        fechasNotificadas.add(fila.getString(1));
                    }
                }

                SharedPreferences sharedPref= getSharedPreferences("datos", MODE_PRIVATE);
                SharedPreferences.Editor editor= sharedPref.edit();
                String fechaStr = fechasNotificadas.toString().substring(1);
                fechaStr = fechaStr.substring(0, fechaStr.length() - 1);
                editor.putString("fechas", fechaStr);
                editor.commit();
                i++;
            }while (fila.moveToNext());
        } catch (Exception e) {
            Log.i("ERROR ", e.getMessage());
        }
        db.close();
        fila.close();



    }
    private String obtenerFechaActual(){
        //String fechaActual = new SimpleDateFormat("dd/MM/yyyy hh:mm", Locale.getDefault()).format(System.currentTimeMillis());
        Date date = Calendar.getInstance().getTime();
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        String fechaActual = dateFormat.format(date);
        return fechaActual;
    }
    private void displayNotification(String usuario) {
        Log.i("Notificacion", "");
        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("Irvin", "Irvin", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }
        Intent intent = new Intent(this, PantallaCarga.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(PantallaCarga.class);
        stackBuilder.addNextIntent(intent);
        PendingIntent pendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT
                );
        NotificationCompat.Builder notification = new NotificationCompat.Builder(getApplicationContext(), "Irvin")
                .setContentTitle("Tarea en menos de 1 día!")
                .setContentText(usuario+" tiene una tarea en menos de 1 dia!")
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.icono_notificacion);

        notificationManager.notify(NOTIFICATION_ID, notification.build());
        NOTIFICATION_ID++;
    }


}
