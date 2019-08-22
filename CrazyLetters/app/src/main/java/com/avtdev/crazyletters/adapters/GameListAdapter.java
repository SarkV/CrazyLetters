package com.avtdev.crazyletters.adapters;

import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.avtdev.crazyletters.R;
import com.avtdev.crazyletters.activities.GameListActivity;
import com.avtdev.crazyletters.models.realm.Game;
import com.avtdev.crazyletters.services.RealmManager;
import com.avtdev.crazyletters.utils.Utils;

import java.text.DateFormat;
import java.util.List;

public class GameListAdapter extends RecyclerView.Adapter<GameListAdapter.ViewHolder> {

    private GameListActivity mActivity;
    private List<Game> mList;

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView time;
        TextView languages;
        TextView velocity;
        TextView lettersType;
        TextView lastUsed;
        ImageView delete;

        ViewHolder(View view) {
            super(view);

            name = view.findViewById(R.id.tvLanguage);
            time = view.findViewById(R.id.tvLanguage);
            languages = view.findViewById(R.id.tvLanguage);
            velocity = view.findViewById(R.id.tvLanguage);
            lettersType = view.findViewById(R.id.tvLanguage);
            lastUsed = view.findViewById(R.id.tvLanguage);
            delete = view.findViewById(R.id.tvLanguage);
        }
    }

    public GameListAdapter(GameListActivity activity, List<Game> list) {
        this.mActivity = activity;
        this.mList = list;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View holder = LayoutInflater.from(parent.getContext()).inflate(R.layout.language_row, parent, false );

        ViewHolder viewHolder = new ViewHolder(holder);

        viewHolder.name.setTypeface(null, Typeface.NORMAL);
        viewHolder.time.setTypeface(null, Typeface.NORMAL);
        viewHolder.languages.setTypeface(null, Typeface.NORMAL);
        viewHolder.velocity.setTypeface(null, Typeface.NORMAL);
        viewHolder.lettersType.setTypeface(null, Typeface.NORMAL);
        viewHolder.lastUsed.setTypeface(null, Typeface.NORMAL);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Game game = mList.get(position);

        holder.name.setText(game.getName());
        holder.velocity.setText(game.getVelocityString());
        holder.lettersType.setText(game.getLettersTypeString());

        if(game.getLastUsed() != null){
            holder.lastUsed.setText(DateFormat.getDateInstance().format(game.getLastUsed()));
        }else{
            holder.lastUsed.setText("-");
        }

        if(game.getTime() == 0){
            holder.time.setText(R.string.infinite);
        }else{
            holder.time.setText(game.getTime());
        }
        if(Utils.isNull(game.getLanguagesString())){
            holder.languages.setText(R.string.all);
        }else{
            holder.languages.setText(game.getLanguagesString());
        }
        if(!game.isCustom()){
            holder.delete.setVisibility(View.VISIBLE);
            holder.delete.setOnClickListener((View v) -> {
                RealmManager.getInstance(mActivity).removeGame(game.getId());
                mList.remove(position);
                notifyDataSetChanged();
            });
        }else{
            holder.delete.setVisibility(View.INVISIBLE);
        }

        holder.itemView.setOnClickListener(v -> mActivity.select(game.getId()));
    }

    @Override
    public int getItemCount() {
        return mList != null ? mList.size() : 0;
    }
}
