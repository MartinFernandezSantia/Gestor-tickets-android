package com.martin.gestortickets.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Path;
import android.util.Log;

import com.martin.gestortickets.db.DatabaseHelper;
import com.martin.gestortickets.entities.Estado;
import com.martin.gestortickets.entities.Ticket;
import com.martin.gestortickets.entities.Usuario;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TicketDAO {
    private DatabaseHelper dbHelper;
    private  UsuarioDAO usuarioDAO;
    private SQLiteDatabase db = null;
    private Cursor cursor = null;

    public TicketDAO(Context context) {
        dbHelper = new DatabaseHelper(context);
        usuarioDAO = new UsuarioDAO(context);
    }

    public boolean create(Ticket ticket) {
        try {
            this.db = dbHelper.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put("titulo", ticket.getTitulo());
            values.put("descripcion", ticket.getDescripcion());
            values.put("usuario_id", ticket.getCreador().getId());
            int ticketID = (int) this.db.insertOrThrow("tickets", null, values);
            ticket.setId(ticketID);

            return ticketID > 0;
        } catch (SQLException e) {
            Log.e("Error al crear ticket", e.getMessage());
        } finally {
            dbHelper.closeResources(this.db, this.cursor);
        }
        return false;
    }

    public boolean update(int ticketID, Integer tecnicoID, int estadoID) {
        try {
            this.db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();

            values.put("ticket_id", ticketID);
            values.put("usuario_id", tecnicoID);
            values.put("estado_id", estadoID);
            long rowsAffected = this.db.insertOrThrow("asignaciones", null, values);

            return rowsAffected > 0;
        } catch (SQLException e) {
            Log.e("Error al actualizar ticket", e.getMessage());
        } finally {
            dbHelper.closeResources(this.db, this.cursor);
        }
        return false;
    }

    public Optional<Ticket> getByID(int id) {
        Optional<Ticket> ticket = Optional.empty();
        try {
            String sql = "SELECT t.id, t.titulo, t.descripcion, t.usuario_id, e.id, e.nombre, a.usuario_id " +
                    "FROM tickets AS t " +
                    "LEFT JOIN (" +
                        "SELECT ticket_id, usuario_id, estado_id, id " +
                        "FROM asignaciones " +
                        "WHERE id IN (" +
                            "SELECT MAX(id) " +
                            "FROM asignaciones " +
                            "GROUP BY ticket_id" +
                        ")" +
                    ") AS a ON t.id = a.ticket_id " +
                    "LEFT JOIN estados_tickets AS e ON a.estado_id = e.id " +
                    "WHERE t.id = ? " +
                    "ORDER BY t.id, a.id";
            this.db = dbHelper.getReadableDatabase();
            this.cursor = this.db.rawQuery(sql, new String[]{String.valueOf(id)});

            if (this.cursor != null && this.cursor.moveToFirst()) {
                int ticketID = this.cursor.getInt(0);
                String titulo = this.cursor.getString(1);
                String descripcion = this.cursor.getString(2);
                Optional<Usuario> creador = usuarioDAO.getByID(this.cursor.getInt(3));
                Estado estado = null;
                Usuario tecnico = null;

                if (!this.cursor.isNull(4) && !this.cursor.isNull(5)) {
                    estado = new Estado(this.cursor.getInt(4), this.cursor.getString(5));
                }
                if (!this.cursor.isNull(6)) {
                    tecnico = usuarioDAO.getByID(this.cursor.getInt(6)).get();
                }

                ticket = Optional.of(new Ticket(ticketID, titulo, descripcion, creador.get(), tecnico, estado));
            }
        } catch (SQLException e) {
            Log.e("Error al obtener ticket", e.getMessage());
        } finally {
            dbHelper.closeResources(this.db, this.cursor);
        }
        return ticket;
    }

    /**@param usuarioID (optional) - set it to 'null' to get all tickets, else get all tickets created by 'usuarioID' */
    public List<Ticket> getAll(Integer usuarioID) {
        List<Ticket> tickets = new ArrayList<>();
        try {
            // Build sql query
            String sql =
                    "SELECT t.id, t.titulo, t.descripcion, t.usuario_id, e.id, e.nombre, a.usuario_id " +
                            "FROM tickets AS t " +
                            "LEFT JOIN (" +
                            "SELECT ticket_id, usuario_id, estado_id, id " +
                            "FROM asignaciones " +
                            "WHERE id IN (" +
                            "SELECT MAX(id) " +
                            "FROM asignaciones " +
                            "GROUP BY ticket_id" +
                            ")" +
                            ") AS a ON t.id = a.ticket_id " +
                            "LEFT JOIN estados_tickets AS e ON a.estado_id = e.id ";
            if (usuarioID != null) {
                sql += "WHERE t.usuario_id = ? ";
            }
            sql += "ORDER BY t.id, a.id";

            this.db = dbHelper.getReadableDatabase();
            this.cursor = this.db.rawQuery(sql,
                    (usuarioID != null) ? new String[]{String.valueOf(usuarioID)} : null
                    );

            while (this.cursor.moveToNext()) {
                int ticketID = this.cursor.getInt(0);
                String titulo = this.cursor.getString(1);
                String descripcion = this.cursor.getString(2);
                Optional<Usuario> creador = usuarioDAO.getByID(this.cursor.getInt(3));
                Estado estado = null;
                Usuario tecnico = null;

                if (!this.cursor.isNull(4) && !this.cursor.isNull(5)) {
                    estado = new Estado(this.cursor.getInt(4), this.cursor.getString(5));
                }
                if (!this.cursor.isNull(6)) {
                    tecnico = usuarioDAO.getByID(this.cursor.getInt(6)).get();
                }

                tickets.add(new Ticket(ticketID, titulo, descripcion, creador.get(), tecnico, estado));
            }
        } catch (SQLException e) {
            Log.e("Error al obtener tickets", e.getMessage());
        } finally {
            dbHelper.closeResources(this.db, this.cursor);
        }
        return tickets;
    }

    /**Gets all tickets taken by 'tecnicoID' which are currently active*/
    public List<Ticket> getAllTaken(int tecnicoID) {
        List<Ticket> tickets = new ArrayList<>();
        try {
            String sql = "SELECT t.id, t.titulo, t.descripcion, t.usuario_id, e.id, e.nombre, a.usuario_id " +
                    "FROM tickets AS t " +
                    "LEFT JOIN (" +
                        "SELECT ticket_id, usuario_id, estado_id, id " +
                        "FROM asignaciones " +
                        "WHERE id IN (" +
                            "SELECT MAX(id) " +
                            "FROM asignaciones " +
                            "GROUP BY ticket_id" +
                    ") " +
                    ") AS a ON t.id = a.ticket_id " +
                    "LEFT JOIN estados_tickets AS e ON a.estado_id = e.id " +
                    "WHERE a.usuario_id = ? AND a.estado_id = 1 " +
                    "ORDER BY t.id, a.id";

            this.db = dbHelper.getReadableDatabase();
            this.cursor = this.db.rawQuery(sql, new String[]{String.valueOf(tecnicoID)});

            while (this.cursor.moveToNext()) {
                int ticketID = this.cursor.getInt(0);
                String titulo = this.cursor.getString(1);
                String descripcion = this.cursor.getString(2);
                Optional<Usuario> creador = usuarioDAO.getByID(this.cursor.getInt(3));
                Estado estado = null;
                Usuario tecnico = null;

                if (!this.cursor.isNull(4) && !this.cursor.isNull(5)) {
                    estado = new Estado(this.cursor.getInt(4), this.cursor.getString(5));
                }
                if (!this.cursor.isNull(6)) {
                    tecnico = usuarioDAO.getByID(this.cursor.getInt(6)).get();
                }

                tickets.add(new Ticket(ticketID, titulo, descripcion, creador.get(), tecnico, estado));
            }
        } catch (SQLException e) {
            Log.e("Error al obtener tickets del tecnico", e.getMessage());
        } finally {
            dbHelper.closeResources(this.db, this.cursor);
        }
        return tickets;
    }

    /**Returns all IDs from the Tecnicos who were previously and are currently associated with the ticket*/
    public List<Integer> getAllTecnicos(int ticketID) {
        List<Integer> tecnicos = new ArrayList<>();
        try {
            String sql = "SELECT usuario_id FROM asignaciones as a WHERE a.ticket_id = ? GROUP BY usuario_id";
            this.db = dbHelper.getReadableDatabase();
            this.cursor = this.db.rawQuery(sql, new String[]{String.valueOf(ticketID)});

            while (this.cursor.moveToNext()) {
                if (this.cursor.isNull(0)) {
                    tecnicos.add(this.cursor.getInt(0));
                }
            }

        } catch (SQLException e) {
            Log.e("Error al obtener tecnicos del ticket", e.getMessage());
        } finally {
            dbHelper.closeResources(this.db, this.cursor);
        }
        return tecnicos;
    }

    boolean wasReopened(int ticketID) {
        try {
            String sql = "SELECT EXISTS (SELECT 1 FROM asignaciones WHERE ticket_id = ? AND estado_id = 3)";
            this.db = dbHelper.getReadableDatabase();
            this.cursor = db.rawQuery(sql, new String[]{String.valueOf(ticketID)});

            if (cursor.moveToFirst()) {
                return cursor.getInt(0) == 1;
            }
        } catch (SQLException e) {
            Log.e("Error al verificar si el ticket fue reabierto", e.getMessage());
        } finally {
            dbHelper.closeResources(this.db, this.cursor);
        }
        return false;
    }
}
