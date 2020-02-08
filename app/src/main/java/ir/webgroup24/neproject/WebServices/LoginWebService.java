package ir.webgroup24.neproject.WebServices;

import android.util.Log;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import io.realm.RealmList;
import ir.webgroup24.neproject.Interfaces.RequestCompleted;
import ir.webgroup24.neproject.Models.FormField;
import ir.webgroup24.neproject.Models.SubmittedForm;
import ir.webgroup24.neproject.Utils.Constants;

public class LoginWebService {
    public static void Login(String username, String password, RequestCompleted action) {
        String url = Constants.server + Constants.login;
        Map<String, String> data = new HashMap<>();
        data.put("email", username);
        data.put("password", password);
        JSONObject js = new JSONObject(data);
        Log.d("Login", "Login: " + js.toString());
        AndroidNetworking.post(url)
                .addJSONObjectBody(js)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        action.onSuccess(response);
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d("ANError1", "onError: " + anError.getErrorBody());
                        Log.d("ANError1", "onError: " + anError.getErrorCode());
                        action.onFailure(anError);
                    }
                });
    }
}
