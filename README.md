# TechCase
# PIN Terminal Activation App

## 🚀 Quick Start

### Prerequisites:

1. **Java**: Ensure you have Java installed on your system.

2. **Required Libraries**:
   - **WireMock**: Download the standalone JAR from their [official website](http://wiremock.org/docs/running-standalone/) or repository.
   - **OkHttp**: Needed to make HTTP requests.
   - **SLF4J**: Used for logging.

### Steps to Run:

1. Navigate to the project directory in your terminal.
2. Ensure the required libraries are in your classpath.
3. Compile the application:
   ```bash
   javac -cp ".:path_to_wiremock_jar:path_to_other_dependencies/*" App.java PINActivator.java

🌟 **Input Sets**

For the current mock setup, the following input sets are valid:

- `customerId: "12345", macAddress: "AA:BB:CC:DD:EE:FF"`
- `customerId: "12345", macAddress: "AA:BB:CC:DD:EE:AA"`
- `customerId: "11111", macAddress: "AA:BB:CC:DD:EE:FF"`

Other combinations will not match any predefined stub and might return unexpected results.

🛠 **Architecture & Logic Behind**

The app consists of two main components:

1. **App**: The main application runner. It initializes a mock server (WireMock) to simulate the PIN activation service. It sets up predefined stubs (mocked endpoints) for specific requests and their respective responses.

2. **PINActivator**: This class handles the activation process. Given a `customerId` and `macAddress`, it makes a POST request to the mock server to activate the PIN. The status of the activation (whether it's successful, failed, etc.) is then communicated to an orchestrator via another POST request.

### Flow:
1. The user is prompted to enter a `customerId` and `macAddress`.
2. These details are passed to the `PINActivator`.
3. The `PINActivator` sends a POST request to the mock server.
4. Based on the stub setup, the mock server responds with a status code.
5. The `PINActivator` reads the response and determines the activation status.
6. This status is then communicated to an orchestrator service via another POST request.

