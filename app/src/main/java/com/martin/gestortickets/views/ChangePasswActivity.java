package com.martin.gestortickets.views;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.martin.gestortickets.R;
import com.martin.gestortickets.dao.UsuarioDAO;

public class ChangePasswActivity extends AppCompatActivity {
    private EditText currPassET;
    private EditText newPass1ET;
    private EditText newPass2ET;
    private Button confirmBtn;

    private UsuarioDAO usuarioDAO;

    private int userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_change_passw);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        currPassET = findViewById(R.id.currPass);
        newPass1ET = findViewById(R.id.newPass1);
        newPass2ET = findViewById(R.id.newPass2);
        confirmBtn = findViewById(R.id.confirmBtn);

        confirmBtn.setOnClickListener(v -> changePassword());

        usuarioDAO = new UsuarioDAO(this);

        // Retrieve transferred data through intent
        Intent intent = getIntent();
        userID = intent.getIntExtra("userID", -1);
    }

    private void changePassword() {
        String currPass = currPassET.getText().toString();
        String newPass1 = newPass1ET.getText().toString();
        String newPass2 = newPass2ET.getText().toString();

        if (!newPass1.equals(newPass2)) {
            Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!usuarioDAO.updatePassword(userID, currPass, newPass1)) {
            Toast.makeText(this, "No se ha podido modificar la contraseña", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast toast = Toast.makeText(this, "Se ha modificado la contraseña exitosamente", Toast.LENGTH_SHORT);
        Intent resultIntent = new Intent();
        setResult(RESULT_OK, resultIntent);
        finish();
    }
}