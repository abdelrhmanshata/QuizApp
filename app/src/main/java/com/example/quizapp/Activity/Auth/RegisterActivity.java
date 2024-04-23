package com.example.quizapp.Activity.Auth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.quizapp.Model.ModelUser;
import com.example.quizapp.R;
import com.example.quizapp.databinding.ActivityRegisterBinding;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {

    ActivityRegisterBinding binding;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference refUsers = database.getReference("Users");

    List<String> typeOfUserList;
    ArrayAdapter typeOfUserAdapter;
    String typeOfUser = "Doctor";

    List<String> academicYearList;
    ArrayAdapter academicYearAdapter;
    String academicYear = "First";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initialize();
        onClickListener();
    }

    void initialize() {
        typeOfUserList = new ArrayList<>();
        typeOfUserList.add("Doctor");
        typeOfUserList.add("Student");
        typeOfUserAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, typeOfUserList);
        typeOfUserAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.userTypeSpinner.setAdapter(typeOfUserAdapter);

        academicYearList = new ArrayList<>();
        academicYearList.add("First");
        academicYearList.add("Second");
        academicYearList.add("Third");
        academicYearList.add("Fourth");

        academicYearAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, academicYearList);
        academicYearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.academicYearSpinner.setAdapter(academicYearAdapter);

    }

    void onClickListener() {
        binding.btnLogin.setOnClickListener(v -> {
            onBackPressed();
        });
        binding.userTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                typeOfUser = parent.getItemAtPosition(position).toString();
                binding.academicYearLayout.setVisibility(typeOfUser.equals("Doctor") ? View.GONE : View.VISIBLE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing when nothing is selected
            }
        });

        binding.academicYearSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                academicYear = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing when nothing is selected
            }
        });

        binding.registerBtn.setOnClickListener(v -> {
            checkUserInput();
        });
    }

    void checkUserInput() {
        binding.progressCircular.setVisibility(View.VISIBLE);

        String inputFullName = Objects.requireNonNull(binding.inputFullName.getText()).toString().trim();
        if (inputFullName.isEmpty()) {
            binding.inputFullName.setError(getString(R.string.fullNameIsRequired));
            binding.inputFullName.requestFocus();
            binding.progressCircular.setVisibility(View.INVISIBLE);
            return;
        }

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

        String inputPassword = Objects.requireNonNull(binding.inputPassword.getText()).toString().trim();
        if (inputPassword.isEmpty()) {
            binding.inputPassword.setError(getString(R.string.passwordIsRequired));
            binding.inputPassword.requestFocus();
            binding.progressCircular.setVisibility(View.INVISIBLE);
            return;
        }
        if (inputPassword.length() < 8) {
            binding.inputPassword.setError(getString(R.string.minimumLength));
            binding.inputPassword.requestFocus();
            binding.progressCircular.setVisibility(View.INVISIBLE);
            return;
        }

        ModelUser modelUser = new ModelUser();
        modelUser.setUserType(typeOfUser);
        modelUser.setUserFullName(inputFullName);
        modelUser.setUserEmail(inputEmail);
        modelUser.setUserPassword(inputPassword);
        modelUser.setUserAcademicYear(academicYear);
        RegisterUser(modelUser);
    }


    public void RegisterUser(ModelUser user) {
        firebaseAuth.createUserWithEmailAndPassword(user.getUserEmail(), user.getUserPassword()).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseUser CurrentUser = firebaseAuth.getCurrentUser();
                assert CurrentUser != null;
                user.setID(CurrentUser.getUid());

                refUsers.child(user.getID()).setValue(user).addOnSuccessListener(unused -> {
                    binding.progressCircular.setVisibility(View.INVISIBLE);
                    Toast.makeText(this, getString(R.string.successRegistered), Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(this, LoginActivity.class));
                    ActivityCompat.finishAffinity(this);
                }).addOnFailureListener(e -> {
                    binding.progressCircular.setVisibility(View.INVISIBLE);
                    if (e instanceof FirebaseAuthUserCollisionException) {
                        Toast.makeText(this, getString(R.string.AlreadyRegistered), Toast.LENGTH_SHORT).show();
                    } else if (e instanceof FirebaseNetworkException) {
                        Toast.makeText(this, getString(R.string.noConnection), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Exception -> " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnFailureListener(e -> {
            binding.progressCircular.setVisibility(View.INVISIBLE);
            if (e instanceof FirebaseAuthUserCollisionException) {
                Toast.makeText(this, getString(R.string.AlreadyRegistered), Toast.LENGTH_SHORT).show();
            } else if (e instanceof FirebaseNetworkException) {
                Toast.makeText(this, getString(R.string.noConnection), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Exception -> " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}