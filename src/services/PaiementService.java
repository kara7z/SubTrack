package services;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import dao.AbonnementDAO;
import dao.PaiementDAO;
import enums.PaiementStatus;
import enums.TypePaiement;
import models.Abonnement;
import models.AbonnementAvecEngagement;
import models.Paiement;

public class PaiementService {
  private PaiementDAO payDao;
  private AbonnementDAO aboDao;

  public PaiementService(PaiementDAO payDao, AbonnementDAO aboDao) {
    this.payDao = payDao;
    this.aboDao = aboDao;
  }

  public static boolean isUsedId(String id, List<Paiement> list) {
    for (Paiement p : list) {
      if (p.getIdPaiement().equals(id)) {
        return true;
      }
    }
    return false;
  }

  public Paiement enregistrer(String idAbo, LocalDate echeance, TypePaiement type) {
    Paiement p = new Paiement(idAbo, echeance, LocalDate.now(), type, PaiementStatus.PAYE);
    payDao.create(p);
    return p;
  }

  public boolean payerEcheance(String idPaiement, TypePaiement type) {
    Optional<Paiement> opt = payDao.findById(idPaiement);
    if (!opt.isPresent()) {
      return false;
    }
    Paiement p = opt.get();
    p.setStatut(PaiementStatus.PAYE);
    p.setDatePaiement(LocalDate.now());
    p.setType(type);
    return payDao.update(p);
  }

  public boolean update(Paiement p) {
    return payDao.update(p);
  }

  public boolean delete(String idPaiement) {
    return payDao.delete(idPaiement);
  }

  public List<Paiement> findByAbonnement(String idAbo) {
    return payDao.findByAbonnement(idAbo);
  }

  public List<Paiement> findAll() {
    return payDao.findAll();
  }

  public Optional<Paiement> findById(String id) {
    return payDao.findById(id);
  }

  public List<Paiement> findImpayesAvecEngagement() {
    return payDao.findAll().stream()
        .filter(p -> p.getStatut() != PaiementStatus.PAYE)
        .filter(p -> {
          Optional<Abonnement> a = aboDao.findById(p.getIdAbonnement());
          return a.isPresent() && a.get() instanceof AbonnementAvecEngagement;
        })
        .collect(Collectors.toList());
  }

  public double totalImpayeAvecEngagement() {
    return findImpayesAvecEngagement().stream()
        .mapToDouble(p -> aboDao.findById(p.getIdAbonnement())
            .map(Abonnement::getMontantMensuel).orElse(0.0))
        .sum();
  }

  public double sommePayee(String idAbo) {
    return payDao.findByAbonnement(idAbo).stream()
        .filter(p -> p.getStatut() == PaiementStatus.PAYE)
        .mapToDouble(p -> aboDao.findById(idAbo)
            .map(Abonnement::getMontantMensuel).orElse(0.0))
        .sum();
  }

  public List<Paiement> last5Global() {
    return payDao.findAll().stream()
        .sorted((a, b) -> {
          LocalDate d1 = a.getDatePaiement() != null ? a.getDatePaiement() : a.getDateEcheance();
          LocalDate d2 = b.getDatePaiement() != null ? b.getDatePaiement() : b.getDateEcheance();
          return d2.compareTo(d1);
        })
        .limit(5)
        .collect(Collectors.toList());
  }

  public double totalPayeForMonth(int year, int month) {
    return payDao.findAll().stream()
        .filter(p -> p.getStatut() == PaiementStatus.PAYE)
        .filter(p -> {
          LocalDate d = p.getDatePaiement() != null ? p.getDatePaiement() : p.getDateEcheance();
          return d.getYear() == year && d.getMonthValue() == month;
        })
        .mapToDouble(p -> aboDao.findById(p.getIdAbonnement())
            .map(Abonnement::getMontantMensuel).orElse(0.0))
        .sum();
  }

  public double totalPayeForYear(int year) {
    return payDao.findAll().stream()
        .filter(p -> p.getStatut() == PaiementStatus.PAYE)
        .filter(p -> {
          LocalDate d = p.getDatePaiement() != null ? p.getDatePaiement() : p.getDateEcheance();
          return d.getYear() == year;
        })
        .mapToDouble(p -> aboDao.findById(p.getIdAbonnement())
            .map(Abonnement::getMontantMensuel).orElse(0.0))
        .sum();
  }

  public long countUnpaidForMonth(int year, int month) {
    return payDao.findAll().stream()
        .filter(p -> p.getStatut() != PaiementStatus.PAYE)
        .filter(p -> p.getDateEcheance().getYear() == year
            && p.getDateEcheance().getMonthValue() == month)
        .count();
  }

  public long countUnpaidForYear(int year) {
    return payDao.findAll().stream()
        .filter(p -> p.getStatut() != PaiementStatus.PAYE)
        .filter(p -> p.getDateEcheance().getYear() == year)
        .count();
  }
}
