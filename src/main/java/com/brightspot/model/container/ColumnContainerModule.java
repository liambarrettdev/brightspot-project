package com.brightspot.model.container;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.brightspot.model.module.AbstractModule;
import com.brightspot.model.module.ShareableModule;
import com.psddev.cms.view.ViewBinding;
import com.psddev.dari.db.Record;
import com.psddev.dari.db.Recordable;

@Recordable.DisplayName("Column Container")
@ViewBinding(value = ColumnContainerModuleViewModel.class, types = ColumnContainerModule.VIEW_CLASS)
public class ColumnContainerModule extends AbstractModule implements ShareableModule {

    protected static final String VIEW_CLASS = "column-container-module";

    @Required
    private ColumnContainer content = new OneColumn();

    public ColumnContainer getContent() {
        return content;
    }

    public void setContent(ColumnContainer content) {
        this.content = content;
    }

    // -- Overrides -- //

    @Override
    public String getViewType() {
        return VIEW_CLASS;
    }

    @Recordable.Embedded
    public abstract static class ColumnContainer extends Record {

        abstract List<List<AbstractModule>> getColumns();
    }

    public static class OneColumn extends ColumnContainer {

        @CollectionMinimum(1)
        private List<AbstractModule> firstColumn;

        public List<AbstractModule> getFirstColumn() {
            if (firstColumn == null) {
                firstColumn = new ArrayList<>();
            }
            return firstColumn;
        }

        public void setFirstColumn(List<AbstractModule> firstColumn) {
            this.firstColumn = firstColumn;
        }

        @Override
        List<List<AbstractModule>> getColumns() {
            return Collections.singletonList(
                getFirstColumn()
            );
        }
    }

    public static class TwoColumn extends OneColumn {

        @CollectionMinimum(1)
        private List<AbstractModule> secondColumn;

        public List<AbstractModule> getSecondColumn() {
            if (secondColumn == null) {
                secondColumn = new ArrayList<>();
            }
            return secondColumn;
        }

        public void setSecondColumn(List<AbstractModule> secondColumn) {
            this.secondColumn = secondColumn;
        }

        @Override
        List<List<AbstractModule>> getColumns() {
            return Arrays.asList(
                getFirstColumn(),
                getSecondColumn()
            );
        }
    }

    public static class ThreeColumn extends TwoColumn {

        @CollectionMinimum(1)
        private List<AbstractModule> thirdColumn;

        public List<AbstractModule> getThirdColumn() {
            if (thirdColumn == null) {
                thirdColumn = new ArrayList<>();
            }
            return thirdColumn;
        }

        public void setThirdColumn(List<AbstractModule> thirdColumn) {
            this.thirdColumn = thirdColumn;
        }

        @Override
        List<List<AbstractModule>> getColumns() {
            return Arrays.asList(
                getFirstColumn(),
                getSecondColumn(),
                getThirdColumn()
            );
        }
    }
}
