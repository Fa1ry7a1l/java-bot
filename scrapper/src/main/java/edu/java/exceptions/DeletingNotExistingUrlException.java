package edu.java.exceptions;

import java.net.URI;
import org.springframework.http.HttpStatus;

public class DeletingNotExistingUrlException extends ScrapperApiException {
    public DeletingNotExistingUrlException(Long id, URI url) {
        super(
            HttpStatus.NOT_FOUND,
            "попытка удалить ссылку, которой нет",
            "чат %d пытается удалить ссылку %s".formatted(id, url.toString())
        );
    }
}
