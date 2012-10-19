package pgu.test.portal.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import com.github.gwtbootstrap.client.ui.Hero;
import com.github.gwtbootstrap.client.ui.NavLink;
import com.github.gwtbootstrap.client.ui.base.IconAnchor;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.Widget;

public class PortalLayoutImpl extends Composite {

    private static PortalLayoutImplUiBinder uiBinder = GWT.create(PortalLayoutImplUiBinder.class);

    interface PortalLayoutImplUiBinder extends UiBinder<Widget, PortalLayoutImpl> {
    }

    @UiField
    FlowPanel                      menu;
    @UiField
    Frame                         frame;
    @UiField
    Hero                          firstPage;

    private final Pgu_test_portal portal;

    public PortalLayoutImpl(final Pgu_test_portal portal) {
        initWidget(uiBinder.createAndBindUi(this));

        this.portal = portal;
        frame.getElement().setId("portal_frame");
        frame.addLoadHandler(new LoadHandler() {

            @Override
            public void onLoad(final LoadEvent event) {
                //                portal.sendPlaceToFrame(getPlace());

                showFrame();
            }
        });

        showHome();
    }

    public void showHome() {
        firstPage.setVisible(true);
        frame.setVisible(false);
    }

    public void showFrame() {
        frame.setVisible(true);
        firstPage.setVisible(false);
    }

    public void updateHistory(final String frame_id, final String token) {
        portal.newTokenHistory(frame_id, token);
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
    private final LinkedHashMap<String, NavLink> frame_id2link     = new LinkedHashMap<String, NavLink>();
    private final HashMap<String, Frame>         frame_id2frame    = new HashMap<String, Frame>();

    public void addFrame(final Frame frame) {
        final String frame_id = frame.getElement().getId();

        final NavLink link = new NavLink();
        link.addClickHandler(clickFrameHandler);

        frame_id2link.put(frame_id, link);
        frame_id2frame.put(frame_id, frame);

        menu.add(link);
    }

    public void addMenuEntry(final String widgetId, final String widgetUrl, final String code, final String title, final String place) {

        final MenuNavLink link = new MenuNavLink();
        link.setWidgetId(widgetId);
        link.setWidgetUrl(widgetUrl);
        link.setPlace(place);
        link.setCode(code);
        link.setText(title);

        if (widgetId2links.containsKey(widgetId)) {
            widgetId2links.get(widgetId).add(link);

        } else {

            final ArrayList<MenuNavLink> links = new ArrayList<MenuNavLink>();
            links.add(link);
            widgetId2links.put(widgetId, links);
        }

        menu.add(link);

        link.addClickHandler(clickFrameHandler);
    }

    ClickHandler clickFrameHandler = new ClickHandler() {

        @Override
        public void onClick(final ClickEvent event) {


            final MenuNavLink link = (MenuNavLink) ((IconAnchor) event.getSource()).getParent();
            final String widgetId = link.getWidgetId();
            final String place = link.getPlace();

            portal.newTokenHistory(widgetId, place);

            //            if (!frame_id.equals(portal
            //                    .getCurrentFrameId())) {
            //                //                displayFrame(frame_id);
            //            }

        }
    };

    private String place = "";

    public String getPlace() {
        return place;
    }

    public void loadFrame(final String widgetUrl, final String place) {
        this.place = place;
        frame.setUrl(widgetUrl + "#" + place);
    }


}
