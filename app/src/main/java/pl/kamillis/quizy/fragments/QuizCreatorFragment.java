package pl.kamillis.quizy.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;
import pl.kamillis.quizy.R;
import pl.kamillis.quizy.models.Option;
import pl.kamillis.quizy.models.Question;
import pl.kamillis.quizy.models.Quiz;
import pl.kamillis.quizy.models.Tag;
import pl.kamillis.quizy.utils.ResponseHandler;
import pl.kamillis.quizy.utils.RestClient;

public class QuizCreatorFragment extends Fragment implements RadioButton.OnClickListener {

    private Quiz quiz;
    private QuizCreatorListener listener;

    @Bind(R.id.quizCreatorName) EditText quizCreatorName;
    @Bind(R.id.quizCreatorTags) EditText quizCreatorTags;
    @Bind(R.id.questionText) EditText questionText;
    @Bind(R.id.questionFirstOption) EditText questionFirstOption;
    @Bind(R.id.questionSecondOption) EditText questionSecondOption;
    @Bind(R.id.questionThirdOption) EditText questionThirdOption;
    @Bind(R.id.questionFourthOption) EditText questionFourthOption;
    @Bind(R.id.questionFirstOptionIsCorrect) RadioButton questionFirstOptionIsCorrect;
    @Bind(R.id.questionSecondOptionIsCorrect) RadioButton questionSecondOptionIsCorrect;
    @Bind(R.id.questionThirdOptionIsCorrect) RadioButton questionThirdOptionIsCorrect;
    @Bind(R.id.questionFourthOptionIsCorrect) RadioButton questionFourthOptionIsCorrect;
    @Bind(R.id.questionList) TextView questionList;
    @Bind(R.id.questionAddButton) Button questionAddButton;
    @Bind(R.id.questionDeleteButton) Button questionDeleteButton;
    @Bind(R.id.quizCreatorButton) Button quizCreatorButton;
    @Bind(R.id.quizCreatorTimeLimit) NumberPicker quizCreatorTimeLimit;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            quiz = (Quiz)savedInstanceState.getSerializable("quiz");
        } else {
            quiz = new Quiz();
            quiz.setTimeLimit(10);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_quiz_creator, container, false);
        ButterKnife.bind(this, view);

        if (savedInstanceState != null) {
            refreshQuestionList();
        }

        initTimeLimitPicker();
        initRadioButtons();

        questionAddButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (validateQuestionCreator()) {
                    quiz.addQuestion(createQuestion());
                    refreshQuestionList();
                    clearQuestionCreator();
                } else {
                    Toast.makeText(getContext(), R.string.empty_fields, Toast.LENGTH_LONG).show();
                }
            }

        });

        questionDeleteButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                ArrayList<String> questions = new ArrayList<>();
                for (Question question : quiz.getQuestions()) {
                    questions.add(question.getText());
                }

                final Spinner spinner = new Spinner(getContext());
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                        android.R.layout.simple_spinner_item, questions);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(adapter);

                AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                alert.setTitle(R.string.select_question);
                alert.setView(spinner);

                alert.setPositiveButton(R.string.question_delete, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int i = spinner.getSelectedItemPosition();
                        if (i < 0 ) return;
                        quiz.deleteQuestion(i);
                        refreshQuestionList();
                    }

                });

                alert.show();
            }

        });

        quizCreatorButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (validateQuizCreator()) {
                    quizCreatorButton.setEnabled(false);
                    quiz.setName(quizCreatorName.getText().toString());
                    quiz.setTags(parseTags());
                    RestClient.post(getContext(), "quiz", quiz, new ResponseHandler(getContext()) {

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, String responseString) {
                            Gson gson = new Gson();
                            Response res = gson.fromJson(responseString, Response.class);
                            if (res.success) {
                                listener.onQuizCreated(res.instance.getId());
                            } else {
                                onFailure(0, headers, responseString, null);
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                            super.onFailure(statusCode, headers, responseString, throwable);
                            quizCreatorButton.setEnabled(true);
                        }

                    });
                } else {
                    Toast.makeText(getContext(), R.string.empty_fields, Toast.LENGTH_LONG).show();
                }
            }

            class Response {
                boolean success;
                Quiz instance;
            }

        });

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
        listener = (QuizCreatorListener)context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    @Override
    public void onClick(View v) {
        questionFirstOptionIsCorrect.setChecked(false);
        questionSecondOptionIsCorrect.setChecked(false);
        questionThirdOptionIsCorrect.setChecked(false);
        questionFourthOptionIsCorrect.setChecked(false);
        switch(v.getId()) {
            case R.id.questionFirstOptionIsCorrect:
                questionFirstOptionIsCorrect.setChecked(true);
                break;
            case R.id.questionSecondOptionIsCorrect:
                questionSecondOptionIsCorrect.setChecked(true);
                break;
            case R.id.questionThirdOptionIsCorrect:
                questionThirdOptionIsCorrect.setChecked(true);
                break;
            case R.id.questionFourthOptionIsCorrect:
                questionFourthOptionIsCorrect.setChecked(true);
                break;
        }
    }

    private void initTimeLimitPicker() {
        quizCreatorTimeLimit.setMinValue(1);
        quizCreatorTimeLimit.setMaxValue(120);
        quizCreatorTimeLimit.setValue(quiz.getTimeLimit());
        quizCreatorTimeLimit.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {

            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                quiz.setTimeLimit(newVal);
            }

        });
    }

    private void initRadioButtons() {
        questionFirstOptionIsCorrect.setOnClickListener(this);
        questionSecondOptionIsCorrect.setOnClickListener(this);
        questionThirdOptionIsCorrect.setOnClickListener(this);
        questionFourthOptionIsCorrect.setOnClickListener(this);
    }

    private void clearRadioButtons() {
        questionFirstOptionIsCorrect.setChecked(true);
        questionSecondOptionIsCorrect.setChecked(false);
        questionThirdOptionIsCorrect.setChecked(false);
        questionFourthOptionIsCorrect.setChecked(false);
    }

    private void clearQuestionCreator() {
        clearRadioButtons();
        questionText.setText("");
        questionFirstOption.setText("");
        questionSecondOption.setText("");
        questionThirdOption.setText("");
        questionFourthOption.setText("");
    }

    private boolean validateQuizCreator() {
        return quiz.getNoQuestions() > 0 &&
                quizCreatorName.getText().length() > 0 &&
                quizCreatorTags.getText().length() > 0;
    }

    private boolean validateQuestionCreator() {
        return questionText.getText().length() > 0 &&
                questionFirstOption.getText().length() > 0 &&
                questionSecondOption.getText().length() > 0 &&
                questionThirdOption.getText().length() > 0 &&
                questionFourthOption.getText().length() > 0;
    }

    private void refreshQuestionList() {
        int i = 0;
        questionList.setText("");
        for (Question question : quiz.getQuestions()) {
            questionList.append("#" + (++i) + " " + question.getText() + "\n");
        }
    }

    private Question createQuestion() {
        Question question = new Question();
        question.setText(questionText.getText().toString());
        question.addOption(createOption(questionFirstOption.getText().toString(),
                questionFirstOptionIsCorrect.isChecked()));
        question.addOption(createOption(questionSecondOption.getText().toString(),
                questionSecondOptionIsCorrect.isChecked()));
        question.addOption(createOption(questionThirdOption.getText().toString(),
                questionThirdOptionIsCorrect.isChecked()));
        question.addOption(createOption(questionFourthOption.getText().toString(),
                questionFourthOptionIsCorrect.isChecked()));
        return question;
    }

    private Option createOption(String text, boolean isCorrect) {
        Option option = new Option();
        option.setText(text);
        option.setCorrect(isCorrect);
        return option;
    }

    private List<Tag> parseTags() {
        String tagsStr = quizCreatorTags.getText().toString();
        String[] tagsArr = tagsStr.split("\\s+");
        List<Tag> tagsList = new ArrayList<>();
        for (String tagStr : tagsArr) {
            Tag tag = new Tag();
            tag.setName(tagStr);
            tagsList.add(tag);
        }
        return tagsList;
    }

    public interface QuizCreatorListener {
        void onQuizCreated(int id);
    }

}
