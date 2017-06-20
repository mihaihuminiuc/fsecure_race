package ro.fsecure.cros.f_secureaplicatiadeintrarelacors;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import java.util.ArrayList;

import ro.fsecure.cros.f_secureaplicatiadeintrarelacors.fragment.ConfirmFragment;
import ro.fsecure.cros.f_secureaplicatiadeintrarelacors.fragment.UserDataFragment;
import ro.fsecure.cros.f_secureaplicatiadeintrarelacors.listener.OnQrCodeDetected;
import ro.fsecure.cros.f_secureaplicatiadeintrarelacors.model.User;

public class MainActivity extends AppCompatActivity implements OnQrCodeDetected {

    private static FragmentManager mFragmentManager;
    private User mUser;
    private LinearLayout backLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_view);
        mFragmentManager = getSupportFragmentManager();


        if (getIntent().getExtras().containsKey("User")) {
            mUser = (User) getIntent().getSerializableExtra("User");
        }

        initUI();

        setActions();

        mFragmentManager.beginTransaction().replace(R.id.frame_x, UserDataFragment.newInstance(mUser), "FRAGMENT_EVENT").commit();
    }

    @Override
    public void onCodeDetected(User user) {
        mFragmentManager.beginTransaction().replace(R.id.frame_x, UserDataFragment.newInstance(user), "FRAGMENT_EVENT").commit();
    }

    @Override
    public void onUserSend(User user) {
        mFragmentManager.beginTransaction().replace(R.id.frame_x, ConfirmFragment.newInstance(user), "FRAGMENT_EVENT").commit();
    }

    @Override
    public void onClickTermsAndContitions() {
        Intent i = null;
        i = new Intent(this, PDFActivity.class);
        Bundle extras = new Bundle();
        i.putExtras(extras);
        startActivity(i);
    }

    @Override
    public void onResume() {
        super.onResume();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    private void initUI(){
        backLayout=(LinearLayout) findViewById(R.id.back_layout);
    }

    private void setActions(){

        backLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<Fragment> fragments = (ArrayList<Fragment>) mFragmentManager.getFragments();
                Fragment displayingFragment = null;
                for (Fragment f : fragments) {
                    if (f != null) {
                        displayingFragment = f; //will be stored the last one (displaying)
                    }
                }

                if (displayingFragment instanceof UserDataFragment) {
                    finish();
                    return;
                }

                if (displayingFragment instanceof ConfirmFragment) {
                    mFragmentManager.beginTransaction().replace(R.id.frame_x, UserDataFragment.newInstance(mUser), "FRAGMENT_EVENT").commit();
                    return;
                }
            }
        });
    }
}
