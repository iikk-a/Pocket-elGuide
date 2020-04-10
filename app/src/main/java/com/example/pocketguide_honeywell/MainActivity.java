package com.example.pocketguide_honeywell;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.iikkag.pocketguide_honeywell.R;

import org.jsoup.Jsoup;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    // Initialize variables
    private EditText eanTextField;
    private Button eanButton;
    private TextView elGuideCodeText;
    private TextView dataBaseText;
    private String dataBaseTextString;
    private boolean fetchSuccess = false;
    private MyDBHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Attach variables to UI elements
        eanTextField = findViewById(R.id.eanTextField);
        eanButton = findViewById(R.id.eanButton);
        elGuideCodeText = findViewById(R.id.barcode_text);
        dataBaseText = findViewById(R.id.dataBase);


        // Setup onClickListener for the button
        eanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonClick();
            }
        });

        elGuideCodeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openURL();
            }
        });

        // Give eanTextField -element proper elements
        eanTextField.setImeActionLabel("Find", KeyEvent.KEYCODE_ENTER);
        eanTextField.setImeOptions(2);

        // Make sure pressing enter means go instead of just hiding the keyboard
        eanTextField.setOnKeyListener(new View.OnKeyListener(){
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                    (keyCode == KeyEvent.KEYCODE_ENTER)) {
                        buttonClick();
                        return true;
                    }
                return false;
            }
        });

        // Setup first element in database, thus initializing it
        db = new MyDBHandler(this, null, null, 1);
        elGuideDB elguide = new elGuideDB("0", "0", "0");
        db.addHandler(elguide);
    }



    // Function to handle code fetching
    public void buttonClick() {

        // Network actions have to be performed in a separate thread
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {

                // Setup variables and URLs for code fetching
                String EAN = eanTextField.getText().toString();
                String URL = "https://www.gigantti.fi/search?SearchTerm=" + EAN + "&search=&searchResultTab=";
                String finalURL = URL;

                // Try to see if EAN and code already exist in the database, if not, fetch it from the website
                if(!isInDatabase(EAN)) {

                    // Catch IOException errors
                    try {
                        // Connect to the URL and follow all redirects. When you find the last redirect, take the URL of that website
                        finalURL = Jsoup.connect(URL).followRedirects(true).execute().url().toExternalForm();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    // Take the final URL and use string splitting around "/" and take the second last element in the array, provided the array has more than 2 elements
                    String[] separated = finalURL.split("/");
                    int index = separated.length > 2 ? separated.length - 2 : 0;
                    elGuideCodeText.setText(separated[index]);

                    // For the database
                    String elGuide = separated[index];
                    String description = separated[index + 1];

                    // If EAN returns a hit, save that to the database. If the query fails, do not save it to the database
                    if (!elGuide.equals("www.gigantti.fi")) {
                        fetchSuccess = true;
                        addToDatabase(EAN, elGuide, description);
                        Log.d("DATABASE", "Added values to the database");
                        dataBaseTextString = description;
                    } else {
                        fetchSuccess = false;
                        elGuideCodeText.setText("Try again!");
                        Log.d("DATABASE", "Parsing failed, www.gigantti.fi has not been added to the database");
                        dataBaseTextString = "EAN: " + EAN;
                    }
                }

                // If the EAN exists in the database, don't fetch it from the website, but fetch it from the database instead making operation faster
                else {
                    fetchSuccess = true;
                    elGuideCodeText.setText(getGuideKoodiFromDatabase(EAN));
                    Log.d("DATABASE", "Got values from the database");
                    dataBaseTextString = "database";
                }

                // UI elements can only be accessed and have to be modified by the original thread that created them AKA the UI thread
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dataBaseText.setText(dataBaseTextString);
                        if(fetchSuccess) {
                            elGuideCodeText.setPaintFlags(elGuideCodeText.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);
                            elGuideCodeText.setTextColor(Color.BLUE);
                        } else {
                            elGuideCodeText.setPaintFlags(0);
                            elGuideCodeText.setTextColor(Color.BLACK);
                        }
                    }

                });
            }
        });

        // Start the fetching thread
        thread.start();

        // Wait for the thread to finish operations before continuing
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Reset the text field to be used for other EANs
        eanTextField.setText("");
    }

    public void openURL() {
        if(fetchSuccess) {
            String URL = "https://www.gigantti.fi/search?SearchTerm=" + elGuideCodeText.getText().toString() + "&search=&searchResultTab=";
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(URL));
            startActivity(intent);
        }
    }


    // Use onPause to close the database
    @Override
    protected void onPause() {
        super.onPause();
        getSupportActionBar().hide();
        db.closeDB();
    }


    // Open the database again with onResume
    @Override
    protected void onResume() {
        super.onResume();
        getSupportActionBar().hide();
        db.loadHandler();
    }

    // Check if EAN already exists in the database
    public boolean isInDatabase(String EAN) {
        elGuideDB elGuide = db.findHandler(EAN);
        if (elGuide != null) {
            return true;
        } else {
            return false;
        }
    }

    // Fetch the code from the database
    public String getGuideKoodiFromDatabase(String EAN) {
        elGuideDB elGuide = db.findHandler(EAN);
        if (elGuide != null) {
            return elGuide.getGuideKoodi();
        } else {
            return "-1";
        }
    }

    // Add the code and EAN to the database
    public void addToDatabase(String EAN, String guideCode, String description) {
        elGuideDB elGuideDB = new elGuideDB(EAN, guideCode, description);
        db.addHandler(elGuideDB);
    }

}
