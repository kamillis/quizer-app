package pl.kamillis.quizy.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import pl.kamillis.quizy.R;
import pl.kamillis.quizy.models.Quiz;

/**
 * Created by Kamil on 08.01.2016.
 */
public class QuizzesListAdapter extends BaseAdapter {

    private List<Quiz> quizList;
    private LayoutInflater inflater;

    public QuizzesListAdapter(LayoutInflater inflater) {
        this.inflater = inflater;
        this.quizList = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return quizList.size();
    }

    @Override
    public Object getItem(int position) {
        return quizList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Quiz quiz = quizList.get(position);
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_quiz, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }

        holder.name.setText(quiz.getName());
        holder.counter.setText(quiz.getFormattedCounter());

        if (quiz.getAvgScore() != null) {
            holder.average.setText(quiz.getFormattedAvgScore());
        } else {
            holder.average.setText(R.string.none);
        }

        if (quiz.getBestScore() != null) {
            holder.best.setText(quiz.getFormattedBestScore());
        } else {
            holder.best.setText(R.string.none);
        }

        return convertView;
    }

    public void removeItems() {
        quizList.clear();
        notifyDataSetChanged();
    }

    public void addItem(Quiz quiz) {
        quizList.add(quiz);
        notifyDataSetChanged();
    }

    public void addItems(Collection<Quiz> quizzes) {
        quizList.addAll(quizzes);
        notifyDataSetChanged();
    }

    static class ViewHolder {
        @Bind(R.id.quizzesListName) TextView name;
        @Bind(R.id.quizzesListCounter) TextView counter;
        @Bind(R.id.quizzesListAvg) TextView average;
        @Bind(R.id.quizzesListBest) TextView best;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

}
