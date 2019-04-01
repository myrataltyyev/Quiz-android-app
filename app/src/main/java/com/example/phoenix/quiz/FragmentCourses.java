package com.example.phoenix.quiz;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;

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

public class FragmentCourses extends ListFragment {

    // Creating JSON Parser object
    JSONParser jsonParser = new JSONParser();

    ArrayList<HashMap<String, String>> coursesList;

    // products JSONArray
    JSONArray coursesJSON = null;

    // URL
    private static final String url_courses_list = "https://heavy-theories.000webhostapp.com/courses_taken_by_student.php";

    // ALL JSON node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_COURSES = "courses";
    private static final String TAG_CID = "cid";
    private static final String TAG_COURSENAME = "courseName";
    private static final String TAG_NUMOFQUESTIONS = "numOfQuestions";

    private static final String TAG_USERNAME = "userName";
    private static final String TAG_PROFESSORNAME = "professorName";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Hashmap for ListView
        coursesList = new ArrayList<HashMap<String, String>>();

        // Loading Courses in Background Thread
        new LoadCourses().execute();

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_courses, container, false);
    }

    /**
     * Background Async Task to Load all Courses
     * */
    class LoadCourses extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected String doInBackground(String... args) {
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("userPhone", User.userPhone));

            // getting JSON string from URL
            JSONObject json = jsonParser.makeHttpRequest(url_courses_list, "GET", params);

            // Check your log cat for JSON reponse
            Log.d("All Courses: ", json.toString());

            try {
                // Checking for SUCCESS TAG
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // courses found
                    // Getting Array of courses
                    coursesJSON = json.getJSONArray(TAG_COURSES);

                    // looping through All courses
                    for (int i = 0; i < coursesJSON.length(); i++) {
                        JSONObject c = coursesJSON.getJSONObject(i);

                        // Storing each json item in variable
                        String cid = c.getString(TAG_CID);
                        String courseName = c.getString(TAG_COURSENAME);
                        String numOfQuestions = c.getString(TAG_NUMOFQUESTIONS);
                        String professorName = c.getString(TAG_USERNAME);

                        // creating new HashMap
                        HashMap<String, String> map = new HashMap<String, String>();

                        // adding each child node to HashMap key => value
                        map.put(TAG_CID, cid);
                        map.put(TAG_COURSENAME, courseName);
                        map.put(TAG_NUMOFQUESTIONS, numOfQuestions);
                        map.put(TAG_PROFESSORNAME, professorName);

                        // adding HashList to ArrayList
                        coursesList.add(map);
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

            // Fill ListView with Array of Hashmap
            ListAdapter listAdapter = new SimpleAdapter(getContext(), coursesList,
                    R.layout.list_item_courses, new String[]{TAG_COURSENAME, TAG_PROFESSORNAME, TAG_NUMOFQUESTIONS},
                    new int[]{R.id.txtCourseName, R.id.txtProfessorName, R.id.txtNumOfQuestions});
            setListAdapter(listAdapter);

            // Set on item click listener
            getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent i = new Intent(getContext(), SelectAnswers.class);
                    i.putExtra(TAG_CID, coursesList.get(position).get(TAG_CID));
                    i.putExtra(TAG_NUMOFQUESTIONS, coursesList.get(position).get(TAG_NUMOFQUESTIONS));
                    startActivity(i);
                }
            });
        }
    }
}
