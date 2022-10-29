package dsk.invoiceapi.api;

import dsk.invoiceapi.domain.Customer;

import java.io.Serializable;
import java.util.List;

/**
 * Invoice sum response, returned by the REST service.
 */
public class SumResponse implements Serializable {
    protected List<Customer> customers;
    protected String currency;

    public SumResponse() {
        super();
    }

    public SumResponse(String currency) {
        this();
        this.currency = currency;
    }

    public List<Customer> getCustomers() {
        return customers;
    }

    public void setCustomers(List<Customer> customers) {
        this.customers = customers;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}
