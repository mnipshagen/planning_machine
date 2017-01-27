package com.dreamingdude.spam;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Moritz on 13.10.2016.
 */

public class Module {

    private String name;
    private String code;
    private int compCredits;
    private int optCompCredits;
    private int achievedCredits;
    private int enrolledCredits;
    private float avgGrade;
    private List<String> courses;
    private int state;              // 0 = compulsory not fulfilled, 1 = compulsory fulfilled, 2 = optional compulsory fulfilled, 3 = cross module exam fulfilled

    public Module(String n, String c, int compC, int optCompC){
        name = n;
        code = c;
        compCredits = compC;
        optCompCredits = optCompC;
        achievedCredits = 0;
        enrolledCredits = 0;
        avgGrade = 0;
        state = 0;
    }

    public String getName(){
        return name;
    }

    public String getCode(){
        return code;
    }

    public int getCompCredits(){
        return compCredits;
    }

    public int getOptCompCredits(){
        return optCompCredits;
    }

    public int getAchievedCredits(){
        return achievedCredits;
    }

    public void setAchievedCredits(int c){
        achievedCredits = c;
    }

    public int getenrolledCredits(){
        return enrolledCredits;
    }

    public void setEnrolledCredits(int c){
        enrolledCredits = c;
    }

    public float getAvgGrade(){
        return avgGrade;
    }

    public void setAvgGrade(float[] g){
        float avg = 0;
        for(float f:g){
            avg += f;
        }
        avg /= g.length;
        avgGrade = avg;
    }

    public void addCourse(String course){
        courses.add(course);
    }

    public List<String> getCourses(){
        return courses;
    }

    public String getCourse(int index){
        return courses.get(index);
    }

    public int getState(){
        return state;
    }

    public boolean setState(int s){
        if(!(s < 0 && s > 4)){
            state = s;
            return true;
        }
        else{
            return false;
        }
    }

}
