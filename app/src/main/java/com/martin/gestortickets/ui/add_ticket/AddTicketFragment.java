package com.martin.gestortickets.ui.add_ticket;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.martin.gestortickets.R;
import com.martin.gestortickets.databinding.FragmentAddTicketBinding;
import com.martin.gestortickets.databinding.FragmentAddUserBinding;
import com.martin.gestortickets.entities.Usuario;
import com.martin.gestortickets.ui.add_user.AddUserViewModel;
import com.martin.gestortickets.ui.users.UsersViewModel;

public class AddTicketFragment extends Fragment {
    private FragmentAddTicketBinding binding;
    private AddTicketViewModel addTicketViewModel;
    private Button createBtn;
    private Usuario user;

    public static AddTicketFragment newInstance() {
        return new AddTicketFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        addTicketViewModel = new ViewModelProvider(this).get(AddTicketViewModel.class);

        binding = FragmentAddTicketBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        user = (Usuario) getArguments().getSerializable("user");

        createBtn = binding.createBtn;
        createBtn.setOnClickListener(v -> createTicket());

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void createTicket() {
        String titulo = binding.titulo.getText().toString();
        String descripcion = binding.descripcion.getText().toString();

        if (titulo.isEmpty() || descripcion.isEmpty()) {
            Toast.makeText(getContext(), "Complete ambos campos si desea crear un nuevo Ticket", Toast.LENGTH_SHORT).show();
            return;
        }

        if (addTicketViewModel.createTicket(titulo, descripcion, user)) {
            Toast.makeText(getContext(), "Nuevo ticket creado con exito", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(getContext(), "No se pudo crear el ticket", Toast.LENGTH_SHORT).show();
        }

        // Implement add ticket to MutableData from ticketsTrabajadorViewModel
    }
}