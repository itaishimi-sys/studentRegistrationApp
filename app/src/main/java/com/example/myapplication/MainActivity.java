package com.example.myapplication;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_STUDENT = "extra_student";

    private EditText etFirstName, etLastName, etEmail;
    private TextView tvFirstNameError, tvLastNameError, tvEmailError, tvGradeError, tvMajorError, tvTermsError;
    private Spinner spGrade;
    private RadioGroup rgMajor;
    private CheckBox cbNotifications, cbTerms;
    private Button btnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bindViews();
        setupGradeSpinner();
        setupListeners();
    }

    private void bindViews() {
        etFirstName = findViewById(R.id.etFirstName);
        etLastName = findViewById(R.id.etLastName);
        etEmail = findViewById(R.id.etEmail);

        tvFirstNameError = findViewById(R.id.tvFirstNameError);
        tvLastNameError = findViewById(R.id.tvLastNameError);
        tvEmailError = findViewById(R.id.tvEmailError);
        tvGradeError = findViewById(R.id.tvGradeError);
        tvMajorError = findViewById(R.id.tvMajorError);
        tvTermsError = findViewById(R.id.tvTermsError);

        spGrade = findViewById(R.id.spGrade);
        rgMajor = findViewById(R.id.rgMajor);
        cbNotifications = findViewById(R.id.cbNotifications);
        cbTerms = findViewById(R.id.cbTerms);
        btnSubmit = findViewById(R.id.btnSubmit);
    }

    private void setupGradeSpinner() {
        List<String> grades = new ArrayList<>();
        grades.add(""); // ברירת מחדל ריקה שמחייבת בחירה מודעת
        grades.add(getString(R.string.grade_y));
        grades.add(getString(R.string.grade_ya));
        grades.add(getString(R.string.grade_yb));

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                grades
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spGrade.setAdapter(adapter);
    }

    private void setupListeners() {
        btnSubmit.setOnClickListener(v -> validateAndProceed());

        // ניקוי הודעות שגיאה ברגע שהמשתמש מתקן
        etFirstName.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus && StudentStorage.isValidRequiredText(etFirstName.getText().toString())) {
                hideError(tvFirstNameError);
            }
        });

        etLastName.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus && StudentStorage.isValidRequiredText(etLastName.getText().toString())) {
                hideError(tvLastNameError);
            }
        });

        etEmail.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus && StudentStorage.isValidEmail(etEmail.getText().toString())) {
                hideError(tvEmailError);
            }
        });

        rgMajor.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId != -1) {
                hideError(tvMajorError);
            }
        });

        cbTerms.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                hideError(tvTermsError);
            }
        });
    }

    private void validateAndProceed() {
        boolean isValid = true;

        String firstName = etFirstName.getText().toString().trim();
        String lastName = etLastName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String grade = spGrade.getSelectedItem() != null ? spGrade.getSelectedItem().toString().trim() : "";
        int selectedMajorId = rgMajor.getCheckedRadioButtonId();
        boolean notifications = cbNotifications.isChecked();
        boolean termsAccepted = cbTerms.isChecked();

        if (!StudentStorage.isValidRequiredText(firstName)) {
            showError(tvFirstNameError, getString(R.string.error_first_name_required));
            isValid = false;
        } else {
            hideError(tvFirstNameError);
        }

        if (!StudentStorage.isValidRequiredText(lastName)) {
            showError(tvLastNameError, getString(R.string.error_last_name_required));
            isValid = false;
        } else {
            hideError(tvLastNameError);
        }

        if (!StudentStorage.isValidRequiredText(email)) {
            showError(tvEmailError, getString(R.string.error_email_required));
            isValid = false;
        } else if (!StudentStorage.isValidEmail(email)) {
            showError(tvEmailError, getString(R.string.error_email_invalid));
            isValid = false;
        } else {
            hideError(tvEmailError);
        }

        if (grade.isEmpty()) {
            showError(tvGradeError, getString(R.string.error_grade_required));
            isValid = false;
        } else {
            hideError(tvGradeError);
        }

        String major = "";
        if (selectedMajorId == -1) {
            showError(tvMajorError, getString(R.string.error_major_required));
            isValid = false;
        } else {
            RadioButton selectedMajorRb = findViewById(selectedMajorId);
            major = selectedMajorRb != null ? selectedMajorRb.getText().toString().trim() : "";
            hideError(tvMajorError);
        }

        if (!termsAccepted) {
            showError(tvTermsError, getString(R.string.error_terms_required));
            isValid = false;
        } else {
            hideError(tvTermsError);
        }

        if (!isValid) {
            return;
        }

        Student student = new Student(
                firstName,
                lastName,
                email,
                grade,
                major,
                notifications,
                termsAccepted
        );

        StudentStorage.saveStudent(this, student);

        Intent intent = new Intent(MainActivity.this, StudentDetailsActivity.class);
        intent.putExtra(EXTRA_STUDENT, student);
        startActivity(intent);
    }

    private void showError(TextView errorView, String message) {
        errorView.setText(message);
        errorView.setVisibility(View.VISIBLE);
    }

    private void hideError(TextView errorView) {
        errorView.setText("");
        errorView.setVisibility(View.GONE);
    }
}