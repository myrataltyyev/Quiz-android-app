package com.example.phoenix.quiz;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.example.phoenix.quiz.JSON.JSONParser;
import com.example.phoenix.quiz.Main_screen.SignIn;
import com.example.phoenix.quiz.User_Information.User;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HomeProfessor extends AppCompatActivity {

    // Progress Dialog
    private ProgressDialog pDialog;

    // Creating JSON Parser object
    JSONParser jParser = new JSONParser();

    // List of courses for ListView
    ArrayList<HashMap<String, String>> coursesList;

    // url to get all products list
    private static String url_all_courses = "https://heavy-theories.000webhostapp.com/get_all_courses.php";
    private static String url_delete_course = "https://heavy-theories.000webhostapp.com/delete_course.php";

    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_COURSES = "courses";
    private static final String TAG_CID = "cid";
    private static final String TAG_COURSENAME = "courseName";
    private static final String TAG_NUMOFQUESTIONS = "numOfQuestions";

    // courses JSONArray
    JSONArray coursesArr = null;

    private Toolbar toolbar;
    RelativeLayout relativeLayout;
    ListAdapter adapter;
    ListView listView;
    View popupView;
    PopupWindow popupWindow;
    int mCurrentX, mCurrentY;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_professor);

        relativeLayout = findViewById(R.id.homeProfessor);
        toolbar = findViewById(R.id.toolbar);
        listView = findViewById(R.id.listView);

        // Hashmap for ListView
        coursesList = new ArrayList<>();

        // Loading products in Background Thread
        new LoadAllCourses().execute();

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Home");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), SignIn.class));
            }
        });

        // get list view and on seleting single course
        // launching edit questions Screen
        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent intent = new Intent(HomeProfessor.this, CourseQuestions.class);
                intent.putExtra(TAG_CID, coursesList.get(position).get(TAG_CID));
                startActivity(intent);
            }
        });


        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeProfessor.this, CreateCourse.class));
                finish();
            }
        });

        registerForContextMenu(listView);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        //menu.setHeaderTitle("Options");
        getMenuInflater().inflate(R.menu.floating_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        switch (item.getItemId()) {
            case R.id.mnu_details:
                showPopUpView(info.position);
                break;
            case R.id.mnu_delete:
                // Deleting products in Background Thread
                new DeleteCourse().execute(String.valueOf(info.position));
                break;
        }

        return true;
    }

    public void showPopUpView(int position) {

        LayoutInflater layoutInflater = (LayoutInflater) getBaseContext()
                .getSystemService(LAYOUT_INFLATER_SERVICE);
        popupView = layoutInflater.inflate(R.layout.popup_course_details, null);
        TextView courseName = popupView.findViewById(R.id.viewCourseName);
        TextView professorName = popupView.findViewById(R.id.viewProfessorName);
        TextView numberOfQuestions = popupView.findViewById(R.id.viewNumberOfQuestions);

        courseName.setText(coursesList.get(position).get(TAG_COURSENAME));
        professorName.setText(User.userName);
        numberOfQuestions.setText(coursesList.get(position).get(TAG_NUMOFQUESTIONS));

        popupWindow = new PopupWindow(popupView,
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

        Button close = popupView.findViewById(R.id.btnClose);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });

        mCurrentX = relativeLayout.getWidth() / 4;
        mCurrentY = relativeLayout.getHeight() / 3;

        popupWindow.showAtLocation(relativeLayout, Gravity.NO_GRAVITY, mCurrentX, mCurrentY);
    }

    class LoadAllCourses extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(HomeProfessor.this);
            pDialog.setMessage("Loading courses. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        /**
         * getting all courses from url
         * */
        protected String doInBackground(String... args) {
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("userPhone", User.userPhone));

            // getting JSON string from URL
            JSONObject json = jParser.makeHttpRequest(url_all_courses, "GET", params);

            // Check your log cat for JSON reponse
            Log.d("All Courses: ", json.toString());

            try {
                // Checking for SUCCESS TAG
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // courses found
                    // Getting Array of courses
                    coursesArr = json.getJSONArray(TAG_COURSES);

                    // looping through All courses
                    for (int i = 0; i < coursesArr.length(); i++) {
                        JSONObject c = coursesArr.getJSONObject(i);

                        // Storing each json item in variable
                        String cid = c.getString(TAG_CID);
                        String courseName = c.getString(TAG_COURSENAME);
                        String numOfQuestions = c.getString(TAG_NUMOFQUESTIONS);

                        // creating new HashMap
                        HashMap<String, String> map = new HashMap<String, String>();

                        // adding each child node to HashMap key => value
                        map.put(TAG_CID, cid);
                        map.put(TAG_COURSENAME, courseName);
                        map.put(TAG_NUMOFQUESTIONS, numOfQuestions);

                        // adding HashList to ArrayList
                        coursesList.add(map);
                    }
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
            // dismiss the dialog after getting all courses
            pDialog.dismiss();
            // updating UI from Background Thread
            runOnUiThread(new Runnable() {
                public void run() {
                    /**
                     * Updating parsed JSON data into ListView
                     * */
                    adapter = new SimpleAdapter(
                            HomeProfessor.this, coursesList,
                            R.layout.list_item_professor, new String[] { TAG_CID,
                            TAG_COURSENAME },
                            new int[] { R.id.cid, R.id.courseName });
                    // updating listview
                    listView.setAdapter(adapter);
                }
            });
        }
    }

    class DeleteCourse extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected String doInBackground(String... args) {

            int position = Integer.parseInt(args[0]);
            String cid = coursesList.get(position).get(TAG_CID);

            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair(TAG_CID, cid));

            // getting JSON string from URL
            JSONObject json = jParser.makeHttpRequest(url_delete_course, "GET", params);

            // Check your log cat for JSON reponse
            Log.d("Response: ", json.toString());

            try {
                // Checking for SUCCESS TAG
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    coursesList.remove(position);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            ((BaseAdapter)adapter).notifyDataSetChanged();
        }
    }
}
