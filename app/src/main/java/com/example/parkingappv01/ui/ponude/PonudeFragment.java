package com.example.parkingappv01.ui.ponude;

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
import com.example.parkingappv01.ui.pronadi1.Pronadi1Fragment;
import com.example.parkingappv01.ui.rezerviran.RezerviranFragment;

public class PonudeFragment extends Fragment {

    private PonudeViewModel mViewModel;

    public static PonudeFragment newInstance() {

        return new PonudeFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.ponude_fragment, container, false);



        return v;
}

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(PonudeViewModel.class);
        // TODO: Use the ViewModel

    }
}
