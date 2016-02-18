package pl.kamillis.quizy.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.loopj.android.http.RequestParams;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;
import pl.kamillis.quizy.R;
import pl.kamillis.quizy.models.Quiz;
import pl.kamillis.quizy.utils.ResponseHandler;
import pl.kamillis.quizy.utils.RestClient;

public class QuizFragment extends Fragment {

    private Quiz quiz;
    private QuizListener listener;

    @Bind(R.id.quizStatus) TextView quizStatus;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_quiz, container, false);
        ButterKnife.bind(this, view);

        if (savedInstanceState != null) {
            quiz = (Quiz)savedInstanceState.getSerializable("quiz");
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

    public void onQuizStarted() {

    }

    public Quiz getQuiz() {
        return quiz;
    }

    private void getQuiz(int id) {
        quizStatus.setText(R.string.loading);
        quizStatus.setVisibility(View.VISIBLE);

        RestClient.get("quiz/" + id, new ResponseHandler(getContext()) {

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                Gson gson = new Gson();
                quiz = gson.fromJson(responseString, Quiz.class);
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

    public interface QuizListener {

    }

}
