package com.example.parkingappv01.ui.prijava;

import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

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
import android.widget.TextView;
import android.widget.Toast;

import com.example.parkingappv01.R;
import com.example.parkingappv01.ui.pocetna.PocetnaFragment;
import com.example.parkingappv01.ui.registracija.RegistracijaFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class PrijavaFragment extends Fragment {
    EditText mEmail, mLozinka;
    Button btnRegSe, btnRegPri;
    FirebaseAuth fAuth;

    private PrijavaViewModel mViewModel;

    public static PrijavaFragment newInstance() {

        return new PrijavaFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.prijava_fragment, container, false);

        mEmail = v.findViewById(R.id.email);
        mLozinka = v.findViewById(R.id.lozinka);
        fAuth=FirebaseAuth.getInstance();

        Button btnPrijavise = (Button) v.findViewById(R.id.btn_pri_se);
        btnPrijavise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String email = mEmail.getText().toString().trim();
                String lozinka = mLozinka.getText().toString().trim();

                if(TextUtils.isEmpty(email)){
                    mEmail.setError("Unesite email");
                    return;
                }
                if(TextUtils.isEmpty(lozinka)){
                    mLozinka.setError("Unesite lozinku");
                    return;
                }
                if(lozinka.length()<8){
                    mLozinka.setError("Lozinka mora biti duža od 8 znakova");
                    return;
                }

                fAuth.signInWithEmailAndPassword(email,lozinka).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(getActivity(),"Dobrodošli! "+email, Toast.LENGTH_SHORT).show();

                            FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                            fragmentTransaction.replace(R.id.container, PocetnaFragment.newInstance());
                            fragmentTransaction.addToBackStack(null);
                            fragmentTransaction.commit();

                        } else {
                            Toast.makeText(getActivity(), "Greška!"+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        Button btnPriReg = (Button) v.findViewById(R.id.btn_pri_reg);
        btnPriReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.container, RegistracijaFragment.newInstance());
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(PrijavaViewModel.class);
        // TODO: Use the ViewModel

    }


}
