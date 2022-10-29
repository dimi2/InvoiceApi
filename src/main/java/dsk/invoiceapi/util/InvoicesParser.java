package dsk.invoiceapi.util;

import dsk.invoiceapi.domain.DocType;
import dsk.invoiceapi.domain.Invoice;
import dsk.invoiceapi.domain.InvoicesHolder;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

/**
 * Invoices list parser (from tabular CSV format).
 */
@Component
public class InvoicesParser {
    // Names may contain cyrillic, kanji, hangul, slashes etc - do not restrict too much.
    public static final String RE_CUSTOMER = "^.+$";
    public static final String RE_VAT = "^([a-zA-Z0-9]){5,20}$";
    public static final String RE_DOCNUMBER = "^\\d+$";
    public static final String RE_DOCTYPE = "^\\d{1}$";
    public static final String RE_CURRENCY = "^([a-zA-Z]){3}$";
    public static final String RE_TOTAL = "^\\d+$";
    protected static final int HEADERS_COUNT = 7;
    // Match comma if the previous char is not backslash (to allow comma escaping in the CSV).
    protected static final String RE_SEPARATOR = "(?<!\\\\),";

    public InvoicesHolder parse(InputStream in) throws IOException {
        InvoicesHolder holder = new InvoicesHolder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
            int lineNumber = 1;

            // Check the headers.
            String[] headers = reader.readLine().split(RE_SEPARATOR, HEADERS_COUNT);
            if (headers.length != HEADERS_COUNT) {
                throw new IllegalArgumentException("Invalid invoices table structure. Line: " + lineNumber);
            }

            // Process the data lines.
            while (reader.ready()) {
                lineNumber++;
                String[] line = reader.readLine().split(RE_SEPARATOR, HEADERS_COUNT);
                if (line.length != HEADERS_COUNT) {
                    throw new IllegalArgumentException("Invalid invoices table headers");
                }
                try {
                    String customerName = (String) acceptCell(0, line[0]);
                    String vatNumber = (String) acceptCell(1, line[1]);
                    Long docNumber = (Long) acceptCell(2, line[2]);
                    DocType docType = (DocType) acceptCell(3, line[3]);
                    Long parentDoc = (Long) acceptCell(4, line[4]);
                    String currency = (String) acceptCell(5, line[5]);
                    Double total = (Double) acceptCell(6, line[6]);

                    Invoice invoice = new Invoice();
                    invoice.setCustomerName(customerName);
                    invoice.setVatNumber(vatNumber);
                    invoice.setDocNumber(docNumber);
                    invoice.setDocType(docType);
                    invoice.setParentDoc(parentDoc);
                    invoice.setCurrency(currency);
                    invoice.setTotal(total);
                    holder.addInvoice(invoice);
                }
                catch (Exception exc) {
                    throw new IllegalArgumentException("Error parsing invoices at line: " + lineNumber, exc);
                }
            } //
            integrityCheck(holder);
        }
        return holder;
    }

    /**
     * Do additional integrity checks on the parsed invoices.
     * @param holder Invoices holder.
     * @throws IllegalArgumentException if the integrity check fails.
     */
    protected void integrityCheck(InvoicesHolder holder) throws IllegalArgumentException {
        // Check if the included invoice parents are available.
        Set<Long> availableDocs = new HashSet<>();
        Set<Long> requiredParentDocs = new HashSet<>();
        for (Invoice invoice : holder.getInvoices()) {
            availableDocs.add(invoice.getDocNumber());
            Long parentDoc = invoice.getParentDoc();
            if (parentDoc != null) {
                requiredParentDocs.add(parentDoc);
            }
        }
        requiredParentDocs.removeAll(availableDocs);
        if (!requiredParentDocs.isEmpty()) {
            throw new IllegalArgumentException("Missing invoice parent docs: " + requiredParentDocs);
        }
    }

    /**
     * Accept the cell values from invoices table, after validation. If the validation fails, the error
     * message indicates the wrong cell to make data fixing easier.
     * @param cellValue Cell value to be accepted.
     * @return Accepted value.
     */
    protected Object acceptCell(int cell, String cellValue) {
        Object value = null;

        // Trim cell values and strip enclosing quotes (if any).
        String v = cellValue.trim();
        if (v.startsWith("\"") && v.endsWith("\"")) {
            v = v.substring(1, v.length() - 1);
        }
        if (!v.isBlank()) {
            // Check the corresponding call values against the expected format.
            switch (cell) {
                case 0:
                    // Customer name.
                    if (!v.matches(RE_CUSTOMER)) {
                        throw new IllegalArgumentException(String.format("Invalid customer (%s)",
                            cellValue));
                    }
                    value = v;
                    break;
                case 1:
                    // Vat number.
                    if (!v.matches(RE_VAT)) {
                        throw new IllegalArgumentException(String.format("Invalid VAT number (%s)",
                            cellValue));
                    }
                    value = v;
                    break;
                case 2:
                    // Document number.
                    if (!v.matches(RE_DOCNUMBER)) {
                        throw new IllegalArgumentException(String.format("Invalid document number (%s)",
                            cellValue));
                    }
                    value = Long.parseLong(v);
                    break;
                case 3:
                    // Document type.
                    if (!v.matches(RE_DOCTYPE)) {
                        throw new IllegalArgumentException(String.format("Invalid document type (%s)",
                            cellValue));
                    }
                    value = DocType.fromCode(Integer.parseInt(v));
                    break;
                case 4:
                    // Parent document.
                    if (!v.matches(RE_DOCNUMBER)) {
                        throw new IllegalArgumentException(String.format("Invalid parent document (%s)",
                            cellValue));
                    }
                    value = Long.parseLong(v);
                    break;
                case 5:
                    // Currency.
                    if (!v.matches(RE_CURRENCY)) {
                        throw new IllegalArgumentException(String.format("Invalid currency (%s)", cellValue));
                    }
                    value = v;
                    break;
                case 6:
                    // Total.
                    if (!v.matches(RE_TOTAL)) {
                        throw new IllegalArgumentException(String.format("Invalid total (%s)", cellValue));
                    }
                    value = Double.parseDouble(v);
            }
        }

        return value;
    }
}
