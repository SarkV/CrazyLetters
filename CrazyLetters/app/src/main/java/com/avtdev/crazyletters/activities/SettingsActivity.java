package com.avtdev.crazyletters.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Switch;

import androidx.annotation.NonNull;

import com.avtdev.crazyletters.R;
import com.avtdev.crazyletters.services.GoogleService;
import com.avtdev.crazyletters.utils.Constants;
import com.avtdev.crazyletters.utils.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class SettingsActivity extends BaseActivity implements View.OnClickListener{

    Switch swAllowInvitations;
    Switch swShowNotifications;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        swShowNotifications = findViewById(R.id.swShowNotifications);
        swAllowInvitations = findViewById(R.id.swAllowInvitations);

        swShowNotifications.setChecked(Utils.getBooleanSharedPreferences(this, Constants.Preferences.SHOW_NOTIFICATIONS.name(), true));
        swAllowInvitations.setChecked(Utils.getBooleanSharedPreferences(this, Constants.Preferences.ALLOW_INVITATIONS.name(), true));

        findViewById(R.id.btnSave).setOnClickListener(this);
        findViewById(R.id.btnCancel).setOnClickListener(this);
        findViewById(R.id.btnSignOut).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnSave:
                Utils.setSharedPreferences(this,
                        Constants.Preferences.SHOW_NOTIFICATIONS.name(),
                        swShowNotifications.isChecked());

                Utils.setSharedPreferences(this,
                        Constants.Preferences.ALLOW_INVITATIONS.name(),
                        swAllowInvitations.isChecked());
            case R.id.btnCancel:
                finish();
                break;
            case R.id.btnSignOut:
                GoogleService.getInstance(this).signOut((@NonNull Task task) -> {
                    logout();
                });
                break;
        }
    }
}
