import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ConnectException;

/**
 * The PINActivator class is responsible for simulating the activation of a PIN terminal
 * based on a given customer ID and MAC Address. It interacts with a mock southbound system
 * to determine the activation status and logs the corresponding response.
 */
public class PINActivator {

    /** Logger instance for logging events and issues related to PIN terminal activation. */
    private static final Logger logger = LoggerFactory.getLogger(PINActivator.class);

    /** HTTP client for making requests to the southbound system. */
    private final OkHttpClient client;
    /** Base URL of the southbound system. */
    private final String baseUrl;
    /** URL of the orchestrator to which statuses should be reported. */
    private final String orchestratorUrl;

    /**
     * Initializes a new instance of the PINActivator class.
     *
     * @param baseUrl         The base URL of the southbound system.
     * @param orchestratorUrl The URL of the orchestrator.
     */
    public PINActivator(String baseUrl, String orchestratorUrl) {
        this.client = new OkHttpClient();
        this.baseUrl = baseUrl;
        this.orchestratorUrl = orchestratorUrl;
    }

    /**
     * Tries to activate a PIN terminal using the provided customer ID and MAC Address.
     * It sends a request to the southbound system, logs the response, and sends the activation status
     * to the orchestrator.
     *
     * @param customerId The ID of the customer trying to activate the PIN terminal.
     * @param macAddress The MAC Address of the PIN terminal to be activated.
     */
    public void activatePinTerminal(String customerId, String macAddress) {
        // Construct JSON request payload
        String json = String.format("{\"customerId\": \"%s\", \"macAddress\": \"%s\"}", customerId, macAddress);
        RequestBody body = RequestBody.create(json, MediaType.get("application/json"));

        // Create POST request to /activate
        Request request = new Request.Builder()
                .url(baseUrl + "/activate")
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            String status;
            // Handle response
            int statusCode = response.code();
            switch (statusCode) {
                case 201:
                    logger.info("The PIN terminal was successfully activated for customerId: {}", customerId);
                    status = "ACTIVE";
                    break;
                case 404:
                    logger.error("The PIN terminal is not registered in the system for customerId: {}", customerId);
                    status = "INACTIVE";
                    break;
                case 409:
                    logger.error("The PIN terminal is already attached to a different customer for customerId: {}", customerId);
                    status = "INACTIVE";
                    break;
                default:
                    /* Given the current WireMock stubs, any input for customerId and macAddress outside of the
                    predefined sets will default to a 404 response from WireMock, resulting in an '
                    INACTIVE' status. To trigger the 'ERROR' status in the code, a new stub returning a different
                    unexpected status code would be needed. */
                    logger.error("Unexpected status code: {} for customerId: {}", statusCode, customerId);
                    status = "ERROR";
                    break;
            }
            sendStatusToOrchestrator(status, customerId);  // Send status to the orchestrator after handling the response
        } catch (Exception e) {
            logger.error("Unexpected error while activating PIN terminal for customerId: {}", customerId, e);
        }
    }

    /**
     * Sends the activation status to the orchestrator. In the current implementation, the status is only logged,
     * but the method contains a commented-out portion that would actually send the status to the orchestrator
     * if implemented.
     *
     * @param status     The activation status (ACTIVE, INACTIVE, ERROR).
     * @param customerId The ID of the customer associated with the activation attempt.
     */
    private void sendStatusToOrchestrator(String status, String customerId) {
        logger.info("Status for customerId {}: {}", customerId, status);

        /**if we were to send an http request to the orchestrator containing
        the status it would be something like this :) but for this app we are just logging it

        RequestBody body = RequestBody.create(status, MediaType.get("text/plain"));
        Request request = new Request.Builder()
                .url(orchestratorUrl + "/status")
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                logger.error("Failed to send status to orchestrator for customerId: {}. HTTP Status: {}", customerId, response.code());
            }
        } catch (ConnectException e) {
            logger.error("Failed to connect to orchestrator at {}. Service might not be running.", orchestratorUrl);
        } catch (IOException e) {
            logger.error("Error while sending status to orchestrator for customerId: {}", customerId, e);
        }**/
    }

}
