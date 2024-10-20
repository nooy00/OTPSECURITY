package com.example.asdfadsf;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mAuth = FirebaseAuth.getInstance();
        preferences = getSharedPreferences("rememberMe", MODE_PRIVATE);

        // 로그인 유지 여부 확인
        boolean rememberMe = preferences.getBoolean("rememberMe", false);

        // 이미 로그인한 사용자가 있는지 확인
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null && rememberMe) {
            // 이미 로그인한 사용자가 있으면 mainpage로 이동
            Intent intent = new Intent(MainActivity.this, mainpage.class);
            startActivity(intent);
            finish(); // 현재 액티비티를 종료
        } else {
            // 로그인한 사용자가 없으면 일정 시간 후에 beforelogin 화면으로 이동
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(MainActivity.this, beforelogin.class);
                    startActivity(intent);
                    finish(); // 현재 액티비티를 종료
                }
            }, 2000); // 2초(2000밀리초) 후에 실행
        }
    }
}
