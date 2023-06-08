package com.example.b2bouz;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {

    private EditText editTextSearch;
    private Button buttonSearch;
    private RecyclerView recyclerViewResults;
    private SearchResultsAdapter searchResultsAdapter;
    private List<String> searchResults;

    public SearchFragment() {
        // Boş parametreli yapıcı metod
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Fragment'in görünümünü oluştur
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        // Görünüm öğelerini tanımla
        editTextSearch = view.findViewById(R.id.editTextSearch);
        recyclerViewResults = view.findViewById(R.id.recyclerViewResults);

        // RecyclerView için LayoutManager'ı ayarla
        recyclerViewResults.setLayoutManager(new LinearLayoutManager(getActivity()));

        // Arama sonuçlarını tutacak liste oluştur
        searchResults = new ArrayList<>();

        // SearchResultsAdapter'ı oluştur ve RecyclerView'e bağla
        searchResultsAdapter = new SearchResultsAdapter(searchResults, getActivity());
        recyclerViewResults.setAdapter(searchResultsAdapter);

        // EditText dinlemesi ekle
        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Değişiklik öncesi
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Metin değiştiğinde arama işlemini gerçekleştir
                performSearch(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Değişiklik sonrası
            }
        });

        // Veritabanından kullanıcıları al ve arama sonuçları listesini güncelle
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("users");
        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                searchResults.clear(); // Listeyi temizle

                // Tüm kullanıcı adlarını dön ve listeye ekle
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String username = userSnapshot.getKey();
                    searchResults.add(username);
                }

                searchResultsAdapter.notifyDataSetChanged(); // Adapter'ı güncelle
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Hata durumunda işlemleri burada yönetin
            }
        });

        return view;
    }

    private void performSearch(String searchText) {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("users");
        Query searchQuery;

        // Arama metninin boş olup olmadığını kontrol et
        if (TextUtils.isEmpty(searchText)) {
            // Boş ise tüm kullanıcıları getir
            searchQuery = usersRef;
        } else {
            // Değilse kullanıcı adına göre filtrele
            searchQuery = usersRef.orderByChild("username").startAt(searchText).endAt(searchText + "\uf8ff");
        }

        searchQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                searchResults.clear(); //
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String username = userSnapshot.getKey();
                    searchResults.add(username);
                }

                searchResultsAdapter.notifyDataSetChanged(); // Adapter'ı güncelle
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Hata durumunda işlemleri burada yönetin
            }
        });
    }
}

