package com.example.myprofileapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.*;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class EditActivity extends AppCompatActivity {
    // tạo các biến
    ImageView imgEditAvatar;
    Button btnChooseImage, btnSave;
    EditText etName, etEmail, etMajor;
    SharedPreferences prefs;
    String savedImagePath = null;

    ActivityResultLauncher<Intent> galleryLauncher;
    // khởi tạo
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        imgEditAvatar = findViewById(R.id.imgEditAvatar);
        btnChooseImage = findViewById(R.id.btnChooseImage);
        btnSave = findViewById(R.id.btnSave);
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etMajor = findViewById(R.id.etMajor);

        prefs = getSharedPreferences("USER_DATA", MODE_PRIVATE);

        loadOldData();

        // Chọn ảnh từ gallery
        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri uri = result.getData().getData();
                        imgEditAvatar.setImageURI(uri);
                        savedImagePath = saveImageToInternal(uri);
                    }
                }
        );

        btnChooseImage.setOnClickListener(v -> {
            Intent pick = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            galleryLauncher.launch(pick);
        });


        btnSave.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String major = etMajor.getText().toString().trim();

            if (!isValidGmail(email)) {
                etEmail.setError("Email không hợp lệ, phải là Gmail!");
                etEmail.requestFocus();
                return;
            }

            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("name", name);
            editor.putString("email", email);
            editor.putString("major", major);
            if (savedImagePath != null) editor.putString("avatar", savedImagePath);
            editor.apply();

            finish();
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        });
    }

    private void loadOldData() {
        etName.setText(prefs.getString("name", ""));
        etEmail.setText(prefs.getString("email", ""));
        etMajor.setText(prefs.getString("major", ""));
        String avatarPath = prefs.getString("avatar", null);
        if (avatarPath != null) {
            File file = new File(avatarPath);
            if (file.exists()) {
                imgEditAvatar.setImageBitmap(BitmapFactory.decodeFile(avatarPath));
                savedImagePath = avatarPath;
            }
        }
    }

    private String saveImageToInternal(Uri uri) {
        try {
            InputStream is = getContentResolver().openInputStream(uri);
            File file = new File(getFilesDir(), "avatar.jpg");
            FileOutputStream fos = new FileOutputStream(file);
            byte[] buffer = new byte[1024];
            int len;
            while ((len = is.read(buffer)) > 0) fos.write(buffer, 0, len);
            fos.close();
            is.close();
            return file.getAbsolutePath();
        } catch (Exception e) {
            return null;
        }
    }

    private boolean isValidGmail(String email) {
        return !TextUtils.isEmpty(email) &&
                Patterns.EMAIL_ADDRESS.matcher(email).matches() &&
                email.endsWith("@gmail.com");
    }
}
