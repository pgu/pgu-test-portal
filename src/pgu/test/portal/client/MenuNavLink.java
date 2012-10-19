package pgu.test.portal.client;

import com.github.gwtbootstrap.client.ui.NavLink;

public class MenuNavLink extends NavLink {

    private String widgetId;
    private String widgetUrl;
    private String place;
    private String code;

    public MenuNavLink() {
    }

    public String getWidgetUrl() {
        return widgetUrl;
    }

    public void setWidgetUrl(final String widgetUrl) {
        this.widgetUrl = widgetUrl;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(final String place) {
        this.place = place;
    }

    public String getCode() {
        return code;
    }

    public void setCode(final String code) {
        this.code = code;
    }

    public String getWidgetId() {
        return widgetId;
    }

    public void setWidgetId(final String widgetId) {
        this.widgetId = widgetId;
    }

}
