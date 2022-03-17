package com.epam.esm;

import java.util.List;
import java.util.Optional;

public interface TagRepository {

    Optional<Tag> getTag(Long id);

    List<Tag> getTags(String order, int max, int offset);

    boolean delete(Long id);

    Long createTag(Tag tag);

    void update(Tag tag);

    List<Tag> getTagsForCertificate(Long id);

    Optional<Tag> getMostUsedTagForRichestUser();

    Optional<Tag> getTagByName(String tagName);

}
