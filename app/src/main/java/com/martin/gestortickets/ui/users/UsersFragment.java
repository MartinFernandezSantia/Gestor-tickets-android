package com.martin.gestortickets.ui.users;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

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
    private int selectedRowIndex = -1;
    private boolean selectedRowWasWhite;
    private UsersViewModel usersViewModel;
    private Usuario user;
    private Button resetPasswBtn;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        usersViewModel = new ViewModelProvider(requireActivity()).get(UsersViewModel.class);
        binding = FragmentUsersBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        userTable = binding.userTable;
        resetPasswBtn = binding.resetPasswBtn;
        user = (Usuario) getArguments().getSerializable("user");

        usersViewModel.getUsers().observe(getViewLifecycleOwner(), this::updateUserTable);
        resetPasswBtn.setOnClickListener(v -> resetPassword());

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
            Usuario user1 = users.get(i);
            if (user1.equals(user)) continue;
            // New row cell values
            String[] userString = new String[]{
                    user1.getId().toString(),
                    user1.getRol().getNombre(),
                    (user1.getFallas() != null) ? user1.getFallas().toString() : null,
                    (user1.getMarcas() != null) ? user1.getMarcas().toString() : null,
            };
            // Add row
            addDataRow(userString, alterBackgroundColor, user1.isBloqueado(), i);
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

            // If a row is selected
            if (selectedRowIndex != -1) {
                // Set unselect the row
                TableRow selectedRow = (TableRow) userTable.getChildAt(selectedRowIndex);
                selectedRow.setBackgroundColor(selectedRowWasWhite ? ContextCompat.getColor(getContext(), R.color.white) :
                        ContextCompat.getColor(getContext(), androidx.cardview.R.color.cardview_dark_background));

                // If the selected row was this one
                if (selectedRowIndex == rowIndex)  {
                    selectedRowIndex = -1;
                    return;
                }
            }

            // Change bg of newly selected row
            selectedRowIndex = rowIndex;
            selectedRowWasWhite = isWhite;
            row.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.teal_700));
        });

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

        // Handle switch toggle events
        userSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Get the parent TableRow of this Switch and call handler
            TableRow parentRow = (TableRow) buttonView.getParent();
            handleSwitchToggle(parentRow, isChecked, userSwitch);
        });

        row.addView(userSwitch);
    }

    private void handleSwitchToggle(TableRow row, boolean isChecked, Switch userSwitch) {
        TextView idTV = (TextView) row.getChildAt(0);
        TextView fallasTV = (TextView) row.getChildAt(2);
        TextView marcasTV = (TextView) row.getChildAt(3);

        int id = Integer.parseInt(idTV.getText().toString());

        if (!usersViewModel.updateUserBlock(id, isChecked)) {
            Toast.makeText(getContext(), "No se ha podido actualizar el bloqueo del usuario", Toast.LENGTH_SHORT).show();
            userSwitch.setChecked(!isChecked);
        }
        else {
            fallasTV.setText("0");
            marcasTV.setText("0");
        }
    }

    private void resetPassword() {
        if (selectedRowIndex == -1) {
            Toast.makeText(getContext(), "No se ha seleccionado un usuario", Toast.LENGTH_SHORT).show();
            return;
        }
        TableRow row = (TableRow) userTable.getChildAt(selectedRowIndex);
        TextView idTV = (TextView) row.getChildAt(0);
        int id = Integer.parseInt(idTV.getText().toString());

        if (usersViewModel.resetPassword(id)) {
            Toast.makeText(getContext(), "Contraseña blanqueada exitosamente", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(getContext(), "No se ha podido blanquear la contraseña del usuario seleccionado", Toast.LENGTH_SHORT).show();
        }
    }
}