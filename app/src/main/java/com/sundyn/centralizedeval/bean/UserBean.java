package com.sundyn.centralizedeval.bean;

import com.sundyn.centralizedeval.commen.CommenUnit;

/**
 * Created by Administrator on 2017/2/21.
 */

public class UserBean {

    private static String TAG = "User";

    private String userName = "";// 用户名
    private String realName = "";// 真名
    private String jobNum = "";// 工号
    private String job = "";// 职位--药剂科主任
    private String jobTitle = ""; // 职称--主任医师
    private String jobDesc = "";// 工作描述，擅长领域
    private String department = "";// 机构,科室
    private String picUrl = "";// 照片
    private String picMd5 = "";// 照片md5

    public int upStatus = 0;

    /**
     * 获取人员图片绝对路径
     *
     * @return
     */
    public String getPicPath() {
        String path = "";
        if (picUrl != null && !picUrl.isEmpty()) {
            path = CommenUnit.DEPT_DIR + picUrl.substring(picUrl.lastIndexOf("/"));
        }
        return path;
    }

    /**
     * 获取人员图片名
     *
     * @return
     */
    public String getPicName() {
        String path = "";
        if (picUrl != null && !picUrl.isEmpty()) {
            path = picUrl.substring(picUrl.lastIndexOf("/") + 1);
        }
        return path;
    }

    /**
     * @return the userName
     */
    public String getUserName() {
        return userName;
    }

    /**
     * @param userName the userName to set
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * @return the realName
     */
    public String getRealName() {
        return realName;
    }

    /**
     * @param realName the realName to set
     */
    public void setRealName(String realName) {
        this.realName = realName;
    }

    /**
     * @return the jobNum
     */
    public String getJobNum() {
        return jobNum;
    }

    /**
     * @param jobNum the jobNum to set
     */
    public void setJobNum(String jobNum) {
        this.jobNum = jobNum;
    }

    /**
     * @return the job
     */
    public String getJob() {
        return job;
    }

    /**
     * @param job the job to set
     */
    public void setJob(String job) {
        this.job = job;
    }

    /**
     * @return the jobTitle
     */
    public String getJobTitle() {
        return jobTitle;
    }

    /**
     * @param jobTitle the jobTitle to set
     */
    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    /**
     * @return the jobDesc
     */
    public String getJobDesc() {
        return jobDesc;
    }

    /**
     * @param jobDesc the jobDesc to set
     */
    public void setJobDesc(String jobDesc) {
        this.jobDesc = jobDesc;
    }

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
     * @return the picUrl
     */
    public String getPicUrl() {
        return picUrl;
    }

    /**
     * @param picUrl the picUrl to set
     */
    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    /**
     * @return the picMd5
     */
    public String getPicMd5() {
        return picMd5;
    }

    /**
     * @param picMd5 the picMd5 to set
     */
    public void setPicMd5(String picMd5) {
        this.picMd5 = picMd5;
    }


}
