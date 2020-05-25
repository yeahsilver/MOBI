package com.mp.test_cv;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.FieldValue;

import org.opencv.android.CameraActivity;

public class NutritionInfoActivity extends AppCompatActivity {
    final String TAG = getClass().getSimpleName();
    PersonalDailyIntake inputIntake;
    TotalDailyIntake inputTotal;
    int calories, carbohydrate, protein, fat, saturatedFat, sugar, sodium, dietaryFiber;
    Map<String, Object> docRefMap = new HashMap<String, Object> ();
    FirebaseUser user;
    FirebaseFirestore db;
    String date;
    boolean getUser = false;
    boolean getDocument = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nutritioninfo);

        //FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        findViewById(R.id.scan).setOnClickListener(onClickListener);
        findViewById(R.id.submit).setOnClickListener(onClickListener);
    }
    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.scan:
                    myStartActivity(CameraView.class);
                    break;
                case R.id.submit:
                    calories = Integer.valueOf(((EditText)findViewById(R.id.editCalorie)).getText().toString());
                    carbohydrate = Integer.valueOf(((EditText)findViewById(R.id.editCarbo)).getText().toString());
                    protein = Integer.valueOf(((EditText)findViewById(R.id.editProtein)).getText().toString());
                    fat = Integer.valueOf(((EditText)findViewById(R.id.editFat)).getText().toString());
                    saturatedFat = Integer.valueOf(((EditText)findViewById(R.id.editSaturFat)).getText().toString());
                    sugar = Integer.valueOf(((EditText)findViewById(R.id.editSugar)).getText().toString());
                    sodium = Integer.valueOf(((EditText)findViewById(R.id.editSodium)).getText().toString());
                    dietaryFiber = Integer.valueOf(((EditText)findViewById(R.id.editFiber)).getText().toString());
                    inputIntake = new PersonalDailyIntake(calories, carbohydrate, protein, fat, saturatedFat, sugar, sodium, dietaryFiber);
                    inputTotal = new TotalDailyIntake(calories, carbohydrate, protein, fat, saturatedFat, sugar, sodium, dietaryFiber);
                    //사용자 정보 db 받고,inputIntake로 해당 정보들 갱신
                    //모든 정보 갱신 과정이 끝나면 mainActivity로 간다. (ui업데이트)
                    if (carbohydrate >= 0 && protein >= 0 && fat >= 0 && saturatedFat >= 0 && sugar >= 0 && sodium >= 0 && dietaryFiber >= 0 ) {
                        user = FirebaseAuth.getInstance().getCurrentUser();
                        db = FirebaseFirestore.getInstance();

                        Date today = new Date();
                        SimpleDateFormat timeFormat = new SimpleDateFormat("yyyyMMddhhmmss");
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
                        String time = timeFormat.format(today);
                        date = dateFormat.format(today);
                        DocumentReference totalDB = db.collection("TotalDailyIntake").document(date);
                        Log.d(TAG, "totalDB error : "+totalDB.equals(date));

                        if (user != null){
                            // DailyIntake 생성
                            db.collection("User").document(user.getUid())
                                    .collection("DailyIntake").document(time)
                                    .set(inputIntake)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            startToast("정보 입력에 성공했습니다..");
                                            getUser = true;
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

                            DocumentReference docRef = db.collection("User").document(user.getUid()).collection("TotalDailyIntake").document(date);
                            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot document = task.getResult();
                                        Log.d(TAG, "Document: " + document);
                                        docRefMap = document.getData();
                                        if (document.exists()) {
                                            Log.d(TAG, "docRefMap: " + docRefMap);

                                        } else {
                                            Log.d(TAG, "docRefMap: " + docRefMap);
                                            Log.d(TAG, "No such document");
                                        }
                                    } else {
                                        Log.d(TAG, "get failed with ", task.getException());
                                    }
                                    // TotalDailyIntake 생성
                                    if(docRefMap == null){
                                        db.collection("User").document(user.getUid())
                                                .collection("TotalDailyIntake").document(date)
                                                .set(inputTotal)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        startToast("정보를 초기화했습니다.");
                                                        getDocument = true;
                                                        finish();
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        startToast("정보 초기화를 실패했습니다.");
                                                        Log.w(TAG, "Error writing document", e);
                                                    }
                                                });
                                    }else{
                                        // TotalDailyIntake Increment
                                        DocumentReference tdb = db.collection("User").document(user.getUid())
                                                .collection("TotalDailyIntake").document(date);
                                        tdb.update("totalCalories", FieldValue.increment(calories));
                                        tdb.update("totalCarbohydrate", FieldValue.increment(carbohydrate));
                                        tdb.update("totalDietaryFiber", FieldValue.increment(dietaryFiber));
                                        tdb.update("totalFat", FieldValue.increment(protein));
                                        tdb.update("totalProtein", FieldValue.increment(fat));
                                        tdb.update("totalSaturatedFat", FieldValue.increment(saturatedFat));
                                        tdb.update("totalSodium", FieldValue.increment(sugar));
                                        tdb.update("totalSugar", FieldValue.increment(sodium));
                                        getDocument = true;
                                    }
                                }
                            });
                        }
                    }
                    else {
                        startToast("사용자정보를 입력하세요.");
                    }
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class );
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    //로그인 한 상태에서 뒤로가기 눌렀을 때 메인액티비로 이동, 나머지 스택 없어짐.
                    startActivity(intent);
            }
        }
    };
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