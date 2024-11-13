package com.brightspot.model.promo;

import com.brightspot.model.AbstractViewModel;
import com.brightspot.model.person.Person;
import com.brightspot.model.person.promo.PersonPromoModuleViewModel;
import com.psddev.cms.view.DelegateView;

public class PromoModuleDelegateViewModel extends AbstractViewModel<PromoModule> implements DelegateView<AbstractPromoModuleViewModel> {

    @Override
    public AbstractPromoModuleViewModel getDelegate() {
        if (model.getPromo() instanceof Person) {
            return createView(PersonPromoModuleViewModel.class, model);
        }
        return createView(PromoModuleViewModel.class, model);
    }
}
