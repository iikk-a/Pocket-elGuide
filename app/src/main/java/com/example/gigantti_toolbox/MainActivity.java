package com.example.gigantti_toolbox;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.iikkag.gigantti_toolbox.R;
import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;

public class MainActivity extends AppCompatActivity
        implements SettingsFragment.OnFragmentInteractionListener, CountrySelectDialog.CountrySelectDialogListener {

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.default_menu, menu);
        return true;
    }

    // Initialize variables
    private EditText eanTextField;
    private Button eanButton;
    private Toolbar toolbar;
    private TextView elGuideCodeText;
    private TextView dataBaseText;
    private FloatingActionButton settingsButton;
    private ImageView productImage;
    private String dataBaseTextString;
    private String finalReturn;
    private ConstraintLayout layout;
    private boolean fetchSuccess = false;
    private Integer[] settings = {0, 1, 0, 0};
    private String[] firstPartURL = {   "https://www.gigantti.fi/search?SearchTerm=",
                                        "https://www.elgiganten.se/search?SearchTerm=",
                                        "https://www.elkjop.no/search?SearchTerm="      };

    private String[] firstPartImgURL = {    "https://www.gigantti.fi",
                                            "https://elgiganten.se",
                                            "https://elkjop.no"     };

    private final char[] bannedCharacters = {'!', '"', '#', '¤', '%', '&', '/', '(', ')', '=', '?', '@', '£', '$',
            '€', '{', '[', ']', '}', '\\'};

    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String IMGBOOL = "imgfetch";

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item1:
                toggleImageFetching();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private static final String DB_LOG = "DATABASE";

    private MyDBHandler db;

    private static final int ZXING_CAMERA_PERMISSION = 1;

    private String cameraEAN = "";

    private String imgURL = "";

    private final String URL_LAST = "&search=&searchResultTab=";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Attach variables to UI elements
        eanTextField = findViewById(R.id.eanTextField);
        eanButton = findViewById(R.id.eanButton);
        elGuideCodeText = findViewById(R.id.barcode_text);
        dataBaseText = findViewById(R.id.dataBase);
        productImage = findViewById(R.id.imageView);
        toolbar = findViewById(R.id.toolbar);
        settingsButton = findViewById(R.id.settingsButton);
        layout = findViewById(R.id.background);

        setSupportActionBar(toolbar);

        // Setup onClickListener for the button
        eanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scannerCamera();
            }
        });

        // Setup onCLickListener for the elGuide code text field so that URL links can be clicked
        elGuideCodeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openURL();
            }
        });
        productImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openURL();
            }
        });
        dataBaseText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openURL();
            }
        });

        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSettingsFragment();
            }
        });

        // Give eanTextField -element proper elements
        eanTextField.setImeActionLabel("Find", KeyEvent.KEYCODE_ENTER);
        eanTextField.setImeOptions(2);

        // Make sure pressing enter means go instead of just hiding the keyboard
        eanTextField.setOnKeyListener(new View.OnKeyListener() {
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
        elGuideDB elguide = new elGuideDB("0", "0", "0", "0", "0", 0);
        db.addHandler(elguide);

        if (getIntent().getStringExtra("EANCODE") != null) {
            cameraEAN = getIntent().getStringExtra("EANCODE");
            eanTextField.setText(cameraEAN);
        }
    }


    // Function to handle code fetching
    private void buttonClick() {
        if (eanTextField.getText().toString().equals("")) {
            return;
        }

        // Network actions have to be performed in a separate thread
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {

                // Setup variables and URLs for code fetching
                String EAN = eanTextField.getText().toString();
                String URL = firstPartURL[settings[0]] + EAN + URL_LAST;
                String finalURL = URL;
                Log.d("SETTINGS", settings[0].toString());

                // Try to see if EAN and code already exist in the database, if not, fetch it from the website
                if (!isInDatabase(EAN)) {

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
                    if (!elGuide.equals("www.gigantti.fi") && !elGuide.equals("www.elgiganten.se") && !elGuide
                            .equals("www.elkjop.no")) {
                        fetchSuccess = true;
                        Document doc = null;
                        try {
                            doc = Jsoup.connect(finalURL).get();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Element firstImage = doc.select("img.first-product-image").first();
                        if (firstImage != null) {
                            imgURL = firstPartImgURL[settings[0]] + firstImage.attr("src");
                        }
                        Log.d("IMAGE", "Image source = " + imgURL);
                        addToDatabase(EAN, elGuide, description, finalURL, imgURL, settings[0]);
                        Log.d(DB_LOG, "Added values to the database");
                        dataBaseTextString = description;
                    } else {
                        fetchSuccess = false;
                        imgURL = "";
                        elGuideCodeText.setText("Try again!");
                        Log.d(DB_LOG, "Parsing failed, " + firstPartImgURL[settings[0]]
                                + " has not been added to the database");
                        dataBaseTextString = "EAN: " + EAN;
                    }
                }

                // If the EAN exists in the database, don't fetch it from the website, but fetch it from the database instead making operation faster
                else {
                    fetchSuccess = true;
                    elGuideCodeText.setText(getGuideKoodiFromDatabase(EAN));
                    Log.d(DB_LOG, "Got values from the database");
                    dataBaseTextString = getDescriptionFromEAN(EAN);
                    imgURL = getImageURLFromDatabase(elGuideCodeText.getText().toString());
                }

                // UI elements can only be accessed and have to be modified by the original thread that created them AKA the UI thread
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dataBaseText.setText(dataBaseTextString);
                        if (fetchSuccess) {
                            elGuideCodeText
                                    .setPaintFlags(elGuideCodeText.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                            elGuideCodeText.setTextColor(Color.BLUE);
                            if (!imgURL.equals("") && settings[1] == 1) {
                                Picasso.get().load(imgURL).into(productImage);
                                Log.d("IMAGE", "Image added successfully. source= " + imgURL);
                            }

                        } else {
                            elGuideCodeText.setPaintFlags(0);
                            elGuideCodeText.setTextColor(Color.BLACK);
                            productImage.setImageResource(android.R.color.transparent);
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

    private void openURL() {
        if (fetchSuccess) {
            String URL = getURLFromDatabase(elGuideCodeText.getText().toString()) == "-1" ? firstPartURL[settings[0]]
                    + elGuideCodeText.getText().toString() + URL_LAST
                    : getURLFromDatabase(elGuideCodeText.getText().toString());
            if (!URL.contains("http") || !URL.contains("https")) {
                return;
            }
            CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
            CustomTabsIntent customTabsIntent = builder.build();
            customTabsIntent.launchUrl(this, Uri.parse(URL));
        }
    }


    // Use onPause to close the database
    @Override
    protected void onPause() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(IMGBOOL, settings[1]);
        editor.putInt("SETTINGS0", settings[0]);
        editor.putInt("SETTINGS1", settings[1]);
        editor.putInt("SETTINGS2", settings[2]);
        editor.putInt("SETTINGS3", settings[3]);
        editor.apply();

        super.onPause();
        getSupportActionBar().hide();
        db.closeDB();
    }


    // Open the database again with onResume
    @Override
    protected void onResume() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        settings[1] = sharedPreferences.getInt(IMGBOOL, 1);
        settings[0] = sharedPreferences.getInt("SETTINGS0", 0);
        settings[1] = sharedPreferences.getInt("SETTINGS1", 1);
        settings[2] = sharedPreferences.getInt("SETTINGS2", 0);
        settings[3] = sharedPreferences.getInt("SETTINGS3", 0);

        super.onResume();
        getSupportActionBar().hide();
        if (!cameraEAN.equals("")) {
            buttonClick();
        }

        Context context = getApplicationContext();
        layout.setBackground(ContextCompat.getDrawable(context, R.color.white));
    }

    // Check if EAN already exists in the database
    private boolean isInDatabase(String EAN) {
        elGuideDB elGuide = db.findHandler(EAN);
        return elGuide != null;
    }

    // Fetch the code from the database
    private String getGuideKoodiFromDatabase(String EAN) {
        elGuideDB elGuideDB = db.findHandler(EAN);
        return elGuideDB == null ? "-1" : elGuideDB.getGuideCode();
    }

    private String getURLFromDatabase(String guideCode) {
        elGuideDB elGuideDB = db.guideHandler(guideCode);
        String toReturn = elGuideDB == null ? "-1" : elGuideDB.getURL();
        if (elGuideDB.getCountry() != settings[0] && elGuideDB != null) {
            toReturn = firstPartURL[settings[0]] + elGuideDB.getGuideCode() + URL_LAST;

        }

        return toReturn;
    }

    private String getImageURLFromDatabase(String guideCode) {
        elGuideDB elGuideDB = db.guideHandler(guideCode);
        return elGuideDB == null ? "-1" : elGuideDB.getImageURL();
    }

    // Add the code and EAN to the database
    private void addToDatabase(String EAN, String guideCode, String description, String URL, String ImageURL,
            Integer country) {
        elGuideDB elGuideDB = new elGuideDB(EAN, guideCode, description, URL, ImageURL, country);
        db.addHandler(elGuideDB);
    }

    private String getDescriptionFromEAN(String EAN) {
        elGuideDB elGuideDB = db.findHandler(EAN);
        finalReturn = elGuideDB == null ? "database" : elGuideDB.getDescription();
        if (elGuideDB.getCountry() != settings[0]) {
            final String toReturn = firstPartURL[settings[0]] + elGuideDB.getGuideCode() + URL_LAST;
            Thread thread = new Thread(new Runnable() {

                @Override
                public void run() {
                    String toSplit = "";
                    try {
                        toSplit = Jsoup.connect(toReturn).followRedirects(true).execute().url().toExternalForm();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    String[] separated = toSplit.split("/");
                    int index = separated.length > 2 ? separated.length - 2 : 0;
                    String elGuide = separated[index];

                    String description = separated[index + 1];
                    finalReturn = description;

                    if (!elGuide.equals("www.gigantti.fi") && !elGuide.equals("www.elgiganten.se") && !elGuide
                            .equals("elkjop.no")) {
                        fetchSuccess = true;
                        Document doc = null;
                        try {
                            doc = Jsoup.connect(toReturn).get();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Element firstImage = doc.select("img.first-product-image").first();
                        imgURL = firstPartImgURL[settings[0]] + firstImage.attr("src");
                        Log.d("IMAGE", "Image source = " + imgURL);
                        //addToDatabase(EAN, elGuide, description, finalURL, imgURL, settings[0]);
                        //Log.d(DB_LOG, "Added values to the database");
                        //dataBaseTextString = description;
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (settings[1] == 1) {
                                Picasso.get().load(imgURL).into(productImage);
                            }
                        }
                    });
                }
            });

            thread.start();

            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return finalReturn;
    }

    private String getDescriptionFromGuideCode(String guideCode) {
        if (guideCode.equals("")) {
            return "";
        }
        elGuideDB elGuideDB = db.guideHandler(guideCode);
        if (elGuideDB == null) {
            return "Try Again!";
        }
        finalReturn = elGuideDB == null ? "database" : elGuideDB.getDescription();
        if (elGuideDB.getCountry() != settings[0] || settings[1] == 1) {
            final String toReturn = firstPartURL[settings[0]] + elGuideDB.getGuideCode() + URL_LAST;
            Thread thread = new Thread(new Runnable() {


                @Override
                public void run() {
                    String toSplit = "";
                    try {
                        toSplit = Jsoup.connect(toReturn).followRedirects(true).execute().url().toExternalForm();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    String[] separated = toSplit.split("/");
                    int index = separated.length > 2 ? separated.length - 2 : 0;
                    String elGuide = separated[index];

                    String description = separated[index + 1];
                    finalReturn = description;

                    if (!elGuide.equals("www.gigantti.fi") && !elGuide.equals("www.elgiganten.se") && !elGuide
                            .equals("elkjop.no")) {
                        fetchSuccess = true;
                        Document doc = null;
                        try {
                            doc = Jsoup.connect(toReturn).get();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Element firstImage = doc.select("img.first-product-image").first();
                        imgURL = firstPartImgURL[settings[0]] + firstImage.attr("src");
                        Log.d("IMAGE", "Image source = " + imgURL);
                        //addToDatabase(EAN, elGuide, description, finalURL, imgURL, settings[0]);
                        //Log.d(DB_LOG, "Added values to the database");
                        //dataBaseTextString = description;
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (settings[1] == 1) {
                                Picasso.get().load(imgURL).into(productImage);
                            }
                        }
                    });
                }
            });

            thread.start();

            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return finalReturn;
    }

    private void scannerCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat
                    .requestPermissions(this, new String[]{Manifest.permission.CAMERA}, ZXING_CAMERA_PERMISSION);
        } else {
            Intent intent = new Intent(this, SimpleScannerActivity.class);
            startActivity(intent);
        }
    }

    private void toggleImageFetching() {
        if (settings[1] == 1) {
            settings[1] = 0;
            Toast.makeText(this, "Image Fetching Has Been Turned Off", Toast.LENGTH_SHORT).show();
            productImage.setVisibility(View.GONE);
        } else {
            settings[1] = 1;
            productImage.setVisibility(View.VISIBLE);
            Toast.makeText(this, "Image Fetching Has Been Turned On", Toast.LENGTH_SHORT).show();
            getDescriptionFromGuideCode(elGuideCodeText.getText().toString());
            FragmentManager fm = getSupportFragmentManager();
            if (fm.getBackStackEntryCount() > 0) {
                fm.popBackStack();
            }
        }
    }

    private void openSettingsFragment() {
        Context context = getApplicationContext();
        layout.setBackground(ContextCompat.getDrawable(context, R.color.darkBackground));

        SettingsFragment fragment = SettingsFragment.newInstance(context, layout, settingsButton);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.addToBackStack(null);
        transaction.add(R.id.frameLayout, fragment, "SettingsFragment").commit();
    }

    @Override
    public void onFragmentInteraction(Integer id, Integer value) {
        //Toast.makeText(this, "The interaction worked!" + id + " " + value, Toast.LENGTH_SHORT).show();
        if (id == 1) {
            toggleImageFetching();
        }
    }

    @Override
    public void passCountryData(Integer id, Integer value) {
        settings[id] = value;
        String[] stringArray;
        String toPrint = "";
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("SETTINGS" + id, id);
        editor.apply();
        if (id == 0) {
            stringArray = getResources().getStringArray(R.array.Countries);
            toPrint = stringArray[settings[0]] + " Selected";
            dataBaseText.setText(getDescriptionFromGuideCode(elGuideCodeText.getText().toString()));
        }

        if (!toPrint.equals("")) {
            Toast.makeText(this, toPrint, Toast.LENGTH_SHORT).show();
        }

        FragmentManager fm = getSupportFragmentManager();
        if (fm.getBackStackEntryCount() > 0) {
            fm.popBackStack();
        }
    }

    private String purgeUserInput(String input) {
        char[] array = input.toCharArray();
        String toReturn = "";
        for (char ch : array) {
            for (char ban : bannedCharacters) {
                ch = ch == ban ? '\0' : ch;
            }
            toReturn += ch;
        }
        return toReturn;
    }

}
