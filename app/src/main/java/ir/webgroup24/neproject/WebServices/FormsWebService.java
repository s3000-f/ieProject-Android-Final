package ir.webgroup24.neproject.WebServices;

import android.util.Log;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmList;
import ir.webgroup24.neproject.Interfaces.RequestCompleted;
import ir.webgroup24.neproject.Models.Form;
import ir.webgroup24.neproject.Models.FormField;
import ir.webgroup24.neproject.Models.SubmittedForm;
import ir.webgroup24.neproject.Models.User;
import ir.webgroup24.neproject.Utils.Constants;

public class FormsWebService {
    final static String TAG = "WebService";
    public static void getFormList(RequestCompleted action) {
        String token = Realm.getDefaultInstance().where(User.class).findFirst().getToken();
        if (token == null) return;
        String url = Constants.server + Constants.forms;
        Log.d(TAG, "getFormList: " + url);
        AndroidNetworking.get(url)
                .setPriority(Priority.HIGH)
                .addHeaders("x-access-token", token)
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

    public static void getForm(String id, RequestCompleted action) {
        String token = Realm.getDefaultInstance().where(User.class).findFirst().getToken();
        if (token == null) return;
        String url = Constants.server + Constants.forms + "/" + id;
        AndroidNetworking.get(url)
                .setPriority(Priority.HIGH)
                .addHeaders("x-access-token", token)
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

    public static void submitForm(SubmittedForm sub, RequestCompleted action) {
        String token = Realm.getDefaultInstance().where(User.class).findFirst().getToken();
        String username = Realm.getDefaultInstance().where(User.class).findFirst().getName();
        if (token == null) return;
        String url = Constants.server + Constants.forms + "/" + sub.getFormID();
        RealmList<FormField> fields = sub.getField();
        RealmList<String> datas = sub.getDatas();
        JSONArray arr = new JSONArray();
        for (int i = 0; i < fields.size(); i++) {
            Map<String, String> data = new HashMap<>();
            FormField ff = fields.get(i);
            if (datas.get(i) != null && !datas.get(i).equals("")) {
                data.put("Value", datas.get(i));
                assert ff != null;
                data.put("Type", ff.getType());
                data.put("Name", ff.getName());
                data.put("Title", ff.getTitle());
                JSONObject jso = new JSONObject(data);
                arr.put(jso);
            }
        }
        JSONObject js = new JSONObject();
        try {
            js.put("username", username);
            js.put("fields", arr);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "submitForm: " + js.toString());
        AndroidNetworking.post(url)
                .addJSONObjectBody(js)
                .addHeaders("x-access-token", token)
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
