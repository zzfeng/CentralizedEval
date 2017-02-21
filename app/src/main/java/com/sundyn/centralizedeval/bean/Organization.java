package com.sundyn.centralizedeval.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/2/21.
 */

public class Organization {

    private static String TAG = "Organization";

    private boolean success = false;// true成功，false失败
    private boolean showDept = false;// true显示机构界面， false不显示
    private String message = "";// 返回信息；
    private String orgTitle = "";// 医院名称，首页标题
    private List<Department> departments = new ArrayList<Department>();// 部门列表

    /**
     * 获取所有员工信息
     *
     * @return
     */
    public List<UserBean> getAllUserBeans() {
        List<UserBean> userBeans = new ArrayList<UserBean>();
        if (departments != null && departments.size() > 0) {
            for (int i = 0; i < departments.size(); i++) {
                Department department = departments.get(i);
                if (department != null && department.getUsers().size() > 0) {
                    userBeans.addAll(department.getUsers());
                }
            }
        }
        return userBeans;
    }

    /**
     * @return the success
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * @param success the success to set
     */
    public void setSuccess(boolean success) {
        this.success = success;
    }

    /**
     * @return the showDept
     */
    public boolean isShowDept() {
        return showDept;
    }

    /**
     * @param showDept the showDept to set
     */
    public void setShowDept(boolean showDept) {
        this.showDept = showDept;
    }

    /**
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * @param message the message to set
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * @return the departments
     */
    public List<Department> getDepartments() {
        return departments;
    }

    /**
     * @param departments the departments to set
     */
    public void setDepartments(List<Department> departments) {
        this.departments = departments;
    }

    /**
     * @return the orgTitle
     */
    public String getOrgTitle() {
        return orgTitle;
    }

    /**
     * @param orgTitle the orgTitle to set
     */
    public void setOrgTitle(String orgTitle) {
        this.orgTitle = orgTitle;
    }


}
