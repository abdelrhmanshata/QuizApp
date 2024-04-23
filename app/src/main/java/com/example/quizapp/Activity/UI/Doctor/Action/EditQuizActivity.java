package com.example.quizapp.Activity.UI.Doctor.Action;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.quizapp.Model.ModelQuestion;
import com.example.quizapp.Model.ModelQuiz;
import com.example.quizapp.R;
import com.example.quizapp.databinding.ActivityEditQuizBinding;
import com.example.quizapp.databinding.SaveQuizDetailsLayoutBinding;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import es.dmoral.toasty.Toasty;

public class EditQuizActivity extends AppCompatActivity {
    ActivityEditQuizBinding binding;
    private static final long TIME_INTERVAL = 2000; // Time interval in milliseconds
    private long backPressedTime;

    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference refQuizzes = firebaseDatabase.getReference("Quizzes");

    List<ModelQuestion> questions;
    String Question, Choose1, Choose2, Choose3, Choose4;
    int Answer;

    List<String> academicYearList;
    ArrayAdapter academicYearAdapter;
    String academicYear = "First";

    ModelQuiz currentQuiz;
    int currentIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditQuizBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        currentQuiz = (ModelQuiz) getIntent().getSerializableExtra("CurrentQuiz");
        initialize();

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

        academicYearList = new ArrayList<>();
        academicYearList.add("First");
        academicYearList.add("Second");
        academicYearList.add("Third");
        academicYearList.add("Fourth");

        academicYearAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, academicYearList);
        academicYearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

    }

    void setQuizData(ModelQuiz quiz) {
        questions = quiz.getQuestions();
        setQuestionData(questions.get(currentIndex));
        academicYear = currentQuiz.getQuizAcademicYear();
    }

    void setQuestionData(ModelQuestion question) {
        binding.questionText.setText(question.getQuestionText());
        binding.choose1.setText(question.getChoice_1());
        binding.choose2.setText(question.getChoice_2());
        binding.choose3.setText(question.getChoice_3());
        binding.choose4.setText(question.getChoice_4());
        Answer = question.getAnswerNumber();
        binding.answer1.setChecked(Answer == 1);
        binding.answer2.setChecked(Answer == 2);
        binding.answer3.setChecked(Answer == 3);
        binding.answer4.setChecked(Answer == 4);
    }

    void onClickListener() {
        binding.back.setOnClickListener(v -> {
            onBackPressed();
        });
        binding.answer1.setOnClickListener(v -> {
            Answer = 1;
            binding.answer2.setChecked(false);
            binding.answer3.setChecked(false);
            binding.answer4.setChecked(false);
        });
        binding.answer2.setOnClickListener(v -> {
            Answer = 2;
            binding.answer1.setChecked(false);
            binding.answer3.setChecked(false);
            binding.answer4.setChecked(false);
        });
        binding.answer3.setOnClickListener(v -> {
            Answer = 3;
            binding.answer1.setChecked(false);
            binding.answer2.setChecked(false);
            binding.answer4.setChecked(false);
        });
        binding.answer4.setOnClickListener(v -> {
            Answer = 4;
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
        });

        binding.previous.setOnClickListener(v -> {
            if (currentIndex > 0) {
                currentIndex--;
                setQuestionData(questions.get(currentIndex));
            } else {
                Toasty.info(this, getString(R.string.this_is_first_question), Toast.LENGTH_SHORT).show();
            }
        });

        binding.saveQuestion.setOnClickListener(v -> {
            saveQuestion();
        });
        binding.save.setOnClickListener(v -> {
            saveQuiz();
        });
    }

    void saveQuestion() {
        Question = Objects.requireNonNull(binding.questionText.getText()).toString().trim();
        if (Question.isEmpty()) {
            binding.questionText.setError(getString(R.string.enter_question));
            binding.questionText.setFocusable(true);
            binding.questionText.requestFocus();
            return;
        }

        Choose1 = Objects.requireNonNull(binding.choose1.getText()).toString().trim();
        if (Choose1.isEmpty()) {
            binding.choose1.setError(getString(R.string.enter_choice_1));
            binding.choose1.setFocusable(true);
            binding.choose1.requestFocus();
            return;
        }

        Choose2 = Objects.requireNonNull(binding.choose2.getText()).toString().trim();
        if (Choose2.isEmpty()) {
            binding.choose2.setError(getString(R.string.enter_choice_2));
            binding.choose2.setFocusable(true);
            binding.choose2.requestFocus();
            return;
        }

        Choose3 = Objects.requireNonNull(binding.choose3.getText()).toString().trim();
        if (Choose3.isEmpty()) {
            binding.choose3.setError(getString(R.string.enter_choice_3));
            binding.choose3.setFocusable(true);
            binding.choose3.requestFocus();
            return;
        }

        Choose4 = Objects.requireNonNull(binding.choose4.getText()).toString().trim();
        if (Choose4.isEmpty()) {
            binding.choose4.setError(getString(R.string.enter_choice_4));
            binding.choose4.setFocusable(true);
            binding.choose4.requestFocus();
            return;
        }

        if (!binding.answer1.isChecked() && !binding.answer2.isChecked() && !binding.answer3.isChecked() && !binding.answer4.isChecked()) {
            Toasty.info(this, "" + getString(R.string.choose_the_answer_number), Toast.LENGTH_SHORT).show();
            return;
        }

        ModelQuestion question = questions.get(currentIndex);
        question.setQuestionText(Question);
        question.setChoice_1(Choose1);
        question.setChoice_2(Choose2);
        question.setChoice_3(Choose3);
        question.setChoice_4(Choose4);
        question.setAnswerNumber(Answer);
        question.setChoiceNumber(0);

        Toasty.success(this, getString(R.string.done), Toast.LENGTH_SHORT).show();
        questions.set(currentIndex, question);
    }

    void saveQuiz() {
        if (questions.isEmpty()) {
            Toasty.info(this, "" + getString(R.string.msg_add_quiz), Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.save_quiz_details_layout, null);
        dialogBuilder.setView(dialogView);
        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
        Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(0));

        SaveQuizDetailsLayoutBinding saveQuizBinding = SaveQuizDetailsLayoutBinding.bind(dialogView);
        saveQuizBinding.academicYearSpinner.setAdapter(academicYearAdapter);

        saveQuizBinding.subjectTitle.setText(currentQuiz.getQuizSubject());
        saveQuizBinding.quizTitle.setText(currentQuiz.getQuizTitle());
        saveQuizBinding.quizDegree.setText(String.valueOf(currentQuiz.getQuizDegree()));
        saveQuizBinding.academicYearSpinner.setSelection(academicYearList.indexOf(currentQuiz.getQuizAcademicYear()));
        saveQuizBinding.selectDate.setText(currentQuiz.getQuizDate());
        saveQuizBinding.selectSTime.setText(currentQuiz.getQuizSTime());
        saveQuizBinding.selectETime.setText(currentQuiz.getQuizETime());

        saveQuizBinding.academicYearSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                academicYear = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing when nothing is selected
            }
        });

        saveQuizBinding.selectDateBtn.setOnClickListener(v -> {
            showDatePickerDialog(saveQuizBinding.selectDate);
        });
        saveQuizBinding.selectSTimeBtn.setOnClickListener(v -> {
            showTimePickerDialog(saveQuizBinding.selectSTime);
        });
        saveQuizBinding.selectETimeBtn.setOnClickListener(v -> {
            showTimePickerDialog(saveQuizBinding.selectETime);
        });

        saveQuizBinding.saveQuiz.setOnClickListener(v -> {
            String subjectTitle = Objects.requireNonNull(saveQuizBinding.subjectTitle.getText()).toString().trim();
            if (subjectTitle.isEmpty()) {
                saveQuizBinding.subjectTitle.setError(getString(R.string.subject_title_is_required));
                saveQuizBinding.subjectTitle.setFocusable(true);
                saveQuizBinding.subjectTitle.requestFocus();
                return;
            }

            String quizTitle = Objects.requireNonNull(saveQuizBinding.quizTitle.getText()).toString().trim();
            if (quizTitle.isEmpty()) {
                saveQuizBinding.quizTitle.setError(getString(R.string.quiz_title_is_required));
                saveQuizBinding.quizTitle.setFocusable(true);
                saveQuizBinding.quizTitle.requestFocus();
                return;
            }

            String quizDegree = Objects.requireNonNull(saveQuizBinding.quizDegree.getText()).toString().trim();
            if (quizDegree.isEmpty()) {
                saveQuizBinding.quizDegree.setError(getString(R.string.quiz_degree_is_required));
                saveQuizBinding.quizDegree.setFocusable(true);
                saveQuizBinding.quizDegree.requestFocus();
                return;
            }

            String selectDate = Objects.requireNonNull(saveQuizBinding.selectDate.getText()).toString().trim();
            if (selectDate.isEmpty()) {
                saveQuizBinding.selectDate.setError(getString(R.string.quiz_date_is_required));
                saveQuizBinding.selectDate.setFocusable(true);
                saveQuizBinding.selectDate.requestFocus();
                return;
            }

            String selectSTime = Objects.requireNonNull(saveQuizBinding.selectSTime.getText()).toString().trim();
            if (selectSTime.isEmpty()) {
                saveQuizBinding.selectSTime.setError(getString(R.string.quiz_start_time_is_required));
                saveQuizBinding.selectSTime.setFocusable(true);
                saveQuizBinding.selectSTime.requestFocus();
                return;
            }

            String selectETime = Objects.requireNonNull(saveQuizBinding.selectETime.getText()).toString().trim();
            if (selectETime.isEmpty()) {
                saveQuizBinding.selectETime.setError(getString(R.string.quiz_end_time_is_required));
                saveQuizBinding.selectETime.setFocusable(true);
                saveQuizBinding.selectETime.requestFocus();
                return;
            }

            ModelQuiz modelQuiz = currentQuiz;
            modelQuiz.setQuizSubject(subjectTitle);
            modelQuiz.setQuizTitle(quizTitle);
            modelQuiz.setQuizDegree(Double.parseDouble(quizDegree));
            modelQuiz.setQuizAcademicYear(academicYear);
            modelQuiz.setQuizDate(selectDate);
            modelQuiz.setQuizSTime(selectSTime);
            modelQuiz.setQuizETime(selectETime);

            modelQuiz.setQuestions(questions);

//            Save Quiz In Database
            refQuizzes.child(modelQuiz.getQuizID()).setValue(modelQuiz).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toasty.success(this, "" + getString(R.string.msg_quiz_saved), Toast.LENGTH_SHORT).show();
                    alertDialog.dismiss();
                    finish();
                } else {
                    Toasty.error(this, "" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void showDatePickerDialog(TextView textView) {
        Toast.makeText(this, "" + getCurrentDate(), Toast.LENGTH_SHORT).show();
        // Get the current date
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new DatePickerDialog instance
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year1, monthOfYear, dayOfMonth) -> {
            // Display the selected date yyyy-MM-dd
            String selectedDate = year1 + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
            textView.setText(selectedDate);
        }, year, month, day);

        // Show the date picker dialog
        datePickerDialog.show();
    }

    @SuppressLint("DefaultLocale")
    private void showTimePickerDialog(TextView textView) {
        Toast.makeText(this, "" + getCurrentTime(), Toast.LENGTH_SHORT).show();
        // Get the current time
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        // Create a new TimePickerDialog instance
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, (view, hourOfDay, minute1) -> {
            // Display selected time in AM/PM format
            String amPm;
            if (hourOfDay < 12) {
                amPm = "AM";
            } else {
                amPm = "PM";
                if (hourOfDay > 12) {
                    hourOfDay -= 12;
                }
            }
            // Display the selected time
            String selectedTime = String.format(Locale.ENGLISH, "%02d:%02d %s", hourOfDay, minute1, amPm);
            textView.setText(selectedTime);
        }, hour, minute, false); // true for 24-hour format, false for 12-hour format

        // Show the time picker dialog
        timePickerDialog.show();
    }

    @SuppressLint("SimpleDateFormat")
    String getCurrentDate() {
        // Get the current date
        Date currentDate = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-M-dd", Locale.ENGLISH);
        return dateFormat.format(currentDate);
    }

    @SuppressLint("SimpleDateFormat")
    String getCurrentTime() {
        // Get the current time
        Date currentTime = new Date();
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.ENGLISH); // AM/PM format
        return timeFormat.format(currentTime);
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