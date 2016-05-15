package com.strongfellow.btcdb.components;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.strongfellow.btcdb.protocol.Block;
import com.strongfellow.btcdb.protocol.Transaction;

@RestController
public class BTCController {

    @Autowired
    private BTCListener listener;

    @Autowired
    ReadOnlyService readService;

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

    @RequestMapping(value="/blocks/{block}/summary")
    @ResponseBody
    public BlockSummary getBlockSummary(@PathVariable("block") String block) {
        return this.readService.getBlockSummary(block);
    }

}
