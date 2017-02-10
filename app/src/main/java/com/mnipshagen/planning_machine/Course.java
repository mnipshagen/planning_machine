package com.mnipshagen.planning_machine;

import java.util.List;

/**
 * Created by nipsh on 29/01/2017.
 */

public class Course {

    private String name;
    private int ects;
    private float grade;
    private List<String> courses;
    private int state;              // 0 = compulsory not fulfilled, 1 = compulsory fulfilled, 2 = optional compulsory fulfilled, 3 = cross module exam fulfilled

    public Course(String n, int ects){
        name = n;
        this.ects = ects;
        grade = 0f;
        state = 0;
    }

    public String getName(){
        return name;
    }

    public int getEcts() {
        return ects;
    }

    public float getGrade(){
        return grade;
    }

    public void setGrade(float g){
        grade = g;
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
