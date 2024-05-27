package com.example.web_server.core.http.utils;

import com.example.web_server.core.http.Http;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.message.BasicLineParser;
import org.apache.hc.core5.util.CharArrayBuffer;
import org.slf4j.*;


import java.util.HashMap;
import java.util.Map;

public class HttpRequestParser {
    private static final Logger logger = LoggerFactory.getLogger(HttpRequestParser.class);

    public Http parse(String request) {
        // Validate the request string
        if (request == null || request.isEmpty()) {
            throw new IllegalArgumentException("Request cannot be null or empty");
        }

        logger.debug("Received request: {}", request);

        try {
            // Split the request into lines
            String[] lines = request.split("\r\n");

            // Parse request line
            String[] requestLineParts = lines[0].split("\\s+");
            String method = requestLineParts[0];
            String path = requestLineParts[1];
            String protocol = requestLineParts[2];

            // Parse headers
            Map<String, String> headers = new HashMap<>();
            int i = 1; // Start after the request line
            while (i < lines.length && !lines[i].isEmpty()) {
                String[] header = lines[i].split(": ", 2);
                if (header.length == 2) {
                    headers.put(header[0], header[1]);
                }
                i++;
            }

            // Extract the body
            StringBuilder body = new StringBuilder();
            for (int j = i + 1; j < lines.length; j++) {
                body.append(lines[j]);
                if (j < lines.length - 1) {
                    body.append("\r\n");
                }
            }

            return new Http(method, body.toString(), path, headers);
        } catch (Exception e) {
            logger.error("Error parsing HTTP request: {}", e.getMessage());
            throw new IllegalArgumentException("Invalid HTTP request format", e);
        }
    }
}
