package com.mp.test_cv;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
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
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.Request;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.common.FirebaseMLException;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.document.FirebaseVisionDocumentText;
import com.google.firebase.ml.vision.document.FirebaseVisionDocumentTextRecognizer;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PreviewActivity extends AppCompatActivity {
    ImageView preview;
    FrameLayout frameLayout;
    TextView txt;
    Bitmap bitmap;
    Button button;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        frameLayout = findViewById(R.id.frameLayout);

        txt = findViewById(R.id.txt);

        preview = findViewById(R.id.preview);

        button = findViewById(R.id.btnRecognized);


        button.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                runCloudTextRecognition();
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
                    e.printStackTrace();
                }
            });
        }

        private void processCloudTextRecognitionResult(FirebaseVisionDocumentText text) {
            List<FirebaseVisionDocumentText.Block> blockList = text.getBlocks();
            if (blockList.size() == 0) {
                Toast.makeText(this, "No Text Found in Image", Toast.LENGTH_SHORT).show();
                return;
            } else {
                for (FirebaseVisionDocumentText.Block block : text.getBlocks()) {
                    String texts = block.getText();
                    txt.setText(texts);
                }
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
}
