package com.strongfellow.btcdb.components;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class MVCController {

    @RequestMapping(value="/api/block/{blockHash}/summary.html", method=RequestMethod.GET)
    public String blockSummary(
            @PathVariable("blockHash") String blockHash,
            @ModelAttribute("model") ModelMap model) {
        model.addAttribute("blockHash", blockHash);
        return "block-summary";
    }
}
