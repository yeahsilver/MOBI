package com.mp.test_cv;

import android.app.AlertDialog;
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
import androidx.fragment.app.DialogFragment;

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

public class NutritionInfoActivity extends AppCompatActivity implements CheckDialog.CheckDialogListener  {
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
    boolean dialogCheck = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nutritioninfo);

        //FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        findViewById(R.id.scan).setOnClickListener(onClickListener);
        findViewById(R.id.submit).setOnClickListener(onClickListener);

        //Intent 가져오기
        Intent intent = getIntent();
        EditText editCalorie = (EditText) findViewById(R.id.editCalorie);
        EditText editCarbo = (EditText) findViewById(R.id.editCarbo);
        EditText editProtein = (EditText) findViewById(R.id.editProtein);
        EditText editFat = (EditText) findViewById(R.id.editFat);
        EditText editSaturFat = (EditText) findViewById(R.id.editSaturFat);
        EditText editSugar = (EditText) findViewById(R.id.editSugar);
        EditText editSodium = (EditText) findViewById(R.id.editSodium);
        EditText editFiber = (EditText) findViewById(R.id.editFiber);

        // Intent의 값이 없으면 0으로 설정
        int calories = intent.getIntExtra("calories",0);
        int carbohydrate = intent.getIntExtra("carbohydrate",0);
        int protein = intent.getIntExtra("protein",0);
        int fat = intent.getIntExtra("fat",0);
        int saturFat = intent.getIntExtra("saturFat",0);
        int sugars = intent.getIntExtra("sugars",0);
        int sodium = intent.getIntExtra("sodium",0);
        int fiber = intent.getIntExtra("dietaryFiber",0);

        editCalorie.setText(Integer.toString(calories));
        editCarbo.setText(Integer.toString(carbohydrate));
        editProtein.setText(Integer.toString(protein));
        editFat.setText(Integer.toString(fat));
        editSaturFat.setText(Integer.toString(saturFat));
        editSugar.setText(Integer.toString(sugars));
        editSodium.setText(Integer.toString(sodium));
        editFiber.setText(Integer.toString(fiber));
    }
   View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.scan:
                    myStartActivity(CameraView.class);
                    break;
                case R.id.submit:
                    showCheckDialog();
                    break;
            }
        }
    };
   /* private void setUpDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.dialog_message)
                .setTitle(R.string.dialog_title);
        AlertDialog dialog = builder.create();
    }*/
    private void startToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
    private void myStartActivity(Class c) {
        Intent intent = new Intent(this, c);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //로그인 한 상태에서 뒤로가기 눌렀을 때 메인액티비로 이동, 나머지 스택 없어짐.
        startActivity(intent);
    }
    public void showCheckDialog() {
        calories = Integer.valueOf(((EditText) findViewById(R.id.editCalorie)).getText().toString());
        carbohydrate = Integer.valueOf(((EditText) findViewById(R.id.editCarbo)).getText().toString());
        protein = Integer.valueOf(((EditText) findViewById(R.id.editProtein)).getText().toString());
        fat = Integer.valueOf(((EditText) findViewById(R.id.editFat)).getText().toString());
        saturatedFat = Integer.valueOf(((EditText) findViewById(R.id.editSaturFat)).getText().toString());
        sugar = Integer.valueOf(((EditText) findViewById(R.id.editSugar)).getText().toString());
        sodium = Integer.valueOf(((EditText) findViewById(R.id.editSodium)).getText().toString());
        dietaryFiber = Integer.valueOf(((EditText) findViewById(R.id.editFiber)).getText().toString());
        inputIntake = new PersonalDailyIntake(calories, carbohydrate, protein, fat, saturatedFat, sugar, sodium, dietaryFiber);
        inputTotal = new TotalDailyIntake(calories, carbohydrate, protein, fat, saturatedFat, sugar, sodium, dietaryFiber);
        // Create an instance of the dialog fragment and show it
        DialogFragment dialog = new CheckDialog();
        Bundle scanInfo = new Bundle();
        makeBundles(scanInfo);
        dialog.setArguments(scanInfo);
        dialog.show(getSupportFragmentManager(), "CheckDialogFragment");
    }
    public void sendData() {
        if(dialogCheck) {
            //사용자 정보 db 받고,inputIntake로 해당 정보들 갱신
            //모든 정보 갱신 과정이 끝나면 mainActivity로 간다. (ui업데이트)
            if (carbohydrate >= 0 && protein >= 0 && fat >= 0 && saturatedFat >= 0 && sugar >= 0 && sodium >= 0 && dietaryFiber >= 0) {
                user = FirebaseAuth.getInstance().getCurrentUser();
                db = FirebaseFirestore.getInstance();

                Date today = new Date();
                SimpleDateFormat timeFormat = new SimpleDateFormat("yyyyMMddhhmmss");
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
                String time = timeFormat.format(today);
                date = dateFormat.format(today);
                DocumentReference totalDB = db.collection("TotalDailyIntake").document(date);
                Log.d(TAG, "totalDB error : " + totalDB.equals(date));

                if (user != null) {
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
                            if (docRefMap == null) {
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
                            } else {
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
            } else {
                startToast("사용자정보를 입력하세요.");
            }
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            //로그인 한 상태에서 뒤로가기 눌렀을 때 메인액티비로 이동, 나머지 스택 없어짐.
            startActivity(intent);
        }
    }
    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        dialogCheck = true;
        sendData();
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        dialogCheck = true;
    }
    private void makeBundles(Bundle arg) {
        arg.putString("calories", Integer.toString(calories));
        arg.putString("fat", Integer.toString(fat));
        arg.putString("carbohydrate", Integer.toString(carbohydrate));
        arg.putString("protein", Integer.toString(protein));
        arg.putString("saturFat", Integer.toString(saturatedFat));
        arg.putString("sugar", Integer.toString(sugar));
        arg.putString("dietaryFiber", Integer.toString(dietaryFiber));
        arg.putString("sodium", Integer.toString(sodium));
    }
}