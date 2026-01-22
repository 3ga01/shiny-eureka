package com.emmanuel.user_service.utility;

import java.awt.*;
import lombok.experimental.UtilityClass;

@UtilityClass
public class PASTEL_COLORS {

  public static final Color[] COLORS = {
    new Color(173, 216, 230), // Light blue
    new Color(255, 182, 193), // Light pink
    new Color(152, 251, 152), // Light green
    new Color(255, 255, 204), // Light yellow
    new Color(221, 160, 221), // Light purple
    new Color(255, 218, 185), // Light peach
    new Color(175, 238, 238), // Light cyan
    new Color(240, 128, 128), // Light coral
    new Color(144, 238, 144), // Light seagreen
    new Color(230, 230, 250), // Lavender
    new Color(255, 228, 225), // Misty rose
    new Color(176, 224, 230), // Powder blue
    new Color(255, 250, 205), // Lemon chiffon
    new Color(245, 245, 220), // Beige
    new Color(255, 239, 213) // Papaya whip
  };

  public static Color getColorForString(String input) {
    if (input == null || input.trim().isEmpty()) {
      return COLORS[0];
    }

    int hash = input.hashCode();
    int index = Math.abs(hash) % COLORS.length;
    return COLORS[index];
  }

  public static Color getColorByIndex(int index) {
    int safeIndex = Math.abs(index) % COLORS.length;
    return COLORS[safeIndex];
  }

  public static Color getRandomColor() {
    int randomIndex = (int) (Math.random() * COLORS.length);
    return COLORS[randomIndex];
  }
}
