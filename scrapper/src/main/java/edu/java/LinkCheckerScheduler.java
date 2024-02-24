package edu.java;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class LinkCheckerScheduler {
    private static final Logger LOGGER = LogManager.getLogger(LinkCheckerScheduler.class.getName());

    @Scheduled(fixedDelayString = "PT${app.scheduler.interval}",
               initialDelayString = "PT${app.scheduler.force-check-delay}")
    public void update() {
        LOGGER.debug("вызвали проверку ссылок");
    }

}
