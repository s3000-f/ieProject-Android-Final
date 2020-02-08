package ir.webgroup24.neproject.Activitys;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.androidnetworking.error.ANError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import ir.webgroup24.neproject.Adapters.FieldListAdapter;
import ir.webgroup24.neproject.Adapters.FormListAdapter;
import ir.webgroup24.neproject.Interfaces.RequestCompleted;
import ir.webgroup24.neproject.Models.FieldOption;
import ir.webgroup24.neproject.Models.Form;
import ir.webgroup24.neproject.Models.FormField;
import ir.webgroup24.neproject.Models.SubmittedForm;
import ir.webgroup24.neproject.R;
import ir.webgroup24.neproject.Utils.DataProcessor;
import ir.webgroup24.neproject.Utils.Datas;
import ir.webgroup24.neproject.WebServices.FormsWebService;

import static com.schibstedspain.leku.LocationPickerActivityKt.LATITUDE;
import static com.schibstedspain.leku.LocationPickerActivityKt.LOCATION_ADDRESS;
import static com.schibstedspain.leku.LocationPickerActivityKt.LONGITUDE;
import static com.schibstedspain.leku.LocationPickerActivityKt.TIME_ZONE_DISPLAY_NAME;
import static com.schibstedspain.leku.LocationPickerActivityKt.TIME_ZONE_ID;
import static com.schibstedspain.leku.LocationPickerActivityKt.TRANSITION_BUNDLE;
import static com.schibstedspain.leku.LocationPickerActivityKt.ZIPCODE;

public class ShowForm extends AppCompatActivity {
    private static final String TAG = "show_form";
    Form selectedForm = null;
    RecyclerView rv;
    FieldListAdapter adapter;
    Realm realm = Realm.getDefaultInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_form);
        Intent i = getIntent();
        String id = i.getStringExtra("id");
        rv = findViewById(R.id.rv);
        selectedForm = realm.where(Form.class).equalTo("id", id).findFirst();
        if (selectedForm == null) finish();
        getFormData(id);
//        Log.d(TAG, "onCreate: " + selectedForm.getFields());


        Button submit = findViewById(R.id.submit);
        submit.setOnClickListener(v -> {
            boolean[] reqs = adapter.getRequireds();
            String[] dat = adapter.getDatas();
            for (boolean b : reqs) {
                if (!b) {
                    Toast.makeText(ShowForm.this, "Please Fill All Fields Marked With *", Toast.LENGTH_LONG).show();
                    return;
                }
            }
            submitFormData(dat);
        });
        Toolbar mTopToolbar = findViewById(R.id.my_toolbar);
        //mTopToolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.White), PorterDuff.Mode.SRC_ATOP);
        setSupportActionBar(mTopToolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(selectedForm.getName());

    }

    private void initRecyclerView() {
        ArrayList<FormField> fields = new ArrayList<>(selectedForm.getFields());
        Log.d(TAG, "initRecyclerView: " + fields.toString());
        adapter = new FieldListAdapter(fields, ShowForm.this);
        rv.setHasFixedSize(true);
        rv.setLayoutManager(new LinearLayoutManager(ShowForm.this));
        rv.setAdapter(adapter);
    }

    private void getFormData(String id) {
        AlertDialog progress = new SpotsDialog.Builder()
                .setContext(ShowForm.this)
                .setCancelable(false)
//                .setCancelListener(dialogInterface -> FAQActivity.this.finish())
                .setMessage("Please Wait...")
                .build();
//        progress.getWindow().setBackgroundDrawable(getDrawable(R.drawable.simple_round));
        progress.show();
        FormsWebService.getForm(id, new RequestCompleted() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    DataProcessor.JSONtoField(realm, selectedForm, response.getJSONArray("fields"));
                    initRecyclerView();
                    progress.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                    progress.dismiss();
                }
            }

            @Override
            public void onFailure(ANError error) {
                progress.dismiss();
                initRecyclerView();
                Toast.makeText(ShowForm.this, "Error Connecting To Server", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void submitFormData(String[] datas) {
        SubmittedForm sub = new SubmittedForm();
        Integer id = (Integer) realm.where(SubmittedForm.class).max("id");
        if (id == null)
            sub.setId(0);
        else {
            sub.setId(id + 1);
        }
        sub.setField(selectedForm.getFields());
        RealmList<String> ind = new RealmList<>(datas);
        sub.setDatas(ind);
        sub.setFormID(selectedForm.getId());
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(sub);
        realm.commitTransaction();
        FormsWebService.submitForm(sub, new RequestCompleted() {
            @Override
            public void onSuccess(JSONObject response) {
                Toast.makeText(ShowForm.this, "Data Sent Successfully", Toast.LENGTH_LONG).show();
                Log.d(TAG, "onSuccess: " + response.toString());
                realm.beginTransaction();
                realm.where(SubmittedForm.class).equalTo("id",sub.getId()).findFirst().deleteFromRealm();
                realm.commitTransaction();
                ShowForm.this.finish();
            }

            @Override
            public void onFailure(ANError error) {
                Toast.makeText(ShowForm.this, "Connection Error", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && data != null) {
            Log.d("RESULT****", "OK");
            double latitude = data.getDoubleExtra(LATITUDE, 0.0);
            Log.d("LATITUDE****", "" + latitude);
            double longitude = data.getDoubleExtra(LONGITUDE, 0.0);
            Log.d("LONGITUDE****", "" + longitude);
            String address = data.getStringExtra(LOCATION_ADDRESS);
            Log.d("ADDRESS****", address);
            String postalcode = data.getStringExtra(ZIPCODE);
            Log.d("POSTALCODE****", postalcode);
//            Bundle bundle = data.getBundleExtra(TRANSITION_BUNDLE);
//            Log.d("BUNDLE TEXT****", bundle.getString("test"));
//            String timeZoneId = data.getStringExtra(TIME_ZONE_ID);
//            Log.d("TIME ZONE ID****", timeZoneId);
//            String timeZoneDisplayName = data.getStringExtra(TIME_ZONE_DISPLAY_NAME);
//            Log.d("TIME ZONE NAME****", timeZoneDisplayName);
            int pos = requestCode;
            int id = -1;
            if (selectedForm.getFields().get(pos).getOptions() != null) {
                for (FieldOption o : selectedForm.getFields().get(pos).getOptions()) {
                    if (o.getLabel().equals("Selected Location")) {
                        id = selectedForm.getFields().get(pos).getOptions().indexOf(o);
                    }
                }
            }
            realm.beginTransaction();
            if (id != -1) selectedForm.getFields().get(pos).getOptions().remove(id);
            FieldOption f = new FieldOption();
            f.setLabel("Selected Location");
            f.setLat(latitude);
            f.setLng(longitude);
            selectedForm.getFields().get(pos).getOptions().add(f);
            realm.commitTransaction();
            adapter.notifyDataSetChanged();
        }
        if (resultCode == Activity.RESULT_CANCELED) {
            Log.d("RESULT****", "CANCELLED");
        }
    }

    @Override
    public boolean onNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        this.finish();
    }


}
