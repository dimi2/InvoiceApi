package dsk.invoiceapi.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Holder for invoices provided by the customer.
 * This emulates 'in memory database'.
 */
public class InvoicesHolder implements Serializable {
    protected List<Invoice> invoices = new ArrayList<>();

    public List<Invoice> getInvoices() {
        return invoices;
    }

    public void setInvoices(List<Invoice> invoices) {
        this.invoices = invoices;
    }

    public void addInvoice(Invoice invoice) {
        invoices.add(invoice);
    }

    public void removeInvoice(Invoice invoice) {
        invoices.remove(invoice);
    }
}
