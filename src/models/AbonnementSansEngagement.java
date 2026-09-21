package models;

import java.time.LocalDate;
import enums.AbonnementStatus;

public class AbonnementSansEngagement extends Abonnement {
  public AbonnementSansEngagement(String nomService, double montantMensuel,
      LocalDate dateDebut, LocalDate dateFin, AbonnementStatus statut) {
    super(nomService, montantMensuel, dateDebut, dateFin, statut);
  }
}
