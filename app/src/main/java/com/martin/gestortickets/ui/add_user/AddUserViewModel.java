package com.martin.gestortickets.ui.add_user;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.martin.gestortickets.dao.RolDAO;
import com.martin.gestortickets.dao.UsuarioDAO;
import com.martin.gestortickets.entities.Rol;
import com.martin.gestortickets.entities.Usuario;

import java.util.Optional;

public class AddUserViewModel extends AndroidViewModel {
    private UsuarioDAO usuarioDAO;
    private RolDAO rolDAO;
    private MutableLiveData<Usuario> newUserLiveData = new MutableLiveData<>();

    public AddUserViewModel(Application application) {
        super(application);

        usuarioDAO = new UsuarioDAO(application);
        rolDAO = new RolDAO(application);
    }

    public boolean createUser(int rol) {
        Usuario newUser = new Usuario();
        Optional<Rol> newUserRol = rolDAO.getByID(rol);

        if (!newUserRol.isPresent()) return false;
        newUser.setRol(newUserRol.get());

        boolean created = usuarioDAO.create(newUser);
        if (created) newUserLiveData.setValue(newUser);

        return created;
    }

    public LiveData<Usuario> getNewUser() {
        return newUserLiveData;
    }
}