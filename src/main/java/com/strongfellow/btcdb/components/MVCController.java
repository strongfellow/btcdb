package com.strongfellow.btcdb.components;

import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class MVCController {

    @RequestMapping(path="/api/block", method=RequestMethod.GET, produces="text/html")
    public String welcome(Map<String, Object> model) {
        return "block";
    }
}
