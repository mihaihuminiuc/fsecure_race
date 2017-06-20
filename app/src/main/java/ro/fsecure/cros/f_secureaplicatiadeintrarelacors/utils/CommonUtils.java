package ro.fsecure.cros.f_secureaplicatiadeintrarelacors.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.view.Display;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by humin on 6/9/2017.
 */

public class CommonUtils {

    @SuppressWarnings("deprecation")
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public static Point getDefaultDisplaySize(Activity activity) {
        Point size = new Point();
        Display d = activity.getWindowManager().getDefaultDisplay();
        if (Build.VERSION.SDK_INT >= 13) {
            d.getSize(size);
        } else {
            size.set(d.getWidth(), d.getHeight());
        }
        return size;
    }

    public static void hideKeyboard(Activity activity) {
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(),InputMethodManager.HIDE_IMPLICIT_ONLY);
        }
    }

    public static String[] parseStandardListsForName(String response, String category) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArrayCigarettes = jsonObject.optJSONArray(category);

            ArrayList<String> nameBrandArray = new ArrayList<>();
            nameBrandArray.add(" - ");
            for (int i = 0; i < jsonArrayCigarettes.length(); i++) {
                JSONObject name = (JSONObject) jsonArrayCigarettes.get(i);
                nameBrandArray.add(name.optString("Value"));
            }
            return nameBrandArray.toArray(new String[nameBrandArray.size()]);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

}
