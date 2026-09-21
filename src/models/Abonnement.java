package models;

import enums.AbonnementStatus;
import java.time.LocalDate;
import java.util.UUID;

public abstract class Abonnement {
  private String id;
  private String nomService;
  private double montantMensuel;
  private LocalDate dateDebut;
  private LocalDate dateFin;
  private AbonnementStatus statut;

  public Abonnement(String nomService, double montantMensuel, LocalDate dateDebut,
      LocalDate dateFin, AbonnementStatus statut) {
    this.id = UUID.randomUUID().toString();
    this.nomService = nomService;
    this.montantMensuel = montantMensuel;
    this.dateDebut = dateDebut;
    this.dateFin = dateFin;
    this.statut = statut;
  }

  public String getId() {
    return id;
  }

  public String getNomService() {
    return nomService;
  }

  public void setNomService(String nomService) {
    this.nomService = nomService;
  }

  public double getMontantMensuel() {
    return montantMensuel;
  }

  public void setMontantMensuel(double montantMensuel) {
    this.montantMensuel = montantMensuel;
  }

  public LocalDate getDateDebut() {
    return dateDebut;
  }

  public void setDateDebut(LocalDate dateDebut) {
    this.dateDebut = dateDebut;
  }

  public LocalDate getDateFin() {
    return dateFin;
  }

  public void setDateFin(LocalDate dateFin) {
    this.dateFin = dateFin;
  }

  public AbonnementStatus getStatut() {
    return statut;
  }

  public void setStatut(AbonnementStatus statut) {
    this.statut = statut;
  }

  @Override
  public String toString() {
    return getClass().getSimpleName()
        + " [id=" + id
        + ", nom=" + nomService
        + ", montant=" + montantMensuel
        + ", debut=" + dateDebut
        + ", fin=" + dateFin
        + ", statut=" + statut + "]";
  }
}
