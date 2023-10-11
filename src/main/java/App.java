import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;

import java.util.Scanner;
/**
 * Main application class to activate a PIN terminal for a customer.
 * The class initializes and manages the WireMock server for stubbing responses,
 * and it allows users to input a customer id and MAC Address to simulate the activation process.
 */
public class App {

    /** The port number on which the WireMock server is set to run. */
    private static final int PORT = 8080;

    /**
     * The main method to start the application.
     * It initializes the WireMock server, sets up the required stubs,
     * and loops to accept user input for customer id and MAC Address until the user decides to exit.
     *
     * @param args Command-line arguments (not used in this application).
     */
    public static void main(String[] args) {
        // Start WireMock server
        WireMockServer wireMockServer = new WireMockServer(PORT);
        wireMockServer.start();

        // Set up WireMock stubs
        setupStubs();

        // Fictional orchestratorUrl
        String orchestratorUrl = "http://localhost:8090";

        PINActivator activator = new PINActivator("http://localhost:" + PORT, orchestratorUrl);

        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("Enter 'exit' to quit the application.");

            System.out.print("Enter customer id: ");
            String customerId = scanner.nextLine();

            if ("exit".equalsIgnoreCase(customerId)) {
                break;
            }

            // Validate customerId (assuming it's numeric)
            if (!customerId.matches("\\d+")) {
                System.out.println("Invalid customer ID. Please enter a valid numeric ID.");
                continue;
            }

            System.out.print("Enter MAC Address: ");
            String macAddress = scanner.nextLine();

            if ("exit".equalsIgnoreCase(macAddress)) {
                break;
            }

            // Validate MAC Address
            if (!macAddress.matches("^([0-9A-Fa-f]{2}[:-]){5}([0-9A-Fa-f]{2})$")) {
                System.out.println("Invalid MAC Address. Please enter in format AA:BB:CC:DD:EE:FF.");
                continue;
            }

            activator.activatePinTerminal(customerId, macAddress);
        }


        // Close scanner
        scanner.close();

        // Stop the WireMock server
        wireMockServer.stop();
    }

    /**
     * Sets up the WireMock stubs for the different scenarios:
     * 1. PIN terminal successfully activated.
     * 2. PIN terminal not registered in the system.
     * 3. PIN terminal already attached to a different customer.
     */
    private static void setupStubs() {
        WireMock.configureFor("localhost", PORT);

        WireMock.stubFor(WireMock.post(WireMock.urlEqualTo("/activate"))
                .withRequestBody(WireMock.equalToJson("{\"customerId\": \"12345\", \"macAddress\": \"AA:BB:CC:DD:EE:FF\"}"))
                .willReturn(WireMock.aResponse().withStatus(201)));

        WireMock.stubFor(WireMock.post(WireMock.urlEqualTo("/activate"))
                .withRequestBody(WireMock.equalToJson("{\"customerId\": \"12345\", \"macAddress\": \"AA:BB:CC:DD:EE:AA\"}"))
                .willReturn(WireMock.aResponse().withStatus(404)));

        WireMock.stubFor(WireMock.post(WireMock.urlEqualTo("/activate"))
                .withRequestBody(WireMock.equalToJson("{\"customerId\": \"11111\", \"macAddress\": \"AA:BB:CC:DD:EE:FF\"}"))
                .willReturn(WireMock.aResponse().withStatus(409)));
    }

}