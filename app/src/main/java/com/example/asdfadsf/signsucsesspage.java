package com.example.asdfadsf;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class signsucsesspage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signsucsesspage);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        // 환영 메시지 텍스트뷰 초기화
        TextView welcomeMessageTextView = findViewById(R.id.welcomeMessageTextView);

        // 회원 가입 성공 시 전달된 이름 가져오기
        Intent intent = getIntent();
        String userName = intent.getStringExtra("userName");



        // 환영 메시지 설정
        welcomeMessageTextView.setText("환영합니다." + "\n"+userName+" 님!");

        Button goBackButton = findViewById(R.id.gobackbutton);
        goBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 이전 페이지(로그인 하기 전 페이지)로 이동
                Intent intent = new Intent(signsucsesspage.this, beforelogin.class);
                startActivity(intent);
            }
        });

        Button loginbutton = findViewById(R.id.loginbutton);
        loginbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 이전 페이지(로그인 하기 전 페이지)로 이동
                Intent intent = new Intent(signsucsesspage.this, loginpage.class);
                startActivity(intent);
            }
        });
    }
}