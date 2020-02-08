package ir.webgroup24.neproject.Models;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class FormField extends RealmObject {
    @PrimaryKey
    private String id;
    private String name;
    private String title;
    private String type;
    private RealmList<FieldOption> options;
    private boolean isRequired;

    public boolean isRequired() {
        return isRequired;
    }

    public void setRequired(boolean required) {
        isRequired = required;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public RealmList<FieldOption> getOptions() {
        return options;
    }

    public void setOptions(RealmList<FieldOption> options) {
        this.options = options;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}

