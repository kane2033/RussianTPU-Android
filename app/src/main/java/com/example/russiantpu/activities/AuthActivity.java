package com.example.russiantpu.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;

import com.example.russiantpu.R;
import com.example.russiantpu.fragments.LoginFragment;

public class AuthActivity extends AppCompatActivity {

    private FragmentManager fragmentManager;
    private final String fragmentTag = String.valueOf(R.string.prev_auth_frag_tag);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        fragmentManager = getSupportFragmentManager();
        //при запуске приложения, открывается фрагмент логина
        fragmentManager.beginTransaction().replace(R.id.fragment_container,
                new LoginFragment()).addToBackStack(fragmentTag).commit();
    }

    @Override
    public void onBackPressed() {
        if (fragmentManager.getBackStackEntryCount() > 1) {
            fragmentManager.popBackStack(); //возврат на предыдущий фрагмент
        }
        else {
            finish();
        }
    }
}