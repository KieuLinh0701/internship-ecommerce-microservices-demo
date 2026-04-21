package com.teamsolution.order.utils;

import com.teamsolution.common.core.util.UuidUtils;

public class OrderNumberUtils {
  public static String generateOrderNumber() {
    String prefix = "TCS";

    String date =
        java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyMMdd"));

    String random = UuidUtils.generate().toString().replace("-", "").substring(0, 6).toUpperCase();

    return prefix + date + random;
  }
}
