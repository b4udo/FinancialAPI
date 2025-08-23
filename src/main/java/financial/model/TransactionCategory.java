package financial.model;

public enum TransactionCategory {
  // Entrate
  SALARY("Stipendio"),
  INVESTMENT_RETURN("Rendimento Investimenti"),
  TRANSFER_IN("Trasferimento in Entrata"),

  // Spese
  BILLS("Bollette"),
  GROCERIES("Spesa"),
  ENTERTAINMENT("Intrattenimento"),
  TRANSPORT("Trasporti"),
  HEALTH("Salute"),

  // Investimenti
  STOCK_PURCHASE("Acquisto Azioni"),
  BOND_PURCHASE("Acquisto Obbligazioni"),
  CRYPTO_PURCHASE("Acquisto Crypto");

  private final String description;

  TransactionCategory(String description) {
    this.description = description;
  }

  public String getDescription() {
    return description;
  }
}
