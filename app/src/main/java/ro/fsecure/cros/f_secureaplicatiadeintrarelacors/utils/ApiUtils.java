package ro.fsecure.cros.f_secureaplicatiadeintrarelacors.utils;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by humin on 6/13/2017.
 */

public class ApiUtils {

    private static final String BASE_URL="https://f-secure.ro";


    public static void getUserData(Context context,final String email, Response.Listener<String> successListener, Response.ErrorListener errorListener){
        RequestQueue queue = Volley.newRequestQueue(context);
        CustomStringRequest request = new CustomStringRequest(null,true, Request.Method.POST, BASE_URL, successListener, errorListener, context){
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }
            @Override
            protected Map<String, String> getParams(){
                Map<String, String> params = new HashMap<String, String>();
                params.put("cros_action", "get_part_single");
                params.put("cros_pass", "123asdqwe!@#");
                params.put("email", email);
                return params;
            }
        };
        queue.add(request);
    }

    public static void sendUserData(Context context,final Map<String, String> params, Response.Listener<String> successListener, Response.ErrorListener errorListener){
        RequestQueue queue = Volley.newRequestQueue(context);
        CustomStringRequest request = new CustomStringRequest(params,true, Request.Method.POST, BASE_URL, successListener, errorListener, context){
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }
            @Override
            protected Map<String, String> getParams(){
                params.put("cros_action", "update");
                params.put("cros_pass", "123asdqwe!@#");
                return params;
            }
        };
        queue.add(request);
    }

}
