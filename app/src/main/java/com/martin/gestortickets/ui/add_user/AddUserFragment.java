package com.martin.gestortickets.ui.add_user;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.martin.gestortickets.databinding.FragmentAddUserBinding;
import com.martin.gestortickets.ui.users.UsersViewModel;

public class AddUserFragment extends Fragment {

    private FragmentAddUserBinding binding;
    private Spinner rolSpinner;
    private Button createBtn;
    private AddUserViewModel addUserViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        addUserViewModel = new ViewModelProvider(this).get(AddUserViewModel.class);
        UsersViewModel usersViewModel = new ViewModelProvider(requireActivity()).get(UsersViewModel.class);

        binding = FragmentAddUserBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        rolSpinner = binding.rolSpinner;
        createBtn = binding.createBtn;

        createBtn.setOnClickListener(v -> createUser());

        // Whenever a user is created update users table
        addUserViewModel.getNewUser().observe(getViewLifecycleOwner(), usersViewModel::addUser);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void createUser() {
        int selectedRol = rolSpinner.getSelectedItemPosition() + 1;

        if (addUserViewModel.createUser(selectedRol)) {
            Toast.makeText(getContext(), "Nuevo usuario creado con exito", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(getContext(), "No se ha podido crear el usuario", Toast.LENGTH_SHORT).show();
        }
    }
}