package HttpServer.network;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server {

    private final int THREAD_TIMEOUT = 30;

    private static final Logger socketLogger = Logger.getLogger(Server.class.getName());

    private ServerSocket ss;

    public Server(int port) {
        this(new byte[]{0, 0, 0, 0}, port);
    }

    public Server(byte[] addr, int port) {
        socketLogger.setLevel(Level.INFO);
        try {
            ss = new ServerSocket(port, 50, InetAddress.getByAddress(addr));
        } catch (IOException e) {
            socketLogger.warning(e.getMessage());
        }
    }

    public void listen() {
        while (true) {
            try {
                Socket socket = ss.accept();
                ServerThread serverThread = new ServerThread(socket);
                ExecutorService executor = Executors.newSingleThreadExecutor();
                Future<?> future = executor.submit(serverThread);
                try {
                    future.get(THREAD_TIMEOUT, TimeUnit.SECONDS);
                } catch (InterruptedException | ExecutionException
                        | TimeoutException e) {
                    future.cancel(true);
                    serverThread.cleanUp();
                }
                executor.shutdown();
            } catch(IOException e){
                socketLogger.warning(e.getMessage());
                close();
                break;
            }
        }
    }

    public void close() {
        try {
            ss.close();
        } catch (IOException e) {
            socketLogger.warning(e.getMessage());
        }
    }

}
