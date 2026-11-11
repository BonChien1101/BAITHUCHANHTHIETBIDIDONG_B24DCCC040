package com.example.bt;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.bt.data.PrefsManager;
import com.example.bt.model.Subject;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Locale;

public class SubjectDetailActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private TextInputEditText etSubjectId;
    private TextInputEditText etSubjectName;
    private TextInputEditText etCredits;
    private TextInputEditText etLecturer;

    private TextInputEditText etAttendanceScore;
    private TextInputEditText etMidtermScore;
    private TextInputEditText etFinalScore;

    private TextView tvCalculatedDtk;
    private TextView tvLetterGrade;
    private TextView tvPoint4;
    private TextView tvStatus;

    private Button btnSaveSubject;

    private PrefsManager prefsManager;
    private String currentSubjectId;
    private Subject currentSubject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_subject_detail);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.subject_detail_root), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        prefsManager = PrefsManager.getInstance(this);

        if (getIntent() != null) {
            currentSubjectId = getIntent().getStringExtra("subject_id");
        }

        if (currentSubjectId == null || currentSubjectId.isEmpty()) {
            Toast.makeText(this, "Không tìm thấy thông tin môn học", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        loadSubjectData();
        setupListeners();
        calculateAndDisplayResults();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        etSubjectId = findViewById(R.id.etSubjectId);
        etSubjectName = findViewById(R.id.etSubjectName);
        etCredits = findViewById(R.id.etCredits);
        etLecturer = findViewById(R.id.etLecturer);

        etAttendanceScore = findViewById(R.id.etAttendanceScore);
        etMidtermScore = findViewById(R.id.etMidtermScore);
        etFinalScore = findViewById(R.id.etFinalScore);

        tvCalculatedDtk = findViewById(R.id.tvCalculatedDtk);
        tvLetterGrade = findViewById(R.id.tvLetterGrade);
        tvPoint4 = findViewById(R.id.tvPoint4);
        tvStatus = findViewById(R.id.tvStatus);

        btnSaveSubject = findViewById(R.id.btnSaveSubject);
    }

    private void loadSubjectData() {
        currentSubject = prefsManager.getSubjectById(currentSubjectId);
        if (currentSubject == null) {
            Toast.makeText(this, "Không tìm thấy thông tin môn học", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        etSubjectId.setText(currentSubject.getSubjectId());
        etSubjectName.setText(currentSubject.getSubjectName());
        etCredits.setText(String.valueOf(currentSubject.getCredits()));
        etLecturer.setText(currentSubject.getLecturer() != null ? currentSubject.getLecturer() : "");

        if (currentSubject.getAttendanceScore() >= 0) {
            etAttendanceScore.setText(formatScore(currentSubject.getAttendanceScore()));
        }
        if (currentSubject.getMidtermScore() >= 0) {
            etMidtermScore.setText(formatScore(currentSubject.getMidtermScore()));
        }
        if (currentSubject.getFinalScore() >= 0) {
            etFinalScore.setText(formatScore(currentSubject.getFinalScore()));
        }
    }

    private String formatScore(double score) {
        if (score == (long) score) {
            return String.format(Locale.US, "%d", (long) score);
        } else {
            return String.format(Locale.US, "%.1f", score);
        }
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());

        TextWatcher scoreTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                calculateAndDisplayResults();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        };

        etAttendanceScore.addTextChangedListener(scoreTextWatcher);
        etMidtermScore.addTextChangedListener(scoreTextWatcher);
        etFinalScore.addTextChangedListener(scoreTextWatcher);

        btnSaveSubject.setOnClickListener(v -> saveSubjectData());
    }

    private void calculateAndDisplayResults() {
        Double attendance = parseScoreInput(etAttendanceScore);
        Double midterm = parseScoreInput(etMidtermScore);
        Double finalScore = parseScoreInput(etFinalScore);

        if (attendance != null && midterm != null && finalScore != null) {
            double dtk = 0.1 * attendance + 0.3 * midterm + 0.6 * finalScore;
            String letter = getLetterGradeForScore(dtk);
            double point4 = getGpaPoint4ForLetter(letter);
            boolean isPassed = dtk >= 4.0;

            tvCalculatedDtk.setText(String.format(Locale.US, "%.2f / 10", dtk));
            tvLetterGrade.setText(letter);
            tvPoint4.setText(String.format(Locale.US, "%.1f", point4));

            if (isPassed) {
                tvStatus.setText("ĐẠT");
                tvStatus.setTextColor(Color.parseColor("#2E7D32")); // Green
            } else {
                tvStatus.setText("KHÔNG ĐẠT (F)");
                tvStatus.setTextColor(Color.parseColor("#C62828")); // Red
            }
        } else {
            tvCalculatedDtk.setText("-- / 10");
            tvLetterGrade.setText("--");
            tvPoint4.setText("--");
            tvStatus.setText("Chưa nhập đủ 3 đầu điểm");
            tvStatus.setTextColor(Color.parseColor("#757575")); // Grey
        }
    }

    private Double parseScoreInput(TextInputEditText et) {
        if (et == null || et.getText() == null) return null;
        String text = et.getText().toString().trim();
        if (text.isEmpty()) return null;
        try {
            double val = Double.parseDouble(text);
            if (val < 0.0 || val > 10.0) {
                return null;
            }
            return val;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String getLetterGradeForScore(double dtk) {
        if (dtk >= 8.5) return "A";
        if (dtk >= 7.0) return "B";
        if (dtk >= 5.5) return "C";
        if (dtk >= 4.0) return "D";
        return "F";
    }

    private double getGpaPoint4ForLetter(String letter) {
        switch (letter) {
            case "A": return 4.0;
            case "B": return 3.0;
            case "C": return 2.0;
            case "D": return 1.0;
            case "F":
            default:  return 0.0;
        }
    }

    private void saveSubjectData() {
        String subjectName = etSubjectName.getText() != null ? etSubjectName.getText().toString().trim() : "";
        String creditsStr = etCredits.getText() != null ? etCredits.getText().toString().trim() : "";
        String lecturer = etLecturer.getText() != null ? etLecturer.getText().toString().trim() : "";

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

        // Validate Scores
        double attendanceScore = validateAndGetScore(etAttendanceScore, "Chuyên cần");
        if (attendanceScore == -2) return; // Validation failed

        double midtermScore = validateAndGetScore(etMidtermScore, "Giữa kỳ");
        if (midtermScore == -2) return; // Validation failed

        double finalScore = validateAndGetScore(etFinalScore, "Cuối kỳ");
        if (finalScore == -2) return; // Validation failed

        // Update current subject fields
        currentSubject.setSubjectName(subjectName);
        currentSubject.setCredits(credits);
        currentSubject.setLecturer(lecturer);
        currentSubject.setAttendanceScore(attendanceScore);
        currentSubject.setMidtermScore(midtermScore);
        currentSubject.setFinalScore(finalScore);

        boolean updated = prefsManager.updateSubject(currentSubject);
        if (updated) {
            Toast.makeText(this, "Đã lưu", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Lưu thất bại", Toast.LENGTH_SHORT).show();
        }
    }

    private double validateAndGetScore(TextInputEditText et, String scoreName) {
        if (et == null || et.getText() == null) return -1;
        String text = et.getText().toString().trim();
        if (text.isEmpty()) return -1; // Not entered yet
        try {
            double val = Double.parseDouble(text);
            if (val < 0.0 || val > 10.0) {
                et.setError("Điểm " + scoreName + " phải từ 0 đến 10");
                return -2; // Out of range error flag
            }
            return val;
        } catch (NumberFormatException e) {
            et.setError("Điểm " + scoreName + " không hợp lệ");
            return -2; // Parse error flag
        }
    }
}
