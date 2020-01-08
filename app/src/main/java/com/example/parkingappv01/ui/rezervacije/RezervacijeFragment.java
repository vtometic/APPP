package com.example.parkingappv01.ui.rezervacije;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.parkingappv01.R;
import com.example.parkingappv01.ui.ponude.PonudeFragment;
import com.example.parkingappv01.ui.ponude.PonudeViewModel;

public class RezervacijeFragment extends Fragment {

    private RezervacijeViewModel mViewModel;

    public static RezervacijeFragment newInstance() {

        return new RezervacijeFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.rezervacije_fragment, container, false);



        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(RezervacijeViewModel.class);
        // TODO: Use the ViewModel

    }
}
