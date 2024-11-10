package com.martin.gestortickets;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.martin.gestortickets.db.DatabaseHelper;
import com.martin.gestortickets.entities.Usuario;
import com.martin.gestortickets.views.ChangePasswActivity;
import com.martin.gestortickets.views.LoginActivity;
import com.martin.gestortickets.views.MainActivity;

public class ControllerActivity extends AppCompatActivity {
    private DatabaseHelper dbHelper;
    private ActivityResultLauncher<Intent> loginLauncher;
    private ActivityResultLauncher<Intent> changePassLauncher;
    private ActivityResultLauncher<Intent> mainLauncher;
    private Usuario user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // GENERATED CODE
        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
//        setContentView(R.layout.activity_main);
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });

        // MY CODE
        loginLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                this::handleLoginResult
        );
        changePassLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                this::handleChangePass
        );
        mainLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                this::handleMainResult
        );

        launchLoginActivity();
    }

    private void launchLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        loginLauncher.launch(intent);
    }

    private void launchChangePassActivity(int userID) {
        Intent intent = new Intent(this, ChangePasswActivity.class);
        intent.putExtra("userID", userID);
        changePassLauncher.launch(intent);
    }

    private void launchMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("user", this.user);
        mainLauncher.launch(intent);
    }

    private void handleLoginResult(ActivityResult result) {
        // If user must change their password
        if (result.getResultCode() == RESULT_FIRST_USER && result.getData() != null) {
            Intent data = result.getData();
            int userID = data.getIntExtra("userID", -1);

            launchChangePassActivity(userID);
        }
        else if (result.getResultCode() == RESULT_OK && result.getData() != null) {
            Intent data = result.getData();
            this.user = (Usuario) data.getSerializableExtra("user");
            launchMainActivity();
        }

    }

    private void handleChangePass(ActivityResult result) {
        if (result.getResultCode() == RESULT_OK) {
            launchLoginActivity();
        }
        else {
            finish();
        }
    }

    private void handleMainResult(ActivityResult result) {
        // If logout
        if (result.getResultCode() == RESULT_CANCELED) {
            this.user = null;
            launchLoginActivity();
        }
        else if (result.getResultCode() == RESULT_FIRST_USER) { // Change Password
            launchChangePassActivity(this.user.getId());
        }
    }
}