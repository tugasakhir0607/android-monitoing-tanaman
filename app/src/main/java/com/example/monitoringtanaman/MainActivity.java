package com.example.monitoringtanaman;

import android.content.Intent;
import android.os.Bundle;

import com.example.monitoringtanaman.config.SharePreference;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.view.MenuItem;
import android.view.View;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.google.android.material.navigation.NavigationView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.widget.TextView;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    private String nama, email;
    private TextView tv_nama, tv_email;
    private SharePreference sharePreference;
    private AppBarConfiguration mAppBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sharePreference = new SharePreference(this);
        if (sharePreference.isLoggedIn() == false) {
            Intent intent = new Intent(this, SignInActivity.class);
            startActivity(intent);
            finish();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_beranda, R.id.nav_profil, R.id.nav_informasi, R.id.nav_keluar)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        MenuItem navLogOut = navigationView.getMenu().findItem(R.id.nav_keluar);
        navLogOut.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                sharePreference.logoutUser();
                return false;
            }
        });

        View header = navigationView.getHeaderView(0);
        tv_nama = (TextView)header.findViewById(R.id.tv_nama);
        tv_email = (TextView)header.findViewById(R.id.tv_email);
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main, menu);
//        return true;
//    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration) || super.onSupportNavigateUp();
    }

    @Override
    protected void onResume() {
        super.onResume();

        HashMap<String, String> user = sharePreference.getUserDetails();
        nama = user.get(sharePreference.KEY_NAMA_PENGGUNA);
        email = user.get(sharePreference.KEY_EMAIL_PENGGUNA);

        tv_nama.setText(nama);
        tv_email.setText(email);
    }
}
