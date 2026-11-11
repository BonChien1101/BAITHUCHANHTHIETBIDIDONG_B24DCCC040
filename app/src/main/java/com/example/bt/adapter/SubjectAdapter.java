package com.example.bt.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bt.R;
import com.example.bt.model.Subject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SubjectAdapter extends RecyclerView.Adapter<SubjectAdapter.SubjectViewHolder> {

    public interface OnSubjectClickListener {
        void onSubjectClick(Subject subject);
        void onSubjectLongClick(Subject subject);
    }

    private List<Subject> subjects = new ArrayList<>();
    private final OnSubjectClickListener listener;

    public SubjectAdapter(OnSubjectClickListener listener) {
        this.listener = listener;
    }

    public void setSubjects(List<Subject> subjects) {
        this.subjects = subjects != null ? subjects : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SubjectViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_subject, parent, false);
        return new SubjectViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SubjectViewHolder holder, int position) {
        Subject subject = subjects.get(position);
        holder.bind(subject, listener);
    }

    @Override
    public int getItemCount() {
        return subjects.size();
    }

    static class SubjectViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvSubjectId;
        private final TextView tvSubjectName;
        private final TextView tvCredits;
        private final TextView tvLecturer;
        private final TextView tvGradeBadge;

        public SubjectViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSubjectId = itemView.findViewById(R.id.tvSubjectId);
            tvSubjectName = itemView.findViewById(R.id.tvSubjectName);
            tvCredits = itemView.findViewById(R.id.tvCredits);
            tvLecturer = itemView.findViewById(R.id.tvLecturer);
            tvGradeBadge = itemView.findViewById(R.id.tvGradeBadge);
        }

        public void bind(Subject subject, OnSubjectClickListener listener) {
            tvSubjectId.setText(subject.getSubjectId());
            tvSubjectName.setText(subject.getSubjectName());
            tvCredits.setText("Số tín chỉ: " + subject.getCredits());

            String lecturerStr = subject.getLecturer();
            if (lecturerStr != null && !lecturerStr.trim().isEmpty()) {
                tvLecturer.setText("GV: " + lecturerStr);
            } else {
                tvLecturer.setText("GV: Chưa rõ");
            }

            if (subject.hasEnteredAllScores()) {
                double dtk = subject.calculateFinalScore();
                String letter = subject.getLetterGrade();
                tvGradeBadge.setText(String.format(Locale.US, "ĐTK: %.1f (%s)", dtk, letter));
                if (subject.isPassed()) {
                    tvGradeBadge.setTextColor(Color.parseColor("#2E7D32")); // Green
                } else {
                    tvGradeBadge.setTextColor(Color.parseColor("#C62828")); // Red (F)
                }
            } else {
                tvGradeBadge.setText("Chưa có điểm");
                tvGradeBadge.setTextColor(Color.parseColor("#757575")); // Grey
            }

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onSubjectClick(subject);
                }
            });

            itemView.setOnLongClickListener(v -> {
                if (listener != null) {
                    listener.onSubjectLongClick(subject);
                }
                return true;
            });
        }
    }
}
