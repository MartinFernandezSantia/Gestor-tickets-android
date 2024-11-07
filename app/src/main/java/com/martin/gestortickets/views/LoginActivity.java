package com.martin.gestortickets.views;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.martin.gestortickets.R;
import com.martin.gestortickets.entities.Usuario;
import com.martin.gestortickets.utils.Auth;

import java.util.Optional;

public class LoginActivity extends AppCompatActivity {
    private EditText idET;
    private EditText passwordET;
    private Button loginBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        idET = findViewById(R.id.id);
        passwordET = findViewById(R.id.password);
        loginBtn = findViewById(R.id.loginButton);

        loginBtn.setOnClickListener(v -> authenticate());
    }

    private void authenticate() {
        try {
            int id = Integer.parseInt(idET.getText().toString());
            String passw = passwordET.getText().toString();

            Optional<Usuario> user = Auth.login(this, id, passw);

            // Si no hay usuario
            if (!user.isPresent()) {
                Toast.makeText(this, "Id y/o contraseña incorrectos", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent resultIntent = new Intent();

            // Si ID y contraseña son iguales
            if (passw.equals(String.valueOf(id))) {
                resultIntent.putExtra("userID", user.get().getId());
                setResult(RESULT_FIRST_USER, resultIntent);
                finish();
                return;
            }

            resultIntent.putExtra("user", user.get());
            setResult(RESULT_OK, resultIntent);
            finish();
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Los tipos de datos son incorrectos", Toast.LENGTH_SHORT).show();
        }
    }
}