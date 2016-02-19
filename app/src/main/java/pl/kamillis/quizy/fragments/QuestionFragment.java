package pl.kamillis.quizy.fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import pl.kamillis.quizy.R;
import pl.kamillis.quizy.models.Option;
import pl.kamillis.quizy.models.Question;

public class QuestionFragment extends Fragment {

    private Question question;
    private int questionNumber;
    private int noQuestions;
    private int selectedButton;
    private boolean isCorrect;
    private QuizFragment quizFragment;

    @Bind(R.id.quizQuestionText) TextView quizQuestionText;
    @Bind(R.id.quizProgress) TextView quizProgress;
    @Bind(R.id.quizFirstOptionButton) Button quizFirstOptionButton;
    @Bind(R.id.quizSecondOptionButton) Button quizSecondOptionButton;
    @Bind(R.id.quizThirdOptionButton) Button quizThirdOptionButton;
    @Bind(R.id.quizFourthOptionButton) Button quizFourthOptionButton;
    @Bind(R.id.quizNextQuestion) Button quizNextQuestion;
    @Bind(R.id.quizQuestionTimerLay) FrameLayout quizQuestionTimerLay;
    @Bind(R.id.quizQuestionTimer) Chronometer quizQuestionTimer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_question, container, false);
        ButterKnife.bind(this, view);

        if (savedInstanceState != null) {
            question = (Question)savedInstanceState.getSerializable("question");
            questionNumber = savedInstanceState.getInt("questionNumber");
            noQuestions = savedInstanceState.getInt("noQuestions");
            selectedButton = savedInstanceState.getInt("selectedButton");
            isCorrect = savedInstanceState.getBoolean("isCorrect");
            selectAfterRotation();
        } else {
            Bundle arguments = getArguments();
            question = (Question)arguments.getSerializable("question");
            questionNumber = arguments.getInt("questionNumber");
            noQuestions = arguments.getInt("noQuestions");
        }

        quizQuestionText.setText(question.getText());
        quizProgress.setText(questionNumber + "/" + noQuestions);

        // Shuffle options
        List<Option> options = question.getOptions();
        Collections.shuffle(options);

        // Set options
        quizFirstOptionButton.setText(options.get(0).getText());
        quizFirstOptionButton.setOnClickListener(new OptionOnClickListener(options.get(0)));
        quizSecondOptionButton.setText(options.get(1).getText());
        quizSecondOptionButton.setOnClickListener(new OptionOnClickListener(options.get(1)));
        quizThirdOptionButton.setText(options.get(2).getText());
        quizThirdOptionButton.setOnClickListener(new OptionOnClickListener(options.get(2)));
        quizFourthOptionButton.setText(options.get(3).getText());
        quizFourthOptionButton.setOnClickListener(new OptionOnClickListener(options.get(3)));

        // Set on next button click listener
        quizNextQuestion.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                quizFragment.onQuestionAnswered(isCorrect);
                v.setEnabled(false);
            }

        });

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putSerializable("question", question);
        outState.putInt("questionNumber", questionNumber);
        outState.putInt("noQuestions", noQuestions);
        outState.putInt("selectedButton", selectedButton);
        outState.putBoolean("isCorrect", isCorrect);
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

    private void selectAfterRotation() {
        switch (selectedButton) {
            case -1:
                quizQuestionTimerLay.setBackgroundColor(Color.RED);
                afterAnswer();
                break;
            case R.id.quizFirstOptionButton:
                quizFirstOptionButton.setBackgroundColor(isCorrect ? Color.GREEN : Color.RED);
                afterAnswer();
                break;
            case R.id.quizSecondOptionButton:
                quizSecondOptionButton.setBackgroundColor(isCorrect ? Color.GREEN : Color.RED);
                afterAnswer();
                break;
            case R.id.quizThirdOptionButton:
                quizThirdOptionButton.setBackgroundColor(isCorrect ? Color.GREEN : Color.RED);
                afterAnswer();
                break;
            case R.id.quizFourthOptionButton:
                quizFourthOptionButton.setBackgroundColor(isCorrect ? Color.GREEN : Color.RED);
                afterAnswer();
                break;
        }
    }

    private void afterAnswer() {
        quizFirstOptionButton.setEnabled(false);
        quizSecondOptionButton.setEnabled(false);
        quizThirdOptionButton.setEnabled(false);
        quizFourthOptionButton.setEnabled(false);
        quizNextQuestion.setEnabled(true);
    }

    private class OptionOnClickListener implements View.OnClickListener {

        private Option option;

        public OptionOnClickListener(Option option) {
            this.option = option;
        }

        @Override
        public void onClick(View v) {
            if (option.isCorrect()) {
                v.setBackgroundColor(Color.GREEN);
                isCorrect = true;
            } else {
                v.setBackgroundColor(Color.RED);
                isCorrect = false;
            }
            quizQuestionTimer.stop();
            selectedButton = v.getId();
            afterAnswer();
        }

    }

}
