package dsk.invoiceapi.domain;

import java.io.Serializable;

/**
 * Represents currency rate.
 */
public class CurrencyRate implements Serializable {
    protected String currency;
    protected double rate;

    public CurrencyRate() {
    }

    public CurrencyRate(String currency, double rate) {
        this();
        setCurrency(currency);
        setRate(rate);
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    @Override
    public String toString() {
        return "{" + currency + ": " + rate;
    }
}
