package com.example.bt.data;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.bt.model.Student;
import com.example.bt.model.Subject;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class PrefsManager {
    private static final String PREF_NAME = "student_app_prefs";
    private static final String KEY_STUDENT_INFO = "student_info";
    private static final String KEY_SUBJECT_LIST = "subject_list";
    private static final String KEY_IS_FIRST_LAUNCH = "is_first_launch";

    private static PrefsManager instance;
    private final SharedPreferences prefs;
    private final Gson gson;

    private PrefsManager(Context context) {
        prefs = context.getApplicationContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
    }

    public static synchronized PrefsManager getInstance(Context context) {
        if (instance == null) {
            instance = new PrefsManager(context);
        }
        return instance;
    }

    public boolean isFirstLaunch() {
        return prefs.getBoolean(KEY_IS_FIRST_LAUNCH, true);
    }

    public void setFirstLaunch(boolean isFirstLaunch) {
        prefs.edit().putBoolean(KEY_IS_FIRST_LAUNCH, isFirstLaunch).apply();
    }

    public void saveStudent(Student student) {
        String json = gson.toJson(student);
        prefs.edit().putString(KEY_STUDENT_INFO, json).apply();
        setFirstLaunch(false);
    }

    public Student getStudent() {
        String json = prefs.getString(KEY_STUDENT_INFO, null);
        if (json == null || json.isEmpty()) {
            return null;
        }
        return gson.fromJson(json, Student.class);
    }

    public List<Subject> getSubjects() {
        String json = prefs.getString(KEY_SUBJECT_LIST, "[]");
        Type type = new TypeToken<ArrayList<Subject>>() {}.getType();
        List<Subject> subjects = gson.fromJson(json, type);
        if (subjects == null) {
            subjects = new ArrayList<>();
        }
        return subjects;
    }

    public void saveSubjects(List<Subject> subjects) {
        String json = gson.toJson(subjects);
        prefs.edit().putString(KEY_SUBJECT_LIST, json).apply();
    }

    public Subject getSubjectById(String subjectId) {
        if (subjectId == null) return null;
        List<Subject> subjects = getSubjects();
        for (Subject subject : subjects) {
            if (subjectId.equalsIgnoreCase(subject.getSubjectId())) {
                return subject;
            }
        }
        return null;
    }

    public boolean addSubject(Subject newSubject) {
        if (newSubject == null || newSubject.getSubjectId() == null) return false;
        List<Subject> subjects = getSubjects();
        for (Subject s : subjects) {
            if (s.getSubjectId().equalsIgnoreCase(newSubject.getSubjectId())) {
                return false; // Duplicate subject ID
            }
        }
        subjects.add(newSubject);
        saveSubjects(subjects);
        return true;
    }

    public boolean updateSubject(Subject updatedSubject) {
        if (updatedSubject == null || updatedSubject.getSubjectId() == null) return false;
        List<Subject> subjects = getSubjects();
        boolean found = false;
        for (int i = 0; i < subjects.size(); i++) {
            if (subjects.get(i).getSubjectId().equalsIgnoreCase(updatedSubject.getSubjectId())) {
                subjects.set(i, updatedSubject);
                found = true;
                break;
            }
        }
        if (found) {
            saveSubjects(subjects);
        }
        return found;
    }

    public boolean deleteSubject(String subjectId) {
        if (subjectId == null) return false;
        List<Subject> subjects = getSubjects();
        boolean removed = false;
        for (int i = 0; i < subjects.size(); i++) {
            if (subjects.get(i).getSubjectId().equalsIgnoreCase(subjectId)) {
                subjects.remove(i);
                removed = true;
                break;
            }
        }
        if (removed) {
            saveSubjects(subjects);
        }
        return removed;
    }
}
