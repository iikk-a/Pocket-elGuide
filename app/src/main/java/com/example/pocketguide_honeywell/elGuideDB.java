package com.example.pocketguide_honeywell;

public class elGuideDB {

    // Initialize the variables
    private String EAN;
    private String elGuideCode;
    private String description;

    // Constructor when no arguments are needed, only need to call the class
    elGuideDB(){}

    // Constructor for an object
    elGuideDB(String EAN, String guideKoodi, String description) {
        this.EAN = EAN;
        this.elGuideCode = guideKoodi;
        this.description = description;
    }

    // Getter for guideCode
    String getGuideCode() { return this.elGuideCode; }

    // Getter for EAN code
    String getEAN() { return this.EAN; }

    // Getter for description
    String getDescription() { return this.description; }


    // Setter for EAN
    void setEAN(String EAN) { this.EAN = EAN; }

    // Setter for guideCode
    void setGuideCode(String guideCode) { this.elGuideCode = guideCode; }

    // Setter for description
    void setDescription(String description) {this.description = description; }



}
