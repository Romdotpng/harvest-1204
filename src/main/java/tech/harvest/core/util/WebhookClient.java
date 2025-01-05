package tech.harvest.core.util;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class WebhookClient {
    private final String webhook;

    public WebhookClient(String webhook) {
        this.webhook = webhook;
    }

    public void send(String content) {
        new Thread(() -> {
            try {
                URL url = new URL(this.webhook);
                HttpURLConnection httpConn = (HttpURLConnection)url.openConnection();
                httpConn.setDoOutput(true);
                httpConn.setRequestMethod("POST");
                httpConn.setRequestProperty("content-type", "application/json");
                httpConn.setRequestProperty("user-agent", "Mozilla/5.0 (Windows NT 10.0; WOW64)");
                OutputStreamWriter writer = new OutputStreamWriter(httpConn.getOutputStream());
                writer.write("{\"content\": \"" + content + "\"}");
                writer.flush();
                writer.close();
                httpConn.getOutputStream().close();
                InputStream responseStream = httpConn.getResponseCode() / 100 == 2 ? httpConn.getInputStream() : httpConn.getErrorStream();
                Scanner s = new Scanner(responseStream).useDelimiter("\\A");
                String response = s.hasNext() ? s.next() : "";
                System.out.println(response);
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }).start();
    }

    public void send(String content, String filename, byte[] fileContent) {
        try {
            URL url = new URL(this.webhook);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("content-type", "multipart/form-data; boundary=----WebKitFormBoundary");
            connection.setRequestProperty("user-agent", "");
            connection.setDoOutput(true);
            DataOutputStream writer = new DataOutputStream(connection.getOutputStream());
            String start = "------WebKitFormBoundary\r\nContent-Disposition: form-data; name=\"files[0]\"; filename=\"" + filename + "\"\r\nContent-Type: application/octet-stream\r\n\r\n";
            String end = "\r\n------WebKitFormBoundary\r\nContent-Disposition: form-data; name=\"payload_json\"\r\n\r\n{\"content\":\"" + content + "\"}\r\n------WebKitFormBoundary--\r\n";
            this.write(writer, start.getBytes());
            this.write(writer, fileContent);
            this.write(writer, end.getBytes());
            writer.flush();
            writer.close();
            connection.getOutputStream().close();
            InputStream responseStream = connection.getResponseCode() / 100 == 2 ? connection.getInputStream() : connection.getErrorStream();
            Scanner s = new Scanner(responseStream).useDelimiter("\\A");
            String string = s.hasNext() ? s.next() : "";
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void write(DataOutputStream writer, byte[] bytes) throws IOException {
        writer.write(bytes);
    }
}
