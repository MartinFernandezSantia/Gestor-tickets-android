package com.martin.gestortickets.ui.users;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.martin.gestortickets.R;
import com.martin.gestortickets.databinding.FragmentUsersBinding;
import com.martin.gestortickets.entities.Usuario;

import java.util.ArrayList;
import java.util.List;

public class UsersFragment extends Fragment {
    private TableLayout userTable;
    private FragmentUsersBinding binding;
    private boolean alterBackgroundColor = true;
    private List<Boolean> rowSelectionStates = new ArrayList<>();


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        UsersViewModel usersViewModel = new ViewModelProvider(requireActivity()).get(UsersViewModel.class);
        binding = FragmentUsersBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        userTable = root.findViewById(R.id.userTable);

        usersViewModel.getUsers().observe(getViewLifecycleOwner(), this::updateUserTable);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void updateUserTable(List<Usuario> users) {
        userTable.removeAllViews(); // Clear table

        // Add headers
        addHeaderRow();

        for (int i=0; i< users.size(); i++) {
            Usuario user = users.get(i);
            // New row cell values
            String[] userString = new String[]{
                    user.getId().toString(),
                    user.getRol().getNombre(),
                    (user.getFallas() != null) ? user.getFallas().toString() : null,
                    (user.getMarcas() != null) ? user.getMarcas().toString() : null,
            };
            // Add row
            addDataRow(userString, alterBackgroundColor, user.isBloqueado(), i);
            // Changes next row's bg
            alterBackgroundColor = !alterBackgroundColor;
        }
    }

    private void addHeaderRow() {
        String[] headers = new String[]{"ID", "Rol", "Fallas", "Marcas", "Bloqueado"};
        // new Row
        TableRow row = new TableRow(getContext());

        // For each cell in the row
        for (int i=0; i < headers.length; i++) {
            addCell(headers[i], row, (i!=1 && i!=4), false);
        }
        userTable.addView(row);
    }

    private void addDataRow(String[] texts, boolean isWhite, boolean isBloqueado, int rowIndex) {
        // new Row
        TableRow row = new TableRow(getContext());

        // For each cell in the row
        for (int i=0; i < texts.length; i++) {
            addCell(texts[i], row, (i!=1 && i!=4), isWhite);
        }
        addSwitchCell(isBloqueado, row, isWhite);

        // Sets row background color
        if (isWhite) {
            row.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.white));
        }

        // Handle row click event for selection
        row.setOnClickListener(view -> {
            // Toggle selection state for this row
            boolean isSelected = rowSelectionStates.get(rowIndex);
            rowSelectionStates.set(rowIndex, !isSelected);

            // Change background color based on selection
            if (!isSelected) {
                row.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.teal_700));
            } else {
                row.setBackgroundColor(isWhite ? ContextCompat.getColor(getContext(), R.color.white) :
                        ContextCompat.getColor(getContext(), androidx.cardview.R.color.cardview_dark_background));
            }
        });

        // Initialize the selection state for this row
        rowSelectionStates.add(false);

        // Add the row to the TableLayout
        userTable.addView(row);
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

    private void addSwitchCell(boolean isBloqueado, TableRow row, boolean isWhite) {
        Switch userSwitch = new Switch(getContext());

        TableRow.LayoutParams params = new TableRow.LayoutParams(
                TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT,
                1f);
        params.setMargins(0,0,70,0);
        userSwitch.setChecked(isBloqueado);
        userSwitch.setText(null);
        userSwitch.setPadding(8, 8, 8, 8);
        userSwitch.setLayoutParams(params);

        if (isWhite) {
            userSwitch.setThumbTintList(ColorStateList.valueOf(getResources().getColor(com.google.android.material.R.color.cardview_dark_background)));
        }
        else {
            userSwitch.setTrackTintList(ColorStateList.valueOf(getResources().getColor(R.color.borderColor)));
        }

        row.addView(userSwitch);
    }
}