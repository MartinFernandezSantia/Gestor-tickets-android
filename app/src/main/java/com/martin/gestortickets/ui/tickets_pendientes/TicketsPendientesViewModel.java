package com.martin.gestortickets.ui.tickets_pendientes;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.martin.gestortickets.dao.TicketDAO;
import com.martin.gestortickets.entities.Ticket;
import com.martin.gestortickets.entities.Usuario;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class TicketsPendientesViewModel extends AndroidViewModel {
    private MutableLiveData<List<Ticket>> ticketListLiveData;
    private TicketDAO ticketDAO;
    private Map<Integer, Integer> ticketsIndex; // Key = ticket.id ; Value = index from List in MutableLiveData


    public TicketsPendientesViewModel(Application application) {
        super(application);
        ticketDAO = new TicketDAO(application);
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

        for (int i = tickets.size()-1; i>=0; i--) {
            if (tickets.get(i).getEstado() != null && tickets.get(i).getEstado().getId() != 3) {
                tickets.remove(i);
            }
        }

        for (int i=0; i < tickets.size(); i++) {
            ticketsIndex.put(tickets.get(i).getId(), i);
        }

        ticketListLiveData.setValue(tickets);
    }

    public boolean takeTicket(int ticketID, Usuario user) {
        boolean taken = ticketDAO.update(ticketID, user.getId(), 1);

        if (!taken) return false;

        // Get current tickets list and remove the ticket
        List<Ticket> tickets = new ArrayList<>(ticketListLiveData.getValue());
        int indexToRemove = ticketsIndex.get(ticketID);
        tickets.remove(indexToRemove);

        // Remove the ticketID from the index map
        ticketsIndex.remove(ticketID);

        // Decrement all indices greater than indexToRemove by 1
        for (Map.Entry<Integer, Integer> entry : ticketsIndex.entrySet()) {
            if (entry.getValue() > indexToRemove) {
                ticketsIndex.put(entry.getKey(), entry.getValue() - 1);
            }
        }

        // Update the LiveData with the modified tickets list
        ticketListLiveData.setValue(tickets);

        return true;
    }


    public int amountOfTicketsTaken(Usuario user) {
        return ticketDAO.getAllTaken(user.getId()).size();
    }

    public Ticket getTicketByID(int ticketID) {
        Optional<Ticket> ticket = ticketDAO.getByID(ticketID);

        return ticket.orElse(null);
    }
}