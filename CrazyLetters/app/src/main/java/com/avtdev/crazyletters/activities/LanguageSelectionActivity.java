package com.avtdev.crazyletters.activities;

import android.os.Bundle;

import androidx.recyclerview.widget.RecyclerView;

import com.avtdev.crazyletters.R;
import com.avtdev.crazyletters.models.realm.Language;
import com.avtdev.crazyletters.services.RealmManager;

import java.util.List;

public class LanguageSelectionActivity extends ListBaseActivity {

    RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language_selection);

        setToolbar(findViewById(R.id.toolbar));

        mRecyclerView = findViewById(R.id.rvGames);

        List<Language> listLanguages = RealmManager.getInstance(this).getLanguages();
    }
}
