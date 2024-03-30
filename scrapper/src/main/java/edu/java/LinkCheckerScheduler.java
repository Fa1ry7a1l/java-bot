package edu.java;

import edu.java.services.UpdatesChecker;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class LinkCheckerScheduler {
    private static final Logger LOGGER = LogManager.getLogger(LinkCheckerScheduler.class.getName());
    private final UpdatesChecker updatesChecker;

    @Scheduled(fixedDelayString = "${app.scheduler.interval}",
               initialDelayString = "${app.scheduler.force-check-delay}")
    public void update() {
        LOGGER.debug("вызвали проверку ссылок");
        updatesChecker.startAllLicksCheck();
    }

}
