package com.epam.esm;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface GiftCertificateRepository {

    Optional<GiftCertificate> getCertificateById(Long id);

    Optional<GiftCertificate> getGiftCertificateByName(String name);

    List<GiftCertificate> getGiftCertificates(String order, int max, int offset);

    List<GiftCertificate> getCertificatesByTags(String order, int max, Set<Tag> tags, int offset);

    boolean delete(Long id);

    Optional<GiftCertificate> updateGiftCertificate(GiftCertificate giftCertificate, long id);

    Long createGiftCertificate(GiftCertificate giftCertificate);


}
