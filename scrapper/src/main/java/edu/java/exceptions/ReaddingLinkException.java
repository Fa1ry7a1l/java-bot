package edu.java.exceptions;

import java.net.URI;
import org.springframework.http.HttpStatus;

public class ReaddingLinkException extends ScrapperApiException {
    public ReaddingLinkException(Long id, URI url) {
        super(
            HttpStatus.CONFLICT,
            "Вы повторно пытаетесь добавить ссылку %s".formatted(url.toString()),
            "чат %d пытается повторно добавить ссылку %s".formatted(id, url.toString())
        );
    }
}
