/**
 * creation date: Jun 28, 2016
 * first author: marco
 * maintained by: marco
 * <p>
 * (C) Copyright abas Software AG, Karlsruhe, Germany; 2016
 */
package de.brill.controller;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class Root {

    @RequestMapping(value="/", method = RequestMethod.GET)
    @ResponseBody
    public String getRoot() throws Exception {
        Logger.getLogger(Root.class).info("test");
        return "hello world";
    }
}
