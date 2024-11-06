package com.martin.gestortickets;

import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.martin.gestortickets.dao.RolDAO;
import com.martin.gestortickets.db.DatabaseHelper;
import com.martin.gestortickets.entities.Rol;

import java.util.Optional;

public class MainActivity extends AppCompatActivity {
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // GENERATED CODE
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // MY CODE
        dbHelper = new DatabaseHelper(this);
        RolDAO rolDAO = new RolDAO(this);

        for (Rol rol : rolDAO.getAll()) {
            Log.i("ROL", rol.getNombre());
        }
    }
}