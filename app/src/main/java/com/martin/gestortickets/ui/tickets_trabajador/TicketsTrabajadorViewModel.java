package com.martin.gestortickets.ui.tickets_trabajador;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;

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

public class TicketsTrabajadorViewModel extends AndroidViewModel {
    private MutableLiveData<List<Ticket>> ticketListLiveData;
    private TicketDAO ticketDAO;
    private UsuarioDAO usuarioDAO;
    private Map<Integer, Integer> ticketsIndex; // Key = ticket.id ; Value = index from List in MutableLiveData
    private SavedStateHandle savedStateHandle;


    public TicketsTrabajadorViewModel(Application application, SavedStateHandle savedStateHandle) {
        super(application);
        ticketDAO = new TicketDAO(application);
        usuarioDAO = new UsuarioDAO(application);
        ticketListLiveData = new MutableLiveData<>();
        ticketsIndex = new HashMap<>();
        this.savedStateHandle = savedStateHandle;

    }

    public LiveData<List<Ticket>> getTickets() {
        return ticketListLiveData;
    }

    public void loadTickets() {
        Usuario user = savedStateHandle.get("user");
        List<Ticket> tickets = ticketDAO.getAll(user.getId());
        ticketsIndex.clear();

        // Remove all finished tickets
        for (int i=tickets.size()-1; i>=0; i--) {
            if (tickets.get(i).getEstado().getId() == 4) {
                tickets.remove(i);
            }
        }

        for (int i=0; i < tickets.size(); i++) {
            ticketsIndex.put(tickets.get(i).getId(), i);
        }

        ticketListLiveData.setValue(tickets);
    }

    public void addTicket(Ticket ticket) {
        List<Ticket> currentTickets = ticketListLiveData.getValue();

        // Create a new list and add the ticket to it
        List<Ticket> updatedTickets = new ArrayList<>(currentTickets);
        updatedTickets.add(ticket);
        ticketsIndex.put(ticket.getId(), updatedTickets.size());

        // Set the updated list to trigger the observer
        ticketListLiveData.setValue(updatedTickets);
    }

    public void setUser(Usuario user) {
        savedStateHandle.set("user", user);
    }

    public boolean finishTicket(int ticketID, int tecnicoID) {
        boolean updated = ticketDAO.update(ticketID, tecnicoID, 4);
        Usuario tecnico = usuarioDAO.getByID(tecnicoID).get();
        int indexToRemove = ticketsIndex.get(ticketID);


        if (!updated) return false;

        // Si el tecnico resolvio un ticket reabierto y tenia fallas, se le descuenta 1
        if (ticketDAO.wasReopened(ticketID) && tecnico.getFallas() > 0) {
            tecnico.setFallas(tecnico.getFallas() -1);
            usuarioDAO.updateMarcasYFallas(tecnico);
        }

        // Remove ticket from liveData
        List<Ticket> tickets = new ArrayList<>(ticketListLiveData.getValue());
        tickets.remove(indexToRemove);

        for (Map.Entry<Integer, Integer> entry : ticketsIndex.entrySet()) {
            if (entry.getValue() > indexToRemove) {
                ticketsIndex.put(entry.getKey(), entry.getValue() - 1);
            }
        }

        ticketListLiveData.setValue(tickets);

        return true;
    }

    public boolean reopenTicket(int ticketID) {
        Ticket ticket = ticketDAO.getByID(ticketID).get();
        Usuario tecnico = ticket.getTecnico();
        boolean reopend = ticketDAO.update(ticketID, null, 3);

        if (!reopend) return false;

        // Update liveData
        Estado estado = new Estado(3, "Reabierto");
        ticketListLiveData.getValue().get(ticketsIndex.get(ticketID)).setEstado(estado);
        ticketListLiveData.getValue().get(ticketsIndex.get(ticketID)).setTecnico(null);

        // Update fallas from Tecnico
        if (tecnico != null && !tecnico.isBloqueado()) {
            tecnico.setFallas(tecnico.getFallas() + 1);
            usuarioDAO.updateMarcasYFallas(tecnico);

            // Block Tecnico if fallas = 3
            if (tecnico.getFallas() == 3) {
                usuarioDAO.updateBloqueado(tecnico.getId(), true);
            }
        }

        return true;
    }

    public Ticket getTicketByID(int ticketID) {
        Optional<Ticket> ticket = ticketDAO.getByID(ticketID);

        return ticket.orElse(null);
    }
}