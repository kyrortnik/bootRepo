package com.epam.esm;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public interface TagRepository {

    Optional<Tag> getTagById(Long tagId);

    //TODO -- refactor order
//    List<Tag> getTags(String order, int max, int offset);

    List<Tag> getTags(HashMap<String,Boolean> sortParams, int max, int offset);

    boolean delete(Long tagId);

    Long createTag(Tag tag);

    Optional<Tag> getMostUsedTagForRichestUser();

    Optional<Tag> getTagByName(String tagName);

}
