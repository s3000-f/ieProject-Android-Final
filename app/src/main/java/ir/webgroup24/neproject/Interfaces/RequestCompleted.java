package ir.webgroup24.neproject.Interfaces;

import com.androidnetworking.error.ANError;

import org.json.JSONObject;

public interface RequestCompleted {
    public void onSuccess(JSONObject response);
    public void onFailure(ANError error);
}
