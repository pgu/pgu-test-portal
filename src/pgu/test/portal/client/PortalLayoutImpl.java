package pgu.test.portal.client;

import com.github.gwtbootstrap.client.ui.NavLink;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class PortalLayoutImpl extends Composite {

    private static PortalLayoutImplUiBinder uiBinder = GWT.create(PortalLayoutImplUiBinder.class);

    interface PortalLayoutImplUiBinder extends UiBinder<Widget, PortalLayoutImpl> {
    }

    @UiField
    NavLink goToEmployees, goToCareers;
    @UiField
    com.google.gwt.user.client.ui.Frame employeesFrame, careersFrame;

    private com.google.gwt.user.client.ui.Frame currentFrame;

    public PortalLayoutImpl() {
        initWidget(uiBinder.createAndBindUi(this));

        employeesFrame.setVisible(true);
        careersFrame.setVisible(false);
        currentFrame = employeesFrame;

        goToEmployees.setActive(true);

        employeesFrame.getElement().setId("frame_employees");
        careersFrame.getElement().setId("frame_careers");

        employeesFrame.setUrl("http://localhost:8080/employees/Pgu_test_widget_employees.html");
        careersFrame.setUrl("http://localhost:8080/careers/Pgu_test_widget_careers.html");

        //        Scheduler.get().scheduleFixedPeriod(new RepeatingCommand() {
        //
        //            @Override
        //            public boolean execute() {
        //                return updateMenu();
        //            }
        //
        //        }, 300);

    }

    public void updateEntry(final String count) {

        NavLink currentLink = null;
        String text = "";

        if (employeesFrame.equals(currentFrame)) {
            currentLink = goToEmployees;
            text = "Employees";

        } else if (careersFrame.equals(currentFrame)) {
            currentLink = goToCareers;
            text = "Careers";

        } else {
            throw new IllegalArgumentException("current frame: " + currentFrame);
        }

        if (null == count || "".equals(count.trim())) {
            currentLink.setText(text);
        } else {

            currentLink.setText(text + " (" + count + ")");
        }

    }

    private boolean updateMenu() {
        updateMenuFromCurrentFrame(currentFrame.getElement().getId(), currentFrame.getUrl());
        return true;
    }

    private native void updateMenuFromCurrentFrame(String current_frame_id, String current_frame_url) /*-{

        $wnd.console.log(current_frame_id);
        $wnd.console.log(current_frame_url);

        var frame = $doc.getElementById(current_frame_id);
        frame.contentWindow.postMessage('{"action":"update_menu"}', 'http://localhost:8080');
    }-*/;

    @UiHandler("goToEmployees")
    public void clickOnEmployees(final ClickEvent e) {
        employeesFrame.setVisible(true);
        careersFrame.setVisible(false);
        currentFrame = employeesFrame;
    }

    @UiHandler("goToCareers")
    public void clickOnCarreers(final ClickEvent e) {
        employeesFrame.setVisible(false);
        careersFrame.setVisible(true);
        currentFrame = careersFrame;
    }

}
