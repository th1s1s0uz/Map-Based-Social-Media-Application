package com.example.b2bouz;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class GonderiYuklemeFragment extends Fragment {

    private EditText editTextBaslik, editTextIcerik;
    private Button buttonGonder;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.gonderiyukleme, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("gonderiler");

        editTextBaslik = view.findViewById(R.id.editTextTitle);
        editTextIcerik = view.findViewById(R.id.editTextContent);
        buttonGonder = view.findViewById(R.id.buttonUpload);

        buttonGonder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gonderiYukle();
            }
        });

        return view;
    }

    private void gonderiYukle() {
        String title = editTextBaslik.getText().toString().trim();
        String content = editTextIcerik.getText().toString().trim();
        String kullaniciId = firebaseAuth.getCurrentUser().getUid();
        String gonderiId = databaseReference.push().getKey();
        String yuklenmeTarihi = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());

        Post post = new Post(gonderiId, kullaniciId, title, content, yuklenmeTarihi);

        databaseReference.child(gonderiId).setValue(post)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getActivity(), "Gönderi başarıyla yüklendi.", Toast.LENGTH_SHORT).show();
                        navigateToAnasayfaFragment(); // AnasayfaFragment'a yönlendirme yap
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), "Gönderi yüklenirken hata oluştu.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void navigateToAnasayfaFragment() {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        AnasayfaFragment anasayfaFragment = new AnasayfaFragment();
        fragmentTransaction.replace(R.id.cercevekapsayici, anasayfaFragment);
        fragmentTransaction.commit();
    }
}
