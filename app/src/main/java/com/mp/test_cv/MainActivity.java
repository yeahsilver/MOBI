package com.mp.test_cv;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
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
import android.widget.ImageView;
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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.googlecode.tesseract.android.TessBaseAPI;

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

import com.github.mikephil.charting.charts.CombinedChart;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";


    private CombinedChart chart;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnInfo = (Button) findViewById(R.id.info);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if(user == null) {
            myStartActivity(SignUpActivity.class);
        }else {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference docRef = db.collection("users").document(user.getUid());
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null) {
                            if (document.exists()) {
                                Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                            } else {
                                Log.d(TAG, "No such document");
                                myStartActivity(MemberInitActivity.class);
                            }
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                    }
                }
            });
        }
        findViewById(R.id.logoutButton).setOnClickListener(onClickListener);

        btnInfo.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), NutritionInfo.class);
                startActivity(intent);
            }
        });


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
            }
        }
    };


    private void myStartActivity(Class c) {
        Intent intent = new Intent(this, c);
        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //로그인 한 상태에서 뒤로가기 눌렀을 때 메인액티비로 이동, 나머지 스택 없어짐.
        startActivity(intent);
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
