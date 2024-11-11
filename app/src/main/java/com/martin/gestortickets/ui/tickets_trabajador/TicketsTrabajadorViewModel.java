package com.martin.gestortickets.ui.tickets_trabajador;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;

import com.martin.gestortickets.dao.TicketDAO;
import com.martin.gestortickets.entities.Ticket;
import com.martin.gestortickets.entities.Usuario;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TicketsTrabajadorViewModel extends AndroidViewModel {
    private MutableLiveData<List<Ticket>> ticketListLiveData;
    private TicketDAO ticketDAO;
    private Map<Integer, Integer> ticketsIndex; // Key = ticket.id ; Value = index from List in MutableLiveData
    private SavedStateHandle savedStateHandle;


    public TicketsTrabajadorViewModel(Application application, SavedStateHandle savedStateHandle) {
        super(application);
        ticketDAO = new TicketDAO(application);
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

//    public boolean finishTicket() {
//        return ticketDAO.
//    }
}