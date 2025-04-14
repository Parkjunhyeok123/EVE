package com.company.eve.ui.community;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.company.eve.R;
import com.company.eve.model.Station;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SearchStationActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private StationsAdapter stationsAdapter;
    private List<Station> stationsList = new ArrayList<>();
    private AutoCompleteTextView searchAutoCompleteTextView;
    private EditText customStationNameEditText;
    private Button searchButton;
    private Button saveButton;
    private ArrayAdapter<String> autoCompleteAdapter;
    private String[] suggestedSearchTerms = {"검색어1", "검색어2", "검색어3"};
    private static final int REQUEST_CODE_WRITE_REVIEW = 1; // 요청 코드 상수 추가

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_station);

        // 상단바 제목 설정
        setTitle("충전소 리뷰");

        searchAutoCompleteTextView = findViewById(R.id.search_auto_complete_text_view);
        searchButton = findViewById(R.id.search_button);

        recyclerView = findViewById(R.id.recycler_view_stations);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        stationsAdapter = new StationsAdapter(stationsList, this::onStationSelected);
        recyclerView.setAdapter(stationsAdapter);

        // 제안된 검색어로 AutoCompleteTextView 초기화
        autoCompleteAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, suggestedSearchTerms);
        searchAutoCompleteTextView.setAdapter(autoCompleteAdapter);
        searchAutoCompleteTextView.setThreshold(2); // 최소 2글자 입력 시 제안

        // 검색어 입력 시 실시간 업데이트
        searchAutoCompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                String query = searchAutoCompleteTextView.getText().toString().trim();
                if (query.length() >= 2) {
                    searchStations(query);
                } else {
                    stationsList.clear();
                    stationsAdapter.notifyDataSetChanged();
                }
            }
        });

        // 검색 버튼 클릭 시 검색 수행
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String query = searchAutoCompleteTextView.getText().toString().trim();
                if (query.length() >= 2) {
                    searchStations(query);
                } else {
                    Toast.makeText(SearchStationActivity.this, "검색어를 두 글자 이상 입력하세요.", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    private void searchStations(String query) {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("stations");

        Log.d("SearchStationActivity", "Querying for: " + query);

        databaseRef.orderByChild("addr").startAt(query).endAt(query + "\uf8ff").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                stationsList.clear();
                for (DataSnapshot stationSnapshot : dataSnapshot.getChildren()) {
                    Station station = stationSnapshot.getValue(Station.class);
                    if (station != null) {
                        stationsList.add(station);
                        Log.d("SearchStationActivity", "Station found: " + station.getStatNm());
                    }
                }
                stationsAdapter.notifyDataSetChanged();
                if (stationsList.isEmpty()) {
                    Toast.makeText(SearchStationActivity.this, "검색 결과가 없습니다.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                String errorMessage = databaseError.getMessage();
                Toast.makeText(SearchStationActivity.this, "데이터베이스 오류: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void onStationSelected(Station station) {
        Intent intent = new Intent(this, WriteReviewActivity.class);
        intent.putExtra("stationName", station.getStatNm());
        startActivityForResult(intent, REQUEST_CODE_WRITE_REVIEW); // 요청 코드 추가
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_WRITE_REVIEW) {
            if (resultCode == RESULT_OK && data != null) {
                boolean reviewSaved = data.getBooleanExtra("reviewSaved", false);
                if (reviewSaved) {
                    // SearchStationActivity 종료하여 CommunityFourthFragment로 돌아감
                    finish();
                }
            }
        }
    }
}