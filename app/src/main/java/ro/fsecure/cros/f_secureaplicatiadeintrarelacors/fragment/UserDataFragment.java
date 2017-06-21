package ro.fsecure.cros.f_secureaplicatiadeintrarelacors.fragment;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import ro.fsecure.cros.f_secureaplicatiadeintrarelacors.R;
import ro.fsecure.cros.f_secureaplicatiadeintrarelacors.adapter.SpinnerAdapter;
import ro.fsecure.cros.f_secureaplicatiadeintrarelacors.listener.OnQrCodeDetected;
import ro.fsecure.cros.f_secureaplicatiadeintrarelacors.model.User;
import ro.fsecure.cros.f_secureaplicatiadeintrarelacors.utils.ApiUtils;
import ro.fsecure.cros.f_secureaplicatiadeintrarelacors.utils.CommonUtils;
import ro.fsecure.cros.f_secureaplicatiadeintrarelacors.utils.StaticList;
import ro.fsecure.cros.f_secureaplicatiadeintrarelacors.view.SignatureView;

/**
 * Created by humin on 6/9/2017.
 */

public class UserDataFragment extends Fragment {

    private User mUser;
    private SpinnerAdapter adapterCategory, adapterTeeShirtSize, adapterRaceType, adapterKitType;
    private EditText mFirstName, mLastName, mCompany, mPhone, mFunction, mEmail;
    private TextView mDiscalimer;
    private Spinner mCategory, mTeeShirt, mRaceType, mKitType;
    private Button mSendButton;
    private CheckBox mTermsAndConditions;
    private OnQrCodeDetected mOnQrCodeDetected;
    private ProgressDialog mProgressDialog;
    private SignatureView signatureView;
    private static final int PERMISSION_REQUEST_CODE = 456;

    private String firstName="", lastName="", company="", function="", phoneNumber="", email="",
            category="", teeShirtSize="", raceType="", kitType="";
    private Map<String, String> params;

    public static UserDataFragment newInstance(User user){
        UserDataFragment instance = new UserDataFragment();
        instance.mUser=user;
        return instance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.user_data_fragment, container, false);

        getActivity().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );

        mProgressDialog = new ProgressDialog(getContext());
        mProgressDialog.setTitle(getString(R.string.please_wait));
        mProgressDialog.setCancelable(false);

        initUi(view);
        setData(mUser);
        setActions();

        if(!checkStoragePermission()){
            requestPermission();
        }

        return view;
    }

    private void initUi(View view){

        mFirstName = (EditText) view.findViewById(R.id.user_first_name);
        mLastName = (EditText) view.findViewById(R.id.user_last_name);
        mCompany = (EditText) view.findViewById(R.id.user_company);
        mPhone = (EditText) view.findViewById(R.id.user_phone);
        mFunction  = (EditText) view.findViewById(R.id.user_function);
        mEmail = (EditText) view.findViewById(R.id.user_email);

        mCategory = (Spinner) view.findViewById(R.id.user_spinner_category);
        mTeeShirt = (Spinner) view.findViewById(R.id.user_spinner_tee_shirt);
        mRaceType = (Spinner) view.findViewById(R.id.user_race_type);
        mKitType  = (Spinner) view.findViewById(R.id.user_kit_type);

        mTermsAndConditions = (CheckBox) view.findViewById(R.id.terms_and_conditions);

        signatureView = (SignatureView) view.findViewById(R.id.signatureView);

        mDiscalimer=(TextView) view.findViewById(R.id.disclaimer);

        mSendButton = (Button) view.findViewById(R.id.confirm_button);
    }

    private void setData(User user) {

        mFirstName.setText(user.firstName);
        mLastName.setText(user.lastName);
        mCompany.setText(user.company);
        mPhone.setText(user.phoneNumber);
        mFunction.setText(user.function);
        mEmail.setText(user.email);

        adapterCategory = new SpinnerAdapter(getContext(), getActivity(), R.layout.simple_spinner_view, StaticList.category);
        adapterTeeShirtSize = new SpinnerAdapter(getContext(), getActivity(), R.layout.simple_spinner_view, StaticList.teeShirtSize);
        adapterRaceType = new SpinnerAdapter(getContext(), getActivity(), R.layout.simple_spinner_view, StaticList.raceType);
        adapterKitType = new SpinnerAdapter(getContext(), getActivity(), R.layout.simple_spinner_view, StaticList.kitType);

        mCategory.setAdapter(adapterCategory);
        mTeeShirt.setAdapter(adapterTeeShirtSize);
        mRaceType.setAdapter(adapterRaceType);
        mKitType.setAdapter(adapterKitType);

        if (user.category != null) {
            for (int i = 0; i < adapterCategory.getCount(); i++) {
                if (user.category.toLowerCase().equals(adapterCategory.getItem(i).toString().toLowerCase())) {
                    mCategory.setSelection(i);
                }
            }
        } else {
            mCategory.setSelection(0);
        }

        if (user.raceType != null) {
            for (int i = 0; i < adapterRaceType.getCount(); i++) {
                if (user.raceType.toLowerCase().equals(adapterRaceType.getItem(i).toString().toLowerCase())) {
                    mRaceType.setSelection(i);
                }
            }
        } else {
            mRaceType.setSelection(0);
        }

        String tempString1 = adapterKitType.getItem(2).toString().toLowerCase().trim();
        tempString1 = tempString1.replaceAll("\\s+", "");

        if (user.raceKit != null) {
            for (int i = 0; i < adapterKitType.getCount(); i++) {
                String tempString = adapterKitType.getItem(i).toString().toLowerCase().trim();
                tempString = tempString.replaceAll("\\s+", "");
                if (user.raceKit.toLowerCase().equals(tempString1))
                    mTeeShirt.setSelection(0);
                if (user.raceKit.toLowerCase().equals(tempString)) {
                    mKitType.setSelection(i);
                    if (user.teeSheetSize != null && !user.raceKit.toLowerCase().equals(tempString1)) {
                        for (int j = 0; j < adapterTeeShirtSize.getCount(); j++) {
                            if (user.teeSheetSize.toLowerCase().equals(adapterTeeShirtSize.getItem(j).toString().toLowerCase())) {
                                mTeeShirt.setSelection(j);
                            }
                        }
                    } else {
                        mTeeShirt.setSelection(0);
                    }
                }
            }
        } else {
            mKitType.setSelection(0);
        }
    }

    private void setActions(){

        SpannableString ss = new SpannableString(getActivity().getResources().getString(R.string.terms_and_conditions));
        ClickableSpan localPdf = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                mOnQrCodeDetected.onClickTermsAndContitions();
            }
            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
            }
        };

        ClickableSpan internetPdf = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://f-secure.ro/stiri/cros-f-secure-step-by-step-2017/"));
                startActivity(browserIntent);
            }
            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
            }
        };

        ss.setSpan(localPdf, 190, 194, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(internetPdf, 175, 186, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        mDiscalimer.setText(ss);

        mDiscalimer.setMovementMethod(LinkMovementMethod.getInstance());
        mDiscalimer.setHighlightColor(Color.TRANSPARENT);

        if (mDiscalimer != null) {
            mDiscalimer.setMovementMethod(LinkMovementMethod.getInstance());
            mDiscalimer.setLinksClickable(true);
        }

        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setUserInformation();
            }
        });
    }

    private void setUserInformation(){

        if(mFirstName.getText().toString().equals("") || mFirstName.getText().toString().isEmpty()){
            showDialog(getString(R.string.insert_name));
            return;
        }else{
            firstName = mFirstName.getText().toString();
        }

        if(mLastName.getText().toString().equals("") || mLastName.getText().toString().isEmpty()){
            showDialog(getString(R.string.insert_lname));
            return;
        }else{
            lastName = mLastName.getText().toString();
        }

        if(mCompany.getText().toString().equals("") || mCompany.getText().toString().isEmpty()){
            showDialog(getString(R.string.insert_company));
            return;
        }else{
            company = mCompany.getText().toString();
        }

        if(mPhone.getText().toString().equals("") || mPhone.getText().toString().isEmpty()){
            showDialog(getString(R.string.insert_phone_number));
            return;
        }else{
            phoneNumber=mPhone.getText().toString();
        }

        if(mFunction.getText().toString().equals("") || mFunction.getText().toString().isEmpty()){
            showDialog(getString(R.string.insert_function));
            return;
        }else{
            function=mFunction.getText().toString();
        }

        if(mEmail.getText().toString().equals("") || mEmail.getText().toString().isEmpty()){
            showDialog(getString(R.string.inser_email));
            return;
        }else{
            email=mEmail.getText().toString();
        }

        if(mCategory.getSelectedItem().toString().equals("-")){
            showDialog(getString(R.string.choose_category));
            return;
        }else{
            category=mCategory.getSelectedItem().toString();
        }

        if(mTeeShirt.getSelectedItem().toString().equals("-")){
            showDialog(getString(R.string.choose_tee_shiert_size));
            return;
        }else{
            teeShirtSize=mTeeShirt.getSelectedItem().toString();
        }

        if(mRaceType.getSelectedItem().toString().equals("-")){
            showDialog(getString(R.string.choose_race_type));
            return;
        }else{
            raceType=mRaceType.getSelectedItem().toString();
        }

        if(mKitType.getSelectedItem().toString().equals("-")){
            showDialog(getString(R.string.choose_kit));
            return;
        }else{
            kitType=mKitType.getSelectedItem().toString();
        }

        if(!mTermsAndConditions.isChecked()){
            showDialog(getString(R.string.tersm_and_conditions));
            return;
        }

        if(!signatureView.isSignedArea()){
            showDialog(getString(R.string.signature_warning));
            return;
        }

        params = new HashMap<>();

        params.put("id", String.valueOf(mUser.userId));
        params.put("part_last_name",firstName);
        params.put("part_first_name",lastName);
        params.put("company",company);
        params.put("function",function);
        params.put("telephone",phoneNumber);
        params.put("email",email);
        params.put("category",category);
        params.put("race",raceType);
        params.put("size",teeShirtSize);

        kitType= kitType.replaceAll("\\s+","");
        params.put("type",kitType.toLowerCase());

        takeScreenshot();
        mProgressDialog.show();
        sendInformation();
    }

    private void sendInformation(){
        final User[] newUser = new User[1];

        ApiUtils.sendUserData(getContext(),params, new Response.Listener<String>() {
            @Override
            public void onResponse(final String response) {
                mProgressDialog.hide();

                try {
                    JSONArray jsonArray = new JSONArray(response);
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    newUser[0] =new User(jsonObject);

                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage(R.string.user_save)
                            .setTitle("Profil");
                    builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                            mOnQrCodeDetected.onUserSend(newUser[0]);
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();

                } catch (JSONException e) {
                    mProgressDialog.hide();
                    showDialog(" Eroare server.");
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mProgressDialog.hide();
                showDialog(" Eroare server. ");
            }
        });
    }

    private void showDialog(String message) {
        mProgressDialog.hide();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setMessage(message)
                .setTitle("Profil");
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mProgressDialog!=null && mProgressDialog.isShowing())
            mProgressDialog.dismiss();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnQrCodeDetected) {
            mOnQrCodeDetected = (OnQrCodeDetected) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnLoginFragmentsInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mOnQrCodeDetected = null;
        if(mProgressDialog!=null && mProgressDialog.isShowing())
            mProgressDialog.dismiss();
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void requestPermission() {
        requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
    }

    private boolean checkStoragePermission() {
        int result = ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
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
                    takeScreenshot();
                } else {
                    Toast.makeText(getActivity(), "Va rugam setati permisiunea de a salva imaginea pe dispozitiv.", Toast.LENGTH_SHORT).show();
                    Log.e("value", "Permission Denied, You cannot use local drive .");
                }
                break;
        }
    }

    private void takeScreenshot() {
        Date now = new Date();
        android.text.format.DateFormat.format("yyyy-MM-dd'T'hh:mm:ss", now);

        final String appDirectoryName = "fsecure_photos";
        final File imageRoot = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), appDirectoryName);

        if(!imageRoot.exists()){    //check if file already exists
            imageRoot.mkdirs();     //if not, create it
        }

        try {
            // image naming and path  to include sd card  appending name you choose for file
            String mPath = imageRoot+"/"+ mUser.firstName+"_"+mUser.lastName+now.toString() + ".jpg";

            // create bitmap screen capture
            View v1 = getActivity().getWindow().getDecorView().getRootView();
            v1.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
            v1.setDrawingCacheEnabled(false);

            File imageFile = new File(mPath);

            FileOutputStream outputStream = new FileOutputStream(imageFile);
            int quality = 100;
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);

            MediaStore.Images.Media.insertImage(getActivity().getContentResolver(), bitmap, mUser.firstName+"_"+mUser.lastName , "ScreenShot");

            outputStream.flush();
            outputStream.close();

        } catch (Throwable e) {
            // Several error may come out with file handling or OOM
            e.printStackTrace();
        }

    }


}
