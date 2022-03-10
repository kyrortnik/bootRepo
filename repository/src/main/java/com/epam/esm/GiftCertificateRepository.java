package com.epam.esm;

import java.util.List;
import java.util.Optional;

public interface GiftCertificateRepository {

    Optional<GiftCertificate> getCertificateById(Long id);

    Optional<GiftCertificate> getCertificateByName(String name);

    List<GiftCertificate> getCertificates(String order, int max);

    List<GiftCertificate> getCertificatesWithParams(String order, int max, String tag, String pattern);

    boolean delete(Long id);

    Optional<GiftCertificate> update(GiftCertificate element, long id);

    Long create(GiftCertificate element);


}
