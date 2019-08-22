package com.avtdev.crazyletters.activities;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.core.app.NavUtils;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.avtdev.crazyletters.R;
import com.avtdev.crazyletters.adapters.GameListAdapter;
import com.avtdev.crazyletters.models.realm.Game;
import com.avtdev.crazyletters.services.RealmManager;
import com.avtdev.crazyletters.utils.Constants;

import java.util.List;

public class GameListActivity extends ListBaseActivity{

    RecyclerView mRecyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_list);

        setToolbar(findViewById(R.id.toolbar));

        mRecyclerView = findViewById(R.id.rvGames);

        List<Game> listGames = RealmManager.getInstance(this).getGames();

        mRecyclerView.setAdapter(new GameListAdapter(this, listGames));
    }

    public void select(long gameId){
        Intent i = new Intent();
        i.putExtra(Constants.Extras.GAME.name(), gameId);
        setResult(RESULT_OK, i);
        finish();
    }
}
