package pl.kamillis.quizy.models;

import com.google.gson.annotations.Expose;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Kamil on 19.12.2015.
 */
public class Question implements Serializable {

    @Expose private String text;
    @Expose private List<Option> options;

    public Question() {
        options = new ArrayList<>();
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<Option> getOptions() {
        return options;
    }

    public void addOption(Option option) {
        this.options.add(option);
    }

    public void addOptions(Collection<Option> options) {
        this.options.addAll(options);
    }

}
