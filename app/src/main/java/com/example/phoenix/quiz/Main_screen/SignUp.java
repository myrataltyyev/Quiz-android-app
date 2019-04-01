package com.example.phoenix.quiz.Main_screen;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.phoenix.quiz.R;
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

public class SignUp extends AppCompatActivity {

    private MaterialEditText edtPhone, edtName, edtPassword;
    private RadioButton rdbtnProfessor;
    private RadioGroup radioGroup;
    private Button btnSignUp;
    private TextView viewSignIn;
    private Toolbar toolbar;
    private String phone = "", name = "", password = "", userType = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        edtPhone = findViewById(R.id.edtPhone);
        edtName = findViewById(R.id.edtName);
        edtPassword = findViewById(R.id.edtPassword);

        btnSignUp = findViewById(R.id.btnSignUP);
        viewSignIn = findViewById(R.id.viewSignIn);
        toolbar = findViewById(R.id.toolbar);

        radioGroup = findViewById(R.id.radioGroup);
        rdbtnProfessor = findViewById(R.id.btnProfessor);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Sign Up");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });

        viewSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SignUp.this, SignIn.class);
                startActivity(i);
                finish();
            }
        });


        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Check for empty fields and then get values
                if (!edtPhone.getText().toString().trim().equals("") &&
                        !edtName.getText().toString().trim().equals("") &&
                        !edtPassword.getText().toString().trim().equals("")) {
                    phone = edtPhone.getText().toString().trim();
                    name = edtName.getText().toString().trim();
                    password = edtPassword.getText().toString().trim();
                }

                int selectedID = radioGroup.getCheckedRadioButtonId();
                userType = (selectedID == rdbtnProfessor.getId()) ? "Professor" : "Student";

                // If only there is no empty fields start AsyncTask
                if (!phone.equals("") && !name.equals("") && !password.equals("")) {
                    BackgroundTask backgroundTask = new BackgroundTask();
                    backgroundTask.execute();
                } else
                    Toast.makeText(SignUp.this, "Don't leave empty field!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // end of OnCreate

    class BackgroundTask extends AsyncTask<String, Void, String> {

        String info_url;
        HttpURLConnection httpURLConnection;

        @Override
        protected void onPreExecute() {
            info_url = "https://heavy-theories.000webhostapp.com/add_info.php";
        }

        @Override
        protected String doInBackground(String... args) {

            try {
                URL url = new URL(info_url);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream,"UTF-8"));

                String data_string = URLEncoder.encode("phone", "UTF-8") + "=" + URLEncoder.encode(phone,"UTF-8") + "&" +
                        URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(password,"UTF-8") + "&" +
                        URLEncoder.encode("userType", "UTF-8") + "=" + URLEncoder.encode(userType,"UTF-8") + "&" +
                        URLEncoder.encode("name", "UTF-8") + "=" + URLEncoder.encode(name,"UTF-8");

                bufferedWriter.write(data_string);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();

                int response_code = httpURLConnection.getResponseCode();

                if(response_code == HttpURLConnection.HTTP_OK) {
                    InputStream inputStream = httpURLConnection.getInputStream();
                    BufferedReader bufferedReadereader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder result = new StringBuilder();
                    String line;

                    while ((line = bufferedReadereader.readLine()) != null) {
                        result.append(line);
                    }

                    bufferedReadereader.close();
                    inputStream.close();

                    return (result.toString());
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

            if (result.toString().contains("Duplicate entry"))
                Toast.makeText(SignUp.this, "Duplicate phone number!", Toast.LENGTH_SHORT).show();
            else if (result.toString().contains("Data too long for column"))
                Toast.makeText(SignUp.this, "Data is too long!", Toast.LENGTH_SHORT).show();
            else if (result.equals("Error in network conneciton"))
                Toast.makeText(SignUp.this, "Error in network connection", Toast.LENGTH_SHORT).show();
            else if (result.toString().contains("Successfully registered")) {
                Toast.makeText(SignUp.this, result.toString(), Toast.LENGTH_SHORT).show();

                // After creating account open Sign In page
                startActivity(new Intent(SignUp.this, SignIn.class));
            }
        }
    }

}
