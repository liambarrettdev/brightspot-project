package com.brightspot.model.event;

import com.brightspot.model.AbstractViewModel;
import com.brightspot.view.model.event.EventPageView;

public class EventPageViewModel extends AbstractViewModel<Event> implements EventPageView {

    @Override
    public Object getName() {
        return model.getDisplayName();
    }

    @Override
    public Object getType() {
        return model.getType().toString();
    }

    @Override
    public Object getLocation() {
        return model.getLocation();
    }

    @Override
    public Object getDate() {
        return buildDateString();
    }

    @Override
    public Object getDescription() {
        return buildRichTextView(model.getDescription());
    }

    // -- Utility Methods -- //

    private String buildDateString() {
        return "tba";
    }
}
