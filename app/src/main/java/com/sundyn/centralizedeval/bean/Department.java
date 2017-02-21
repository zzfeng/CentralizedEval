package com.sundyn.centralizedeval.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/2/21.
 */

public class Department {

    private static String TAG = "Department";
    private String department = "";// 机构,科室
    private List<UserBean> users = new ArrayList<UserBean>();// 职员列表

    /**
     * @return the department
     */
    public String getDepartment() {
        return department;
    }

    /**
     * @param department the department to set
     */
    public void setDepartment(String department) {
        this.department = department;
    }

    /**
     * @return the users
     */
    public List<UserBean> getUsers() {
        return users;
    }

    /**
     * @param users the users to set
     */
    public void setUsers(List<UserBean> users) {
        this.users = users;
    }


}
