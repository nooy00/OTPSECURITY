package com.example.asdfadsf;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class loginpage extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private CheckBox rememberMeCheckBox;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loginpage);

        mAuth = FirebaseAuth.getInstance();
        preferences = getSharedPreferences("rememberMe", MODE_PRIVATE);

        EditText IDeditText = findViewById(R.id.IDeditText);
        EditText PWDeditText = findViewById(R.id.PWDeditText);
        rememberMeCheckBox = findViewById(R.id.rememberme);

        // SharedPreferences에서 저장된 로그인 정보를 불러오기
        String savedEmail = preferences.getString("email", "");
        String savedPassword = preferences.getString("password", "");
        boolean rememberMe = preferences.getBoolean("rememberMe", false);

        if (!TextUtils.isEmpty(savedEmail) && !TextUtils.isEmpty(savedPassword)) {
            IDeditText.setText(savedEmail);
            PWDeditText.setText(savedPassword);
            rememberMeCheckBox.setChecked(rememberMe);
        }

        // gobackbutton 클릭 시 이벤트 처리
        Button goBackButton = findViewById(R.id.gobackbutton);
        goBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 이전 페이지(로그인 하기 전 페이지)로 이동
                Intent intent = new Intent(loginpage.this, beforelogin.class);
                startActivity(intent);
            }
        });

        IDeditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IDeditText.setFocusableInTouchMode(true);
                IDeditText.requestFocus();
            }
        });

        PWDeditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PWDeditText.setFocusableInTouchMode(true);
                PWDeditText.requestFocus();
            }
        });

        Button loginButton = findViewById(R.id.loginbutton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 사용자가 입력한 이메일과 비밀번호 가져오기
                String email = IDeditText.getText().toString().trim();
                String password = PWDeditText.getText().toString().trim();

                if (email.isEmpty()) {
                    Toast.makeText(loginpage.this, "이메일을 입력해주세요.", Toast.LENGTH_SHORT).show();
                    return; // 이메일 또는 비밀번호가 비어있으면 함수 종료
                }
                else if(!TextUtils.isEmpty(email) && TextUtils.isEmpty(password)){
                    Toast.makeText(loginpage.this,"비밀번호를 입력해주세요.",Toast.LENGTH_SHORT).show();
                    return;
                }

                // Firebase Authentication을 사용하여 로그인
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(loginpage.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // 로그인 성공
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    Toast.makeText(loginpage.this, "로그인에 성공했습니다.", Toast.LENGTH_SHORT).show();

                                    // 체크박스 상태에 따라 로그인 정보 저장
                                    SharedPreferences.Editor editor = preferences.edit();
                                    if (rememberMeCheckBox.isChecked()) {
                                        editor.putString("email", email);
                                        editor.putString("password", password);
                                        editor.putBoolean("rememberMe", true);
                                    } else {
                                        editor.clear();
                                    }
                                    editor.apply();

                                    Intent intent = new Intent(loginpage.this, mainpage.class);
                                    startActivity(intent);
                                } else {
                                    // 로그인 실패
                                    Toast.makeText(loginpage.this, "로그인에 실패했습니다. 이메일 또는 비밀번호를 확인해주세요.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }
}
