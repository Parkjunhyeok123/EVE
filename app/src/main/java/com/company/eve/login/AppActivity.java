package com.company.eve.login;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.company.eve.subActivity;
import com.company.eve.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class AppActivity extends AppCompatActivity {

    private FirebaseAuth fAuth; // 파이어베이스 인증
    private DatabaseReference dRef; // 실시간 데이터베이스

    Button loginBtn;
    TextView findIdTextView, findPwTextView, signUpTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app);

        EditText edtId, edtPw;

        fAuth = FirebaseAuth.getInstance();
        dRef = FirebaseDatabase.getInstance().getReference();

        edtId = findViewById(R.id.login_idEditText);
        edtPw = findViewById(R.id.login_pwEditText);

        loginBtn = (Button) findViewById(R.id.loginBtn);
        findIdTextView = (TextView) findViewById(R.id.findIdTextView);
        findPwTextView = (TextView) findViewById(R.id.findPwTextView);
        signUpTextView = (TextView) findViewById(R.id.signUpTextView);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String id = edtId.getText().toString();
                final String pw = edtPw.getText().toString();

                if (id.isEmpty() || pw.isEmpty()) {
                    Toast.makeText(AppActivity.this, "아이디와 비밀번호를 입력하세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

                fAuth.signInWithEmailAndPassword(id, pw).addOnCompleteListener(AppActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("AppActivity", "로그인 시도 완료");
                        if (task.isSuccessful()) {
                            Log.d("AppActivity", "로그인 성공");
                            Intent intent = new Intent(AppActivity.this, subActivity.class);
                            startActivity(intent);
                        } else {
                            Log.d("AppActivity", "로그인 실패", task.getException());
                            Toast.makeText(AppActivity.this, "로그인 실패: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        findIdTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // findIdTextView를 클릭했을 때의 동작
                Intent intent = new Intent(com.company.eve.login.AppActivity.this, com.company.eve.login.FindIdActivity.class);
                startActivity(intent);
            }
        });

        findPwTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // findPwTextView를 클릭했을 때의 동작
                Intent intent = new Intent(com.company.eve.login.AppActivity.this, com.company.eve.login.FindPwActivity.class);
                startActivity(intent);
            }
        });

        signUpTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // signUpTextView를 클릭했을 때의 동작
                Intent intent = new Intent(com.company.eve.login.AppActivity.this, com.company.eve.login.SignUpActivity.class);
                startActivity(intent);
            }
        });
    }
}