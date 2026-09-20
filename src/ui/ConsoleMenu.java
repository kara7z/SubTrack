package src.ui;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import src.dao.AbonnementDAO;
import src.dao.PaiementDAO;
import src.enums.AbonnementStatus;
import src.enums.PaiementStatus;
import src.enums.TypePaiement;
import src.models.Abonnement;
import src.models.AbonnementAvecEngagement;
import src.models.Paiement;
import src.services.AbonnementService;
import src.services.PaiementService;
import src.util.DateUtil;

public class ConsoleMenu {
  private Scanner sc = new Scanner(System.in);
  private AbonnementDAO aboDao = new AbonnementDAO();
  private PaiementDAO payDao = new PaiementDAO();
  private AbonnementService aboService = new AbonnementService(aboDao, payDao);
  private PaiementService payService = new PaiementService(payDao, aboDao);

  public void start() {
    seedDemo();
    boolean run = true;
    while (run) {
      printMenu();
      String choice = sc.nextLine().trim();
      try {
        switch (choice) {
          case "1": createAbo(); break;
          case "2": listAbos(); break;
          case "3": updateAbo(); break;
          case "4": deleteAbo(); break;
          case "5": resilierAbo(); break;
          case "6": listPaiementsByAbo(); break;
          case "7": payerEcheance(); break;
          case "8": updatePaiement(); break;
          case "9": deletePaiement(); break;
          case "10": showImpayes(); break;
          case "11": showSommePayee(); break;
          case "12": showLast5(); break;
          case "13": rapports(); break;
          case "0": run = false; System.out.println("Bye."); break;
          default: System.out.println("Invalid choice, try 0-13.");
        }
      } catch (Exception e) {
        System.out.println("Error: " + e.getMessage());
      }
    }
  }

  private void printMenu() {
    System.out.println("\n=== SubTrack ===");
    System.out.println("1. Create subscription");
    System.out.println("2. List subscriptions");
    System.out.println("3. Update subscription");
    System.out.println("4. Delete subscription");
    System.out.println("5. Cancel subscription (resilier)");
    System.out.println("6. Show payments of a subscription");
    System.out.println("7. Pay a due date / record payment");
    System.out.println("8. Update payment");
    System.out.println("9. Delete payment");
    System.out.println("10. Show missed payments + total (with engagement only)");
    System.out.println("11. Show total paid for a subscription");
    System.out.println("12. Show last 5 payments (global)");
    System.out.println("13. Financial reports (monthly, yearly, unpaid)");
    System.out.println("0. Exit");
    System.out.print("Choice: ");
  }

  private void seedDemo() {
    try {
      if (!aboService.findAll().isEmpty()) {
        return;
      }
      AbonnementAvecEngagement a1 = aboService.createAvecEngagement(
          "Netflix", 12.99, LocalDate.of(2026, 1, 10), LocalDate.of(2026, 6, 10), 6);
      aboService.createSansEngagement(
          "Spotify", 9.99, LocalDate.of(2026, 3, 5), LocalDate.of(2026, 8, 5));
      List<Paiement> list = payService.findByAbonnement(a1.getId());
      if (!list.isEmpty()) {
        payService.payerEcheance(list.get(0).getIdPaiement(), TypePaiement.CARD);
      }
    } catch (Exception e) {
      System.out.println("Seed warning: " + e.getMessage());
    }
  }

  private void createAbo() {
    System.out.print("Type (1=with engagement, 2=without): ");
    String t = sc.nextLine().trim();
    System.out.print("Service name: ");
    String nom = sc.nextLine().trim();
    if (nom.isEmpty()) {
      System.out.println("Name cannot be empty.");
      return;
    }
    System.out.print("Monthly amount (ex 12.99): ");
    double montant;
    try {
      montant = Double.parseDouble(sc.nextLine().trim());
    } catch (NumberFormatException e) {
      System.out.println("Invalid amount.");
      return;
    }
    System.out.print("Start date (yyyy-MM-dd): ");
    String s1 = sc.nextLine().trim();
    System.out.print("End date (yyyy-MM-dd): ");
    String s2 = sc.nextLine().trim();
    if (!DateUtil.isValid(s1) || !DateUtil.isValid(s2)) {
      System.out.println("Invalid date format, use yyyy-MM-dd.");
      return;
    }
    LocalDate debut = DateUtil.parse(s1);
    LocalDate fin = DateUtil.parse(s2);
    if (fin.isBefore(debut)) {
      System.out.println("End date must be after start date.");
      return;
    }
    if (t.equals("1")) {
      System.out.print("Engagement duration in months: ");
      int duree;
      try {
        duree = Integer.parseInt(sc.nextLine().trim());
      } catch (NumberFormatException e) {
        System.out.println("Invalid duration.");
        return;
      }
      AbonnementAvecEngagement a = aboService.createAvecEngagement(nom, montant, debut, fin, duree);
      System.out.println("Created: " + a);
    } else {
      Abonnement a = aboService.createSansEngagement(nom, montant, debut, fin);
      System.out.println("Created: " + a);
    }
    System.out.println("Due dates auto-generated.");
  }

  private void listAbos() {
    List<Abonnement> all = aboService.findAll();
    if (all.isEmpty()) {
      System.out.println("No subscriptions.");
      return;
    }
    for (Abonnement a : all) {
      System.out.println(a);
    }
  }

  private void updateAbo() {
    System.out.print("Subscription id: ");
    String id = sc.nextLine().trim();
    Optional<Abonnement> opt = aboService.findById(id);
    if (!opt.isPresent()) {
      System.out.println("Not found.");
      return;
    }
    Abonnement a = opt.get();
    System.out.print("New name (empty=keep " + a.getNomService() + "): ");
    String nom = sc.nextLine().trim();
    if (!nom.isEmpty()) {
      a.setNomService(nom);
    }
    System.out.print("New amount (empty=keep " + a.getMontantMensuel() + "): ");
    String m = sc.nextLine().trim();
    if (!m.isEmpty()) {
      try {
        a.setMontantMensuel(Double.parseDouble(m));
      } catch (NumberFormatException e) {
        System.out.println("Invalid amount, kept old.");
      }
    }
    System.out.print("New status ACTIVE/SUSPENDU/RESILIE (empty=keep " + a.getStatut() + "): ");
    String s = sc.nextLine().trim().toUpperCase();
    if (!s.isEmpty()) {
      try {
        a.setStatut(AbonnementStatus.valueOf(s));
      } catch (IllegalArgumentException e) {
        System.out.println("Invalid status, kept old.");
      }
    }
    if (a instanceof AbonnementAvecEngagement) {
      System.out.print("New engagement months (empty=keep): ");
      String d = sc.nextLine().trim();
      if (!d.isEmpty()) {
        try {
          ((AbonnementAvecEngagement) a).setDureeEngagementMois(Integer.parseInt(d));
        } catch (NumberFormatException e) {
          System.out.println("Invalid duration, kept old.");
        }
      }
    }
    aboService.update(a);
    System.out.println("Updated: " + a);
  }

  private void deleteAbo() {
    System.out.print("Subscription id to delete: ");
    String id = sc.nextLine().trim();
    boolean ok = aboService.delete(id);
    System.out.println(ok ? "Deleted (payments also deleted)." : "Not found.");
  }

  private void resilierAbo() {
    System.out.print("Subscription id to cancel: ");
    String id = sc.nextLine().trim();
    boolean ok = aboService.resilier(id);
    System.out.println(ok ? "Cancelled, history kept (end=today)." : "Not found.");
  }

  private void listPaiementsByAbo() {
    System.out.print("Subscription id: ");
    String id = sc.nextLine().trim();
    List<Paiement> list = payService.findByAbonnement(id);
    if (list.isEmpty()) {
      System.out.println("No payments for this subscription.");
      return;
    }
    for (Paiement p : list) {
      System.out.println(p);
    }
  }

  private void payerEcheance() {
    System.out.print("Payment id (echeance NON_PAYE) OR type NEW to record new: ");
    String in = sc.nextLine().trim();
    if (in.equalsIgnoreCase("NEW")) {
      System.out.print("Subscription id: ");
      String idAbo = sc.nextLine().trim();
      if (!aboService.findById(idAbo).isPresent()) {
        System.out.println("Subscription not found.");
        return;
      }
      System.out.print("Due date (yyyy-MM-dd): ");
      String sd = sc.nextLine().trim();
      if (!DateUtil.isValid(sd)) {
        System.out.println("Invalid date.");
        return;
      }
      TypePaiement type = askType();
      Paiement p = payService.enregistrer(idAbo, DateUtil.parse(sd), type);
      System.out.println("Recorded: " + p);
      return;
    }
    TypePaiement type = askType();
    boolean ok = payService.payerEcheance(in, type);
    System.out.println(ok ? "Marked as PAYE." : "Payment id not found.");
  }

  private TypePaiement askType() {
    System.out.print("Type (CARD/CASH): ");
    String t = sc.nextLine().trim().toUpperCase();
    try {
      return TypePaiement.valueOf(t);
    } catch (Exception e) {
      System.out.println("Invalid type, using CASH.");
      return TypePaiement.CASH;
    }
  }

  private void updatePaiement() {
    System.out.print("Payment id: ");
    String id = sc.nextLine().trim();
    Optional<Paiement> opt = payService.findById(id);
    if (!opt.isPresent()) {
      System.out.println("Not found.");
      return;
    }
    Paiement p = opt.get();
    System.out.print("New status PAYE/NON_PAYE/EN_RETARD (empty=keep " + p.getStatut() + "): ");
    String s = sc.nextLine().trim().toUpperCase();
    if (!s.isEmpty()) {
      try {
        p.setStatut(PaiementStatus.valueOf(s));
        if (p.getStatut() == PaiementStatus.PAYE && p.getDatePaiement() == null) {
          p.setDatePaiement(LocalDate.now());
        }
      } catch (IllegalArgumentException e) {
        System.out.println("Invalid status, kept old.");
      }
    }
    System.out.print("New type CARD/CASH (empty=keep " + p.getType() + "): ");
    String t = sc.nextLine().trim().toUpperCase();
    if (!t.isEmpty()) {
      try {
        p.setType(TypePaiement.valueOf(t));
      } catch (IllegalArgumentException e) {
        System.out.println("Invalid type, kept old.");
      }
    }
    payService.update(p);
    System.out.println("Updated: " + p);
  }

  private void deletePaiement() {
    System.out.print("Payment id to delete: ");
    String id = sc.nextLine().trim();
    System.out.println(payService.delete(id) ? "Deleted." : "Not found.");
  }

  private void showImpayes() {
    List<Paiement> list = payService.findImpayesAvecEngagement();
    if (list.isEmpty()) {
      System.out.println("No missed payments (with engagement).");
      return;
    }
    for (Paiement p : list) {
      System.out.println(p);
    }
    System.out.println("Total unpaid (with engagement): " + payService.totalImpayeAvecEngagement());
  }

  private void showSommePayee() {
    System.out.print("Subscription id: ");
    String id = sc.nextLine().trim();
    System.out.println("Total paid: " + payService.sommePayee(id));
  }

  private void showLast5() {
    List<Paiement> last = payService.last5Global();
    if (last.isEmpty()) {
      System.out.println("No payments yet.");
      return;
    }
    for (Paiement p : last) {
      System.out.println(p);
    }
  }

  private void rapports() {
    System.out.println("1. Monthly  2. Yearly  3. Unpaid summary");
    System.out.print("Choice: ");
    String c = sc.nextLine().trim();
    if (c.equals("1")) {
      System.out.print("Year (ex 2026): ");
      int y = Integer.parseInt(sc.nextLine().trim());
      System.out.print("Month (1-12): ");
      int m = Integer.parseInt(sc.nextLine().trim());
      System.out.println("Paid total: " + payService.totalPayeForMonth(y, m));
      System.out.println("Unpaid count: " + payService.countUnpaidForMonth(y, m));
    } else if (c.equals("2")) {
      System.out.print("Year (ex 2026): ");
      int y = Integer.parseInt(sc.nextLine().trim());
      System.out.println("Paid total: " + payService.totalPayeForYear(y));
      System.out.println("Unpaid count: " + payService.countUnpaidForYear(y));
    } else {
      List<Paiement> list = payService.findImpayesAvecEngagement();
      System.out.println("Unpaid count (with engagement): " + list.size());
      System.out.println("Unpaid total: " + payService.totalImpayeAvecEngagement());
    }
  }
}
