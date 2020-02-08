package ir.webgroup24.neproject;

import android.app.AlertDialog;
import android.content.Intent;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.androidnetworking.error.ANError;

import org.json.JSONException;
import org.json.JSONObject;

import dmax.dialog.SpotsDialog;
import io.realm.Realm;
import ir.webgroup24.neproject.Activitys.MainActivity;
import ir.webgroup24.neproject.Activitys.ShowForm;
import ir.webgroup24.neproject.Interfaces.RequestCompleted;
import ir.webgroup24.neproject.Models.User;

import static ir.webgroup24.neproject.WebServices.LoginWebService.Login;

public class login extends AppCompatActivity {
    private TextView fbook, acc, sin, sup;
    private EditText mal, pswd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        fbook = (TextView) findViewById(R.id.sinnp);
        mal = (EditText) findViewById(R.id.mal);
        pswd = (EditText) findViewById(R.id.pswd);
        fbook.setOnClickListener(v -> {
            AlertDialog progress = new SpotsDialog.Builder()
                    .setContext(login.this)
                    .setCancelable(false)
//                .setCancelListener(dialogInterface -> FAQActivity.this.finish())
                    .setMessage("Please Wait...")
                    .build();
//        progress.getWindow().setBackgroundDrawable(getDrawable(R.drawable.simple_round));
            progress.show();
            Login(mal.getText().toString(), pswd.getText().toString(), new RequestCompleted() {
                @Override
                public void onSuccess(JSONObject response) {
                    try {
                        if (response.getString("status").equals("OK")) {
                            User user = new User();
                            JSONObject u = response.getJSONObject("user");
                            user.setEmail(u.getString("email"));
                            user.setId(u.getString("_id"));
                            user.setName(u.getString("name"));
                            user.setToken(u.getString("access_token"));
                            Realm realm = Realm.getDefaultInstance();
                            realm.beginTransaction();
                            realm.deleteAll();
                            realm.copyToRealm(user);
                            realm.commitTransaction();
                            Log.d("Login", "onSuccess: " + response.toString());
                            Intent intent = new Intent(login.this, MainActivity.class);
                            progress.dismiss();
                            startActivity(intent);
                        }
                    } catch (JSONException e) {
                        progress.dismiss();
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(ANError error) {
                    progress.dismiss();
                    if (error.getErrorCode() == 401) {
                        Toast.makeText(login.this, "Wrong Username Or Password", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(login.this, "Connection Error", Toast.LENGTH_LONG).show();
                    }
                }
            });
        });
    }
}

