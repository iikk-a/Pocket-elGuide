package com.example.gigantti_toolbox;

public class elGuideDB {

    // Initialize the variables
    private String EAN;
    private String elGuideCode;
    private String description;
    private String URL;
    private String ImageURL;

    // Constructor when no arguments are needed, only need to call the class
    public elGuideDB(){}

    // Constructor for an object
    elGuideDB(String EAN, String guideKoodi, String description, String URL, String ImageURL) {
        this.EAN = EAN;
        this.elGuideCode = guideKoodi;
        this.description = description;
        this.URL = URL;
        this.ImageURL = ImageURL;
    }

    // Getter for guideCode
    String getGuideCode() { return this.elGuideCode; }

    // Getter for EAN code
    String getEAN() { return this.EAN; }

    // Getter for description
    String getDescription() { return this.description; }

    // Getter for URL
    String getURL() { return this.URL; }

    // Getter for ImageURL
    String getImageURL() { return this.ImageURL; }

    // Setter for EAN
    void setEAN(String EAN) { this.EAN = EAN; }

    // Setter for guideCode
    void setGuideCode(String guideCode) { this.elGuideCode = guideCode; }

    // Setter for description
    void setDescription(String description) {this.description = description; }

    // Setter for URL
    void setURL(String URL) { this.URL = URL; }

    // Setter for ImageURL
    void setImageURL(String ImageURL) { this.ImageURL = ImageURL; }


}
