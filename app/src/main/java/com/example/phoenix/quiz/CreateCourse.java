package com.example.phoenix.quiz;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.phoenix.quiz.JSON.JSONParser;
import com.example.phoenix.quiz.User_Information.User;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CreateCourse extends AppCompatActivity {

    private int numOfQuestions = 0;
    private Button next;
    private TextView alert;
    private EditText editNumOfQues;
    private EditText editCourseName;

    // Progress Dialog
    private ProgressDialog pDialog;

    JSONParser jsonParser = new JSONParser();

    // url to create new product
    private static String url_create_product = "https://heavy-theories.000webhostapp.com/create_course.php";

    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_COURSENAME = "courseName";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_course);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("New Course");

        editNumOfQues = findViewById(R.id.numOfQuestions);
        editCourseName = findViewById(R.id.courseName);
        next = findViewById(R.id.btnNext);
        alert = findViewById(R.id.alertNum);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!editNumOfQues.getText().toString().matches(""))
                    numOfQuestions = Integer.parseInt(editNumOfQues.getText().toString().trim());
                else
                    numOfQuestions = 0;

                if(numOfQuestions > 0 && numOfQuestions <= 100){
                    // creating new product in background thread
                    new CreateNewCourse().execute();
                } else {
                    alert.setVisibility(View.VISIBLE);
                    numOfQuestions = 0;
                }
            }
        });
    }


    class CreateNewCourse extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(CreateCourse.this);
            pDialog.setMessage("Creating Course...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        /**
         * Creating course
         * */
        protected String doInBackground(String... args) {
            String courseName = editCourseName.getText().toString();
            String numOfQuestions = editNumOfQues.getText().toString();
            String userPhone = User.userPhone;

            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("courseName", courseName));
            params.add(new BasicNameValuePair("userPhone", userPhone));
            params.add(new BasicNameValuePair("numOfQuestions", numOfQuestions));

            // getting JSON Object
            JSONObject json = jsonParser.makeHttpRequest(url_create_product,
                    "POST", params);

            // check log cat for response
            Log.d("Create Response", json.toString());

            // check for success tag
            try {
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    Intent i = new Intent(getApplicationContext(), MakeQuestions.class);
                    i.putExtra(TAG_COURSENAME,courseName);
                    startActivity(i);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog once done
            pDialog.dismiss();
        }

    }
}
