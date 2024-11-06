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
            String sql = "SELECT * FROM notificaciones WHERE usuario_id = ? AND visto = FALSE AND mensaje LIKE ?";
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
            int notificacionID = (int) this.db.insertOrThrow("usuarios", null, values);
            notificacion.setId(notificacionID);

            return (notificacionID > 0) ? 1 : 0;
        } catch (SQLException e) {
            Log.e("Error al crear notificacion", e.getMessage());
        } finally {
            dbHelper.closeResources(this.db, this.cursor);
        }
        return 0;
    }

    boolean updateVisto(List<Integer> idList) {
        try {
            // Ensure list isn't empty
            if (idList.isEmpty()) return false;

            this.db = dbHelper.getWritableDatabase();
            StringBuilder sql = new StringBuilder("UPDATE notificaciones SET visto = 1 WHERE id IN (");

            // Create a list of placeholders
            String[] placeholders = new String[idList.size()];
            Arrays.fill(placeholders, "?");
            sql.append(TextUtils.join(",", placeholders));  // "?, ?, ?..." based on number of IDs
            sql.append(")");

            this.db.beginTransaction();

            // Prepare the statement
            SQLiteStatement statement = this.db.compileStatement(sql.toString());

            // Bind each ID parameter
            for (int i = 0; i < idList.size(); i++) {
                statement.bindLong(i + 1, idList.get(i)); // SQLite indices start at 1
            }

            // Execute the update and check if rows were affected
            int affectedRows = statement.executeUpdateDelete();

            this.db.setTransactionSuccessful();
            return affectedRows > 0;
        } catch (SQLException e) {
            Log.e("Error al actualizar notificacion/es", e.getMessage());
        } finally {
            if (this.db != null && this.db.inTransaction()) this.db.endTransaction();
            dbHelper.closeResources(this.db, this.cursor);
        }
        return false;
    }

    List<Notificacion> getAllNotSeen() {
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
