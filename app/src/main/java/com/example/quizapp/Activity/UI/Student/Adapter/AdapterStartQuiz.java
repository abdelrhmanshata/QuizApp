package com.example.quizapp.Activity.UI.Student.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quizapp.Activity.UI.Student.Action.QuizActivity;
import com.example.quizapp.Model.ModelQuiz;
import com.example.quizapp.R;
import com.example.quizapp.databinding.ItemStudentQuizLayoutBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import es.dmoral.toasty.Toasty;

public class AdapterStartQuiz extends RecyclerView.Adapter<AdapterStartQuiz.ViewHolder> {
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseUser currentUser = firebaseAuth.getCurrentUser();
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference refDegrees = firebaseDatabase.getReference("Degrees");
    Context context;
    List<ModelQuiz> mQuizzes;
    private OnItemClickListener mListener;

    public AdapterStartQuiz(Context context, List<ModelQuiz> quizs, OnItemClickListener mListener) {
        this.context = context;
        this.mQuizzes = quizs;
        this.mListener = mListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        ItemStudentQuizLayoutBinding binding = ItemStudentQuizLayoutBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    @SuppressLint({"SimpleDateFormat", "ResourceAsColor", "SetTextI18n"})
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ModelQuiz quiz = mQuizzes.get(position);

        holder.binding.quizTitle.setText(quiz.getQuizTitle());
        holder.binding.quizSubject.setText("Subject : " + quiz.getQuizSubject());
        holder.binding.quizNumQuestions.setText(String.valueOf(quiz.getQuestions().size()));
        holder.binding.quizDegree.setText(String.valueOf(quiz.getQuizDegree()));
        holder.binding.quizDate.setText(quiz.getQuizDate());
        holder.binding.quizStartTime.setText(quiz.getQuizSTime());
        holder.binding.quizEndTime.setText(quiz.getQuizETime());

        try {
            if (checkDate(quiz.getQuizDate()) == 0) {
                holder.binding.quizStatus.setText("Today");
                holder.binding.quizStatus.setTextColor(context.getColor(R.color.mainColor));
            } else if (checkDate(quiz.getQuizDate()) > 0) {
                holder.binding.quizStatus.setText("Expired");
                holder.binding.quizStatus.setTextColor(context.getColor(R.color.secondColor));
            } else {
                holder.binding.quizStatus.setText("Pending");
                holder.binding.quizStatus.setTextColor(context.getColor(R.color.gray));
            }


            if (checkDate(quiz.getQuizDate()) == 0 && checkTime(quiz.getQuizSTime(), quiz.getQuizETime())) {
                holder.binding.startQuiz.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.mainColor));
                holder.binding.startQuiz.setText("Start Quiz");
            } else if (checkDate(quiz.getQuizDate()) > 0) {
                holder.binding.startQuiz.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.secondColor));
                holder.binding.startQuiz.setText("Expired Quiz");
            } else {
                holder.binding.startQuiz.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.gray));
                holder.binding.startQuiz.setText("Pending Quiz");
            }


            holder.binding.startQuiz.setOnClickListener(v -> {
                String ID = quiz.getQuizID() + ":" + currentUser.getUid();
                refDegrees.child(ID).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            // ID exists in the database
                            // You can perform your actions here
                            Toasty.info(context, context.getString(R.string.you_have_tried_the_test_before), Toast.LENGTH_SHORT).show();
                        } else {
                            // ID does not exist in the database
                            try {
                                if (checkDate(quiz.getQuizDate()) == 0 && checkTime(quiz.getQuizSTime(), quiz.getQuizETime())) {
                                    context.startActivity(new Intent(context, QuizActivity.class).putExtra("CurrentQuiz", quiz));
                                }
                            } catch (ParseException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Handle any errors
                        Toast.makeText(context, "Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            });

        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int getItemCount() {
        return mQuizzes.size();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public interface OnItemClickListener {
        void onItem_Click(int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ItemStudentQuizLayoutBinding binding;

        public ViewHolder(ItemStudentQuizLayoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.getRoot().setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mListener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    mListener.onItem_Click(position);
                }
            }
        }
    }

    public int checkDate(String quizDate) throws ParseException {
        SimpleDateFormat sdformat = new SimpleDateFormat("yyyy-M-dd", Locale.ENGLISH);
        Date d1 = sdformat.parse(getCurrentDate());
        Date d2 = sdformat.parse(quizDate);
        assert d1 != null;
        return d1.compareTo(d2);
    }

    @SuppressLint("SimpleDateFormat")
    String getCurrentDate() {
        // Get the current date
        Date currentDate = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-M-dd", Locale.ENGLISH);
        return dateFormat.format(currentDate);
    }

    @SuppressLint("SimpleDateFormat")
    public boolean checkTime(String startTime, String endTime) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.ENGLISH);
            Date timeToCheck = sdf.parse(getCurrentTime());
            Date startTimeDate = sdf.parse(startTime);
            Date endTimeDate = sdf.parse(endTime);
            assert timeToCheck != null;
            return timeToCheck.after(startTimeDate) && timeToCheck.before(endTimeDate);
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }

    @SuppressLint("SimpleDateFormat")
    String getCurrentTime() {
        // Get the current time
        Date currentTime = new Date();
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.ENGLISH); // AM/PM format
        return timeFormat.format(currentTime);
    }

}
