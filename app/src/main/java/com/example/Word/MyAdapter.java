package com.example.Word;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    private List<Word> allWords = new ArrayList<>();
    private boolean useCardView;
    private WordViewModel wordViewModel;
    MyAdapter(boolean useCardView,WordViewModel wordViewModel) {
        this.useCardView = useCardView;
        this.wordViewModel= wordViewModel;
    }

    void setAllWords(List<Word> allWords) {
        this.allWords = allWords;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View itemView;
        if (useCardView) {
            itemView = layoutInflater.inflate(R.layout.cell_card_2,parent,false);
        } else {
            itemView = layoutInflater.inflate(R.layout.cell_normal_2,parent,false);
        }
       final MyViewHolder holder = new MyViewHolder(itemView);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("https://m.youdao.com/dict?le=eng&q=" + holder.textViewEnglish.getText());
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(uri);
                holder.itemView.getContext().startActivity(intent);
            }
        });
        holder.aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Word word=(Word)holder.itemView.getTag(R.id.word_holder);
                if(b){
                    holder.textViewChinese.setVisibility(View.GONE);
                    word.setChinese_invisiable(true);
                    wordViewModel.updateWords(word);
                }else {
                    holder.textViewChinese.setVisibility(View.VISIBLE);
                    word.setChinese_invisiable(false);
                    wordViewModel.updateWords(word);
                }
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        final Word word = allWords.get(position);
        holder.itemView.setTag(R.id.word_holder,word);
        holder.textViewNumber.setText(String.valueOf(position + 1));
        holder.textViewEnglish.setText(word.getWord());
        holder.textViewChinese.setText(word.getChineseMeaning());
        if(word.isChinese_invisiable()){
            holder.textViewChinese.setVisibility(View.GONE);
            holder.aSwitch.setChecked(true);
        }else {
            holder.textViewChinese.setVisibility(View.VISIBLE);
            holder.aSwitch.setChecked(false);
        }
    }

    @Override
    public int getItemCount() {
        return allWords.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView textViewNumber,textViewEnglish,textViewChinese;
        Switch aSwitch,bSwitch;
        MyViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewNumber = itemView.findViewById(R.id.textViewNumber);
            textViewEnglish = itemView.findViewById(R.id.textViewEnglish);
            textViewChinese = itemView.findViewById(R.id.textViewChinese);
            aSwitch = itemView.findViewById(R.id.switch3);

        }
    }
}
