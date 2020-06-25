package com.example.parkingappv01.ui.profil;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.parkingappv01.MainActivity;
import com.example.parkingappv01.R;
import com.example.parkingappv01.ui.uplata.UplataViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;

import static java.lang.System.in;

public class ProfilFragment extends Fragment {
    FirebaseFirestore fStore;
    FirebaseAuth fAuth;
    TextView mIip, mRasi, mBpon, mIpon, mBrez, mIrez;

    private ProfilViewModel mViewModel;


    public static ProfilFragment newInstance() {
        return new ProfilFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable final Bundle savedInstanceState) {
        ((MainActivity) getActivity()).unlockDrawer();
        final View v = inflater.inflate(R.layout.profil_fragment, container, false);
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        mIip = v.findViewById(R.id.piip);
        mRasi = v.findViewById(R.id.pkredit1);
        mBpon = v.findViewById(R.id.pbp1);
        mIpon = v.findViewById(R.id.pip1);
        mBrez = v.findViewById(R.id.pbr1);
        mIrez = v.findViewById(R.id.pir1);
        final DecimalFormat df = new DecimalFormat("###0.00");
        final DocumentReference doc = fStore.collection("korisnici").document(fAuth.getUid());
        doc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        mIip.setText(document.getString("ime")+" "+document.getString("prezime"));
                        mRasi.setText(df.format(Double.valueOf(document.getString("kredit"))));
                    }
                }
            }
        });

        CollectionReference collection1 = fStore.collection("parkirna_mjesta");
        Query rezQuery = collection1.whereEqualTo("kreator_id", fAuth.getUid());
        rezQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            int i = 0;
            String Pid;
            @Override
            public void onComplete(@NonNull final Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Pid = document.getString("parking_id");

                        i++;
                    }
                    mBpon.setText(String.valueOf(i));

                }
            }
        });
        CollectionReference colRef1 = fStore.collection("parkirna_mjesta");
        colRef1.whereEqualTo("kreator_id", fAuth.getUid()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String Pid = document.getString("parking_id");
                        getProvjeri(Pid);
                    }
                }
            }
        });

        CollectionReference collection = fStore.collection("rezervacije");
        Query rezervacijeQuery = collection.whereEqualTo("kreator_id", fAuth.getUid());
        rezervacijeQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            int i = 0;
            double trosak = 0;
            @Override
            public void onComplete(@NonNull final Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        trosak= trosak+Double.valueOf(document.getString("iznos"));
                        i++;

                    }
                    mIrez.setText((df.format(trosak)));
                    mBrez.setText(String.valueOf(i));

                }
            }
        });
        return v;

    }
    public void getProvjeri(String Pid){
        final DecimalFormat df = new DecimalFormat("###0.00");
        final CollectionReference collection = fStore.collection("rezervacije");
        Query rezervacijeQuery = collection.whereEqualTo("parking_id", Pid);
        rezervacijeQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull final Task<QuerySnapshot> task) {
                double zarada = 0;
                if(task.isSuccessful()){
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        zarada = zarada+Double.valueOf(document.getString("iznos"));
                        }
                    setZarada(zarada);
                }
            }
        });
    }
    public void setZarada(double zarada){
        final DecimalFormat df = new DecimalFormat("###0.00");
        double z1 = Double.valueOf(mIpon.getText().toString());
        double z2 = zarada;
        double ukupno = z2+z1;
        mIpon.setText((df.format(ukupno)));



    }

        @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // TODO: Use the ViewModel
    }
}

