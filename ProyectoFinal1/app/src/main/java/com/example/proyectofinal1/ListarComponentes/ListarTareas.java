package com.example.proyectofinal1.ListarComponentes;

import static com.example.proyectofinal1.PantallaCarga.fechasNotificadas;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.proyectofinal1.BasesDeDatos.TareasDatabaseHelper;
import com.example.proyectofinal1.FechaServicio;
import com.example.proyectofinal1.Objetos.Tarea;
import com.example.proyectofinal1.PantallaCarga;
import com.example.proyectofinal1.R;
import com.example.proyectofinal1.ViewHolder.ViewHolderTarea;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class ListarTareas extends AppCompatActivity{
    Cursor fila;
    SQLiteDatabase db;
    /*
    RecyclerView recyclerview_tareas;
    RecyclerView.LayoutManager linearLayoutManager;
    */
    Parcelable mListState;
    String usuario, contrasena;
    Dialog dialog;
    Button btn_eliminar_tarea, btn_compartir_tarea;
    RecyclerView.LayoutManager mLayoutManager;
    RecyclerView listaTareas;
    RecyclerView.Adapter adapter;
    boolean mantener_dialog = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listar_tareas);
        if(savedInstanceState!=null){
            Log.i("onsavedinstance","");
            usuario = savedInstanceState.getString("usuario");
            contrasena = savedInstanceState.getString("contrasena");
            mantener_dialog = true;

        }
        else{
            Log.i("notsavedinstace","");
            obtenerDatos();
        }
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Lista de tareas");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        dialog = new Dialog(ListarTareas.this);

        listarTareas();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void listarTareas(){
        TareasDatabaseHelper tareasDatabaseHelper = new TareasDatabaseHelper(this);
        db = tareasDatabaseHelper.getReadableDatabase();
        Log.i("listarTareas usuario",""+usuario);
        fila=db.rawQuery("select USUARIO,TITULO,DESCRIPCION, FECHA_REGISTRO, FECHA_ENTREGA,HORARIO_ENTREGA " +
                "from TAREAS where USUARIO=?",new String[] {usuario});
        if (!fila.moveToFirst()) {
            fila.close();
            db.close();

            setContentView(R.layout.no_tareas);
            return;
        }
        ArrayList<Tarea> tareas = new ArrayList<>();
        do {
            String titulo = fila.getString(0);
            Tarea tarea = new Tarea(fila.getString(1),
                    fila.getString(2),
                    fila.getString(0),
                    fila.getString(3),
                    fila.getString(4),
                    fila.getString(5));
            tareas.add(tarea);
        } while (fila.moveToNext());
        fila.close();
        db.close();
        this.listaTareas = (RecyclerView) findViewById(R.id.recyclerview_tareas);


        //PARA CAMBIAR LA VISUALIZACION DE TAREAS ENTRE TABLET Y SMARTPHONE
        Configuration config = getResources().getConfiguration();
        if (config.smallestScreenWidthDp >= 600) {
            mLayoutManager = new GridLayoutManager(ListarTareas.this, 2);
        } else {
            mLayoutManager = new LinearLayoutManager(ListarTareas.this, LinearLayoutManager.VERTICAL,false);
        }
        //mLayoutManager = new LinearLayoutManager(ListarTareas.this, LinearLayoutManager.VERTICAL,false);
        this.listaTareas.setLayoutManager(mLayoutManager);
        final ViewHolderTarea.MyClickListener mcl = new ViewHolderTarea.MyClickListener() {
            @Override
            public void onItemClick(int position) {
            }
            @Override
            public void onItemLongClick(int position) {
            }
        };
        //CODIGO NECESARIO PARA HACER FUNCIONAR CLICK Y LONGCLICK DEL RECYCLER VIEW
        adapter = new ViewHolderTarea(tareas, mcl) {
            @NonNull
            @Override
            public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = (View) LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_view_holder_tarea, parent, false);
                ViewHolder viewHolder = new ViewHolder(v);
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = viewHolder.getAdapterPosition();
                        dialog.setContentView(R.layout.dialogo_masinfotarea);
                        TextView descripcion = dialog.findViewById(R.id.masinfo_tarea);
                        descripcion.setText(getItem(position).getDescripcion());



                        dialog.show();


                        if (mMyClickListener != null) mMyClickListener.onItemClick(position);
                    }
                });
                viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        int position = viewHolder.getAdapterPosition();
                        Log.i("ViewHolderLongClick():", ""+getItem(position).getTitulo());

                        dialog.setContentView(R.layout.dialogo_opciones_tarea);
                        btn_eliminar_tarea = dialog.findViewById(R.id.btn_eliminar_tarea);
                        btn_compartir_tarea = dialog.findViewById(R.id.btn_compartir_tarea);
                        btn_eliminar_tarea.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                //eliminarTarea(getItem(position).getTitulo());
                                new eliminarAsync().execute(getItem(position).getTitulo());
                                dialog.dismiss();
                                onBackPressed();
                            }
                        });
                        btn_compartir_tarea.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                //eliminarTarea(getItem(position).getTitulo());
                                Intent intent = new Intent(Intent.ACTION_SEND);
                                intent.setType("text/plain");
                                String mensaje = "Hola!\n"+usuario+" te comparte la siguiente tarea:\n"
                                                    +getItem(position).getTitulo()+"\n"
                                                    +getItem(position).getDescripcion()
                                                    +"\nEsta actividad vence el "
                                                    +getItem(position).getFecha_entrega();
                                intent.putExtra(Intent.EXTRA_TEXT, mensaje);
                                Intent chosenIntent = Intent.createChooser(intent,"Compartir");
                                startActivity(chosenIntent);
                                dialog.dismiss();
                            }
                        });


                        dialog.show();


                        if (mMyClickListener != null) mMyClickListener.onItemLongClick(position);
                        return false;
                    }
                });
                return viewHolder;
            }

        };

        listaTareas.setAdapter(adapter);

    }
    private String obtenerFechaActual(){
        //String fechaActual = new SimpleDateFormat("dd/MM/yyyy hh:mm", Locale.getDefault()).format(System.currentTimeMillis());
        Date date = Calendar.getInstance().getTime();
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        String fechaActual = dateFormat.format(date);
        return fechaActual;
    }
    private class eliminarAsync extends AsyncTask<String, Void, Boolean> {

        protected Boolean doInBackground(String... titulo) {
            Log.i("TITULO", titulo[0]);
            //Eliminar nota en bdd
            eliminarTarea(titulo[0]);
            return true;
        }


    }
    private void eliminarTarea(String titulo) {
        //Eliminar nota en bdd
        SQLiteOpenHelper tareasDatabaseHelper = new TareasDatabaseHelper(getApplicationContext());
        SQLiteDatabase tarea_eliminar = tareasDatabaseHelper.getReadableDatabase();
        SQLiteDatabase escribir = tareasDatabaseHelper.getWritableDatabase();
        Cursor tarea=tarea_eliminar.rawQuery("select TITULO from TAREAS where TITULO=?",new String[] {titulo});
        Log.i("DENTRO DE ELIMINARTAREA",titulo);
        try {
            if(tarea.moveToFirst()){
                String titulo_bdd=tarea.getString(0);
                if (titulo_bdd.equals(titulo)){
                    fechasNotificadas.remove(titulo);
                    SharedPreferences sharedPref= getSharedPreferences("datos", MODE_PRIVATE);
                    SharedPreferences.Editor editor= sharedPref.edit();
                    String fechaStr = fechasNotificadas.toString().substring(1);
                    fechaStr = fechaStr.substring(0, fechaStr.length() - 1);
                    editor.putString("fechas", fechaStr);
                    editor.commit();
                    escribir.delete("TAREAS", "TITULO = ?", new String[]{titulo});



                    Log.i("Eliminando elemento " + titulo,fechasNotificadas.toString());
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(ListarTareas.this, "Tarea eliminada!", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
            else {
                Toast.makeText(ListarTareas.this, "No se pudo eliminar", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(ListarTareas.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        tarea_eliminar.close();
        escribir.close();
        tarea.close();
    }
    private void obtenerDatos(){
        usuario = getIntent().getStringExtra("usuario");
        contrasena = getIntent().getStringExtra("contrasena");
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.i("salvando datos!","");
        outState.putString("usuario", usuario);
        outState.putString("contrasena", contrasena);
        outState.putBoolean("dialog", mantener_dialog);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}
