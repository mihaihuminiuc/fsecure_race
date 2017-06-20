package ro.fsecure.cros.f_secureaplicatiadeintrarelacors;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.support.percent.PercentRelativeLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import ro.fsecure.cros.f_secureaplicatiadeintrarelacors.model.User;
import ro.fsecure.cros.f_secureaplicatiadeintrarelacors.utils.ApiUtils;
import ro.fsecure.cros.f_secureaplicatiadeintrarelacors.utils.CommonUtils;

/**
 * Created by humin on 6/15/2017.
 */

public class QrActivity extends AppCompatActivity {

    private BarcodeDetector barcodeDetector;
    private CameraSource cameraSource;
    private SurfaceView cameraView;
    private ProgressDialog mProgressDialog;
    private static final int PERMISSION_REQUEST_CODE = 123;
    private Point mScreenSize;
    private LinearLayout mScannerView;
    private AlertDialog errorDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qr_activity);

        mScreenSize = CommonUtils.getDefaultDisplaySize(this);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle(getString(R.string.please_wait));
        mProgressDialog.setCancelable(false);

        if(!checkCameraPermission())
            requestPermission();

        initUI();

        setupBarcodeScanner();

        setActions();
    }

    void initUI() {
        cameraView = (SurfaceView) findViewById(R.id.camera_view);
        mScannerView = (LinearLayout) findViewById(R.id.scanner_container);

        PercentRelativeLayout.LayoutParams layoutParams= new PercentRelativeLayout.LayoutParams(0,0);
        layoutParams.height=((int) (mScreenSize.x * 0.83));
        layoutParams.width=((int) (mScreenSize.y * 0.62));
        layoutParams.addRule(PercentRelativeLayout.ALIGN_PARENT_BOTTOM);
        layoutParams.addRule(PercentRelativeLayout.CENTER_HORIZONTAL);
        cameraView.setLayoutParams(layoutParams);
    }

    void setActions() {
        mScannerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCamera();
                if(checkCameraPermission()){
                    try {
                        cameraSource.start(cameraView.getHolder());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                initCamera();
            }
        });
    }

    private void setupBarcodeScanner() {
        barcodeDetector =
                new BarcodeDetector.Builder(getApplicationContext())
                        .setBarcodeFormats(Barcode.QR_CODE)
                        .build();
        cameraSource = new com.google.android.gms.vision.CameraSource
                .Builder(getBaseContext(), barcodeDetector)
                .setFacing(CameraSource.CAMERA_FACING_FRONT)
                .setRequestedPreviewSize(((int) (mScreenSize.x * 0.5)),((int) (mScreenSize.y * 0.4)))
                .build();
    }

    private void initCamera(){
        if(checkCameraPermission()){
            cameraView.getHolder().addCallback(  new SurfaceHolder.Callback() {
                @Override
                public void surfaceCreated(SurfaceHolder holder) {

                }

                @Override
                public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

                }

                @Override
                public void surfaceDestroyed(SurfaceHolder holder) {
                    cameraSource.stop();
                }
            });
        }else{
            requestPermission();
        }

        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {
            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();

                if (barcodes.size() != 0) {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mProgressDialog.show();
                            hideCamera();
                        }
                    });

                    final User[] newUser = new User[1];

                    String segments[] = barcodes.valueAt(0).displayValue.split("-");
                    String email = segments[segments.length - 1];

                    ApiUtils.getUserData(getApplicationContext(), email, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {

                                JSONArray jsonArray = new JSONArray(response);
                                JSONObject jsonObject = jsonArray.getJSONObject(0);
                                newUser[0] =new User(jsonObject);
                                if(newUser[0].contestNumber.isEmpty() || newUser[0].contestNumber.equals(""))
                                    goToUserData(newUser[0]);
                                else{
                                    showDialog(newUser[0].contestNumber);
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mProgressDialog.hide();
                                        hideCamera();
                                    }
                                });
                                Log.i("TAG","ERROR");
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mProgressDialog.hide();
                                    hideCamera();
                                }
                            });
                            Log.i("TAG","ERROR");
                        }
                    });
                }
            }
        });

    }

    private void showDialog(String number){
        mProgressDialog.hide();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String message = getString(R.string.qr_code_error);
        message = message.replace("XXX", number);
        builder.setMessage(message)
                .setTitle("Profil");
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                errorDialog.dismiss();

            }
        });
        errorDialog = builder.create();
        errorDialog.show();
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA}, PERMISSION_REQUEST_CODE);
    }

    private boolean checkCameraPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.CAMERA);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    Toast.makeText(getApplicationContext(), "Va rugam setati permisiunea", Toast.LENGTH_SHORT).show();
                    Log.e("value", "Permission Denied, You cannot use local drive .");
                }
                break;
        }
    }

    public void hideCamera(){
        mScannerView.setVisibility(View.VISIBLE);
        cameraSource.stop();
    }

    public void showCamera(){
        mScannerView.setVisibility(View.GONE);
    }

    @Override
    public void onResume() {
        super.onResume();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        if(mProgressDialog!=null && mProgressDialog.isShowing())
            mProgressDialog.dismiss();
        hideCamera();
    }

    /**
     * Stops the camera.
     */
    @Override
    public void onPause() {
        super.onPause();
        cameraSource.stop();
    }

    /**
     * Releases the resources associated with the camera source, the associated detectors, and the
     * rest of the processing pipeline.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        cameraSource.stop();
        if(mProgressDialog!=null && mProgressDialog.isShowing())
            mProgressDialog.dismiss();
    }

    private void goToUserData(User user){
        mProgressDialog.hide();
        Intent i = null;
        i = new Intent(this, MainActivity.class);
        Bundle extras = new Bundle();
        extras.putSerializable("User",user);
        i.putExtras(extras);
        startActivity(i);
    }
}
