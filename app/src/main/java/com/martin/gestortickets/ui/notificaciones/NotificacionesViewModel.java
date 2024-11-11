package com.martin.gestortickets.ui.notificaciones;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.martin.gestortickets.dao.NotificacionDAO;
import com.martin.gestortickets.entities.Notificacion;
import com.martin.gestortickets.entities.Ticket;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NotificacionesViewModel extends AndroidViewModel {

    private MutableLiveData<List<Notificacion>> notificacionesListLiveData;
    private NotificacionDAO notificacionDAO;
    private Map<Integer, Integer> notificacionesIndex; // Key = ticket.id ; Value = index from List in MutableLiveData


    public NotificacionesViewModel(Application application) {
        super(application);
        notificacionDAO = new NotificacionDAO(application);
        notificacionesListLiveData = new MutableLiveData<>();
        notificacionesIndex = new HashMap<>();
        loadTickets();
    }

    public LiveData<List<Notificacion>> getNotificaciones() {
        return notificacionesListLiveData;
    }

    public void loadTickets() {
        List<Notificacion> notificaciones = notificacionDAO.getAllNotSeen();
        Log.d("TAG", ""+notificaciones);
        notificacionesIndex.clear();

        for (int i=0; i < notificaciones.size(); i++) {
            notificacionesIndex.put(notificaciones.get(i).getId(), i);
        }

        notificacionesListLiveData.setValue(notificaciones);
    }

    public boolean marcarVista(int notificacionID) {
        boolean updated = notificacionDAO.updateVisto(notificacionID);
        int indexToRemove = notificacionesIndex.get(notificacionID);

        if (!updated) return false;

        // Remove notificacion from liveData
        List<Notificacion> tickets = new ArrayList<>(notificacionesListLiveData.getValue());
        tickets.remove(indexToRemove);

        for (Map.Entry<Integer, Integer> entry : notificacionesIndex.entrySet()) {
            if (entry.getValue() > indexToRemove) {
                notificacionesIndex.put(entry.getKey(), entry.getValue() - 1);
            }
        }

        notificacionesListLiveData.setValue(tickets);

        return true;
    }
}