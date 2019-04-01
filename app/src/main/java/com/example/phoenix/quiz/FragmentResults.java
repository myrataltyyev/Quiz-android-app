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

public class FragmentResults extends ListFragment {

    // Creating JSON Parser object
    JSONParser jsonParser = new JSONParser();

    ArrayList<HashMap<String, String>> resultsList;

    // products JSONArray
    JSONArray resultsJSON = null;

    // URL
    private static final String url_course_results = "https://heavy-theories.000webhostapp.com/course_results.php";

    // ALL JSON node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_RESULTS = "results";
    private static final String TAG_CID = "cid";
    private static final String TAG_COURSENAME = "courseName";
    private static final String TAG_PROFESSORNAME = "professorName";
    private static final String TAG_POINTS = "points";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Hashmap for ListView
        resultsList = new ArrayList<HashMap<String, String>>();

        // Loading Courses in Background Thread
        new LoadResults().execute();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_results, container, false);
    }

    /**
     * Background Async Task to Load all Courses
     * */
    class LoadResults extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected String doInBackground(String... args) {
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("userPhone", User.userPhone));

            // getting JSON string from URL
            JSONObject json = jsonParser.makeHttpRequest(url_course_results, "GET", params);

            // Check your log cat for JSON reponse
            Log.d("Response: ", json.toString());

            try {
                // Checking for SUCCESS TAG
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // courses found
                    // Getting Array of courses
                    resultsJSON = json.getJSONArray(TAG_RESULTS);

                    // looping through All courses
                    for (int i = 0; i < resultsJSON.length(); i++) {
                        JSONObject c = resultsJSON.getJSONObject(i);

                        // Storing each json item in variable
                        String cid = c.getString(TAG_CID);
                        String courseName = c.getString(TAG_COURSENAME);
                        String professorName = c.getString(TAG_PROFESSORNAME);
                        String points = c.getString(TAG_POINTS);

                        // creating new HashMap
                        HashMap<String, String> map = new HashMap<String, String>();

                        // adding each child node to HashMap key => value
                        map.put(TAG_CID, cid);
                        map.put(TAG_COURSENAME, courseName);
                        map.put(TAG_PROFESSORNAME, professorName);
                        map.put(TAG_POINTS, points);

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

            // Fill ListView with Array of Hashmap
            setListAdapter(new SimpleAdapter(getActivity(), resultsList,
                    R.layout.list_item_results, new String[]{TAG_COURSENAME, TAG_PROFESSORNAME, TAG_POINTS},
                    new int[]{R.id.txtCourseName, R.id.txtProfessorName, R.id.txtPoints}));

            // Set on item click listener
            getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent i = new Intent(getContext(), ResultDetails.class);
                    i.putExtra(TAG_CID, resultsList.get(position).get(TAG_CID));
                    startActivity(i);
                }
            });
        }
    }
}
