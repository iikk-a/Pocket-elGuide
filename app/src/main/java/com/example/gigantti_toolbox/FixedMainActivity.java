package com.example.gigantti_toolbox;

/*import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.example.gigantti_toolbox.MyDBHandler;
import com.example.gigantti_toolbox.elGuideDB;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.iikkag.gigantti_toolbox.R;
import java.io.IOException;
import org.jsoup.Jsoup;

public class FixedMainActivity {

    private EditText eanTextField;
    private Button eanButton;
    private TextView elGuideCodeText, dataBaseText;
    private String dataBaseTextString, finalReturn, cameraEAN, imgURL;
    private FloatingActionButton settingsButton;
    private ImageView productImage;
    private ConstraintLayout layout;
    private MyDBHandler db;
    private boolean fetchSuccess = false;
    private Integer[] settings = {0, 1, 0, 0};
    private String[] firstPartURL = {   "https://www.gigantti.fi/search?SearchTerm=",
                                        "https://www.elgiganten.se/search?SearchTerm=",
                                        "https://www.elkjop.no/search?SearchTerm="      };

    private String[] firstPartImgURL = {    "https://www.gigantti.fi",
                                            "https://elgiganten.se",
                                            "https://elkjop.no"     };

    private final String URL_LAST = "&search=&searchResultTab=";

    private final char[] bannedCharacters = {   '!', '"', '#', '¤', '%', '&', '/', '(', ')', '=', '?', '@', '£', '$',
                                                '€', '{', '[', ']', '}', '\\'       };

    private static final String SHARED_PREFS = "sharedPrefs";
    private static final String IMGBOOL = "imgfetch";


    protected void onCreate(Bundle savedInstanceState) {
        variableInitialization();

    }

    private void variableInitialization() {
        eanTextField = findViewById(R.id.eanTextField);
        eanButton = findViewById(R.id.eanButton);
        elGuideCodeText = findViewById(R.id.barcode_text);
        dataBaseText = findViewById(R.id.dataBase);
        productImage = findViewById(R.id.imageView);
        settingsButton = findViewById(R.id.settingsButton);
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
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSettingsFragment();
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
        elGuideDB elguide = new elGuideDB("0", "0", "0", "0", "0", 0);
        db.addHandler(elguide);

        if (getIntent().getStringExtra("EANCODE") != null) {
            cameraEAN = getIntent().getStringExtra("EANCODE");
            eanTextField.setText(cameraEAN);
        }
    }

    private void buttonClick() {
        if (eanTextField.getText().toString().equals("")) { return; }

        startNetworkThread();
    }

    private void startNetworkThread() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {

            }
        });
    }

    // Get the final address of a given URL. MUST BE CALLED USING A NETWORK THREAD
    private String getFinalAddress(String startingAddress) {
        String finalAddress = "";
        try {
            finalAddress = Jsoup.connect(startingAddress).followRedirects(true).execute().url().toExternalForm();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return finalAddress == null ? "" : finalAddress;
    }


}*/
