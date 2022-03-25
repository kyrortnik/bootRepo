package com.epam.esm;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface GiftCertificateRepository {

    Optional<GiftCertificate> getCertificateById(Long giftCertificateId);

    Optional<GiftCertificate> getGiftCertificateByName(String giftCertificateName);

    List<GiftCertificate> getGiftCertificates(HashMap<String, Boolean> sortParams, int max, int offset);

    List<GiftCertificate> getGiftCertificatesByTags(HashMap<String, Boolean> sortParams, int max, Set<Tag> tags, int offset);

    boolean deleteGiftCertificate(Long giftCertificateId);

    Optional<GiftCertificate> updateGiftCertificate(GiftCertificate giftCertificate, GiftCertificate existingGiftCertificate);

    Long createGiftCertificate(GiftCertificate giftCertificate);


}
