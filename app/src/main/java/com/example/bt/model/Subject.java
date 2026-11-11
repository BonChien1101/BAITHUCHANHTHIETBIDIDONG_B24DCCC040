package com.example.bt.model;

import java.io.Serializable;

public class Subject implements Serializable {
    private String subjectId;
    private String subjectName;
    private int credits;
    private String lecturer;
    private double attendanceScore; // Chuyên cần (10%), default -1 if not entered
    private double midtermScore;    // Giữa kỳ (30%), default -1 if not entered
    private double finalScore;      // Cuối kỳ (60%), default -1 if not entered

    public Subject() {
        this.attendanceScore = -1;
        this.midtermScore = -1;
        this.finalScore = -1;
    }

    public Subject(String subjectId, String subjectName, int credits, String lecturer) {
        this.subjectId = subjectId;
        this.subjectName = subjectName;
        this.credits = credits;
        this.lecturer = lecturer;
        this.attendanceScore = -1;
        this.midtermScore = -1;
        this.finalScore = -1;
    }

    public Subject(String subjectId, String subjectName, int credits, String lecturer,
                   double attendanceScore, double midtermScore, double finalScore) {
        this.subjectId = subjectId;
        this.subjectName = subjectName;
        this.credits = credits;
        this.lecturer = lecturer;
        this.attendanceScore = attendanceScore;
        this.midtermScore = midtermScore;
        this.finalScore = finalScore;
    }

    public String getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(String subjectId) {
        this.subjectId = subjectId;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public int getCredits() {
        return credits;
    }

    public void setCredits(int credits) {
        this.credits = credits;
    }

    public String getLecturer() {
        return lecturer;
    }

    public void setLecturer(String lecturer) {
        this.lecturer = lecturer;
    }

    public double getAttendanceScore() {
        return attendanceScore;
    }

    public void setAttendanceScore(double attendanceScore) {
        this.attendanceScore = attendanceScore;
    }

    public double getMidtermScore() {
        return midtermScore;
    }

    public void setMidtermScore(double midtermScore) {
        this.midtermScore = midtermScore;
    }

    public double getFinalScore() {
        return finalScore;
    }

    public void setFinalScore(double finalScore) {
        this.finalScore = finalScore;
    }

    public boolean hasEnteredAllScores() {
        return attendanceScore >= 0 && midtermScore >= 0 && finalScore >= 0;
    }

    public double calculateFinalScore() {
        if (!hasEnteredAllScores()) {
            return 0.0;
        }
        return 0.1 * attendanceScore + 0.3 * midtermScore + 0.6 * finalScore;
    }

    public String getLetterGrade() {
        if (!hasEnteredAllScores()) {
            return "N/A";
        }
        double dtk = calculateFinalScore();
        // Rounding to 1 decimal place or comparing with small epsilon if needed,
        // but standard range checks:
        if (dtk >= 8.5) return "A";
        if (dtk >= 7.0) return "B";
        if (dtk >= 5.5) return "C";
        if (dtk >= 4.0) return "D";
        return "F";
    }

    public double getGpaPoint4() {
        if (!hasEnteredAllScores()) {
            return 0.0;
        }
        String letter = getLetterGrade();
        switch (letter) {
            case "A": return 4.0;
            case "B": return 3.0;
            case "C": return 2.0;
            case "D": return 1.0;
            case "F":
            default:  return 0.0;
        }
    }

    public boolean isPassed() {
        if (!hasEnteredAllScores()) {
            return false;
        }
        return calculateFinalScore() >= 4.0;
    }
}
