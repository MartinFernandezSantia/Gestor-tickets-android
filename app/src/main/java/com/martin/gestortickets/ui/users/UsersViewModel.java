package com.martin.gestortickets.ui.users;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.martin.gestortickets.dao.UsuarioDAO;
import com.martin.gestortickets.entities.Usuario;

import java.util.ArrayList;
import java.util.List;

public class UsersViewModel extends AndroidViewModel {
    private MutableLiveData<List<Usuario>> userListLiveData;
    private UsuarioDAO usuarioDAO;

    public UsersViewModel(Application application) {
        super(application);
        usuarioDAO = new UsuarioDAO(application);
        userListLiveData = new MutableLiveData<>();
        loadUsers();
    }

    public LiveData<List<Usuario>> getUsers() {
        return userListLiveData;
    }

    private void loadUsers() {
        List<Usuario> users = usuarioDAO.getAll();

        userListLiveData.setValue(users);
    }

    public void addUser(Usuario usuario) {
        List<Usuario> currentUsers = userListLiveData.getValue();

        // Create a new list and add the user to it
        List<Usuario> updatedUsers = new ArrayList<>(currentUsers);
        updatedUsers.add(usuario);

        // Set the updated list to trigger the observer
        userListLiveData.setValue(updatedUsers);
    }

}