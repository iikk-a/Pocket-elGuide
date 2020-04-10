package com.example.pocketguide_honeywell;

public class elGuideDB {
    private String EAN;
    private String elGuideKoodi;
    private String description;

    public elGuideDB(){}

    public elGuideDB(String EAN, String guideKoodi, String description) {
        this.EAN = EAN;
        this.elGuideKoodi = guideKoodi;
        this.description = description;
    }

    public String getGuideKoodi() { return this.elGuideKoodi; }

    public String getEAN() { return this.EAN; }

    public String getDescription() { return this.description; }

    public void setEAN(String EAN) { this.EAN = EAN; }

    public void setGuideKoodi(String guideKoodi) { this.elGuideKoodi = guideKoodi; }

    public void setDescription(String description) {this.description = description; }



}
