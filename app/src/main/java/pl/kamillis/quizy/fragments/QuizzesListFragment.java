package pl.kamillis.quizy.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;
import com.loopj.android.http.RequestParams;

import java.util.List;

import cz.msebera.android.httpclient.Header;

import butterknife.Bind;
import butterknife.ButterKnife;
import pl.kamillis.quizy.R;
import pl.kamillis.quizy.adapters.QuizzesListAdapter;
import pl.kamillis.quizy.models.Quiz;
import pl.kamillis.quizy.utils.ResponseHandler;
import pl.kamillis.quizy.utils.RestClient;

public class QuizzesListFragment extends Fragment {

    private int offset;
    private String order;
    private String tag;
    private boolean isAllLoaded;
    private volatile boolean isLoading;
    private QuizzesListAdapter listAdapter;
    private QuizzesListListener listener;
    private static final int BREAKPOINT = 5;
    private static final int LIMIT = 15;

    @Bind(R.id.sortTypesSpinner) Spinner sortTypesSpinner;
    @Bind(R.id.quizzesListView) ListView quizzesListView;
    @Bind(R.id.quizzesListStatus) TextView quizzesListStatus;
    @Bind(R.id.tagFilterEditText) EditText tagFilterEditText;
    @Bind(R.id.tagFilterButton) Button tagFilterButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_quizzes_list, container, false);
        ButterKnife.bind(this, view);

        offset = 0;
        order = null;
        tag = null;
        isAllLoaded = false;
        isLoading = false;

        if (savedInstanceState != null) {
            order = savedInstanceState.getString("order");
            tag = savedInstanceState.getString("tag");
        } else {
            Bundle arguments = getArguments();
            if (arguments != null && arguments.getString("tag") != null) {
                tag = arguments.getString("tag");
                tagFilterEditText.setText(tag);
            }
        }

        // Init tag filter
        initTagFilter();

        // Init sort types spinner
        initSortTypesSpinner();

        // Init list view
        initListView(inflater);

        // Get first part of list
        getQuizzesList();

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString("order", (order != null && order.isEmpty()) ? null : order);
        outState.putString("tag", tag);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        listener = (QuizzesListListener)context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    private void initTagFilter() {
        tagFilterButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                tag = tagFilterEditText.getText().toString();
                clearListView();
                getQuizzesList();
            }

        });
    }

    private void initSortTypesSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.sort_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortTypesSpinner.setAdapter(adapter);
        sortTypesSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String prevOrder = order;
                switch (position) {
                    case 1:
                        order = "avgScoreASC";
                        break;
                    case 2:
                        order = "counterDESC";
                        break;
                    default:
                        order = "";
                }
                if (prevOrder != null) {
                    clearListView();
                    getQuizzesList();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // nothing to do
            }

        });
    }

    private void initListView(LayoutInflater inflater) {
        listAdapter = new QuizzesListAdapter(inflater);
        quizzesListView.setAdapter(listAdapter);
        quizzesListView.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                // nothing to do
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (offset > 0 && !isAllLoaded && !isLoading &&
                        (totalItemCount - visibleItemCount) <= (firstVisibleItem + BREAKPOINT)) {
                    getQuizzesList();
                }
            }

        });
        quizzesListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Quiz selectedQuiz = (Quiz)listAdapter.getItem(position);
                listener.onQuizSelected(selectedQuiz.getId());
            }

        });
    }

    private void clearListView() {
        listAdapter.removeItems();
        isAllLoaded = false;
        offset = 0;
    }

    private void getQuizzesList() {
        RequestParams params = new RequestParams();
        params.put("limit", LIMIT);
        params.put("offset", offset);

        if (order != null && !order.isEmpty())  params.put("order", order);
        if (tag != null && !tag.isEmpty())      params.put("tag", tag);

        isLoading = true;
        quizzesListStatus.setText(R.string.loading);
        quizzesListStatus.setVisibility(View.VISIBLE);

        RestClient.get("quiz/list", params, new ResponseHandler(getContext()) {

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                Gson gson = new Gson();
                QuizzesList list = gson.fromJson(responseString, QuizzesList.class);

                if (list.quizzes.size() == 0) {
                    isAllLoaded = true;
                    if (offset == 0) {
                        quizzesListStatus.setText(R.string.not_found_quiz);
                        isLoading = false;
                        return;
                    }
                }

                listAdapter.addItems(list.quizzes);
                quizzesListStatus.setVisibility(View.GONE);
                offset += list.quizzes.size();
                isLoading = false;
            }

            class QuizzesList {
                List<Quiz> quizzes;
            }

        });
    }

    public interface QuizzesListListener {
        void onQuizSelected(int id);
    }

}
