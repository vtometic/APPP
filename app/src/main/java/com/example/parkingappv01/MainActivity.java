package com.example.parkingappv01;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.parkingappv01.ui.main.MainFragment;
import com.example.parkingappv01.ui.pocetna.PocetnaFragment;
import com.example.parkingappv01.ui.ponude.PonudeFragment;
import com.example.parkingappv01.ui.ponudi1.Ponudi1Fragment;
import com.example.parkingappv01.ui.prijava.PrijavaFragment;
import com.example.parkingappv01.ui.profil.ProfilFragment;
import com.example.parkingappv01.ui.pronadi1.Pronadi1Fragment;
import com.example.parkingappv01.ui.registracija.RegistracijaFragment;
import com.example.parkingappv01.ui.rezervacije.RezervacijeFragment;
import com.example.parkingappv01.ui.uplata.UplataFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import static androidx.constraintlayout.widget.Constraints.TAG;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    ActionBarDrawerToggle toogle;
    FirebaseAuth fAuth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);
        fAuth=FirebaseAuth.getInstance();

        setSupportActionBar(toolbar);

        navigationView.bringToFront();
        toogle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.otvori, R.string.zatvori);
        drawerLayout.addDrawerListener(toogle);
        toogle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, MainFragment.newInstance())
                .commitNow();



    }


    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

            switch (menuItem.getItemId()){
                case R.id.nav_pocetna:
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.container, PocetnaFragment.newInstance())
                            .commitNow();
                    break;
                case R.id.nav_ponude:
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.container, PonudeFragment.newInstance())
                            .commitNow();
                    break;
                case R.id.nav_ponudi:
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.container, Ponudi1Fragment.newInstance())
                            .commitNow();
                    break;
                case R.id.nav_pretraga:
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.container, Pronadi1Fragment.newInstance())
                            .commitNow();
                    break;
                case R.id.nav_profil:
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.container, ProfilFragment.newInstance())
                            .commitNow();
                    break;
                case R.id.nav_rezervacije:
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.container, RezervacijeFragment.newInstance())
                            .commitNow();
                    break;
                case R.id.nav_uplata:
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.container, UplataFragment.newInstance())
                            .commitNow();
                    break;
                case R.id.nav_odjava:
                    fAuth.getInstance().signOut();
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.container, PrijavaFragment.newInstance())
                            .commitNow();
                    break;
            }
            drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
    public void lockDrawer() {
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        getSupportActionBar().hide();
    }


    public void unlockDrawer() {
        UpdateNavHeader();
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        getSupportActionBar().show();
    }

    public void UpdateNavHeader(){

        FirebaseFirestore fStore = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final String currentUser = user.getUid();

       NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        final TextView navUserName = headerView.findViewById(R.id.headerName);
        final TextView navUserMail = headerView.findViewById(R.id.headerMail);
        ImageView navUserPhoto = headerView.findViewById(R.id.headerPhoto);

        fStore.collection("korisnici")
                .whereEqualTo("id",currentUser)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {

                                navUserName.setText((CharSequence) document.get("ime")+" "+document.get("prezime"));
                                navUserMail.setText((CharSequence) document.get("email"));


                            }
                        }
                    }
                });
    }

}
