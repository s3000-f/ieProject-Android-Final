package ir.webgroup24.neproject.Models;

import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Form extends RealmObject {
    @PrimaryKey
    private String id;
    private String name;
    private RealmList<FormField> fields;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public RealmList<FormField> getFields() {
        return fields;
    }

    public void setFields(RealmList<FormField> fields) {
        this.fields = fields;
    }
}
