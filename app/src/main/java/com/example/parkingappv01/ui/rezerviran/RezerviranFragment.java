package com.example.parkingappv01.ui.rezerviran;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

import com.example.parkingappv01.R;
import com.example.parkingappv01.ui.kreiran.KreiranFragment;
import com.example.parkingappv01.ui.kreiran.KreiranViewModel;
import com.example.parkingappv01.ui.ponude.PonudeFragment;
import com.example.parkingappv01.ui.rezervacije.RezervacijeFragment;

public class RezerviranFragment extends Fragment {

    private RezerviranViewModel mViewModel;

    public static RezerviranFragment newInstance() {

        return new RezerviranFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.rezerviran_fragment, container, false);
        Button btnOk2 = (Button) v.findViewById(R.id.btn_ok2);
        btnOk2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.container, RezervacijeFragment.newInstance());
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        return v;
    }

        @Override
        public void onActivityCreated(@Nullable Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            mViewModel = ViewModelProviders.of(this).get(RezerviranViewModel.class);
            // TODO: Use the ViewModel

    }
}
