package com.brightspot.model.event.promo;

import java.util.Optional;

import com.brightspot.model.event.Event;
import com.brightspot.model.promo.AbstractPromoModuleViewModel;
import com.brightspot.view.model.event.promo.EventPromoModuleView;

public class EventPromoModuleViewModel extends AbstractPromoModuleViewModel implements EventPromoModuleView {

    @Override
    public Object getLocation() {
        return Optional.of(model.getPromo())
            .filter(Event.class::isInstance)
            .map(Event.class::cast)
            .map(Event::getLocation)
            .orElse(null);
    }
}
