package com.martin.gestortickets.ui.tickets_admin;

import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.martin.gestortickets.R;
import com.martin.gestortickets.databinding.FragmentTicketsAdminBinding;
import com.martin.gestortickets.databinding.FragmentTicketsTrabajadorBinding;
import com.martin.gestortickets.databinding.FragmentUsersBinding;
import com.martin.gestortickets.entities.Ticket;
import com.martin.gestortickets.entities.Usuario;
import com.martin.gestortickets.ui.ViewTicketDialog;
import com.martin.gestortickets.ui.users.UsersViewModel;

import java.util.ArrayList;
import java.util.List;

public class TicketsAdminFragment extends Fragment {
    private TableLayout ticketTable;
    private FragmentTicketsAdminBinding binding;
    private boolean alterBackgroundColor = true;
    private int selectedRowIndex = -1;
    private boolean selectedRowWasWhite;
    private Usuario user;
    private TicketsAdminViewModel ticketsAdminViewModel;
    private UsersViewModel usersViewModel;
    private Button reabrirBtn;
    private Button verTicketBtn;
    private Spinner mySpinner;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ticketsAdminViewModel = new ViewModelProvider(requireActivity()).get(TicketsAdminViewModel.class);
        usersViewModel = new ViewModelProvider(requireActivity()).get(UsersViewModel.class);
        binding = FragmentTicketsAdminBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        ticketTable = binding.ticketsTable;
        reabrirBtn = binding.reabrirBtn;
        verTicketBtn = binding.verTicketBtn;
        mySpinner = binding.mySpinner;

        ticketsAdminViewModel.getTickets().observe(getViewLifecycleOwner(), this::updateTicketTable);
        ticketsAdminViewModel.getUpdatedTecnico().observe(getViewLifecycleOwner(), usersViewModel::updateUser);

        reabrirBtn.setOnClickListener(v -> reabirTicket());
        verTicketBtn.setOnClickListener(v -> viewTicket());
        mySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                filterTickets();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Optionally handle when nothing is selected (if needed)
            }
        });
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void updateTicketTable(List<Ticket> tickets) {
        ticketTable.removeAllViews(); // Clear table

        // Add headers
        addHeaderRow();

        for (int i=0; i< tickets.size(); i++) {
            Ticket ticket = tickets.get(i);

            // New row cell values
            String[] ticketString = new String[]{
                    ticket.getId().toString(),
                    ticket.getTitulo(),
                    ticket.getCreador().getId().toString(),
                    (ticket.getEstado() != null) ? ticket.getEstado().getNombre() : "-",
                    (ticket.getTecnico() != null) ? ticket.getTecnico().getId().toString() : "-"
            };
            // Add row
            addDataRow(ticketString, alterBackgroundColor);
            // Changes next row's bg
            alterBackgroundColor = !alterBackgroundColor;
        }
    }

    private void addHeaderRow() {
        String[] headers = new String[]{"ID", "Titulo", "Creador", "Estado", "Tecnico"};
        // new Row
        TableRow row = new TableRow(getContext());

        // For each cell in the row
        for (int i=0; i < headers.length; i++) {
            addCell(headers[i], row, (i!=1), false);
        }
        ticketTable.addView(row);
    }

    private void addDataRow(String[] texts, boolean isWhite) {
        // new Row
        TableRow row = new TableRow(getContext());

        // For each cell in the row
        for (int i = 0; i < texts.length; i++) {
            addCell(texts[i], row, (i!=1), isWhite);
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


    private void filterTickets() {
        // Reset important variables
        alterBackgroundColor = true;
        selectedRowIndex = -1;

        int selectedEstado = mySpinner.getSelectedItemPosition();

        if (selectedEstado == 0) {
            updateTicketTable(ticketsAdminViewModel.getTickets().getValue());
            return;
        }

        ticketTable.removeAllViews(); // Clear table

        // Add headers
        addHeaderRow();

        List<Ticket> tickets = ticketsAdminViewModel.getTicketsFiltered(selectedEstado - 1);
        for (int i=0; i< tickets.size(); i++) {
            Ticket ticket = tickets.get(i);

            // New row cell values
            String[] ticketString = new String[]{
                    ticket.getId().toString(),
                    ticket.getTitulo(),
                    ticket.getCreador().getId().toString(),
                    (ticket.getEstado() != null) ? ticket.getEstado().getNombre() : "-",
                    (ticket.getTecnico() != null) ? ticket.getTecnico().getId().toString() : "-"
            };
            // Add row
            addDataRow(ticketString, alterBackgroundColor);
            // Changes next row's bg
            alterBackgroundColor = !alterBackgroundColor;
        }
    }

    private void reabirTicket() {
        if (selectedRowIndex == -1) {
            Toast.makeText(getContext(), "No se ha seleccionado un ticket", Toast.LENGTH_SHORT).show();
            return;
        }

        TableRow row = (TableRow) ticketTable.getChildAt(selectedRowIndex);
        TextView idTV = (TextView) row.getChildAt(0);
        int id = Integer.parseInt(idTV.getText().toString());

        if (ticketsAdminViewModel.reopenTicket(id)) {
            Toast.makeText(getContext(), "Ticket reabierto", Toast.LENGTH_SHORT).show();
            filterTickets();
        }
        else {
            Toast.makeText(getContext(), "No se ha podido reabrir el ticket", Toast.LENGTH_SHORT).show();
        }
    }

    private void viewTicket() {
        if (selectedRowIndex == -1) {
            Toast.makeText(getContext(), "No se ha seleccionado un ticket", Toast.LENGTH_SHORT).show();
            return;
        }

        TableRow row = (TableRow) ticketTable.getChildAt(selectedRowIndex);
        TextView idTV = (TextView) row.getChildAt(0);
        int id = Integer.parseInt(idTV.getText().toString());

        Ticket ticket = ticketsAdminViewModel.getTicketByID(id);
        if (ticket == null) return;

        ViewTicketDialog viewTicketDialog = ViewTicketDialog.newInstance(
                ticket.getTitulo(),
                (ticket.getEstado() != null) ? ticket.getEstado().getNombre() : "No atendido",
                ticket.getCreador().getId().toString(),
                (ticket.getTecnico() != null) ? ticket.getTecnico().getId().toString() : "No asignado",
                ticket.getDescripcion()
        );
        viewTicketDialog.show(getParentFragmentManager(), "My View Dialog");
    }
}