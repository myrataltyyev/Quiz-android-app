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
import android.widget.ListView;

import com.example.phoenix.quiz.Adapter.DataAdapter;
import com.example.phoenix.quiz.JSON.JSONParser;
import com.example.phoenix.quiz.User_Information.User;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ResultDetails extends AppCompatActivity {

    // Creating JSON Parser object
    JSONParser jsonParser = new JSONParser();

    // List of courses for ListView
    ArrayList<HashMap<String, String>> resultsList;

    // url to get all products list
    private static String url_result_details = "https://heavy-theories.000webhostapp.com/result_details.php";

    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_CID = "cid";
    private static final String TAG_USERPHONE = "userPhone";
    private static final String TAG_RESULTS = "results";
    private static final String TAG_QUESTION = "question";
    private static final String TAG_CHOICE = "choice";
    private static final String TAG_ISRIGHT = "is_right";

    // courses JSONArray
    JSONArray resultsJSONArr = null;

    private ListView listView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_details);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Result Details");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), HomeStudent.class));
            }
        });

        listView = findViewById(R.id.listView);

        // Hashmap for ListView
        resultsList = new ArrayList<HashMap<String, String>>();

        // Loading products in Background Thread
        new LoadResultDetails().execute();
    }

    /**
     * Background Async Task to Load all Courses
     * */
    class LoadResultDetails extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected String doInBackground(String... args) {
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair(TAG_USERPHONE, User.userPhone));
            params.add(new BasicNameValuePair(TAG_CID, getIntent().getStringExtra(TAG_CID)));

            // getting JSON string from URL
            JSONObject json = jsonParser.makeHttpRequest(url_result_details, "GET", params);

            // Check your log cat for JSON reponse
            Log.d("Result Details: ", json.toString());

            try {
                // Checking for SUCCESS TAG
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // courses found
                    // Getting Array of courses
                    resultsJSONArr = json.getJSONArray(TAG_RESULTS);

                    // looping through All courses
                    for (int i = 0; i < resultsJSONArr.length(); i++) {
                        JSONObject c = resultsJSONArr.getJSONObject(i);

                        // Storing each json item in variable
                        String question = c.getString(TAG_QUESTION);
                        String choice = c.getString(TAG_CHOICE);
                        String is_right = c.getString(TAG_ISRIGHT);

                        // creating new HashMap
                        HashMap<String, String> map = new HashMap<String, String>();

                        // adding each child node to HashMap key => value
                        map.put(TAG_QUESTION, question);
                        map.put(TAG_CHOICE, choice);
                        map.put(TAG_ISRIGHT, is_right);

                        // adding HashList to ArrayList
                        resultsList.add(map);
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

            listView.setAdapter(new DataAdapter(getApplicationContext(), resultsList));
        }
    }
}
