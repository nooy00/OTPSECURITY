package com.example.asdfadsf;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class signpage extends AppCompatActivity {


    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signpage);

        mAuth = FirebaseAuth.getInstance();

        // gobackbutton 클릭 시 이벤트 처리
        Button goBackButton = findViewById(R.id.gobackbutton);
        goBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 이전 페이지(로그인 하기 전 페이지)로 이동
                Intent intent = new Intent(signpage.this, beforelogin.class);
                startActivity(intent);
            }
        });

        EditText editText1 = findViewById(R.id.editText1);
        EditText editText2 = findViewById(R.id.editText2);
        EditText editText3 = findViewById(R.id.editText3);
        EditText editText4 = findViewById(R.id.editText4);
        EditText editText5 = findViewById(R.id.editText5);


        editText1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText1.setFocusableInTouchMode(true);
                editText1.requestFocus();
            }
        });

        editText2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText2.setFocusableInTouchMode(true);
                editText2.requestFocus();
            }
        });

        editText3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText3.setFocusableInTouchMode(true);
                editText3.requestFocus();
            }
        });

        editText4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText4.setFocusableInTouchMode(true);
                editText4.requestFocus();
            }
        });

        editText5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText5.setFocusableInTouchMode(true);
                editText5.requestFocus();
            }
        });

        // signinbutton 클릭 시 이벤트 처리
        Button signInButton = findViewById(R.id.signinbutton);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 회원가입 정보 가져오기
                String address = editText2.getText().toString().trim();
                String id = editText4.getText().toString().trim();
                String password = editText5.getText().toString().trim();
                String name = editText1.getText().toString().trim();
                String phoneNumber = editText3.getText().toString().trim();

                // 필수 정보가 입력되었는지 확인
                if (address.isEmpty() || id.isEmpty() || password.isEmpty() || name.isEmpty() || phoneNumber.isEmpty()) {
                    // 사용자에게 필수 정보를 입력하라는 메시지 표시
                    Toast.makeText(signpage.this, "모든 정보를 입력해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // 개인정보 이용 동의 확인
                Switch consentSwitch = findViewById(R.id.switch1);
                if (!consentSwitch.isChecked()) {
                    // 동의 스위치가 체크되지 않은 경우
                    Toast.makeText(signpage.this, "개인정보 이용에 동의해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Firebase Realtime Database에서 전화번호 중복 확인
                DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("users");
                usersRef.orderByChild("phoneNumber").equalTo(phoneNumber).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            // 전화번호가 이미 존재하는 경우
                            Toast.makeText(signpage.this, "이미 사용 중인 전화번호입니다.", Toast.LENGTH_SHORT).show();
                        } else {
                            // Firebase Authentication을 사용하여 회원가입
                            mAuth.createUserWithEmailAndPassword(id, password)
                                    .addOnCompleteListener(signpage.this, new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if (task.isSuccessful()) {
                                                // 회원가입 성공
                                                FirebaseUser user = mAuth.getCurrentUser();
                                                Toast.makeText(signpage.this, "회원가입에 성공했습니다.", Toast.LENGTH_SHORT).show();

                                                // Firebase Realtime Database에 사용자 추가 정보 저장
                                                DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("users");
                                                String userId = user.getUid();

                                                // 사용자 정보를 HashMap에 저장
                                                HashMap<String, Object> userInfo = new HashMap<>();
                                                userInfo.put("name", name);
                                                userInfo.put("address", address);
                                                userInfo.put("phoneNumber", phoneNumber);
                                                userInfo.put("password",password);
                                                userInfo.put("UID",userId);

                                                // 해당 사용자의 데이터베이스 경로에 정보 업데이트
                                                usersRef.child(userId).setValue(userInfo)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    // 사용자 정보 업데이트 성공
                                                                    Toast.makeText(signpage.this, "사용자 정보가 저장되었습니다.", Toast.LENGTH_SHORT).show();
                                                                    Intent intent = new Intent(signpage.this, signsucsesspage.class);
                                                                    intent.putExtra("userName", name);
                                                                    startActivity(intent);
                                                                } else {
                                                                    // 사용자 정보 업데이트 실패
                                                                    Toast.makeText(signpage.this, "사용자 정보 저장에 실패했습니다.", Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                        });
                                            } else if(password.length()< 6){

                                                Toast.makeText(signpage.this, "비밀번호를 6자리 이상 입력 해주세요.", Toast.LENGTH_SHORT).show();
                                            }
                                            else{
                                                Toast.makeText(signpage.this, "회원가입에 실패 했습니다.", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // 데이터베이스 조회 실패
                        Toast.makeText(signpage.this, "데이터베이스 조회에 실패했습니다.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}