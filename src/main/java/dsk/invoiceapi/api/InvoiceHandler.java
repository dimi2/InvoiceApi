package dsk.invoiceapi.api;

import dsk.invoiceapi.domain.Customer;
import dsk.invoiceapi.domain.Invoice;
import dsk.invoiceapi.domain.InvoicesHolder;
import dsk.invoiceapi.util.CurrencyConverter;
import dsk.invoiceapi.util.InvoicesParser;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

import static dsk.invoiceapi.api.InvoiceHandler.API_PREFIX;
import static dsk.invoiceapi.util.InvoicesParser.RE_CURRENCY;
import static dsk.invoiceapi.util.InvoicesParser.RE_VAT;

/**
 * REST handler for invoice summing calls.
 */
@RestController
@RequestMapping(value = API_PREFIX)
public class InvoiceHandler {
    public static final String API_PREFIX = "/api/v1";
    protected InvoicesParser invoicesParser;
    protected Log logger = LogFactory.getLog(getClass());

    public InvoiceHandler(InvoicesParser invParser) {
        invoicesParser = invParser;
    }

    @PostMapping(value = "sumInvoices", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SumResponse> sumInvoices(@RequestParam("file") MultipartFile file,
                 @RequestParam("exchangeRates") String exchangeRates,
                 @RequestParam("outputCurrency") @Pattern(regexp = RE_CURRENCY) String outputCurrency,
                 @RequestParam(value = "customerVat", required = false)
                     @Pattern(regexp = RE_VAT) @NotBlank String customerVat,
                 HttpServletRequest request) throws IOException {
        // Log the external calls.
        logger.info("Called sumInvoice from IP: " + request.getRemoteAddr());

        // Prepare response headers.
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

        // Parse the provided input.
        CurrencyConverter currencyConverter = new CurrencyConverter(exchangeRates);
        InvoicesHolder invoicesHolder = invoicesParser.parse(file.getInputStream());

        // Sum the invoice totals by customer.
        Map<String, Customer> customers = new TreeMap<>();
        for (Invoice invoice : invoicesHolder.getInvoices()) {
            if ((customerVat != null) && !customerVat.equals(invoice.getVatNumber())) {
                // The caller is interested in only one customer - skip the others.
                continue;
            }

            // Calculate the balance per customer.
            Customer customer = customers.computeIfAbsent(invoice.getCustomerName(),
                key -> new Customer(key));
            double convTotal = currencyConverter.convert(invoice.getTotal(),
                invoice.getCurrency(), outputCurrency);
            switch (invoice.getDocType()) {
                case INVOICE, DEBIT_NOTE -> customer.setBalance(customer.getBalance() + convTotal);
                case CREDIT_NOTE -> customer.setBalance(customer.getBalance() - convTotal);
            }
        }

        // Signal for 'no results'.
        if (customers.isEmpty()) {
            new ResponseEntity<>(headers, HttpStatus.NOT_FOUND);
        }

        // Prepare the response.
        logger.info(String.format("Returned %d records", customers.size()));
        SumResponse response = new SumResponse(outputCurrency);
        response.setCustomers(new ArrayList<>(customers.values()));
        return new ResponseEntity<>(response, headers, HttpStatus.OK);
    }

}
