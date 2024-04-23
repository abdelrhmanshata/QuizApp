package com.example.quizapp.Activity.UI.Doctor.Fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.example.quizapp.Activity.UI.Doctor.Action.AddQuizActivity;
import com.example.quizapp.Activity.UI.Doctor.Action.StudentDegreeActivity;
import com.example.quizapp.Activity.UI.Doctor.Adapter.AdapterQuiz;
import com.example.quizapp.Model.ModelQuiz;
import com.example.quizapp.databinding.FragmentHomeBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment implements AdapterQuiz.OnItemClickListener {

    FragmentHomeBinding binding;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseUser currentUser = firebaseAuth.getCurrentUser();
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference refQuizzes = firebaseDatabase.getReference("Quizzes");

    List<ModelQuiz> quizzes;
    AdapterQuiz adapterQuiz;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);

        init();
        getQuizzes();
        onClickListener();

        return binding.getRoot();
    }


    void init() {
        quizzes = new ArrayList<>();
        adapterQuiz = new AdapterQuiz(getContext(), quizzes, this);
        binding.recyclerViewQuiz.setAdapter(adapterQuiz);
    }


    private void getQuizzes() {
        binding.progressCircular.setVisibility(View.VISIBLE);
        refQuizzes.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                quizzes.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ModelQuiz quiz = snapshot.getValue(ModelQuiz.class);
                    if (quiz != null) {
                        if (quiz.getUserID().equals(currentUser.getUid())) quizzes.add(quiz);
                    }
                }
                binding.progressCircular.setVisibility(View.GONE);
                adapterQuiz.notifyDataSetChanged();
                if (quizzes.isEmpty()) binding.emptyImage.setVisibility(View.VISIBLE);
                else binding.emptyImage.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                binding.progressCircular.setVisibility(View.GONE);
            }
        });
    }


    void onClickListener() {
        binding.addQuizExam.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), AddQuizActivity.class));
        });
    }


    @Override
    public void onItem_Click(int position) {
        ModelQuiz quiz = quizzes.get(position);
        startActivity(new Intent(getActivity(), StudentDegreeActivity.class).putExtra("QuizID", quiz.getQuizID()));
    }
}