package com.example.b2bouz;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

public class SearchResultsAdapter extends RecyclerView.Adapter<SearchResultsAdapter.ViewHolder> {

    private List<String> searchResults;
    private DatabaseReference databaseReference;
    private Context context;
    private FirebaseUser currentUser;

    public SearchResultsAdapter(List<String> searchResults, Context context) {
        this.searchResults = searchResults;
        this.context = context;
        databaseReference = FirebaseDatabase.getInstance().getReference().child("users");
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_result_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String userId = searchResults.get(position);
        holder.bind(userId);
    }

    @Override
    public int getItemCount() {
        return searchResults.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView usernameTextView;
        private ImageView profileImageView;
        private String userId;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameTextView = itemView.findViewById(R.id.username_textview);
            profileImageView = itemView.findViewById(R.id.profile_image);
            itemView.setOnClickListener(this);
        }

        public void bind(String userId) {
            this.userId = userId;
            DatabaseReference userRef = databaseReference.child(userId);
            userRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String username = dataSnapshot.child("username").getValue(String.class);
                        String profileImageUrl = dataSnapshot.child("profileImageUrl").getValue(String.class);

                        usernameTextView.setText(username);

                        if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
                            Picasso.get().load(profileImageUrl).into(profileImageView);
                        } else {
                            Picasso.get().load(R.drawable.ic_launcher_background).into(profileImageView);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Hata durumunda işlemleri burada yönetin
                }
            });
        }

        @Override
        public void onClick(View v) {
            // Tıklanan kullanıcının profiline gitmek için ProfileFragment'e geçiş yap
            FragmentManager fragmentManager = ((AppCompatActivity) context).getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            // ProfileFragment'i oluştur ve kullanıcı ID'sini ileterek fragmente bağla
            ProfileFragment profileFragment = new ProfileFragment();
            Bundle bundle = new Bundle();
            bundle.putString("userId", userId);
            profileFragment.setArguments(bundle);

            fragmentTransaction.replace(R.id.cercevekapsayici, profileFragment);
            fragmentTransaction.addToBackStack(null);  // Geri tuşuna basıldığında önceki fragmente dönmek için geriye ekleyin
            fragmentTransaction.commit();
        }
    }
}