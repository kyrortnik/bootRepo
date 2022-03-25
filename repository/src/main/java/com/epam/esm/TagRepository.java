package com.epam.esm;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface TagRepository {

    Optional<Tag> getTagById(Long tagId);

    List<Tag> getTags(HashMap<String,Boolean> sortParams, int max, int offset);

    boolean delete(Long tagId);

    Long createTag(Tag tag);

    Optional<Tag> getMostUsedTagForRichestUser();

    Optional<Tag> getTagByName(String tagName);

    /**
     * @param tagsToUpdate new Tag Set for GiftCertificate from client
     * @return Tag Set where existing tags were replaced with proxies and non-existing tags not changes so they could be created.
     */
    Set<Tag> replaceExistingTagsWithProxy(Set<Tag> tagsToUpdate);

}
