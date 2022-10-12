package com.example.proyectofinal1;

import static com.example.proyectofinal1.PantallaCarga.fechasNotificadas;
import static java.lang.Thread.sleep;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import android.Manifest;
import android.app.Activity;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.PersistableBundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.proyectofinal1.AgregarTarea.AgregarTarea;
import com.example.proyectofinal1.BasesDeDatos.FotosDatabaseHelper;
import com.example.proyectofinal1.BasesDeDatos.UsersDatabaseHelper;
import com.example.proyectofinal1.ListarComponentes.ListarTareas;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class MenuPrincipal extends AppCompatActivity {
    Button cerrar_sesion, agregar_tarea, listar_tareas, editar_imagen_perfil;
    TextView nombre_usuario_txt;
    ImageView imagen_perfil,icono_eliminar;
    String usuario, contrasena;
    Uri imagenUri = null;
    Thread repeatTaskThread;
    private Intent requestFileIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_principal);
        inicializarComponentes();
        obtenerDatos();

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Menu principal");
        if(savedInstanceState!=null){
            usuario = savedInstanceState.getString("usuario");
            contrasena = savedInstanceState.getString("contrasena");
            Log.i("Usuario recuperado", ""+usuario);
        }
        nombre_usuario_txt.setText(usuario);
        cerrar_sesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MenuPrincipal.this,"Cerraste sesion", Toast.LENGTH_SHORT).show();


                SQLiteOpenHelper usuariosDatabaseHelper = new UsersDatabaseHelper(MenuPrincipal.this);
                SQLiteDatabase escribir = usuariosDatabaseHelper.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put("NOMBRE", usuario);
                values.put("CONTRASENA", contrasena);
                String si = "NO";
                values.put("SESION_INICIADA", si);
                escribir.update("USUARIOS", values, "NOMBRE=?", new String[] {usuario});
                escribir.close();
                Log.i("Cerrar sesion btn ", "FUERA");


                startActivity(new Intent(MenuPrincipal.this, Login.class));
                finish();
            }
        });
        agregar_tarea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MenuPrincipal.this,  AgregarTarea.class);
                intent.putExtra("usuario", usuario);
                intent.putExtra("contrasena", contrasena);
                startActivity(intent);
            }
        });
        listar_tareas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MenuPrincipal.this,  ListarTareas.class);
                intent.putExtra("usuario", usuario);
                intent.putExtra("contrasena", contrasena);
                startActivity(intent);
            }
        });

        editar_imagen_perfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ContextCompat.checkSelfPermission(MenuPrincipal.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                    seleccionarImagenGaleria();

                }else{
                    solicitudPermisoGaleria.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                }
            }
        });
        obtenerImagenPerfil();
    }

    //Permisos para acceder a galeria
    private ActivityResultLauncher<String> solicitudPermisoGaleria = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            isGranted -> {
                if(isGranted){
                    seleccionarImagenGaleria();
                }else{
                    Toast.makeText(this, "Permiso denegado", Toast.LENGTH_SHORT).show();
                }
            });

    private void seleccionarImagenGaleria() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        galeriaActivityResultLauncher.launch(intent);

    }
    private ActivityResultLauncher<Intent> galeriaActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode() == Activity.RESULT_OK){
                        Intent data = result.getData();
                        imagenUri = data.getData();
                        Log.i("guardando foto", getFullPathFromContentUri(MenuPrincipal.this, imagenUri));
                        new guardarImagenBD().execute();
                    }else{
                        Toast.makeText(MenuPrincipal.this, "Cancelado por el usuario", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );
    public static String getFullPathFromContentUri(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if ("com.android.externalstorage.documents".equals(uri.getAuthority())) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }//non-primary e.g sd card
                else {
                    String filePath = null;
                    if (Build.VERSION.SDK_INT > 20) {
                        //getExternalMediaDirs() added in API 21
                        File extenal[] = context.getExternalMediaDirs();
                        for (File f : extenal) {
                            filePath = f.getAbsolutePath();
                            if (filePath.contains(type)) {
                                int endIndex = filePath.indexOf("Android");
                                filePath = filePath.substring(0, endIndex) + split[1];
                            }
                        }
                    }else{
                        filePath = "/storage/" + type + "/" + split[1];
                    }
                    return filePath;
                }
            }
            // DownloadsProvider
            else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                Cursor cursor = null;
                final String column = "_data";
                final String[] projection = {
                        column
                };

                try {
                    cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                            null);
                    if (cursor != null && cursor.moveToFirst()) {
                        final int column_index = cursor.getColumnIndexOrThrow(column);
                        return cursor.getString(column_index);
                    }
                } finally {
                    if (cursor != null)
                        cursor.close();
                }
                return null;
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }
    private static String getDataColumn(Context context, Uri uri, String selection,
                                        String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }
    private class guardarImagenBD extends AsyncTask<Void, Void, Boolean> {
        protected Boolean doInBackground(Void... lol) {
            SQLiteDatabase db;
            SQLiteOpenHelper fotosDatabaseHelper = new FotosDatabaseHelper(MenuPrincipal.this);
            db = fotosDatabaseHelper.getWritableDatabase();
            db.execSQL("DELETE FROM FOTOS WHERE USUARIO='"+usuario+"'");
            Cursor fila = db.rawQuery("select FOTO from FOTOS where USUARIO=?", new String[]{usuario});
            try {
                if (!fila.moveToFirst()) {
                    Log.i("guardando foto","");
                    ContentValues values = new ContentValues();
                    values.put("USUARIO", usuario);
                    values.put("FOTO", getFullPathFromContentUri(MenuPrincipal.this, imagenUri));
                    db.insert("FOTOS",null,values);
                }

            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
            db.close();
            fila.close();
            obtenerImagenPerfil();
            return true;
        }
    }
    private void obtenerImagenPerfil() {
        SQLiteDatabase db;
        SQLiteOpenHelper fotosDatabaseHelper = new FotosDatabaseHelper(MenuPrincipal.this);
        db = fotosDatabaseHelper.getReadableDatabase();
        Cursor fila = db.rawQuery("select FOTO from FOTOS where USUARIO=?", new String[]{usuario});
        try {
            if(fila.moveToFirst()){
                String foto=fila.getString(0);
                //Log.i("obtenerImagen",foto);
                File imgFile = new  File(foto);
                if(imgFile.exists()){
                    Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                    imagen_perfil.setImageBitmap(myBitmap);
                }
            }
            else{
                    Log.i("No hay fotos","lol");

            }

        } catch (Exception e) {
            Log.i("obtenerImagenPerfil", e.getMessage());
        }
        db.close();
        fila.close();
    }


    private void inicializarComponentes(){
        nombre_usuario_txt = findViewById(R.id.nombre_usuario_txt);
        cerrar_sesion = findViewById(R.id.cerrar_sesion);
        agregar_tarea = findViewById(R.id.agregar_tarea);
        listar_tareas = findViewById(R.id.listar_tareas);
        editar_imagen_perfil = findViewById(R.id.editar_imagen_perfil);
        imagen_perfil= findViewById(R.id.imagen_perfil);
    }
    private void obtenerDatos(){
        usuario = getIntent().getStringExtra("usuario");
        contrasena = getIntent().getStringExtra("contrasena");
    }


    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("usuario", usuario);
        outState.putString("contrasena", contrasena);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}