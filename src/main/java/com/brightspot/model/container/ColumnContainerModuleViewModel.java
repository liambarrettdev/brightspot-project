package com.brightspot.model.container;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.brightspot.model.AbstractViewModel;
import com.brightspot.model.module.AbstractModule;
import com.brightspot.view.model.container.ColumnContainerModuleView;

public class ColumnContainerModuleViewModel extends AbstractViewModel<ColumnContainerModule> implements ColumnContainerModuleView {

    @Override
    public Collection<?> getColumns() {
        List<Object> views = new ArrayList<>();
        for (List<AbstractModule> container : model.getContent().getColumns()) {
            views.add(buildConcatenatedView(buildModuleViews(container)));
        }
        return views;
    }

    @Override
    public Object getType() {
        return model.getContent().getClass().getSimpleName();
    }
}
