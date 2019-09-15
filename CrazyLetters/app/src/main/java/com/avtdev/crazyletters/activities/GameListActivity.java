package com.avtdev.crazyletters.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.avtdev.crazyletters.R;
import com.avtdev.crazyletters.adapters.GameListAdapter;
import com.avtdev.crazyletters.models.realm.Game;
import com.avtdev.crazyletters.services.RealmManager;
import com.avtdev.crazyletters.utils.Constants;

import java.util.List;

public class GameListActivity extends ListBaseActivity {

    RecyclerView mRecyclerView;

    boolean hasModified = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_list);

        setToolbar(findViewById(R.id.toolbar));

        if(getIntent() != null && getIntent().getExtras() != null){
            hasModified = getIntent().getExtras().getBoolean(Constants.Extras.GAME_MODIFIED.name());
        }

        mRecyclerView = findViewById(R.id.rvGames);

        List<Game> listGames = RealmManager.getInstance(this).getGames();

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mRecyclerView.setAdapter(new GameListAdapter(this, listGames));
    }

    public void select(long gameId){

        if(hasModified){
            showTwoBtnDialog(R.string.warning, R.string.warning_game_override, R.string.accept,
                    (dialog, which) -> {
                        Intent i = new Intent();
                        i.putExtra(Constants.Extras.GAME.name(), gameId);
                        setResult(RESULT_OK, i);
                        finish();
                    },
                    R.string.cancel, (dialog, which) -> {dialog.dismiss();});

        }else{
            Intent i = new Intent();
            i.putExtra(Constants.Extras.GAME.name(), gameId);
            setResult(RESULT_OK, i);
            finish();
        }
    }
}
