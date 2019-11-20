package com.example.parkingappv01;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.parkingappv01.ui.main.MainFragment;
import com.example.parkingappv01.ui.pocetna.PocetnaFragment;
import com.example.parkingappv01.ui.prijava.PrijavaFragment;
import com.example.parkingappv01.ui.registracija.RegistracijaFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, MainFragment.newInstance())
                    .commitNow();
        }
    }

}
