package com.example.phoenix.quiz;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

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

public class EditQuestions extends AppCompatActivity {

    private EditText choice1, choice2, choice3, choice4, question;
    private Button btnSave;

    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_CID = "cid";
    private static final String TAG_QUESTION = "question";
    private static final String TAG_QUESTIONID = "question_id";
    private static final String TAG_QUESTIONNUMBER = "question_num";

    private static final String TAG_CHOICE = "choice";
    private static final String TAG_CHOICE_0 = "choice_0";
    private static final String TAG_CHOICE_1 = "choice_1";
    private static final String TAG_CHOICE_2 = "choice_2";
    private static final String TAG_CHOICE_3 = "choice_3";

    private static final String TAG_CHOICEID = "choice_id";
    private static final String TAG_CHOICEID_0 = "choice_id_0";
    private static final String TAG_CHOICEID_1 = "choice_id_1";
    private static final String TAG_CHOICEID_2 = "choice_id_2";
    private static final String TAG_CHOICEID_3 = "choice_id_3";

    // URLs
    private static String url_question_choices = "https://heavy-theories.000webhostapp.com/get_question_choices.php";
    private static String url_update = "https://heavy-theories.000webhostapp.com/update_question_answers.php";

    private String cid;
    private String questionNum;
    private String question_id;
    private String strQuestion;
    private String[] choice_id = new String[4];
    private String[] choice = new String[4];

    JSONParser jsonParser = new JSONParser();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_questions);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Question and choices");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CourseQuestions.class);
                intent.putExtra(TAG_CID, cid);
                startActivity(intent);
            }
        });

        question = findViewById(R.id.question);
        choice1 = findViewById(R.id.choice1);
        choice2 = findViewById(R.id.choice2);
        choice3 = findViewById(R.id.choice3);
        choice4 = findViewById(R.id.choice4);
        btnSave = findViewById(R.id.btnNext);
        btnSave.setText("Save");

        // get Extras from intent
        Intent intent = getIntent();
        cid = intent.getStringExtra(TAG_CID);
        questionNum = intent.getStringExtra(TAG_QUESTIONNUMBER);

        // Loading products in Background Thread
        new LoadQuestionChoices().execute();

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                strQuestion = "\"" + question.getText() + "\"";
                choice[0] = "'" + (choice1.getText().toString()).substring(3) + "'";
                choice[1] = "'" + (choice2.getText().toString()).substring(3) + "'";
                choice[2] = "'" + (choice3.getText().toString()).substring(3) + "'";
                choice[3] = "'" + (choice4.getText().toString()).substring(3) + "'";

                // Saving products in Background Thread
                new SaveAnswers().execute();
            }
        });

        question.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!s.toString().startsWith(questionNum + ".")) {
                    question.setText(questionNum + ".");
                    Selection.setSelection(question.getText(), question.getText().length());
                }
            }
        });

        choice1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!s.toString().startsWith("c) ")) {
                    String text = "c) " + choice[0];
                    choice1.setText(text);
                    Selection.setSelection(choice1.getText(), choice1.getText().length());
                }
            }
        });

        choice2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!s.toString().startsWith("w) ")) {
                    String text = "w) " + choice[1];
                    choice2.setText(text);
                    Selection.setSelection(choice2.getText(), choice2.getText().length());
                }
            }
        });

        choice3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!s.toString().startsWith("w) ")) {
                    String text = "w) " + choice[2];
                    choice3.setText(text);
                    Selection.setSelection(choice3.getText(), choice3.getText().length());
                }
            }
        });

        choice4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!s.toString().startsWith("w) ")) {
                    String text = "w) " + choice[3];
                    choice4.setText(text);
                    Selection.setSelection(choice4.getText(), choice4.getText().length());
                }
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
            params.add(new BasicNameValuePair(TAG_QUESTIONNUMBER, questionNum));

            // getting JSON Object
            JSONObject json = jsonParser.makeHttpRequest(url_question_choices, "GET", params);

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

                    // Choices object values
                    for (int j = 0; j < choices.length; j++) {
                        // get choices object from array
                        choices[j] = choicesArr.getJSONObject(j);

                        // assign values to array variables by tags
                        choice_id[j] = choices[j].getString(TAG_CHOICEID);
                        choice[j] = choices[j].getString(TAG_CHOICE);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(String s) {
            question.setText(strQuestion);
            choice1.setText(choice[0]);
            choice2.setText(choice[1]);
            choice3.setText(choice[2]);
            choice4.setText(choice[3]);
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
            params.add(new BasicNameValuePair(TAG_QUESTIONID, question_id));
            params.add(new BasicNameValuePair(TAG_QUESTION, strQuestion));

            params.add(new BasicNameValuePair(TAG_CHOICEID_0, choice_id[0]));
            params.add(new BasicNameValuePair(TAG_CHOICEID_1, choice_id[1]));
            params.add(new BasicNameValuePair(TAG_CHOICEID_2, choice_id[2]));
            params.add(new BasicNameValuePair(TAG_CHOICEID_3, choice_id[3]));

            params.add(new BasicNameValuePair(TAG_CHOICE_0, choice[0]));
            params.add(new BasicNameValuePair(TAG_CHOICE_1, choice[1]));
            params.add(new BasicNameValuePair(TAG_CHOICE_2, choice[2]));
            params.add(new BasicNameValuePair(TAG_CHOICE_3, choice[3]));

            // getting JSON Object
            JSONObject json = jsonParser.makeHttpRequest(url_update,
                    "GET", params);

            // check log cat for response
            Log.d("Response", json.toString());

            // check json success tag
            try {
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // successfully updated
                    Intent intent = new Intent(getApplicationContext(), CourseQuestions.class);
                    intent.putExtra(TAG_CID, cid);
                    startActivity(intent);
                } else {
                    // failed to update product
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }
}
