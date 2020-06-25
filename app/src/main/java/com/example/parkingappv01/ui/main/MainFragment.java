package com.example.parkingappv01.ui.main;

import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.parkingappv01.MainActivity;
import com.example.parkingappv01.R;
import com.example.parkingappv01.ui.pocetna.PocetnaFragment;
import com.example.parkingappv01.ui.prijava.PrijavaFragment;
import com.example.parkingappv01.ui.registracija.RegistracijaFragment;

public class MainFragment extends Fragment {
    private MainViewModel mViewModel;


    public static MainFragment newInstance() {

        return new MainFragment();
    }



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        ((MainActivity) getActivity()).lockDrawer();
        View v = inflater.inflate(R.layout.main_fragment, container, false);
        ImageButton imgBtn = (ImageButton) v.findViewById(R.id.imgBtn);
        final ImageView imgView = (ImageView) v.findViewById(R.id.imageView4);
        final TextView t2 = (TextView) v.findViewById(R.id.textView2);
        final TextView t3 = (TextView) v.findViewById(R.id.textView3);

        imgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgView.setVisibility(View.INVISIBLE);
                t2.setVisibility(View.INVISIBLE);
                t3.setVisibility(View.VISIBLE);
            }
        });

        Button btnPrijava = (Button) v.findViewById(R.id.btn_prijava);
        btnPrijava.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.container, PrijavaFragment.newInstance());
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        Button btnRegistracija = (Button) v.findViewById(R.id.btn_registracija);
        btnRegistracija.setOnClickListener(new View.OnClickListener() {
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
        mViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        // TODO: Use the ViewModel

    }

}
