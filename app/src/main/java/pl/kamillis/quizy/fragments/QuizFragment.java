package pl.kamillis.quizy.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;

import java.util.Iterator;

import butterknife.Bind;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;
import pl.kamillis.quizy.R;
import pl.kamillis.quizy.models.Question;
import pl.kamillis.quizy.models.Quiz;
import pl.kamillis.quizy.utils.ResponseHandler;
import pl.kamillis.quizy.utils.RestClient;

public class QuizFragment extends Fragment {

    private Quiz quiz;
    private Iterator<Question> questionIterator;
    private int questionNumber = 0;
    private int correctAnswers = 0;
    private QuizListener listener;

    @Bind(R.id.quizStatus) TextView quizStatus;
    @Bind(R.id.quizContainer) RelativeLayout quizContainer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_quiz, container, false);
        ButterKnife.bind(this, view);

        if (savedInstanceState != null) {
            quiz = (Quiz)savedInstanceState.getSerializable("quiz");
            questionNumber = savedInstanceState.getInt("questionNumber");
            correctAnswers = savedInstanceState.getInt("correctAnswers");
            questionIterator = quiz.getQuestions().listIterator(questionNumber);
        } else {
            Bundle arguments = getArguments();
            if (arguments != null && arguments.getInt("id", 0) > 0) {
                getQuiz(arguments.getInt("id"));
            }
        }

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putSerializable("quiz", quiz);
        outState.putInt("questionNumber", questionNumber);
        outState.putInt("correctAnswers", correctAnswers);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        listener = (QuizListener)context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    public Quiz getQuiz() {
        return quiz;
    }

    public void restartQuiz() {
        questionNumber = 0;
        correctAnswers = 0;
        questionIterator = quiz.getQuestions().listIterator();
        onQuizStarted();
    }

    public void onQuizStarted() {
        Question firstQuestion = questionIterator.next();

        Bundle bundle = new Bundle();
        bundle.putInt("questionNumber", ++questionNumber);
        bundle.putInt("noQuestions", quiz.getNoQuestions());
        bundle.putSerializable("question", firstQuestion);

        Fragment fragment = new QuestionFragment();
        fragment.setArguments(bundle);

        replaceFragment(fragment);
    }

    public void onQuestionAnswered(boolean isCorrect) {
        if (isCorrect) ++correctAnswers;
        if (questionIterator.hasNext()) {
            Question question = questionIterator.next();

            Bundle bundle = new Bundle();
            bundle.putInt("questionNumber", ++questionNumber);
            bundle.putInt("noQuestions", quiz.getNoQuestions());
            bundle.putSerializable("question", question);

            Fragment fragment = new QuestionFragment();
            fragment.setArguments(bundle);

            replaceFragment(fragment);
        } else {
            saveResults();
        }
    }

    private void onQuizFinished(double score) {
        Bundle bundle = new Bundle();
        bundle.putDouble("score", score);

        Fragment fragment = new SummaryFragment();
        fragment.setArguments(bundle);

        replaceFragment(fragment);
    }

    private void saveResults() {
        quizStatus.setText(R.string.loading);
        quizStatus.setVisibility(View.VISIBLE);
        quizContainer.setVisibility(View.GONE);

        final double score = 100.0 * correctAnswers / quiz.getNoQuestions();
        SaveResultsRequest req = new SaveResultsRequest(score);
        String url = "quiz/" + quiz.getId() + "/score";

        RestClient.post(getContext(), url, req, new ResponseHandler(getContext()) {

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                Gson gson = new Gson();
                SaveResultsResponse res = gson.fromJson(responseString, SaveResultsResponse.class);

                if (res.success) {
                    quiz.setAvgScore(res.instance.getAvgScore());
                    quiz.setBestScore(res.instance.getBestScore());
                    quiz.setCounter(res.instance.getCounter());
                }

                onQuizFinished(score);
                quizStatus.setVisibility(View.GONE);
                quizContainer.setVisibility(View.VISIBLE);
            }

        });
    }

    private void getQuiz(int id) {
        quizStatus.setText(R.string.loading);
        quizStatus.setVisibility(View.VISIBLE);

        RestClient.get("quiz/" + id, new ResponseHandler(getContext()) {

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                Gson gson = new Gson();
                quiz = gson.fromJson(responseString, Quiz.class);
                questionIterator = quiz.getQuestions().listIterator();
                quizStatus.setVisibility(View.GONE);
                setMainFragment();
            }

        });
    }

    private void setMainFragment() {
        Fragment fragment = new QuizInfoFragment();
        getChildFragmentManager()
                .beginTransaction()
                .add(R.id.quizContainer, fragment)
                .commit();
    }

    private void replaceFragment(Fragment fragment) {
        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.quizContainer, fragment)
                .commit();
    }

    private class SaveResultsRequest {
        @Expose double score;
        public SaveResultsRequest(double score) {
            this.score = score;
        }
    }

    private class SaveResultsResponse {
        boolean success;
        Quiz instance;
    }

    public interface QuizListener {

    }

}
