package ir.webgroup24.neproject.Adapters;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.schibstedspain.leku.LocationPickerActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import ir.webgroup24.neproject.Activitys.ShowForm;
import ir.webgroup24.neproject.Models.FieldOption;
import ir.webgroup24.neproject.Models.Form;
import ir.webgroup24.neproject.Models.FormField;
import ir.webgroup24.neproject.R;

public class FieldListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<FormField> fields;
    private Activity ctx;
    private boolean[] requireds;
    private String[] datas;

    public boolean[] getRequireds() {
        return requireds;
    }

    public String[] getDatas() {
        return datas;
    }

    public FieldListAdapter(List<FormField> fields, Activity ctx) {
        this.fields = fields;
        this.ctx = ctx;
        requireds = new boolean[fields.size()];
        datas = new String[fields.size()];
        Arrays.fill(requireds, false);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        String t = fields.get(i).getType();
        Log.d("asd2", "onCreateViewHolder: " + i);
        Log.d("asd2", "onCreateViewHolder: " + t.toString());
        RecyclerView.ViewHolder listItem;
        switch (t) {
            case "Location":
                View item = layoutInflater.inflate(R.layout.field_location_item, viewGroup, false);
                listItem = new LocationHolder(item);
                break;
            case "Text":
            case "Number":
                View item2 = layoutInflater.inflate(R.layout.field_text_item, viewGroup, false);
                listItem = new TextHolder(item2);
                break;
            case "Date":
                View item3 = layoutInflater.inflate(R.layout.field_date_item, viewGroup, false);
                listItem = new DateHolder(item3);
                break;
            case "DropDown":
                View item5 = layoutInflater.inflate(R.layout.field_dropdown_item, viewGroup, false);
                listItem = new DropdownHolder(item5);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + t);
        }
        Log.d("asd2", "onCreateViewHolder: " + listItem);
        return listItem;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int i) {
        FormField item = fields.get(i);
        Log.d("asd", "onBindViewHolder: " + item.getType().toString());
        Log.d("asd", "onBindViewHolder: " + holder);
        switch (item.getType()) {
            case "Location":
                ((LocationHolder) holder).setup(item, i);
                break;
            case "Text":
            case "Number":
                ((TextHolder) holder).setup(item, i);
                break;
            case "Date":
                ((DateHolder) holder).setup(item, i);
                break;
            case "DropDown":
                ((DropdownHolder) holder).setup(item, i);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return fields.size();
    }

    class TextHolder extends RecyclerView.ViewHolder {
        TextView title;
        EditText content;

        TextHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.text_title);
            content = itemView.findViewById(R.id.text_content);
        }

        void setup(FormField field, int pos) {
            if (field.getType().equals("Number")) {
                content.setInputType(InputType.TYPE_CLASS_NUMBER);
            }
            if (field.isRequired()) {
                title.setText(String.format("%s*", field.getTitle()));
            } else {
                title.setText(field.getTitle());
                requireds[pos] = true;
            }
            content.addTextChangedListener(new TextWatcher() {
                public void afterTextChanged(Editable s) {
                    if (content.getText().toString() != "") {
                        requireds[pos] = true;
                    }
                    datas[pos] = content.getText().toString();
                }

                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }
            });
        }
    }

    class DropdownHolder extends RecyclerView.ViewHolder {
        TextView title;
        Spinner content;

        DropdownHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.dropdown_title);
            content = itemView.findViewById(R.id.dropdown_content);
        }

        void setup(FormField field, int pos) {
            if (field.isRequired()) {
                title.setText(String.format("%s*", field.getTitle()));
            } else {
                title.setText(field.getTitle());
                requireds[pos] = true;
            }
            List<FieldOption> ops = field.getOptions();
            String[] opts = new String[ops.size() + 1];
            for (int i = 0; i < ops.size(); i++) {
                opts[i + 1] = ops.get(i).getLabel();
            }
            opts[0] = "Choose";
            ArrayAdapter arrayAdapter = new ArrayAdapter<>(ctx, R.layout.spinner_item_white, opts);
            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            content.setAdapter(arrayAdapter);
            content.setSelection(0);
            content.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (position != 0) {
                        if (field.isRequired()) requireds[pos] = true;
                        datas[pos] = opts[position];
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }
    }

    class DateHolder extends RecyclerView.ViewHolder {
        TextView title;
        EditText content;

        DateHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.date_title);
            content = itemView.findViewById(R.id.date_content);
        }

        void setup(FormField field, int pos) {
            if (field.isRequired()) {
                title.setText(String.format("%s*", field.getTitle()));
            } else {
                title.setText(field.getTitle());
                requireds[pos] = true;
            }
            content.setInputType(InputType.TYPE_NULL);
            content.setOnClickListener(v -> {
                DatePickerDialog dialog = new DatePickerDialog(ctx, (view, year, month, dayOfMonth) -> {
                    String s = year + "/" + month + "/" + dayOfMonth;
                    datas[pos] = s;
                    requireds[pos] = true;
                    content.setText(s);
                }, 2020, 1, 1);
                dialog.setTitle("Choose Date: ");
                dialog.show();
            });

        }
    }

    class LocationHolder extends RecyclerView.ViewHolder implements OnMapReadyCallback {
        TextView title;
        MapView content;
        GoogleMap mapCurrent;
        Button setLocation;
//        EditText content;

        LocationHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.location_title);
            content = itemView.findViewById(R.id.location_content);
            setLocation = itemView.findViewById(R.id.set_location);
            if (content != null) {
                content.onCreate(null);
                content.onResume();
                content.getMapAsync(this);
            }
        }

        void setup(FormField field, int pos) {
            title.setText(field.getTitle());
            requireds[pos] = true;
            if (field.getOptions() != null && field.getOptions().size() > 0) {
                if (mapCurrent != null) {

                    mapCurrent.clear();
                    mapCurrent.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    double minLat = 1000;
                    double minLng = 1000;
                    double maxLat = -1000;
                    double maxLng = -1000;
                    for (FieldOption o : field.getOptions()) {
                        if (o.getLabel().equals("Selected Location")) {
                            datas[pos] = "lat=" + o.getLat() + "&long=" + o.getLng();
                        }
                        double lat = o.getLat();
                        double lng = o.getLng();
                        Log.d("asda", String.format("%f %f", lat, lng));
                        mapCurrent.addMarker(new MarkerOptions()
                                .position(new LatLng(lat, lng))
                                .title(o.getLabel()));
                        if (lat < minLat) minLat = lat;
                        if (lng < minLng) minLng = lng;
                        if (lng > maxLng) maxLng = lng;
                        if (lat > maxLat) maxLat = lat;
                    }
                    Log.d("asda", String.format("%f %f %f %f", minLat, minLng, maxLat, maxLng));
                    mapCurrent.setLatLngBoundsForCameraTarget(
                            new LatLngBounds(
                                    new LatLng(minLat, minLng),
                                    new LatLng(maxLat, maxLng)));
                    double finalMinLat = minLat;
                    double finalMaxLat = maxLat;
                    double finalMinLng = minLng;
                    double finalMaxLng = maxLng;
                    setLocation.setOnClickListener(v -> {
                        Intent locationPickerIntent = new LocationPickerActivity.Builder()
                                .withLocation((finalMinLat + finalMaxLat) / 2, (finalMinLng + finalMaxLng) / 2)
                                .withGeolocApiKey("AIzaSyDU-Sc1BtXrw4c-PSE3eO2EiPE1-o7WqMA")
                                .withSatelliteViewHidden()
                                .withGooglePlacesEnabled()
                                .withGoogleTimeZoneEnabled()
                                .withVoiceSearchHidden()
                                .withUnnamedRoadHidden()
                                .build(ctx);
                        locationPickerIntent.putExtra("position", pos);
                        ctx.startActivityForResult(locationPickerIntent, pos);
                    });
                } else {

                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            ctx.runOnUiThread(() -> setup(field, pos));
                        }
                    }, 1000);
                }
            } else {
                setLocation.setOnClickListener(v -> {
                    Intent locationPickerIntent = new LocationPickerActivity.Builder()
//                            .withLocation(41.4036299, 2.1743558)
                            .withGeolocApiKey("AIzaSyDU-Sc1BtXrw4c-PSE3eO2EiPE1-o7WqMA")
                            .withSatelliteViewHidden()
                            .withGooglePlacesEnabled()
                            .withGoogleTimeZoneEnabled()
                            .withVoiceSearchHidden()
                            .withUnnamedRoadHidden()
                            .build(ctx);
                    locationPickerIntent.putExtra("position", pos);
                    ctx.startActivityForResult(locationPickerIntent, pos);
                });
            }
        }

        @Override
        public void onMapReady(GoogleMap googleMap) {
            MapsInitializer.initialize(ctx);
            mapCurrent = googleMap;

        }
    }


}
