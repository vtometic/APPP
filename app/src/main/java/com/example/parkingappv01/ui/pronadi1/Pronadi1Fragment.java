package com.example.parkingappv01.ui.pronadi1;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

import com.example.parkingappv01.MainActivity;
import com.example.parkingappv01.R;
import com.example.parkingappv01.ui.pocetna.PocetnaFragment;
import com.example.parkingappv01.ui.pronadi2.Pronadi2Fragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.SnapshotMetadata;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.android.volley.VolleyLog.TAG;


public class Pronadi1Fragment extends Fragment {
    public Pronadi1Fragment() {
        // Required empty public constructor
    }
    private Pronadi1ViewModel mViewModel;

    public static Pronadi1Fragment newInstance() {

        return new Pronadi1Fragment();
    }
    FirebaseFirestore fStore;
    Double mLat, mLng;
    String mNaziv, mPid, naziv, vriod, vrido, cps;
    TextView iNaziv, iDat, iVriOd, iVriDo, iCps, iRating;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {


        ((MainActivity) getActivity()).lockDrawer();
        final View v = inflater.inflate(R.layout.pronadi1_fragment, container, false);
        fStore = FirebaseFirestore.getInstance();
        updateBase(fStore);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.frg);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(final GoogleMap mMap) {
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                mMap.clear();

                CameraPosition googlePlex = CameraPosition.builder()
                        .target(new LatLng(44.866077,13.851645))
                        .zoom(13)
                        .bearing(0)
                        .tilt(45)
                        .build();

                mMap.moveCamera(CameraUpdateFactory.newCameraPosition(googlePlex));
                setMarkers(mMap);
                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        final AlertDialog.Builder mBulider = new AlertDialog.Builder(getActivity());
                        final View mView = getLayoutInflater().inflate(R.layout.pinfo_dialog, null);
                        mBulider.setView(mView);
                        final AlertDialog dialog = mBulider.create();
                        searchById(marker.getTag().toString(),mView);
                        final String ParkingId = marker.getTag().toString();
                        Button   mOdustani = (Button) mView.findViewById(R.id.btnOdustani);
                        mOdustani.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                        Button   mRezerviraj = (Button) mView.findViewById(R.id.btnRezerviraj);
                        mRezerviraj.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                                String id = ParkingId;

                                Bundle bundle = new Bundle();
                                bundle.putString("Naziv", naziv);
                                bundle.putString("Vriod", vriod);
                                bundle.putString("Vrido", vrido);
                                bundle.putString("Cps", cps);
                                bundle.putString("Id", id);

                                Pronadi2Fragment pronadi2Fragment = new Pronadi2Fragment();
                                pronadi2Fragment.setArguments(bundle);

                                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                                fragmentTransaction.replace(R.id.container,pronadi2Fragment);
                                fragmentTransaction.addToBackStack(null);
                                fragmentTransaction.commit();
                            }
                        });
                        dialog.show();
                        return true;
                    }
                });
            }
        });


        ImageButton imgBtnIks2 = v.findViewById(R.id.imgBtnIks2);
        imgBtnIks2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.container, PocetnaFragment.newInstance());
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
        return v;
    }

    public void setMarkers(final GoogleMap mMap){
        fStore.collection("parkirna_mjesta").whereEqualTo("status","slobodno").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if(e != null){
                    Log.e(TAG, "On Event", e);
                    return;
                }if(queryDocumentSnapshots != null){
                    List<DocumentSnapshot> snapshotList = queryDocumentSnapshots.getDocuments();
                    for(DocumentSnapshot snapshot : snapshotList){
                        mNaziv = snapshot.getData().get("naziv").toString();
                        mLat = Double.parseDouble(snapshot.getData().get("lat").toString());
                        mLng = Double.parseDouble(snapshot.getData().get("lng").toString());
                        mPid = snapshot.getId();

                        Marker marker = mMap.addMarker(new MarkerOptions().position(new LatLng(mLat,mLng)).title(mNaziv));
                        marker.setTag(mPid);
                        Log.d(TAG, mNaziv +" "+mPid);
                    }

                }else {
                    Log.e(TAG, "Query was null");
                }
            }
        });

    }
    public void searchById(String id, final View v){
        CollectionReference peopleRef = fStore.collection("parkirna_mjesta");
        peopleRef.whereEqualTo("parking_id", id)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                iNaziv = v.findViewById(R.id.naziv);
                                iNaziv.setText("Naziv: "+document.getData().get("naziv").toString());
                                naziv = document.getData().get("naziv").toString();
                                iDat = v.findViewById(R.id.datum);
                                SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
                                String danas = sdf.format(new Date());
                                iDat.setText("Datum: "+danas);

                                iVriOd = v.findViewById(R.id.vrijemeod);
                                iVriOd.setText("Raspoloživo od: "+document.getData().get("vrijemeod").toString()+"h");
                                vriod = document.getData().get("vrijemeod").toString();

                                iVriDo = v.findViewById(R.id.vrijemedo);
                                iVriDo.setText("Raspoloživo do: "+document.getData().get("vrijemedo").toString()+"h");
                                vrido = document.getData().get("vrijemedo").toString();

                                iCps = v.findViewById(R.id.cijena1);
                                iCps.setText("Cijena po satu: "+document.getData().get("cijena").toString()+"kn");
                                cps = document.getData().get("cijena").toString();

                                DecimalFormat df = new DecimalFormat("0.00");
                                iRating = v.findViewById(R.id.rating);
                                double nor = Double.valueOf(document.getString("nor"));
                                double rate = Double.valueOf(document.getString("rating"));

                                iRating.setText("Ocjena korisnika: "+df.format(rate/nor)+"/5");

                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

    }
    public void updateBase(FirebaseFirestore fStore){
        final String currentDate = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(new Date());
        final String currentTime = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
        final CollectionReference colRef = fStore.collection("parkirna_mjesta");
        colRef.whereLessThan("datumdo", currentDate).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        colRef.document(document.getId()).update("status", "isteklo");
                    }
                }
            }
        });
        CollectionReference colRef1 = fStore.collection("rezervacije");
        colRef1.whereEqualTo("placeno", "Da").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {

                            String vod = document.getString("vrijemeod");
                            String vdo = document.getString("vrijemedo");
                            String Pid = document.getString("parking_id");
                            String dat = document.getString("datum");
                            getProvjeri(vod, vdo, Pid, dat);

                    }
                }
            }
        });

    }
    public void getProvjeri (String vod, final String vdo, String Pid, final String dat){
        SimpleDateFormat sdate = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        final String currentDate = sdate.format(new Date());

        SimpleDateFormat stime = new SimpleDateFormat("HH:mm", Locale.getDefault());
        final String currentTime = stime.format(new Date());

        final CollectionReference collection = fStore.collection("parkirna_mjesta");
        Query rezervacijeQuery = collection.whereEqualTo("parking_id", Pid);
        rezervacijeQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull final Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if(dat.equals(currentDate)){
                            if(vdo.compareTo(currentTime)<0){

                                collection.document(document.getId()).update("status", "slobodno");
                            }

                        }
                    }
                }
            }
        });


    }



        @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(Pronadi1ViewModel.class);
        // TODO: Use the ViewModel

    }
}
