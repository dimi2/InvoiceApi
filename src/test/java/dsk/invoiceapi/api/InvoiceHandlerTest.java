package dsk.invoiceapi.api;

import dsk.invoiceapi.domain.Customer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static dsk.invoiceapi.api.InvoiceHandler.API_PREFIX;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test for Invoice REST handler.
 * The test runs embedded Tomcat server with the application deployed.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class InvoiceHandlerTest {

    @Autowired
    protected WebTestClient client;

    @Test
    public void sumInvoicesDefault() throws IOException {
        // Prepare the API request.
        String outputCurrency = "EUR";
        MultipartBodyBuilder bodyBuilder = prepareSumInvoicesRequest("Invoices1.csv", "ExchangeRates1.txt",
            outputCurrency, null);

        // Execute the API request.
        SumResponse response = client.post().uri(API_PREFIX + "/" + "sumInvoices")
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .body(BodyInserters.fromMultipartData(bodyBuilder.build()))
            .exchange()
            .expectStatus().isOk()
            .expectBody(SumResponse.class)
            .returnResult().getResponseBody();

        // Validate returned result.
        assertEquals(outputCurrency, response.getCurrency());
        Customer customer1 = response.getCustomers().get(0);
        assertEquals("Vendor 1", customer1.getName());
        assertEquals(1962.22, customer1.getBalance());
        Customer customer2 = response.getCustomers().get(1);
        assertEquals("Vendor 2", customer2.getName());
        assertEquals(697.36, customer2.getBalance());
        Customer customer3 = response.getCustomers().get(2);
        assertEquals("Vendor 3", customer3.getName());
        assertEquals(1580.64, customer3.getBalance());
        assertEquals(3, response.getCustomers().size());
    }

    @Test
    public void sumInvoicesFiltered() throws IOException {
        // Prepare the API request.
        String outputCurrency = "USD";
        MultipartBodyBuilder bodyBuilder = prepareSumInvoicesRequest("Invoices1.csv", "ExchangeRates1.txt",
            outputCurrency, "123456789");

        // Execute the API request.
        SumResponse response = client.post().uri(API_PREFIX + "/" + "sumInvoices")
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .body(BodyInserters.fromMultipartData(bodyBuilder.build()))
            .exchange()
            .expectStatus().isOk()
            .expectBody(SumResponse.class)
            .returnResult().getResponseBody();

        // Validate returned result.
        assertEquals(outputCurrency, response.getCurrency());
        Customer customer1 = response.getCustomers().get(0);
        assertEquals("Vendor 1", customer1.getName());
        assertEquals(1936.71, customer1.getBalance());
        assertEquals(1, response.getCustomers().size());
    }

    @Test
    public void invalidRequest() {
        client.post().uri(API_PREFIX + "/" + "sumInvoices")
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .body(BodyInserters.fromValue("Invalid Request"))
            .exchange()
            .expectStatus().isBadRequest();
    }

    /**
     * Prepare sum invoices service request.
     * @param invoicesFile Invoice file name (must be in the classpath).
     * @param exchangeRatesFile Exchange rates file name (must be in the classpath).
     * @param outputCurrency Desired output currency code (three letters - ISO 4217).
     * @param customerVat Vat number of single desired customer.
     * @return Prepared request body.
     * @throws IOException if file operation error occurs.
     */
    protected MultipartBodyBuilder prepareSumInvoicesRequest(String invoicesFile, String exchangeRatesFile,
                                       String outputCurrency, String customerVat) throws IOException {
        String exchangeRates = new String(new ClassPathResource(exchangeRatesFile).getInputStream()
            .readAllBytes(), StandardCharsets.UTF_8);
        MultipartBodyBuilder bodyBuilder = new MultipartBodyBuilder();
        bodyBuilder.part("file", new ClassPathResource(invoicesFile).getInputStream().readAllBytes())
            .header("Content-Disposition", "form-data; name=file; filename=" + invoicesFile);
        bodyBuilder.part("exchangeRates", exchangeRates);
        bodyBuilder.part("outputCurrency", outputCurrency.toUpperCase());
        if (customerVat != null) {
            bodyBuilder.part("customerVat", customerVat);
        }
        return bodyBuilder;
    }
}
