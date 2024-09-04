package com.dct.controller.security;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 *
 * @Author vic
 * @Date 18:13 2018/12/18
 * @param
 * @return
 **/
@Controller
public class HealthExaminationController {

    @RequestMapping(value = "/health/examination", method = RequestMethod.GET)
    @ResponseBody
    public String getConfig(HttpServletRequest request){
        return "ok";
    }

}
