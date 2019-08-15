package com.avtdev.crazyletters.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.avtdev.crazyletters.R;
import com.avtdev.crazyletters.models.realm.Language;
import com.avtdev.crazyletters.utils.Utils;

import java.util.List;

public class LanguageListAdapter extends RecyclerView.Adapter {

    Context mContext;
    List<Language> mList;

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView language;
        public TextView ocurrences;
        public ImageView selected;
        public ViewHolder(View view) {
            super(view);
            language = view.findViewById(R.id.tvLanguage);
            ocurrences = view.findViewById(R.id.tvOcurrences);
            selected= view.findViewById(R.id.ivSelected);
        }
    }

    public LanguageListAdapter(Context context, List<Language> list) {
        this.mContext = context;
        this.mList = list;

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View holder = LayoutInflater.from(parent.getContext()).inflate(R.layout.language_row, parent, false );
        return new ViewHolder(holder);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(position > 0){
            if(Utils.isNull(mList.get(position - 1).getLanguage())){
                ((ViewHolder) holder).language.setText(R.string.all);
            }else{
                ((ViewHolder) holder).language.setText(mList.get(position - 1).getLanguage());
            }
            ((ViewHolder) holder).ocurrences.setText(String.valueOf(mList.get(position - 1).getOcurrences()));
            if(mList.get(position - 1).isSelected()){
                ((ViewHolder) holder).selected.setVisibility(View.VISIBLE);
            }else{
                ((ViewHolder) holder).selected.setVisibility(View.INVISIBLE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return mList != null ? mList.size() + 1 : 1;
    }
}
