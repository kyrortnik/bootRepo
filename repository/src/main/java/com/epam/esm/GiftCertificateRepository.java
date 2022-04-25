package com.epam.esm;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface GiftCertificateRepository extends JpaRepository<GiftCertificate, Long> {

    Optional<GiftCertificate> findByName(String name);

    Page<GiftCertificate> findByTagsIn(Set<Tag> tags, Pageable pageable);

}
