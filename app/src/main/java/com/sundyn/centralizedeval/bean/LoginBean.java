package com.sundyn.centralizedeval.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2017/2/21.
 */

public class LoginBean implements Serializable {

    /**
     * result : success
     * detail : [{"id":"12681228","department":"66"},{"id":"12681227","department":"74"}]
     */

    private String result;
    private List<DetailBean> detail;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public List<DetailBean> getDetail() {
        return detail;
    }

    public void setDetail(List<DetailBean> detail) {
        this.detail = detail;
    }

    public static class DetailBean implements Serializable{
        /**
         * id : 12681228
         * department : 66
         */

        private String id;
        private String department;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getDepartment() {
            return department;
        }

        public void setDepartment(String department) {
            this.department = department;
        }

        @Override
        public String toString() {
            return "DetailBean [id=" + id + ", department=" + department + "]";
        }

    }

    @Override
    public String toString() {
        return "LoginBean [result=" + result + ", detail=" + detail + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((detail == null) ? 0 : detail.hashCode());
        result = prime * result
                + ((this.result == null) ? 0 : this.result.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        LoginBean other = (LoginBean) obj;
        if (detail == null) {
            if (other.detail != null)
                return false;
        } else if (!detail.equals(other.detail))
            return false;
        if (result == null) {
            if (other.result != null)
                return false;
        } else if (!result.equals(other.result))
            return false;
        return true;
    }



}
