package com.example.quizapp.Activity.UI.Doctor.Action;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.quizapp.Activity.UI.Doctor.Adapter.AdapterStudentDegree;
import com.example.quizapp.Model.ModelQuizDegree;
import com.example.quizapp.databinding.ActivityStudentDegreeBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class StudentDegreeActivity extends AppCompatActivity implements AdapterStudentDegree.OnItemClickListener {

    ActivityStudentDegreeBinding binding;

    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference refUsers = firebaseDatabase.getReference("Users");
    DatabaseReference refQuizzes = firebaseDatabase.getReference("Quizzes");
    DatabaseReference refDegrees = firebaseDatabase.getReference("Degrees");

    List<ModelQuizDegree> quizzesDegrees;
    AdapterStudentDegree adapterStudentDegree;

    String QuizID = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStudentDegreeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        QuizID = getIntent().getStringExtra("QuizID");

        quizzesDegrees = new ArrayList<>();
        adapterStudentDegree = new AdapterStudentDegree(this, quizzesDegrees, this);
        binding.recyclerViewStudents.setAdapter(adapterStudentDegree);


        binding.back.setOnClickListener(v -> {
            onBackPressed();
        });

        getQuizzesDegrees(QuizID);

    }

    private void getQuizzesDegrees(String quizID) {
        binding.progressCircular.setVisibility(View.VISIBLE);
        refDegrees.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                quizzesDegrees.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ModelQuizDegree quizDegree = snapshot.getValue(ModelQuizDegree.class);
                    if (quizDegree != null) {
                        if (quizDegree.getQuizID().equals(quizID)) {
                            quizzesDegrees.add(quizDegree);
                        }
                    }
                }
                binding.progressCircular.setVisibility(View.GONE);
                adapterStudentDegree.notifyDataSetChanged();
                binding.emptyImage.setVisibility(quizzesDegrees.isEmpty() ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                binding.progressCircular.setVisibility(View.GONE);
                Toast.makeText(StudentDegreeActivity.this, "" + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onItem_Click(int position) {

    }
}