package dsk.invoiceapi.domain;

import java.io.Serializable;

/**
 * Represents a customer.
 */
public class Customer implements Serializable {
    protected String name;
    protected Double balance = 0.0; // BigDecimal could also be used for better fractional precision.

    public Customer() {
        super();
    }

    public Customer(String name) {
        this();
        setName(name);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

}
