package net.anvisys.xpen.Login;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import net.anvisys.xpen.R;

public class RegisterActivity extends AppCompatActivity {
    Fragment registerFragment;
    Fragment planFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        FragmentManager fm =  getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        planFragment = new PlanFragment();
        ft.add(R.id.frameRegister, planFragment);
        ft.commit();
    }

    public void Selected(View v)
    {
        FragmentTransaction ft=  getSupportFragmentManager().beginTransaction();
        registerFragment = new RegisterFragment();
        ft.replace(R.id.frameRegister,registerFragment);
        ft.addToBackStack(null);
        ft.commit();
    }
}
