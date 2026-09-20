package src.models;

import java.time.LocalDate;
import src.enums.AbonnementStatus;

public class AbonnementAvecEngagement extends Abonnement {
  private int dureeEngagementMois;

  public AbonnementAvecEngagement(String nomService, double montantMensuel,
      LocalDate dateDebut, LocalDate dateFin, AbonnementStatus statut, int dureeMois) {
    super(nomService, montantMensuel, dateDebut, dateFin, statut);
    this.dureeEngagementMois = dureeMois;
  }

  public int getDureeEngagementMois() {
    return dureeEngagementMois;
  }

  public void setDureeEngagementMois(int dureeEngagementMois) {
    this.dureeEngagementMois = dureeEngagementMois;
  }

  @Override
  public String toString() {
    return super.toString().replace("]", ", engagement=" + dureeEngagementMois + " mois]");
  }
}
