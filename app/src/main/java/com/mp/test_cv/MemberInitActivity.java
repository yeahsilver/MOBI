package com.mp.test_cv;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class MemberInitActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private Spinner spinner;
    private Spinner gender_spinner;
    private double activityMeasure;
    private int gender; //0 = female, 1 = male.
    private static final String TAG = "MemberInitActivity";
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_init);

        mAuth = FirebaseAuth.getInstance();
        findViewById(R.id.checkButton).setOnClickListener(onClickListener);
        spinner = (Spinner)findViewById(R.id.activitySpinner);
        gender_spinner = (Spinner)findViewById(R.id.genderSpinner);
        //input array data
        final ArrayList<String> list = new ArrayList<>();
        list.add("주로 앉아서 생활함");
        list.add("보통의 활동량을 가짐");
        list.add("활발한 활동량을 가짐");
        list.add("몸으로 하는 활동이 많음");
       // String a = list.get(0);
        final ArrayList<String> list_gender = new ArrayList<>();
        list_gender.add("female");
        list_gender.add("male");

        ArrayAdapter spinnerAdapter;
        spinnerAdapter = new ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, list);
        spinner.setAdapter(spinnerAdapter);
        //event listener
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch(position) { // female 기준
                    case 0:
                        activityMeasure = 1.0;
                        break;
                    case 1:
                        activityMeasure = 1.12;
                        break;
                    case 2:
                        activityMeasure = 1.27;
                        break;
                    case 3:
                        activityMeasure = 1.45;
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                activityMeasure = 1.0;
                startToast("activity set default value(: 1.0)");
            }
        });

        ArrayAdapter spinnerAdapter_;
        spinnerAdapter_ = new ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, list_gender);
        gender_spinner.setAdapter(spinnerAdapter_);
        //event listener
        gender_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch(position) {
                    case 0:
                        gender = 0; //female
                        break;
                    case 1:
                        gender = 1; //male
                        if (activityMeasure == 1.12) { activityMeasure -= 1; }
                        else if (activityMeasure == 1.27) { activityMeasure -= 2; }
                        else if (activityMeasure == 1.45) { activityMeasure += 3; }
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                gender = 0; //female
                startToast("default value female(0)");
            }
        });
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.checkButton:
                    profileUpdate();
                    break;
            }
        }
    };

    private void profileUpdate() {
        float height = Float.parseFloat(((EditText)findViewById(R.id.heightEditText)).getText().toString());
        float weight = Float.parseFloat(((EditText)findViewById(R.id.weightEditText)).getText().toString());
        int age = Integer.parseInt(((EditText)findViewById(R.id.ageEditText)).getText().toString());

        int tmpCalories = 0;
        int recCalories = 0;

        switch (gender) {
            case 0:
                // female
                // 354 - (6.91 * age) + PA[9.36 * weight(kg) + 726 * height(m)]
                tmpCalories = (int)(354 - (6.91 * age) + (activityMeasure * ((9.36 * weight) + (726 * (height * 0.01)))));
                recCalories = (tmpCalories / 100) * 100;
                break;
            case 1:
                //male
                // 662 - (9.53 * age) + PA[15.91 * weight(kg) + 539.6 * height(m)]
                tmpCalories = (int)(662 - (9.53 * age) + (activityMeasure * ((15.91 * weight) + (539.6 * (height * 0.01)))));
                recCalories = (tmpCalories / 100) * 100;
                break;
        }
        int recCarbohydrate = (int)(recCalories * 0.65); // 55 ~ 70 %
        int recProtein = (int)(recCalories * 0.15); // 7 ~ 20 %
        int recFat = (int)(recCalories * 0.2); // 15 ~ 25 %

        if (height > 0 && weight > 0 && age > 0 && gender >= 0 && activityMeasure > 0) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            MemberInfo memberInfo = new MemberInfo(height, weight, age, activityMeasure, gender);
           if (user != null){
               // RecDailyIntake 생성
               RecDailyIntake recDailyIntake = new RecDailyIntake(recCalories, recCarbohydrate, recProtein, recFat, 1, 1, 1, 1);

               db.collection("User").document(user.getUid())
                       .set(memberInfo)
                       .addOnSuccessListener(new OnSuccessListener<Void>() {
                           @Override
                           public void onSuccess(Void aVoid) {
                               startToast("정보 입력에 성공하였습니다.");
                               finish();
                           }
                       })
                       .addOnFailureListener(new OnFailureListener() {
                           @Override
                           public void onFailure(@NonNull Exception e) {
                               startToast("정보 입력에 실패했습니다..");
                               Log.w(TAG, "Error writing document", e);
                           }
                       });
               // RecDailyIntake 컬렉션 생성
               db.collection("User").document(user.getUid())
               .collection("RecDailyIntake").document(user.getUid())
                       .set(recDailyIntake)
                       .addOnSuccessListener(new OnSuccessListener<Void>() {
                           @Override
                           public void onSuccess(Void aVoid) {
                               finish();
                           }
                       })
                       .addOnFailureListener(new OnFailureListener() {
                           @Override
                           public void onFailure(@NonNull Exception e) {
                               startToast("정보 입력에 실패했습니다..");
                               Log.w(TAG, "Error writing document", e);
                           }
                       });

           }
        }
        else {
            startToast("사용자정보를 입력하세요.");
        }
    }
    private void startToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
    private void myStartActivity(Class c) {
        Intent intent = new Intent(this, c);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //로그인 한 상태에서 뒤로가기 눌렀을 때 메인액티비로 이동, 나머지 스택 없어짐.
        startActivity(intent);
    }
}
