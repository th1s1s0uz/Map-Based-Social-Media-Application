package com.example.b2bouz;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DmFragment extends Fragment implements MessagesAdapter.OnItemClickListener {

    private RecyclerView recyclerViewMessages;
    private MessagesAdapter messagesAdapter;
    private List<Message> messageList;

    public DmFragment() {
        // Boş parametreli yapıcı metot
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dm, container, false);

        // RecyclerView'i tanımla
        recyclerViewMessages = view.findViewById(R.id.recyclerViewMessages);

        // RecyclerView için LayoutManager'ı ayarla
        recyclerViewMessages.setLayoutManager(new LinearLayoutManager(getActivity()));


        // Mesajları tutacak liste oluştur
        messageList = new ArrayList<>();

        // Mesajları getir ve listeyi güncelle
        messagesAdapter = new MessagesAdapter(messageList);
        messagesAdapter.setOnItemClickListener(this);
        recyclerViewMessages.setAdapter(messagesAdapter);
        loadMessages();

        return view;
    }

    private void loadMessages() {
        DatabaseReference messagesRef = FirebaseDatabase.getInstance().getReference().child("messages");
        String currentUserId = getCurrentUserId(); // Mevcut kullanıcının kimliğini al

        Set<String> previousContacts = new HashSet<>(); // Önceden konuşulan kişilerin kimliklerini depolamak için küme oluştur

        messagesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                previousContacts.clear(); // Önceden konuşulan kişilerin kimliklerini temizle

                for (DataSnapshot messageSnapshot : dataSnapshot.getChildren()) {
                    String senderId = messageSnapshot.child("senderId").getValue(String.class);
                    String receiverId = messageSnapshot.child("receiverId").getValue(String.class);

                    if (senderId != null && receiverId != null) {
                        if (senderId.equals(currentUserId)) {
                            previousContacts.add(receiverId);
                        } else if (receiverId.equals(currentUserId)) {
                            previousContacts.add(senderId);
                        }
                    }
                }

                // Kullanıcı adlarını almak için getUserInfo(userId) metodunu kullanarak listeleme işlemini yapabilirsiniz
                List<String> userIds = new ArrayList<>(previousContacts);
                for (String userId : userIds) {
                    getUserInfo(userId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), "Mesajları yüklerken hata oluştu", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getUserInfo(String userId) {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId);

        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String username = dataSnapshot.child("username").getValue(String.class);
                if (username != null) {
                    Message message = new Message();
                    message.setUsername(username);
                    messageList.add(message);
                    messagesAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), "Kullanıcı bilgilerini alırken hata oluştu", Toast.LENGTH_SHORT).show();
            }
        });
    }



    private String getCurrentUserId() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            return currentUser.getUid();
        }
        return null;
    }

    @Override
    public void onItemClick(int position) {
        Message message = messageList.get(position);
        String username = message.getUsername();
        // ChatFragment'ı açmak için fragment geçişini yap
        ChatFragment chatFragment = new ChatFragment();
        Bundle bundle = new Bundle();
        bundle.putString("username", username); // Kullanıcı adını ChatFragment'a iletiyoruz
        chatFragment.setArguments(bundle);

        // Fragment geçişini gerçekleştir
        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.cercevekapsayici, chatFragment)
                .addToBackStack(null)
                .commit();
    }
}
