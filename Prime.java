import java.net.*;
import java.io.*;
import java.util.concurrent.*;

public class Prime {

	private static final int PORT = 7001;
	private static final int DEFAULT_WORKER_THREADS = 1;
	private static final int MAX_INPUT_QUEUE_SIZE = 1024;
	private static final int MAX_OUTPUT_QUEUE_SIZE = 128;
	private BlockingQueue<Integer> inputQueue;
	private BlockingQueue<Result> outputQueue;

	Prime() {
		inputQueue = new LinkedBlockingQueue<>(MAX_INPUT_QUEUE_SIZE);
		outputQueue = new LinkedBlockingQueue<>(MAX_OUTPUT_QUEUE_SIZE);
	}

	private void run(int nbrWorkerThreads) {
		System.out.printf("Prime starts with %s worker thread(s).\n", nbrWorkerThreads);

		for (int i=0; i<nbrWorkerThreads; i++)
			new Thread(() -> { processNumbers(); }).start();

		try {
			Socket socket = new Socket("localhost", PORT);
			DataInputStream in = new DataInputStream(socket.getInputStream());
			DataOutputStream out = new DataOutputStream(socket.getOutputStream());

			new Thread(() -> { sendResults(out); }).start();

			while (true) {
				int nbr = in.readInt();
				inputQueue.put(nbr);
			}
		} catch (Exception e) {
			System.err.println(e);
		}
	}

	private void processNumbers() {
		try {
			while (true) {
				int nbr = inputQueue.take();
				boolean isPrime = checkPrime(nbr);
				outputQueue.put(new Result(nbr, isPrime));
			}
		} catch (Exception e) {
			System.err.println(e);
		}
	}

	private void sendResults(DataOutputStream out) {
		try {
			while (true) {
				Result result = outputQueue.take();
				out.writeInt(result.nbr);
				out.writeBoolean(result.isPrime);
				out.flush();
			}
		} catch (Exception e) {
			System.err.println(e);
		}
	}

	private class Result {
		int nbr;
		boolean isPrime;
		Result(int nbr, boolean isPrime) {
			this.nbr = nbr;
			this.isPrime = isPrime;
		}
	}

	private static boolean checkPrime(int n) {
		if (n < 2)
			return false;
		if (n == 2 || n ==3)
			return true;
		if (n % 2 == 0 || n % 3 == 0)
			return false;

		// a prime cannot be divided by 6n-1 and 6n+1 for all positive integer n
		int i = 5;
		int j = 7;
		while (i * i <= n) {
			if (n % i == 0 || n % j == 0)
				return false;
			i += 6;
			j += 6;
		}

		return true;
	}

	public static void main(String args[]) {
		int nbrWorkerThreads = DEFAULT_WORKER_THREADS;
		if (args.length == 1)
			nbrWorkerThreads = Integer.parseInt(args[0]);

		new Prime().run(nbrWorkerThreads);
	}
}
