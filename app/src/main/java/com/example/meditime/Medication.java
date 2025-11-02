package com.example.meditime;

public class Medication {


    private long id;
    private String name;
    private int color;
    private int dosesPerDay;
    private String initialTime;
    private int intervalHrs;
    private String frequency;

    public Medication() {}


    public Medication(long id, String name, int dosesPerDay, String initialTime, int color) {
        this.id = id;
        this.name = name;
        this.dosesPerDay = dosesPerDay;
        this.initialTime = initialTime;
        this.color = color;
    }


    public Medication(long id, String name, int dosesPerDay, String initialTime,
                      int color, int intervalHrs, String frequency) {
        this.id = id;
        this.name = name;
        this.dosesPerDay = dosesPerDay;
        this.initialTime = initialTime;
        this.color = color;
        this.intervalHrs = intervalHrs;
        this.frequency = frequency;
    }



    public Medication(String name, int dosesPerDay, String initialTime, int color, int intervalHrs, String frequency) {
        this.name = name;
        this.dosesPerDay = dosesPerDay;
        this.initialTime = initialTime;
        this.color = color;
        this.intervalHrs = intervalHrs;
        this.frequency = frequency;
    }



    public long getId() { // ⚠️ Tipo de retorno corregido a long
        return id;
    }
    public void setId(long id) { // ⚠️ Tipo de parámetro corregido a long
        this.id = id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public int getColor() {
        return color;
    }
    public void setColor(int color) {
        this.color = color;
    }

    public int getDosesPerDay() {
        return dosesPerDay;
    }
    public void setDosesPerDay(int dosesPerDay) {
        this.dosesPerDay = dosesPerDay;
    }

    public String getInitialTime() {
        return initialTime;
    }
    public void setInitialTime(String initialTime) {
        this.initialTime = initialTime;
    }

    public int getIntervalHrs() {
        return intervalHrs;
    }
    public void setIntervalHrs(int intervalHrs) {
        this.intervalHrs = intervalHrs;
    }

    public String getFrequency() {
        return frequency;
    }
    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }
}