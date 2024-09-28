package com.example.cms;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class FacultyProfileActivity extends AppCompatActivity {

    private TextView facultyNameTextView, facultyEmailTextView, facultyDepartmentTextView;
    private ImageView facultyProfileImageView;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faculty_profile);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();


        facultyNameTextView = findViewById(R.id.facultyNameTextView);
        facultyEmailTextView = findViewById(R.id.facultyEmailTextView);
        facultyDepartmentTextView = findViewById(R.id.facultyDepartmentTextView);
        facultyProfileImageView = findViewById(R.id.facultyProfileImageView);


        loadFacultyData();
    }

    private void loadFacultyData() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {

            DocumentReference docRef = db.collection("users").document(user.getUid());
            docRef.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    String name = documentSnapshot.getString("name");
                    String email = documentSnapshot.getString("email");
                    String profileImageUrl = documentSnapshot.getString("profileImageUrl");


                    facultyNameTextView.setText("Name: " + name);
                    facultyEmailTextView.setText("Email: " + email);


                    facultyDepartmentTextView.setText("Department: Computer");


                    if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
                        Glide.with(FacultyProfileActivity.this)
                                .load(profileImageUrl)
                                .into(facultyProfileImageView);
                    } else {
                        facultyProfileImageView.setImageResource(R.drawable.defaultphoto);
                    }
                }
            }).addOnFailureListener(e -> {

            });
        }
    }
}
