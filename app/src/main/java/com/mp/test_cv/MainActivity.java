package com.mp.test_cv;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.media.Image;
import android.media.ImageReader;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.ScatterChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.BubbleData;
import com.github.mikephil.charting.data.BubbleDataSet;
import com.github.mikephil.charting.data.BubbleEntry;
import com.github.mikephil.charting.data.CandleData;
import com.github.mikephil.charting.data.CandleDataSet;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.ScatterData;
import com.github.mikephil.charting.data.ScatterDataSet;
import com.github.mikephil.charting.interfaces.datasets.IDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.w3c.dom.Text;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.github.mikephil.charting.charts.CombinedChart;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    FirebaseUser user;
    FirebaseFirestore db;
    String date;
    Map<String, Integer> totNutritionMap = new HashMap<String, Integer>(); //Nutrition과 섭취량을 매핑할 변수 생성
    Map<String, Integer> recNutritionMap = new HashMap<String, Integer>();

    private CombinedChart chart;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        user = FirebaseAuth.getInstance().getCurrentUser();
        Date today = new Date();
        SimpleDateFormat timeFormat = new SimpleDateFormat("yyyyMMddhhmmss");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        String time = timeFormat.format(today);
        date = dateFormat.format(today);

        findViewById(R.id.logoutButton).setOnClickListener(onClickListener);
        findViewById(R.id.NutritionInfoButton).setOnClickListener(onClickListener);
        if (user != null){
            // DailyIntake 생성
            final DocumentReference docRef = db.collection("User").document(user.getUid());
            docRef.collection("TotalDailyIntake").document(date).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    TotalDailyIntake totalDailyIntake = documentSnapshot.toObject(TotalDailyIntake.class);
                    if (totalDailyIntake == null) {
                        //totalDailyintake초기화
                        totalDailyIntake = new TotalDailyIntake(0,0,0,0,0,0,0,0);
                        /*docRef.set(totalDailyIntake)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        startToast("정보 입력에 성공했습니다..");
                                        finish();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        startToast("정보 입력에 실패했습니다..");
                                        Log.w(TAG, "Error writing document", e);
                                    }
                                });*/
                        startToast("총섭취량 데이터 zero 초기화.");

                    }
                    totNutritionMap.put("calories", totalDailyIntake.gettotalCalories());
                    totNutritionMap.put("carbohydrate", totalDailyIntake.gettotalCarbohydrate());
                    totNutritionMap.put("protein", totalDailyIntake.gettotalProtein());
                    totNutritionMap.put("fat", totalDailyIntake.gettotalFat());
                    totNutritionMap.put("saturatedFat", totalDailyIntake.gettotalSaturatedFat());
                    totNutritionMap.put("sugar", totalDailyIntake.gettotalSugar());
                    totNutritionMap.put("sodium", totalDailyIntake.gettotalSodium());
                    totNutritionMap.put("dietaryfiber", totalDailyIntake.gettotalDietaryFiber());
                    //후에 get으로 성분별 데이터 가져올 수 있음.
                }
            });
            docRef.collection("RecDailyIntake").document(user.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    RecDailyIntake recDailyIntake = documentSnapshot.toObject(RecDailyIntake.class);
                    if (recDailyIntake == null) {
                        //totalDailyintake초기화
                        startToast("사용자 데이터가 없습니다.");
                    }
                    else {
                        recNutritionMap.put("calories", recDailyIntake.getrecCalories());
                        recNutritionMap.put("carbohydrate", recDailyIntake.getrecCarbohydrate());
                        recNutritionMap.put("protein", recDailyIntake.getrecProtein());
                        recNutritionMap.put("fat", recDailyIntake.getrecFat());
                        //recNutritionMap.put("saturatedFat", recDailyIntake.getrecSaturatedFat());
                        //recNutritionMap.put("sugar", recDailyIntake.getrecSugar());
                        //recNutritionMap.put("sodium", recDailyIntake.getrecSodium());
                        //recNutritionMap.put("dietaryfiber", recDailyIntake.getrecDietaryFiber());
                        //후에 get으로 성분별 데이터 가져올 수 있음.
                    }
                }
            });
        }
        else {
            startToast("사용자정보를 입력하세요.");
        }
        //totlDailyIntake data에서 totalCalories / recDatilyIntake에서 recCalories
        int totalCal;
        int recCal;
        int caloriePercent;
        // TODO : 각종 수치들 변수로 바꾸기
        // 칼로리 내역 표시
        TextView textCaloriePercent = (TextView) findViewById(R.id.textCaloriePercent);
        TextView textRealCalorie = (TextView) findViewById(R.id.textRealCalorie);
        TextView textRecommendedCalorie = (TextView) findViewById(R.id.textRecommendedCalorie);
        textCaloriePercent.setText("오늘 권장칼로리의 70%를\n섭취하셨습니다.");
        textRealCalorie.setText("총 섭취 칼로리 : 1400kcal");
        textRecommendedCalorie.setText("권장 섭취 칼로리 : 2000kcal");

        // 탄수화물 섭취 내역
        updateChartActivity(R.id.carboChart);   // 탄수화물 섭취 현황 차트
        TextView textCarbo = (TextView) findViewById(R.id.textCarbo);
        textCarbo.setText("탄수화물\n오늘 권장탄수화물의 120%를\n섭취하셨습니다.\n권장섭취량 : 100g\n실제섭취량 : 120g\n");
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
    private void updateChartActivity(int id){
        chart = findViewById(id);

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
        leftAxis.setAxisMaximum(200f);

        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setEnabled(false);


        // 그래프 그리기
        CombinedData data = new CombinedData();
        data.setData(generateBarData(120f));
        data.setData(generateScatterData(100f));

        chart.setData(data);
        chart.invalidate();
    }

    // BarData : 실제 섭취량
    private BarData generateBarData(float realIntake) {

        ArrayList<BarEntry> entries = new ArrayList<>();

       // TODO : 실제 섭취량 받아와서 넣기
        entries.add(new BarEntry(0f ,realIntake));

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
        entries.add(new Entry(0f,recommendedIntake));


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
