package com.example.parkingappv01.ui.registracija;

import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.parkingappv01.MainActivity;
import com.example.parkingappv01.R;
import com.example.parkingappv01.ui.pocetna.PocetnaFragment;
import com.example.parkingappv01.ui.prijava.PrijavaFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil;

import java.util.HashMap;
import java.util.Map;

public class RegistracijaFragment extends Fragment {
    TextInputLayout mIme, mPrezime, mEmail, mLozinka, mPonLozinka;
    Button btnRegSe, btnRegPri;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userID;

    private RegistracijaViewModel mViewModel;

    public static RegistracijaFragment newInstance() {

        return new RegistracijaFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        ((MainActivity) getActivity()).lockDrawer();
        View v = inflater.inflate(R.layout.registracija_fragment, container, false);

        mIme = v.findViewById(R.id.ime);
        mPrezime = v.findViewById(R.id.prezime);
        mEmail = v.findViewById(R.id.email);
        mLozinka = v.findViewById(R.id.lozinka);
        mPonLozinka = v.findViewById(R.id.ponlozinka);
        fAuth=FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        Button btnRegSe = (Button) v.findViewById(R.id.btn_reg_se);
        btnRegSe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logIn(v);
                UIUtil.hideKeyboard(getActivity());

            }
        });
        Button btnRegPri = (Button) v.findViewById(R.id.btn_reg_pri);
        btnRegPri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.container, PrijavaFragment.newInstance());
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                UIUtil.hideKeyboard(getActivity());
            }
        });

        return v;
    }
    private boolean validateIme(){
        String ime = mIme.getEditText().getText().toString().trim();
        if(ime.isEmpty()){
            mIme.setError("Unesite Ime");
            return false;
        }else{
            mIme.setError(null);
            return true;
        }
    }
    private boolean validatePrezime(){
        String prezime = mPrezime.getEditText().getText().toString().trim();
        if(prezime.isEmpty()){
            mPrezime.setError("Unesite email");
            return false;
        }else{
            mPrezime.setError(null);
            return true;
        }
    }
    private boolean validateEmail(){
        String email = mEmail.getEditText().getText().toString().trim();
        if(email.isEmpty()){
            mEmail.setError("Unesite email");
            return false;
        }else{
            mEmail.setError(null);
            return true;
        }
    }
    private boolean validateLozinka(){
        String lozinka = mLozinka.getEditText().getText().toString().trim();
        if(lozinka.isEmpty()){
            mLozinka.setError("Unesite lozinku!");
            return false;
        }
        if(lozinka.length()<8){
            mLozinka.setError("Lozinka mora biti duža od 8 znakova");
            return false;
        }
        else{
            mLozinka.setError(null);
            return true;
        }
    }
    private boolean validatePonLozinka(){
        String ponlozinka = mPonLozinka.getEditText().getText().toString().trim();
        String lozinka = mLozinka.getEditText().getText().toString().trim();
        if(ponlozinka.isEmpty()){
            mPonLozinka.setError("Potvrdite lozinku!");
            return false;
        }
        if(!ponlozinka.equals(lozinka)){
            mPonLozinka.setError("Ponovljena lozinka nije identična lozinki");
            return false;
        }
        else{
            mLozinka.setError(null);
            return true;
        }
    }
    public void logIn (View v){
        if(!validateIme()
                | !validatePrezime()
                | !validateEmail()
                | !validateLozinka()
                | !validatePonLozinka()){
            return;
        }

        final String ime = mIme.getEditText().getText().toString().trim();
        final String prezime = mPrezime.getEditText().getText().toString().trim();
        final String email = mEmail.getEditText().getText().toString().trim();
        final String lozinka = mLozinka.getEditText().getText().toString().trim();

        fAuth.createUserWithEmailAndPassword(email,lozinka).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(getActivity(),"Korisnik kreiran", Toast.LENGTH_SHORT).show();
                    userID = fAuth.getCurrentUser().getUid();
                    DocumentReference documentReference = fStore.collection("korisnici").document(userID);
                    Map<String,Object> korisnik = new HashMap<>();
                    korisnik.put("id",userID);
                    korisnik.put("ime",ime);
                    korisnik.put("prezime",prezime);
                    korisnik.put("email",email);
                    korisnik.put("kredit", "0");
                    documentReference.set(korisnik).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d("TAG","onSuccess: Profil je kreiran za korisnika: "+userID );
                        }
                    });


                    FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.container, PrijavaFragment.newInstance());
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();

                } else {
                    Toast.makeText(getActivity(), "Greška!"+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(RegistracijaViewModel.class);
        // TODO: Use the ViewModel

    }


}