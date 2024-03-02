package edu.java.bot;

import java.net.URI;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

@Component
public class LinkChecker {

    static final Logger LOGGER = LogManager.getLogger(LinkChecker.class.getName());

    public URI tryValidate(String link) {

        LOGGER.debug("Начали проверку со строкой " + link);

        URI uri;
        try {
            uri = URI.create(link);
        } catch (IllegalArgumentException e) {
            LOGGER.info("Не удалось распарсить строку " + link);
            return null;
        }

        LOGGER.debug(uri.getHost());

        return uri;
    }
}
