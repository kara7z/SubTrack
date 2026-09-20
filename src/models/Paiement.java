package src.models;

import java.time.LocalDate;
import java.util.UUID;
import src.enums.PaiementStatus;
import src.enums.TypePaiement;

public class Paiement {
  private String idPaiement;
  private String idAbonnement;
  private LocalDate dateEcheance;
  private LocalDate datePaiement;
  private TypePaiement type;
  private PaiementStatus statut;

  public Paiement(String idAbonnement, LocalDate dateEcheance, LocalDate datePaiement,
      TypePaiement type, PaiementStatus statut) {
    this.idPaiement = UUID.randomUUID().toString();
    this.idAbonnement = idAbonnement;
    this.dateEcheance = dateEcheance;
    this.datePaiement = datePaiement;
    this.type = type;
    this.statut = statut;
  }

  public String getIdPaiement() {
    return idPaiement;
  }

  public String getIdAbonnement() {
    return idAbonnement;
  }

  public LocalDate getDateEcheance() {
    return dateEcheance;
  }

  public void setDateEcheance(LocalDate dateEcheance) {
    this.dateEcheance = dateEcheance;
  }

  public LocalDate getDatePaiement() {
    return datePaiement;
  }

  public void setDatePaiement(LocalDate datePaiement) {
    this.datePaiement = datePaiement;
  }

  public TypePaiement getType() {
    return type;
  }

  public void setType(TypePaiement type) {
    this.type = type;
  }

  public PaiementStatus getStatut() {
    return statut;
  }

  public void setStatut(PaiementStatus statut) {
    this.statut = statut;
  }

  @Override
  public String toString() {
    return "Paiement [id=" + idPaiement
        + ", abo=" + idAbonnement
        + ", echeance=" + dateEcheance
        + ", payeLe=" + datePaiement
        + ", type=" + type
        + ", statut=" + statut + "]";
  }
}
