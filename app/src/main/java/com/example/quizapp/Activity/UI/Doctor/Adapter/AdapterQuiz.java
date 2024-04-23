package com.example.quizapp.Activity.UI.Doctor.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quizapp.Activity.UI.Doctor.Action.EditQuizActivity;
import com.example.quizapp.Model.ModelQuiz;
import com.example.quizapp.R;
import com.example.quizapp.databinding.DeleteMessageLayoutBinding;
import com.example.quizapp.databinding.ItemQuizLayoutBinding;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;
import java.util.Objects;

import es.dmoral.toasty.Toasty;

public class AdapterQuiz extends RecyclerView.Adapter<AdapterQuiz.ViewHolder> {

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference refQuizzes = database.getReference("Quizzes");

    Context context;
    List<ModelQuiz> mQuizzes;
    private OnItemClickListener mListener;

    public AdapterQuiz(Context context, List<ModelQuiz> notes, OnItemClickListener mListener) {
        this.context = context;
        this.mQuizzes = notes;
        this.mListener = mListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        ItemQuizLayoutBinding binding = ItemQuizLayoutBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    @SuppressLint("SimpleDateFormat")
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ModelQuiz quiz = mQuizzes.get(position);

        holder.binding.quizTitle.setText(quiz.getQuizTitle());
        holder.binding.quizSubject.setText("Subject : " + quiz.getQuizSubject());
        holder.binding.academicYear.setText(quiz.getQuizAcademicYear());
        holder.binding.quizDate.setText(quiz.getQuizDate());
        holder.binding.quizStartTime.setText(quiz.getQuizSTime());
        holder.binding.quizEndTime.setText(quiz.getQuizETime());


        holder.binding.editQuiz.setOnClickListener(v -> {
            context.startActivity(new Intent(context, EditQuizActivity.class).putExtra("CurrentQuiz", quiz));
        });
        holder.binding.deleteQuiz.setOnClickListener(v -> {
            showDialogDeleteMsgLayout(context.getString(R.string.are_your_sure_delete), quiz);
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
        ItemQuizLayoutBinding binding;

        public ViewHolder(ItemQuizLayoutBinding binding) {
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void showDialogDeleteMsgLayout(String msg, ModelQuiz quiz) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.delete_message_layout, null);
        dialogBuilder.setView(dialogView);
        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
        Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(0));

        // Initialize ViewBinding for the layout
        DeleteMessageLayoutBinding deleteMessageBinding = DeleteMessageLayoutBinding.bind(dialogView);
        deleteMessageBinding.inputMessage.setText(msg);
        deleteMessageBinding.buttonYes.setOnClickListener(v -> {
            refQuizzes.child(quiz.getQuizID()).removeValue().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toasty.success(context, context.getString(R.string.delete), Toast.LENGTH_SHORT).show();
                    alertDialog.dismiss();
                }
            });
        });

        deleteMessageBinding.buttonNo.setOnClickListener(v -> {
            alertDialog.dismiss();
        });
    }
}
