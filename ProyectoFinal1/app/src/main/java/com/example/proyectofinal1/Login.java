package com.example.proyectofinal1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.proyectofinal1.BasesDeDatos.UsersDatabaseHelper;

public class Login extends AppCompatActivity {
    EditText nombre_login, pass_login;
    Button btn_login;
    TextView usuario_nuevo;
    String nombre = "", contrasena = "";
    Cursor fila, sesion_iniciada;
    SQLiteDatabase db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        inicializarComponentes();
        if(savedInstanceState!=null){
            nombre = savedInstanceState.getString("nombre");
            contrasena = savedInstanceState.getString("contrasena");
            nombre_login.setText(nombre);
        }
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Login");

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validarDatos();
            }
        });
        usuario_nuevo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Login.this, Registro.class));
                finish();
            }
        });
    }
    private void validarDatos() {
        nombre = nombre_login.getText().toString();
        contrasena = pass_login.getText().toString();
        if(TextUtils.isEmpty(contrasena)){
            Toast.makeText(this,"Ingrese contraseña", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(nombre)){
            Toast.makeText(this,"Ingrese su nombre", Toast.LENGTH_SHORT).show();
        }
        else{
            loginUsuario();
        }
    }

    private void loginUsuario() {

        SQLiteOpenHelper usuariosDatabaseHelper = new UsersDatabaseHelper(this);

        db = usuariosDatabaseHelper.getReadableDatabase();
        fila=db.rawQuery("select NOMBRE,CONTRASENA from USUARIOS where NOMBRE='"+
                nombre+"' and CONTRASENA='"+contrasena+"'",null);
        try {
            if(fila.moveToFirst()){
                String usua=fila.getString(0);
                String pass=fila.getString(1);
                if (nombre.equals(usua)&&contrasena.equals(pass)){

                    SQLiteDatabase escribir = usuariosDatabaseHelper.getWritableDatabase();
                    ContentValues values = new ContentValues();
                    values.put("SESION_INICIADA", "SI");
                    escribir.update("USUARIOS", values, "NOMBRE=?", new String[] {nombre});
                    escribir.close();


                    Intent intent = new Intent(Login.this,  MenuPrincipal.class);
                    intent.putExtra("usuario", nombre);
                    intent.putExtra("contrasena", contrasena);
                    startActivity(intent);
                    finish();
                }
            }
            else {
                Toast.makeText(this, "Usuario o contraseña son incorrectos", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        db.close();
        fila.close();
    }

    private void inicializarComponentes(){
        nombre_login = findViewById(R.id.nombre_login);
        pass_login = findViewById(R.id.pass_login);
        btn_login = findViewById(R.id.btn_login);
        usuario_nuevo = findViewById(R.id.usuario_nuevo);
    }
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState, @NonNull PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putString("nombre", nombre_login.getText().toString());
        outState.putString("contrasena", pass_login.getText().toString());
    }

}