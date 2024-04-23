package com.example.quizapp.Activity.UI.Doctor.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quizapp.Model.ModelQuiz;
import com.example.quizapp.Model.ModelQuizDegree;
import com.example.quizapp.R;
import com.example.quizapp.databinding.ItemNumStudentQuizLayoutBinding;
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

public class AdapterNumStudentQuiz extends RecyclerView.Adapter<AdapterNumStudentQuiz.ViewHolder> {
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference refDegrees = firebaseDatabase.getReference("Degrees");
    int numStudent = 0;
    Context context;
    List<ModelQuiz> mQuizzes;
    private OnItemClickListener mListener;

    public AdapterNumStudentQuiz(Context context, List<ModelQuiz> quizs, OnItemClickListener mListener) {
        this.context = context;
        this.mQuizzes = quizs;
        this.mListener = mListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        ItemNumStudentQuizLayoutBinding binding = ItemNumStudentQuizLayoutBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    @SuppressLint({"SimpleDateFormat", "ResourceAsColor", "SetTextI18n"})
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        ModelQuiz quiz = mQuizzes.get(position);

        holder.binding.quizTitle.setText(quiz.getQuizTitle());
        holder.binding.quizSubject.setText("Subject : " + quiz.getQuizSubject());
        holder.binding.academicYear.setText(quiz.getQuizAcademicYear());
        holder.binding.quizDate.setText(quiz.getQuizDate());

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
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }


        refDegrees.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                numStudent = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ModelQuizDegree quizDegree = snapshot.getValue(ModelQuizDegree.class);
                    if (quizDegree != null) {
                        if (quizDegree.getQuizID().equals(quiz.getQuizID())) {
                            numStudent++;
                        }
                    }
                }
                holder.binding.numStudent.setText(String.valueOf(numStudent));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
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
        ItemNumStudentQuizLayoutBinding binding;

        public ViewHolder(ItemNumStudentQuizLayoutBinding binding) {
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

}
