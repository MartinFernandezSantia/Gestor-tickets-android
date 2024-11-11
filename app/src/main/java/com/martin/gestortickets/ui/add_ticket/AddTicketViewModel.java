package com.martin.gestortickets.ui.add_ticket;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.martin.gestortickets.dao.TicketDAO;
import com.martin.gestortickets.entities.Ticket;
import com.martin.gestortickets.entities.Usuario;

public class AddTicketViewModel extends AndroidViewModel {
    private TicketDAO ticketDAO;
    private MutableLiveData<Ticket> newTicketLiveData = new MutableLiveData<>();

    public AddTicketViewModel(Application application) {
        super(application);

        ticketDAO = new TicketDAO(application);
    }

    public LiveData<Ticket> getNewTicket() {return newTicketLiveData;}

    public boolean createTicket(String title, String description, Usuario creator) {
        Ticket newTicket = new Ticket(title, description, creator);
        boolean created = ticketDAO.create(newTicket);

        if (created) newTicketLiveData.setValue(newTicket);

        return created;
    }
}