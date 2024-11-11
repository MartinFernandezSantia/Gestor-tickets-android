package com.martin.gestortickets.ui.users;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.martin.gestortickets.dao.UsuarioDAO;
import com.martin.gestortickets.entities.Usuario;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UsersViewModel extends AndroidViewModel {
    private MutableLiveData<List<Usuario>> userListLiveData;
    private UsuarioDAO usuarioDAO;
    private Map<Integer, Integer> usersIndex; // Key = user.id ; Value = index from List in MutableLiveData

    public UsersViewModel(Application application) {
        super(application);
        usuarioDAO = new UsuarioDAO(application);
        userListLiveData = new MutableLiveData<>();
        usersIndex = new HashMap<>();
        loadUsers();
    }

    public LiveData<List<Usuario>> getUsers() {
        return userListLiveData;
    }

    private void loadUsers() {
        List<Usuario> users = usuarioDAO.getAll();

        for (int i=0; i < users.size(); i++) {
            usersIndex.put(users.get(i).getId(), i);
        }

        userListLiveData.setValue(users);
    }

    public void addUser(Usuario usuario) {
        List<Usuario> currentUsers = userListLiveData.getValue();

        // Create a new list and add the user to it
        List<Usuario> updatedUsers = new ArrayList<>(currentUsers);
        updatedUsers.add(usuario);
        usersIndex.put(usuario.getId(), updatedUsers.size());

        // Set the updated list to trigger the observer
        userListLiveData.setValue(updatedUsers);
    }

    public boolean updateUserBlock(int id, boolean blocked) {
        if (!usuarioDAO.updateBloqueado(id, blocked)) return false;

        userListLiveData.getValue().get(usersIndex.get(id)).setBloqueado(blocked);
        return true;
    }

    public boolean resetPassword(int id) {
        return usuarioDAO.resetPassword(id, true);
    }
}