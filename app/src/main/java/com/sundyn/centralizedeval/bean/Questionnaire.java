package com.sundyn.centralizedeval.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/2/21.
 */

public class Questionnaire {
    private static String TAG = "Questionnaire";

    private String id = "";
    private String name = "";
    private long startTime = 0;
    private long endTime = 0;
    private List<Question> questions = new ArrayList<Question>();

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the startTime
     */
    public long getStartTime() {
        return startTime;
    }

    /**
     * @param startTime the startTime to set
     */
    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    /**
     * @return the endTime
     */
    public long getEndTime() {
        return endTime;
    }

    /**
     * @param endTime the endTime to set
     */
    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    /**
     * @return the content
     */
    public List<Question> getQuestions() {
        return questions;
    }

    /**
     * @param content the content to set
     */
    public void setQuestions(ArrayList<Question> questions) {
        this.questions = questions;
    }

    @Override
    public String toString() {
        return "Questionnaire [id=" + id + ", name=" + name + ", startTime=" + startTime
                + ", endTime=" + endTime + ", questions=" + questions + "]";
    }
}
