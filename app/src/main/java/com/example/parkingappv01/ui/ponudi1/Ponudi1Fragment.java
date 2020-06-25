package com.example.parkingappv01.ui.ponudi1;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.inputmethodservice.Keyboard;
import android.location.Address;
import android.location.Geocoder;
import android.nfc.Tag;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.inputmethod.EditorInfoCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

import com.example.parkingappv01.MainActivity;
import com.example.parkingappv01.R;
import com.example.parkingappv01.ui.kreiran.KreiranFragment;
import com.example.parkingappv01.ui.pocetna.PocetnaFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil;

import java.io.IOException;
import java.io.ObjectInput;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.app.Activity.RESULT_OK;
import static androidx.constraintlayout.widget.Constraints.TAG;

public class Ponudi1Fragment extends Fragment {

    private Ponudi1ViewModel mViewModel;
    private Throwable Exception;

    public static Ponudi1Fragment newInstance() {

        return new Ponudi1Fragment();
    }

    FirebaseFirestore fStore;
    FirebaseAuth fAuth;
    //Vars

     TextInputLayout mDatOd, mDatDo, mVriOd, mVriDo, mCps;
     TextView mNaziv;


     String mLat, mLng;

    //Widgets
    private EditText mSearchText;
    private GoogleMap mMap;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        ((MainActivity) getActivity()).lockDrawer();
        View v = inflater.inflate(R.layout.ponudi1_fragment, container, false);
        fAuth= FirebaseAuth.getInstance();
        mSearchText =(EditText) v.findViewById(R.id.adresa);
        mSearchText.setSingleLine();
        fStore = FirebaseFirestore.getInstance();

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapa);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

                mMap.clear();

                CameraPosition googlePlex = CameraPosition.builder()
                        .target(new LatLng(44.866077,13.851645))
                        .zoom(13)
                        .bearing(0)
                        .tilt(45)
                        .build();

                mMap.moveCamera(CameraUpdateFactory.newCameraPosition(googlePlex));

            }
        });



        ImageButton imgBtnIks = (ImageButton) v.findViewById(R.id.imgBtnIks);
        imgBtnIks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIUtil.hideKeyboard(getActivity());
                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.container, PocetnaFragment.newInstance());
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        ImageButton imgBtnNaprijed = (ImageButton) v.findViewById(R.id.imgBtnNapr);
        imgBtnNaprijed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String adresa = mSearchText.getText().toString().trim();
                if(TextUtils.isEmpty(adresa)){
                    mSearchText.setError("Unesite adresu parkinga");
                    return;
                }
                init();
                geoLocate();
            }
        });

        return v;
    }
    private void init(){
        Log.d(TAG, "init: initializing");

        mSearchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || event.getAction() == KeyEvent.ACTION_DOWN
                        || event.getAction() == KeyEvent.KEYCODE_ENTER
                        ){
                   geoLocate();

                }
                return false;
            }
        });
        UIUtil.hideKeyboard(getActivity());
    }

    private void geoLocate(){
        Log.d(TAG,"geoLocate: geolocationig");
        boolean ok = false;
        final String searchString = mSearchText.getText().toString();
        Geocoder geocoder = new Geocoder(getActivity());
        List<Address> list = new ArrayList<>();
        try{
            list = geocoder.getFromLocationName(searchString, 1);
        }catch (IOException e){
            Log.e(TAG, "geoLocate: IOException: " + e.getMessage() );
        }

        if(list.size() > 0){

            Toast.makeText(getActivity(),"Adresa pronađena", Toast.LENGTH_SHORT).show();
            ok = true;


        }
        else {
            Toast.makeText(getActivity(),"Greška, nepostojeća adresa", Toast.LENGTH_SHORT).show();
            mSearchText.setError("Greška, nepostojeća adresa");
            UIUtil.hideKeyboard(getActivity());

        }
        if(ok){
            Address address = list.get(0);
            moveCamera(new LatLng(address.getLatitude(), address.getLongitude()), 18,
                    address.getAddressLine(0));
            final AlertDialog.Builder mBulider = new AlertDialog.Builder(getActivity());
            final View mView = getLayoutInflater().inflate(R.layout.ponudi_dialog, null);
            mBulider.setView(mView);
            final AlertDialog dialog = mBulider.create();
            dialog.show();

            mNaziv = (TextView) mView.findViewById(R.id.naziv);
            mNaziv.setText(mSearchText.getText());
            mDatOd = (TextInputLayout) mView.findViewById(R.id.datumod1);
            mDatOd.getEditText().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dateOd();
                }
            });
            mDatDo = (TextInputLayout) mView.findViewById(R.id.datumdo1);
            mDatDo.getEditText().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dateDo();
                }
            });
            mVriOd = mView.findViewById(R.id.vrijemeod1);
            mVriOd.getEditText().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    timeOd();
                }
            });
            mVriDo = mView.findViewById(R.id.vrijemedo1);
            mVriDo.getEditText().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    timeDo();
                }
            });
            mCps = mView.findViewById(R.id.cijena1);


            Button   mOdustani = (Button) mView.findViewById(R.id.btnOdustani);
            mOdustani.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            Button   mKreiraj = (Button) mView.findViewById(R.id.btnKreiraj);
            mKreiraj.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    final String datod = mDatOd.getEditText().getText().toString().trim();
                    final String datdo = mDatDo.getEditText().getText().toString().trim();
                    final String vriod = mVriOd.getEditText().getText().toString().trim();
                    final String vrido = mVriDo.getEditText().getText().toString().trim();
                    final String cps = mCps.getEditText().getText().toString().trim();

                    if(!validateDatOd()
                            | !validateDatDo()
                            | !validateVriOd()
                            | !validateVriDo()
                            | !validateCps()){
                        return;
                    }

                    final Map<String,String> parking = new HashMap<>();
                    parking.put("kreator_id", fAuth.getUid());
                    parking.put("naziv", searchString);
                    parking.put("lat",  mLat);
                    parking.put("lng", mLng);
                    parking.put("parking_id", "0");
                    parking.put("datumod", datod);
                    parking.put("datumdo", datdo);
                    parking.put("vrijemeod", vriod);
                    parking.put("vrijemedo", vrido);
                    parking.put("cijena", cps);
                    parking.put("status","slobodno");
                    parking.put("nor","0");
                    parking.put("rating","0");


                    fStore.collection("parkirna_mjesta").add(parking).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            DocumentReference np = fStore.collection("parkirna_mjesta").document(documentReference.getId());
                            np.update("parking_id", documentReference.getId());

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            String error = e.getMessage();
                            Toast.makeText(getActivity(), "Greška: " + error, Toast.LENGTH_SHORT).show();
                        }
                    });

                    dialog.dismiss();
                    FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.container, KreiranFragment.newInstance());
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
            });

        }
    }

    private void moveCamera(final LatLng latLng, final float zoom, final String title){
        Log.d(TAG, "moveCamera: moving the camera to: lat: " + latLng.latitude + ", lng: " + latLng.longitude );
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapa);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

                mMap.clear();

                CameraPosition googlePlex = CameraPosition.builder()
                        .target(new LatLng(latLng.latitude,latLng.longitude))
                        .zoom(zoom)
                        .bearing(0)
                        .tilt(45)
                        .build();

                mMap.moveCamera(CameraUpdateFactory.newCameraPosition(googlePlex));
                Marker marker = mMap.addMarker(new MarkerOptions().position(new LatLng(latLng.latitude,latLng.longitude)).title(title));

                mLat = String.valueOf(latLng.latitude);
                mLng = String.valueOf(latLng.longitude);
            }

        });

        UIUtil.hideKeyboard(getActivity());
    }
    public void dateOd (){

        final Calendar calendar = Calendar.getInstance();
        int Y = calendar.get(Calendar.YEAR);
        int M = calendar.get(Calendar.MONTH);
        int D = calendar.get(Calendar.DATE);
        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int y, int m, int d) {

                Calendar calendar1 = Calendar.getInstance();
                calendar1.set(Calendar.YEAR, y);
                calendar1.set(Calendar.MONTH, m);
                calendar1.set(Calendar.DATE, d);
                String dateString = String.valueOf(DateFormat.format("dd.MM.yy", calendar1));
                mDatOd.getEditText().setText(dateString);
            }
        }, Y, M, D);
        datePickerDialog.show();
    }
    public void dateDo (){

        final Calendar calendar = Calendar.getInstance();
        int Y = calendar.get(Calendar.YEAR);
        int M = calendar.get(Calendar.MONTH);
        int D = calendar.get(Calendar.DATE);
        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int y, int m, int d) {

                Calendar calendar1 = Calendar.getInstance();
                calendar1.set(Calendar.YEAR, y);
                calendar1.set(Calendar.MONTH, m);
                calendar1.set(Calendar.DATE, d);
                String dateString = String.valueOf(DateFormat.format("dd.MM.yy", calendar1));
                mDatDo.getEditText().setText(dateString);
            }
        }, Y, M, D);
        datePickerDialog.show();
    }
    public void timeOd (){
        Calendar calendar = Calendar.getInstance();
        int H = calendar.get(Calendar.HOUR);
        int M = calendar.get(Calendar.MINUTE);


        TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int h, int m) {

                mVriOd.getEditText().setText(String.format("%02d:%02d",h,m));
            }
        }, H, M,true);
        timePickerDialog.show();
    }
    public void timeDo (){
        Calendar calendar = Calendar.getInstance();

        int H = calendar.get(Calendar.HOUR_OF_DAY);
        int M = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int h, int m) {

                mVriDo.getEditText().setText(String.format("%02d:%02d",h,m));
            }
        }, H, M, true);
        timePickerDialog.show();
    }

    private boolean validateDatOd(){
        String datod = mDatOd.getEditText().getText().toString().trim();
        String datdo = mDatDo.getEditText().getText().toString().trim();
        if(datod.isEmpty()){
            mDatOd.setError("Odaberite datum");
            return false;
        }
        if(datod.compareTo(datdo) > 0){
            mDatOd.setError("Pocetni datum mora biti manji od zavrsnog:");
            mDatDo.setError("Zavrsni datum mora biti manji od pocetnog");
            return false;
        }
        else{
            mDatOd.setError(null);
            return true;
        }
    }

    private boolean validateDatDo(){
        String datdo = mDatDo.getEditText().getText().toString().trim();
        if(datdo.isEmpty()){
            mDatDo.setError("Odaberite datum");
            return false;
        }else{
            mDatDo.setError(null);
            return true;
        }
    }
    private boolean validateVriOd(){
        String vriod = mVriOd.getEditText().getText().toString().trim();
        if(vriod.isEmpty()){
            mVriOd.setError("Odaberite vrijeme");
            return false;
        }else{
            mVriOd.setError(null);
            return true;
        }
    }
    private boolean validateVriDo(){
        String vrido = mVriDo.getEditText().getText().toString().trim();
        if(vrido.isEmpty()){
            mVriDo.setError("Odaberite vrijeme");
            return false;
        }else{
            mVriDo.setError(null);
            return true;
        }
    }
    private boolean validateCps(){
        String cps = mCps.getEditText().getText().toString().trim();
        if(cps.isEmpty()){
            mCps.setError("Unesite cijenu");
            return false;
        }else{
            mCps.setError(null);
            return true;
        }
    }

        @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(Ponudi1ViewModel.class);
        // TODO: Use the ViewModel

    }

}

