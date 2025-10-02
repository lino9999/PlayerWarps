package com.Lino.playerWarps.utils;

import net.md_5.bungee.api.ChatColor;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ColorUtils {

    private static final Pattern GRADIENT_PATTERN = Pattern.compile("<gradient:#([0-9A-Fa-f]{6}):#([0-9A-Fa-f]{6})>(.*?)</gradient>");
    private static final Pattern HEX_PATTERN = Pattern.compile("&#([0-9A-Fa-f]{6})");

    public static String applyGradient(String text) {
        Matcher matcher = GRADIENT_PATTERN.matcher(text);
        StringBuffer buffer = new StringBuffer();

        while (matcher.find()) {
            String startHex = matcher.group(1);
            String endHex = matcher.group(2);
            String content = matcher.group(3);

            String gradient = createGradient(content, startHex, endHex);
            matcher.appendReplacement(buffer, Matcher.quoteReplacement(gradient));
        }

        matcher.appendTail(buffer);
        String result = buffer.toString();

        result = applyHexColors(result);
        result = ChatColor.translateAlternateColorCodes('&', result);

        return result;
    }

    private static String createGradient(String text, String startHex, String endHex) {
        Color startColor = Color.decode("#" + startHex);
        Color endColor = Color.decode("#" + endHex);

        StringBuilder result = new StringBuilder();
        int length = text.length();

        for (int i = 0; i < length; i++) {
            char c = text.charAt(i);

            if (c == ' ') {
                result.append(c);
                continue;
            }

            float ratio = (float) i / (length - 1);
            int r = (int) (startColor.getRed() + ratio * (endColor.getRed() - startColor.getRed()));
            int g = (int) (startColor.getGreen() + ratio * (endColor.getGreen() - startColor.getGreen()));
            int b = (int) (startColor.getBlue() + ratio * (endColor.getBlue() - startColor.getBlue()));

            Color color = new Color(r, g, b);
            result.append(ChatColor.of(color)).append(c);
        }

        return result.toString();
    }

    private static String applyHexColors(String text) {
        Matcher matcher = HEX_PATTERN.matcher(text);
        StringBuffer buffer = new StringBuffer();

        while (matcher.find()) {
            String hex = matcher.group(1);
            matcher.appendReplacement(buffer, ChatColor.of("#" + hex).toString());
        }

        matcher.appendTail(buffer);
        return buffer.toString();
    }

    public static List<String> applyGradient(List<String> lines) {
        List<String> result = new ArrayList<>();
        for (String line : lines) {
            result.add(applyGradient(line));
        }
        return result;
    }
}