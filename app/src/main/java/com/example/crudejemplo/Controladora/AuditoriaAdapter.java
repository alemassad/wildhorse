package com.example.crudejemplo.Controladora;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.crudejemplo.R;

import java.util.List;
import java.util.Map;

public class AuditoriaAdapter extends RecyclerView.Adapter<AuditoriaAdapter.AuditoriaViewHolder> {

    private List<Map<String, String>> listaAuditoria;

    public AuditoriaAdapter(List<Map<String, String>> listaAuditoria) {
        this.listaAuditoria = listaAuditoria;
    }

    @NonNull
    @Override
    public AuditoriaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_auditoria, parent, false);
        return new AuditoriaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AuditoriaViewHolder holder, int position) {
        Map<String, String> auditoria = listaAuditoria.get(position);
        holder.textViewUsuario.setText("Usuario: " + auditoria.get("usuario"));
        holder.textViewAccion.setText("Acción: " + auditoria.get("accion"));
        holder.textViewFecha.setText("Fecha: " + auditoria.get("fechaHora"));
    }

    @Override
    public int getItemCount() {
        return listaAuditoria.size();
    }

    public static class AuditoriaViewHolder extends RecyclerView.ViewHolder {
        TextView textViewUsuario, textViewAccion, textViewFecha;

        public AuditoriaViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewUsuario = itemView.findViewById(R.id.textViewUsuario);
            textViewAccion = itemView.findViewById(R.id.textViewAccion);
            textViewFecha = itemView.findViewById(R.id.textViewFecha);
        }
    }
}