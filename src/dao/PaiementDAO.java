package dao;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import enums.PaiementStatus;
import models.Paiement;

public class PaiementDAO {
  private List<Paiement> paiements = new ArrayList<Paiement>();

  public void create(Paiement p) {
    paiements.add(p);
  }

  public Optional<Paiement> findById(String id) {
    for (Paiement p : paiements) {
      if (p.getIdPaiement().equals(id)) {
        return Optional.of(p);
      }
    }
    return Optional.empty();
  }

  public List<Paiement> findAll() {
    return new ArrayList<Paiement>(paiements);
  }

  public List<Paiement> findByAbonnement(String idAbonnement) {
    List<Paiement> result = new ArrayList<Paiement>();
    for (Paiement p : paiements) {
      if (p.getIdAbonnement().equals(idAbonnement)) {
        result.add(p);
      }
    }
    return result;
  }

  public boolean update(Paiement updated) {
    for (int i = 0; i < paiements.size(); i++) {
      if (paiements.get(i).getIdPaiement().equals(updated.getIdPaiement())) {
        paiements.set(i, updated);
        return true;
      }
    }
    return false;
  }

  public boolean delete(String idPaiement) {
    for (int i = 0; i < paiements.size(); i++) {
      if (paiements.get(i).getIdPaiement().equals(idPaiement)) {
        paiements.remove(i);
        return true;
      }
    }
    return false;
  }

  public void deleteByAbonnement(String idAbonnement) {
    for (int i = paiements.size() - 1; i >= 0; i--) {
      if (paiements.get(i).getIdAbonnement().equals(idAbonnement)) {
        paiements.remove(i);
      }
    }
  }

  public List<Paiement> findUnpaidByAbonnement(String idAbonnement) {
    List<Paiement> result = new ArrayList<Paiement>();
    for (Paiement p : paiements) {
      if (p.getIdAbonnement().equals(idAbonnement) && p.getStatut() != PaiementStatus.PAYE) {
        result.add(p);
      }
    }
    return result;
  }

  public List<Paiement> findLastPayments(int n) {
    List<Paiement> copy = new ArrayList<Paiement>(paiements);
    Collections.sort(copy, new Comparator<Paiement>() {
      public int compare(Paiement a, Paiement b) {
        LocalDateSafe d1 = new LocalDateSafe(a);
        LocalDateSafe d2 = new LocalDateSafe(b);
        return d2.value.compareTo(d1.value);
      }
    });
    if (copy.size() > n) {
      return copy.subList(0, n);
    }
    return copy;
  }

  private static class LocalDateSafe {
    LocalDate value;
    LocalDateSafe(Paiement p) {
      if (p.getDatePaiement() != null) {
        value = p.getDatePaiement();
      } else {
        value = p.getDateEcheance();
      }
    }
  }
}
