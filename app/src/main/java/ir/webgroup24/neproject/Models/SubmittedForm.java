package ir.webgroup24.neproject.Models;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class SubmittedForm extends RealmObject {
    @PrimaryKey
    private int id;
    private String formID;
    private RealmList<FormField> field;
    private RealmList<String> datas;

    public String getFormID() {
        return formID;
    }

    public void setFormID(String formID) {
        this.formID = formID;
    }

    public RealmList<FormField> getField() {
        return field;
    }

    public void setField(RealmList<FormField> field) {
        this.field = field;
    }

    public RealmList<String> getDatas() {
        return datas;
    }

    public void setDatas(RealmList<String> datas) {
        this.datas = datas;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
