package pgu.test.portal.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

public abstract class AsyncCallbackApp<T> implements AsyncCallback<T> {

    @Override
    public void onFailure(final Throwable caught) {
        throw new RuntimeException(caught);
    }

}
