package ir.webgroup24.neproject.Activitys;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ProgressBar;

import io.realm.Realm;
import ir.webgroup24.neproject.Models.User;
import ir.webgroup24.neproject.R;
import ir.webgroup24.neproject.login;

public class FirstScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_screen);
        ProgressBar p = findViewById(R.id.progressBar);
        p.setIndeterminate(true);
        Realm realm = Realm.getDefaultInstance();
        User u = realm.where(User.class).findFirst();
        if (u == null) {
            Intent intent = new Intent(FirstScreen.this, login.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(FirstScreen.this, MainActivity.class);
            startActivity(intent);
        }
    }
}
