package com.martin.gestortickets.ui.users;

import android.app.Application;

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
}