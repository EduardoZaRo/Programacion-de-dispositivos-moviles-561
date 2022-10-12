package com.example.proyectofinal1.Objetos;

public class Tarea {
    String titulo, descripcion, usuario, fecha_registro, fecha_entrega, hora_entrega;
/*titulo,
descripcion,
usuario,
fecha_registro,
fecha_entrega*/
    public Tarea(String titulo,
                 String descripcion,
                 String usuario,
                 String fecha_registro,
                 String fecha_entrega,
                 String hora_entrega) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.usuario = usuario;
        this.fecha_registro = fecha_registro;
        this.fecha_entrega = fecha_entrega;
        this.hora_entrega = hora_entrega;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getFecha_registro() {
        return fecha_registro;
    }

    public void setFecha_registro(String fecha_registro) {
        this.fecha_registro = fecha_registro;
    }

    public String getFecha_entrega() {
        return fecha_entrega;
    }

    public void setFecha_entrega(String fecha_entrega) {
        this.fecha_entrega = fecha_entrega;
    }

    public String getHora_entrega() {
        return hora_entrega;
    }

    public void setHora_entrega(String hora_entrega) {
        this.hora_entrega = hora_entrega;
    }
}
