package com.example.parkingappv01.ui.pronadi2;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

import com.example.parkingappv01.R;
import com.example.parkingappv01.ui.kreiran.KreiranFragment;
import com.example.parkingappv01.ui.pocetna.PocetnaFragment;
import com.example.parkingappv01.ui.pronadi1.Pronadi1Fragment;
import com.example.parkingappv01.ui.pronadi1.Pronadi1ViewModel;
import com.example.parkingappv01.ui.rezerviran.RezerviranFragment;

public class Pronadi2Fragment extends Fragment {

    private Pronadi2ViewModel mViewModel;

    public static Pronadi2Fragment newInstance() {

        return new Pronadi2Fragment();
}
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.pronadi2_fragment, container, false);
        ImageButton imgBtnNazad3 = (ImageButton) v.findViewById(R.id.imgBtnNaz2);
        imgBtnNazad3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.container, Pronadi1Fragment.newInstance());
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        Button btnRezerviraj = (Button) v.findViewById(R.id.btn_rezerviraj);
        btnRezerviraj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.container, RezerviranFragment.newInstance());
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(Pronadi2ViewModel.class);
        // TODO: Use the ViewModel

    }
}