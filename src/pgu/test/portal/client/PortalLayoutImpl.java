package pgu.test.portal.client;

import java.util.HashMap;
import java.util.LinkedHashMap;

import com.github.gwtbootstrap.client.ui.NavLink;
import com.github.gwtbootstrap.client.ui.base.IconAnchor;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

public class PortalLayoutImpl extends Composite {

    private static PortalLayoutImplUiBinder uiBinder = GWT.create(PortalLayoutImplUiBinder.class);

    interface PortalLayoutImplUiBinder extends UiBinder<Widget, PortalLayoutImpl> {
    }

    @UiField
    HTMLPanel                     menu;
    @UiField
    Frame                         frame;

    private final Pgu_test_portal portal;

    public PortalLayoutImpl(final Pgu_test_portal portal) {
        initWidget(uiBinder.createAndBindUi(this));

        this.portal = portal;
    }

    public void updateHistory(final String frame_id, final String token) {
        portal.newTokenHistory(frame_id, token);
    }

    public void updateEntry(final String frame_id, final String titleEntry) {
        final NavLink link = frame_id2link.get(frame_id);
        link.setText(titleEntry);
    }

    private final LinkedHashMap<String, NavLink> frame_id2link     = new LinkedHashMap<String, NavLink>();
    private final HashMap<NavLink, String>       link2frame_id     = new HashMap<NavLink, String>();
    private final HashMap<String, Frame>         frame_id2frame    = new HashMap<String, Frame>();

    ClickHandler                                 clickFrameHandler = new ClickHandler() {

        @Override
        public void onClick(final ClickEvent event) {
            final NavLink link = (NavLink) ((IconAnchor) event
                    .getSource()).getParent();
            final String frame_id = link2frame_id
                    .get(link);

            if (!frame_id.equals(portal
                    .getCurrentFrameId())) {
                //                displayFrame(frame_id);
            }

            portal.newTokenHistory(frame_id, "");
        }
    };

    public void addFrame(final Frame frame) {
        final String frame_id = frame.getElement().getId();

        final NavLink link = new NavLink();
        link.addClickHandler(clickFrameHandler);

        frame_id2link.put(frame_id, link);
        link2frame_id.put(link, frame_id);
        frame_id2frame.put(frame_id, frame);

        menu.add(link);
    }

    //    public void displayFrame(final String frame_id) {
    //        portal.updateCurrentFrameId(frame_id);
    //
    //        if (applicationArea.getWidget() == null) {
    //            final Frame frame = frame_id2frame.get(frame_id);
    //            applicationArea.setWidget(frame);
    //            return;
    //        }
    //
    //        final String curr_frame_id = applicationArea.getWidget().getElement().getId();
    //        if (frame_id.equals(curr_frame_id)) {
    //            return;
    //        }
    //
    //        final Frame curr_frame = frame_id2frame.get(curr_frame_id);
    //        hiddenframes.add(curr_frame);
    //
    //        final Frame frame = frame_id2frame.get(frame_id);
    //        applicationArea.setWidget(frame);
    //    }

    public void updateMenu(final String widgetId, final String code, final String title) {
        // TODO Auto-generated method stub

    }

}
