package com.strongfellow.btcdb.components;

import org.springframework.web.bind.annotation.RestController;

import com.strongfellow.btcdb.protocol.Block;
import com.strongfellow.btcdb.protocol.Transaction;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@RestController
public class BTCController {

    @Autowired
    private BTCListener listener;
    
    @RequestMapping("/")
    public String index() {
        return "Greetings from Spring Boot!";
    }

    @RequestMapping(value="/transactions", method=RequestMethod.POST)
    public void postTransaction(@RequestBody Transaction transaction) {
        listener.processTransaction(transaction);
    }

    @RequestMapping(value="/blocks", method=RequestMethod.POST)
    public void postBlock(@RequestBody Block block) throws IOException {
        listener.processBlock(block);
    }

}
