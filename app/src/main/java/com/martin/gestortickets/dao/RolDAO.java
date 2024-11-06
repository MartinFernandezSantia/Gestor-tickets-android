package com.martin.gestortickets.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import com.martin.gestortickets.db.DatabaseHelper;
import com.martin.gestortickets.entities.Rol;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RolDAO {
    private final DatabaseHelper dbHelper;
    private SQLiteDatabase db = null;
    private Cursor cursor = null;

    public RolDAO(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public Optional<Rol> getByID(int id) {
        Optional<Rol> rol = Optional.empty();
        try {
            this.db = dbHelper.getReadableDatabase();

            String sql = "SELECT * FROM roles WHERE id = ?";
            this.cursor = db.rawQuery(sql, new String[]{String.valueOf(id)});

            if (cursor != null && cursor.moveToFirst()) {
                int rolID = cursor.getInt(0);
                String rolName = cursor.getString(1);
                rol = Optional.of(new Rol(rolID, rolName));
            }
        } catch (SQLException e) {
            Log.e("Error al obtener rol por ID", e.getMessage());
        } finally {
            dbHelper.closeResources(db, cursor);
        }
        return rol;
    }

    public List<Rol> getAll() {
        List<Rol> roles = new ArrayList<>();
        try {
            this.db = dbHelper.getReadableDatabase();
            String sql = "SELECT * FROM roles";
            this.cursor = db.rawQuery(sql, null);

            if (cursor == null) return roles;

            while (cursor.moveToNext()) {
                int rolID = cursor.getInt(0);
                String rolName = cursor.getString(1);
                roles.add(new Rol(rolID, rolName));
            }
        } catch (SQLException e) {
            Log.e("Error al obtener roles", e.getMessage());
        } finally {
            dbHelper.closeResources(this.db, this.cursor);
        }
        return roles;
    }
}
