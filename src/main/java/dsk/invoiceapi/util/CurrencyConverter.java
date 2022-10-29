package dsk.invoiceapi.util;

import dsk.invoiceapi.domain.CurrencyRate;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Currency converter.
 */
public class CurrencyConverter {
    public static final Pattern RE_CURRENCY_RATE = Pattern.compile("^([a-zA-Z]{3}):(\\d*(.\\d+)*)$");
    protected Map<String, Double> rates;
    protected String defaultCurrency;

    /**
     * Create converter with given exchange rates.
     * @param exchangeRates Exchange rates.
     */
    public CurrencyConverter(Set<CurrencyRate> exchangeRates) {
        parseExchangeRates(exchangeRates);
    }

    /**
     * Create converter, after parsing currency exchange rates list. It is comma separated list with elements
     * in this format ("currencyCode:rate").
     * @param exchangeRatesList Comma separated list with currency rates.
     */
    public CurrencyConverter(String exchangeRatesList) {
        parseExchangeRates(exchangeRatesList);
    }

    /**
     * Convert given amount of money from one currency to another.
     * @param amount Amount of money.
     * @param fromCurrency Source currency code (three letters - ISO 4217).
     * @param toCurrency Target currency code (three letters - ISO 4217)
     * @return Converted amount (rounded to second digit after the decimal point).
     */
    public double convert(double amount, String fromCurrency, String toCurrency) {
        double crossRate = getRate(toCurrency) / getRate(fromCurrency);
        return round(amount * crossRate, 2);
    }

    /**
     * Get the exchange rate of given currency, against the default currency.
     * @param toCurrency Target currency.
     * @return Currency exchange rate.
     */
    public double getRate(String toCurrency) {
        Double rate = rates.get(toCurrency.toUpperCase());
        if (rate == null) {
            throw new IllegalArgumentException("Unsupported currency: " + toCurrency);
        }
        return rate;
    }

    /**
     * Round specified float point number to desired precision.
     * Example: for d = 17.4960 and precision = 2, the result will be 17.50.
     * It is useful to avoid endless fraction numbers like 2.6457513110645905905016157536393.
     * @param d The float number.
     * @param precision Desired precision (digits after the point).
     * @return Rounded up number.
     */
    public double round(Double d, int precision) {
        var fraction = Math.pow(10.0, precision);
        return Math.ceil(d * fraction) / fraction;
    }

    protected void parseExchangeRates(String exchangeRatesList) {
        if ((exchangeRatesList == null) || exchangeRatesList.isBlank()) {
            throw new IllegalArgumentException("Currency exchange rates list is empty");
        }

        Set<CurrencyRate> rates = new HashSet<>();
        String[] list = exchangeRatesList.split(",");
        for (String er : list) {
            Matcher erMatcher = RE_CURRENCY_RATE.matcher(er);
            if (erMatcher.find()) {
                var currencyCode = erMatcher.group(1).toUpperCase();
                var currencyValue = Double.parseDouble(erMatcher.group(2));
                rates.add(new CurrencyRate(currencyCode, currencyValue));
            }
            else {
                throw new IllegalArgumentException(String.format("Invalid currency exchange rate: '%s'", er));
            }
        }

        parseExchangeRates(rates);
    }

    protected void parseExchangeRates(Set<CurrencyRate> exchangeRates) {
        Map<String, Double> baseRates = new ConcurrentHashMap<>();
        String defCurrency = null;
        for (CurrencyRate exchangeRate : exchangeRates) {
            if ((defCurrency == null) && exchangeRate.getRate() == 1) {
                defCurrency = exchangeRate.getCurrency();
            }
            baseRates.put(exchangeRate.getCurrency(), exchangeRate.getRate());
        }
        if (defCurrency == null) {
            throw new IllegalArgumentException("No default currency");
        }
        rates = baseRates;
        defaultCurrency = defCurrency;
    }

}
