package org.readtogether.library.factory;

import lombok.experimental.UtilityClass;
import org.readtogether.library.entity.BookSessionEntity;

import java.util.UUID;

@UtilityClass
public class BookSessionFactory {

    public static BookSessionEntity createBookSessionEntity(
            UUID sessionId,
            UUID bookId,
            UUID userId,
            Integer pagesRead,
            Integer readingTimeSeconds) {

        return BookSessionEntity.builder()
                .sessionId(sessionId)
                .bookId(bookId)
                .userId(userId)
                .pagesRead(pagesRead)
                .readingTimeSeconds(readingTimeSeconds)
                .build();
    }
}
