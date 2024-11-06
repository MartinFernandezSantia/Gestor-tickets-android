package com.martin.gestortickets.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.martin.gestortickets.db.DatabaseHelper;
import com.martin.gestortickets.entities.Rol;
import com.martin.gestortickets.entities.Usuario;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UsuarioDAO {
    private final DatabaseHelper dbHelper;
    private SQLiteDatabase db = null;
    private Cursor cursor = null;
    private RolDAO rolDAO;

    public UsuarioDAO(Context context) {
        dbHelper = new DatabaseHelper(context);
        rolDAO = new RolDAO(context);
    }

    boolean create(Usuario usuario) {
        try {
            // Begin transaction
            this.db = dbHelper.getWritableDatabase();
            this.db.beginTransaction();

            // Insert new user
            ContentValues newUser = new ContentValues();
            newUser.put("contraseña", usuario.getPassword());
            newUser.put("rol_id", usuario.getRol().getId());
            int newUserID = (int) this.db.insertOrThrow("usuarios", null, newUser);
            usuario.setId(newUserID);
            usuario.setBloqueado(false);

            // Insert marcas and fallas if user is "Tecnico"
            if (usuario.getRol().getId() == 2) {
                ContentValues marcasYfallas = new ContentValues();
                marcasYfallas.put("usuario_id", newUserID);
                this.db.insertOrThrow("fallas_y_marcas", null, marcasYfallas);
                usuario.setFallas(0);
                usuario.setMarcas(0);
            }

            // Finish transaction
            this.db.setTransactionSuccessful();

            // Set password = id
            resetPassword(newUserID);

            return true;
        } catch (SQLException e) {
            Log.e("Error al crear usuario", e.getMessage());
        } finally {
            if (this.db != null) this.db.endTransaction();
            dbHelper.closeResources(this.db, this.cursor);
        }
        return false;
    }

    boolean resetPassword(int id) {
        try {
            this.db = dbHelper.getWritableDatabase();

            // Update password to equal user's ID
            ContentValues values = new ContentValues();
            values.put("contraseña", String.valueOf(id));
            int rowsAffected = this.db.update("usuarios", values, "id = ?",
                    new String[]{String.valueOf(id)});

            return rowsAffected > 0;
        } catch (SQLException e) {
            Log.e("Error al blanquear contraseña del usuario", e.getMessage());
        } finally {
            dbHelper.closeResources(this.db, this.cursor);
        }
        return false;
    }

    boolean updateBloqueado(int id, boolean bloqueado) {
        try {
            this.db = dbHelper.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put("bloqueado", bloqueado);
            int rowsAffected = this.db.update("usuarios", values, "id = ?",
                    new String[]{String.valueOf(id)});

            return rowsAffected > 0;
        } catch (SQLException e) {
            Log.e("Error al modificar bloqueo de usuario", e.getMessage());
        } finally {
            dbHelper.closeResources(this.db, this.cursor);
        }
        return false;
    }

    boolean updatePassword(int id, String currPass, String newPass) {
        try {
            this.db = dbHelper.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put("contraseña", newPass);
            int rowsAffected = this.db.update("usuarios", values, "id = ? AND contraseña = ?",
                    new String[]{String.valueOf(id), currPass});

            return rowsAffected > 0;
        } catch (SQLException e) {
            Log.e("Error al modificar la contraseña del usuario", e.getMessage());
        } finally {
            dbHelper.closeResources(this.db, this.cursor);
        }
        return false;
    }

    boolean updateMarcasYFallas(Usuario tecnico) {
        try {
            this.db = dbHelper.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put("marcas", tecnico.getMarcas());
            values.put("fallas", tecnico.getFallas());
            int rowsAffected = this.db.update("fallas_y_marcas", values, "usuario_id = ?",
                    new String[]{String.valueOf(tecnico.getId())});

            return rowsAffected > 0;
        } catch (SQLException e) {
            Log.e("Error al actualizar las marcas y fallas del tecnico", e.getMessage());
        } finally {
            dbHelper.closeResources(this.db, this.cursor);
        }
        return false;
    }

//    Integer takenTickets(int tecnicoID) {
//        try {
//            this.db = dbHelper.getReadableDatabase();
//            String sql = "SELECT COUNT(*) FROM (SELECT MAX(id) AS latest_id FROM asignaciones GROUP BY ticket_id) AS latest_assignments JOIN asignaciones AS a ON a.id = latest_assignments.latest_id WHERE a.usuario_id = ? AND a.estado_id = 1";
//            this.cursor = this.db.rawQuery(sql, new String[]{String.valueOf(tecnicoID)});
//
//            if (this.cursor.moveToFirst()) {
//                return this.cursor.getInt(0);
//            }
//        } catch (SQLException e) {
//            Log.e("Error al actualizar las marcas y fallas del tecnico", e.getMessage());
//        } finally {
//            dbHelper.closeResources(this.db, this.cursor);
//        }
//        return -1;
//    }

    Optional<Usuario> getByID(int id) {
        Optional<Usuario> usuario = Optional.empty();
        try {
            this.db = dbHelper.getReadableDatabase();

            String sql = "SELECT usuarios.id, usuarios.rol_id, usuarios.bloqueado, "
                    + "fallas_y_marcas.num_fallas, fallas_y_marcas.num_marcas "
                    + "FROM usuarios "
                    + "LEFT JOIN fallas_y_marcas ON usuarios.id = fallas_y_marcas.usuario_id "
                    + "WHERE usuarios.id = ?";
            this.cursor = db.rawQuery(sql, new String[]{String.valueOf(id)});

            if (this.cursor != null && this.cursor.moveToFirst()) {
                int usuarioID = this.cursor.getInt(0);
                Rol rol = rolDAO.getByID(this.cursor.getInt(1)).get();
                boolean bloqueado = (this.cursor.getInt(2) == 1);
                int fallas = this.cursor.getInt(3);
                int marcas = this.cursor.getInt(4);

                usuario = Optional.of(new Usuario(usuarioID, rol, bloqueado, fallas, marcas));
            }
        } catch (SQLException e) {
            Log.e("Error al obtener usuario por ID", e.getMessage());
        } finally {
            dbHelper.closeResources(db, cursor);
        }
        return usuario;
    }

    List<Usuario> getAll() {
        List<Usuario> usuarios = new ArrayList<>();
        try {
            this.db = dbHelper.getReadableDatabase();
            String sql = "SELECT usuarios.id, usuarios.rol_id, usuarios.bloqueado, "
                    + "fallas_y_marcas.num_fallas, fallas_y_marcas.num_marcas "
                    + "FROM usuarios "
                    + "LEFT JOIN fallas_y_marcas ON usuarios.id = fallas_y_marcas.usuario_id";
            this.cursor = db.rawQuery(sql, null);

            if (cursor == null) return usuarios;

            while (cursor.moveToNext()) {
                int usuarioID = this.cursor.getInt(0);
                Rol rol = rolDAO.getByID(this.cursor.getInt(1)).get();
                boolean bloqueado = (this.cursor.getInt(2) == 1);
                int fallas = this.cursor.getInt(3);
                int marcas = this.cursor.getInt(4);

                usuarios.add(new Usuario(usuarioID, rol, bloqueado, fallas, marcas));
            }
        } catch (SQLException e) {
            Log.e("Error al obtener usuarios", e.getMessage());
        } finally {
            dbHelper.closeResources(this.db, this.cursor);
        }
        return usuarios;
    }
}
