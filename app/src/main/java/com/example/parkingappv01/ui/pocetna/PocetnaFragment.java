package com.example.parkingappv01.ui.pocetna;


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
import com.example.parkingappv01.ui.kreiran.KreiranFragment;
import com.example.parkingappv01.ui.pocetna.PocetnaViewModel;
import com.example.parkingappv01.ui.ponudi1.Ponudi1Fragment;
import com.example.parkingappv01.ui.pronadi1.Pronadi1Fragment;

public class PocetnaFragment extends Fragment {

    private PocetnaViewModel mViewModel;

    public static PocetnaFragment newInstance() {

        return new PocetnaFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        ((MainActivity) getActivity()).unlockDrawer();
        View v = inflater.inflate(R.layout.pocetna_fragment, container, false);
        Button btnPonudiparking = (Button) v.findViewById(R.id.btn_ponudi);
        btnPonudiparking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.container, Ponudi1Fragment.newInstance());
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        Button btnPronadiparking = (Button) v.findViewById(R.id.btn_pronadi);
        btnPronadiparking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.container, Pronadi1Fragment.newInstance());
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(PocetnaViewModel.class);
        // TODO: Use the ViewModel

    }


}