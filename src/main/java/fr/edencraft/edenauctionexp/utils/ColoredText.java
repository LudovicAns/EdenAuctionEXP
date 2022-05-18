package fr.edencraft.edenauctionexp.utils;


import net.md_5.bungee.api.ChatColor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ColoredText {

    private String coloredText;

    private final Pattern hexColorPattern = Pattern.compile("&#[a-fA-F0-9]{6}");

    public ColoredText(String coloredText) {
        this.coloredText = coloredText;
    }

    /**
     * This method doesn't work below version 1.16 of spigot.
     *
     * @return Treated text.
     */
    public String treat() {
        Matcher match = hexColorPattern.matcher(this.coloredText);
        while (match.find()) {
            String hexColorCode = coloredText.substring(match.start() + 1, match.end());
            String codeFormat = coloredText.substring(match.start(), match.end());

            coloredText = coloredText.replace(
                    codeFormat,
                    ChatColor.of(hexColorCode) + ""
            );
            match = hexColorPattern.matcher(coloredText);
        }

        return ChatColor.translateAlternateColorCodes('&', coloredText);
    }

}
