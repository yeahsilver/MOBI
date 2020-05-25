package com.mp.test_cv;

import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.hardware.camera2.CameraDevice;
import android.media.ImageReader;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.googlecode.tesseract.android.TessBaseAPI;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class OCRScanActivity extends AppCompatActivity {
    private Button button;
    private TextView OCRTextView;
    static TessBaseAPI tessBaseAPI;
    CameraSurfaceView surfaceView;
    ImageView imageView;
    private String dataPath = "";
    private String lang = "";

    // private int ACTIVITY_REQUEST_CODE = 1;

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }
    protected CameraDevice cameraDevice;
    private ImageReader imageReader;
    Bitmap imgBase;
    Bitmap roi;
    private static final String TAG = "MAINACTIVITY";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ocrscan);

       // if(FirebaseAuth.getInstance().getCurrentUser() == null) {


        //뷰 선언
        imageView = findViewById(R.id.imageView);
        surfaceView = findViewById(R.id.surfaceView);
        OCRTextView = findViewById(R.id.textView);
        button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                capture();
            }
        });
        //캡처하면 이미지 처리를 capture()함수로 한다.

        tessBaseAPI = new TessBaseAPI();
        String dir = getFilesDir()+"/tesseract";
        if(checkLanguageFile(dir+"/tessdata")){
            tessBaseAPI.init("/tessdata","eng");
        } else {
            Toast.makeText(this, dir + " isn't found", Toast.LENGTH_SHORT).show();
        }

        // Example of a call to a native method
        //processImage(BitmapFactory.decodeResource(getResources(), R.drawable.nutrition_facts));
    }

    boolean checkLanguageFile(String dir)
    {
        File file = new File(dir);
        if(!file.exists() && file.mkdirs())
            createFiles(dir);
        else if(file.exists()){
            String filePath = dir + "/eng.traineddata";
            File langDataFile = new File(filePath);
            if(!langDataFile.exists())
                createFiles(dir);
        }
        return true;
    }

    private void createFiles(String dir)
    {
        AssetManager assetMgr = this.getAssets();

        InputStream inputStream = null;
        OutputStream outputStream = null;

        try {
            inputStream = assetMgr.open("eng.traineddata");

            String destFile = dir + "/eng.traineddata";

            outputStream = new FileOutputStream(destFile);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, read);
            }
            inputStream.close();
            outputStream.flush();
            outputStream.close();
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void capture()
    {
        surfaceView.capture(new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] bytes, Camera camera) {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 8;

                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                bitmap = GetRotatedBitmap(bitmap, 90);
                imageView.setImageBitmap(bitmap);

                button.setEnabled(false);
                button.setText("텍스트 인식중...");
                new AsyncTess().execute(bitmap);
                camera.startPreview();




                /*BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 8;

                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
                bitmap = GetRotatedBitmap(bitmap, 90);
                //bitmap은 원본

                OpenCVLoader.initDebug(); // 초기화

                Mat uncropped = new Mat();
                Utils.bitmapToMat(bitmap, uncropped);
                Rect roi = new Rect(0, 0, 200, 200);
                Mat matGray = new Mat();
                Imgproc.cvtColor(uncropped, matGray, Imgproc.COLOR_BGR2GRAY); // GrayScale

                Mat cropped = new Mat(uncropped, roi);
                // cropped한 이미지 Mat객체로 가짐
                Bitmap imgRoi = Bitmap.createBitmap(cropped.cols(), cropped.rows(), Bitmap.Config.ARGB_8888);
                imgRoi = GetRotatedBitmap(imgRoi, 90);

                Utils.matToBitmap(cropped, imgRoi);
                //  imageView.setImageBitmap(imgRoi);

                button.setEnabled(false);
                button.setText("텍스트 인식중...");
                new OCRScanActivity.AsyncTess().execute(imgRoi);

                camera.startPreview();
*/

            }
        });
    }

    public synchronized static Bitmap GetRotatedBitmap(Bitmap bitmap, int degrees) {
        if (degrees != 0 && bitmap != null) {
            Matrix m = new Matrix();
            m.setRotate(degrees, (float) bitmap.getWidth() / 2, (float) bitmap.getHeight() / 2);
            try {
                Bitmap b2 = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
                if (bitmap != b2) {
                    bitmap = b2;
                }
            } catch (OutOfMemoryError ex) {
                ex.printStackTrace();
            }
        }
        return bitmap;
    }

    private class AsyncTess extends AsyncTask<Bitmap, Integer, String> {
        @Override
        protected String doInBackground(Bitmap... mRelativeParams) {
            tessBaseAPI.setImage(mRelativeParams[0]);
            return tessBaseAPI.getUTF8Text();
        }

        protected void onPostExecute(String result) {
            Log.d(TAG, result);

            String[] texts = result.split("\n");
            String nutrition;
            int calories = 0;
            int fat= 0;
            int transFat=0;
            int carbohydrate=0;
            int dietaryFiber=0;
            int sugars=0;



            for(int i=0;i<texts.length;i++) {
                texts[i] = texts[i].trim();

                if(texts[i].contains("Calories")) {
                    calories = tokenizing(texts[i], "Calories");
                }

                else if(texts[i].contains("Total Fat")){
                    fat = tokenizing(texts[i], "Total Fat");
                }

                else if(texts[i].contains("Trans Fat")){
                    transFat = tokenizing(texts[i], "Trans Fat");
                }

                else if(texts[i].contains("Total Carbohydrate")){
                    carbohydrate = tokenizing(texts[i], "Total Carbohydrate");
                }

                else if(texts[i].contains("Dietary Fiber")){
                    dietaryFiber = tokenizing(texts[i], "Dietary Fiber");
                }

                else if(texts[i].contains("Total Sugars")){
                    sugars = tokenizing(texts[i], "Total Sugars");
                }

            }

            System.out.println("Calories : "+ calories);
            System.out.println("Total Fat : "+ fat);
            System.out.println("Trans Fat : "+ transFat);
            System.out.println("Total Carbohydrate : "+ carbohydrate);
            System.out.println("Dietary Fiber : "+ dietaryFiber);
            System.out.println("Total Sugars : "+ sugars);

            OCRTextView.setText(result);
            Toast.makeText(OCRScanActivity.this, ""+result, Toast.LENGTH_LONG).show();

            button.setEnabled(true);
            button.setText("텍스트 인식");
        }

        protected int tokenizing(String oneline, String nutrition) {
            String[] tokens;
            int intake = 0;

            if(oneline.contains(nutrition)) {
                System.out.println("**" + oneline);
                tokens = oneline.split(nutrition);

                for(int j=0;j<tokens.length;j++) {
                    tokens[j] = tokens[j].trim();
                    System.out.println("--"+tokens[j]);


                    if(tokens[j].length() < 1) continue;
                    if(tokens[j].charAt(0) >= '0' && tokens[j].charAt(0) <= '9') {
                        tokens = tokens[j].split(" ");
                        System.out.println("++"+tokens);

                        if(tokens[0].endsWith("g")) {
                            intake = Integer.parseInt(tokens[0].split("g")[0]);
                        }
                        else if(tokens[0].endsWith("9")){
                            intake = Integer.parseInt(tokens[0].split("9")[0]);

                        }
                        else {
                            intake = Integer.parseInt(tokens[0]);
                        }

                    }

                }
            }
            return intake;
        }

    }
    private void startLoginActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }



    /* public void processImage(Bitmap bitmap) {
        Toast.makeText(getApplicationContext(), "이미지가 복잡할 경우 해석시 많은 시간이 소요될 수 있습니다.", Toast.LENGTH_LONG).show();
        String OCRresult = null;
        tess.setImage(bitmap);
        OCRresult = tess.getUTF8Text();
        OCRTextView.setText(OCRresult);
    }
*/
    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */

    public native String stringFromJNI();
}

