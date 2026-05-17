package com.tamilquran.ift.model.entity;

public class VerseRow {

    public int sura;
    public int ayah;
    public String tamilText;
    public String arabicText;
    public String serialNo;
    public boolean bismillahRow;

    public VerseRow(int sura, int ayah, String tamilText, String arabicText, boolean bismillahRow) {
        this.sura = sura;
        this.ayah = ayah;
        this.tamilText = tamilText;
        this.arabicText = arabicText;
        this.bismillahRow = bismillahRow;
        if (!bismillahRow && ayah > 0) {
            this.serialNo = sura + ":" + ayah + ". ";
        } else {
            this.serialNo = " ";
        }
    }
}
