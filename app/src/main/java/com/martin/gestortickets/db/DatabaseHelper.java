package com.martin.gestortickets.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "control_tickets.db";
    private static final int DATABASE_VERSION = 3;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create the roles table
        String CREATE_ROLES_TABLE = "CREATE TABLE IF NOT EXISTS roles (" +
                "id INTEGER PRIMARY KEY, " +
                "nombre TEXT NOT NULL" +
                ");";
        db.execSQL(CREATE_ROLES_TABLE);

        // Create the usuarios table
        String CREATE_USUARIOS_TABLE = "CREATE TABLE IF NOT EXISTS usuarios (" +
                "id INTEGER PRIMARY KEY, " +
                "contraseña TEXT NOT NULL, " +
                "bloqueado INTEGER NOT NULL DEFAULT 0, " +
                "rol_id INTEGER NOT NULL, " +
                "FOREIGN KEY (rol_id) REFERENCES roles(id)" +
                ");";
        db.execSQL(CREATE_USUARIOS_TABLE);

        // Create the tickets table
        String CREATE_TICKETS_TABLE = "CREATE TABLE IF NOT EXISTS tickets (" +
                "id INTEGER PRIMARY KEY, " +
                "titulo TEXT NOT NULL, " +
                "descripcion TEXT NOT NULL, " +
                "usuario_id INTEGER NOT NULL, " +
                "FOREIGN KEY (usuario_id) REFERENCES usuarios(id)" +
                ");";
        db.execSQL(CREATE_TICKETS_TABLE);

        // Create the fallas_y_marcas table
        String CREATE_FALLAS_Y_MARCAS_TABLE = "CREATE TABLE IF NOT EXISTS fallas_y_marcas (" +
                "id INTEGER PRIMARY KEY, " +
                "num_fallas INTEGER NOT NULL DEFAULT 0, " +
                "num_marcas INTEGER NOT NULL DEFAULT 0, " +
                "usuario_id INTEGER NOT NULL, " +
                "FOREIGN KEY (usuario_id) REFERENCES usuarios(id)" +
                ");";
        db.execSQL(CREATE_FALLAS_Y_MARCAS_TABLE);

        // Create the estados_tickets table
        String CREATE_ESTADOS_TICKETS_TABLE = "CREATE TABLE IF NOT EXISTS estados_tickets (" +
                "id INTEGER PRIMARY KEY, " +
                "nombre TEXT NOT NULL" +
                ");";
        db.execSQL(CREATE_ESTADOS_TICKETS_TABLE);

        // Create the asignaciones table
        String CREATE_ASIGNACIONES_TABLE = "CREATE TABLE IF NOT EXISTS asignaciones (" +
                "id INTEGER PRIMARY KEY, " +
                "ticket_id INTEGER NOT NULL, " +
                "usuario_id INTEGER, " +
                "estado_id INTEGER NOT NULL, " +
                "FOREIGN KEY (ticket_id) REFERENCES tickets(id), " +
                "FOREIGN KEY (usuario_id) REFERENCES usuarios(id), " +
                "FOREIGN KEY (estado_id) REFERENCES estados_tickets(id)" +
                ");";
        db.execSQL(CREATE_ASIGNACIONES_TABLE);

        // Create the notificaciones table
        String CREATE_NOTIFICACIONES_TABLE = "CREATE TABLE IF NOT EXISTS notificaciones (" +
                "id INTEGER PRIMARY KEY, " +
                "mensaje TEXT NOT NULL, " +
                "visto INTEGER NOT NULL DEFAULT 0, " +
                "usuario_id INTEGER NOT NULL, " +
                "FOREIGN KEY (usuario_id) REFERENCES usuarios(id)" +
                ");";
        db.execSQL(CREATE_NOTIFICACIONES_TABLE);

        // Insert hardcoded roles, estados and admin
        insertInitialRoles(db);
        insertInitialEstados(db);
        insertInitialAdmin(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop existing tables if they exist and recreate them
        db.execSQL("DROP TABLE IF EXISTS notificaciones");
        db.execSQL("DROP TABLE IF EXISTS asignaciones");
        db.execSQL("DROP TABLE IF EXISTS estados_tickets");
        db.execSQL("DROP TABLE IF EXISTS fallas_y_marcas");
        db.execSQL("DROP TABLE IF EXISTS tickets");
        db.execSQL("DROP TABLE IF EXISTS usuarios");
        db.execSQL("DROP TABLE IF EXISTS roles");
        onCreate(db);
    }

    public void closeResources(SQLiteDatabase db, Cursor cursor) {
        if (db != null) {
            db.close();
        }
        if (cursor != null) {
            cursor.close();
        }
    }

    private void insertInitialRoles(SQLiteDatabase db) {
        db.execSQL("INSERT INTO roles (nombre) VALUES ('Administrador');");
        db.execSQL("INSERT INTO roles (nombre) VALUES ('Tecnico');");
        db.execSQL("INSERT INTO roles (nombre) VALUES ('Trabajador');");
    }

    private void insertInitialEstados(SQLiteDatabase db) {
        db.execSQL("INSERT INTO estados_tickets (nombre) VALUES ('Atendido');");
        db.execSQL("INSERT INTO estados_tickets (nombre) VALUES ('Resuelto');");
        db.execSQL("INSERT INTO estados_tickets (nombre) VALUES ('Reabierto');");
        db.execSQL("INSERT INTO estados_tickets (nombre) VALUES ('Finalizado');");
    }

    private void insertInitialAdmin(SQLiteDatabase db) {
        db.execSQL("INSERT INTO usuarios (contraseña, rol_id) VALUES ('1', 1);");
    }
}

