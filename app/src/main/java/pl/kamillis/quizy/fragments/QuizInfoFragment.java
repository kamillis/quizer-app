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

public class QuizInfoFragment extends Fragment {

    private QuizFragment quizFragment;

    @Bind(R.id.quizInfoName) TextView quizInfoName;
    @Bind(R.id.quizInfoAvg) TextView quizInfoAvg;
    @Bind(R.id.quizInfoBest) TextView quizInfoBest;
    @Bind(R.id.quizInfoCounter) TextView quizInfoCounter;
    @Bind(R.id.quizInfoNoQuestions) TextView quizInfoNoQuestions;
    @Bind(R.id.quizInfoTimeLimit) TextView quizInfoTimeLimit;
    @Bind(R.id.quizStartButton) Button quizStartButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_quiz_info, container, false);
        ButterKnife.bind(this, view);

        Quiz quiz = quizFragment.getQuiz();
        quizInfoName.setText(quiz.getName());
        quizInfoCounter.setText(quiz.getFormattedCounter());
        quizInfoNoQuestions.setText(quiz.getFormattedNoQuestions());
        quizInfoTimeLimit.setText(quiz.getFormattedTimeLimit());

        if (quiz.getAvgScore() != null) {
            quizInfoAvg.setText(quiz.getFormattedAvgScore());
        } else {
            quizInfoAvg.setText(R.string.none);
        }

        if (quiz.getBestScore() != null) {
            quizInfoBest.setText(quiz.getFormattedBestScore());
        } else {
            quizInfoBest.setText(R.string.none);
        }

        quizStartButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                quizFragment.onQuizStarted();
            }

        });

        return view;
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
