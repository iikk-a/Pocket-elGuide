package com.example.gigantti_toolbox;

public class elGuideDB {

    // Initialize the variables
    private String EAN;
    private String elGuideCode;
    private String description;
    private String URL;
    private String ImageURL;
    private Integer country;

    // Constructor when no arguments are needed, only need to call the class
    public elGuideDB(){}

    // Constructor for an object
    elGuideDB(String EAN, String guideKoodi, String description, String URL, String ImageURL, Integer country) {
        this.EAN = EAN;
        this.elGuideCode = guideKoodi;
        this.description = description;
        this.URL = URL;
        this.ImageURL = ImageURL;
        this.country = country;
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

    // Getter for country settings
    Integer getCountry() { return this.country; }


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

    // Setter for country
    void setCountry(Integer country) { this.country = country; }

}
