package dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import enums.AbonnementStatus;
import models.Abonnement;
import models.AbonnementAvecEngagement;
import models.AbonnementSansEngagement;

public class AbonnementDAO {
  private List<Abonnement> abonnements = new ArrayList<Abonnement>();

  public void create(Abonnement a) {
    abonnements.add(a);
  }

  public Optional<Abonnement> findById(String id) {
    for (Abonnement a : abonnements) {
      if (a.getId().equals(id)) {
        return Optional.of(a);
      }
    }
    return Optional.empty();
  }

  public List<Abonnement> findAll() {
    return new ArrayList<Abonnement>(abonnements);
  }

  public boolean update(Abonnement updated) {
    for (int i = 0; i < abonnements.size(); i++) {
      if (abonnements.get(i).getId().equals(updated.getId())) {
        abonnements.set(i, updated);
        return true;
      }
    }
    return false;
  }

  public boolean delete(String id) {
    for (int i = 0; i < abonnements.size(); i++) {
      if (abonnements.get(i).getId().equals(id)) {
        abonnements.remove(i);
        return true;
      }
    }
    return false;
  }

  public List<Abonnement> findActiveSubscriptions() {
    List<Abonnement> result = new ArrayList<Abonnement>();
    for (Abonnement a : abonnements) {
      if (a.getStatut() == AbonnementStatus.ACTIVE) {
        result.add(a);
      }
    }
    return result;
  }

  public List<Abonnement> findByType(Class<?> type) {
    List<Abonnement> result = new ArrayList<Abonnement>();
    for (Abonnement a : abonnements) {
      if (type.isInstance(a)) {
        result.add(a);
      }
    }
    return result;
  }

  public List<AbonnementAvecEngagement> findAvecEngagement() {
    List<AbonnementAvecEngagement> result = new ArrayList<AbonnementAvecEngagement>();
    for (Abonnement a : abonnements) {
      if (a instanceof AbonnementAvecEngagement) {
        result.add((AbonnementAvecEngagement) a);
      }
    }
    return result;
  }

  public List<AbonnementSansEngagement> findSansEngagement() {
    List<AbonnementSansEngagement> result = new ArrayList<AbonnementSansEngagement>();
    for (Abonnement a : abonnements) {
      if (a instanceof AbonnementSansEngagement) {
        result.add((AbonnementSansEngagement) a);
      }
    }
    return result;
  }
}
