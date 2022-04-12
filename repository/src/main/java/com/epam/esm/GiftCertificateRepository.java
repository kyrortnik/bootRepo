package com.epam.esm;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.lang.NonNull;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface GiftCertificateRepository extends JpaRepository<GiftCertificate, Long> {

//    @Override
//    @NonNull Optional<GiftCertificate> findById(@NonNull Long aLong);

    Optional<GiftCertificate> findByName(String name);

//    @Override
//    @NonNull Page<GiftCertificate> findAll(@NonNull Pageable pageable);

    //TODO -- make sure that works -- TBD
    Page<GiftCertificate> findByTagsIn(Set<Tag> tags, Pageable pageable);

//    @Override
//    void deleteById(@NonNull Long giftCertificateId);
//
//    @Override
//    @NonNull <S extends GiftCertificate> S save(@NonNull S giftCertificate);
}
