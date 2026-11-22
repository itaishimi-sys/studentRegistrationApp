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

public class EditStudentActivity extends AppCompatActivity {

    private EditText etFirstName, etLastName, etEmail;
    private TextView tvFirstNameError, tvLastNameError, tvEmailError, tvGradeError, tvMajorError, tvTermsError;
    private Spinner spGrade;
    private RadioGroup rgMajor;
    private CheckBox cbNotifications, cbTerms;
    private Button btnCancel, btnSaveAndReturn;

    private List<String> grades;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_student);

        bindViews();
        setupGradeSpinner();
        populateExistingData();
        setupListeners();
    }

    private void bindViews() {
        etFirstName = findViewById(R.id.etEditFirstName);
        etLastName = findViewById(R.id.etEditLastName);
        etEmail = findViewById(R.id.etEditEmail);

        tvFirstNameError = findViewById(R.id.tvEditFirstNameError);
        tvLastNameError = findViewById(R.id.tvEditLastNameError);
        tvEmailError = findViewById(R.id.tvEditEmailError);
        tvGradeError = findViewById(R.id.tvEditGradeError);
        tvMajorError = findViewById(R.id.tvEditMajorError);
        tvTermsError = findViewById(R.id.tvEditTermsError);

        spGrade = findViewById(R.id.spEditGrade);
        rgMajor = findViewById(R.id.rgEditMajor);
        cbNotifications = findViewById(R.id.cbEditNotifications);
        cbTerms = findViewById(R.id.cbEditTerms);

        btnCancel = findViewById(R.id.btnCancel);
        btnSaveAndReturn = findViewById(R.id.btnSaveAndReturn);
    }

    private void setupGradeSpinner() {
        grades = new ArrayList<>();
        grades.add("");
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

    private void populateExistingData() {
        Student existing = null;
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(StudentDetailsActivity.EXTRA_STUDENT_TO_EDIT)) {
            Object raw = intent.getSerializableExtra(StudentDetailsActivity.EXTRA_STUDENT_TO_EDIT);
            if (raw instanceof Student) {
                existing = (Student) raw;
            }
        }

        if (existing == null) {
            existing = StudentStorage.loadStudent(this);
        }

        if (existing == null) return;

        etFirstName.setText(existing.getFirstName());
        etLastName.setText(existing.getLastName());
        etEmail.setText(existing.getEmail());
        cbNotifications.setChecked(existing.isNotificationsEnabled());
        cbTerms.setChecked(existing.isTermsAccepted());

        if (existing.getGrade() != null) {
            int gradeIndex = grades.indexOf(existing.getGrade());
            if (gradeIndex >= 0) {
                spGrade.setSelection(gradeIndex);
            }
        }

        selectMajorRadioButton(existing.getMajor());
    }

    private void selectMajorRadioButton(String majorValue) {
        if (majorValue == null || majorValue.isEmpty()) {
            rgMajor.clearCheck();
            return;
        }

        int childCount = rgMajor.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = rgMajor.getChildAt(i);
            if (child instanceof RadioButton) {
                RadioButton rb = (RadioButton) child;
                if (majorValue.equals(rb.getText().toString())) {
                    rb.setChecked(true);
                    return;
                }
            }
        }
    }

    private void setupListeners() {
        btnCancel.setOnClickListener(v -> {
            setResult(RESULT_CANCELED);
            finish();
        });

        btnSaveAndReturn.setOnClickListener(v -> validateAndSave());

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

    private void validateAndSave() {
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

        Student updatedStudent = new Student(
                firstName,
                lastName,
                email,
                grade,
                major,
                notifications,
                termsAccepted
        );

        StudentStorage.saveStudent(this, updatedStudent);

        Intent resultIntent = new Intent();
        resultIntent.putExtra(StudentDetailsActivity.EXTRA_UPDATED_STUDENT, updatedStudent);
        setResult(RESULT_OK, resultIntent);
        finish();
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