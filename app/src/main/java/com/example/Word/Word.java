package com.example.Word;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Word {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "english_word")
    private String word;
    @ColumnInfo(name = "chinese_meaning")
    private String chineseMeaning;
    @ColumnInfo(name = "chinese_invisiable")
    private boolean chinese_invisiable;

    public boolean isChinese_invisiable() {
        return chinese_invisiable;
    }

    public void setChinese_invisiable(boolean chinese_invisiable) {
        this.chinese_invisiable = chinese_invisiable;
    }
//    @ColumnInfo(name = "foo_data")
//    private boolean foo;
//    @ColumnInfo(name = "bar_data")
//    private boolean bar;
//
//    public boolean isBar() {
//        return bar;
//    }
//
//    public void setBar(boolean bar) {
//        this.bar = bar;
//    }
//
//    public boolean isFoo() {
//        return foo;
//    }
//
//    public void setFoo(boolean foo) {
//        this.foo = foo;
//    }

    public Word(String word, String chineseMeaning) {
        this.word = word;
        this.chineseMeaning = chineseMeaning;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getChineseMeaning() {
        return chineseMeaning;
    }

    public void setChineseMeaning(String chineseMeaning) {
        this.chineseMeaning = chineseMeaning;
    }
}
