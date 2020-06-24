package com.mp.test_cv;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mp.test_cv.LoginActivity;

public class SignUpActivity extends AppCompatActivity {
    private static final String TAG = "SignupActivity";
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        //get Button Event
        findViewById(R.id.signUpButton).setOnClickListener(onClickListener);
        findViewById(R.id.gotoLoginButton).setOnClickListener(onClickListener);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        moveTaskToBack(true);
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
        //회원 가입화면에서 뒤로가기 눌렀을 때, 아예 꺼버림
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.signUpButton:
                    signUp();
                    break;
                case R.id.gotoLoginButton:
                    myStartActivity(LoginActivity.class);
                    break;
            }
        }
    };

    private void signUp() {
        String email = ((EditText)findViewById(R.id.emailEditText)).getText().toString(); //get text를 사용하기 위해 형 변환
        String password = ((EditText)findViewById(R.id.passwordEditText)).getText().toString();
        String passwordCheck = ((EditText)findViewById(R.id.passwordCheckEditText)).getText().toString();
        if (email.length() > 0 && password.length() > 0 && passwordCheck.length() > 0) {
            if (password.equals(passwordCheck)) {
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d(TAG, "createUserWithEmail:success");
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    startToast("회원가입을 성공했습니다.");
                                    myStartActivity(MemberInitActivity.class);
                                    //updateUI(user); 성공했을 때 ui로직
                                } else {
                                    if (task.getException() != null) {
                                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                        startToast(task.getException().toString());
                                        //updateUI(null); 실패했을 때 ui로직
                                    }
                                }
                            }
                        });
            } else {
                startToast("비밀번호가 일치하지 않습니다.");
                //updateUI(null); 실패했을 때 ui로직
            }
        }
        else {
            startToast("이메일 또는 비밀번호를 입력해 주세요.");
        }
    }

    private void startToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
    private void myStartActivity(Class c) {
        Intent intent = new Intent(this, c);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
