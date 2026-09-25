# SubTrack

A console-based subscription tracker. Manage subscriptions, generate their monthly
payment due dates, record what you actually paid, and produce simple financial reports.

The domain model and identifiers are in French (`Abonnement`, `Paiement`, `echeance`,
`resilier`); the console UI is in English.

## Features

- Two subscription flavours: **with** and **without** a binding commitment period
- Automatic generation of one monthly payment due date (`echeance`) per subscription
- Record, update and delete payments, tracked as `CARD` or `CASH`
- Cancel a subscription while keeping its payment history
- Reports: paid totals per month/year, unpaid counts, and an unpaid summary

## Requirements

- JDK 17 or newer (uses `java.time` and pattern matching for `instanceof`)

There is no build tool. The project is plain Java and compiles with `javac`.

## How to run

```bash
javac -d bin $(find src -name '*.java')
java -cp bin Main
```

The project is also configured for **Eclipse** — import the folder directly and
run `Main.java`. The classpath and output folder live in `.classpath`.

## Project structure

```
src/
├── Main.java                       Entry point
├── models/                         Domain entities
│   ├── Abonnement.java              Abstract base subscription
│   ├── AbonnementAvecEngagement.java  Subscription with a commitment period
│   ├── AbonnementSansEngagement.java  Subscription without one
│   └── Paiement.java                 A single payment / due date
├── enums/
│   ├── AbonnementStatus.java        ACTIVE, SUSPENDU, RESILIE
│   ├── PaiementStatus.java           PAYE, NON_PAYE, EN_RETARD
│   └── TypePaiement.java             CARD, CASH
├── dao/                            In-memory data access
│   ├── AbonnementDAO.java
│   └── PaiementDAO.java
├── services/                       Business logic
│   ├── AbonnementService.java
│   └── PaiementService.java
├── ui/
│   └── ConsoleMenu.java            Interactive menu
└── util/
    └── DateUtil.java               yyyy-MM-dd parsing, formatting, validation
```

## Domain model

### Abonnement (subscription)

| Field | Type | Notes |
| --- | --- | --- |
| `id` | `String` | UUID, generated in the constructor |
| `nomService` | `String` | Service name, e.g. `Netflix` |
| `montantMensuel` | `double` | Monthly price |
| `dateDebut` | `LocalDate` | Start date |
| `dateFin` | `LocalDate` | End date |
| `statut` | `AbonnementStatus` | `ACTIVE`, `SUSPENDU`, `RESILIE` |

`Abonnement` is abstract. The two concrete types are:

- **`AbonnementAvecEngagement`** — adds `dureeEngagementMois` (`int`), the length of the
  binding commitment in months. Also overrides `toString()` to show `engagement=N mois`.
- **`AbonnementSansEngagement`** — a plain subscription, cancellable at any time. Adds
  no fields.

### Paiement (payment / due date)

| Field | Type | Notes |
| --- | --- | --- |
| `idPaiement` | `String` | UUID, generated in the constructor |
| `idAbonnement` | `String` | Foreign key to the owning subscription |
| `dateEcheance` | `LocalDate` | When the payment is due |
| `datePaiement` | `LocalDate` | When it was actually paid, `null` if unpaid |
| `type` | `TypePaiement` | `CARD` or `CASH` |
| `statut` | `PaiementStatus` | `PAYE`, `NON_PAYE`, `EN_RETARD` |

## Menu reference

| Choice | Action |
| --- | --- |
| 1 | Create subscription (prompts for type: with/without engagement) |
| 2 | List subscriptions |
| 3 | Update subscription |
| 4 | Delete subscription (also deletes its payments) |
| 5 | Cancel subscription (`resilier`) — sets end date to today, keeps history |
| 6 | Show payments of a subscription |
| 7 | Pay a due date, or type `NEW` to record a fresh payment |
| 8 | Update payment |
| 9 | Delete payment |
| 10 | Show missed payments + total (with engagement only) |
| 11 | Show total paid for a subscription |
| 12 | Show last 5 payments (global) |
| 13 | Financial reports — monthly, yearly, unpaid summary |
| 0 | Exit |

Dates are entered as `yyyy-MM-dd`, e.g. `2026-01-10`.

## How due dates are generated

`AbonnementService.genererEcheances` walks month by month from `dateDebut` to
`dateFin` (**both inclusive**) and creates one `NON_PAYE` / `CASH` payment per month,
keeping the start day of month. If a month is short, the day is clamped to the last
valid day (a subscription starting on the 31st is billed on the 28th in February).

The method is idempotent: it skips months that already have a payment, so it is safe
to re-run after changing the dates of a subscription.

## Demo data

On startup, if there are no subscriptions yet, the app seeds two examples:

- **Netflix** — 12.99/month, 2026-01-10 to 2026-06-10, 6-month engagement
- **Spotify** — 9.99/month, 2026-03-05 to 2026-08-05, no engagement

The first Netflix due date is marked as paid by card.

## Current limitations

Worth knowing before building on top of this:

- **Storage is in-memory only.** The DAOs hold plain `ArrayList`s. Everything is lost
  on exit; there is no database and no file persistence.
- **The commitment period is not enforced.** `dureeEngagementMois` is stored and
  displayed, but `resilier` cancels both subscription types identically — it never
  checks whether you are still inside the commitment window, and there is no
  early-termination fee.
- **Payments carry no amount.** A `Paiement` records that a due date exists and whether
  it was paid, but not how much. Every report total is derived from the subscription's
  current `montantMensuel`, so a price change retroactively alters historical totals.
- **The unpaid summary ignores commitment-free subscriptions.** Choices 10 and 13.3
  filter on `instanceof AbonnementAvecEngagement`, so subscriptions without an
  engagement never appear there.
- **`EN_RETARD` (overdue) is never set automatically.** Payments are generated as
  `NON_PAYE`; nothing inspects today's date to flag overdue ones. It can only be set
  manually via choice 8.

## Related files

- `SubTrack_Presentation_Dark_Kara.pptx` — project presentation
