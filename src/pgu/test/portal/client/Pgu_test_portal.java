package pgu.test.portal.client;

import java.util.LinkedHashMap;
import java.util.Map.Entry;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.RootPanel;

public class Pgu_test_portal implements EntryPoint {

    private final GreetingServiceAsync greetingService = GWT.create(GreetingService.class);

    private native void log(String msg) /*-{
        $wnd.console.log("portal: " + msg);
    }-*/;

    private String current_frame_id = "";

    private final LinkedHashMap<String, String> widgetId2url = new LinkedHashMap<String, String>();

    private PortalLayoutImpl portalLayout;

    @Override
    public void onModuleLoad() {

        portalLayout = new PortalLayoutImpl(this);
        RootPanel.get().add(portalLayout);

        History.addValueChangeHandler(new ValueChangeHandler<String>() {

            @Override
            public void onValueChange(final ValueChangeEvent<String> event) {
                final String token = event.getValue();

                log("history: [" + token + "]");

                if ("".equals(token)) {
                    portalLayout.showHome();
                    return;
                }

                String widgetId = "";
                String place = "";

                if (token.contains("#")) {
                    final String[] parts = token.split("#");
                    widgetId = parts[0];

                    if (parts.length > 1) {
                        place = parts[1];
                    }

                } else {
                    widgetId = token;
                }

                if (widgetId2url.containsKey(widgetId)) {

                    final String widgetUrl = widgetId2url.get(widgetId);

                    portalLayout.loadFrame(widgetId, widgetUrl, place);

                    //                    if (widgetId.equals(current_frame_id)) {
                    //                    }

                } else {
                    portalLayout.showHome();
                }

            }

        });

        listenToMessage(functionToApplyOnFrameResponse(portalLayout));

        fetchWidgetsOnLoad();
    }

    private int counterWidgetToLoad = 0;

    private void fetchWidgetsOnLoad() {
        greetingService.getWidgets(new AsyncCallbackApp<LinkedHashMap<String, String>>() {

            @Override
            public void onSuccess(final LinkedHashMap<String, String> result) {

                widgetId2url.putAll(result);
                counterWidgetToLoad = result.size();

                for (final Entry<String, String> e : result.entrySet()) {
                    final String widgetId = e.getKey();
                    final String widgetUrl = e.getValue();

                    addEntryMenu(widgetId, widgetUrl);
                }

            }

            @Override
            public void onFailure(final Throwable caught) {
                counterWidgetToLoad = 0;
                super.onFailure(caught);
            }


        });
    }

    private void addEntryMenu(final String widgetId, final String widgetUrl) {

        greetingService.getWidgetMenu(widgetUrl, new AsyncCallbackApp<String>() {

            @Override
            public void onSuccess(final String jsonMenu) {
                translateJsonMenuAndSendToView(widgetId, widgetUrl, jsonMenu, portalLayout);

                counterWidgetToLoad--;

                if (counterWidgetToLoad == 0) {
                    History.fireCurrentHistoryState();
                }
            }

            @Override
            public void onFailure(final Throwable caught) {
                counterWidgetToLoad--;
                if (counterWidgetToLoad == 0) {
                    History.fireCurrentHistoryState();
                }

                super.onFailure(caught);
            }

        });
    }

    private native void translateJsonMenuAndSendToView(final String widgetId, String widgetUrl, final String jsonMenu, PortalLayoutImpl view) /*-{

        var
            menu = JSON.parse(jsonMenu)
          , entries = menu.entries || []
        ;

        for (var i = 0, len = entries.length; i < len; i++) {

            var
                entry = entries[i]
              , code = entry.code || ''
              , title = entry.title || ''
              , place = entry.place || ''
            ;

            view.@pgu.test.portal.client.PortalLayoutImpl::addMenuEntry(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)( //
            widgetId, widgetUrl, code, title, place);
        }

    }-*/;

    public native void sendPlaceToFrame(String place) /*-{

        $wnd.console.log('send place [' + place + ']');

        var notification = {};
        notification.type = 'history';
        notification.place = place;

        var msg_back = JSON.stringify(notification);

        var f = $doc.getElementById('portal_frame');

        f.contentWindow.postMessage(msg_back, 'http://localhost:8080');
        f.contentWindow.postMessage(msg_back, 'http://127.0.0.1:8888');

    }-*/;

    public String getPortalToken(final String widgetId, final String place) {
        if (null == place || "".equals(place.trim())) {
            return widgetId;
        }
        return widgetId + "#" + place;
    };

    public void newTokenHistory(final String widgetId, final String place) {
        History.newItem(getPortalToken(widgetId, place));

        //        if (frame_id.equals(current_frame_id)) {
        //        }
    }

    private native void listenToMessage(JavaScriptObject fn_to_apply) /*-{
        $wnd.addEventListener('message', fn_to_apply, false);
    }-*/;

    private native JavaScriptObject functionToApplyOnFrameResponse(PortalLayoutImpl view) /*-{

		return function receiver(e) {

			$wnd.console.log('receiving: portal: ' + e.data);

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
    					view.@pgu.test.portal.client.PortalLayoutImpl::updateEntry(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)(msg.id, msg.code, msg.title);

				    } else if (type === 'notification') {
    					view.@pgu.test.portal.client.PortalLayoutImpl::showNotification(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)(msg.id, msg.alert_type, msg.body);

				    } else if (type === 'chat') {
    					view.@pgu.test.portal.client.PortalLayoutImpl::showChatBtn(Ljava/lang/String;)(msg.msg);

				    } else if (type === 'size') {
    					view.@pgu.test.portal.client.PortalLayoutImpl::updateMenuBar(Z)(msg.is_full);

				    } else {
    					$wnd.console.log('Unsupported type ' + type);
				    }

				} else {
					$wnd.console.log('Unsupported widget with id ' + msg.id);

				}
			} else {
			    $wnd.console.log('Unsupported origin');
			}

		}
    }-*/;

    public void updateCurrentFrameId(final String frame_id) {
        current_frame_id = frame_id;
    }

    public String getCurrentFrameId() {
        return current_frame_id;
    }

    public native void sendChatToFrame(final String text) /*-{

        $wnd.console.log('send chat [' + text + ']');

        var notification = {};
        notification.type = 'chat';
        notification.msg = text;

        var msg_back = JSON.stringify(notification);

        var f = $doc.getElementById('portal_frame');

        f.contentWindow.postMessage(msg_back, 'http://localhost:8080');
        f.contentWindow.postMessage(msg_back, 'http://127.0.0.1:8888');

    }-*/;

}
