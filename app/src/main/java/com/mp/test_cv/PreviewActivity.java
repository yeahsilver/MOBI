package com.mp.test_cv;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.document.FirebaseVisionDocumentText;
import com.google.firebase.ml.vision.document.FirebaseVisionDocumentTextRecognizer;

import java.io.File;
import java.util.List;

public class PreviewActivity extends AppCompatActivity {
    ImageView preview;
    FrameLayout frameLayout;
    TextView txt;
    Bitmap bitmap;
    FloatingActionButton btnSave, btnRetry;
    private Button btnHome;

    int calories = 0;
    int carbohydrate = 0;
    int protein = 0;
    int fat = 0;
    int saturFat = 0;
    int sugars = 0;
    int sodium = 0;
    int dietaryFiber = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        frameLayout = findViewById(R.id.frameLayout);

        txt = findViewById(R.id.txt);

        preview = findViewById(R.id.preview);

        btnSave = findViewById(R.id.btnSave);
        btnRetry = findViewById(R.id.btnRetry);
        btnHome = findViewById(R.id.btnHome);
        btnHome.setEnabled(false);
        btnHome.setText("텍스트 인식중...");

        // 인식 완료를 누르면 NutritionInfoActivity로 값을 전달
        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PreviewActivity.this, NutritionInfoActivity.class);
                intent.putExtra("calories", calories);
                intent.putExtra("carbohydrate", carbohydrate);
                intent.putExtra("protein", protein);
                intent.putExtra("fat", fat);
                intent.putExtra("saturFat", saturFat);
                intent.putExtra("sugars", sugars);
                intent.putExtra("sodium", sodium);
                intent.putExtra("dietaryFiber", dietaryFiber);
                startActivity(intent);
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runCloudTextRecognition();
            }
        });

        btnRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PreviewActivity.this, CameraView.class);
                startActivity(intent);
            }
        });

        Glide.with(this).asBitmap()
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .load(GetFileFromInternal())
                .into(preview);
    }

        private void runCloudTextRecognition(){
            bitmap = GetBitmapFromInternal();

            FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmap);

            FirebaseVisionDocumentTextRecognizer recognizer = FirebaseVision.getInstance().getCloudDocumentTextRecognizer();

            recognizer.processImage(image).addOnSuccessListener(new OnSuccessListener<FirebaseVisionDocumentText>() {
                @Override
                public void onSuccess(FirebaseVisionDocumentText firebaseVisionDocumentText) {
                    processCloudTextRecognitionResult(firebaseVisionDocumentText);
                }
            }).addOnFailureListener(new OnFailureListener() {

                @Override
                public void onFailure(@NonNull Exception e) {
                    failRecognize();
                    e.printStackTrace();
                }
            });
        }

        private void failRecognize(){
            Toast.makeText(this, "fail", Toast.LENGTH_SHORT).show();
        }

        private void processCloudTextRecognitionResult(FirebaseVisionDocumentText text) {
            List<FirebaseVisionDocumentText.Block> blockList = text.getBlocks();
            if (blockList.size() == 0) {
                Toast.makeText(this, "No Text Found in Image", Toast.LENGTH_SHORT).show();
                return;
            } else {
                String fullTexts = "";
                fullTexts = text.getText();
                //txt.setText(fullTexts);
                onPostExecute(fullTexts);
                /*
                for (FirebaseVisionDocumentText.Block block : text.getBlocks()) {
                    String texts = block.getText();
                    //txt.setText(texts);
                }
                 */
            }
        }

        private Bitmap GetBitmapFromInternal(){
            File storage = getFilesDir();
            File tempFile = new File(storage,"temp.png");
            return BitmapFactory.decodeFile(tempFile.getAbsolutePath());
        }

        private File GetFileFromInternal(){
            return new File(getFilesDir(),"temp.png");
        }

        protected void onPostExecute(String result) {
            Log.d("onPostExecute", result);

            // tockenizing start
            String[] texts = result.split("\n");
            String nutrition;


            for (int i = 0; i < texts.length; i++) {
                texts[i] = texts[i].trim();

                if (texts[i].contains("Calories")) {
                    calories = tokenizing(texts[i], "Calories");
                } else if (texts[i].contains("Total Carbohydrate")) {
                    carbohydrate = tokenizing(texts[i], "Total Carbohydrate");
                } else if (texts[i].contains("Protein")) {
                    protein = tokenizing(texts[i], "Protein");
                } else if (texts[i].contains("Total Fat")) {
                    fat = tokenizing(texts[i], "Total Fat");
                } else if (texts[i].contains("Saturated Fat")) {
                    saturFat = tokenizing(texts[i], "Saturated Fat");
                } else if (texts[i].contains("Total Sugars")) {
                    sugars = tokenizing(texts[i], "Total Sugars");
                } else if (texts[i].contains("Sodium")) {
                    sodium = tokenizing(texts[i], "Sodium");
                } else if (texts[i].contains("Dietary Fiber")) {
                    dietaryFiber = tokenizing(texts[i], "Dietary Fiber");
                }

            }

            // 결과를 화면에 띄어줌
            txt.setText("칼로리(Calories) : " + calories +
                    "\n탄수화물(Total Carbohydrate) : " + carbohydrate +
                    "\n단백질(Protein) : " + protein +
                    "\n지방(Total Fat) : " + fat +
                    "\n포화지방(Saturated Fat) : " + saturFat +
                    "\n당류(Total Sugars) : " + sugars +
                    "\n나트륨(Sodium) : " + sodium +
                    "\n식이섬유(Dietary Fiber) : " + dietaryFiber);


            /*
            System.out.println("Calories : " + calories);
            System.out.println("Total Fat : " + fat);
            System.out.println("Trans Fat : " + transFat);
            System.out.println("Total Carbohydrate : " + carbohydrate);
            System.out.println("Dietary Fiber : " + dietaryFiber);
            System.out.println("Total Sugars : " + sugars);
            */

            // tockenizing --
            //txt.setText(result);
            //Toast.makeText(PreviewActivity.this, "" + result, Toast.LENGTH_LONG).show();

            btnHome.setEnabled(true);
            btnHome.setText("인식 완료");
            //button.setEnabled(true);
            //button.setText("텍스트 인식");
        }

        // tockenizing --
        protected int tokenizing(String oneline, String nutrition) {
            System.out.println("tokenizing : " + oneline);
            String[] tokens;
            int intake = 0;

            if (oneline.contains(nutrition)) {
                System.out.println("**" + oneline);
                tokens = oneline.split(nutrition);

                for (int j = 0; j < tokens.length; j++) {
                    tokens[j] = tokens[j].trim();
                    System.out.println("--" + tokens[j]);


                    if (tokens[j].length() < 1) continue;
                    if (tokens[j].charAt(0) >= '0' && tokens[j].charAt(0) <= '9') {
                        tokens = tokens[j].split(" ");
                        System.out.println("++" + tokens);

                        if (tokens[0].endsWith("mg")) {
                            intake = Integer.parseInt(tokens[0].split("mg")[0]);
                        } else if (tokens[0].endsWith("g")) {
                            intake = Integer.parseInt(tokens[0].split("g")[0]);
                        } else if (tokens[0].endsWith("9")) {
                            intake = Integer.parseInt(tokens[0].split("9")[0]);

                        } else {
                            intake = Integer.parseInt(tokens[0]);
                        }

                    }

                }
            }
            return intake;
        }
        // tockenizing end
}
