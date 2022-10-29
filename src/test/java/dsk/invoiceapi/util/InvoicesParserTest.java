package dsk.invoiceapi.util;

import dsk.invoiceapi.domain.DocType;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Test for InvoicesParser functionality.
 */
class InvoicesParserTest {

    @Test
    public void validInvoices() throws IOException {
        // Load invoices table from file.
        var invoicesData = new ClassPathResource("Invoices1.csv").getInputStream();
        // Parse them.
        var invoicesHolder = new InvoicesParser().parse(invoicesData);

        // Check the parsing results.
        var invoice1 = invoicesHolder.getInvoices().get(0);
        assertEquals("Vendor 1", invoice1.getCustomerName());
        assertEquals("123456789", invoice1.getVatNumber());
        assertEquals(1000000257L, invoice1.getDocNumber());
        assertEquals(DocType.INVOICE, invoice1.getDocType());
        assertNull(invoice1.getParentDoc());
        assertEquals("USD", invoice1.getCurrency());
        assertEquals(400, invoice1.getTotal());

        assertEquals(8, invoicesHolder.getInvoices().size());
    }

    @Test
    public void invalidInvoices() throws IOException {
        // Load invalid invoices table from file.
        var invoicesData = new ClassPathResource("Invoices2.csv").getInputStream();

        // Try to parse them.
        try {
            new InvoicesParser().parse(invoicesData);
            fail("Invalid invoices table must be rejected");
        }
        catch (IllegalArgumentException e) {
            assertEquals("Error parsing invoices at line: 5", e.getMessage());
            assertEquals("Invalid currency (100)", e.getCause().getMessage());
        }

    }
}