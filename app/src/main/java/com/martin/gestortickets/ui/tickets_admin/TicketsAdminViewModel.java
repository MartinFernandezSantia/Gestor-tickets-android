package com.martin.gestortickets.ui.tickets_admin;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

import com.martin.gestortickets.dao.TicketDAO;
import com.martin.gestortickets.dao.UsuarioDAO;
import com.martin.gestortickets.entities.Estado;
import com.martin.gestortickets.entities.Ticket;
import com.martin.gestortickets.entities.Usuario;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class TicketsAdminViewModel extends AndroidViewModel {
    private MutableLiveData<List<Ticket>> ticketListLiveData;
    private MutableLiveData<Usuario> updatedTecnico = new MutableLiveData<>();
    private TicketDAO ticketDAO;
    private UsuarioDAO usuarioDAO;
    private Map<Integer, Integer> ticketsIndex; // Key = ticket.id ; Value = index from List in MutableLiveData


    public TicketsAdminViewModel(Application application) {
        super(application);
        ticketDAO = new TicketDAO(application);
        usuarioDAO = new UsuarioDAO(application);
        ticketListLiveData = new MutableLiveData<>();
        ticketsIndex = new HashMap<>();
        loadTickets();
    }

    public LiveData<List<Ticket>> getTickets() {
        return ticketListLiveData;
    }

    public void loadTickets() {
        List<Ticket> tickets = ticketDAO.getAll(null);
        ticketsIndex.clear();

        for (int i=0; i < tickets.size(); i++) {
            ticketsIndex.put(tickets.get(i).getId(), i);
        }

        ticketListLiveData.setValue(tickets);
    }

    public List<Ticket> getTicketsFiltered(int status) {
        List<Ticket> tickets = ticketDAO.getAll(null);

        // Remove all tickets which arent attended
        if (status == 0) {
            for (int i = tickets.size() - 1; i >= 0; i--) {
                if (tickets.get(i).getEstado() != null) {
                    tickets.remove(i);
                }
            }
        }
        else if (status > 0) { // Remove all tickets whose status aren't the selected one
            for (int i = tickets.size() - 1; i >= 0; i--) {
                if (tickets.get(i).getEstado() == null || tickets.get(i).getEstado().getId() != status) {
                    tickets.remove(i);
                }
            }
        }

        return tickets;
    }

    public boolean reopenTicket(int ticketID) {
        Ticket ticket = ticketDAO.getByID(ticketID).get();
        Usuario tecnico = ticket.getTecnico();
        boolean reopend = ticketDAO.update(ticketID, null, 3);

        if (!reopend) return false;

        Estado estado = new Estado(3, "Reabierto");
        ticketListLiveData.getValue().get(ticketsIndex.get(ticketID)).setEstado(estado);

        Log.d("TAG", "" + (tecnico != null));
        // Update marcas and fallas from Tecnico
        if (tecnico != null && !tecnico.isBloqueado()) {
            Log.d("TAG", "IN");
            if (tecnico.getMarcas() == 0) {
                tecnico.setMarcas(1);
            }
            else {
                tecnico.setMarcas(0);
                tecnico.setFallas(tecnico.getFallas() + 1);
            }
            usuarioDAO.updateMarcasYFallas(tecnico);

            // Block Tecnico if fallas = 3
            if (tecnico.getFallas() == 3) {
                usuarioDAO.updateBloqueado(tecnico.getId(), true);
            }

            updatedTecnico.setValue(tecnico);
            ticketListLiveData.getValue().get(ticketsIndex.get(ticketID)).setTecnico(tecnico);
        }



        return true;
    }

    public LiveData<Usuario> getUpdatedTecnico() {return updatedTecnico;}

    public Ticket getTicketByID(int ticketID) {
        Optional<Ticket> ticket = ticketDAO.getByID(ticketID);

        return ticket.orElse(null);
    }
}