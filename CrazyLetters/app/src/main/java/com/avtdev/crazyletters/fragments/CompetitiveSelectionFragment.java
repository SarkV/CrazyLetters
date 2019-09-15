package com.avtdev.crazyletters.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.avtdev.crazyletters.R;
import com.avtdev.crazyletters.listeners.IMain;
import com.avtdev.crazyletters.utils.GameConstants;
import com.google.android.gms.games.multiplayer.realtime.Room;

public class CompetitiveSelectionFragment extends Fragment implements View.OnClickListener{

    IMain mListener;

    private static final int WAITING_ROOM_CODE = 9007;

    public static CompetitiveSelectionFragment newInstance(IMain listener) {

        Bundle args = new Bundle();

        CompetitiveSelectionFragment fragment = new CompetitiveSelectionFragment();
        fragment.setArguments(args);
        fragment.mListener = listener;
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_competitive_selection, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.btnEasy).setOnClickListener(this);
        view.findViewById(R.id.btnMedium).setOnClickListener(this);
        view.findViewById(R.id.btnDifficult).setOnClickListener(this);
        view.findViewById(R.id.btnImpossible).setOnClickListener(this);
        view.findViewById(R.id.btnBack).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnBack:
                mListener.onBackPressed();
                break;
            case R.id.btnEasy:
                selectGameMode(GameConstants.Level.EASY);
                break;
            case R.id.btnMedium:
                selectGameMode(GameConstants.Level.MEDIUM);
                break;
            case R.id.btnDifficult:
                selectGameMode(GameConstants.Level.DIFFICULT);
                break;
            case R.id.btnImpossible:
                selectGameMode(GameConstants.Level.IMPOSSIBLE);
                break;
        }
    }

    private void selectGameMode(GameConstants.Level level){

       /* mGoogleService.startQuickGame(level.getValue(), new RoomUpdateCallback() {
            @Override
            public void onRoomCreated(int i, @Nullable Room room) {
            }

            @Override
            public void onJoinedRoom(int i, @Nullable Room room) {
                showWaitingRoom(room);
            }

            @Override
            public void onLeftRoom(int i, @NonNull String s) {

            }

            @Override
            public void onRoomConnected(int i, @Nullable Room room) {
                showWaitingRoom(room);
            }
        });*/
    }

    private void showWaitingRoom(Room room) {
       /* Games.getRealTimeMultiplayerClient(this, mGoogleService.getSignedAccount())
                .getWaitingRoomIntent(room, 1)
                .addOnSuccessListener(intent -> startActivityForResult(intent, WAITING_ROOM_CODE));*/
    }

   /* @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == WAITING_ROOM_CODE) {

            // Look for finishing the waiting room from code, for example if a
            // "start game" message is received.  In this case, ignore the result.
            if (mWaitingRoomFinishedFromCode) {
                return;
            }

            if (resultCode == RESULT_OK) {
                /*Intent i = new Intent(this, WaitingRoomActivity.class);
                Game game = RealmManager.getInstance(this).getDefaultGame(level);
                long id = 0;
                if(game != null){
                    id = game.getId();
                }
                i.putExtra(Constants.Extras.GAME_MODE.name(), GameConstants.Mode.MULTI_PLAYER.name());
                i.putExtra(Constants.Extras.GAME.name(), id);
                startActivity(i);
                finish();*/
         /*   } else if (resultCode == RESULT_CANCELED) {
                // Waiting room was dismissed with the back button. The meaning of this
                // action is up to the game. You may choose to leave the room and cancel the
                // match, or do something else like minimize the waiting room and
                // continue to connect in the background.

                // in this example, we take the simple approach and just leave the room:
                Games.getRealTimeMultiplayerClient(CompetitiveSelectionFragment.this,
                        mGoogleService.getSignedAccount())
                        .leave(mJoinedRoomConfig, mRoom.getRoomId());
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            } else if (resultCode == GamesActivityResultCodes.RESULT_LEFT_ROOM) {
                // player wants to leave the room.
                Games.getRealTimeMultiplayerClient(thisActivity,
                        GoogleSignIn.getLastSignedInAccount(this))
                        .leave(mJoinedRoomConfig, mRoom.getRoomId());
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            }
        }
    }*/
}
