import java.net.*;
import java.io.*;
import java.util.concurrent.*;

public class Randomizer {

	private static final int PORT = 7001;
	private static final int MAX_RANDOM_NUMBER = 1000000;
	private static final int MAX_QUEUE_SIZE = 1024;
	private BlockingQueue<Integer> numberQueue;

	Randomizer() {
		numberQueue = new LinkedBlockingQueue<>(MAX_QUEUE_SIZE);
	}

	private void run() throws Exception {
		System.out.println("Randomizer starts");

		new Thread(() -> { generateNumbers(); }).start();

		ServerSocket ssock = new ServerSocket(PORT);
		while (true) {
			Socket sock = ssock.accept();
			new Thread(() -> { sendNumbers(sock); }).start();
			new Thread(() -> { readResults(sock); }).start();
		}
	}

	private void generateNumbers() {
		try {
			while (true) {
				numberQueue.put(ThreadLocalRandom.current().nextInt(MAX_RANDOM_NUMBER));
			}
		} catch (Exception e) {
			System.err.println(e);
		}
	}
			
	private void sendNumbers(Socket socket) {
		try {
			DataOutputStream out = new DataOutputStream(socket.getOutputStream());
			while (true) {
				out.writeInt(numberQueue.take());
				out.flush();
			}
		} catch (Exception e) {
			System.err.println(e);
		}
	}

	private void readResults(Socket socket) {
		try {
			DataInputStream in = new DataInputStream(socket.getInputStream());
			while (true) {
				int nbr = in.readInt();
				boolean isPrime = in.readBoolean();
				System.out.printf("%s %s \n", nbr, isPrime);
			}
		} catch (Exception e) {
			System.err.println(e);
		}
	}

	public static void main(String args[]) throws Exception {
		new Randomizer().run();
	}
}
