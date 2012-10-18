package pgu.test.portal.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.TreeMap;

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

        final Properties props = loadProperties(inputStream);

        final TreeMap<Integer, String> order2key = new TreeMap<Integer, String>();
        final HashMap<String, String> fullKey2key = new HashMap<String, String>();

        for (final Entry<Object, Object> e : props.entrySet()) {
            final String propKey = (String) e.getKey();

            if (propKey.contains(".")) {
                final String[] parts = propKey.split("\\.");

                Integer order = 9999;
                try {
                    order = Integer.valueOf(parts[0]);

                }catch (final Exception ex) {
                    // fail silently
                }

                order2key.put(order, propKey);
                fullKey2key.put(propKey, parts[1]);

            } else {
                order2key.put(9999, propKey);
                fullKey2key.put(propKey, propKey);
            }
        }

        final LinkedHashMap<String, String> m = new LinkedHashMap<String, String>();

        for (final Entry<Integer, String> e : order2key.entrySet()) {
            final String propKey = e.getValue();

            final String key = fullKey2key.get(propKey);
            final String url = (String) props.get(propKey);
            m.put(key, url);
        }

        return m;
    }

    private Properties loadProperties(final InputStream inputStream) {
        try {
            final Properties props = new Properties();
            props.load(inputStream);
            return props;

        } catch (final FileNotFoundException e) {
            throw new RuntimeException(e);

        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
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

}
