package com.epam.esm;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {

    String THE_RICHEST_USER_QUERY =
            "SELECT u.id FROM users AS u\n" +
                    "LEFT JOIN orders AS o ON u.id = o.user_id WHERE o.order_cost IS NOT NULL\n" +
                    "GROUP BY u.id\n" +
                    "ORDER BY SUM(o.order_cost) DESC\n" +
                    "LIMIT 1";

    String MOST_USED_TAG =
            "SELECT t.name FROM tags AS t\n" +
                    "LEFT JOIN certificates_tags AS ct ON t.id = ct.tag_id\n" +
                    "LEFT JOIN certificates AS c ON ct.certificate_id =  c.id\n" +
                    "LEFT JOIN orders AS o ON c.id = o.gift_certificate_id\n" +
                    "LEFT JOIN users AS u ON o.user_id = u.id WHERE u.id = :userId\n" +
                    "GROUP BY t.name\n" +
                    "ORDER BY COUNT(t.name) DESC\n" +
                    "LIMIT 1";

    @Query(value = MOST_USED_TAG, nativeQuery = true)
    String getMostUsedTagForRichestUser(@Param("userId") long userId);

    Optional<Tag> findByName(String tagName);

    boolean existsByName(String tagName);

    @Query(value = THE_RICHEST_USER_QUERY, nativeQuery = true)
    Long getRichestUserId();

    Set<Tag> findByCertificatesId(Long certificatesId);

}
