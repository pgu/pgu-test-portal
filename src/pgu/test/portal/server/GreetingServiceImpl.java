package pgu.test.portal.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.LinkedHashMap;

import pgu.test.portal.client.GreetingService;
import pgu.test.portal.shared.FieldVerifier;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class GreetingServiceImpl extends RemoteServiceServlet implements GreetingService {

    @Override
    public String greetServer(String input) throws IllegalArgumentException {
        // Verify that the input is valid.
        if (!FieldVerifier.isValidName(input)) {
            // If the input is not valid, throw an IllegalArgumentException back to
            // the client.
            throw new IllegalArgumentException("Name must be at least 4 characters long");
        }

        final String serverInfo = getServletContext().getServerInfo();
        String userAgent = getThreadLocalRequest().getHeader("User-Agent");

        // Escape data from the client to avoid cross-site script vulnerabilities.
        input = escapeHtml(input);
        userAgent = escapeHtml(userAgent);

        return "Hello, " + input + "!<br><br>I am running " + serverInfo + ".<br><br>It looks like you are using:<br>"
        + userAgent;
    }

    /**
     * Escape an html string. Escaping data received from the client helps to prevent cross-site script vulnerabilities.
     * 
     * @param html
     *            the html string to escape
     * @return the escaped string
     */
    private String escapeHtml(final String html) {
        if (html == null) {
            return null;
        }
        return html.replaceAll("&", "&amp;").replaceAll("<", "&lt;").replaceAll(">", "&gt;");
    }

    private static final String FILE_PROTOCOL = "file://";
    private static final String DIR_VAR       = "PORTAL_DIR";
    private static final String FILE_NAME     = "widgets.properties";

    @Override
    public LinkedHashMap<String, String> getWidgets() {
        final String filePath = System.getenv(DIR_VAR);

        InputStream inputStream;

        if (!isBlank(filePath)) {
            inputStream = newFileInputStream(filePath, FILE_NAME);

        } else { // fallback with the webapp's classpath

            inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(FILE_NAME);
        }

        final BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));

        final LinkedHashMap<String, String> id2url = new LinkedHashMap<String, String>();

        String line;

        try {
            while ((line = br.readLine()) != null)   {

                if (line.trim().isEmpty()) {
                    continue;
                }

                final String[] parts = line.split(",");
                id2url.put(parts[0], parts[1]);
            }

        } catch (final IOException e) {
            throw new RuntimeException(e);
        }

        closeBufferedReader(br);

        return id2url;
    }

    private boolean isBlank(final String filePath) {
        return filePath == null || filePath.trim().isEmpty();
    }

    private InputStream newFileInputStream(final String filePath, final String fileName) {

        final String fullPathToProperties = fullPathToProperties(filePath, fileName);

        if (filePath.startsWith(FILE_PROTOCOL)) {
            return newURLStream(fullPathToProperties);
        }

        return newFileStream(fullPathToProperties);
    }

    private String fullPathToProperties(final String filePath, final String fileName) {

        if (filePath.endsWith(File.separator)) {
            return filePath + fileName;
        }

        return filePath + File.separator + fileName;
    }

    private InputStream newFileStream(final String fullPathToProperties) {
        try {
            return new FileInputStream(fullPathToProperties);

        } catch (final FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private InputStream newURLStream(final String fullPathToProperties) {
        try {
            return new URL(fullPathToProperties).openStream();

        } catch (final MalformedURLException e) {
            throw new RuntimeException(e);

        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getWidgetMenu(final String widgetUrl) {

        final String urlMenu = getUrlMenu(widgetUrl);

        final URL url = getURL(urlMenu);
        final URLConnection connection = getUrlConnection(url);

        final BufferedReader in = getBufferedReader(connection);

        final StringBuilder sb = new StringBuilder();

        readInput(in, sb);

        closeBufferedReader(in);

        return sb.toString();
    }

    private void readInput(final BufferedReader in, final StringBuilder sb) {
        String inputLine;
        try {
            while ((inputLine = in.readLine()) != null) {
                sb.append(inputLine);
            }

        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void closeBufferedReader(final BufferedReader in) {
        try {
            in.close();
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    private BufferedReader getBufferedReader(final URLConnection connection) {
        try {
            return new BufferedReader(new InputStreamReader(
                    connection.getInputStream()));
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    private URLConnection getUrlConnection(final URL url) {
        try {
            return url.openConnection();
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    private URL getURL(final String urlMenu) {
        try {
            return new URL(urlMenu);

        } catch (final MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    private String getUrlMenu(final String widgetUrl) {

        if (widgetUrl.contains(".html")) {
            return widgetUrl.substring(0, widgetUrl.lastIndexOf("/") + 1) + "menu";

        } else if (widgetUrl.endsWith("/")) {
            return widgetUrl + "menu";

        } else {
            return widgetUrl + "/menu";
        }
    }


}
