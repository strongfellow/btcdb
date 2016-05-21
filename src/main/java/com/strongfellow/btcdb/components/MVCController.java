package com.strongfellow.btcdb.components;

import java.io.IOException;

import org.apache.commons.codec.DecoderException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class MVCController {

    @Autowired
    ReadOnlyService readService;

    @RequestMapping(value="/api/block/{blockHash}/summary.html", method=RequestMethod.GET)
    public String blockSummary(
            @PathVariable("blockHash") String blockHash,
            @ModelAttribute("model") ModelMap model) throws IOException, DecoderException {
        BlockSummary bs = readService.getBlockSummary(blockHash);
        model.addAttribute("blockHash", blockHash);
        model.addAttribute("numTx", bs.numTx);
        model.addAttribute("height", bs.getHeight());
        model.addAttribute("sumOfTxOuts", bs.getSumOfTxouts());
        model.addAttribute("timestamp", bs.getTimestamp());
        model.addAttribute("coinbase", bs.getCoinbaseValue());
        model.addAttribute("feesClaimed", bs.getFeesClaimed());
        model.addAttribute("feesAvailable", bs.getFeesAvailable());
        model.addAttribute("reward", bs.getReward());
        model.addAttribute("bits", bs.getBits());
        model.addAttribute("size", bs.getSize());
        model.addAttribute("version", bs.getVersion());
        model.addAttribute("nonce", bs.getNonce());
        model.addAttribute("blockReward", bs.getReward());
        StringBuilder coinbaseScript = new StringBuilder();
        for (byte b : bs.getCoinbaseScript()) {
            coinbaseScript.append((char)b);
        }

        model.addAttribute("coinbaseScript", coinbaseScript.toString());
        return "block-summary";
    }
}
