package pgu.test.portal.client;

import java.util.LinkedHashMap;
import java.util.Map.Entry;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.RootPanel;

public class Pgu_test_portal implements EntryPoint {

    private final GreetingServiceAsync greetingService = GWT.create(GreetingService.class);

    public static final LinkedHashMap<String, String> id2url = new LinkedHashMap<String, String>();

    private static final String employees_id = "employees";
    private static final String careers_id = "careers";

    static {
        id2url.put(careers_id, "http://localhost:8080/careers/Pgu_test_widget_careers.html");
        id2url.put(employees_id, "http://localhost:8080/employees/Pgu_test_widget_employees.html");
    }

    private native void log(String msg) /*-{
        $wnd.console.log("portal: " + msg);
    }-*/;


    private String current_frame_id = "";

    @Override
    public void onModuleLoad() {

        final PortalLayoutImpl portalLayout = new PortalLayoutImpl(this);
        RootPanel.get().add(portalLayout);

        for (final Entry<String, String> e : id2url.entrySet()) {
            final String id = e.getKey();
            final String url = e.getValue();

            final Frame frame = new Frame(url);
            frame.getElement().setId(id);
            frame.setWidth("100%");
            frame.setHeight("800px");

            portalLayout.addFrame(frame);
        }

        portalLayout.displayFrame(employees_id);

        History.addValueChangeHandler(new ValueChangeHandler<String>() {

            @Override
            public void onValueChange(final ValueChangeEvent<String> event) {
                final String token = event.getValue();
                log("history: " + token);

                if (token.startsWith(employees_id)) {

                    if (!employees_id.equals(current_frame_id)) {
                        portalLayout.displayFrame(employees_id);
                    }
                    sendTokenToFrame(employees_id, token);

                } else if (token.startsWith(careers_id) //
                        || "".equals(token) //
                        || "#".equals(token) //
                        ) {

                    if (careers_id.equals(current_frame_id)) {
                        portalLayout.displayFrame(careers_id);
                    }
                    sendTokenToFrame(careers_id, token);

                } else {
                    throw new UnsupportedOperationException("Unknown token " + token);
                }

            }

        });

        listenToMessage(functionToApplyOnFrameResponse(portalLayout));

    }

    public native void sendTokenToFrame(final String frame_id, String token) /*-{

        var clean_token = token.substring(frame_id.length);
        if (clean_token.length > 0) {
            clean_token = clean_token.substring(1);
        }

        $wnd.console.log('>>> clean token ' + clean_token);

        var notification = {};
        notification.type = 'history';
        notification.token = clean_token;

        var msg_back = JSON.stringify(notification);

        $wnd.console.log('notification for ' + frame_id);
        $wnd.console.log(notification);

        var f = $doc.getElementById(frame_id);

        f.contentWindow.postMessage(msg_back, 'http://localhost:8080');
        f.contentWindow.postMessage(msg_back, 'http://127.0.0.1:8888');

    }-*/;

    public String getPortalToken(final String frame_id, final String token) {
        if (null == token || "".equals(token.trim())) {
            return frame_id;
        }
        return frame_id + "#" + token;
    };

    public void newTokenHistory(final String frame_id, final String token) {
        if (frame_id.equals(current_frame_id)) {
            History.newItem(getPortalToken(frame_id, token));
        }
    }

    private native void listenToMessage(JavaScriptObject fn_to_apply) /*-{
        $wnd.addEventListener('message', fn_to_apply, false);
    }-*/;

    private native JavaScriptObject functionToApplyOnFrameResponse(PortalLayoutImpl view) /*-{

		return function receiver(e) {

			$wnd.console.log('portal receiver');
			$wnd.console.log(e);

			if (e.origin === 'http://localhost:8080' //
			        || e.origin === 'http://127.0.0.1:8888' ) {
				var
				    msg = JSON.parse(e.data)
				  , type = msg.type
				;

				if ([ 'employees', 'careers' ].indexOf(msg.id) > -1) {

				    if (type === 'response') {
//    					view.@pgu.test.portal.client.PortalLayoutImpl::updateEntry(Ljava/lang/String;)(msg.count);

				    } else if (type === 'notif') {
//    					view.@pgu.test.portal.client.PortalLayoutImpl::updateEntry(Ljava/lang/String;)(msg.count);

				    } else if (type === 'history') {
    					view.@pgu.test.portal.client.PortalLayoutImpl::updateHistory(Ljava/lang/String;Ljava/lang/String;)(msg.id, msg.token);

				    } else if (type === 'title') {
    					view.@pgu.test.portal.client.PortalLayoutImpl::updateEntry(Ljava/lang/String;Ljava/lang/String;)(msg.id, msg.title);


				    } else {
    					$wnd.console.log('Unsupported type ' + type);
				    }

				} else {
					$wnd.console.log('Unsupported widget with id ' + msg.id);

				}
			}

		}
    }-*/;

    public void updateCurrentFrameId(final String frame_id) {
        current_frame_id = frame_id;
    }

    public String getCurrentFrameId() {
        return current_frame_id;
    }

}
