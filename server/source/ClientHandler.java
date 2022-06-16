import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.time.LocalDateTime;

class ClientHandler implements Runnable {
	private final Socket socket;
	private BufferedReader reader;
	private BufferedWriter writer;

	public ClientHandler(Socket socket) {
		this.socket = socket;
	}

	public void run() {
		this.initClient();
		String input;
		try {
			listening: while (this.isConnected() && (input = reader.readLine()) != null) {
				if ("exit".equals(input)) {
					break;
				} else {
					handleClientCommand(input);
				}
			}

			if (this.isConnected()) {
				this.writer.write("[Server] Bye bye\r\n");
			}
		} catch (IOException ex) {
			System.err.println("> Error while reading client input: " + ex.getMessage());
		} finally {
			this.close();
		}
	}

	private void handleClientCommand(String input) throws IOException {
		if (input.startsWith("fib")) {
			String[] cmdParts = input.split(" ");
			if (cmdParts.length != 2) {
				this.sentToClient("`fib` command must be processed with a number");
				return;
			}
			try {
				var n = Integer.parseInt(cmdParts[1]);
				System.out.println("> Calculating Fibonacci for " + n);
				var fibResult = this.fib(n);
				this.sentToClient("Fibonacci of %d is %d".formatted(n, fibResult));
			} catch (NumberFormatException ex) {
				this.sentToClient("Invalid number to `fib` command");
				return;
			}

		} else if ("time".equals(input)) {
			String message = "Current time is %s".formatted(LocalDateTime.now().toString());
			this.sentToClient(message);

		} else {
			System.out.println("[Client] " + input);
		}
	}

	private void sentToClient(String message) throws IOException {
		this.writer.write("[Server] %s\r\n".formatted(message));
		this.writer.flush();
	}

	private void initClient() {
		if (!this.isConnected()) {
			System.out.println("> Client isn't connected anymore");
			return;
		}

		try {
			this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			this.writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			this.writer.write("[Server] Hello from VirtualThread Server\r\n");
			this.writer.flush();
		} catch (IOException ex) {
			System.err.println("> Error while initializing client: " + ex.getMessage());
		}
	}

	private boolean isConnected() {
		return !this.socket.isClosed();
	}

	public void close() {
		System.out.println("> Closing client connection");
		try {
			this.writer.close();
		} catch (IOException ex) {
		}
		try {
			this.reader.close();
		} catch (IOException ex) {
		}
		try {
			this.socket.close();
		} catch (IOException ex) {
		}
	}

	private long fib(int n) {
		if (n <= 2)
			return 1;
		return fib(n - 1) + fib(n - 2);
	}
}
