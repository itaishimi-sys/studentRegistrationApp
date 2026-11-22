package com.example.myapplication;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

public class StudentDetailsActivity extends AppCompatActivity {

    public static final String EXTRA_STUDENT_TO_EDIT = "extra_student_to_edit";
    public static final String EXTRA_UPDATED_STUDENT = "extra_updated_student";

    private TextView tvStudentDetailsContent;
    private Button btnEditDetails, btnSendEmail, btnShareDetails;

    private Student currentStudent;
    private ActivityResultLauncher<Intent> editStudentLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_details);

        bindViews();
        initActivityResultLauncher();
        loadInitialStudent();
        renderStudentDetails();
        setupButtons();
    }

    private void bindViews() {
        tvStudentDetailsContent = findViewById(R.id.tvStudentDetailsContent);
        btnEditDetails = findViewById(R.id.btnEditDetails);
        btnSendEmail = findViewById(R.id.btnSendEmail);
        btnShareDetails = findViewById(R.id.btnShareDetails);
    }

    private void initActivityResultLauncher() {
        editStudentLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Object rawStudent = result.getData().getSerializableExtra(EXTRA_UPDATED_STUDENT);
                        if (rawStudent instanceof Student) {
                            currentStudent = (Student) rawStudent;
                            renderStudentDetails();
                            Toast.makeText(this, getString(R.string.msg_saved_success), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
    }

    private void loadInitialStudent() {
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(MainActivity.EXTRA_STUDENT)) {
            Object raw = intent.getSerializableExtra(MainActivity.EXTRA_STUDENT);
            if (raw instanceof Student) {
                currentStudent = (Student) raw;
            }
        }

        if (currentStudent == null) {
            currentStudent = StudentStorage.loadStudent(this);
        }

        if (currentStudent == null) {
            currentStudent = new Student("", "", "", "", "", false, false);
        }
    }

    private void renderStudentDetails() {
        if (currentStudent == null) return;
        String summary = StudentStorage.formatStudentSummary(this, currentStudent);
        tvStudentDetailsContent.setText(summary);
    }

    private void setupButtons() {
        btnEditDetails.setOnClickListener(v -> {
            Intent intent = new Intent(StudentDetailsActivity.this, EditStudentActivity.class);
            intent.putExtra(EXTRA_STUDENT_TO_EDIT, currentStudent);
            editStudentLauncher.launch(intent);
        });

        btnSendEmail.setOnClickListener(v -> sendStudentEmail());
        btnShareDetails.setOnClickListener(v -> shareStudentDetails());
    }

    private void sendStudentEmail() {
        if (currentStudent == null) return;

        String recipientEmail = currentStudent.getEmail() != null ? currentStudent.getEmail().trim() : "";
        String subject = getString(R.string.email_subject);
        String body = StudentStorage.formatEmailOrShareBody(this, currentStudent);

        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        emailIntent.setData(Uri.parse("mailto:" + Uri.encode(recipientEmail)));
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{recipientEmail});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        emailIntent.putExtra(Intent.EXTRA_TEXT, body);

        try {
            Intent chooser = Intent.createChooser(emailIntent, getString(R.string.email_chooser_title));
            startActivity(chooser);
        } catch (ActivityNotFoundException | SecurityException e) {
            Toast.makeText(this, getString(R.string.error_no_email_app), Toast.LENGTH_SHORT).show();
        }
    }

    private void shareStudentDetails() {
        if (currentStudent == null) return;

        String shareBody = StudentStorage.formatEmailOrShareBody(this, currentStudent);

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.email_subject));
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareBody);

        try {
            Intent chooser = Intent.createChooser(shareIntent, getString(R.string.share_chooser_title));
            startActivity(chooser);
        } catch (ActivityNotFoundException | SecurityException e) {
            Toast.makeText(this, getString(R.string.error_no_share_app), Toast.LENGTH_SHORT).show();
        }
    }
}