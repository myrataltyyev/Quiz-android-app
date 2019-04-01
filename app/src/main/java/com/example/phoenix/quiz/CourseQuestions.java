package com.example.phoenix.quiz;

import android.app.ListActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import com.example.phoenix.quiz.JSON.JSONParser;
import com.example.phoenix.quiz.Main_screen.SignIn;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CourseQuestions extends AppCompatActivity {

    // Creating JSON Parser object
    JSONParser jsonParser = new JSONParser();

    // List of courses for ListView
    ArrayList<HashMap<String, String>> questionsList;

    // url to get all products list
    private static String url_questions = "https://heavy-theories.000webhostapp.com/get_course_questions.php";

    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_CID = "cid";
    private static final String TAG_QUESTIONS = "questions";
    private static final String TAG_QUESTION = "question";
    private static final String TAG_QUESTIONID = "question_id";
    private static final String TAG_QUESTIONNUMBER = "question_num";

    String cid;

    // courses JSONArray
    JSONArray questionsArr = null;

    private ListView listView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_questions);

        // Hashmap for ListView
        questionsList = new ArrayList<HashMap<String, String>>();

        Toolbar toolbar = findViewById(R.id.toolbar);
        listView = findViewById(R.id.listView);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Questions");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), HomeProfessor.class));
            }
        });

        // Get extras from Intent
        cid = getIntent().getStringExtra(TAG_CID);

        // Loading products in Background Thread
        new LoadQuestions().execute();

        // on question select
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String questionNum = String.valueOf(questionsList.get(position).get(TAG_QUESTION).charAt(0));

                Intent intent = new Intent(CourseQuestions.this, EditQuestions.class);
                intent.putExtra(TAG_CID, cid);
                intent.putExtra(TAG_QUESTIONNUMBER, questionNum);
                startActivity(intent);
            }
        });
    }

    /**
     * Background Async Task to Load all Courses
     * */
    class LoadQuestions extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected String doInBackground(String... args) {
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair(TAG_CID, cid));

            // getting JSON string from URL
            JSONObject json = jsonParser.makeHttpRequest(url_questions, "GET", params);

            // Check your log cat for JSON reponse
            Log.d("Questions: ", json.toString());

            try {
                // Checking for SUCCESS TAG
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // questions found
                    // Getting Array of questions
                    questionsArr = json.getJSONArray(TAG_QUESTIONS);

                    // looping through All courses
                    for (int i = 0; i < questionsArr.length(); i++) {
                        JSONObject c = questionsArr.getJSONObject(i);

                        // Storing each json item in variable
                        String question = c.getString(TAG_QUESTION);
                        String question_id = c.getString(TAG_QUESTIONID);

                        // creating new HashMap
                        HashMap<String, String> map = new HashMap<String, String>();

                        // adding each child node to HashMap key => value
                        map.put(TAG_QUESTION, question);
                        map.put(TAG_QUESTIONID, question_id);

                        // adding HashList to ArrayList
                        questionsList.add(map);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            runOnUiThread(new Runnable() {
                public void run() {
                    /**
                     * Updating parsed JSON data into ListView
                     * */
                    ListAdapter adapter = new SimpleAdapter(
                            CourseQuestions.this, questionsList,
                            R.layout.list_item_questions,
                            new String[] { TAG_QUESTION },
                            new int[] { R.id.txtQuestions });
                    // updating listview
                    listView.setAdapter(adapter);
                }
            });
        }
    }
}
