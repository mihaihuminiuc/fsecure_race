package ro.fsecure.cros.f_secureaplicatiadeintrarelacors.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import ro.fsecure.cros.f_secureaplicatiadeintrarelacors.MainActivity;
import ro.fsecure.cros.f_secureaplicatiadeintrarelacors.QrActivity;
import ro.fsecure.cros.f_secureaplicatiadeintrarelacors.R;
import ro.fsecure.cros.f_secureaplicatiadeintrarelacors.model.User;

/**
 * Created by humin on 6/9/2017.
 */

public class ConfirmFragment extends Fragment {

    private User mUser;
    private TextView mRaceNumber, mRaceCategory, mRaceType;
    private Button mBack;

    public static ConfirmFragment newInstance(User user){
        ConfirmFragment instance = new ConfirmFragment();
        instance.mUser=user;
        return instance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.confirm_fragment, container, false);

        initUI(view);
        setupData();
        setActions();

        return view;
    }

    private void initUI(View view){
        mRaceNumber = (TextView) view.findViewById(R.id.contest_number);
        mRaceCategory = (TextView) view.findViewById(R.id.race_category);
        mRaceType = (TextView) view.findViewById(R.id.race_type);
        mBack = (Button) view.findViewById(R.id.confirm_button);
    }

    private void setupData(){
        mRaceNumber.setText(mUser.contestNumber);
        mRaceCategory.setText(mUser.category);
        mRaceType.setText(mUser.raceType);
    }

    private void setActions(){

        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
                Intent i = null;
                i = new Intent(getActivity(), QrActivity.class);
                Bundle extras = new Bundle();
                i.putExtras(extras);
                startActivity(i);
            }
        });
    }

}