package com.example.quizapp.Activity.UI.Doctor.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quizapp.Model.ModelQuizDegree;
import com.example.quizapp.databinding.ItemStudentDegreeLayoutBinding;

import java.util.List;

public class AdapterStudentDegree extends RecyclerView.Adapter<AdapterStudentDegree.ViewHolder> {
    Context context;
    List<ModelQuizDegree> mQuizzesDegrees;
    private OnItemClickListener mListener;

    public AdapterStudentDegree(Context context, List<ModelQuizDegree> quizs, OnItemClickListener mListener) {
        this.context = context;
        this.mQuizzesDegrees = quizs;
        this.mListener = mListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        ItemStudentDegreeLayoutBinding binding = ItemStudentDegreeLayoutBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    @SuppressLint({"SimpleDateFormat", "ResourceAsColor", "SetTextI18n"})
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ModelQuizDegree quizDegree = mQuizzesDegrees.get(position);

        holder.binding.studentName.setText(quizDegree.getStudentName());
        holder.binding.academicYear.setText(quizDegree.getQuizAcademicYear());
        holder.binding.studentDegree.setText(quizDegree.getStudentDegree());
        holder.binding.quizDegree.setText(quizDegree.getQuizDegree());
    }

    @Override
    public int getItemCount() {
        return mQuizzesDegrees.size();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public interface OnItemClickListener {
        void onItem_Click(int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ItemStudentDegreeLayoutBinding binding;

        public ViewHolder(ItemStudentDegreeLayoutBinding binding) {
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

}
