package com.mp.test_cv;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


import java.lang.reflect.Member;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    String date;
    int totCalorie;
    int recCalorie;
    Map<String, Integer> totNutritionMap; //Nutrition과 섭취량을 매핑할 변수 생성
    Map<String, Integer> recNutritionMap; //권장 영양소 섭취량
    MemberInfo userInfo;
    float userBMI;
    boolean isTotalLoaded = false;
    boolean isRecommendLoaded;
    boolean isUserLoaded;
    FirebaseUser user;

    // RecyclerView
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onResume() {
        super.onResume();
        calorieTextView();
        setRecyclerView();
        BMIinfoSetting();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        totNutritionMap = new HashMap<String, Integer>();
        recNutritionMap = new HashMap<String, Integer>();
        user = FirebaseAuth.getInstance().getCurrentUser();
        Date today = new Date();
        SimpleDateFormat timeFormat = new SimpleDateFormat("yyyyMMddhhmmss");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        String time = timeFormat.format(today);
        date = dateFormat.format(today);

        findViewById(R.id.logoutButton).setOnClickListener(onClickListener);
        findViewById(R.id.NutritionInfoButton).setOnClickListener(onClickListener);
        if (user == null) {
            myStartActivity(SignUpActivity.class);
            startToast("로그인을 해주세요.");
        }
        else {
            // DailyIntake 생성
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference docRef = db.collection("User").document(user.getUid());
            docRef.collection("TotalDailyIntake").document(date).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    TotalDailyIntake totalDailyIntake = documentSnapshot.toObject(TotalDailyIntake.class);
                    if (documentSnapshot.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + documentSnapshot.getData());
                        totalDailyIntakeMapping(totalDailyIntake);
                        calorieTextView();
                        setRecyclerView();
                    } else {
                        Log.d(TAG, "No such document");
                        TotalDailyIntake initialTotalDailyIntake = new TotalDailyIntake(0,0,0,0,0,0,0,0);
                        totalDailyIntakeMapping(initialTotalDailyIntake);
                        calorieTextView();
                        setRecyclerView();
                    }
                }
            });
            docRef.collection("RecDailyIntake").document(user.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    RecDailyIntake recDailyIntake = documentSnapshot.toObject(RecDailyIntake.class);
                    if (documentSnapshot.exists()) {
                        recommendDailyIntakeMapping(recDailyIntake);
                        calorieTextView();
                        setRecyclerView();
                        Log.d(TAG, "DocumentSnapshot data: " + documentSnapshot.getData());
                    } else {
                        if (!isRecommendLoaded) {
                            startToast("기본 정보를 기록해주세요.");
                            myStartActivity(MemberInitActivity.class);
                        }
                        startToast("잘못된 접근입니다..");
                        Log.d(TAG, "No such document");
                    }
                }
            });
            docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    MemberInfo memberInfo = documentSnapshot.toObject(MemberInfo.class);
                    if (documentSnapshot.exists()) {
                        userInfoSetUp(memberInfo);
                        BMIinfoSetting();
                        calorieTextView();
                        setRecyclerView();
                        Log.d(TAG, "DocumentSnapshot data: " + documentSnapshot.getData());
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        DocumentReference docRef = db.collection("User").document(user.getUid());
                        docRef.collection("TotalDailyIntake").document(date).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                TotalDailyIntake totalDailyIntake = documentSnapshot.toObject(TotalDailyIntake.class);
                                if (documentSnapshot.exists()) {
                                    Log.d(TAG, "DocumentSnapshot data: " + documentSnapshot.getData());
                                    totalDailyIntakeMapping(totalDailyIntake);
                                    calorieTextView();
                                    setRecyclerView();
                                } else {
                                    Log.d(TAG, "No such document");
                                    TotalDailyIntake initialTotalDailyIntake = new TotalDailyIntake(0,0,0,0,0,0,0,0);
                                    totalDailyIntakeMapping(initialTotalDailyIntake);
                                    calorieTextView();
                                    setRecyclerView();
                                }
                            }
                        });
                    }
                    else{
                        Log.d(TAG, "No such document");
                        // startToast("회원가입을 해주세요.");
                        //myStartActivity(SignUpActivity.class);
                    }
                }
            });
            //totlDailyIntake data에서 totalCalories / recDatilyIntake에서 recCalorie
        }
    }


    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.logoutButton:
                    FirebaseAuth.getInstance().signOut();
                    myStartActivity(SignUpActivity.class);
                    break;
                case R.id.NutritionInfoButton:
                    Intent intent = new Intent(getApplicationContext(), NutritionInfoActivity.class);
                    startActivity(intent);
                    break;
            }
        }
    };


    private void myStartActivity(Class c) {
        Intent intent = new Intent(this, c);
        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //로그인 한 상태에서 뒤로가기 눌렀을 때 메인액티비로 이동, 나머지 스택 없어짐.
        startActivity(intent);
    }

    private void startToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }


    private void calorieTextView() {
        int caloriePercent;
        TextView textCaloriePercent = (TextView) findViewById(R.id.textCaloriePercent);
        TextView textRealCalorie = (TextView) findViewById(R.id.textRealCalorie);
        TextView textRecommendedCalorie = (TextView) findViewById(R.id.textRecommendedCalorie);

        if(recCalorie != 0){
            caloriePercent = (totCalorie * 100) / recCalorie;
        } else {
            caloriePercent = 0;
        }

        textCaloriePercent.setText("오늘 권장칼로리의 " + caloriePercent + "%를\n섭취하셨습니다.");
        textCaloriePercent.setTextColor(Color.parseColor("#673AB7"));
        textRealCalorie.setText("총 섭취 칼로리 : " + totCalorie+ "kcal");
        textRecommendedCalorie.setText("권장 섭취 칼로리 : " + recCalorie + "kcal");
    }

    private void totalDailyIntakeMapping(TotalDailyIntake totalDailyIntake) {
        totCalorie = totalDailyIntake.gettotalCalories();
        totNutritionMap.put("carbohydrate", totalDailyIntake.gettotalCarbohydrate());
        totNutritionMap.put("protein", totalDailyIntake.gettotalProtein());
        totNutritionMap.put("fat", totalDailyIntake.gettotalFat());
        totNutritionMap.put("saturatedFat", totalDailyIntake.gettotalSaturatedFat());
        totNutritionMap.put("sugar", totalDailyIntake.gettotalSugar());
        totNutritionMap.put("sodium", totalDailyIntake.gettotalSodium());
        totNutritionMap.put("dietaryfiber", totalDailyIntake.gettotalDietaryFiber());
        //후에 get으로 성분별 데이터 가져올 수 있음.
        isTotalLoaded = true;
    }
    private void recommendDailyIntakeMapping(RecDailyIntake recDailyIntake) {
        recCalorie = recDailyIntake.getrecCalories();
        recNutritionMap.put("carbohydrate", recDailyIntake.getrecCarbohydrate());
        recNutritionMap.put("protein", recDailyIntake.getrecProtein());
        recNutritionMap.put("fat", recDailyIntake.getrecFat());
        recNutritionMap.put("saturatedFat", recDailyIntake.getrecSaturatedFat());
        recNutritionMap.put("sugar", recDailyIntake.getrecSugar());
        recNutritionMap.put("sodium", recDailyIntake.getrecSodium());
        recNutritionMap.put("dietaryfiber", recDailyIntake.getrecDietaryFiber());
        //후에 get으로 성분별 데이터 가져올 수 있음.
        isRecommendLoaded = true;
    }
    private void BMIinfoSetting() {
        if (!isUserLoaded) {
            this.userBMI = 0;
            return;
        }
        else {
            this.userBMI = userInfo.getWeight() / (float)Math.pow(userInfo.getHeight() / 100, 2);
            TextView bmiView = (TextView)findViewById(R.id.BMI_info);
            bmiView.setText("BMI: " + userBMI + "\n");
        }
    }
    private void userInfoSetUp(MemberInfo userInfo) {
        if (userInfo == null) {
            //myStartActivity(MemberInitActivity.class);
            return;
        }
        else {
            this.userInfo = userInfo;
            isUserLoaded = true;
        }
    }

    private void setRecyclerView() {
        if (!isRecommendLoaded || !isTotalLoaded) {
            return;
        }

        if (recCalorie != 0) {
            if(totNutritionMap!=null && recNutritionMap!=null){
                recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
                recyclerView.setHasFixedSize(true);
                layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, true);
                recyclerView.setLayoutManager(layoutManager);
                mAdapter = new MyAdapter(totNutritionMap, recNutritionMap);
                recyclerView.setAdapter(mAdapter);
            }
        } else {
            startToast("사용자 정보가 없습니다.");
        }
    }

}