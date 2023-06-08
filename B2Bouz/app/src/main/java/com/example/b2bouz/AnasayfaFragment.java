package com.example.b2bouz;


import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import android.Manifest;

public class AnasayfaFragment extends Fragment implements OnMapReadyCallback {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;
    private GoogleMap mMap;
    private FrameLayout frameLayout;

    BottomNavigationView bottomNavigationView;

    private ImageView searchIcon;
    private ImageView dmIcon;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_anasayfa, container, false);

        //Framelayout'a Google Maps fragmenti eklenmesi
        frameLayout = view.findViewById(R.id.map);
        SupportMapFragment mapFragment = new SupportMapFragment();
        mapFragment.getMapAsync(this);
        getChildFragmentManager().beginTransaction().replace(R.id.map, mapFragment).commit();
        searchIcon = view.findViewById(R.id.searchIcon);
        dmIcon = view.findViewById(R.id.dmIcon);

        searchIcon = view.findViewById(R.id.searchIcon);
        dmIcon = view.findViewById(R.id.dmIcon);

        searchIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSearchFragment(v);
            }
        });

        dmIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDMFragment(v);
            }
        });

        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (mMap != null) {
            // Harita türünü ayarlayın
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

            // Konum servisini kontrol etmek için özel bir sınıf kullanın
            KonumServisi konumServisi = new KonumServisi(getActivity());
            LatLng konum = konumServisi.getKonum();

            // Konum servisi başarılı ise haritayı kullanıcının konumuna göre ayarlayın
            if (konum != null) {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(konum, 15));
                mMap.addMarker(new MarkerOptions().position(konum).title("Konumum"));
            } else { // Konum servisi başarısız ise İstanbul'a odaklanın ve yakınlaştırın
                LatLng location = new LatLng(41.0082, 28.9784);
                mMap.addMarker(new MarkerOptions().position(location).title("İstanbul"));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15));
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            // Konum izni verildi mi kontrol edin
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Konum servisi kontrolü
                KonumServisi konumServisi = new KonumServisi(getActivity());
                LatLng konum = konumServisi.getKonum();

                // Konum servisi başarılı ise haritayı kullanıcının konumuna göre ayarlayın
                if (konum != null) {
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(konum, 15));
                    mMap.addMarker(new MarkerOptions().position(konum).title("Konumum"));
                } else { // Konum servisi başarısız ise İstanbul'a odaklanın ve yakınlaştırın
                    LatLng location = new LatLng(41.0082, 28.9784);
                    mMap.addMarker(new MarkerOptions().position(location).title("İstanbul"));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15));
                }
            } else {
                // Konum izni verilmediyse, kullanıcıya açıklama yapın ve izinleri yeniden isteyin
                Toast.makeText(getActivity(), "Konum izni verilmedi.", Toast.LENGTH_SHORT).show();
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            }
        }
    }
    public void openSearchFragment(View view) {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        SearchFragment searchFragment = new SearchFragment();
        fragmentTransaction.replace(R.id.cercevekapsayici, searchFragment);
        fragmentTransaction.commit();
    }

    public void openDMFragment(View view) {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        DmFragment dmFragment = new DmFragment();
        fragmentTransaction.replace(R.id.cercevekapsayici, dmFragment);
        fragmentTransaction.commit();
    }
}
