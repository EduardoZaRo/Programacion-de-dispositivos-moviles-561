package com.example.proyectofinal1.AgregarTarea;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.proyectofinal1.BasesDeDatos.TareasDatabaseHelper;
import com.example.proyectofinal1.BasesDeDatos.UsersDatabaseHelper;
import com.example.proyectofinal1.Login;
import com.example.proyectofinal1.Objetos.Tarea;
import com.example.proyectofinal1.R;
import com.example.proyectofinal1.Registro;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AgregarTarea extends AppCompatActivity {
    TextView fecha_actual, fecha_entrega;
    TextView horario_entrega;
    EditText titulo, descripcion;
    Button btn_calendario, btn_guardar_tarea;
    int dia, mes, year;
    TimePicker timePicker;
    Cursor fila;
    SQLiteDatabase db;
    String fecha_a;
    Button btn_horario;
    String usuario, contrasena, fecha_actual_str,titulo_str,descripcion_str,fecha_entrega_str,horario_entrega_str;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_tarea);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Agregar tarea");
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        inicializarComponentes();
        if(savedInstanceState!=null){
            usuario = savedInstanceState.getString("usuario");
            contrasena = savedInstanceState.getString("contrasena");
            titulo.setText(savedInstanceState.getString("titulo"));
            descripcion.setText(savedInstanceState.getString("descripcion"));
        }
        obtenerDatos();
        fecha_a = obtenerFechaActual();
        btn_calendario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                abrirCalendario(fecha_a);
            }
        });
        btn_guardar_tarea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                agregarTarea();
            }
        });

        btn_horario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                abrirReloj();
            }
        });
    }
    private void abrirReloj(){
        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker = new TimePickerDialog(AgregarTarea.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                if(selectedHour < 10)
                    horario_entrega.setText( "0"+selectedHour + ":" + selectedMinute);
                if(selectedMinute < 10)
                    horario_entrega.setText( ""+selectedHour + ":0" + selectedMinute);
                if(selectedHour < 10 && selectedMinute < 10)
                    horario_entrega.setText( "0"+selectedHour + ":0" + selectedMinute);
                if(selectedHour >= 10 && selectedMinute >= 10)
                    horario_entrega.setText( ""+selectedHour + ":" + selectedMinute);
            }
        }, hour, minute, true);
        mTimePicker.setTitle("Reloj");
        mTimePicker.show();
    }
    private void inicializarComponentes() {
        btn_horario = findViewById(R.id.btn_horario);
        horario_entrega = findViewById(R.id.horario_entrega);
        fecha_actual = findViewById(R.id.fecha_actual);
        fecha_entrega = findViewById(R.id.fecha_entrega);
        titulo = findViewById(R.id.titulo);
        descripcion = findViewById(R.id.descripcion);
        btn_calendario = findViewById(R.id.btn_calendario);
        btn_guardar_tarea = findViewById(R.id.btn_guardar_tarea);
    }
    private void agregarTarea() {
        fecha_actual_str = fecha_actual.getText().toString();
        titulo_str = titulo.getText().toString();
        descripcion_str = descripcion.getText().toString();
        fecha_entrega_str = fecha_entrega.getText().toString();
        horario_entrega_str = horario_entrega.getText().toString();
        Log.i("Horario entrega", horario_entrega_str);
        if(!fecha_actual_str.equals("")
                && !titulo_str.equals("")&& !descripcion_str.equals("")
                && !fecha_entrega_str.equals("")&& !fecha_actual_str.equals("")
                && !horario_entrega_str.equals("")){
            Tarea tarea = new Tarea(
                    titulo_str,
                    descripcion_str,
                    usuario,
                    fecha_actual_str,
                    fecha_entrega_str,
                    horario_entrega_str
                    );

            SQLiteDatabase db;
            TareasDatabaseHelper tareaDatabaseHelper = new TareasDatabaseHelper(this);
            db = tareaDatabaseHelper.getReadableDatabase();
            fila=db.rawQuery("select TITULO from TAREAS where TITULO=?",new String[] {titulo_str});
            try {
                if(fila.moveToFirst()){
                    String titulo_aux=fila.getString(0);
                    if (titulo_str.equals(titulo_aux)){
                        Toast.makeText(this, "Tarea ya existente", Toast.LENGTH_SHORT).show();
                    }
                }
                else {

                    ContentValues values = new ContentValues();
                    values.put("USUARIO", usuario);
                    values.put("TITULO", titulo_str);
                    values.put("DESCRIPCION", descripcion_str);
                    values.put("FECHA_REGISTRO", fecha_actual_str);
                    values.put("FECHA_ENTREGA", fecha_entrega_str);
                    values.put("HORARIO_ENTREGA", horario_entrega_str);
                    db.insert("TAREAS",null,values);
                    db.close();

                    Toast.makeText(this, "Tarea agregada exitosamente", Toast.LENGTH_SHORT).show();
                    onBackPressed();
                }
            } catch (Exception e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(this, "Llenar todos los campos", Toast.LENGTH_SHORT).show();
        }
    }

    private String obtenerFechaActual(){
        String fechaActual = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                .format(System.currentTimeMillis());
        fecha_actual.setText(fechaActual);
        return fechaActual;
    }
    private void abrirCalendario(String fecha_a){
        final Calendar calendar = Calendar.getInstance();
        dia = calendar.get(Calendar.DAY_OF_MONTH);
        mes = calendar.get(Calendar.MONTH);
        year = calendar.get(Calendar.YEAR);
        DatePickerDialog datePickerDialog =
                new DatePickerDialog(AgregarTarea.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year_sel, int mes_sel, int dia_sel) {
                        String diaFormateado, mesFormateado;
                        if(dia_sel < 10){
                            diaFormateado = "0"+String.valueOf(dia_sel);
                        }else{
                            diaFormateado = String.valueOf(dia_sel);
                        }
                        if(mes_sel < 10){
                            mesFormateado = "0"+String.valueOf(mes_sel+1);
                        }else{
                            mesFormateado = String.valueOf(mes_sel+1);
                        }
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        //Date date1 = sdf.parse("2009-12-31");
                        Date date1 = null;
                        try {
                            date1 = sdf.parse(year_sel+"-"+mesFormateado+"-"+diaFormateado);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        Date date2 = null;
                        try {
                            date2 = sdf.parse(fecha_a);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        if (date1.before(date2) || date1.equals(date2)) {
                            Toast.makeText(AgregarTarea.this, "La fecha no es válida", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            fecha_entrega.setText(diaFormateado+"/"+mesFormateado+"/"+year_sel);
                        }



                    }
                }, year, mes, dia);
        datePickerDialog.show();
    }
    private void obtenerDatos(){
        usuario = getIntent().getStringExtra("usuario");
        contrasena = getIntent().getStringExtra("contrasena");
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState, @NonNull PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putString("usuario", usuario);
        outState.putString("contrasena", contrasena);
        outState.putString("titulo", titulo_str);
        outState.putString("descripcion", descripcion_str);
    }
}