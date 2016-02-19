package pl.kamillis.quizy.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import pl.kamillis.quizy.R;

public class SummaryFragment extends Fragment {

    private QuizFragment quizFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_summary, container, false);
        ButterKnife.bind(this, view);


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
