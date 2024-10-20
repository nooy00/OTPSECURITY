package com.example.asdfadsf;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class settingpage extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settingpage);

        mAuth = FirebaseAuth.getInstance();

        Button gobackButton = findViewById(R.id.gobackbutton);
        gobackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(settingpage.this, mainpage.class);
                startActivity(intent);
            }
        });

        Button findMypwdButton = findViewById(R.id.findmypwdButton); // 비밀번호 찾기 / 재설정
        findMypwdButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCustomPWDDialog();
            }
        });


        Button withDrawerButton = findViewById(R.id.WtihdrawalButton);
        withDrawerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showWithdrawConfirmationDialog();
            }
        });

        Button customInfoButton = findViewById(R.id.custominfoButton); // 이름 , 전화번호 변경
        customInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCustomInfoDialog();
            }
        });
    }


    private void showCustomInfoDialog() {
        AlertDialog.Builder dlg = new AlertDialog.Builder(settingpage.this);
        dlg.setTitle("회원정보 수정");

        final EditText editTextPhoneNumber = new EditText(settingpage.this);
        editTextPhoneNumber.setHint("새 전화번호를 입력하세요.");
        editTextPhoneNumber.setInputType(InputType.TYPE_CLASS_PHONE);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(100, 50, 100, 0);
        editTextPhoneNumber.setLayoutParams(params);

        LinearLayout layout = new LinearLayout(settingpage.this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(editTextPhoneNumber);

        dlg.setView(layout);

        dlg.setPositiveButton("변경하기", (dialog, which) -> {
            String newPhoneNumber = editTextPhoneNumber.getText().toString().trim();
            if (isValidPhoneNumber(newPhoneNumber)) {
                FirebaseUser user = mAuth.getCurrentUser();
                if (user != null) {
                    String userId = user.getUid();
                    DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId);

                    // Check if the new phone number already exists in the database
                    DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
                    rootRef.child("users").orderByChild("phoneNumber").equalTo(newPhoneNumber).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                // New phone number already exists
                                Toast.makeText(settingpage.this, "이미 존재하는 전화번호입니다.", Toast.LENGTH_SHORT).show();
                            } else {
                                // New phone number does not exist, proceed with the update
                                userRef.child("phoneNumber").get().addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        DataSnapshot snapshot = task.getResult();
                                        if (snapshot.exists() && snapshot.getValue(String.class).equals(newPhoneNumber)) {
                                            Toast.makeText(settingpage.this, "새 전화번호가 기존 번호와 동일합니다.", Toast.LENGTH_SHORT).show();
                                        } else {
                                            userRef.child("phoneNumber").setValue(newPhoneNumber)
                                                    .addOnCompleteListener(updateTask -> {
                                                        if (updateTask.isSuccessful()) {
                                                            Toast.makeText(settingpage.this, "전화번호가 성공적으로 변경되었습니다.", Toast.LENGTH_SHORT).show();
                                                        } else {
                                                            Toast.makeText(settingpage.this, "전화번호 업데이트 실패: " + updateTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                        }
                                    } else {
                                        Toast.makeText(settingpage.this, "전화번호 조회 실패: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Toast.makeText(settingpage.this, "전화번호 조회 실패: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } else {
                Toast.makeText(settingpage.this, "유효한 전화번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
            }
        });

        dlg.setNegativeButton("취소", null);

        AlertDialog alertDialog = dlg.create();
        alertDialog.show();
        alertDialog.getWindow().setLayout(1000, WindowManager.LayoutParams.WRAP_CONTENT);
    }


    private boolean isValidPhoneNumber(String phoneNumber) {
        return phoneNumber.matches("^01[0-9]{9}$");
    }
    private void showWithdrawConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("회원 탈퇴 확인")
                .setMessage("정말로 회원 탈퇴를 하시겠습니까?");

        builder.setPositiveButton("예", null);
        builder.setNegativeButton("아니오", null);

        AlertDialog dialog = builder.create();
        dialog.show();

        // 버튼을 가져와서 순서를 바꿈
        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        Button negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);

        LinearLayout.LayoutParams positiveParams = (LinearLayout.LayoutParams) positiveButton.getLayoutParams();
        LinearLayout.LayoutParams negativeParams = (LinearLayout.LayoutParams) negativeButton.getLayoutParams();

        positiveParams.weight = 1;
        negativeParams.weight = 1;

        LinearLayout buttonLayout = (LinearLayout) positiveButton.getParent();
        buttonLayout.removeView(positiveButton);
        buttonLayout.removeView(negativeButton);

        buttonLayout.addView(positiveButton, negativeParams);
        buttonLayout.addView(negativeButton, positiveParams);

        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                withdrawUser();
                dialog.dismiss();
            }
        });

        negativeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    private void withdrawUser() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();

            user.delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // 사용자 계정 삭제 성공
                            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId);
                            DatabaseReference otpRef = FirebaseDatabase.getInstance().getReference().child("otpList").child(userId); // otp참조
                            DatabaseReference authRef = FirebaseDatabase.getInstance().getReference().child("authentic_list").child(userId); // authentic 참조
                            authRef.removeValue(); // 삭제 함수
                            otpRef.removeValue(); // 삭제 함수
                            userRef.removeValue()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            // Realtime Database의 사용자 데이터 삭제 성공
                                            Toast.makeText(settingpage.this, "회원탈퇴가 완료되었습니다.", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(settingpage.this, beforelogin.class);
                                            startActivity(intent);
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            // Realtime Database의 사용자 데이터 삭제 실패
                                            Toast.makeText(settingpage.this, "다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // 사용자 계정 삭제 실패
                            Toast.makeText(settingpage.this, "다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void showCustomPWDDialog() {
        AlertDialog.Builder dlg = new AlertDialog.Builder(settingpage.this);
        dlg.setTitle("비밀번호 재설정"); // 제목

        final EditText editTextCurrentPwd = new EditText(settingpage.this);
        editTextCurrentPwd.setHint("현재 비밀번호를 입력하세요.");
        editTextCurrentPwd.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        final EditText editTextNewPwd = new EditText(settingpage.this);
        editTextNewPwd.setHint("새 비밀번호를 입력하세요.");
        editTextNewPwd.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(100, 50, 100, 0); // 좌우 마진 조정
        editTextCurrentPwd.setLayoutParams(params);
        editTextNewPwd.setLayoutParams(params);

        LinearLayout layout = new LinearLayout(settingpage.this);
        layout.setOrientation(LinearLayout.VERTICAL);

        layout.addView(editTextCurrentPwd);
        layout.addView(editTextNewPwd);

        dlg.setView(layout);

        dlg.setPositiveButton("변경하기", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                String currentPwd = editTextCurrentPwd.getText().toString().trim();
                String newPwd = editTextNewPwd.getText().toString().trim();

                if (currentPwd.isEmpty() || newPwd.isEmpty()) {
                    Toast.makeText(settingpage.this, "모든 필드를 입력해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (currentPwd.equals(newPwd)) {
                    Toast.makeText(settingpage.this, "새 비밀번호는 현재 비밀번호와 다르게 설정해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

                FirebaseUser user = mAuth.getCurrentUser();
                if (user != null && user.getEmail() != null) {
                    // 현재 비밀번호로 재인증
                    AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), currentPwd);
                    user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                // 재인증 성공 시 비밀번호 변경
                                user.updatePassword(newPwd).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            // 비밀번호 변경 성공
                                            Toast.makeText(settingpage.this, "비밀번호가 성공적으로 변경되었습니다.", Toast.LENGTH_SHORT).show();

                                            // 사용자의 데이터베이스에 비밀번호 업데이트
                                            String uid = user.getUid();
                                            updatePasswordInDatabase(uid, newPwd);
                                        } else {
                                            // 비밀번호 변경 실패
                                            Toast.makeText(settingpage.this, "비밀번호 변경에 실패했습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            } else {
                                // 재인증 실패
                                Toast.makeText(settingpage.this, "현재 비밀번호가 올바르지 않습니다.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

        dlg.setNegativeButton("취소", null);

        AlertDialog alertDialog = dlg.create();
        alertDialog.show();

        // AlertDialog의 너비와 높이를 설정합니다.
        alertDialog.getWindow().setLayout(1000, WindowManager.LayoutParams.WRAP_CONTENT);
    }

    private void updatePasswordInDatabase(String uid, String newPassword) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(uid);

        userRef.child("password").setValue(newPassword)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // 데이터베이스에 비밀번호 업데이트 성공
                        Toast.makeText(settingpage.this, "데이터베이스에 비밀번호가 성공적으로 업데이트되었습니다.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // 데이터베이스에 비밀번호 업데이트 실패
                        Toast.makeText(settingpage.this, "데이터베이스에 비밀번호를 업데이트하는 중에 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
                    }
                });

    }
}
