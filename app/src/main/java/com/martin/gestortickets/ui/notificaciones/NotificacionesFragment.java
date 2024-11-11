package com.martin.gestortickets.ui.notificaciones;

import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.martin.gestortickets.R;
import com.martin.gestortickets.databinding.FragmentHomeBinding;
import com.martin.gestortickets.databinding.FragmentNotificacionesBinding;
import com.martin.gestortickets.databinding.FragmentTicketsTrabajadorBinding;
import com.martin.gestortickets.entities.Notificacion;
import com.martin.gestortickets.entities.Ticket;
import com.martin.gestortickets.entities.Usuario;
import com.martin.gestortickets.ui.home.HomeViewModel;
import com.martin.gestortickets.ui.tickets_trabajador.TicketsTrabajadorViewModel;

import java.util.List;

public class NotificacionesFragment extends Fragment {
    private TableLayout ticketTable;
    private boolean alterBackgroundColor = true;
    private int selectedRowIndex = -1;
    private boolean selectedRowWasWhite;
    private Usuario user;
    private NotificacionesViewModel notificacionesViewModel;
    private FragmentNotificacionesBinding binding;
    private Button verNotificacionBtn;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        notificacionesViewModel = new ViewModelProvider(requireActivity()).get(NotificacionesViewModel.class);
        binding = FragmentNotificacionesBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        user = (Usuario) getArguments().getSerializable("user");
        ticketTable = binding.ticketsTable;
        verNotificacionBtn = binding.verNotificacion;

        notificacionesViewModel.getNotificaciones().observe(getViewLifecycleOwner(), this::updateTicketTable);
        verNotificacionBtn.setOnClickListener(v -> updateVisto());

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void updateTicketTable(List<Notificacion> tickets) {
        ticketTable.removeAllViews(); // Clear table

        // Add headers
        addHeaderRow();

        for (int i=0; i< tickets.size(); i++) {
            Notificacion ticket = tickets.get(i);

            // New row cell values
            String[] ticketString = new String[]{
                    String.valueOf(ticket.getId()),
                    ticket.getMensaje()
            };
            // Add row
            addDataRow(ticketString, alterBackgroundColor);
            // Changes next row's bg
            alterBackgroundColor = !alterBackgroundColor;
        }
    }

    private void addHeaderRow() {
        String[] headers = new String[]{"ID", "Notificaciones"};
        // new Row
        TableRow row = new TableRow(getContext());

        // For each cell in the row
        for (int i=0; i < headers.length; i++) {
            addCell(headers[i], row, (i==0), false);
        }
        ticketTable.addView(row);
    }

    private void addDataRow(String[] texts, boolean isWhite) {
        // new Row
        TableRow row = new TableRow(getContext());

        // For each cell in the row
        for (int i = 0; i < texts.length; i++) {
            addCell(texts[i], row, (i != 1 && i != 4), isWhite);
        }

        // Sets row background color
        if (isWhite) {
            row.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.white));
        }

        // Handle row click event for selection
        row.setOnClickListener(view -> {
            // Clear previous selection if any
            if (selectedRowIndex != -1) {
                TableRow previousRow = (TableRow) ticketTable.getChildAt(selectedRowIndex);
                int previousColor = selectedRowWasWhite ? ContextCompat.getColor(getContext(), R.color.white) :
                        ContextCompat.getColor(getContext(), androidx.cardview.R.color.cardview_dark_background);
                previousRow.setBackgroundColor(previousColor);
            }

            // Select or deselect the clicked row
            if (selectedRowIndex == ticketTable.indexOfChild(row)) {
                selectedRowIndex = -1; // Deselect if the same row is clicked again
            } else {
                selectedRowIndex = ticketTable.indexOfChild(row); // Track selected row index
                selectedRowWasWhite = isWhite; // Store the original color for reset
                row.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.teal_700)); // Selected color
            }
        });

        // Add the row to the TableLayout
        ticketTable.addView(row);
    }

    private void addCell(String text, TableRow row, boolean halfWidth, boolean isWhite) {
        // new Cell
        TextView textView = new TextView(getContext());

        // Cell parameters
        TableRow.LayoutParams params = new TableRow.LayoutParams(
                TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT,
                (halfWidth) ? 0.5f : 1f);

        // Cell settings
        textView.setText(text);
        textView.setPadding(8,8,8,8);
        textView.setGravity(Gravity.CENTER);
        textView.setLayoutParams(params);

        // Cell text color
        if (!isWhite) {
            textView.setTextAppearance(R.style.TableTextStyle);
        }

        row.addView(textView);
    }

    private void updateVisto() {
        if (selectedRowIndex == -1) {
            Toast.makeText(getContext(), "No se ha seleccionado una notificacion", Toast.LENGTH_SHORT).show();
            return;
        }

        TableRow row = (TableRow) ticketTable.getChildAt(selectedRowIndex);
        TextView idTV = (TextView) row.getChildAt(0);
        int id = Integer.parseInt(idTV.getText().toString());

        if (notificacionesViewModel.marcarVista(id)) {
            updateTicketTable(notificacionesViewModel.getNotificaciones().getValue());
        }
        else {
            Toast.makeText(getContext(), "No se ha podido marcar la notificacion como vista", Toast.LENGTH_SHORT).show();
        }
    }
}