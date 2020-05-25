package com.mp.test_cv;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.CombinedChart;
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

import java.util.ArrayList;
import java.util.Map;

class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    private String[] nutritions;
    private Map<String, Integer> totNutritionMap;
    private Map<String, Integer> recNutritionMap;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // 리스트에 들어가는 한 요소
        public CombinedChart chart;
        public TextView textViewTitle;
        public TextView textView;
        public MyViewHolder(LinearLayout v) {
            super(v);
            chart = v.findViewById(R.id.nutritionChart);
            textViewTitle = v.findViewById(R.id.nutritionTitleText);
            textView = v.findViewById(R.id.nutritionText);
        }
    }

    // myDataset : 각 줄 마다 보여줄 내용을 가진다
    // Provide a suitable constructor (depends on the kind of dataset)
    public MyAdapter(Map<String, Integer> totNutritionMap, Map<String, Integer> recNutritionMap) {
        this.totNutritionMap = totNutritionMap;
        this.recNutritionMap = recNutritionMap;
        nutritions = totNutritionMap.keySet().toArray(new String[0]);
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MyAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                     int viewType) {
        // 리스트의 한 요소에 들어갈 내용
        LinearLayout v = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.today_nutrition,parent, false);
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyViewHolder holder, int i) {
        String nutrition = nutritions[i];
        int totNutrition;
        int recNutrition;
        int ratioNutrition;

        if(totNutritionMap.get(nutrition) != null){
            totNutrition = totNutritionMap.get(nutrition);
        }
        else{
            totNutrition = 0;
        }

        if(recNutritionMap.get(nutrition) != null){
            recNutrition = recNutritionMap.get(nutrition);
            ratioNutrition = (totNutrition * 100) / recNutrition;
        }
        else{
            recNutrition = 0;
            ratioNutrition = 0;
        }

        // 텍스트

        switch(nutritions[i]){
            case "carbohydrate" :
                nutrition = "탄수화물";
                break;

            case "protein" :
                nutrition = "단백질";
                break;

            case "fat" :
                nutrition = "지방";
                break;

            case "saturatedFat" :
                nutrition = "포화지방";
                break;

            case "sugar" :
                nutrition = "당류";
                break;

            case "sodium" :
                nutrition = "나트륨";
                break;

            case "dietaryfiber" :
                nutrition = "식이섬유";
                break;
        }

        holder.textViewTitle.setText(nutrition);
        holder.textView.setText("오늘 권장섭취량의 "
                + ratioNutrition
                + "%를\n섭취하셨습니다.\n권장섭취량 : " + recNutrition +
                "g\n실제섭취량 : " + totNutrition + "g\n");

        // 차트
        holder.chart.getDescription().setEnabled(false);
        holder.chart.setBackgroundColor(Color.WHITE);
        holder.chart.setDrawGridBackground(false);
        holder.chart.setDrawBarShadow(false);
        holder.chart.setHighlightFullBarEnabled(false);

        // draw bars behind scatter
        holder.chart.setDrawOrder(new CombinedChart.DrawOrder[]{
                CombinedChart.DrawOrder.BAR, CombinedChart.DrawOrder.SCATTER
        });


        // 비활성화
        holder.chart.setTouchEnabled(false);
        holder.chart.setDragEnabled(false);
        holder.chart.setScaleEnabled(false);
        holder.chart.setPinchZoom(false);

        // 범례
        Legend l = holder.chart.getLegend();
        l.setWordWrapEnabled(true);
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);

        // x축 : 영양소
        XAxis xAxis = holder.chart.getXAxis();
        xAxis.setEnabled(false);

        // y축 : 섭취량
        YAxis leftAxis = holder.chart.getAxisLeft();
        leftAxis.setEnabled(true);
        leftAxis.setDrawGridLines(true);
        leftAxis.setDrawAxisLine(true);

        // TODO : min, max 세팅 영양소별로 다르게 해줘야함
        leftAxis.setAxisMinimum(0f);
        leftAxis.setAxisMaximum(1000f);

        YAxis rightAxis = holder.chart.getAxisRight();
        rightAxis.setEnabled(false);


        // 그래프 그리기
        CombinedData data = new CombinedData();
        data.setData(generateBarData(totNutrition));
        data.setData(generateScatterData(recNutrition));

        holder.chart.setData(data);
        holder.chart.invalidate();

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return nutritions.length;
    }

    // BarData : 실제 섭취량
    private BarData generateBarData(float totNutrition) {

        ArrayList<BarEntry> entries = new ArrayList<>();

        // TODO : 실제 섭취량 받아와서 넣기
        entries.add(new BarEntry(0f, totNutrition));

        BarDataSet set = new BarDataSet(entries, "실제 섭취량");
        set.setColor(0xFFC9B3FF);
        set.setValueTextColor(Color.BLACK);
        set.setValueTextSize(10f);


        BarData d = new BarData(set);
        d.setBarWidth(0.3f);

        return d;
    }
    // ScatterData : 권장 섭취량
    private ScatterData generateScatterData(float recNutrition) {

        ScatterData d = new ScatterData();

        ArrayList<Entry> entries = new ArrayList<>();

        // TODO : 권장 섭취량 받아와서 넣기
        entries.add(new Entry(0f, recNutrition));


        ScatterDataSet set = new ScatterDataSet(entries, "권장 섭취량");
        set.setColors(0xFF80FFFF);
        set.setScatterShapeSize(20f);
        set.setScatterShape(ScatterChart.ScatterShape.CIRCLE);
        set.setDrawValues(false);
        set.setValueTextSize(10f);

        d.addDataSet(set);

        return d;
    }
}
