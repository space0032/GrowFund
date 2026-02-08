package com.growfund.seedtowealth.utils;

import java.text.NumberFormat;
import java.util.Locale;

public class MoneyUtils {

    private static final Locale INDIA_LOCALE = new Locale("en", "IN");
    private static final NumberFormat CURRENCY_FORMAT = NumberFormat.getCurrencyInstance(INDIA_LOCALE);

    /**
     * Formats a long amount as Indian Rupee currency.
     * Example: 50000 -> "₹50,000"
     */
    public static String formatCurrency(long amount) {
        // Remove decimals by casting to long if needed, but input is long here
        String formatted = CURRENCY_FORMAT.format(amount);
        // Remove .00 if present (common in some implementations of CurrencyInstance)
        if (formatted.endsWith(".00")) {
            formatted = formatted.substring(0, formatted.length() - 3);
        }
        return formatted;
    }

    /**
     * Formats a double amount as Indian Rupee currency.
     * Useful for calculations that result in doubles, but display as integer money.
     */
    public static String formatCurrency(double amount) {
        return formatCurrency(Math.round(amount));
    }
}
