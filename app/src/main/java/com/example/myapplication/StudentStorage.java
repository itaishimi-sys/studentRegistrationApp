package com.example.myapplication; // החלף לחבילה שלך

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Patterns;

public class StudentStorage {
    private static final String PREFS_NAME = "student_prefs";
    private static final String KEY_HAS_STUDENT = "has_student";
    private static final String KEY_FIRST_NAME = "first_name";
    private static final String KEY_LAST_NAME = "last_name";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_GRADE = "grade";
    private static final String KEY_MAJOR = "major";
    private static final String KEY_NOTIFICATIONS = "notifications";
    private static final String KEY_TERMS = "terms";

    public static void saveStudent(Context context, Student student) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(KEY_HAS_STUDENT, true);
        editor.putString(KEY_FIRST_NAME, student.getFirstName().trim());
        editor.putString(KEY_LAST_NAME, student.getLastName().trim());
        editor.putString(KEY_EMAIL, student.getEmail().trim());
        editor.putString(KEY_GRADE, student.getGrade().trim());
        editor.putString(KEY_MAJOR, student.getMajor().trim());
        editor.putBoolean(KEY_NOTIFICATIONS, student.isNotificationsEnabled());
        editor.putBoolean(KEY_TERMS, student.isTermsAccepted());
        editor.apply();
    }

    public static Student loadStudent(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        if (!prefs.getBoolean(KEY_HAS_STUDENT, false)) {
            return null;
        }
        String firstName = prefs.getString(KEY_FIRST_NAME, "");
        String lastName = prefs.getString(KEY_LAST_NAME, "");
        String email = prefs.getString(KEY_EMAIL, "");
        String grade = prefs.getString(KEY_GRADE, "");
        String major = prefs.getString(KEY_MAJOR, "");
        boolean notifications = prefs.getBoolean(KEY_NOTIFICATIONS, false);
        boolean terms = prefs.getBoolean(KEY_TERMS, false);

        return new Student(firstName, lastName, email, grade, major, notifications, terms);
    }

    public static boolean isValidRequiredText(String value) {
        return value != null && !value.trim().isEmpty();
    }

    public static boolean isValidEmail(String email) {
        if (email == null) return false;
        String trimmed = email.trim();
        return !TextUtils.isEmpty(trimmed) && Patterns.EMAIL_ADDRESS.matcher(trimmed).matches();
    }

    public static String formatStudentSummary(Context context, Student student) {
        String notificationsText = student.isNotificationsEnabled()
                ? context.getString(R.string.yes_approved)
                : context.getString(R.string.no_not_approved);

        String termsText = student.isTermsAccepted()
                ? context.getString(R.string.yes_approved)
                : context.getString(R.string.no_not_approved);

        return context.getString(
                R.string.template_student_summary,
                student.getFirstName(),
                student.getLastName(),
                student.getEmail(),
                student.getGrade(),
                student.getMajor(),
                notificationsText,
                termsText
        );
    }

    public static String formatEmailOrShareBody(Context context, Student student) {
        String summary = formatStudentSummary(context, student);
        return context.getString(R.string.template_email_or_share_body, summary);
    }
}