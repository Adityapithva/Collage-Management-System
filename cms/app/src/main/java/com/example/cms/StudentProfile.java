package com.example.cms;

import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class StudentProfile extends AppCompatActivity {

    private ImageView profileImageView;
    private TextView nameTextView,enrollmentTextView,emailTextView;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_profile);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        profileImageView = findViewById(R.id.profileImageView);
        nameTextView = findViewById(R.id.nameTextView);
        emailTextView = findViewById(R.id.emailTextView);
        enrollmentTextView = findViewById(R.id.enrollmentTextView);

        loadUserProfile();
    }

    private void loadUserProfile(){
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            String userId = currentUser.getUid();
            db.collection("users").document(userId).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if(documentSnapshot.exists()){
                            String name = documentSnapshot.getString("name");
                            String email = documentSnapshot.getString("email");
                            String enrollmentNumber = documentSnapshot.getString("enrollmentNumber");
                            String profileImageUrl = documentSnapshot.getString("profileImageUrl");
                            nameTextView.setText(name);
                            emailTextView.setText(email);
                            enrollmentTextView.setText(enrollmentNumber);
                            if(profileImageUrl != null && !profileImageUrl.isEmpty()){
                                Glide.with(StudentProfile.this)
                                        .load(Uri.parse(profileImageUrl))
                                        .into(profileImageView);
                            }else{
                                profileImageView.setImageResource(R.drawable.defaultphoto);
                            }
                        }else{

                            Toast.makeText(StudentProfile.this, "Profile not found!", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> Toast.makeText(StudentProfile.this, "Failed to load profile!", Toast.LENGTH_SHORT).show());
        }
    }
}