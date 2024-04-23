package com.example.quizapp.Activity.UI.Student.Fragment;

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

import com.example.quizapp.Activity.UI.Student.Adapter.AdapterDegreeQuiz;
import com.example.quizapp.Model.ModelQuizDegree;
import com.example.quizapp.Model.ModelUser;
import com.example.quizapp.databinding.FragmentStudentDegreesBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class StudentDegreesFragment extends Fragment implements AdapterDegreeQuiz.OnItemClickListener {

    FragmentStudentDegreesBinding binding;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference refUsers = firebaseDatabase.getReference("Users");
    DatabaseReference refDegrees = firebaseDatabase.getReference("Degrees");

    ModelUser currentUser;

    List<ModelQuizDegree> quizzesDegrees;
    AdapterDegreeQuiz adapterDegreeQuiz;

    List<String> academicYearList;
    ArrayAdapter academicYearAdapter;
    String academicYear = "First";

    public StudentDegreesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentStudentDegreesBinding.inflate(inflater, container, false);

        init();
        getUserData();

        return binding.getRoot();
    }

    void init() {
        quizzesDegrees = new ArrayList<>();
        adapterDegreeQuiz = new AdapterDegreeQuiz(getContext(), quizzesDegrees, this);
        binding.recyclerViewQuizDegree.setAdapter(adapterDegreeQuiz);

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
                getQuizzesDegrees(academicYear);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing when nothing is selected
            }
        });
    }

    void getUserData() {
        refUsers.child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ModelUser user = dataSnapshot.getValue(ModelUser.class);
                if (user != null) {
                    currentUser = user;
                    academicYear = user.getUserAcademicYear();
                    binding.academicYearSpinner.setSelection(academicYearList.indexOf(user.getUserAcademicYear()));
                    getQuizzesDegrees(academicYear);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "" + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getQuizzesDegrees(String quizAcademicYear) {
        refDegrees.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                quizzesDegrees.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ModelQuizDegree quizDegree = snapshot.getValue(ModelQuizDegree.class);
                    if (quizDegree != null) {
                        if (quizDegree.getStudentID().equals(firebaseUser.getUid())) {
                            if (quizDegree.getQuizAcademicYear().equals(quizAcademicYear))
                                quizzesDegrees.add(quizDegree);
                        }
                    }
                }

                adapterDegreeQuiz.notifyDataSetChanged();
                binding.emptyImage.setVisibility(quizzesDegrees.isEmpty() ? View.VISIBLE : View.GONE);

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