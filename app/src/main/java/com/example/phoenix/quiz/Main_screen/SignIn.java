package com.example.phoenix.quiz.Main_screen;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.phoenix.quiz.HomeProfessor;
import com.example.phoenix.quiz.HomeStudent;
import com.example.phoenix.quiz.R;
import com.example.phoenix.quiz.SelectAnswers;
import com.example.phoenix.quiz.User_Information.User;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class SignIn extends AppCompatActivity {

    private Button btnSignIn;
    private MaterialEditText edtPhone, edtPassword;
    private Toolbar toolbar;
    private TextView viewSignUp;
    private String userPhone = "", password = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        edtPhone = findViewById(R.id.edtPhone);
        edtPassword = findViewById(R.id.edtPassword);

        btnSignIn = findViewById(R.id.btnSignIn);
        viewSignUp = findViewById(R.id.viewSignUp);
        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Sign In");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });

        viewSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SignIn.this, SignUp.class);
                startActivity(i);
                finish();
            }
        });


        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                userPhone = edtPhone.getText().toString().trim();
                password = edtPassword.getText().toString().trim();

                BackgroundTask backgroundTask = new BackgroundTask();
                backgroundTask.execute();
            }
        });
    }

    class BackgroundTask extends AsyncTask<String, Void, String> {

        String info_url;
        HttpURLConnection httpURLConnection;

        @Override
        protected void onPreExecute() {
            info_url = "https://heavy-theories.000webhostapp.com/login.php";
        }

        @Override
        protected String doInBackground(String... strings) {

            try {
                URL url = new URL(info_url);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream,"UTF-8"));

                String data_string = URLEncoder.encode("userPhone", "UTF-8") + "=" + URLEncoder.encode(userPhone,"UTF-8") + "&" +
                        URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(password,"UTF-8");

                bufferedWriter.write(data_string);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();

                int response_code = httpURLConnection.getResponseCode();

                if(response_code == HttpURLConnection.HTTP_OK) {
                    InputStream inputStream = httpURLConnection.getInputStream();
                    BufferedReader bufferedReadereader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder response = new StringBuilder();
                    String line;

                    while ((line = bufferedReadereader.readLine()) != null) {
                        response.append(line);
                    }

                    bufferedReadereader.close();
                    inputStream.close();

                    return (response.toString());
                } else
                    return ("unsuccessfull");

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                httpURLConnection.disconnect();
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String result) {

            String message = result.toString();
            int indexOfName1 = message.indexOf("(") + 1;
            int indexOfName2 = message.indexOf(")");
            int indexOfUserType = message.indexOf("=") + 1;
            int indexOfUserPhone = message.lastIndexOf("=") + 1;

            // Display success or failure message
            if (message.contains("Succeded")) {
                Toast.makeText(SignIn.this, "Login succeded", Toast.LENGTH_SHORT).show();

                // Save the user
                User.userPhone = message.substring(indexOfUserPhone);
                User.userName = message.substring(indexOfName1, indexOfName2);
                User.userType = message.substring(indexOfUserType);
            }
            else if (message.contains("Failed"))
                Toast.makeText(SignIn.this, "Invalid phone or password", Toast.LENGTH_SHORT).show();

            // Check userType and open corresponding activity
            if (message.contains("Professor"))
                startActivity(new Intent(SignIn.this, HomeProfessor.class));
            else if (message.contains("Student"))
                startActivity(new Intent(SignIn.this, HomeStudent.class));

        }
    }
}
