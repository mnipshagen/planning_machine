package com.mnipshagen.planning_machine;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nipsh on 29/01/2017.
 */

public class Module {

    private String name;
    private String code;
    private int compCredits;
    private int optCompCredits;
    private int achievedCredits;
    private int enrolledCredits;
    private float avgGrade;
    private List<Course> courses;
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

    public void addCourse(Course course){
        courses.add(course);
    }

    public List<Course> getCourses(){
        return courses;
    }

    public Course getCourse(int index){
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

    //// debug ///
    public static List<Module> prepareModules(){
        List<Module> moduleList = new ArrayList<Module>();
        Module m = new Module("Computational Linguistics", "CL", 8, 12);
        moduleList.add(m);

        m = new Module("Computer Science", "INF", 9, 9);
        moduleList.add(m);

        m = new Module("Cognitive (Neuro-)Psychology", "KNP", 8, 8);
        moduleList.add(m);

        m = new Module("Artificial Intelligence", "KI", 8, 12);
        moduleList.add(m);

        m = new Module("Mathematics", "MAT", 9, 9);
        moduleList.add(m);

        m = new Module("Neuroinformatics", "NI", 12, 12);
        moduleList.add(m);

        m = new Module("Neuroscience", "NW", 8, 12);
        moduleList.add(m);

        m = new Module("Philosophy on Mind and Cognition", "PHIL", 10, 8);
        moduleList.add(m);

        m = new Module("Logic", "LOG", 6, 6);
        moduleList.add(m);

        m = new Module("Statistics", "SD", 8, 8);
        moduleList.add(m);

        m = new Module("Foundations of Cognitive Science", "PWB", 3, 3);
        moduleList.add(m);

        return moduleList;
    }

}
