package com.epam.esm;

import java.util.List;
import java.util.Optional;

public interface GiftCertificateRepositoryOptional {

    Optional<GiftCertificate> getCertificate(Long id);

    List<GiftCertificate> getCertificates(String order, int max);

    List<GiftCertificate> getCertificatesWithParams(String order, int max, String tag, String pattern);

    boolean delete(Long id);

    Optional<GiftCertificate> update(GiftCertificate element, long id);

    Long create(GiftCertificate element);

}
