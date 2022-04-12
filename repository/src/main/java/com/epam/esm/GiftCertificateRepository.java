package com.epam.esm;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.Set;

public interface GiftCertificateRepository extends JpaRepository<GiftCertificate, Long> {

    Optional<GiftCertificate> findByName(String name);

    //TODO -- make sure that works -- TBD
    Page<GiftCertificate> findByTagsIn(Set<Tag> tags, Pageable pageable);

}
