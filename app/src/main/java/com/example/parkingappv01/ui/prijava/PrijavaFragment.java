package com.example.parkingappv01.ui.prijava;

import androidx.appcompat.widget.Toolbar;
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
import android.widget.Toast;

import com.example.parkingappv01.MainActivity;
import com.example.parkingappv01.R;
import com.example.parkingappv01.ui.pocetna.PocetnaFragment;
import com.example.parkingappv01.ui.registracija.RegistracijaFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil;

public class PrijavaFragment extends Fragment {
    TextInputLayout mEmail, mLozinka;
    FirebaseAuth fAuth;
    Toolbar toolbar;

    private PrijavaViewModel mViewModel;

    public static PrijavaFragment newInstance() {

        return new PrijavaFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        ((MainActivity) getActivity()).lockDrawer();
        View v = inflater.inflate(R.layout.prijava_fragment, container, false);

        mEmail = v.findViewById(R.id.email);
        mLozinka = v.findViewById(R.id.lozinka);
        fAuth=FirebaseAuth.getInstance();
        toolbar = v.findViewById(R.id.toolbar);


        Button btnPrijavise = (Button) v.findViewById(R.id.btn_pri_se);
        btnPrijavise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logIn(v);
                UIUtil.hideKeyboard(getActivity());
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
    private boolean validatePassword(){
        String password = mLozinka.getEditText().getText().toString().trim();
        if(password.isEmpty()){
            mLozinka.setError("Unesite lozinku!");
            return false;
        }else{
            mLozinka.setError(null);
            return true;
        }
    }
    public void logIn (View v){
        if(!validateEmail() | !validatePassword()){
            return;
        }
        final String email = mEmail.getEditText().getText().toString().trim();
        String password = mLozinka.getEditText().getText().toString().trim();
        fAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(getActivity(),"Dobrodošli! "+email, Toast.LENGTH_SHORT).show();

                    FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.container, PocetnaFragment.newInstance());
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();

                } else {
                    Toast.makeText(getActivity(), "Greška! "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(PrijavaViewModel.class);
        // TODO: Use the ViewModel

    }


}
