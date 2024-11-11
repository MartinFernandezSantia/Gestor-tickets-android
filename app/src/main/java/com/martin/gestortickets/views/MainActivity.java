package com.martin.gestortickets.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.martin.gestortickets.R;
import com.martin.gestortickets.databinding.ActivityMainBinding;
import com.martin.gestortickets.entities.Usuario;
import com.martin.gestortickets.ui.add_ticket.AddTicketFragment;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;

    private Usuario user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_users, R.id.nav_tickets_admin, R.id.nav_tickets_tecnico, R.id.nav_tickets_trabajador, R.id.nav_tickets_pendientes)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        // MY CODE
        // Retrieve transferred data through intent
        Intent intent = getIntent();
        user = (Usuario) intent.getSerializableExtra("user");

        Bundle bundle = new Bundle();
        bundle.putSerializable("user", user);

        // Handle the selection of the menu item to navigate to AddTicketFragment
        navigationView.setNavigationItemSelectedListener(item -> {

            if (R.id.nav_add_ticket == item.getItemId()) {
                // Instead of navigating directly, navigate via NavController with the argument
                navController.navigate(R.id.nav_add_ticket, bundle);
            }
            else if (R.id.nav_users == item.getItemId()) {
                navController.navigate(R.id.nav_users, bundle);
            }
            else if (R.id.nav_home == item.getItemId()) {
                navController.navigate(R.id.nav_home);
            }
            else if (R.id.nav_tickets_pendientes == item.getItemId()) {
                navController.navigate(R.id.nav_tickets_pendientes, bundle);
            }
            else if (R.id.nav_tickets_admin == item.getItemId()) {
                navController.navigate(R.id.nav_tickets_admin, bundle);
            }
            else  if (R.id.nav_tickets_tecnico == item.getItemId()) {
                navController.navigate(R.id.nav_tickets_tecnico, bundle);
            }
            else  if (R.id.nav_add_user == item.getItemId()) {
                navController.navigate(R.id.nav_add_user, bundle);
            }
            else  if (R.id.nav_tickets_trabajador == item.getItemId()) {
                navController.navigate(R.id.nav_tickets_trabajador, bundle);
            }

            drawer.closeDrawers(); // Close the drawer after selection
            return true; // Indicate that the item was handled
        });

        // If user is Tecnico fill marcas and fallas TextViews in nav_headers_main
        if (user.getRol().getId() == 2) {
            NavigationView navigationView1 = findViewById(R.id.nav_view);
            View headerView = navigationView.getHeaderView(0);

            TextView fallasTextView = headerView.findViewById(R.id.fallas);
            TextView marcasTextView = headerView.findViewById(R.id.marcas);

            fallasTextView.setText("Fallas: " + user.getFallas());
            marcasTextView.setText("Marcas: " + user.getMarcas());
        }

        // Hide menu items based on user's role
        Menu navMenu = navigationView.getMenu();
        switch (user.getRol().getId()) {
            case 1:
                navMenu.findItem(R.id.nav_tickets_pendientes).setVisible(false);
                navMenu.findItem(R.id.nav_tickets_tecnico).setVisible(false);
                navMenu.findItem(R.id.nav_tickets_trabajador).setVisible(false);
                navMenu.findItem(R.id.nav_add_ticket).setVisible(false);
                break;

            case 2:
                navMenu.findItem(R.id.nav_add_ticket).setVisible(false);
                navMenu.findItem(R.id.nav_add_user).setVisible(false);
                navMenu.findItem(R.id.nav_tickets_trabajador).setVisible(false);
                navMenu.findItem(R.id.nav_tickets_admin).setVisible(false);
                navMenu.findItem(R.id.nav_users).setVisible(false);
                break;

            case 3:
                navMenu.findItem(R.id.nav_add_user).setVisible(false);
                navMenu.findItem(R.id.nav_tickets_admin).setVisible(false);
                navMenu.findItem(R.id.nav_users).setVisible(false);
                navMenu.findItem(R.id.nav_tickets_pendientes).setVisible(false);
                navMenu.findItem(R.id.nav_tickets_tecnico).setVisible(false);
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.logout) {
            setResult(RESULT_CANCELED);
            finish();
            return true;
        }
        else if (id == R.id.changePass) {
            setResult(RESULT_FIRST_USER);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}