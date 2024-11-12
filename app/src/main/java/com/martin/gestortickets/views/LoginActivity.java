package com.martin.gestortickets.views;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.martin.gestortickets.R;
import com.martin.gestortickets.dao.NotificacionDAO;
import com.martin.gestortickets.dao.UsuarioDAO;
import com.martin.gestortickets.entities.Notificacion;
import com.martin.gestortickets.entities.Usuario;
import com.martin.gestortickets.utils.Auth;

import java.util.Optional;

public class LoginActivity extends AppCompatActivity {
    private EditText idET;
    private EditText passwordET;
    private Button loginBtn;
    private ImageButton unlockBtn;

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
        unlockBtn = findViewById(R.id.unlockBtn);

        loginBtn.setOnClickListener(v -> authenticate());
        unlockBtn.setOnClickListener(v -> requestUnlock());
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

            // If user is blocked
            if (user.get().isBloqueado()) {
                Toast.makeText(this, "Lo sentimos, pero actualmente te encuentras bloqueado. Solicita el desbloqueo de tu cuenta al administrador para poder continuar", Toast.LENGTH_LONG).show();
                return;
            }

//            Intent resultIntent = new Intent();

            // Si ID y contraseña son iguales fuerzo el cambio de contraseña
            if (passw.equals(String.valueOf(id))) {
                Intent intent = new Intent(LoginActivity.this, ChangePasswActivity.class);
                intent.putExtra("userID", user.get().getId());
                startActivity(intent);
                return;
            }

            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.putExtra("user", user.get());
            startActivity(intent);
            finish();
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Los tipos de datos son incorrectos", Toast.LENGTH_SHORT).show();
        }
    }

    private void requestUnlock() {
        try {
            int id = Integer.parseInt(idET.getText().toString());

            NotificacionDAO notificacionDAO = new NotificacionDAO(this);
            UsuarioDAO usuarioDAO = new UsuarioDAO(this);
            Optional<Usuario> usuario = usuarioDAO.getByID(id);

            if (!usuario.isPresent()) {
                Toast.makeText(this, "No existe un usuario con el ID proporcionado", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!usuario.get().isBloqueado()) {
                Toast.makeText(this, "Usted no se encuentra bloqueado en este momento", Toast.LENGTH_SHORT).show();
                return;
            }

            String message = "Nueva solicitud de desbloqueo: ID n° " + id;
            Notificacion notificacion = new Notificacion(message, usuario.get());
            int created = notificacionDAO.create(notificacion);

            switch (created) {
                case 1:
                    Toast.makeText(this, "Se ha enviado una solicitud de desbloqueo al administrador", Toast.LENGTH_SHORT).show();
                    break;
                case -1:
                    Toast.makeText(this, "La solicitud ya fue enviada, porfavor espere hasta que su cuenta sea desbloqueada", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    Toast.makeText(this, "No se ha podido enviar una solicitud de desbloqueo al administrado", Toast.LENGTH_SHORT).show();
                    break;
            }

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Los tipos de datos son incorrectos", Toast.LENGTH_SHORT).show();
        }
    }
}