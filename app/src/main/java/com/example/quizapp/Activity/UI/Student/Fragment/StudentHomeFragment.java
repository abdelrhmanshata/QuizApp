package com.example.quizapp.Activity.UI.Student.Fragment;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.quizapp.Activity.UI.Student.Adapter.AdapterStartQuiz;
import com.example.quizapp.Model.ModelQuiz;
import com.example.quizapp.Model.ModelUser;
import com.example.quizapp.databinding.FragmentStudentHomeBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class StudentHomeFragment extends Fragment implements AdapterStartQuiz.OnItemClickListener, SwipeRefreshLayout.OnRefreshListener {

    FragmentStudentHomeBinding binding;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference refUsers = firebaseDatabase.getReference("Users");
    DatabaseReference refQuizzes = firebaseDatabase.getReference("Quizzes");

    ModelUser currentUser;

    List<ModelQuiz> quizzes;
    AdapterStartQuiz adapterQuiz;

    public StudentHomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentStudentHomeBinding.inflate(inflater, container, false);

        init();
        getUserData();

        return binding.getRoot();
    }

    void init() {
        quizzes = new ArrayList<>();
        adapterQuiz = new AdapterStartQuiz(getContext(), quizzes, this);
        binding.recyclerViewQuiz.setAdapter(adapterQuiz);
        binding.swipeRefreshLayout.setOnRefreshListener(this);
    }

    void getUserData() {
        binding.progressCircular.setVisibility(View.VISIBLE);
        refUsers.child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ModelUser user = dataSnapshot.getValue(ModelUser.class);
                if (user != null) {
                    currentUser = user;
                    getQuizzes();
                }
                binding.progressCircular.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                binding.progressCircular.setVisibility(View.INVISIBLE);
            }
        });
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
                        if (quiz.getQuizAcademicYear().equals(currentUser.getUserAcademicYear()))
                            quizzes.add(quiz);
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

    @Override
    public void onItem_Click(int position) {

    }


    @Override
    public void onRefresh() {
        new Handler().postDelayed(() -> {
            binding.swipeRefreshLayout.setRefreshing(false);
            getQuizzes();
        }, 1000);
    }
}