package com.brightspot.model.promo;

import com.brightspot.model.AbstractViewModel;
import com.brightspot.model.event.Event;
import com.brightspot.model.event.promo.EventPromotableViewModel;
import com.brightspot.model.person.Person;
import com.brightspot.model.person.promo.PersonPromotableViewModel;
import com.psddev.cms.view.DelegateView;

public class PromotableDelegateViewModel extends AbstractViewModel<Promotable> implements DelegateView<AbstractPromotableViewModel> {

    @Override
    public AbstractPromotableViewModel getDelegate() {
        if (model instanceof Person) {
            return createView(PersonPromotableViewModel.class, model);
        }
        if (model instanceof Event) {
            return createView(EventPromotableViewModel.class, model);
        }
        return createView(PromotableViewModel.class, model);
    }
}
