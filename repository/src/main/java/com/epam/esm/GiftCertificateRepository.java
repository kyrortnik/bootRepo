package com.epam.esm;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface GiftCertificateRepository {

    Optional<GiftCertificate> getCertificateById(Long id);

    Optional<GiftCertificate> getCertificateByName(String name);

    List<GiftCertificate> getCertificates(String order, int max, int offset);

//    List<GiftCertificate> getCertificatesWithParams(String order, int max, String tag, String pattern);

    List<GiftCertificate> getCertificatesByTags(String order, int max, Set<Tag> tags, int offset);

//    List<GiftCertificate> getCertificatesByTags(String order, int max, Set<Tag> tags, int offset);

    boolean delete(Long id);

    Optional<GiftCertificate> update(GiftCertificate element, long id);

    Long create(GiftCertificate element);


}
