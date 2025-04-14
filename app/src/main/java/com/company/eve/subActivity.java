package com.company.eve;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.widget.Button;
import android.widget.TextView;

import com.company.eve.databinding.ActivitySubBinding;
import com.company.eve.login.UserAccount;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.appcompat.widget.Toolbar;

public class subActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivitySubBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // View Binding 초기화
        binding = ActivitySubBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Toolbar 설정
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // DrawerLayout 및 NavigationView 설정
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;

        // AppBarConfiguration 설정
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_community, R.id.nav_sercenter, R.id.nav_option, R.id.nav_mypage)
                .setOpenableLayout(drawer)
                .build();

        // NavController 설정
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        // 로그인 버튼 클릭 이벤트 처리
        View headerView = navigationView.getHeaderView(0);
        Button loginButton = headerView.findViewById(R.id.button_login);
        TextView titleTextView = headerView.findViewById(R.id.nav_header_title);
        TextView subtitleTextView = headerView.findViewById(R.id.nav_header_subtitle);

        FirebaseAuth fAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = fAuth.getCurrentUser();

        if (loginButton != null) {
            if (currentUser != null) {
                // 사용자가 로그인한 상태라면 로그아웃 버튼으로 텍스트 변경
                loginButton.setText("로그아웃");

                // 파이어베이스에서 사용자 정보 가져와서 헤더에 표시
                DatabaseReference userRef = FirebaseDatabase.getInstance().getReference()
                        .child("users").child(currentUser.getUid());

                userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            // Firebase에서 UserAccount 객체로 변환
                            UserAccount userAccount = snapshot.getValue(UserAccount.class);
                            if (userAccount != null) {
                                String displayName = userAccount.getName();  // 사용자 이름
                                String email = userAccount.getId();          // 사용자 이메일
                                String number = userAccount.getNumber();     // 사용자 번호

                                // Log로 데이터 확인
                                Log.d("subActivity", "User data fetched: Name=" + displayName +
                                        ", Email=" + email + ", Number=" + number);

                                // 메인 스레드에서 UI 업데이트
                                runOnUiThread(() -> {
                                    titleTextView.setText(displayName);   // 사용자 이름 설정
                                    subtitleTextView.setText(email);      // 사용자 이메일 설정
                                });
                            } else {
                                Log.e("subActivity", "UserAccount object is null");
                                titleTextView.setText("게스트");
                                subtitleTextView.setText("로그인 해주세요");
                            }
                        } else {
                            Log.e("subActivity", "No user data found for the current user.");
                            titleTextView.setText("게스트");
                            subtitleTextView.setText("로그인 해주세요");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("subActivity", "Error fetching user data: " + error.getMessage());
                    }
                });

            } else {
                // 사용자가 로그인하지 않은 상태라면 로그인 버튼으로 텍스트 변경
                loginButton.setText("로그인");

                // 사용자 정보 초기화
                titleTextView.setText(getString(R.string.nav_header_title));
                subtitleTextView.setText(getString(R.string.nav_header_subtitle));
            }

            // 로그인 버튼 클릭 리스너 설정
            loginButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FirebaseUser currentUser = fAuth.getCurrentUser();
                    if (currentUser != null) {
                        // 사용자가 로그인한 상태라면 로그아웃 처리
                        fAuth.signOut();
                        loginButton.setText("로그인");

                        // 헤더에 표시되는 사용자 정보 초기화
                        titleTextView.setText(getString(R.string.nav_header_title));
                        subtitleTextView.setText(getString(R.string.nav_header_subtitle));

                        // 다른 로그아웃 관련 처리 코드 추가 가능
                    } else {
                        // 사용자가 로그인하지 않은 상태라면 로그인 화면으로 이동
                        startActivity(new Intent(subActivity.this, com.company.eve.login.AppActivity.class));
                    }
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // 메뉴 Inflate (옵션 메뉴 추가)
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        // 네비게이션 업 동작 처리
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}
