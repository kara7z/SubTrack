package src.services;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import src.dao.AbonnementDAO;
import src.dao.PaiementDAO;
import src.enums.AbonnementStatus;
import src.enums.PaiementStatus;
import src.enums.TypePaiement;
import src.models.Abonnement;
import src.models.AbonnementAvecEngagement;
import src.models.AbonnementSansEngagement;
import src.models.Paiement;

public class AbonnementService {
  private AbonnementDAO aboDao;
  private PaiementDAO payDao;

  public AbonnementService(AbonnementDAO aboDao, PaiementDAO payDao) {
    this.aboDao = aboDao;
    this.payDao = payDao;
  }

  public AbonnementAvecEngagement createAvecEngagement(String nom, double montant,
      LocalDate debut, LocalDate fin, int dureeMois) {
    AbonnementAvecEngagement a = new AbonnementAvecEngagement(
        nom, montant, debut, fin, AbonnementStatus.ACTIVE, dureeMois);
    aboDao.create(a);
    genererEcheances(a);
    return a;
  }

  public AbonnementSansEngagement createSansEngagement(String nom, double montant,
      LocalDate debut, LocalDate fin) {
    AbonnementSansEngagement a = new AbonnementSansEngagement(
        nom, montant, debut, fin, AbonnementStatus.ACTIVE);
    aboDao.create(a);
    genererEcheances(a);
    return a;
  }

  public Optional<Abonnement> findById(String id) {
    return aboDao.findById(id);
  }

  public List<Abonnement> findAll() {
    return aboDao.findAll();
  }

  public List<Abonnement> findActive() {
    return aboDao.findActiveSubscriptions();
  }

  public boolean update(Abonnement a) {
    return aboDao.update(a);
  }

  public boolean delete(String id) {
    payDao.deleteByAbonnement(id);
    return aboDao.delete(id);
  }

  public boolean resilier(String id) {
    Optional<Abonnement> opt = aboDao.findById(id);
    if (!opt.isPresent()) {
      return false;
    }
    Abonnement a = opt.get();
    a.setStatut(AbonnementStatus.RESILIE);
    a.setDateFin(LocalDate.now());
    return aboDao.update(a);
  }

  public int genererEcheances(Abonnement a) {
    int created = 0;
    LocalDate d = a.getDateDebut().withDayOfMonth(1);
    LocalDate end = a.getDateFin().withDayOfMonth(1);
    List<Paiement> existing = payDao.findByAbonnement(a.getId());
    while (!d.isAfter(end)) {
      boolean already = false;
      for (Paiement p : existing) {
        if (p.getDateEcheance().getYear() == d.getYear()
            && p.getDateEcheance().getMonth() == d.getMonth()) {
          already = true;
          break;
        }
      }
      if (!already) {
        int day = Math.min(a.getDateDebut().getDayOfMonth(), d.lengthOfMonth());
        LocalDate echeance = LocalDate.of(d.getYear(), d.getMonth(), day);
        Paiement p = new Paiement(a.getId(), echeance, null, TypePaiement.CASH, PaiementStatus.NON_PAYE);
        payDao.create(p);
        existing.add(p);
        created++;
      }
      d = d.plusMonths(1);
    }
    return created;
  }
}
