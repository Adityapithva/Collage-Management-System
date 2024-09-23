package com.example.cms;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;
public class RegisterActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private EditText nameEditText, emailEditText, passwordEditText, registrationCodeEditText;
    private ImageView profileImageView;
    private Button uploadImageButton, registerButton;
    private TextView loginTextView;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private StorageReference storageReference;
    private Intent i;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_register);

        // Initialize Intent
        i = new Intent(RegisterActivity.this, LoginActivity.class);

        // Initialize Firebase services
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference("profile_images");

        // Initialize UI components
        loginTextView = findViewById(R.id.loginTextView);
        nameEditText = findViewById(R.id.nameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        registrationCodeEditText = findViewById(R.id.registrationCodeEditText);
        profileImageView = findViewById(R.id.profileImageView);
        uploadImageButton = findViewById(R.id.uploadImageButton);
        registerButton = findViewById(R.id.registerButton);

        // Set listener for image upload
        uploadImageButton.setOnClickListener(v -> openImageChooser());

        // Set listener for registration button
        registerButton.setOnClickListener(v -> registerUser());

        // Set listener for login TextView
        loginTextView.setOnClickListener(v -> startActivity(i));
    }

    private void openImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            if (imageUri != null) {
                profileImageView.setImageURI(imageUri);
            } else {
                Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void registerUser() {
        String name = nameEditText.getText().toString();
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String registrationCode = registrationCodeEditText.getText().toString();

        // Validate registration code
        if (!isValidRegistrationCode(registrationCode)) {
            Toast.makeText(this, "Invalid registration code!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate inputs
        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all required fields!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Register the user
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            uploadUserDataToFirestore(user, name, email, registrationCode, imageUri);
                        }
                        Toast.makeText(RegisterActivity.this, "Registration successful", Toast.LENGTH_SHORT).show();
                        startActivity(i);
                    } else {
                        Toast.makeText(RegisterActivity.this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private boolean isValidRegistrationCode(String code) {
        return code.equals("STUDENT123") || code.equals("FACULTY456");
    }

    private void uploadUserDataToFirestore(FirebaseUser user, String name, String email, String registrationCode, Uri imageUri) {
        String userId = user.getUid();

        if (imageUri != null) {
            StorageReference imageRef = storageReference.child(userId + ".jpg");
            imageRef.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> imageRef.getDownloadUrl()
                            .addOnSuccessListener(uri -> storeUserData(userId, name, email, registrationCode, uri.toString()))
                            .addOnFailureListener(e -> Toast.makeText(RegisterActivity.this, "Image upload failed", Toast.LENGTH_SHORT).show())
                    );
        } else {
            storeUserData(userId, name, email, registrationCode, null);
        }
    }

    private void storeUserData(String userId, String name, String email, String registrationCode, @Nullable String imageUrl) {
        Map<String, Object> userData = new HashMap<>();
        userData.put("name", name);
        userData.put("email", email);
        userData.put("registrationCode", registrationCode);
        if (imageUrl != null) {
            userData.put("profileImageUrl", imageUrl);
        }

        db.collection("users").document(userId).set(userData)
                .addOnSuccessListener(aVoid -> Toast.makeText(RegisterActivity.this, "User data stored successfully", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(RegisterActivity.this, "Failed to store user data", Toast.LENGTH_SHORT).show());
    }
}
