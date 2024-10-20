package com.example.asdfadsf;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseAuth;

public class authenticpage extends AppCompatActivity {
    private boolean isAdminAuthenticated = false; // 관리자 인증 여부 변수

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authenticpage);

        Button gobackButton = findViewById(R.id.gobackbutton);
        gobackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(authenticpage.this, mainpage.class);
                startActivity(intent);
            }
        });

        EditText AuthNumber = findViewById(R.id.et_auth);
        Button AuthButton = findViewById(R.id.btn_auth);

        AuthButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String AuthNum = AuthNumber.getText().toString().trim();
                if (AuthNum.isEmpty()) {
                    Toast.makeText(authenticpage.this, "인증코드를 입력해 주세요.", Toast.LENGTH_SHORT).show();
                } else {
                    // Firebase Database 참조
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference myRef = database.getReference("auth_number");

                    // 데이터베이스에서 auth_number 값을 읽어옵니다.
                    myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            // 데이터베이스에서 숫자 값을 읽어옵니다.
                            Integer authNumber = dataSnapshot.getValue(Integer.class);
                            if (authNumber != null && authNumber.toString().equals(AuthNum)) {
                                // 사용자가 입력한 값과 데이터베이스의 값이 같은 경우
                                showChecklistDialog();
                            } else {
                                // 값이 다른 경우
                                Toast.makeText(authenticpage.this, "코드가 올바르지 않습니다.", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            // 데이터 읽기 실패
                            Log.w("authenticpage", "Failed to read value.", databaseError.toException());
                        }
                    });
                }
            }
        });
    }

    private void showChecklistDialog() {
        AlertDialog.Builder dlg = new AlertDialog.Builder(authenticpage.this);
        dlg.setTitle("관리자 체크리스트"); //제목
        final String[] versionArray = new String[]{"관리자 (거주자) 가 맞습니다.", "OTP를 공유하지 않겠습니다.", "정보제공에 동의합니다."};
        final boolean[] checkArray = new boolean[]{false, false, false};

        dlg.setMultiChoiceItems(versionArray, checkArray, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                // 체크박스 클릭 이벤트 처리
            }
        });
        dlg.setPositiveButton("동의", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                boolean allChecked = true;
                for (boolean checked : checkArray) {
                    if (!checked) {
                        allChecked = false;
                        break;
                    }
                }
                if (allChecked) {
                    // 모두 체크된 경우 동의 처리
                    Toast.makeText(authenticpage.this, "관리자 체크리스트에 동의하셨습니다.", Toast.LENGTH_SHORT).show();

                    // Firebase Realtime Database에 UID 저장
                    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                    if (currentUser != null) {
                        String uid = currentUser.getUid();
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference authListRef = database.getReference("authentic_list");
                        authListRef.child(uid).setValue(true);
                        Intent intent = new Intent(authenticpage.this,mainpage.class);
                        Toast.makeText(authenticpage.this, "관리자 인증이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                        startActivity(intent);
                    }
                } else {
                    // 모두 체크되지 않은 경우 메시지 표시
                    Toast.makeText(authenticpage.this, "모두 체크해 주세요.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        dlg.setNegativeButton("거부", null);
        dlg.show();
    }
}