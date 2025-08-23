package org.readtogether.session.fixtures;

import lombok.experimental.UtilityClass;
import org.readtogether.session.common.enums.MediaType;
import org.readtogether.session.model.request.SessionCreateRequest;
import org.readtogether.session.model.request.SessionUpdateRequest;

import java.util.UUID;

import static org.readtogether.session.common.enums.MediaType.AUDIO;
import static org.readtogether.session.common.enums.MediaType.VIDEO;

@UtilityClass
public class SessionRequestFixtures {

    public static final UUID DEFAULT_READING_ROOM_ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440002");

    public static SessionCreateRequest createDefaultCreateSessionRequest() {

        SessionCreateRequest request = new SessionCreateRequest();
        request.setTitle("Test Session");
        request.setDescription("A test session for reading practice");
        request.setMediaType(AUDIO);
        request.setPublic(false);
        request.setReadingRoomId(DEFAULT_READING_ROOM_ID);
        request.setTags("test,reading,practice");
        request.setBookTitle("Test Book");
        request.setLanguage("en");
        request.setLive(false);
        request.setAuthorName("Test Author");
        return request;
    }

    public static SessionCreateRequest createPublicVideoCreateRequest() {

        SessionCreateRequest request = new SessionCreateRequest();
        request.setTitle("Public Video Session");
        request.setDescription("A public video session for community viewing");
        request.setMediaType(VIDEO);
        request.setPublic(true);
        request.setTags("public,video,community");
        request.setBookTitle("Community Book");
        request.setLanguage("en");
        request.setLive(false);
        request.setAuthorName("Community Author");
        return request;
    }

    public static SessionCreateRequest createCreateSessionRequest(
            String title,
            String description,
            MediaType mediaType,
            boolean isPublic) {

        SessionCreateRequest request = new SessionCreateRequest();
        request.setTitle(title);
        request.setDescription(description);
        request.setMediaType(mediaType);
        request.setPublic(isPublic);
        request.setTags("test,session");
        request.setLanguage("en");
        request.setLive(false);
        return request;
    }

    public static SessionCreateRequest createCreateSessionRequest(
            String title,
            String description,
            MediaType mediaType,
            boolean isPublic,
            UUID readingRoomId,
            String tags) {

        SessionCreateRequest request = new SessionCreateRequest();
        request.setTitle(title);
        request.setDescription(description);
        request.setMediaType(mediaType);
        request.setPublic(isPublic);
        request.setReadingRoomId(readingRoomId);
        request.setTags(tags);
        request.setLanguage("en");
        request.setLive(false);
        return request;
    }

    public static SessionUpdateRequest createDefaultUpdateSessionRequest() {

        SessionUpdateRequest request = new SessionUpdateRequest();
        request.setTitle("Updated Session Title");
        request.setDescription("Updated session description");
        request.setIsPublic(true);
        request.setTags("updated,test,session");
        return request;
    }

}