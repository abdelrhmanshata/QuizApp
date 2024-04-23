package com.example.quizapp.Activity.UI.Student.Action;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.quizapp.Model.ModelQuestion;
import com.example.quizapp.Model.ModelQuiz;
import com.example.quizapp.Model.ModelQuizDegree;
import com.example.quizapp.Model.ModelUser;
import com.example.quizapp.R;
import com.example.quizapp.databinding.ActivityQuizBinding;
import com.example.quizapp.databinding.QuizDegreeLayoutBinding;
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

public class QuizActivity extends AppCompatActivity {

    private static final long TIME_INTERVAL = 2000; // Time interval in milliseconds
    ActivityQuizBinding binding;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference refUsers = firebaseDatabase.getReference("Users");
    DatabaseReference refDegrees = firebaseDatabase.getReference("Degrees");

    ModelUser currentUser;
    List<ModelQuestion> questions;
    int Answer;
    ModelQuiz currentQuiz;
    int currentIndex = 0;
    private long backPressedTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityQuizBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        currentQuiz = (ModelQuiz) getIntent().getSerializableExtra("CurrentQuiz");

        initialize();
        getUserData();

        if (currentQuiz != null) {
            setQuizData(currentQuiz);
        } else {
            Toasty.error(this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT, true).show();
            onBackPressed();
            onBackPressed();
        }

        onClickListener();
    }

    void initialize() {
        questions = new ArrayList<>();
    }

    void setQuizData(ModelQuiz quiz) {
        questions = quiz.getQuestions();
        setQuestionData(questions.get(currentIndex));
    }

    void setQuestionData(ModelQuestion question) {
        binding.questionText.setText(question.getQuestionText());
        binding.choose1.setText(question.getChoice_1());
        binding.choose2.setText(question.getChoice_2());
        binding.choose3.setText(question.getChoice_3());
        binding.choose4.setText(question.getChoice_4());
        int Choice = question.getChoiceNumber();
        binding.answer1.setChecked(Choice == 1);
        binding.answer2.setChecked(Choice == 2);
        binding.answer3.setChecked(Choice == 3);
        binding.answer4.setChecked(Choice == 4);
    }

    void onClickListener() {
        binding.back.setOnClickListener(v -> {
            onBackPressed();
        });
        binding.answer1.setOnClickListener(v -> {
            Answer = 1;
            questions.get(currentIndex).setChoiceNumber(Answer);
            binding.answer2.setChecked(false);
            binding.answer3.setChecked(false);
            binding.answer4.setChecked(false);
        });
        binding.answer2.setOnClickListener(v -> {
            Answer = 2;
            questions.get(currentIndex).setChoiceNumber(Answer);
            binding.answer1.setChecked(false);
            binding.answer3.setChecked(false);
            binding.answer4.setChecked(false);
        });
        binding.answer3.setOnClickListener(v -> {
            Answer = 3;
            questions.get(currentIndex).setChoiceNumber(Answer);
            binding.answer1.setChecked(false);
            binding.answer2.setChecked(false);
            binding.answer4.setChecked(false);
        });
        binding.answer4.setOnClickListener(v -> {
            Answer = 4;
            questions.get(currentIndex).setChoiceNumber(Answer);
            binding.answer1.setChecked(false);
            binding.answer2.setChecked(false);
            binding.answer3.setChecked(false);
        });

        binding.next.setOnClickListener(v -> {
            if (currentIndex < questions.size() - 1) {
                currentIndex++;
                setQuestionData(questions.get(currentIndex));
            } else {
                Toasty.info(this, getString(R.string.this_is_final_question), Toast.LENGTH_SHORT).show();
            }

            if (currentIndex == questions.size() - 1) {
                binding.submitAnswer.setVisibility(View.VISIBLE);
            }
        });

        binding.previous.setOnClickListener(v -> {
            if (currentIndex > 0) {
                currentIndex--;
                setQuestionData(questions.get(currentIndex));
            } else {
                Toasty.info(this, getString(R.string.this_is_first_question), Toast.LENGTH_SHORT).show();
            }

            if (currentIndex != questions.size() - 1) {
                binding.submitAnswer.setVisibility(View.GONE);
            }
        });

        binding.submitAnswer.setOnClickListener(v -> {
            AutomaticCorrection();
        });
    }

    void AutomaticCorrection() {
        double studentCorrectAnswer = 0.0;
        for (ModelQuestion question : questions) {
            if (question.getChoiceNumber() == question.getAnswerNumber()) {
                studentCorrectAnswer++;
            }
        }

        studentCorrectAnswer = (studentCorrectAnswer / questions.size());
        double studentDegree = studentCorrectAnswer * (double) currentQuiz.getQuizDegree();
        showDialogExamScore(studentDegree, currentQuiz.getQuizDegree());
    }

    private void showDialogExamScore(double score, double Total) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.quiz_degree_layout, null);
        dialogBuilder.setView(dialogView);
        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
        Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(0));

        QuizDegreeLayoutBinding degreeLayoutBinding = QuizDegreeLayoutBinding.bind(dialogView);

        if (score == 0) {
            degreeLayoutBinding.Congratulation.setAnimation(R.raw.sad_tear);
        } else if (score <= (Total / 2.0)) {
            degreeLayoutBinding.Congratulation.setAnimation(R.raw.sad_face);
        } else {
            degreeLayoutBinding.Congratulation.setAnimation(R.raw.trophy_animation);
        }

        TextView examDegree = dialogView.findViewById(R.id.yourScore);
        examDegree.setText(score + " / " + Total);
        Button ok = dialogView.findViewById(R.id.btnOK);

        ok.setOnClickListener(v -> {
            String ID = currentQuiz.getQuizID() + ":" + firebaseUser.getUid();
            ModelQuizDegree modelQuizDegree = new ModelQuizDegree();
            modelQuizDegree.setID(ID);
            modelQuizDegree.setStudentID(firebaseUser.getUid());
            modelQuizDegree.setQuizID(currentQuiz.getQuizID());
            modelQuizDegree.setStudentDegree(String.valueOf(score));
            modelQuizDegree.setQuizDegree(String.valueOf(currentQuiz.getQuizDegree()));
            modelQuizDegree.setStudentName(currentUser.getUserFullName());
            modelQuizDegree.setQuizTitle(currentQuiz.getQuizTitle());
            modelQuizDegree.setQuizSubject(currentQuiz.getQuizSubject());
            modelQuizDegree.setQuizAcademicYear(currentQuiz.getQuizAcademicYear());

            refDegrees.child(ID).setValue(modelQuizDegree).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    alertDialog.dismiss();
                    finish();
                }
            });
        });
    }

    void getUserData() {
        refUsers.child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ModelUser user = dataSnapshot.getValue(ModelUser.class);
                if (user != null) {
                    currentUser = user;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(QuizActivity.this, "" + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (backPressedTime + TIME_INTERVAL > System.currentTimeMillis()) {
            // If the time difference is less than the interval, exit the app
            super.onBackPressed();
        } else {
            // Show a toast message indicating that the user should press back again to exit
            Toasty.info(this, getString(R.string.press_back_again_to_exit), Toast.LENGTH_SHORT).show();
        }
        backPressedTime = System.currentTimeMillis();
    }
}