package com.sundyn.centralizedeval.utils;

import com.sundyn.centralizedeval.bean.ButtonBean;

import java.io.InputStream;
import java.util.List;

/**
 * Created by Administrator on 2017/2/21.
 */

public interface ButtonParser {
    public List<ButtonBean> parser(InputStream is)throws Exception;
}
