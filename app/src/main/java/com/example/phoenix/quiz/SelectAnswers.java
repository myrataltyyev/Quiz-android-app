package com.example.phoenix.quiz;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.phoenix.quiz.JSON.JSONParser;
import com.example.phoenix.quiz.User_Information.User;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class SelectAnswers extends AppCompatActivity {

    private Button btnNext;
    private TextView viewQuestion;
    private RadioGroup radioGroupAns;
    private RadioButton radioButton1, radioButton2, radioButton3, radioButton4;

    private Integer[] locationsArr = new Integer[] {0, 1, 2, 3};

    private String cid;
    private String numOfQuestions;
    private String question_id;
    private String strQuestion;
    private String[] choice_id = new String[4];
    private String[] choice = new String[4];
    private String[] is_right_choice = new String[4];
    private int question_num = 1;

    // Progress Dialog
    private ProgressDialog pDialog;

    JSONParser jsonParser = new JSONParser();

    // URLs
    private static String url_question_choices = "https://heavy-theories.000webhostapp.com/get_question_choices.php";
    private static String url_save_answers = "https://heavy-theories.000webhostapp.com/save_answers.php";

    // ALL JSON node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_CID = "cid";
    private static final String TAG_USERPHONE= "userPhone";
    private static final String TAG_NUMOFQUESTIONS = "numOfQuestions";
    private static final String TAG_QUESTIONNUMBER = "question_num";
    private static final String TAG_QUESTIONID = "question_id";
    private static final String TAG_ISRIGHTCHOICE = "is_right_choice";
    private static final String TAG_QUESTION = "question";
    private static final String TAG_CHOICEID = "choice_id";
    private static final String TAG_CHOICE = "choice";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_answers);

        radioGroupAns = findViewById(R.id.radioGroupAns);
        btnNext = findViewById(R.id.btnNext);
        viewQuestion = findViewById(R.id.question);
        radioButton1 = findViewById(R.id.btnAnswer1);
        radioButton2 = findViewById(R.id.btnAnswer2);
        radioButton3 = findViewById(R.id.btnAnswer3);
        radioButton4= findViewById(R.id.btnAnswer4);

        // Get extras from Intent
        Intent intent = getIntent();
        cid = intent.getStringExtra(TAG_CID);
        numOfQuestions = intent.getStringExtra(TAG_NUMOFQUESTIONS);

        // loading question and choices in background thread
        new LoadQuestionChoices().execute(String.valueOf(question_num));

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String choiceId = null, isRightChoice = null;
                
                if (radioGroupAns.getCheckedRadioButtonId() != -1) {
                    if (question_num <= Integer.parseInt(numOfQuestions)) {

                        switch (radioGroupAns.getCheckedRadioButtonId()) {
                            case R.id.btnAnswer1:
                                choiceId = choice_id[0];
                                isRightChoice = is_right_choice[0];
                                break;
                            case R.id.btnAnswer2:
                                choiceId = choice_id[1];
                                isRightChoice = is_right_choice[1];
                                break;
                            case R.id.btnAnswer3:
                                choiceId = choice_id[2];
                                isRightChoice = is_right_choice[2];
                                break;
                            case R.id.btnAnswer4:
                                choiceId = choice_id[3];
                                isRightChoice = is_right_choice[3];
                                break;
                        }

                        new SaveAnswers().execute(choiceId, isRightChoice);

                        if (question_num != Integer.parseInt(numOfQuestions)) {
                            radioGroupAns.clearCheck();
                            new LoadQuestionChoices().execute(String.valueOf(++question_num));
                        } else {
                            Toast.makeText(SelectAnswers.this, "Finished", Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(getApplicationContext(), HomeStudent.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        }
                    }
                } else
                    Toast.makeText(SelectAnswers.this, "Select one answer", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Background Async Task to create question in database
     * */
    class LoadQuestionChoices extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected String doInBackground(String... args) {
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair(TAG_CID, cid));
            params.add(new BasicNameValuePair(TAG_QUESTIONNUMBER, args[0]));

            // getting JSON Object
            JSONObject json = jsonParser.makeHttpRequest(url_question_choices,
                    "GET", params);

            // check log cat for response
            Log.d("Response", json.toString());

            // json success tag
            int success = 0;
            try {
                success = json.getInt(TAG_SUCCESS);
                if (success == 1) {
                    // successfully received details
                    JSONArray questionArr = json.getJSONArray("question");
                    JSONArray choicesArr = json.getJSONArray("choices");

                    // get first question object from JSON Array
                    JSONObject question = questionArr.getJSONObject(0);
                    JSONObject[] choices = new JSONObject[4];

                    // Question object values
                    question_id = question.getString(TAG_QUESTIONID);
                    strQuestion = question.getString(TAG_QUESTION);

                    // Shuffle locations array
                    Collections.shuffle(Arrays.asList(locationsArr));

                    // Choices object values
                    for (int j = 0; j < choices.length; j++) {
                        // get choices object from array
                        choices[j] = choicesArr.getJSONObject(locationsArr[j]);

                        // assign values to array variables by tags
                        choice_id[j] = choices[j].getString(TAG_CHOICEID);
                        choice[j] = choices[j].getString(TAG_CHOICE);
                        is_right_choice[j] = choices[j].getString(TAG_ISRIGHTCHOICE);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(String s) {
            viewQuestion.setText(strQuestion);
            radioButton1.setText(choice[0]);
            radioButton2.setText(choice[1]);
            radioButton3.setText(choice[2]);
            radioButton4.setText(choice[3]);
        }

    }


    /**
     * Background Async Task to create question in database
     * */
    class SaveAnswers extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected String doInBackground(String... args) {
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair(TAG_USERPHONE, User.userPhone));
            params.add(new BasicNameValuePair(TAG_CID, cid));
            params.add(new BasicNameValuePair(TAG_QUESTIONID, question_id));
            params.add(new BasicNameValuePair(TAG_CHOICEID, args[0]));
            params.add(new BasicNameValuePair("is_right", args[1]));

            // getting JSON Object
            JSONObject json = jsonParser.makeHttpRequest(url_save_answers,
                    "POST", params);

            // check log cat for response
            Log.d("Response", json.toString());

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }
}
