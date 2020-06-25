package com.example.parkingappv01.ui.uplata;

import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.parkingappv01.MainActivity;
import com.example.parkingappv01.R;
import com.example.parkingappv01.ui.pocetna.PocetnaFragment;
import com.example.parkingappv01.ui.ponudi1.Ponudi1Fragment;
import com.example.parkingappv01.ui.profil.ProfilFragment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.android.volley.VolleyLog.TAG;
import static java.util.logging.Logger.global;

public class UplataFragment extends Fragment {
    FirebaseFirestore fStore;
    FirebaseAuth fAuth;
    TextInputLayout mCardNr, mImeIPrez, mDatumIsteka, mControlNr, mIznosUplate;
    private UplataViewModel mViewModel;

    public static UplataFragment newInstance() {
        return new UplataFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        ((MainActivity) getActivity()).lockDrawer();
        View v = inflater.inflate(R.layout.uplata_fragment, container, false);
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        mCardNr = (TextInputLayout) v.findViewById(R.id.crdnr);
        mCardNr.getEditText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardNr();
            }
        });
        mImeIPrez = (TextInputLayout) v.findViewById(R.id.iip);
        mImeIPrez.getEditText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iip();
            }
        });
        mDatumIsteka = v.findViewById(R.id.datis);
        mDatumIsteka.getEditText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datIs();
            }
        });
        mControlNr = v.findViewById(R.id.controlnr);
        mControlNr.getEditText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cvv();
            }
        });
        mIznosUplate = v.findViewById(R.id.iznos);
        mIznosUplate.getEditText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iznosUplate();
            }
        });


        Button mBtnOdustani = v.findViewById(R.id.btnOdustani);
        mBtnOdustani.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.container, PocetnaFragment.newInstance());
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
        Button mBtnUplati = v.findViewById(R.id.btnUplati);
        mBtnUplati.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!cardNr()
                        | !iip()
                        | !cvv()
                        | !iznosUplate()
                        | !datIs()){
                    return;
                }

                final DocumentReference user = fStore.collection("korisnici").document(fAuth.getUid());
                user.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String noviIznos = izracun(documentSnapshot.getString("kredit"), mIznosUplate.getEditText().getText().toString().trim());
                        user
                                .update("kredit", noviIznos)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(getActivity(),"Transakcija provedena", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getActivity(),"Greška, pokušajte ponovo", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                });




                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.container, ProfilFragment.newInstance());
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        return v;
    }
    private boolean cardNr(){
        String cardnr = mCardNr.getEditText().getText().toString().trim();
        if(cardnr.isEmpty()){
            mCardNr.setError("Unesite broj kartice");
            return false;
        }
        if(cardnr.length()!=16){
            mCardNr.setError("Broj mora sadržavati točno 16 znamenaka ");
            return false;
        }
        else{
            mCardNr.setError(null);
            return true;
        }
    }
    private boolean iip(){
        String iip = mImeIPrez.getEditText().getText().toString();
        if(iip.isEmpty()){
            mImeIPrez.setError("Unesite ime i prezime");
            return false;
        }
        else{
            mImeIPrez.setError(null);
            return true;
        }
    }
    private boolean datIs(){
        String datis = mDatumIsteka.getEditText().getText().toString();
        SimpleDateFormat simpleDate = new SimpleDateFormat("MM/yy");
        try {
            Date d = simpleDate.parse(datis);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if(datis.isEmpty()){
            mDatumIsteka.setError("Datum isteka");
            return false;
        }
        else{
            mDatumIsteka.setError(null);
            return true;
        }
    }
    private boolean cvv(){
        String cvv = mControlNr.getEditText().getText().toString().trim();
        if(cvv.isEmpty()){
            mControlNr.setError("Unesite kontrolni broj");
            return false;
        }
        if(cvv.length() !=3 ){
            mControlNr.setError("Neispravan kontolni broj ");
            return false;
        }
        else{
            mControlNr.setError(null);
            return true;
        }
    }
    private boolean iznosUplate(){
        String iznos = mIznosUplate.getEditText().getText().toString().trim();
        if(iznos.isEmpty()){
            mIznosUplate.setError("Unesite iznos koji želite uplatiti");
            return false;
        }else{
            mIznosUplate.setError(null);
            return true;
        }
    }
    private String izracun(String stariIznos, String iznosUplate){

        double i1 = Double.valueOf(iznosUplate);
        double i2 = Double.valueOf(stariIznos);
        double ukupno1 = i1+i2;
        return String.valueOf(ukupno1);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(UplataViewModel.class);
        // TODO: Use the ViewModel
    }

}
