package com.example.quizapp.Activity.Auth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.quizapp.R;
import com.example.quizapp.databinding.ActivityForgetPasswordBinding;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class ForgetPasswordActivity extends AppCompatActivity {

    ActivityForgetPasswordBinding binding;
    FirebaseAuth auth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityForgetPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        onClickListener();
    }

    void onClickListener() {

        binding.btnForgetPassword.setOnClickListener(v -> {
            checkUserInput();
        });

        binding.btnRegister.setOnClickListener(v -> {
            startActivity(new Intent(ForgetPasswordActivity.this, RegisterActivity.class));
            ActivityCompat.finishAffinity(this);
        });
    }

    void checkUserInput() {
        binding.progressCircular.setVisibility(View.VISIBLE);
        String inputEmail = Objects.requireNonNull(binding.inputEmail.getText()).toString().trim();
        if (inputEmail.isEmpty()) {
            binding.inputEmail.setError(getString(R.string.emailIsRequired));
            binding.inputEmail.requestFocus();
            binding.progressCircular.setVisibility(View.INVISIBLE);
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(inputEmail).matches()) {
            binding.inputEmail.setError(getString(R.string.please_enter_valid_email));
            binding.inputEmail.requestFocus();
            binding.progressCircular.setVisibility(View.INVISIBLE);
            return;
        }

        auth.sendPasswordResetEmail(inputEmail).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(ForgetPasswordActivity.this, R.string.reset_password_link_sent_to_your_email, Toast.LENGTH_SHORT).show();
                onBackPressed();
            } else {
                String message = Objects.requireNonNull(task.getException()).getMessage();
                Toast.makeText(ForgetPasswordActivity.this, "Reset Password Failed " + message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}