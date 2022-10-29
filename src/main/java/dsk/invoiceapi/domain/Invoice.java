package dsk.invoiceapi.domain;

import java.io.Serializable;

/**
 * Represents an invoice.
 */
public class Invoice implements Serializable {
    protected String customerName;
    protected String vatNumber;
    protected Long docNumber;
    protected DocType docType;
    protected Long parentDoc;
    protected String currency;
    protected Double total; // BigDecimal could also be used for better fractional precision.

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getVatNumber() {
        return vatNumber;
    }

    public void setVatNumber(String vatNumber) {
        this.vatNumber = vatNumber;
    }

    public Long getDocNumber() {
        return docNumber;
    }

    public void setDocNumber(Long docNumber) {
        this.docNumber = docNumber;
    }

    public DocType getDocType() {
        return docType;
    }

    public void setDocType(DocType docType) {
        this.docType = docType;
    }

    public Long getParentDoc() {
        return parentDoc;
    }

    public void setParentDoc(Long parentDoc) {
        this.parentDoc = parentDoc;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

}
