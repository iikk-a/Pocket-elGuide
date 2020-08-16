package com.example.gigantti_toolbox;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import com.iikkag.gigantti_toolbox.R;
import com.squareup.picasso.Picasso;
import java.io.IOException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class MainActivity extends AppCompatActivity {

    private EditText eanTextField;
    private Button eanButton;
    private TextView elGuideCodeText, dataBaseText;
    private String dataBaseTextString, imgURL = " ", cameraEAN = "";
    private ImageView productImage;
    private ConstraintLayout layout;
    private MyDBHandler db;
    private boolean fetchSuccess = false;
    private elGuideDB mElGuideDB;

    // First setting is for country
    // Second setting is for image fetching
    private Integer[] settings = {0, 1, 0, 0};
    private String[] firstPartURL = {   "https://www.gigantti.fi/search?SearchTerm=",
            "https://www.elgiganten.se/search?SearchTerm=",
            "https://www.elkjop.no/search?SearchTerm="      };

    private String[] firstPartImgURL = {    "https://www.gigantti.fi",
            "https://elgiganten.se",
            "https://elkjop.no"     };

    private final String URL_LAST = "&search=&searchResultTab=";


    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
        variableInitialization();
        super.onCreate(savedInstanceState);
    }

    private void variableInitialization() {
        eanTextField = findViewById(R.id.eanTextField);
        eanButton = findViewById(R.id.eanButton);
        elGuideCodeText = findViewById(R.id.barcode_text);
        dataBaseText = findViewById(R.id.dataBase);
        productImage = findViewById(R.id.imageView);
        layout = findViewById(R.id.background);

        eanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scannerCamera();
            }
        });
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

        eanTextField.setImeActionLabel("Find", KeyEvent.KEYCODE_ENTER);
        eanTextField.setImeOptions(2);
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
        db = new MyDBHandler(this, null, null, 1);

        if (getIntent().getStringExtra("EANCODE") != null) {
            cameraEAN = getIntent().getStringExtra("EANCODE");
            eanTextField.setText(cameraEAN);
        }
    }

    private void buttonClick() {
        if (eanTextField.getText().toString().equals("")) { return; }
        String EAN = eanTextField.getText().toString().length() == 12 ? "0" + eanTextField.getText().toString() : eanTextField.getText().toString();
        elGuideDB elGuideItem;

        if(!isInDatabase(EAN)) {
            elGuideItem = getFromWebsite(EAN);
        } else {
            fetchSuccess = true;
            elGuideItem = getGuideObjectFromDatabase(EAN);
            elGuideCodeText.setText(elGuideItem.getGuideCode());
            dataBaseTextString = elGuideItem.getDescription();
            imgURL = elGuideItem.getImageURL();
        }

        dataBaseText.setText(dataBaseTextString);
        if(fetchSuccess) {
            elGuideCodeText.setPaintFlags(elGuideCodeText.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            elGuideCodeText.setTextColor(Color.BLUE);
            if(!imgURL.equals(" ") && settings[1] == 1) { Picasso.get().load(imgURL).into(productImage); }
        } else {
            elGuideCodeText.setPaintFlags(0);
            elGuideCodeText.setTextColor(Color.BLACK);
            productImage.setImageResource(android.R.color.transparent);
        }

        eanTextField.setText("");
        mElGuideDB = elGuideItem;
    }

    private void openURL() {
        if (fetchSuccess) {
            elGuideDB elGuideDB = getGuideObjectFromDatabase(elGuideCodeText.getText().toString());
            String URL = elGuideDB.getURL();
            if (!URL.contains("http") || !URL.contains("https")) { return; }

            CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
            CustomTabsIntent customTabsIntent = builder.build();
            customTabsIntent.launchUrl(this, Uri.parse(URL));
        }
    }

    private elGuideDB getFromWebsite(String scanCode) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                String EAN = eanTextField.getText().toString();
                EAN = EAN.length() == 12 ? "0" + EAN : EAN;
                String URL = firstPartURL[settings[0]] + EAN + URL_LAST;

                try {
                    URL = Jsoup.connect(URL).followRedirects(true).execute().url().toExternalForm();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                String[] separated = URL.split("/");
                int index = separated.length > 2 ? separated.length - 2 : 0;
                String elGuide = separated[index];
                String description = separated[++index];
                elGuideCodeText.setText(elGuide);

                if(!elGuide.equals("www.gigantti.fi") && !elGuide.equals("www.elgiganten.se") && !elGuide.equals("www.elkjop.no")) {
                    fetchSuccess = true;
                    if(settings[1] == 1) {
                        getPictureURL(URL);
                    } else { imgURL = " "; }

                    mElGuideDB = new elGuideDB(EAN, elGuide, description, URL, imgURL, settings[0]);
                    addToDatabase(mElGuideDB);
                    dataBaseTextString = description;

                } else {
                    fetchSuccess = false;
                    imgURL = " ";
                    elGuideCodeText.setText("Try Again!");
                    dataBaseTextString = "EAN: " + EAN;
                }
            }
        });

        thread.start();

        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return mElGuideDB;
    }

    private void addToDatabase(elGuideDB elGuideDB) {
        db.addHandler(elGuideDB);
    }

    private String getPictureURL(String URL) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Document doc = null;
                try {
                    doc = Jsoup.connect(URL).get();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Element firstImage = doc.selectFirst("img.first-product-image");
                if (firstImage != null) {
                    imgURL = firstPartImgURL[settings[0]] + firstImage.attr("src");
                }
            }
        });

        thread.start();

        try {
            thread.join();
        } catch(InterruptedException e) {
            e.printStackTrace();
        }

        return imgURL;
    }

    private elGuideDB getGuideObjectFromDatabase(String EAN) {
        return db.findHandler(EAN) == null ? db.guideHandler(EAN) : db.findHandler(EAN);
    }

    private boolean isInDatabase(String EAN) {
        return db.findHandler(EAN) == null ? db.guideHandler(EAN) != null : db.findHandler(EAN) != null;
    }

    private void scannerCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat
                    .requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 1);
        } else {
            Intent intent = new Intent(this, SimpleScannerActivity.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!cameraEAN.equals("")) {
            buttonClick();
        }

        Context context = getApplicationContext();
        layout.setBackground(ContextCompat.getDrawable(context, R.color.white));
    }

    @Override
    protected void onPause() {
        super.onPause();
        db.closeDB();
    }
}
