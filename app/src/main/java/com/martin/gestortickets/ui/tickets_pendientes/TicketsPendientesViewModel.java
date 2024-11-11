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


        List<Ticket> tickets = new ArrayList<>(ticketListLiveData.getValue());
        Log.d("TAG", ticketsIndex.toString() + " - " + tickets.toString());

        Log.d("TAG", ticketsIndex.get(ticketID).toString() + " - " + ticketsIndex.size());

        tickets.remove((int) ticketsIndex.get(ticketID));
        Log.d("TAG", "" + tickets.size());

        ticketsIndex.remove(ticketID);
        ticketListLiveData.setValue(tickets);

        return true;
    }

    public int amountOfTicketsTaken(Usuario user) {
        return ticketDAO.getAllTaken(user.getId()).size();
    }
}