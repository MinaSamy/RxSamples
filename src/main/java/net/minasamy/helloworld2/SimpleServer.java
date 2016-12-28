package net.minasamy.helloworld2;

import sun.nio.ch.IOUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Mina.Samy on 11/29/2016.
 */
public class SimpleServer {

    private static final String RESPONSE_MESSAGE="Hello There";
    public static final byte[] RESPONSE = (
            "HTTP/1.1 200 OK\r\n" +
                    "Content-length: "+RESPONSE_MESSAGE.length()+"\r\n" +
                    "\r\n" +
                    RESPONSE_MESSAGE).getBytes();

    public static void startServer() throws IOException {
        final ServerSocket serverSocket=new ServerSocket(8000,100);
        while(!Thread.currentThread().isInterrupted()){
            final Socket socket=serverSocket.accept();
            handle(socket);
        }
    }

    public static void handle(Socket client){
        while(!Thread.currentThread().isInterrupted()){
            try {
                readFullRequest(client);
                client.getOutputStream().write(RESPONSE);
            } catch (IOException e) {
                System.out.println("IOException: Handle "+e.toString());
            }
        }
    }

    private static void readFullRequest(Socket client) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
        String line=reader.readLine();
        while(line!=null&&!line.isEmpty()){
            line=reader.readLine();
            System.out.println(line);
        }
    }
}
