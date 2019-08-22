package com.avtdev.crazyletters.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.avtdev.crazyletters.R;
import com.avtdev.crazyletters.adapters.LanguageListAdapter;
import com.avtdev.crazyletters.models.realm.Language;
import com.avtdev.crazyletters.services.RealmManager;
import com.avtdev.crazyletters.utils.Constants;

import java.util.List;

public class LanguageSelectionActivity extends ListBaseActivity implements View.OnClickListener {

    RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language_selection);

        setToolbar(findViewById(R.id.toolbar));

        findViewById(R.id.btnSaveLanguage).setOnClickListener(this);

        mRecyclerView = findViewById(R.id.rvLanguages);

        List<Language> listLanguages = RealmManager.getInstance(this).getLanguages();

        String selectedLanguages = null;

        if(getIntent() != null && getIntent().getExtras() != null){
            selectedLanguages = getIntent().getExtras().getString(Constants.Extras.LANGUAGE_LIST.name());
        }

        boolean all = true;

        if(selectedLanguages != null && !selectedLanguages.isEmpty()){
            String[] splits = selectedLanguages.split(";");

            for(String s : splits){
                for(int i = 0; i < listLanguages.size(); i++){
                    if(s.equals(listLanguages.get(i).getLanguage())){
                        all = false;
                        listLanguages.get(i).setSelected(true);
                    }
                }
            }
        }
        if(all){
            listLanguages.get(0).setSelected(true);
        }

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mRecyclerView.setAdapter(new LanguageListAdapter(listLanguages));
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btnSaveLanguage){
            Intent i = new Intent();
            i.putStringArrayListExtra(Constants.Extras.LANGUAGE_LIST.name(), ((LanguageListAdapter) mRecyclerView.getAdapter()).getSelectedLanguages());
            setResult(RESULT_OK, i);
            finish();
        }
    }
}
