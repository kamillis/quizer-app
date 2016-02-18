package pl.kamillis.quizy.models;

import com.google.gson.annotations.Expose;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Kamil on 19.12.2015.
 */
public class Quiz implements Serializable {

    private int id;
    private int counter;
    private Double avgScore;
    private Double bestScore;
    @Expose private int timeLimit;
    @Expose private String name;
    @Expose private List<Tag> tags;
    @Expose private List<Question> questions;

    public Quiz() {
        tags = new ArrayList<>();
        questions = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getAvgScore() {
        return avgScore;
    }

    public String getFormattedAvgScore() {
        return avgScore != null ? String.format("%.2f%%", avgScore) : null;
    }

    public void setAvgScore(Double avgScore) {
        this.avgScore = avgScore;
    }

    public Double getBestScore() {
        return bestScore;
    }

    public String getFormattedBestScore() {
        return bestScore != null ? String.format("%.2f%%", bestScore) : null;
    }

    public void setBestScore(Double bestScore) {
        this.bestScore = bestScore;
    }

    public int getCounter() {
        return counter;
    }

    public String getFormattedCounter() {
        return String.format("%d", counter);
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }

    public int getTimeLimit() {
        return timeLimit;
    }

    public String getFormattedTimeLimit() {
        return String.format("%ds", timeLimit);
    }

    public void setTimeLimit(int timeLimit) {
        this.timeLimit = timeLimit;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public int getNoQuestions() {
        return questions.size();
    }

    public String getFormattedNoQuestions() {
        return String.format("%d", questions.size());
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public void addTag(Tag tag) {
        this.tags.add(tag);
    }

    public void addTags(Collection<Tag> tags) {
        this.tags.addAll(tags);
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    public void deleteQuestion(int i) {
        questions.remove(i);
    }

    public void addQuestion(Question question) {
        this.questions.add(question);
    }

    public void addQuestions(Collection<Question> questions) {
        this.questions.addAll(questions);
    }

}
