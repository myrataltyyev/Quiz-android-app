package com.example.phoenix.quiz;

import android.app.ProgressDialog;
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
import android.widget.Toast;

import com.example.phoenix.quiz.JSON.JSONParser;
import com.example.phoenix.quiz.User_Information.User;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MakeQuestions extends AppCompatActivity {

    private EditText choice1, choice2, choice3, choice4, question;
    private Button btnNext;
    private String cid, courseName, question_id;
    private int numOfQuestions, count = 1;
    private boolean right_choice;

    // Progress Dialog
    private ProgressDialog pDialog;

    JSONParser jsonParser = new JSONParser();

    // URLs
    private static String url_cid = "https://heavy-theories.000webhostapp.com/get_course_id.php";
    private static String url_question = "https://heavy-theories.000webhostapp.com/create_question.php";
    private static String url_choices = "https://heavy-theories.000webhostapp.com/create_choices.php";
    private static String url_question_id = "https://heavy-theories.000webhostapp.com/get_question_id.php";

    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_COURSENAME = "courseName";
    private static final String TAG_USERPHONE = "userPhone";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_questions);

        question = findViewById(R.id.question);
        choice1 = findViewById(R.id.choice1);
        choice2 = findViewById(R.id.choice2);
        choice3 = findViewById(R.id.choice3);
        choice4 = findViewById(R.id.choice4);
        btnNext = findViewById(R.id.btnNext);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Question and choices");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), CreateCourse.class));
            }
        });

        // getting Course name from intent
        Intent i = getIntent();
        courseName = i.getStringExtra(TAG_COURSENAME);
        courseName = "\"" + courseName + "\"";

        // if course is newly created get cid
        new GetCID().execute();

        question.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!s.toString().startsWith(String.valueOf(count + "."))) {
                    question.setText(String.valueOf(count + "."));
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
                    choice1.setText("c) ");
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
                    choice2.setText("w) ");
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
                    choice3.setText("w) ");
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
                    choice4.setText("w) ");
                    Selection.setSelection(choice4.getText(), choice4.getText().length());
                }
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            Boolean emptyQuestionField = question.getText().toString().equals("");
            Boolean emptyCorrectAnswer = choice1.getText().toString().equals("");

            if (!emptyQuestionField && !emptyCorrectAnswer) {
                if (count <= numOfQuestions) {
                    // creating question in background thread
                    new CreateNewQuestion().execute();

                    // creating answers in background thread
                    String choice_1 = (choice1.getText().toString()).substring(3);
                    String choice_2 = (choice2.getText().toString()).substring(3);
                    String choice_3 = (choice3.getText().toString()).substring(3);
                    String choice_4 = (choice4.getText().toString()).substring(3);

                    for (int i = 1; i <= 4; i++) {
                        switch (i) {
                            case 1:
                                right_choice = true;
                                new CreateNewChoices().execute(choice_1, right_choice + "");
                                break;
                            case 2:
                                right_choice = false;
                                new CreateNewChoices().execute(choice_2, right_choice + "");
                                break;
                            case 3:
                                right_choice = false;
                                new CreateNewChoices().execute(choice_3, right_choice + "");
                                break;
                            case 4:
                                right_choice = false;
                                new CreateNewChoices().execute(choice_4, right_choice + "");
                                break;
                        }
                    }

                    if (count != numOfQuestions) {
                        choice1.setText("");
                        choice2.setText("");
                        choice3.setText("");
                        choice4.setText("");
                    } else {
                        Toast.makeText(MakeQuestions.this, "Finished", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(getApplicationContext(), HomeProfessor.class);
                        startActivity(intent);
                    }
                }
            } else if (emptyQuestionField)
                Toast.makeText(MakeQuestions.this,
                        "Don't leave question field empty", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(MakeQuestions.this,
                        "Don't leave correct answer field empty", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Background Async Task to create question in database
     * */
    class CreateNewQuestion extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MakeQuestions.this);
            pDialog.setMessage("Adding Question..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        protected String doInBackground(String... args) {
            String ques = question.getText().toString();

            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("cid", cid));
            params.add(new BasicNameValuePair("question_num", String.valueOf(ques.charAt(0))));
            params.add(new BasicNameValuePair("question", ques));

            // getting JSON Object
            JSONObject jsonPost = jsonParser.makeHttpRequest(url_question,
                    "POST", params);

            // check log cat for response
            Log.d("Response", jsonPost.toString());

            // remove last question and add it surrounding with quotation marks
            params.remove(params.size() - 1);
            ques = "\"" + ques + "\"";
            params.add(new BasicNameValuePair("question", ques));

            // getting JSON Object
            JSONObject jsonGet = jsonParser.makeHttpRequest(url_question_id,
                    "GET", params);

            // check log cat for response
            Log.d("Response", jsonGet.toString());

            // json success tag
            int success = 0;
            try {
                success = jsonGet.getInt(TAG_SUCCESS);
                if (success == 1) {
                    // successfully received question details
                    JSONArray questionArr = jsonGet
                            .getJSONArray("question"); // JSON Array

                    // get first question object from JSON Array
                    JSONObject question = questionArr.getJSONObject(0);

                    question_id = question.getString("question_id");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(String file_url) {
            // dismiss the dialog once done
            pDialog.dismiss();
            if (count != numOfQuestions)
                question.setText(String.valueOf(++count));
        }

    }

    /**
     * Background Async Task to create choices in database
     * */
    class CreateNewChoices extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected String doInBackground(String... args) {

            String choice = args[0];
            String right_choice = args[1];
            String is_right_choice = (right_choice.equals("true") ? 1 : 0) + "";

            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("question_id", question_id));
            params.add(new BasicNameValuePair("choice", choice));
            params.add(new BasicNameValuePair("is_right_choice", is_right_choice));

            // getting JSON Object
            JSONObject json = jsonParser.makeHttpRequest(url_choices,
                    "POST", params);

            // check log cat for response
            Log.d("Response", json.toString());

            return null;
        }

        protected void onPostExecute(String file_url) {
            // dismiss the dialog once done
            pDialog.dismiss();
        }

    }

    /**
     * Background Async Task to get course ID
     * */
    class GetCID extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {

            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair(TAG_COURSENAME, courseName));
            params.add(new BasicNameValuePair(TAG_USERPHONE, User.userPhone));

            // getting JSON string from URL
            JSONObject json = jsonParser.makeHttpRequest(url_cid, "GET", params);

            // Check your log cat for JSON reponse
            Log.d("Course ID: ", json.toString());

            try {
                // Checking for SUCCESS TAG
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // successfully received course details
                    JSONArray courseArr = json
                            .getJSONArray("course"); // JSON Array

                    // get first course object from JSON Array
                    JSONObject course = courseArr.getJSONObject(0);

                    cid = course.getString("cid");
                    numOfQuestions = Integer.parseInt(course.getString("numOfQuestions"));
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
