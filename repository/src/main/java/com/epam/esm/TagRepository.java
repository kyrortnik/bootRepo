package com.epam.esm;

import java.util.List;
import java.util.Optional;

public interface TagRepository {

    Optional<Tag> getTag(Long id);

    List<Tag> getTags(String order, int max);

    boolean delete(Long id);

    Long create(Tag element);

    List<Tag> getTagsForCertificate(Long id);

    Optional<Tag> getMostUsedTag();
}
