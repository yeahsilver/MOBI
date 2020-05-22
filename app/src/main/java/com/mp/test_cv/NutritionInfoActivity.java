package com.mp.test_cv;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.opencv.android.CameraActivity;

public class NutritionInfoActivity extends AppCompatActivity {
    final String TAG = getClass().getSimpleName();
    PersonalDailyIntake inputIntake;
    int calories, carbohydrate, protein, fat, saturatedFat, sugar, sodium, dietaryFiber;
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
                    //calories = Integer.valueOf(((EditText)findViewById(R.id.editCal)).getText().toString());
                    carbohydrate = Integer.valueOf(((EditText)findViewById(R.id.editCarbo)).getText().toString());
                    protein = Integer.valueOf(((EditText)findViewById(R.id.editProtein)).getText().toString());
                    fat = Integer.valueOf(((EditText)findViewById(R.id.editFat)).getText().toString());
                    saturatedFat = Integer.valueOf(((EditText)findViewById(R.id.editSaturFat)).getText().toString());
                    sugar = Integer.valueOf(((EditText)findViewById(R.id.editSugar)).getText().toString());
                    sodium = Integer.valueOf(((EditText)findViewById(R.id.editSodium)).getText().toString());
                    dietaryFiber = Integer.valueOf(((EditText)findViewById(R.id.editFiber)).getText().toString());
                    inputIntake = new PersonalDailyIntake(calories, carbohydrate, protein, fat, saturatedFat, sugar, sodium, dietaryFiber);
                    //사용자 정보 db 받고,inputIntake로 해당 정보들 갱신
                    //모든 정보 갱신 과정이 끝나면 mainActivity로 간다. (ui업데이트)
                    if (carbohydrate >= 0 && protein >= 0 && fat >= 0 && saturatedFat >= 0 && sugar >= 0 && sodium >= 0 && dietaryFiber >= 0 ) {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                   if (user != null){
                       // DailyIntake 생성
                       db.collection("User").document(user.getUid())
                               .collection("DailyIntake").document(user.getUid())
                               .set(inputIntake)
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
                               });
                       }
                    }
                    else {
                        startToast("사용자정보를 입력하세요.");
                    }
                    myStartActivity(MainActivity.class);
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
