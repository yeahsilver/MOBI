package com.mp.test_cv;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.ScatterChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.ScatterData;
import com.github.mikephil.charting.data.ScatterDataSet;
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

import com.github.mikephil.charting.charts.CombinedChart;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    String date;
    Map<String, Integer> totNutritionMap; //Nutrition과 섭취량을 매핑할 변수 생성
    Map<String, Integer> recNutritionMap;
    MemberInfo userInfo;
    float userBMI;
    boolean isTotalLoaded;
    boolean isRecommendLoaded;
    boolean isUserLoaded;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        totNutritionMap = new HashMap<String, Integer>();
        recNutritionMap = new HashMap<String, Integer>();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        Date today = new Date();
        SimpleDateFormat timeFormat = new SimpleDateFormat("yyyyMMddhhmmss");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        String time = timeFormat.format(today);
        date = dateFormat.format(today);

        findViewById(R.id.logoutButton).setOnClickListener(onClickListener);
        findViewById(R.id.NutritionInfoButton).setOnClickListener(onClickListener);
        if (user != null) {
            // DailyIntake 생성
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference docRef = db.collection("User").document(user.getUid());
            docRef.collection("TotalDailyIntake").document(date).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    TotalDailyIntake totalDailyIntake = documentSnapshot.toObject(TotalDailyIntake.class);
                    if (documentSnapshot != null) {
                        if (documentSnapshot.exists()) {
                            Log.d(TAG, "DocumentSnapshot data: " + documentSnapshot.getData());
                            totalDailyIntakeMapping(totalDailyIntake);
                            textSetUpProcess();
                        } else {
                            Log.d(TAG, "No such document");
                            myStartActivity(NutritionInfoActivity.class);
                            startToast("총섭취량 데이터 zero 초기화.");
                        }
                    }
                }
            });
            docRef.collection("RecDailyIntake").document(user.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    RecDailyIntake recDailyIntake = documentSnapshot.toObject(RecDailyIntake.class);
                    if (documentSnapshot != null) {
                        if (documentSnapshot.exists()) {
                            recommendDailyIntakeMapping(recDailyIntake);
                            textSetUpProcess();
                            Log.d(TAG, "DocumentSnapshot data: " + documentSnapshot.getData());
                        } else {
                            myStartActivity(MemberInitActivity.class);
                            Log.d(TAG, "No such document");
                            startToast("섭취량을 기록해주세요. 아직 없다면 수정완료를 눌러주세요");
                        }
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
                        Log.d(TAG, "DocumentSnapshot data: " + documentSnapshot.getData());
                    }
                    else{
                        if (!isRecommendLoaded) {
                            myStartActivity(MemberInitActivity.class);
                        }
                        Log.d(TAG, "No such document");
                        startToast("회원가입을 해주세요.");
                    }
                }
            });
            //totlDailyIntake data에서 totalCalories / recDatilyIntake에서 recCalorie
        } else {
            myStartActivity(SignUpActivity.class);
            startToast("로그인을 해주세요.");
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

    // 그래프 그리기
    private void updateChartActivity() {
        CombinedChart chart = findViewById(R.id.carboChart);

        chart.getDescription().setEnabled(false);
        chart.setBackgroundColor(Color.WHITE);
        chart.setDrawGridBackground(false);
        chart.setDrawBarShadow(false);
        chart.setHighlightFullBarEnabled(false);

        // draw bars behind scatter
        chart.setDrawOrder(new CombinedChart.DrawOrder[]{
                CombinedChart.DrawOrder.BAR, CombinedChart.DrawOrder.SCATTER
        });


        // 비활성화
        chart.setTouchEnabled(false);
        chart.setDragEnabled(false);
        chart.setScaleEnabled(false);
        chart.setPinchZoom(false);

        // 범례
        Legend l = chart.getLegend();
        l.setWordWrapEnabled(true);
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);

        // x축 : 영양소
        XAxis xAxis = chart.getXAxis();
        xAxis.setEnabled(false);

        // y축 : 섭취량
        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setEnabled(true);
        leftAxis.setDrawGridLines(true);
        leftAxis.setDrawAxisLine(true);

        // TODO : min, max 세팅 영양소별로 다르게 해줘야함
        leftAxis.setAxisMinimum(0f);
        leftAxis.setAxisMaximum(2000f);

        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setEnabled(false);


        // 그래프 그리기
        int realIntake = totNutritionMap.get("carbohydrate");
        int recommendedIntake = recNutritionMap.get("carbohydrate");
        CombinedData data = new CombinedData();
        data.setData(generateBarData(realIntake));
        data.setData(generateScatterData(recommendedIntake));

        chart.setData(data);
        chart.invalidate();
    }

    private void textSetUpProcess() {
        if (!isRecommendLoaded || !isTotalLoaded) {
            return;
        }
        if (recNutritionMap.get("calories") != null && recNutritionMap.get("carbohydrate") != null) {
            int caloriePercent = totNutritionMap.get("calories") / recNutritionMap.get("calories");
            int carbohydratePercent = totNutritionMap.get("carbohydrate") / recNutritionMap.get("carbohydrate");
            // 칼로리 내역 표시
            calorieTextView(caloriePercent);
            // 탄수화물 섭취 내역
            updateChartActivity();   // 탄수화물 섭취 현황 차트
            carbohydrateTextView(carbohydratePercent);
            BMIinfoSetting();
        } else {
            calorieTextView(0);
            carbohydrateTextView(0);
            startToast("사용자 정보가 없습니다.");
            myStartActivity(MemberInitActivity.class);
        }
    }
    private void calorieTextView(int caloriePercent) {
        TextView textCaloriePercent = (TextView) findViewById(R.id.textCaloriePercent);
        TextView textRealCalorie = (TextView) findViewById(R.id.textRealCalorie);
        TextView textRecommendedCalorie = (TextView) findViewById(R.id.textRecommendedCalorie);
        textCaloriePercent.setText("오늘 권장칼로리의 " + caloriePercent + "%를\n섭취하셨습니다.");
        textRealCalorie.setText("총 섭취 칼로리 : " + totNutritionMap.get("calories") + "kcal");
        textRecommendedCalorie.setText("권장 섭취 칼로리 : " + recNutritionMap.get("calories") + "kcal");
    }
    private void carbohydrateTextView(int carbohydratePercent) {
        TextView textCarbo = (TextView) findViewById(R.id.textCarbo);
        textCarbo.setText("탄수화물\n오늘 권장탄수화물의 " + carbohydratePercent
                + "%를\n섭취하셨습니다.\n권장섭취량 : " + recNutritionMap.get("carbohydrate") +
                "g\n실제섭취량 : " + totNutritionMap.get("carbohydrate") + "g\n");
    }
    private void totalDailyIntakeMapping(TotalDailyIntake totalDailyIntake) {
        totNutritionMap.put("calories", totalDailyIntake.gettotalCalories());
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
        recNutritionMap.put("calories", recDailyIntake.getrecCalories());
        recNutritionMap.put("carbohydrate", recDailyIntake.getrecCarbohydrate());
        recNutritionMap.put("protein", recDailyIntake.getrecProtein());
        recNutritionMap.put("fat", recDailyIntake.getrecFat());
        //recNutritionMap.put("saturatedFat", recDailyIntake.getrecSaturatedFat());
        //recNutritionMap.put("sugar", recDailyIntake.getrecSugar());
        //recNutritionMap.put("sodium", recDailyIntake.getrecSodium());
        //recNutritionMap.put("dietaryfiber", recDailyIntake.getrecDietaryFiber());
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
            myStartActivity(MemberInitActivity.class);
            return;
        }
        else {
            this.userInfo = userInfo;
            isUserLoaded = true;
        }
    }
    // BarData : 실제 섭취량
    private BarData generateBarData(float realIntake) {

        ArrayList<BarEntry> entries = new ArrayList<>();

        // TODO : 실제 섭취량 받아와서 넣기
        entries.add(new BarEntry(0f, realIntake));

        BarDataSet set = new BarDataSet(entries, "실제 섭취량");
        set.setColor(getResources().getColor(R.color.colorPrimaryLight));
        set.setValueTextColor(Color.BLACK);
        set.setValueTextSize(10f);


        BarData d = new BarData(set);
        d.setBarWidth(0.3f);

        return d;
    }
    // ScatterData : 권장 섭취량
    private ScatterData generateScatterData(float recommendedIntake) {

        ScatterData d = new ScatterData();

        ArrayList<Entry> entries = new ArrayList<>();

        // TODO : 권장 섭취량 받아와서 넣기
        entries.add(new Entry(0f, recommendedIntake));


        ScatterDataSet set = new ScatterDataSet(entries, "권장 섭취량");
        set.setColors(getResources().getColor(R.color.colorAccent));
        set.setScatterShapeSize(20f);
        set.setScatterShape(ScatterChart.ScatterShape.CIRCLE);
        set.setDrawValues(false);
        set.setValueTextSize(10f);

        d.addDataSet(set);

        return d;
    }

}
