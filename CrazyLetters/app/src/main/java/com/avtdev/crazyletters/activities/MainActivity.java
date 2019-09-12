package com.avtdev.crazyletters.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.avtdev.crazyletters.R;
import com.avtdev.crazyletters.services.GoogleService;
import com.avtdev.crazyletters.utils.Constants;
import com.avtdev.crazyletters.utils.GameConstants;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesActivityResultCodes;
import com.google.android.gms.games.multiplayer.Invitation;
import com.google.android.gms.games.multiplayer.Multiplayer;
import com.google.android.gms.tasks.OnSuccessListener;

public class MainActivity extends BaseActivity implements View.OnClickListener{

    private static final String TAG = "MainActivty";

    public final static int SHOW_INVITATION_CODE = 9008;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setBannerAd();

        findViewById(R.id.btnSinglePlayer).setOnClickListener(this);
        if(GoogleService.getInstance(this).getPlayerId() != null){
            findViewById(R.id.btnMultiplayer).setOnClickListener(this);
            findViewById(R.id.btnSeeInvitations).setOnClickListener(this);
            findViewById(R.id.btnLeaderboard).setOnClickListener(this);

        }else{
            findViewById(R.id.btnMultiplayer).setEnabled(false);
            findViewById(R.id.btnSeeInvitations).setEnabled(false);
            findViewById(R.id.btnLeaderboard).setEnabled(false);
        }
        findViewById(R.id.btnSettings).setOnClickListener(this);
        findViewById(R.id.btnExit).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSinglePlayer:
                Intent i = new Intent(MainActivity.this, GameDefinitionActivity.class);
                i.putExtra(Constants.Extras.GAME_MODE.name(), GameConstants.Mode.SINGLE_PLAYER.name());
                startActivity(i);
                break;
            case R.id.btnMultiplayer:
                startActivity(new Intent(MainActivity.this, CompetitiveSelectionActivity.class));
                finish();
                break;
            case R.id.btnSeeInvitations:
                Games.getInvitationsClient(this, GoogleService.getInstance(this).getSignedAccount())
                        .getInvitationInboxIntent()
                        .addOnSuccessListener(intent -> startActivityForResult(intent, SHOW_INVITATION_CODE));
                break;
            case R.id.btnLeaderboard:
                GoogleService.getInstance(this).showLeaderboard();
                break;
            case R.id.btnSettings:
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                break;
            case R.id.btnExit:
                showTwoBtnDialog(R.string.warning,
                        R.string.warning_exit,
                        R.string.exit,
                        (dialog, which) -> finish(),
                        R.string.cancel,
                        (dialog, which) -> dialog.cancel());
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SHOW_INVITATION_CODE) {
            if (resultCode != RESULT_OK) {
                // Canceled or some error.
                return;
            }
            Invitation invitation = data.getExtras().getParcelable(Multiplayer.EXTRA_INVITATION);
            if (invitation != null) {
                /*RoomConfig.Builder builder = RoomConfig.builder(mRoomUpdateCallback)
                        .setInvitationIdToAccept(invitation.getInvitationId());
                mJoinedRoomConfig = builder.build();
                Games.getRealTimeMultiplayerClient(thisActivity,
                        GoogleSignIn.getLastSignedInAccount(this))
                        .join(mJoinedRoomConfig);
                // prevent screen from sleeping during handshake
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);*/
            }
        }
    }
}
