package com.example.proyectofinal1.ViewHolder;

import static com.example.proyectofinal1.PantallaCarga.fechasNotificadas;
import static java.lang.Thread.sleep;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.proyectofinal1.ListarComponentes.ListarTareas;
import com.example.proyectofinal1.MenuPrincipal;
import com.example.proyectofinal1.Objetos.Tarea;
import com.example.proyectofinal1.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class ViewHolderTarea extends RecyclerView.Adapter<ViewHolderTarea.ViewHolder> {
    ArrayList<Tarea> listaTareas;
    public final MyClickListener mMyClickListener;

    public ViewHolderTarea(ArrayList<Tarea> listaTareas,MyClickListener myClickListener ) {
        this.listaTareas = listaTareas;
        mMyClickListener = myClickListener;
    }


    public interface MyClickListener {
        void onItemClick(int position);
        void onItemLongClick(int position);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = (View) LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_view_holder_tarea, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = viewHolder.getAdapterPosition();
                Log.i("ViewHolder onClick():", ""+getItem(position).getTitulo());
                if (mMyClickListener != null) mMyClickListener.onItemClick(position);
            }
        });
        viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                int position = viewHolder.getAdapterPosition();
                Log.i("ViewHolderLongClick():", ""+getItem(position).getTitulo());
                if (mMyClickListener != null) mMyClickListener.onItemLongClick(position);
                return false;
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Tarea tarea = listaTareas.get(position);
        holder.titulo.setText(tarea.getTitulo());
        holder.descripcion.setText(tarea.getDescripcion());
        holder.fecha_registro_tarea.setText(tarea.getFecha_registro());
        holder.fecha_entrega_tarea.setText(tarea.getFecha_entrega());

        //CODIGO PARA CAMBIAR EL ICONO DE LA TAREA
        String fecha_1, fecha_2;
        Date d1 = null, d2 = null;
        fecha_1 = tarea.getFecha_entrega();
        fecha_1 = fecha_1 + " " + tarea.getHora_entrega();
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
        float diff = d1.getTime() - d2.getTime();
        TimeUnit time = TimeUnit.DAYS;
        float diffrence = time.convert((long)diff, TimeUnit.MILLISECONDS);
        diffrence = (diff/ (1000*60*60*24)%365);
        Log.i("diffrence", ""+(diff/ (1000*60*60*24)%365));
        if (diffrence <= 1 && diffrence >=0) {
            holder.icono_item_tarea.setColorFilter(Color.argb(255, 255, 0, 0));
            holder.icono_item_tarea.setImageResource(R.drawable.icono_warning);
        }

    }
    private String obtenerFechaActual(){
        //String fechaActual = new SimpleDateFormat("dd/MM/yyyy hh:mm", Locale.getDefault()).format(System.currentTimeMillis());
        Date date = Calendar.getInstance().getTime();
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        String fechaActual = dateFormat.format(date);
        return fechaActual;
    }
    @Override
    public int getItemCount() {
        if (listaTareas != null) {
            return listaTareas.size();
        } else {
            return 0;
        }
    }

    public Tarea getItem(int position) {
        return listaTareas.get(position);
    }
    public class ViewHolder extends RecyclerView.ViewHolder{
        public final View view;
        public final TextView titulo, descripcion, fecha_registro_tarea, fecha_entrega_tarea;
        public final ImageView icono_item_tarea;
        public ViewHolder(View view) {
            super(view);
            this.view = view;
            titulo = view.findViewById(R.id.titulo_tarea);
            descripcion = view.findViewById(R.id.descripcion_tarea);
            fecha_registro_tarea = view.findViewById(R.id.fecha_registro_tarea);
            fecha_entrega_tarea = view.findViewById(R.id.fecha_entrega_tarea);
            icono_item_tarea = view.findViewById(R.id.icono_item_tarea);
        }

    }
}