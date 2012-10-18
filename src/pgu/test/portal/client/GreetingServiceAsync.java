package pgu.test.portal.client;

import java.util.LinkedHashMap;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>GreetingService</code>.
 */
public interface GreetingServiceAsync {
    void greetServer(String input, AsyncCallback<String> callback) throws IllegalArgumentException;

    void getWidgets(AsyncCallback<LinkedHashMap<String, String>> asyncCallback);
}
