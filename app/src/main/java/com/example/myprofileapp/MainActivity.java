package com.example.myprofileapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import java.io.File;

public class MainActivity extends AppCompatActivity {
    // tạo các biến ảnh, textName, email, chuyên ngành
    ImageView imgAvatar;
    TextView tvName, tvEmail, tvMajor;
    Button btnEdit;
    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imgAvatar = findViewById(R.id.imgAvatar);
        tvName = findViewById(R.id.tvName);
        tvEmail = findViewById(R.id.tvEmail);
        tvMajor = findViewById(R.id.tvMajor);
        btnEdit = findViewById(R.id.btnEdit);

        prefs = getSharedPreferences("USER_DATA", MODE_PRIVATE);

        loadData();

        btnEdit.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, EditActivity.class));
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    private void loadData() {
        String name = prefs.getString("name", "Chưa có tên");
        String email = prefs.getString("email", "Chưa có email");
        String major = prefs.getString("major", "Chưa có chuyên ngành");
        String avatarPath = prefs.getString("avatar", null);

        tvName.setText(name);
        tvEmail.setText(email);
        tvMajor.setText(major);

        if (avatarPath != null) {
            File file = new File(avatarPath);
            if (file.exists()) imgAvatar.setImageBitmap(BitmapFactory.decodeFile(avatarPath));
        }
    }
}
