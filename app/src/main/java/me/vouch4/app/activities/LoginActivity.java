package me.vouch4.app.activities;

import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import me.vouch4.app.fragments.LoginFragment;

public class LoginActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(me.vouch4.app.R.layout.activity_login);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            getSupportFragmentManager().beginTransaction()
                    .replace(me.vouch4.app.R.id.root_layout, LoginFragment.newInstance()).commit();
        }

    }

}
