package ir.webgroup24.neproject.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import io.realm.Realm;
import ir.webgroup24.neproject.Models.FieldOption;
import ir.webgroup24.neproject.Models.Form;
import ir.webgroup24.neproject.Models.FormField;

public class DataProcessor {
    public static void JSONtoField(Realm realm, Form form, JSONArray fields) throws Exception {
        for (int i = 0; i < fields.length(); i++) {
            JSONObject obj = fields.getJSONObject(i);
            FormField field = new FormField();
            field.setId(obj.getString("_id"));
            field.setName(obj.getString("name"));
            field.setTitle(obj.getString("title"));
            field.setRequired(obj.getBoolean("required"));
            field.setType(obj.getString("type"));

            if (field.getType().equals("Text")) {
                try {
                    JSONArray opt = obj.getJSONArray("options");
                    field.getOptions().clear();
                    for (int j = 0; j < opt.length(); j++) {
                        JSONObject item = opt.getJSONObject(j);
                        FieldOption option = new FieldOption();
                        option.setLabel(item.getString("label"));
                        option.setTextValue(item.getString("value"));
                        field.getOptions().add(option);
                    }
                    field.setType("DropDown");

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (field.getType().equals("Location")) {
                try {
                    JSONArray opt = obj.getJSONArray("options");
                    field.getOptions().clear();
                    for (int j = 0; j < opt.length(); j++) {
                        JSONObject item = opt.getJSONObject(j);
                        FieldOption option = new FieldOption();
                        option.setLabel(item.getString("label"));
                        option.setLat(item.getJSONObject("value").getDouble("lat"));
                        option.setLng(item.getJSONObject("value").getDouble("long"));
                        field.getOptions().add(option);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            realm.beginTransaction();
            realm.copyToRealmOrUpdate(field);
            FormField isThere = null;
            for (FormField f : form.getFields()) {
                if (f.getId().equals(field.getId())) {
                    isThere = f;
                    break;
                }
            }
            if (isThere != null) {
                int index = form.getFields().indexOf(isThere);
                form.getFields().remove(index);
                form.getFields().add(index, field);
            } else {
                form.getFields().add(field);
            }
            realm.commitTransaction();

        }
    }
}
