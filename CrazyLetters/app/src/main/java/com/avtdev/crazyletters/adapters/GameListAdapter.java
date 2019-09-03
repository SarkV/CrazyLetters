package com.avtdev.crazyletters.adapters;

import android.graphics.Typeface;
import android.view.Gravity;
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
import com.avtdev.crazyletters.utils.GameConstants;
import com.avtdev.crazyletters.utils.Utils;

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
        TextView accents;
        ImageView delete;

        ViewHolder(View view) {
            super(view);

            name = view.findViewById(R.id.tvName);
            time = view.findViewById(R.id.tvTime);
            languages = view.findViewById(R.id.tvLanguages);
            velocity = view.findViewById(R.id.tvVelocity);
            lettersType = view.findViewById(R.id.tvLettersType);
            accents = view.findViewById(R.id.tvAccent);
            delete = view.findViewById(R.id.ivDelete);
        }
    }

    public GameListAdapter(GameListActivity activity, List<Game> list) {
        this.mActivity = activity;
        this.mList = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View holder = LayoutInflater.from(parent.getContext()).inflate(R.layout.game_row, parent, false );

        ViewHolder viewHolder = new ViewHolder(holder);

        viewHolder.name.setTypeface(null, Typeface.NORMAL);
        viewHolder.name.setGravity(Gravity.LEFT);
        viewHolder.time.setTypeface(null, Typeface.NORMAL);
        viewHolder.languages.setTypeface(null, Typeface.NORMAL);
        viewHolder.velocity.setTypeface(null, Typeface.NORMAL);
        viewHolder.lettersType.setTypeface(null, Typeface.NORMAL);
        viewHolder.accents.setTypeface(null, Typeface.NORMAL);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Game game = mList.get(position);

        holder.name.setText(game.getName());
        game.getLettersType();
        Integer[] vel = game.getVelocity();
        if(vel != null && vel.length == 2){
            holder.velocity.setText(vel[0] == vel[1] ? String.valueOf(vel[0]) : (vel[0] + " - " + vel[1]));
        }else{
            holder.velocity.setText("");
        }
        GameConstants.LettersType[] lettersTypes = game.getLettersType();
        StringBuilder stringBuilder = new StringBuilder();
        if(lettersTypes != null){
            for(GameConstants.LettersType type: lettersTypes){
                if(stringBuilder.length() == 0){
                    stringBuilder.append(" ");
                }
                switch (type){
                    case HORIZONTAL_MOVE:
                        stringBuilder.append(mActivity.getText(R.string.horizontal_arrow));
                        break;
                    case VERTICAL_MOVE:
                        stringBuilder.append(mActivity.getText(R.string.vertical_arrow));
                        break;
                    case DIAGONAL_MOVE:
                        stringBuilder.append(mActivity.getText(R.string.diagonal_arrow));
                        break;
                    case SHOW_HIDE:
                        stringBuilder.append(mActivity.getText(R.string.show_hide));
                        break;
                }
            }
        }
        holder.lettersType.setText(stringBuilder.toString());
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
        holder.accents.setText(game.hasAccent() ? R.string.tick : R.string.cross);


        if(!game.isDefaultGame()){
            holder.delete.setVisibility(View.VISIBLE);
            holder.delete.setOnClickListener((View v) -> {
                mActivity.showTwoBtnDialog(R.string.warning, R.string.warning_remove_game, R.string.accept,
                        (dialog, which) -> {
                            RealmManager.getInstance(mActivity).removeGame(game.getId());
                            mList.remove(position);
                            notifyDataSetChanged();
                        },
                        R.string.cancel, (dialog, which) -> {dialog.dismiss();});
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
