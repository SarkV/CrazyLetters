package com.avtdev.crazyletters.adapters;

import android.content.Context;
import android.graphics.Typeface;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LanguageListAdapter extends RecyclerView.Adapter<LanguageListAdapter.ViewHolder> {

    private List<Language> mList;

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView language;
        TextView ocurrences;
        ImageView selected;
        ViewHolder(View view) {
            super(view);
            language = view.findViewById(R.id.tvLanguage);
            ocurrences = view.findViewById(R.id.tvOcurrences);
            selected= view.findViewById(R.id.ivSelected);
        }
    }

    public LanguageListAdapter(List<Language> list) {
        this.mList = list;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View holder = LayoutInflater.from(parent.getContext()).inflate(R.layout.language_row, parent, false );

        ViewHolder viewHolder = new ViewHolder(holder);
        viewHolder.language.setTypeface(null, Typeface.NORMAL);
        viewHolder.ocurrences.setTypeface(null, Typeface.NORMAL);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if(Utils.isNull(mList.get(position).getLanguage())){
            holder.language.setText(R.string.all);
        }else{
            Locale loc = new Locale(mList.get(position ).getLanguage());
            holder.language.setText(loc.getDisplayLanguage());
        }
        holder.ocurrences.setText(String.valueOf(mList.get(position ).getOcurrences()));
        if(mList.get(position ).isSelected()){
            holder.selected.setVisibility(View.VISIBLE);
        }else{
            holder.selected.setVisibility(View.INVISIBLE);
        }

        holder.itemView.setOnClickListener(v -> validateClick(position));
    }

    private void validateClick(int position){
        if(position == 0){
            if(mList.get(position).isSelected()){
                mList.get(position).setSelected(false);
            }else{
                for(Language lan : mList){
                    lan.setSelected(false);
                }
                mList.get(position).setSelected(true);
            }
        }else{
            if(mList.get(position).isSelected()){
                mList.get(position).setSelected(false);
            }else {
                if (mList.get(0).isSelected()) {
                    for(Language lan : mList){
                        lan.setSelected(true);
                    }
                    mList.get(0).setSelected(false);
                    mList.get(position).setSelected(false);
                }else{
                    mList.get(position).setSelected(true);
                }
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mList != null ? mList.size() : 0;
    }

    public ArrayList<String> getSelectedLanguages(){
        ArrayList<String> list = new ArrayList<>();
        if(!mList.get(0).isSelected()) {
            for (Language lan : mList) {
                if(lan.isSelected()){
                    list.add(lan.getLanguage());
                }
            }
        }
        return list;
    }
}
