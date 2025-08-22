package org.readtogether.feed.fixtures;

import lombok.experimental.UtilityClass;
import org.readtogether.feed.model.CreateCommentRequest;

@UtilityClass
public class FeedRequestFixtures {

    public static CreateCommentRequest createDefaultCommentRequest() {

        CreateCommentRequest request = new CreateCommentRequest();
        request.setContent("This is a test comment on the feed item");
        return request;
    }

    public static CreateCommentRequest createEmptyCommentRequest() {
        CreateCommentRequest request = new CreateCommentRequest();
        request.setContent("");
        return request;
    }

}