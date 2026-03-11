package com.mai.backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.SecureRandom;

@RestController
@RequestMapping("/api")
public class PasswordController {

  private static final String LOWER = "abcdefghijklmnopqrstuvwxyz";
  private static final String UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
  private static final String DIGITS = "0123456789";
  private static final String SPECIAL = "#.,!@&^%*";
  private static final SecureRandom RANDOM = new SecureRandom();

  @GetMapping("/password")
  public PasswordResponse generatePassword(
          @RequestParam(defaultValue = "12") int length
  ) {
    if (length < 8 || length > 16) {
      throw new IllegalArgumentException("Length must be between 8 and 16");
    }

    StringBuilder password = new StringBuilder(length);

    password.append(LOWER.charAt(RANDOM.nextInt(LOWER.length())));
    password.append(UPPER.charAt(RANDOM.nextInt(UPPER.length())));
    password.append(DIGITS.charAt(RANDOM.nextInt(DIGITS.length())));
    password.append(SPECIAL.charAt(RANDOM.nextInt(SPECIAL.length())));

    String allChars = LOWER + UPPER + DIGITS + SPECIAL;
    for (int i = 4; i < length; i++) {
      password.append(allChars.charAt(RANDOM.nextInt(allChars.length())));
    }
    return new PasswordResponse(shuffle(password.toString()));
  }

  private String shuffle(String input) {
    char[] chars = input.toCharArray();
    for (int i = chars.length - 1; i > 0; i--) {
      int j = RANDOM.nextInt(i + 1);
      char temp = chars[i];
      chars[i] = chars[j];
      chars[j] = temp;
    }
    return new String(chars);
  }

  public record PasswordResponse(String password) {
  }

}
