package com.example.b2bouz;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ChatFragment extends Fragment {

    private ImageView profileImageView;
    private TextView usernameTextView;
    private RecyclerView recyclerViewMessages;
    private ChatAdapter chatAdapter;
    private List<Message> messageList;
    private String userId;

    private EditText messageEditText;

    public ChatFragment() {
        // Boş parametreli yapıcı metot
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.message_screen, container, false);

        // Profil fotoğrafı ve kullanıcı adı görünümlerini tanımla
        profileImageView = view.findViewById(R.id.profileImageView);
        usernameTextView = view.findViewById(R.id.usernameTextView);
        messageEditText = view.findViewById(R.id.messageEditText);

        // RecyclerView'i tanımla
        recyclerViewMessages = view.findViewById(R.id.recyclerViewMessages);

        // RecyclerView için LayoutManager'ı ayarla
        recyclerViewMessages.setLayoutManager(new LinearLayoutManager(getActivity()));

        // Mesajları tutacak liste oluştur
        messageList = new ArrayList<>();

        // ChatAdapter'ı oluştur ve RecyclerView'e bağla
        chatAdapter = new ChatAdapter(messageList);
        recyclerViewMessages.setAdapter(chatAdapter);

        // İlgili kullanıcının profil fotoğrafını ve kullanıcı adını yükle
        loadUserData();

        // Mesajları yükle
        loadMessages();

        // Mesaj gönderme butonunu tanımla ve tıklama olayını belirle
        Button sendMessageButton = view.findViewById(R.id.sendmessageButton);
        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

        return view;
    }

    private void loadUserData() {
        // Kullanıcı kimliğini alın (örneğin, getArguments() ile geçilen bilgiyi kullanabilirsiniz)
        Bundle arguments = getArguments();
        if (arguments != null) {
            userId = arguments.getString("userId");

            // Kullanıcının profil fotoğrafını ve kullanıcı adını çekmek için Firebase veritabanı referansını al
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId);
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    // Veritabanından gelen verileri al
                    String profileImageUrl = dataSnapshot.child("profileImage").getValue(String.class);
                    String username = dataSnapshot.child("username").getValue(String.class);

                    // Profil fotoğrafını ve kullanıcı adını ilgili görünümlere yerleştir
                    Glide.with(getActivity()).load(profileImageUrl).into(profileImageView);
                    usernameTextView.setText(username);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // İstek iptal edildiğinde veya hata oluştuğunda yapılacak işlemler
                    Toast.makeText(getActivity(), "Kullanıcı bilgileri alınamadı", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void loadMessages() {
        // Mesajları getir ve listeyi güncelle
        DatabaseReference messagesRef = FirebaseDatabase.getInstance().getReference().child("messages");

        messagesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                messageList.clear(); // Önceki mesajları temizle
                for (DataSnapshot messageSnapshot : dataSnapshot.getChildren()) {
                    Message message = messageSnapshot.getValue(Message.class);
                    if (message != null && isMessageValid(message)) {
                        messageList.add(message);
                    }
                }

                chatAdapter.notifyDataSetChanged(); // Adapter'i güncelle
                recyclerViewMessages.scrollToPosition(messageList.size() - 1); // Son mesaja kaydır
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Hata durumunda yapılacak işlemleri burada tanımlayabilirsiniz
            }
        });
    }

    private boolean isMessageValid(Message message) {
        if (message == null) {
            return false;
        }
        String currentUserId = getCurrentUserId();

        // Mesajın senderId ve receiverId değerlerini kontrol et
        // Eşleşme varsa true, aksi halde false döndür

        if (message.getSenderId() != null && message.getSenderId().equals(currentUserId) && message.getReceiverId() != null && message.getReceiverId().equals(userId)) {
            return true;
        } else if (message.getSenderId() != null && message.getSenderId().equals(userId) && message.getReceiverId() != null && message.getReceiverId().equals(currentUserId)) {
            return true;
        } else {
            return false;
        }
    }
    private void sendMessage() {
        // Mesajı gönderme işlemini gerçekleştir

        // Mesajı oluştur
        String messageText = messageEditText.getText().toString(); // Kullanıcı tarafından girilen metin
        String senderId = getCurrentUserId(); // Örneğin
        String receiverId = userId; // Alıcı kullanıcının ID'si

        // Mesajı Firebase veritabanına kaydet
        DatabaseReference messagesRef = FirebaseDatabase.getInstance().getReference().child("messages");
        String messageId = messagesRef.push().getKey(); // Yeni bir benzersiz mesaj kimliği al
        Message message = new Message(messageId, messageText, senderId, receiverId);

        // Yeni düğümleri oluştur ve mesajı kaydet
        messagesRef.child(messageId).setValue(message)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Mesaj gönderildiğinde yapılacak işlemler
                        messageEditText.setText(""); // Mesaj girişini temizle
                        recyclerViewMessages.scrollToPosition(messageList.size() - 1); // Son mesaja kaydır
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Mesaj gönderilemediğinde yapılacak işlemler
                        Toast.makeText(getActivity(), "Mesaj gönderilemedi", Toast.LENGTH_SHORT).show();
                    }
                });

    }
    private String getCurrentUserId() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            return currentUser.getUid();
        } else {
            return ""; // Eğer oturum açmış bir kullanıcı yoksa, boş bir kimlik döndür
        }
    }
}
