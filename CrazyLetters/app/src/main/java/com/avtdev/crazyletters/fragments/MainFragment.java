package com.avtdev.crazyletters.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.avtdev.crazyletters.R;
import com.avtdev.crazyletters.listeners.IGame;
import com.avtdev.crazyletters.listeners.IMain;
import com.avtdev.crazyletters.listeners.ISettings;
import com.avtdev.crazyletters.services.GoogleService;
import com.avtdev.crazyletters.utils.GameConstants;
import com.avtdev.crazyletters.utils.Utils;
import com.google.android.gms.games.Games;

public class MainFragment extends Fragment implements View.OnClickListener{

    private static final String TAG = "MainActivty";

    private IMain mListener;

    private final static int SHOW_INVITATION_CODE = 9008;

    private Button btnMultiplayer, btnSeeInvitations, btnLeaderboard;


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof IMain){
            mListener = (IMain) context;
        }else{
            throw new RuntimeException("Context not instance of IMain");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public static MainFragment newInstance() {

        Bundle args = new Bundle();

        MainFragment fragment = new MainFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.btnSinglePlayer).setOnClickListener(this);
        btnMultiplayer = view.findViewById(R.id.btnMultiplayer);
        btnSeeInvitations = view.findViewById(R.id.btnSeeInvitations);
        btnLeaderboard = view.findViewById(R.id.btnLeaderboard);
        view.findViewById(R.id.btnSettings).setOnClickListener(this);
        view.findViewById(R.id.btnExit).setOnClickListener(this);

        checkButtons();
    }

    public void checkButtons(){
        if(!mListener.isOffline()){
            mListener.setEnabled(btnMultiplayer, this);
            mListener.setEnabled(btnSeeInvitations, this);
            mListener.setEnabled(btnLeaderboard, this);
        }else{
            mListener.setDisabled(btnMultiplayer);
            mListener.setDisabled(btnSeeInvitations);
            mListener.setDisabled(btnLeaderboard);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSinglePlayer:
                GameDefinitionFragment gameDefinitionFragment = GameDefinitionFragment.newInstance( GameConstants.Mode.SINGLE_PLAYER);
                mListener.changeFragment(gameDefinitionFragment, false);
                break;
            case R.id.btnMultiplayer:
                mListener.changeFragment(CompetitiveSelectionFragment.newInstance(), false);
                break;
            case R.id.btnSeeInvitations:
                Games.getInvitationsClient(getContext(), GoogleService.getInstance(getContext()).getSignedAccount())
                        .getInvitationInboxIntent()
                        .addOnSuccessListener(intent -> startActivityForResult(intent, SHOW_INVITATION_CODE));
                break;
            case R.id.btnLeaderboard:
                GoogleService.getInstance(getContext()).showLeaderboard();
                break;
            case R.id.btnSettings:
                mListener.changeFragment(SettingsFragment.newInstance(), false);
                break;
            case R.id.btnExit:
                mListener.onBackPressed();
                break;
        }
    }

 /*   @Override
    void onActivityResult(int requestCode, int resultCode, Intent data) {
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
         /*   }
        }
    }*/

}
