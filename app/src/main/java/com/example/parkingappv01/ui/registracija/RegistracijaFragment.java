package com.example.parkingappv01.ui.registracija;

import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
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

import com.example.parkingappv01.R;
import com.example.parkingappv01.ui.pocetna.PocetnaFragment;
import com.example.parkingappv01.ui.prijava.PrijavaFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegistracijaFragment extends Fragment {
    EditText mIme, mPrezime, mEmail, mLozinka, mPonLozinka;
    Button btnRegSe, btnRegPri;
    FirebaseAuth fAuth;

    private RegistracijaViewModel mViewModel;

    public static RegistracijaFragment newInstance() {

        return new RegistracijaFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.registracija_fragment, container, false);

        mIme = v.findViewById(R.id.editText3);
        mPrezime = v.findViewById(R.id.editText4);
        mEmail = v.findViewById(R.id.email);
        mLozinka = v.findViewById(R.id.lozinka);
        mPonLozinka = v.findViewById(R.id.ponlozinka);

        fAuth=FirebaseAuth.getInstance();

        /*if(fAuth.getCurrentUser() != null){

            FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.container, PocetnaFragment.newInstance());
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }*/

        Button btnRegSe = (Button) v.findViewById(R.id.btn_reg_se);
        btnRegSe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ime = mIme.getText().toString().trim();
                String prezime = mPrezime.getText().toString().trim();
                String email = mEmail.getText().toString().trim();
                String lozinka = mLozinka.getText().toString().trim();
                String ponlozinka = mPonLozinka.getText().toString().trim();

                if(TextUtils.isEmpty(ime)){
                    mIme.setError("Unesite ime");
                    return;
                }
                if(TextUtils.isEmpty(prezime)){
                    mPrezime.setError("Unesite prezime");
                    return;
                }
                if(TextUtils.isEmpty(email)){
                    mEmail.setError("Unesite email");
                    return;
                }
                if(TextUtils.isEmpty(lozinka)){
                    mLozinka.setError("Unesite lozinku");
                    return;
                }
                if(TextUtils.isEmpty(ponlozinka)){
                    mPonLozinka.setError("Potvrdite lozinku");
                    return;
                }
                if(lozinka.length()<8){
                    mLozinka.setError("Lozinka mora biti duža od 8 znakova");
                    return;
                }
                if(!ponlozinka.equals(lozinka)){
                    mPonLozinka.setError("Ponovljena lozinka nije identična lozinki");
                    return;
                }

                fAuth.createUserWithEmailAndPassword(email,lozinka).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(getActivity(),"Korisnik kreiran", Toast.LENGTH_SHORT).show();

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
        });
        Button btnRegPri = (Button) v.findViewById(R.id.btn_reg_pri);
        btnRegPri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.container, PrijavaFragment.newInstance());
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(RegistracijaViewModel.class);
        // TODO: Use the ViewModel

    }


}