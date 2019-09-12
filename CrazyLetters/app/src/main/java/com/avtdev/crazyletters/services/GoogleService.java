package com.avtdev.crazyletters.services;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;

import androidx.annotation.NonNull;

import com.avtdev.crazyletters.R;
import com.avtdev.crazyletters.activities.BaseActivity;
import com.avtdev.crazyletters.listeners.ISplash;
import com.avtdev.crazyletters.utils.Constants;
import com.avtdev.crazyletters.utils.Logger;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesCallbackStatusCodes;
import com.google.android.gms.games.GamesClientStatusCodes;
import com.google.android.gms.games.InvitationsClient;
import com.google.android.gms.games.Player;
import com.google.android.gms.games.PlayersClient;
import com.google.android.gms.games.RealTimeMultiplayerClient;
import com.google.android.gms.games.multiplayer.Participant;
import com.google.android.gms.games.multiplayer.realtime.RoomConfig;
import com.google.android.gms.games.multiplayer.realtime.RoomUpdateCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;

public class GoogleService {

    private static final String TAG = "GoogleService";

    private Context mContext;
    private static GoogleService sInstance;

    // Client used to sign in with Google APIs
    private GoogleSignInClient mGoogleSignInClient = null;

    // Client used to interact with the real time ic_multiplayer system.
    private RealTimeMultiplayerClient mRealTimeMultiplayerClient = null;

    // Client used to interact with the Invitation system.
    private InvitationsClient mInvitationsClient = null;

    // The participants in the currently active game
    ArrayList<Participant> mParticipants = null;

    // If non-null, this is the id of the invitation we received via the
    // invitation listener
    String mIncomingInvitationId = null;

    // Message buffer for sending messages
    byte[] mMsgBuf = new byte[2];

    // Room ID where the currently active game is taking place; null if we're
    // not playing.
    String mRoomId = null;

    // Holds the configuration of the current room.
    RoomConfig mRoomConfig;

    private RoomConfig mJoinedRoomConfig;

    private String mPlayerId;
    // Google Account
    GoogleSignInAccount mSignedInAccount = null;

    private GoogleService(Context context){
        mContext = context;
        createGoogleSignClient();
    }

    private void createGoogleSignClient(){
        GoogleSignInOptions  signInOptions =
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN)
                        .requestId()
                        .build();

        mGoogleSignInClient = GoogleSignIn.getClient(mContext, signInOptions);
    }

    public static GoogleService getInstance(Context context) {
        if(sInstance == null){
            sInstance = new GoogleService(context);
        }else{
            sInstance.mContext = context;
        }
        return sInstance;
    }

    public void startSignInIntent() {
        ((Activity) mContext).startActivityForResult(mGoogleSignInClient.getSignInIntent(), ConstantGS.REQUEST_CODE.SIGN_IN);
    }

    public void signInSilently(final ISplash listener) {
        Logger.d(TAG , "signInSilently");

        GoogleSignInOptions signInOptions = GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN;
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(mContext);

        if (GoogleSignIn.hasPermissions(account, signInOptions.getScopeArray())) {
            onConnected(account, listener);
        } else {
            GoogleSignInClient signInClient = GoogleSignIn.getClient(mContext, signInOptions);
            signInClient
                .silentSignIn()
                .addOnCompleteListener(
                        (Activity) mContext,
                        (@NonNull Task<GoogleSignInAccount> task) -> {
                                if (task.isSuccessful()) {
                                    mSignedInAccount = task.getResult();
                                    onConnected(mSignedInAccount, listener);
                                } else {
                                    listener.setSignInResult(Constants.SignInStatus.ERROR_SIGN_IN);
                                }
                        });
        }
    }

    public void signOut(OnCompleteListener listener){
        mGoogleSignInClient.signOut()
                .addOnCompleteListener((Activity) mContext, task -> {
                    onDisconnected();
                    listener.onComplete(task);
                });

    }

    public void checkSignIn(Intent data, ISplash listener){
        Object message = null;
        GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
        if (result.isSuccess()) {
            // The signed in account is stored in the result.
            GoogleSignInAccount signedInAccount = result.getSignInAccount();
            onConnected(signedInAccount, listener);
        } else {
            message = result.getStatus().getStatusMessage();
            if (message == null || ((String) message).isEmpty()) {
                message = R.string.error_signin_other;
            }
            ((BaseActivity) mContext).showOneBtnDialog(null, message, android.R.string.ok, null);
        }
    }

    private void onConnected(GoogleSignInAccount googleSignInAccount, ISplash listener) {
        Logger.d(TAG , "onConnected");
        if (mSignedInAccount != googleSignInAccount) {

            mSignedInAccount = googleSignInAccount;

            // update the clients
            mPlayerId = mSignedInAccount.getId();
            mRealTimeMultiplayerClient = Games.getRealTimeMultiplayerClient(mContext, googleSignInAccount);
            mInvitationsClient = Games.getInvitationsClient(mContext, googleSignInAccount);

            // get the playerId from the PlayersClient
            PlayersClient playersClient = Games.getPlayersClient(mContext, googleSignInAccount);
            playersClient.getCurrentPlayer()
                    .addOnSuccessListener((Player player) -> {
                        mPlayerId = player.getPlayerId();
                        listener.setSignInResult(Constants.SignInStatus.OK);
                    })
                    .addOnFailureListener(createFailureListener("There was a problem getting the player id!", listener));
        }else{
            listener.setSignInResult(Constants.SignInStatus.OK);
        }
    }

    private void onDisconnected() {
        Logger.d(TAG , "onDisconnected");

        mRealTimeMultiplayerClient = null;
        mInvitationsClient = null;
        mPlayerId = null;
        mSignedInAccount = null;
    }

    public void showLeaderboard() {
        Games.getLeaderboardsClient(mContext, GoogleSignIn.getLastSignedInAccount(mContext))
                .getLeaderboardIntent(mContext.getString(R.string.leaderboard_easy_level))
                .addOnSuccessListener(new OnSuccessListener<Intent>() {
                    @Override
                    public void onSuccess(Intent intent) {
                        ((Activity) mContext).startActivityForResult(intent, 9004);
                    }
                });
    }

    public String getPlayerId() {
        return mPlayerId;
    }


    private OnFailureListener createFailureListener(final String string, ISplash listener) {
        return (@NonNull Exception e) -> {
               handleException(e, string, listener);
        };
    }

    /**
     * Since a lot of the operations use tasks, we can use a common handler for whenever one fails.
     *
     * @param exception The exception to evaluate.  Will try to display a more descriptive reason for the exception.
     * @param details   Will display alongside the exception if you wish to provide more details for why the exception
     *                  happened
     */
    private void handleException(Exception exception, String details, ISplash listener) {
        int status = 0;

        if (exception instanceof ApiException) {
            ApiException apiException = (ApiException) exception;
            status = apiException.getStatusCode();
        }

        String errorString = null;
        switch (status) {
            case GamesCallbackStatusCodes.OK:
                break;
            case GamesClientStatusCodes.MULTIPLAYER_ERROR_NOT_TRUSTED_TESTER:
                errorString = mContext.getString(R.string.status_multiplayer_error_not_trusted_tester);
                break;
            case GamesClientStatusCodes.MATCH_ERROR_ALREADY_REMATCHED:
                errorString = mContext.getString(R.string.error_match_already_rematched);
                break;
            case GamesClientStatusCodes.NETWORK_ERROR_OPERATION_FAILED:
                errorString = mContext.getString(R.string.error_network_operation_failed);
                break;
            case GamesClientStatusCodes.INTERNAL_ERROR:
                errorString = mContext.getString(R.string.internal_error);
                break;
            case GamesClientStatusCodes.MATCH_ERROR_INACTIVE_MATCH:
                errorString = mContext.getString(R.string.error_match_inactive_match);
                break;
            case GamesClientStatusCodes.MATCH_ERROR_LOCALLY_MODIFIED:
                errorString = mContext.getString(R.string.match_error_locally_modified);
                break;
            default:
                errorString = mContext.getString(R.string.unexpected_status, GamesClientStatusCodes.getStatusCodeString(status));
                break;
        }

        if (errorString == null) {
            return;
        }

        String message = mContext.getString(R.string.status_exception_error, details, status, exception);

        listener.setSignInResult(Constants.SignInStatus.ERROR_PLAYER);

        ((BaseActivity) mContext).showOneBtnDialog(null, message + "\n" + errorString, android.R.string.ok, null);
    }

    public GoogleSignInAccount getSignedAccount() {
        return mSignedInAccount;
    }

    public void startQuickGame(long gameType, RoomUpdateCallback roomUpdateCallback, ){
        // auto-match criteria to invite one random automatch opponent.
        // You can also specify more opponents (up to 3).
        Bundle autoMatchCriteria = RoomConfig.createAutoMatchCriteria(1, 1, gameType);

        // build the room config:
      /*  mJoinedRoomConfig = RoomConfig.builder(roomUpdateCallback)
                        .setOnMessageReceivedListener(mMessageReceivedHandler)
                        .setRoomStatusUpdateCallback(mRoomStatusCallbackHandler)
                        .setAutoMatchCriteria(autoMatchCriteria)
                        .build();*/

        // prevent screen from sleeping during handshake
        ((Activity) mContext).getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // create room:
        Games.getRealTimeMultiplayerClient(mContext, mSignedInAccount)
                .create(mJoinedRoomConfig);
    }
}
