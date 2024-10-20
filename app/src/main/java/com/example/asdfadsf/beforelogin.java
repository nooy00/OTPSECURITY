package com.example.asdfadsf;

import android.content.Intent;
import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.view.View;
import android.widget.Button;

public class beforelogin extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_beforelogin);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 회원가입 버튼 클릭 시 이벤트 처리
        Button signpageButton = findViewById(R.id.signpagebutton);
        signpageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // signpage 액티비티로 이동
                Intent intent = new Intent(beforelogin.this, signpage.class);
                startActivity(intent);
            }
        });

        // 로그인 버튼 클릭 시 이벤트 처리
        Button loginpageButton = findViewById(R.id.loginpagebutton);
        loginpageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // signpage 액티비티로 이동
                Intent intent = new Intent(beforelogin.this, loginpage.class);
                startActivity(intent);
            }
        });
    }
}