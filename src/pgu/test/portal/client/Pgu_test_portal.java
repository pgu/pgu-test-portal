package pgu.test.portal.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.ui.RootPanel;

public class Pgu_test_portal implements EntryPoint {

    private final GreetingServiceAsync greetingService = GWT.create(GreetingService.class);

    @Override
    public void onModuleLoad() {
        final PortalLayoutImpl portalLayout = new PortalLayoutImpl();
        RootPanel.get().add(portalLayout);

        listenToMessage(functionToApplyOnFrameResponse(portalLayout));
    }

    private native void listenToMessage(JavaScriptObject fn_to_apply) /*-{
        $wnd.addEventListener('message', fn_to_apply, false);
    }-*/;

    private native JavaScriptObject functionToApplyOnFrameResponse(PortalLayoutImpl view) /*-{

		return function receiver(e) {

			$wnd.console.log('portal receiver');
			$wnd.console.log(e);

			if (e.origin === 'http://localhost:8080') {
				var
				    msg = JSON.parse(e.data)
				  , type = msg.type
				;

				if ([ 'employees', 'careers' ].indexOf(msg.id) > -1) {

				    if (type === 'response') {
    					view.@pgu.test.portal.client.PortalLayoutImpl::updateEntry(Ljava/lang/String;)(msg.count);

				    } else if (type === 'notif') {
    					view.@pgu.test.portal.client.PortalLayoutImpl::updateEntry(Ljava/lang/String;)(msg.count);


				    } else {
    					$wnd.console.log('Unsupported type ' + type);
				    }

				} else {
					$wnd.console.log('Unsupported widget with id ' + msg.id);

				}
			}

		}
    }-*/;

}
