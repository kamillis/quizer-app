package pl.kamillis.quizy.models;

import com.google.gson.annotations.Expose;

import java.io.Serializable;

/**
 * Created by Kamil on 19.12.2015.
 */
public class Option implements Serializable {

    @Expose private String text;
    @Expose private boolean isCorrect;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isCorrect() {
        return isCorrect;
    }

    public void setCorrect(boolean correct) {
        this.isCorrect = correct;
    }

}
