package com.martin.gestortickets.utils;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.martin.gestortickets.dao.RolDAO;
import com.martin.gestortickets.db.DatabaseHelper;
import com.martin.gestortickets.entities.Rol;
import com.martin.gestortickets.entities.Usuario;

import java.util.Optional;

public class Auth {
    public static Optional<Usuario> login(Context context, int id, String passw) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = null;
        Cursor cursor = null;
        Optional<Usuario> usuario = Optional.empty();
        try {
            String sql = "SELECT usuarios.id, usuarios.rol_id, usuarios.bloqueado, "
                    + "fallas_y_marcas.num_fallas, fallas_y_marcas.num_marcas "
                    + "FROM usuarios "
                    + "LEFT JOIN fallas_y_marcas ON usuarios.id = fallas_y_marcas.usuario_id "
                    + "WHERE usuarios.id = ? AND usuarios.contraseña = ?";
            db = dbHelper.getReadableDatabase();
            cursor = db.rawQuery(sql, new String[]{String.valueOf(id), passw});

            if (cursor != null && cursor.moveToFirst()) {
                RolDAO rolDAO = new RolDAO(context);

                int usuarioID = cursor.getInt(0);
                Rol rol = rolDAO.getByID(cursor.getInt(1)).get();
                boolean bloqueado = (cursor.getInt(2) == 1);
                Integer fallas = null;
                Integer marcas = null;

                if (rol.getId() == 2) {
                    fallas = cursor.getInt(3);
                    marcas = cursor.getInt(4);
                }

                usuario = Optional.of(new Usuario(usuarioID, rol, bloqueado, fallas, marcas));
            }
        } catch (SQLException e) {
            Log.e("Error al obtener usuario", e.getMessage());
        } finally {
            dbHelper.closeResources(db, cursor);
        }
        return usuario;
    }
}
