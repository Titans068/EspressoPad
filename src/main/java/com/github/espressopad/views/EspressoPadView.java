package com.github.espressopad.views;

import com.github.espressopad.controller.EspressoPadController;
import com.github.espressopad.models.ViewModel;

import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class EspressoPadView extends JPanel {
    private final EspressoPadController controller = new EspressoPadController();
    private final JTabbedPane tabPane = new JTabbedPane();
    private final JToolBar toolBar = new JToolBar();
    private final JMenuBar menuBar = new JMenuBar();
    private final List<ViewModel> viewModels = new ArrayList<>();
    private static final AtomicInteger tabCounter = new AtomicInteger(2);

    public EspressoPadView() {
        this.setLayout(new BorderLayout());
        this.add(this.tabPane, BorderLayout.CENTER);
        this.createTab(true);
        this.addTabButton();
        this.controller.setupMiddleMouseListener(this.tabPane);
    }

    public JPanel createTab() {
        ViewModel model = new ViewModel();
        JPanel tab = model.getTab();
        return this.createTab(tab, model);
    }

    public JPanel createTab(boolean addToTabPane) {
        ViewModel model = new ViewModel();
        JPanel tab = model.getTab();
        return this.createTab(addToTabPane, tab, model);
    }

    public JPanel createTab(String text, JPanel tab, ViewModel model) {
        model.getTextEditor().setText(text);
        return this.createTab(tab, model);
    }

    private JPanel createTab(JPanel tab, ViewModel model) {
        return this.createTab(true, tab, model);
    }

    private JPanel createTab(boolean addToTabPane, JPanel tab, ViewModel model) {
        String tl;
        int tabCount = this.tabPane.getTabCount() - 1;
        if (tabCount > 1)
            tl = String.format("Tab%d", tabCounter.getAndIncrement());
        else tl = "Tab1";
        if (addToTabPane) {
            this.tabPane.add(tl, tab);
            this.controller.setupClosableTabs(this.tabPane, tl);
        }
        this.viewModels.add(model);
        return tab;
    }

    private void addTabButton() {
        tabPane.addTab("+", new JPanel());
        tabPane.getModel().addChangeListener(new ChangeListener() {
            private boolean ignore = false;

            @Override
            public void stateChanged(ChangeEvent e) {
                if (!ignore) {
                    ignore = true;
                    try {
                        int selected = tabPane.getSelectedIndex();
                        String title = tabPane.getTitleAt(selected);
                        if ("+".equals(title)) {
                            JPanel pane = createTab();
                            String tl = String.format("Tab%d", tabCounter.get());
                            tabPane.insertTab(tl, null, pane, null, tabPane.getTabCount() - 2);
                            tabPane.setSelectedComponent(pane);
                            controller.setupClosableTabs(tabPane, tl);
                        }
                    } finally {
                        ignore = false;
                    }
                }
            }
        });
    }
}
