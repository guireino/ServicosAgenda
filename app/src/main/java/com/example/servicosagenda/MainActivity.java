package com.example.servicosagenda;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import android.util.Log;

import com.example.servicosagenda.fragment.FragmentCalendario;
import com.example.servicosagenda.fragment.FragmentHome;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.annotations.Nullable;


public class MainActivity extends AppCompatActivity {

    private BottomNavigationView btmNavigationView;
    private BottomNavigationView.OnNavigationItemSelectedListener onNavigationItemSelectedListener;

    private FragmentHome fragmentHome;
    private FragmentCalendario fragmentCalendario;

    private Fragment fragment;
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btmNavigationView = findViewById(R.id.bottomNavigationView);

        navigationBtm();

        fragmentHome = new FragmentHome();
        fragmentCalendario = new FragmentCalendario();

        fragmentManager = getSupportFragmentManager();

        // instanciando frament no mainactivity
        fragmentManager.beginTransaction().replace(R.id.frameLayout_Fragment, fragmentHome).commit();
    }

    private void navigationBtm(){

        // fazendo click funcionar navegacao botao
        onNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@Nullable MenuItem item) {

                switch (item.getItemId()){

                    case R.id.item_navegacao_home:
                        fragment = fragmentHome;
                        Log.d("fragment home", "fragment " + fragmentHome);
                    break;

                    case R.id.item_navegacao_calendario:
                        fragment = fragmentCalendario;
                        Log.d("fragmentCalendario ", "fragmentCalendario " + fragmentCalendario);
                    break;
                }

                // setando qual frameLayout que estiver ativo
                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout_Fragment, fragment).commit();

                return true;
            }
        };

        btmNavigationView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener);
    }
}