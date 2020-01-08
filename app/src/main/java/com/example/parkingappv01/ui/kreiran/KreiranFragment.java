package com.example.parkingappv01.ui.kreiran;

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
import com.example.parkingappv01.ui.ponude.PonudeFragment;
import com.example.parkingappv01.ui.ponudi3.Ponudi3Fragment;
import com.example.parkingappv01.ui.ponudi3.Ponudi3ViewModel;
import com.example.parkingappv01.ui.prijava.PrijavaFragment;

public class KreiranFragment extends Fragment {

    private KreiranViewModel mViewModel;

    public static KreiranFragment newInstance() {

        return new KreiranFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.kreiran_fragment, container, false);
        Button btnOk = (Button) v.findViewById(R.id.btn_ok1);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.container, PonudeFragment.newInstance());
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        return v;
}

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(KreiranViewModel.class);
        // TODO: Use the ViewModel

    }
}
