package com.sundyn.centralizedeval.bean;

/**
 * Created by Administrator on 2017/2/21.
 */

public class Result {
    private static String TAG = "Results";

    private String id = "";
    private String result = "";

    private boolean select = false;

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
     * @return the result
     */
    public String getResult() {
        return result;
    }

    /**
     * @param result the result to set
     */
    public void setResult(String result) {
        this.result = result;
    }

    /**
     * @return the select
     */
    public boolean isSelect() {
        return select;
    }

    /**
     * @param select the select to set
     */
    public void setSelect(boolean select) {
        this.select = select;
    }

    @Override
    public String toString() {
        return "Result [id=" + id + ", result=" + result + ", select=" + select + "]";
    }
}
