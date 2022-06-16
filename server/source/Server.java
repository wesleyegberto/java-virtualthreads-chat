import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
	private final ServerMode serverMode;

	private final ServerSocket server;
	private final List<ClientHandler> clients = new LinkedList<>();

	private ExecutorService es;

	public Server(ServerMode serverMode) {
		this.serverMode = serverMode;

		this.es = this.createExecutorService();

		try {
			System.out.printf("> Server starting in mode %s at port 8000%n", serverMode);
			this.server = new ServerSocket(8000);
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}

	private boolean isMultithread() {
		return this.serverMode == ServerMode.PLATFORM_MULTITHREAD
				|| this.serverMode == ServerMode.VIRTUAL_MULTITHREAD;
	}

	public void start() {
		while (true) {
			System.out.println("> Waiting for clients...");
			try {
				var clientSocket = this.server.accept();
				this.handleClient(clientSocket);
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	public void shutdown() {
		if (!this.clients.isEmpty()) {
			this.clients.forEach(ClientHandler::close);
		}
		try {
			this.server.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	private void handleClient(Socket clientSocket) throws IOException {
		System.out.println("> Client connected");
		var clientHandler = new ClientHandler(clientSocket);
		this.clients.add(clientHandler);
		if (this.isMultithread()) {
			System.out.println("> Dispatching client");
			es.execute(clientHandler);
		} else {
			clientHandler.run();
		}
	}

	private ExecutorService createExecutorService() {
		return switch(serverMode) {
			case null -> throw new IllegalArgumentException("Invalid server mode");
			case PLATFORM_MULTITHREAD -> Executors.newCachedThreadPool(
					Thread.ofPlatform().name("chat-client-", 0).factory()
			);
			case VIRTUAL_MULTITHREAD -> Executors.newThreadPerTaskExecutor(
					Thread.ofVirtual().name("chat-client-", 0).factory()
			);
			default -> null;
		};
	}
}


enum ServerMode {
	MONOTHREAD,
	PLATFORM_MULTITHREAD,
	VIRTUAL_MULTITHREAD
}
