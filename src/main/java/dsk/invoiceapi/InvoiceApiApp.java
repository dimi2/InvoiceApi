package dsk.invoiceapi;

import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * The main application.
 */
@SpringBootApplication
public class InvoiceApiApp {

	/**
	 * Application entry point.
	 * @param args Command line arguments (not used).
	 */
	public static void main(String[] args) {
		SpringApplication application = new SpringApplication(dsk.invoiceapi.InvoiceApiApp.class);
		application.setBannerMode(Banner.Mode.OFF);
		application.run(args);
	}

}
