package com.example.b2bouz;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class  FragmentKapsayici extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Layout tanımlama
        setContentView(R.layout.fragmentkapsayici);

        bottomNavigationView = findViewById(R.id.navigation);
        Bundle intent = getIntent().getExtras();

        getSupportFragmentManager().beginTransaction().replace(R.id.cercevekapsayici, new AnasayfaFragment()).commit();

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                switch (item.getItemId()) {
                    case R.id.navigation_add:
                        selectedFragment = new GonderiYuklemeFragment();
                        break;
                    case R.id.navigation_profile:
                        ProfileFragment profileFragment = new ProfileFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString("userId", userId);
                        profileFragment.setArguments(bundle);
                        selectedFragment = profileFragment;
                        break;
                    case R.id.navigation_map:
                        selectedFragment = new AnasayfaFragment();
                        break;
                }

                // Null kontrolü yapın
                if (selectedFragment != null) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.cercevekapsayici, selectedFragment).commit();
                    return true;
                } else {
                    return false; // Null ise işlemi iptal edin veya bir hata durumu ele alın
                }
            }
        });


    }
}
