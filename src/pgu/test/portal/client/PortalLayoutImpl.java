package pgu.test.portal.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;

import com.github.gwtbootstrap.client.ui.AlertBlock;
import com.github.gwtbootstrap.client.ui.Hero;
import com.github.gwtbootstrap.client.ui.NavLink;
import com.github.gwtbootstrap.client.ui.base.IconAnchor;
import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

public class PortalLayoutImpl extends Composite {

    private static PortalLayoutImplUiBinder uiBinder = GWT.create(PortalLayoutImplUiBinder.class);

    interface PortalLayoutImplUiBinder extends UiBinder<Widget, PortalLayoutImpl> {
    }

    @UiField
    FlowPanel                      menu;
    @UiField
    NavLink homeLink;
    @UiField
    Frame                         frame;
    @UiField
    Hero                          firstPage;
    @UiField
    HTMLPanel alert, leftPanel, rightPanel;

    private final Pgu_test_portal portal;

    public PortalLayoutImpl(final Pgu_test_portal portal) {
        initWidget(uiBinder.createAndBindUi(this));

        this.portal = portal;
        frame.getElement().setId("portal_frame");

        //        showHome();

        frame.addLoadHandler(new LoadHandler() {

            @Override
            public void onLoad(final LoadEvent event) {

                if (frame.getUrl() == null //
                        || "".equals(frame.getUrl().trim())) {
                    return;
                }

                final Date d = new Date();
                GWT.log("frame: onload: " + d.getTime());

                //                sendPlaceAndShowFrame();
                showFrame();
            }
        });

    }

    @UiHandler("homeLink")
    public void clickHome(final ClickEvent e) {
        portal.newTokenHistory("", "");
    }

    public void showHome() {
        firstPage.setVisible(true);
        frame.setVisible(false);
    }

    public void showFrame() {
        frame.setVisible(true);
        firstPage.setVisible(false);
    }

    public void updateEntry(final String widgetId, final String code, final String title) {

        final ArrayList<MenuNavLink> links = widgetId2links.get(widgetId);
        for (final MenuNavLink link : links) {
            if (link.getCode().equals(code)) {
                link.setText(title);
                break;
            }
        }
    }

    private final LinkedHashMap<String, ArrayList<MenuNavLink>> widgetId2links     = new LinkedHashMap<String, ArrayList<MenuNavLink>>();
    //    private final LinkedHashMap<String, NavLink> frame_id2link     = new LinkedHashMap<String, NavLink>();
    //    private final HashMap<String, Frame>         frame_id2frame    = new HashMap<String, Frame>();

    //    public void addFrame(final Frame frame) {
    //        final String frame_id = frame.getElement().getId();
    //
    //        final NavLink link = new NavLink();
    //        link.addClickHandler(clickFrameHandler);
    //
    //        frame_id2link.put(frame_id, link);
    //        frame_id2frame.put(frame_id, frame);
    //
    //        menu.add(link);
    //    }
    //
    public void addMenuEntry(final String widgetId, final String widgetUrl, final String code, final String title, final String place) {

        final MenuNavLink link = new MenuNavLink();
        link.setWidgetId(widgetId);
        link.setWidgetUrl(widgetUrl);
        link.setPlace(place);
        link.setCode(code);
        link.setText(title);
        link.addClickHandler(clickFrameHandler);
        menu.add(link);

        if (widgetId2links.containsKey(widgetId)) {
            widgetId2links.get(widgetId).add(link);

        } else {

            final ArrayList<MenuNavLink> links = new ArrayList<MenuNavLink>();
            links.add(link);
            widgetId2links.put(widgetId, links);
        }

    }

    ClickHandler clickFrameHandler = new ClickHandler() {

        @Override
        public void onClick(final ClickEvent event) {


            final MenuNavLink link = (MenuNavLink) ((IconAnchor) event.getSource()).getParent();
            final String widgetId = link.getWidgetId();
            final String place = link.getPlace();

            portal.newTokenHistory(widgetId, place);
        }
    };

    private String place = "";
    private String visibleWidgetId = null;

    public String getPlace() {
        return place;
    }

    public void loadFrame(final String widgetId, final String widgetUrl, final String place) {
        this.place = place;

        if (!widgetId.equals(visibleWidgetId)) {
            String url = widgetUrl;

            if (null != place && !"".equals(place.trim())) {
                url += "#" + place;
            }
            frame.setUrl(url);
            visibleWidgetId = widgetId;

        } else {
            sendPlaceAndShowFrame();
        }

    }

    private void sendPlaceAndShowFrame() {
        portal.sendPlaceToFrame(getPlace());
        if (!frame.isVisible()) {
            showFrame();
        }
    }

    public void updateHistory(final String frame_id, final String token) {
        portal.newTokenHistory(frame_id, token);
    }

    private AlertBlock block = null;

    public void updateMenuBar(final boolean childIsFull) {
        if (childIsFull) {
            leftPanel.setVisible(false);
            rightPanel.setStyleName("span12");
        } else {
            rightPanel.setStyleName("span10");
            leftPanel.setVisible(true);
        }
    }

    public void showNotification(final String widgetId, final String typeAlert, final String body) {

        if (block != null) {
            block.close();
            Scheduler.get().scheduleDeferred(new ScheduledCommand() {

                @Override
                public void execute() {
                    addBlock(typeAlert, body);
                }
            });
            return;
        }

        addBlock(typeAlert, body);
    }

    private void addBlock(final String typeAlert, final String body) {
        block = new AlertBlock();
        block.setClose(true);
        block.setAnimation(true);
        block.setHeading("Hey!");
        block.setText(body);

        if ("error".equals(typeAlert)) {
            block.setType(AlertType.ERROR);

        } else if ("success".equals(typeAlert)) {
            block.setType(AlertType.SUCCESS);

        }

        alert.add(block);
    }

}
