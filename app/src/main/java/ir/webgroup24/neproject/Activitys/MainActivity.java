package ir.webgroup24.neproject.Activitys;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.androidnetworking.error.ANError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;
import io.realm.Realm;
import io.realm.RealmResults;
import ir.webgroup24.neproject.Adapters.FormListAdapter;
import ir.webgroup24.neproject.Interfaces.RequestCompleted;
import ir.webgroup24.neproject.Models.Form;
import ir.webgroup24.neproject.Models.SubmittedForm;
import ir.webgroup24.neproject.R;
import ir.webgroup24.neproject.WebServices.FormsWebService;
import ir.webgroup24.neproject.login;

public class MainActivity extends AppCompatActivity {
    RecyclerView rv;
    Realm realm = Realm.getDefaultInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rv = findViewById(R.id.rv);
        syncData();
        AlertDialog progress = new SpotsDialog.Builder()
                .setContext(MainActivity.this)
                .setCancelable(false)
//                .setCancelListener(dialogInterface -> FAQActivity.this.finish())
                .setMessage("Please Wait...")
                .build();
//        progress.getWindow().setBackgroundDrawable(getDrawable(R.drawable.simple_round));
        progress.show();
        FormsWebService.getFormList(new RequestCompleted() {
            @Override
            public void onSuccess(JSONObject response) {
                try {

                    JSONArray f = response.getJSONArray("forms");
                    for (int i = 0; i < f.length(); i++) {
                        JSONObject item = f.getJSONObject(i);
                        Form form = new Form();
                        form.setId(item.getString("_id"));
                        form.setName(item.getString("title"));
                        realm.beginTransaction();
                        realm.copyToRealmOrUpdate(form);
                        realm.commitTransaction();
                    }
                    initRecyclerView();
                    progress.dismiss();
                } catch (JSONException e) {
                    progress.dismiss();
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(ANError error) {
                progress.dismiss();
                initRecyclerView();
                Toast.makeText(MainActivity.this, "Error Connecting To Server", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void initRecyclerView() {
        RealmResults<Form> rf = realm.where(Form.class).findAll();
        if (rf == null || rf.isEmpty()) {
            Toast.makeText(MainActivity.this, "No Forms To Show!", Toast.LENGTH_LONG).show();
            return;
        }
        ArrayList<Form> forms = new ArrayList<>(rf);
        FormListAdapter adapter = new FormListAdapter(forms, MainActivity.this);
        rv.setHasFixedSize(true);
        rv.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        rv.setAdapter(adapter);
    }

    private void syncData() {
        RealmResults<SubmittedForm> forms = realm.where(SubmittedForm.class).findAll();
        for (SubmittedForm s: forms) {
            FormsWebService.submitForm(s, new RequestCompleted() {
                @Override
                public void onSuccess(JSONObject response) {
                    Log.d("asdfasdf", "onSuccess: " + response.toString());
                    realm.beginTransaction();
                    s.deleteFromRealm();
                    realm.commitTransaction();
                }

                @Override
                public void onFailure(ANError ignored) {}
            });
        }
    }
}
