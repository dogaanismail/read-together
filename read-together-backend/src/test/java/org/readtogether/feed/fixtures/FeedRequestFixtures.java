package org.readtogether.feed.fixtures;

import lombok.experimental.UtilityClass;
import org.readtogether.feed.model.CreateCommentRequest;

import java.util.UUID;

@UtilityClass
public class FeedRequestFixtures {

    public static CreateCommentRequest createDefaultCommentRequest() {
        CreateCommentRequest request = new CreateCommentRequest();
        request.setContent("This is a test comment on the feed item");
        return request;
    }

    public static CreateCommentRequest createCommentRequest(String content) {
        CreateCommentRequest request = new CreateCommentRequest();
        request.setContent(content);
        return request;
    }

    public static CreateCommentRequest createReplyCommentRequest(UUID parentCommentId) {
        CreateCommentRequest request = new CreateCommentRequest();
        request.setContent("This is a reply to another comment");
        request.setParentCommentId(parentCommentId);
        return request;
    }

    public static CreateCommentRequest createReplyCommentRequest(String content, UUID parentCommentId) {
        CreateCommentRequest request = new CreateCommentRequest();
        request.setContent(content);
        request.setParentCommentId(parentCommentId);
        return request;
    }

    public static CreateCommentRequest createLongCommentRequest() {
        CreateCommentRequest request = new CreateCommentRequest();
        request.setContent("This is a longer comment that tests the character limit validation. ".repeat(20));
        return request;
    }

    public static CreateCommentRequest createEmptyCommentRequest() {
        CreateCommentRequest request = new CreateCommentRequest();
        request.setContent("");
        return request;
    }

    public static CreateCommentRequest createNullContentCommentRequest() {
        CreateCommentRequest request = new CreateCommentRequest();
        request.setContent(null);
        return request;
    }
}