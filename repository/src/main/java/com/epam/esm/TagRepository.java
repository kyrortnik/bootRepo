package com.epam.esm;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;

import java.util.Optional;

public interface TagRepository extends PagingAndSortingRepository<Tag, Long> {

     String THE_RICHEST_USER_QUERY =
            "SELECT u.id FROM users AS u\n" +
            "LEFT JOIN orders AS o ON u.id = o.user_id WHERE o.order_cost IS NOT NULL\n" +
            "GROUP BY u.id\n" +
            "ORDER BY SUM(o.order_cost) DESC";

     String MOST_USED_TAG =
             "SELECT t.name FROM tags AS t" +
             "LEFT JOIN certificates_tags AS ct ON t.id = ct.tag_id" +
             "LEFT JOIN certificates AS c ON ct.certificate_id =  c.id" +
             "LEFT JOIN orders AS o ON c.id = o.gift_certificate_id" +
             "LEFT JOIN users AS u ON o.user_id = u.id WHERE u.id = :userId" +
             "GROUP BY t.name" +
             "ORDER BY COUNT(t.name) DESC)";

    @Override
    @NonNull Optional<Tag> findById(@NonNull Long tagId);

    @Override
    @NonNull Page<Tag> findAll(@NonNull Pageable pageable);

    @Override
    @NonNull void deleteById(@NonNull Long tagId);

    @Override
    @NonNull <S extends Tag> S save(@NonNull S tag);

    @Query(value = MOST_USED_TAG, nativeQuery = true)
    Optional<Tag> getMostUsedTagForRichestUser(@Param("userId") long userId);

    Optional<Tag> findByName(String name);

    @Query(value = THE_RICHEST_USER_QUERY, nativeQuery = true)
     long getRichestUserId();

//    /**
//     * @param tagsToUpdate new Tag Set for GiftCertificate from client
//     * @return Tag Set where existing tags were replaced with proxies and non-existing tags not changes so they could be created.
//     */
//    Set<Tag> replaceExistingTagsWithProxy(Set<Tag> tagsToUpdate);




}
