package com.martin.gestortickets.ui.tickets_trabajador;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.martin.gestortickets.R;

public class TicketsTrabajadorFragment extends Fragment {

    private TicketsTrabajadorViewModel mViewModel;

    public static TicketsTrabajadorFragment newInstance() {
        return new TicketsTrabajadorFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tickets_trabajador, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(TicketsTrabajadorViewModel.class);
        // TODO: Use the ViewModel
    }

}