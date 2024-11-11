package com.martin.gestortickets.ui.tickets_tecnico;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

import com.martin.gestortickets.dao.NotificacionDAO;
import com.martin.gestortickets.dao.TicketDAO;
import com.martin.gestortickets.entities.Notificacion;
import com.martin.gestortickets.entities.Ticket;
import com.martin.gestortickets.entities.Usuario;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class TicketsTecnicoViewModel extends AndroidViewModel {
    private MutableLiveData<List<Ticket>> ticketListLiveData;
    private TicketDAO ticketDAO;
    private NotificacionDAO notificacionDAO;
    private Map<Integer, Integer> ticketsIndex; // Key = ticket.id ; Value = index from List in MutableLiveData
    private SavedStateHandle savedStateHandle;


    public TicketsTecnicoViewModel(Application application, SavedStateHandle savedStateHandle) {
        super(application);
        ticketDAO = new TicketDAO(application);
        notificacionDAO = new NotificacionDAO(application);
        ticketListLiveData = new MutableLiveData<>();
        ticketsIndex = new HashMap<>();
        this.savedStateHandle = savedStateHandle;
    }

    public LiveData<List<Ticket>> getTickets() {
        return ticketListLiveData;
    }

    public void loadTickets() {
        Usuario user = savedStateHandle.get("user");
        List<Ticket> tickets = ticketDAO.getAllTaken(user.getId());

        for (int i=0; i < tickets.size(); i++) {
            ticketsIndex.put(tickets.get(i).getId(), i);
        }

        ticketListLiveData.setValue(tickets);
    }

    public void setUser(Usuario user) {
        savedStateHandle.set("user", user);
    }

    public boolean solveTicket(int ticketID) {
        Usuario user = savedStateHandle.get("user");
        boolean updated = ticketDAO.update(ticketID, user.getId(), 2);
        int indexToRemove = ticketsIndex.get(ticketID);


        if (!updated) return false;

        Log.d("TAG", "" + ticketsIndex.get(ticketID));
        List<Ticket> tickets = new ArrayList<>(ticketListLiveData.getValue());
        tickets.remove(indexToRemove);

        ticketsIndex.remove(ticketID);
        // Decrement all indices greater than indexToRemove by 1
        for (Map.Entry<Integer, Integer> entry : ticketsIndex.entrySet()) {
            if (entry.getValue() > indexToRemove) {
                ticketsIndex.put(entry.getKey(), entry.getValue() - 1);
            }
        }

        ticketListLiveData.setValue(tickets);

        return true;
    }

    public int requestReopen(int ticketID) {
        Usuario user = savedStateHandle.get("user");
        String message = "Técnico " + user.getId() + " solicita reabrir el ticket con ID " + ticketID;
        Notificacion notificacion = new Notificacion(message, user);

        return notificacionDAO.create(notificacion);
    }

    public Ticket getTicketByID(int ticketID) {
        Optional<Ticket> ticket = ticketDAO.getByID(ticketID);

        return ticket.orElse(null);
    }
}