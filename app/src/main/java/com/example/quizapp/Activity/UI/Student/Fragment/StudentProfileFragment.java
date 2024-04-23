package com.example.quizapp.Activity.UI.Student.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.example.quizapp.Activity.Auth.LoginActivity;
import com.example.quizapp.Model.ModelUser;
import com.example.quizapp.R;
import com.example.quizapp.databinding.FragmentStudentProfileBinding;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import es.dmoral.toasty.Toasty;


public class StudentProfileFragment extends Fragment {

    FragmentStudentProfileBinding binding;

    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user = auth.getCurrentUser();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference refUsers = database.getReference("Users");
    ModelUser currentUser;

    List<String> academicYearList;
    ArrayAdapter academicYearAdapter;
    String academicYear = "First";

    public StudentProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentStudentProfileBinding.inflate(inflater, container, false);

        init();
        getUserData();

        binding.saveBtn.setOnClickListener(v -> {
            checkUserInput();
        });

        binding.logout.setOnClickListener(v -> {
            auth.signOut();
            Toasty.warning(requireContext(), getString(R.string.logout), Toast.LENGTH_SHORT).show();
            startActivity(new Intent(requireContext(), LoginActivity.class));
            ActivityCompat.finishAffinity(getActivity());
        });

        return binding.getRoot();
    }

    void init() {
        academicYearList = new ArrayList<>();
        academicYearList.add("First");
        academicYearList.add("Second");
        academicYearList.add("Third");
        academicYearList.add("Fourth");

        academicYearAdapter = new ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, academicYearList);
        academicYearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.academicYearSpinner.setAdapter(academicYearAdapter);

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
    }


    void getUserData() {
        binding.progressCircle.setVisibility(View.VISIBLE);
        refUsers.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ModelUser user = dataSnapshot.getValue(ModelUser.class);
                if (user != null) {
                    currentUser = user;
                    binding.inputEmail.setText(user.getUserEmail());
                    binding.inputFullName.setText(user.getUserFullName());
                    binding.academicYearSpinner.setSelection(academicYearList.indexOf(user.getUserAcademicYear()));
                }
                binding.progressCircle.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                binding.progressCircle.setVisibility(View.INVISIBLE);
            }
        });
    }

    void checkUserInput() {
        binding.progressCircle.setVisibility(View.VISIBLE);
        String inputFullName = Objects.requireNonNull(binding.inputFullName.getText()).toString().trim();
        if (inputFullName.isEmpty()) {
            binding.inputFullName.setError(getString(R.string.fullNameIsRequired));
            binding.inputFullName.requestFocus();
            binding.progressCircle.setVisibility(View.INVISIBLE);
            return;
        }
        currentUser.setUserFullName(inputFullName);
        currentUser.setUserAcademicYear(academicYear);
        updateUser(currentUser);
    }

    void updateUser(ModelUser user) {
        refUsers.child(user.getID()).setValue(user).addOnSuccessListener(unused -> {
            binding.progressCircle.setVisibility(View.INVISIBLE);
            Toasty.success(requireContext(), getString(R.string.update_successfully), Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(e -> {
            binding.progressCircle.setVisibility(View.INVISIBLE);
            if (e instanceof FirebaseNetworkException) {
                Toast.makeText(requireContext(), getString(R.string.noConnection), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(requireContext(), "Exception -> " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}