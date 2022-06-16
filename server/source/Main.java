public class Main {
	public static void main(String[] args) {
		String serverModeArg = "M";
		if (args != null && args.length > 0) {
			serverModeArg = args[0];
		}
		var serverMode = switch(serverModeArg) {
			case "P" -> ServerMode.PLATFORM_MULTITHREAD;
			case "V" -> ServerMode.VIRTUAL_MULTITHREAD;
			default -> ServerMode.MONOTHREAD;
		};

		var server = new Server(serverMode);
		try {
			server.start();
		} catch (RuntimeException ex) {
			ex.printStackTrace();
		} finally {
			server.shutdown();
		}
	}
}
