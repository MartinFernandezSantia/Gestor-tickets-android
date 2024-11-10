package com.martin.gestortickets.ui.tickets_tecnico;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.martin.gestortickets.R;

public class TicketsTecnicoFragment extends Fragment {

    private TicketsTecnicoViewModel mViewModel;

    public static TicketsTecnicoFragment newInstance() {
        return new TicketsTecnicoFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tickets_tecnico, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(TicketsTecnicoViewModel.class);
        // TODO: Use the ViewModel
    }

}