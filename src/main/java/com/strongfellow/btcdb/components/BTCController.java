package com.strongfellow.btcdb.components;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@RestController
public class BTCController {

    @RequestMapping("/")
    public String index() {
        return "Greetings from Spring Boot!";
    }

    @RequestMapping(value="/transactions", method=RequestMethod.POST)
    public void postTransaction() {
        
    }

    @RequestMapping(value="/blocks", method=RequestMethod.POST)
    public void postBlock() {
        
    }

}
