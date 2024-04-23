package com.example.quizapp.Activity.UI.Doctor.Fragment;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.example.quizapp.Activity.UI.Doctor.Adapter.AdapterNumStudentQuiz;
import com.example.quizapp.Model.ModelQuiz;
import com.example.quizapp.databinding.FragmentExamsBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class ExamsFragment extends Fragment implements AdapterNumStudentQuiz.OnItemClickListener {
    FragmentExamsBinding binding;

    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseUser currentUser = firebaseAuth.getCurrentUser();
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference refQuizzes = firebaseDatabase.getReference("Quizzes");

    List<ModelQuiz> quizzes;
    AdapterNumStudentQuiz adapterNumStudentQuiz;
    List<String> academicYearList;
    ArrayAdapter academicYearAdapter;
    String academicYear = "First";

    public ExamsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentExamsBinding.inflate(inflater, container, false);

        init();
        getQuizzes(academicYear);
        return binding.getRoot();
    }

    void init() {
        quizzes = new ArrayList<>();
        adapterNumStudentQuiz = new AdapterNumStudentQuiz(getContext(), quizzes, this);
        binding.recyclerViewQuizDegree.setAdapter(adapterNumStudentQuiz);

        academicYearList = new ArrayList<>();
        academicYearList.add("First");
        academicYearList.add("Second");
        academicYearList.add("Third");
        academicYearList.add("Fourth");

        academicYearAdapter = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, academicYearList);
        academicYearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.academicYearSpinner.setAdapter(academicYearAdapter);

        binding.academicYearSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                academicYear = parent.getItemAtPosition(position).toString();
                getQuizzes(academicYear);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing when nothing is selected
            }
        });
    }

    private void getQuizzes(String academicYearSelect) {
        refQuizzes.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                quizzes.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ModelQuiz quiz = snapshot.getValue(ModelQuiz.class);
                    if (quiz != null) {
                        if (quiz.getUserID().equals(currentUser.getUid()) && quiz.getQuizAcademicYear().equals(academicYearSelect))
                            quizzes.add(quiz);
                    }
                }
                adapterNumStudentQuiz.notifyDataSetChanged();
                binding.emptyImage.setVisibility(quizzes.isEmpty() ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "" + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onItem_Click(int position) {

    }
}