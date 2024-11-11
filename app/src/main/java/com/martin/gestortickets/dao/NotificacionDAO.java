package com.martin.gestortickets.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.text.TextUtils;
import android.util.Log;

import com.martin.gestortickets.db.DatabaseHelper;
import com.martin.gestortickets.entities.Notificacion;
import com.martin.gestortickets.entities.Usuario;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class NotificacionDAO {
    private final DatabaseHelper dbHelper;
    private SQLiteDatabase db = null;
    private Cursor cursor = null;
    private UsuarioDAO usuarioDAO;


    public NotificacionDAO(Context context) {
        dbHelper = new DatabaseHelper(context);
        usuarioDAO = new UsuarioDAO(context);
    }

    public int create(Notificacion notificacion) {
        try {
            // Verify the notification wasn't already created and still hasn't been seen
            this.db = dbHelper.getWritableDatabase();
            String sql = "SELECT * FROM notificaciones WHERE usuario_id = ? AND visto = 0 AND mensaje LIKE ?";
            this.cursor = this.db.rawQuery(sql, new String[]{
                    String.valueOf(notificacion.getRemitente().getId()),
                    notificacion.getMensaje()}
            );

            // If notificacion was indeed already created and still wasn't seen
            if (this.cursor.moveToFirst()) return -1;

            // Create new notification
            ContentValues values = new ContentValues();
            values.put("mensaje", notificacion.getMensaje());
            values.put("usuario_id", notificacion.getRemitente().getId());
            int notificacionID = (int) this.db.insertOrThrow("notificaciones", null, values);
            notificacion.setId(notificacionID);

            return (notificacionID > 0) ? 1 : 0;
        } catch (SQLException e) {
            Log.e("Error al crear notificacion", e.getMessage());
        } finally {
            dbHelper.closeResources(this.db, this.cursor);
        }
        return 0;
    }

    public boolean updateVisto(int notificacionID) {
        try {
            this.db = dbHelper.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put("visto", 1);
            int rowsAffected = this.db.update("notificaciones", values, "id = ?",
                    new String[]{String.valueOf(notificacionID)});

            return rowsAffected > 0;
        } catch (SQLException e) {
            Log.e("Error al actualizar notificacion/es", e.getMessage());
        } finally {
            dbHelper.closeResources(this.db, this.cursor);
        }
        return false;
    }

    public List<Notificacion> getAllNotSeen() {
        List<Notificacion> notificaciones = new ArrayList<>();
        try {
            this.db = dbHelper.getReadableDatabase();
            String sql = "SELECT id, mensaje, usuario_id FROM notificaciones WHERE visto = 0";
            this.cursor = this.db.rawQuery(sql, null);

            if (this.cursor == null) return notificaciones;

            while (this.cursor.moveToNext()) {
                int id = this.cursor.getInt(0);
                String mensaje = this.cursor.getString(1);
                Optional<Usuario> remitente = usuarioDAO.getByID((int) this.cursor.getInt(2));

                if (!remitente.isPresent()) continue;

                notificaciones.add(new Notificacion(id, mensaje, false, remitente.get()));
            }

        } catch (SQLException e) {
            Log.e("Error al obtener notificaciones no vistas", e.getMessage());
        } finally {
            dbHelper.closeResources(this.db, this.cursor);
        }
        return notificaciones;
    }
}
