package dsk.invoiceapi.util;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Test for CurrencyConverter.
 */
class CurrencyConverterTest {

    @Test
    public void validRates() throws IOException {
        // Load currency exchange rates from file.
        var exchangeRates = new String(new ClassPathResource("ExchangeRates1.txt").
            getInputStream().readAllBytes());

        // Check the currency conversion results.
        var converter = new CurrencyConverter(exchangeRates);
        assertEquals(9.87, converter.convert(10, "EUR", "USD"));
        assertEquals(8.78, converter.convert(10, "EUR", "GBP"));
        assertEquals(11.39, converter.convert(10, "GBP", "EUR"));
        assertEquals(11.25, converter.convert(10, "GBP", "USD"));
        assertEquals(1.000, converter.convert(1, "EUR", "EUR"));
    }

    @Test
    public void invalidRates() throws IOException {
        // Load invalid exchange rates from file.
        var exchangeRates = new String(new ClassPathResource("ExchangeRates2.txt").
            getInputStream().readAllBytes());

        // Thy to parse the invalid exchange rates.
        try {
            new CurrencyConverter(exchangeRates);
            fail("Parsing invalid rates must fail");
        }
        catch (IllegalArgumentException e) {
            assertEquals("Invalid currency exchange rate: 'US:0.987'", e.getMessage());
        }
    }

}