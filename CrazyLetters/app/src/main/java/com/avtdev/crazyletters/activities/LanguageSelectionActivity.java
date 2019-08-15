package com.avtdev.crazyletters.activities;

import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.avtdev.crazyletters.R;
import com.avtdev.crazyletters.adapters.LanguageListAdapter;
import com.avtdev.crazyletters.models.realm.Language;
import com.avtdev.crazyletters.services.RealmManager;
import com.avtdev.crazyletters.utils.Constants;

import java.util.List;

public class LanguageSelectionActivity extends ListBaseActivity {

    RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language_selection);

        setToolbar(findViewById(R.id.toolbar));

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

        mRecyclerView.setAdapter(new LanguageListAdapter(this, listLanguages));
    }
}
