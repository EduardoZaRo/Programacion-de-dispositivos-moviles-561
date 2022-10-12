package com.example.proyectofinal1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.proyectofinal1.BasesDeDatos.UsersDatabaseHelper;

public class Registro extends AppCompatActivity {
    EditText nombre_et, correo_et, contrasena_et, confirmar_contrasena_et;
    Button registrar_usuario;
    TextView tengo_una_cuenta;
    String nombre =" ", password = " ", confirmarpassword = " ";
    Cursor fila;
    SQLiteDatabase db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);
        inicializarComponentes();
        registrar_usuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validarDatos();
            }
        });
        tengo_una_cuenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Registro.this, Login.class));
                finish();
            }
        });
    }

    private void validarDatos() {
        nombre = nombre_et.getText().toString();
        password = contrasena_et.getText().toString();
        confirmarpassword = confirmar_contrasena_et.getText().toString();


        if(TextUtils.isEmpty(nombre)){
            Toast.makeText(this,"Ingrese nombre", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(password)){
            Toast.makeText(this,"Ingrese contraseña", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(confirmarpassword)){
            Toast.makeText(this,"Confirme contraseña", Toast.LENGTH_SHORT).show();
        }
        else if(!password.equals(confirmarpassword)){
            Toast.makeText(this,"No coinciden las contraseña :(", Toast.LENGTH_SHORT).show();
        }
        else if(password.length() < 6){
            Toast.makeText(this,"La contraseña debe ser de minimo 6 caracteres", Toast.LENGTH_SHORT).show();
        }
        else{
            crearCuenta(); //Todos los campos son validos
        }
    }

    private void crearCuenta() {
        SQLiteOpenHelper usuariosDatabaseHelper = new UsersDatabaseHelper(this);
        db = usuariosDatabaseHelper.getReadableDatabase();
        fila=db.rawQuery("select NOMBRE from USUARIOS where NOMBRE=?",new String[] {nombre});
        try {
            if(fila.moveToFirst()){
                String usua=fila.getString(0);
                if (nombre.equals(usua)){
                    Toast.makeText(this, "Usuario ya existente", Toast.LENGTH_SHORT).show();
                }
            }
            else {
                /*
                nombre = nombre_et.getText().toString();
                password = contrasena_et.getText().toString();
                ContentValues values = new ContentValues();
                values.put("NOMBRE", nombre);
                values.put("CONTRASENA", password);
                db.insert("USUARIOS",null,values);
                db.close();
                Toast.makeText(this, "Usuario registrado!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(Registro.this, Login.class));
                finish();
                */
                nombre = nombre_et.getText().toString();
                password = contrasena_et.getText().toString();
                ContentValues values = new ContentValues();
                values.put("NOMBRE", nombre);
                values.put("CONTRASENA", password);
                values.put("SESION_INICIADA", "NO");
                db.insert("USUARIOS",null,values);
                db.close();
                Toast.makeText(this, "Usuario registrado!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(Registro.this, Login.class));
                finish();
            }
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }


    }

    private void inicializarComponentes(){
        nombre_et = findViewById(R.id.nombre_et);
        contrasena_et = findViewById(R.id.contrasena_et);
        confirmar_contrasena_et = findViewById(R.id.confirmar_contrasena_et);
        registrar_usuario = findViewById(R.id.registrar_usuario);
        tengo_una_cuenta = findViewById(R.id.tengo_una_cuenta);
    }
    @Override
    public void onDestroy(){
        super.onDestroy();

        if(null !=db){
            db.close();
            fila.close();
        }
    }
}