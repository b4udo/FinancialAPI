package financial.util;

import org.springframework.stereotype.Component;

@Component
public class DataMaskingUtil {

  public String maskEmail(String email) {
    if (email == null) return null;
    String[] parts = email.split("@");
    if (parts.length != 2) return email;

    String name = parts[0];
    String domain = parts[1];

    if (name.length() <= 2) {
      return name + "@" + domain;
    }

    return name.substring(0, 2) + "***@" + domain;
  }

  public String maskPhoneNumber(String phoneNumber) {
    if (phoneNumber == null) return null;
    String digits = phoneNumber.replaceAll("[^0-9]", "");
    if (digits.length() < 4) return phoneNumber;

    return "***" + digits.substring(digits.length() - 4);
  }

  public String maskIban(String iban) {
    if (iban == null) return null;
    if (iban.length() < 8) return iban;

    return iban.substring(0, 4) + "****" + iban.substring(iban.length() - 4);
  }

  public String maskCreditCard(String cardNumber) {
    if (cardNumber == null) return null;
    String digits = cardNumber.replaceAll("[^0-9]", "");
    if (digits.length() < 4) return cardNumber;

    return "****-****-****-" + digits.substring(digits.length() - 4);
  }
}
