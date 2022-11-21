package HttpServer.network;

import HttpServer.protocol.HttpRequest;
import HttpServer.protocol.HttpResponse;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerThread implements Runnable {

    private final Socket socket;
    private final BufferedReader reader;
    private final BufferedOutputStream writer;

    private final HttpRequest httpRequest = new HttpRequest();
    private HttpResponse httpResponse;

    private static final Logger threadLogger = Logger.getLogger(ServerThread.class.getName());

    public ServerThread(Socket socket) throws IOException {
        threadLogger.setLevel(Level.INFO);
        this.socket = socket;
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        writer = new BufferedOutputStream(socket.getOutputStream());
    }

    private String readline() throws IOException {
        StringBuilder sb = new StringBuilder();
        int buf = reader.read();
        if(buf == -1) {
            return null;
        }
        while(buf != -1) {
            if((char) buf == '\r') {
                expect_slash_n();
                sb.append("\r\n");
                break;
            }
            sb.append((char) buf);
            buf = reader.read();
        }
        return sb.toString();
    }

    private void runImpl() {
        List<String> lines = new ArrayList<>();
        while(true) {
            try {
                String line = readline();
                if(line == null || line.equals("\r\n")) {
                    httpRequest.parseLine(lines);
                    httpResponse = new HttpResponse(httpRequest, writer);
                    httpResponse.processRequest();
                    httpResponse.send();
                    break;
                }
                if(line.length() > 2 && line.endsWith("\r\n")) {
                    lines.add(line.substring(0, line.length() - 2));
                } else {
                    lines.add(line);
                }
            } catch (IOException e) {
                if(!e.getMessage().equals("Socket closed")) {
                    threadLogger.warning(e.getMessage());
                }
                break;
            }
        }

    }

    public void cleanUp() throws IOException {
        socket.close();
        reader.close();
        writer.flush();
        writer.close();
    }

    @Override
    public void run() {
        try {
            runImpl();
            cleanUp();
        } catch (IOException e) {
            threadLogger.warning(e.getMessage());
        }
    }

    private void expect(char ch) throws IOException {
        int temp = reader.read();
        if(temp < 0 || (char) temp != ch) {
            throw new IOException("bad sequence");
        }
    }

    private void expect_slash_n() throws IOException {
        expect('\n');
    }

}
