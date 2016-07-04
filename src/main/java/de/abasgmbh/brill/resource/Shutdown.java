/**
 * creation date: Jul 04, 2016
 * first author: marco
 * maintained by: marco
 * <p>
 * (C) Copyright abas Software AG, Karlsruhe, Germany; 2016
 */
package de.abasgmbh.brill.resource;

import de.abasgmbh.brill.utils.Utils;
import org.apache.log4j.Logger;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class Shutdown {

    @RequestMapping(value="/shutdown", method = RequestMethod.GET)
    @ResponseBody
    public String getShutdown() throws Exception {
        shutdown();
        return "bye";
    }

    @Async
    private void shutdown() {
        Utils.sleep(1000);
        Logger.getLogger(Shutdown.class).info("shuting down");
        System.exit(0);
    }

}
