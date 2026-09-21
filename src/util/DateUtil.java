package util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class DateUtil {
  private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

  public static LocalDate parse(String s) {
    return LocalDate.parse(s.trim(), FMT);
  }

  public static String format(LocalDate d) {
    if (d == null) {
      return "-";
    }
    return d.format(FMT);
  }

  public static boolean isValid(String s) {
    try {
      parse(s);
      return true;
    } catch (DateTimeParseException e) {
      return false;
    }
  }
}
