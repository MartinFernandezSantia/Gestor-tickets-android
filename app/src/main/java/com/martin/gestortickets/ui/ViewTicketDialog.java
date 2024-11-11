package com.martin.gestortickets.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.martin.gestortickets.R;

public class ViewTicketDialog extends BottomSheetDialogFragment {
    private TextView titulo;
    private TextView estado;
    private TextView creador;
    private TextView tecnico;
    private TextView descripcion;

    // Use this method to create a new instance with arguments
    public static ViewTicketDialog newInstance(String title, String status, String creator, String technician, String description) {
        ViewTicketDialog fragment = new ViewTicketDialog();
        Bundle args = new Bundle();
        args.putString("titulo", title);
        args.putString("estado", status);
        args.putString("creador", creator);
        args.putString("tecnico", technician);
        args.putString("descripcion", description);
        fragment.setArguments(args);
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.view_ticket_dialog, container, false);

        titulo = view.findViewById(R.id.titulo);
        estado = view.findViewById(R.id.estado);
        creador = view.findViewById(R.id.creador);
        tecnico = view.findViewById(R.id.tecnico);
        descripcion = view.findViewById(R.id.descripcion);

        // Retrieve the arguments
        if (getArguments() != null) {
            String title = getArguments().getString("titulo");
            String status = getArguments().getString("estado");
            String creator = getArguments().getString("creador");
            String technician = getArguments().getString("tecnico");
            String description = getArguments().getString("descripcion");

            titulo.setText(title);
            estado.setText(status);
            creador.setText(creator);
            tecnico.setText(technician);
            descripcion.setText(description);
        }

        return view;
    }
}
