package pl.kamillis.quizy.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import pl.kamillis.quizy.R;
import pl.kamillis.quizy.models.Quiz;

public class SummaryFragment extends Fragment {

    private double score;
    private QuizFragment quizFragment;

    @Bind(R.id.quizSummaryName) TextView quizSummaryName;
    @Bind(R.id.quizSummaryScore) TextView quizSummaryScore;
    @Bind(R.id.quizSummaryAvg) TextView quizSummaryAvg;
    @Bind(R.id.quizSummaryBest) TextView quizSummaryBest;
    @Bind(R.id.quizRestartButton) Button quizRestartButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_summary, container, false);
        ButterKnife.bind(this, view);

        if (savedInstanceState != null) {
            score = savedInstanceState.getDouble("score");
        } else {
            Bundle arguments = getArguments();
            score = arguments.getDouble("score");
        }

        Quiz quiz = quizFragment.getQuiz();
        quizSummaryName.setText(quiz.getName());
        quizSummaryScore.setText(String.format("%.2f%%", score));

        if (quiz.getAvgScore() != null) {
            quizSummaryAvg.setText(quiz.getFormattedAvgScore());
        } else {
            quizSummaryAvg.setText(R.string.none);
        }

        if (quiz.getBestScore() != null) {
            quizSummaryBest.setText(quiz.getFormattedBestScore());
        } else {
            quizSummaryBest.setText(R.string.none);
        }

        quizRestartButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                quizFragment.restartQuiz();
                quizRestartButton.setEnabled(false);
            }

        });

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putDouble("score", score);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        quizFragment = (QuizFragment)getParentFragment();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        quizFragment = null;
    }

}
