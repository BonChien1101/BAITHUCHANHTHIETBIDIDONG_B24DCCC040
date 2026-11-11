package com.example.bt;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bt.adapter.SubjectAdapter;
import com.example.bt.data.PrefsManager;
import com.example.bt.model.Subject;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

public class SubjectListActivity extends AppCompatActivity implements SubjectAdapter.OnSubjectClickListener {

    private ImageButton btnBack;
    private RecyclerView rvSubjects;
    private TextView tvEmptyList;
    private ExtendedFloatingActionButton btnAddSubject;

    private SubjectAdapter subjectAdapter;
    private PrefsManager prefsManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_subject_list);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.subject_list_root), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        prefsManager = PrefsManager.getInstance(this);

        initViews();
        setupRecyclerView();
        setupListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadSubjects();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        rvSubjects = findViewById(R.id.rvSubjects);
        tvEmptyList = findViewById(R.id.tvEmptyList);
        btnAddSubject = findViewById(R.id.btnAddSubject);
    }

    private void setupRecyclerView() {
        subjectAdapter = new SubjectAdapter(this);
        rvSubjects.setLayoutManager(new LinearLayoutManager(this));
        rvSubjects.setAdapter(subjectAdapter);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());
        btnAddSubject.setOnClickListener(v -> showAddSubjectDialog());
    }

    private void loadSubjects() {
        List<Subject> subjects = prefsManager.getSubjects();
        if (subjects == null || subjects.isEmpty()) {
            tvEmptyList.setVisibility(View.VISIBLE);
            rvSubjects.setVisibility(View.GONE);
        } else {
            tvEmptyList.setVisibility(View.GONE);
            rvSubjects.setVisibility(View.VISIBLE);
            subjectAdapter.setSubjects(subjects);
        }
    }

    // F2.4: Click on subject -> Open SubjectDetailActivity
    @Override
    public void onSubjectClick(Subject subject) {
        if (subject == null) return;
        Intent intent = new Intent(this, SubjectDetailActivity.class);
        intent.putExtra("subject_id", subject.getSubjectId());
        startActivity(intent);
    }

    // F2.5: Long click on subject -> Ask confirmation to delete
    @Override
    public void onSubjectLongClick(Subject subject) {
        if (subject == null) return;
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc chắn muốn xóa môn học \"" + subject.getSubjectName() + "\" không?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    boolean success = prefsManager.deleteSubject(subject.getSubjectId());
                    if (success) {
                        Toast.makeText(SubjectListActivity.this, "Đã xóa môn học", Toast.LENGTH_SHORT).show();
                        loadSubjects();
                    } else {
                        Toast.makeText(SubjectListActivity.this, "Xóa không thành công", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss())
                .show();
    }

    // F2.2 & F2.3: Dialog to Add Subject
    private void showAddSubjectDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_subject, null);

        TextInputEditText etSubjectId = view.findViewById(R.id.etSubjectId);
        TextInputEditText etSubjectName = view.findViewById(R.id.etSubjectName);
        TextInputEditText etCredits = view.findViewById(R.id.etCredits);
        TextInputEditText etLecturer = view.findViewById(R.id.etLecturer);

        builder.setView(view);
        builder.setPositiveButton("Thêm", null);
        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String subjectId = etSubjectId.getText() != null ? etSubjectId.getText().toString().trim() : "";
            String subjectName = etSubjectName.getText() != null ? etSubjectName.getText().toString().trim() : "";
            String creditsStr = etCredits.getText() != null ? etCredits.getText().toString().trim() : "";
            String lecturer = etLecturer.getText() != null ? etLecturer.getText().toString().trim() : "";

            if (subjectId.isEmpty()) {
                etSubjectId.setError("Vui lòng nhập mã môn học");
                return;
            }

            // Check if subject ID already exists (F2.3 requirement: không trùng)
            if (prefsManager.getSubjectById(subjectId) != null) {
                etSubjectId.setError("Mã môn học đã tồn tại!");
                return;
            }

            if (subjectName.isEmpty()) {
                etSubjectName.setError("Vui lòng nhập tên môn học");
                return;
            }

            if (creditsStr.isEmpty()) {
                etCredits.setError("Vui lòng nhập số tín chỉ");
                return;
            }

            int credits;
            try {
                credits = Integer.parseInt(creditsStr);
                if (credits < 1 || credits > 6) {
                    etCredits.setError("Số tín chỉ phải từ 1 đến 6");
                    return;
                }
            } catch (NumberFormatException e) {
                etCredits.setError("Số tín chỉ không hợp lệ");
                return;
            }

            Subject newSubject = new Subject(subjectId, subjectName, credits, lecturer);
            boolean added = prefsManager.addSubject(newSubject);
            if (added) {
                Toast.makeText(SubjectListActivity.this, "Thêm môn học thành công", Toast.LENGTH_SHORT).show();
                loadSubjects();
                dialog.dismiss();
            } else {
                etSubjectId.setError("Mã môn học đã tồn tại!");
            }
        });
    }
}
