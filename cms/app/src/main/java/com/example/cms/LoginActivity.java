package com.example.cms;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private EditText editTextEmail, editTextPassword, editTextLoginCode;
    private Button buttonLogin;
    private ProgressBar progressBar;
    private TextView textViewRegister;

    private FirebaseAuth auth;

    // Codes for student and faculty
    private static final String STUDENT_CODE = "STUDENT123";
    private static final String FACULTY_CODE = "FACULTY456";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auth = FirebaseAuth.getInstance();

        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextLoginCode = findViewById(R.id.editTextLoginCode);
        buttonLogin = findViewById(R.id.buttonLogin);
        progressBar = findViewById(R.id.progressBar);
        textViewRegister = findViewById(R.id.textViewRegister);

        buttonLogin.setOnClickListener(v -> loginUser());

        textViewRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    private void loginUser() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String loginCode = editTextLoginCode.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            editTextEmail.setError("Email is required");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            editTextPassword.setError("Password is required");
            return;
        }

        if (TextUtils.isEmpty(loginCode)) {
            editTextLoginCode.setError("Login code is required");
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        if (loginCode.equals(STUDENT_CODE)) {
                            Toast.makeText(LoginActivity.this, "Login as student successful", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(LoginActivity.this, StudentDashboardActivity.class);
                            startActivity(intent);
                            finish();
                        } else if (loginCode.equals(FACULTY_CODE)) {
                            Toast.makeText(LoginActivity.this, "Login as faculty successful", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(LoginActivity.this, FacultyDashboardActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(LoginActivity.this, "Invalid login code", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(LoginActivity.this, "Login failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
