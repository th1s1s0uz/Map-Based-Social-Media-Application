package com.example.b2bouz;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.b2bouz.Post;
import com.example.b2bouz.PostAdapter;
import com.example.b2bouz.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment {
    private TextView usernameTextView;
    private RecyclerView postRecyclerView;
    private PostAdapter postAdapter;
    private DatabaseReference userRef;
    private DatabaseReference postsRef;
    private String userId;

    public ProfileFragment() {
        // Boş yapıcı yöntem
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        usernameTextView = view.findViewById(R.id.username);
        postRecyclerView = view.findViewById(R.id.post_list);
        postRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        List<Post> postList = new ArrayList<>(); // Post veri listesini burada tanımlayın ve doldurun

        postAdapter = new PostAdapter(postList); // PostAdapter'ı veri listesiyle birlikte oluşturun

        postRecyclerView.setAdapter(postAdapter);

        // Kullanıcının adını yükleme
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        userId = getArguments().getString("userId");

        if (userId == null || userId.isEmpty()) {
            userId = currentUser.getUid();
        }

        userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);
        postsRef = FirebaseDatabase.getInstance().getReference("gonderiler");

        // Kullanıcı adını yükleme
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String username = dataSnapshot.child("username").getValue(String.class);
                usernameTextView.setText(username);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Hata durumunda yapılacak işlemleri burada tanımlayabilirsiniz
            }
        });
        // Gönderileri getir ve dinle
        postsRef = FirebaseDatabase.getInstance().getReference("gonderiler");
        postsRef.orderByChild("userID").equalTo(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Post> postList = new ArrayList<>();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Post post = postSnapshot.getValue(Post.class);
                    if (post != null) {
                        postList.add(post);
                    }
                }
                postAdapter.setPostList(postList);
                postAdapter.notifyDataSetChanged(); // Adapter'i güncelle
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Hata durumunda yapılacak işlemleri burada tanımlayabilirsiniz
            }
        });

        // "Send Message" butonunu tanımlama ve tıklama olayını ayarlama
        Button sendMessageButton = view.findViewById(R.id.gotoChatfragment);
        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoChatFragment(userId); // userId'i parametre olarak gönder
            }
        });

        return view;
    }

    public void gotoChatFragment(String userId) {
        // ChatFragment'a geçmek için gerekli işlemleri burada gerçekleştirin
        ChatFragment chatFragment = new ChatFragment();

        // userId bilgisini Chat Fragment'a iletmek için bir Bundle oluşturun
        Bundle bundle = new Bundle();
        bundle.putString("userId", userId);
        chatFragment.setArguments(bundle);

        // ChatFragment'ı yükleme işlemlerini gerçekleştirin
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.cercevekapsayici, chatFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}