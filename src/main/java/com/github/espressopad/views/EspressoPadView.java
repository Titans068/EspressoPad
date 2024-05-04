package com.github.espressopad.views;

import bibliothek.gui.DockFrontend;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.DefaultDockable;
import bibliothek.gui.dock.SplitDockStation;
import bibliothek.gui.dock.event.DockFrontendAdapter;
import bibliothek.gui.dock.station.split.SplitDockProperty;
import com.github.espressopad.controller.EspressoPadController;
import com.github.espressopad.controller.TextEditorController;
import com.github.espressopad.models.SettingsModel;
import com.github.espressopad.models.ViewModel;
import com.github.espressopad.utils.Utils;
import com.github.espressopad.utils.XmlUtils;
import com.github.espressopad.views.components.FileTree;
import com.github.espressopad.views.components.TextEditor;
import org.fife.ui.rsyntaxtextarea.Theme;
import org.fife.ui.rtextarea.RTextScrollPane;
import org.kordamp.ikonli.fontawesome5.FontAwesomeRegular;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.swing.FontIcon;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class EspressoPadView extends JPanel {
    private final EspressoPadController controller = new EspressoPadController();
    private static final AtomicInteger tabCounter = new AtomicInteger(1);
    private final JTabbedPane tabPane = new JTabbedPane();
    private final JToolBar toolBar = new JToolBar();
    private final JMenuBar menuBar = new JMenuBar();
    private final List<ViewModel> viewModels = new ArrayList<>();
    private final TextEditorController editorController = new TextEditorController();
    private final XmlUtils handler = new XmlUtils();
    private final JFrame frame;
    private boolean ignore = false;
    private final SettingsModel settings;

    public EspressoPadView(JFrame frame) {
        this.frame = frame;
        this.frame.addWindowListener(new WindowClosingListener());
        this.settings = this.handler.parseSettingsXml();
        try {
            if (this.settings != null) {
                String laf = this.settings.getLookAndFeel();
                UIManager.setLookAndFeel(laf);
                SwingUtilities.updateComponentTreeUI(this.tabPane);
            }
        } catch (UnsupportedLookAndFeelException | ClassNotFoundException | InstantiationException |
                 IllegalAccessException ioe) {
            throw new RuntimeException(ioe);
        }
        this.createTab(false);
        this.addTabButton();
        this.setupInterface();
        this.setupMiddleMouseListener();
        this.controller.addArtifactsAndImports(EspressoPadController.getShell());
    }

    private void setupInterface() {
        this.setupDocking();
        this.setupMenuBar();
        this.setupToolBar();
    }

    private void setupDocking() {
        DockFrontend frontend = new DockFrontend(this.frame);
        SplitDockStation splitDockStation = new SplitDockStation();
        frontend.addRoot("root", splitDockStation);
        FileTree fileTree = new FileTree(Utils.validateDefaultDirectory().toFile());
        fileTree.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                File file = controller.setupTreeMouseListener(fileTree, e);
                if (file != null) {
                    openFile(file);
                    closeAllDuplicateTabs();
                }
            }
        });
        DefaultDockable fileTreeDockable = Utils.createDockable(new JScrollPane(fileTree), "File Tree");
        fileTreeDockable.setTitleIcon(FontIcon.of(FontAwesomeRegular.FILE_ALT, 15));
        frontend.addDockable("fileTree", fileTreeDockable);
        frontend.setHideable(fileTreeDockable, true);
        frontend.addFrontendListener(new FrontendAdapter(fileTreeDockable, frontend));
        splitDockStation.drop(fileTreeDockable, new SplitDockProperty(0, 0, .25, 1));
        DefaultDockable tabPaneDockable = Utils.createDockable(this.tabPane, "Open Files");
        tabPaneDockable.setTitleIcon(FontIcon.of(FontAwesomeSolid.FILE_CODE, 11));
        frontend.addDockable("results", tabPaneDockable);
        frontend.setHideable(tabPaneDockable, false);
        splitDockStation.drop(tabPaneDockable, new SplitDockProperty(0.25, 0, .75, 1));
        this.setLayout(new BorderLayout());
        this.add(splitDockStation, BorderLayout.CENTER);
    }

    private void setupTextEditorAppearance(TextEditor textEditor) {
        try {
            if (this.settings == null) return;
            String themeLocation = this.settings.getTheme();
            String font = this.settings.getFont();
            int fontSize = this.settings.getFontSize();
            boolean wordWrap = this.settings.isWordWrap();
            InputStream in = this.getClass().getResourceAsStream(themeLocation);
            Theme theme = Theme.load(in);
            theme.apply(textEditor);
            textEditor.setFont(new Font(font, Font.PLAIN, fontSize));
            textEditor.setLineWrap(wordWrap);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void setupToolBar() {
        JButton newFileBtn = new JButton(FontIcon.of(FontAwesomeSolid.FILE, 15));
        newFileBtn.setToolTipText("New File");
        newFileBtn.addActionListener(event -> this.createTab(true));
        this.toolBar.add(newFileBtn);

        JButton openFileBtn = new JButton(FontIcon.of(FontAwesomeSolid.FOLDER_OPEN, 15));
        openFileBtn.setToolTipText("Open File");
        openFileBtn.addActionListener(event -> this.openFile());
        this.toolBar.add(openFileBtn);

        JButton saveFileBtn = new JButton(FontIcon.of(FontAwesomeSolid.SAVE, 15));
        saveFileBtn.setToolTipText("Save");
        saveFileBtn.addActionListener(event -> this.saveFile());
        this.toolBar.add(saveFileBtn);

        this.toolBar.addSeparator();

        JButton undoFileBtn = new JButton(FontIcon.of(FontAwesomeSolid.UNDO, 15));
        undoFileBtn.setToolTipText("Undo");
        undoFileBtn.addActionListener(event -> this.editorController.undo(this.getCurrentTextEditor()));
        this.toolBar.add(undoFileBtn);

        JButton redoFileBtn = new JButton(FontIcon.of(FontAwesomeSolid.REDO, 15));
        redoFileBtn.setToolTipText("Redo");
        redoFileBtn.addActionListener(event -> this.editorController.redo(this.getCurrentTextEditor()));
        this.toolBar.add(redoFileBtn);

        this.toolBar.addSeparator();

        JButton cutFileBtn = new JButton(FontIcon.of(FontAwesomeSolid.CUT, 15));
        cutFileBtn.setToolTipText("Cut");
        cutFileBtn.addActionListener(event -> this.editorController.cut(this.getCurrentTextEditor()));
        this.toolBar.add(cutFileBtn);

        JButton copyFileBtn = new JButton(FontIcon.of(FontAwesomeSolid.COPY, 15));
        copyFileBtn.setToolTipText("Copy");
        copyFileBtn.addActionListener(event -> this.editorController.copy(this.getCurrentTextEditor()));
        this.toolBar.add(copyFileBtn);

        JButton pasteFileBtn = new JButton(FontIcon.of(FontAwesomeSolid.PASTE, 15));
        pasteFileBtn.setToolTipText("Paste");
        pasteFileBtn.addActionListener(event -> this.editorController.paste(this.getCurrentTextEditor()));
        this.toolBar.add(pasteFileBtn);

        this.toolBar.addSeparator();

        JButton findBtn = new JButton(FontIcon.of(FontAwesomeSolid.SEARCH, 15));
        findBtn.setToolTipText("Find");
        findBtn.addActionListener(event ->
                this.editorController.findAction(
                        this.getCurrentTextEditor(), this.getCurrentViewModel().getStatusBar())
        );
        this.toolBar.add(findBtn);

        this.toolBar.addSeparator();

        JButton runBtn = new JButton(FontIcon.of(FontAwesomeSolid.PLAY, 15));
        runBtn.setToolTipText("Run");
        runBtn.addActionListener(event -> this.controller.run(this.getCurrentViewModel()));
        this.toolBar.add(runBtn);

        this.toolBar.addSeparator();

        this.add(this.toolBar, BorderLayout.NORTH);
    }

    private void setupMenuBar() {
        JMenu fileMenu = new JMenu("File");
        int ctrlDownMask = Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx();

        JMenuItem newFileItem = new JMenuItem("New File");
        newFileItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ctrlDownMask));
        newFileItem.setMnemonic('N');
        newFileItem.addActionListener(event -> this.createTab(true));
        fileMenu.add(newFileItem);

        JMenuItem openFileItem = new JMenuItem("Open File");
        openFileItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ctrlDownMask));
        openFileItem.setMnemonic('O');
        openFileItem.addActionListener(event -> this.openFile());
        fileMenu.add(openFileItem);

        fileMenu.add(new JSeparator());

        JMenuItem saveFileItem = new JMenuItem("Save File");
        saveFileItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ctrlDownMask));
        saveFileItem.setMnemonic('S');
        saveFileItem.addActionListener(event -> this.saveFile());
        fileMenu.add(saveFileItem);

        JMenuItem saveAsFileItem = new JMenuItem("Save File As");
        saveAsFileItem.setAccelerator(
                KeyStroke.getKeyStroke(KeyEvent.VK_S, ctrlDownMask | InputEvent.SHIFT_DOWN_MASK)
        );
        saveAsFileItem.setMnemonic('A');
        saveAsFileItem.addActionListener(event -> this.saveFileAs());
        fileMenu.add(saveAsFileItem);

        fileMenu.add(new JSeparator());

        JMenuItem closeFileItem = new JMenuItem("Close File");
        closeFileItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, ctrlDownMask));
        closeFileItem.setMnemonic('C');
        closeFileItem.addActionListener(event -> this.removeCurrentTab());
        fileMenu.add(closeFileItem);

        fileMenu.add(new JSeparator());

        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.setMnemonic('x');
        exitItem.addActionListener(event -> this.exit());
        fileMenu.add(exitItem);

        JMenu editMenu = new JMenu("Edit");
        JMenuItem undoItem = new JMenuItem("Undo");
        undoItem.setMnemonic('u');
        undoItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, ctrlDownMask));
        undoItem.addActionListener(event -> this.editorController.undo(this.getCurrentTextEditor()));
        editMenu.add(undoItem);

        JMenuItem redoItem = new JMenuItem("Redo");
        redoItem.setMnemonic('r');
        redoItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, ctrlDownMask));
        redoItem.addActionListener(event -> this.editorController.redo(this.getCurrentTextEditor()));
        editMenu.add(redoItem);

        editMenu.add(new JSeparator());

        JMenuItem cutItem = new JMenuItem("Cut");
        cutItem.setMnemonic('t');
        cutItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, ctrlDownMask));
        cutItem.addActionListener(event -> this.editorController.cut(this.getCurrentTextEditor()));
        editMenu.add(cutItem);

        JMenuItem copyItem = new JMenuItem("Copy");
        copyItem.setMnemonic('c');
        copyItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ctrlDownMask));
        copyItem.addActionListener(event -> this.editorController.copy(this.getCurrentTextEditor()));
        editMenu.add(copyItem);

        JMenuItem pasteItem = new JMenuItem("Paste");
        pasteItem.setMnemonic('p');
        pasteItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, ctrlDownMask));
        pasteItem.addActionListener(event -> this.editorController.paste(this.getCurrentTextEditor()));
        editMenu.add(pasteItem);

        editMenu.add(new JSeparator());

        JMenuItem findItem = new JMenuItem("Find");
        findItem.setMnemonic('f');
        findItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, ctrlDownMask));
        findItem.addActionListener(event ->
                this.editorController.findAction(
                        this.getCurrentTextEditor(),
                        this.getCurrentViewModel().getStatusBar()
                )
        );
        editMenu.add(findItem);

        JMenuItem replaceItem = new JMenuItem("Replace");
        replaceItem.setMnemonic('e');
        replaceItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, ctrlDownMask));
        replaceItem.addActionListener(event ->
                this.editorController.replaceAction(
                        this.getCurrentTextEditor(),
                        this.getCurrentViewModel().getStatusBar()
                )
        );
        editMenu.add(replaceItem);

        editMenu.add(new JSeparator());

        JMenuItem selectAllItem = new JMenuItem("Select All");
        selectAllItem.setMnemonic('a');
        selectAllItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, ctrlDownMask));
        selectAllItem.addActionListener(event -> this.editorController.selectAll(this.getCurrentTextEditor()));
        editMenu.add(selectAllItem);

        JMenuItem goToLineItem = new JMenuItem("Go to line");
        goToLineItem.setMnemonic('g');
        goToLineItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G, ctrlDownMask));
        goToLineItem.addActionListener(event -> this.editorController.setupGoToLine(this.getCurrentTextEditor()));
        editMenu.add(goToLineItem);

        JMenuItem duplicateSelectionItem = new JMenuItem("Duplicate");
        duplicateSelectionItem.setMnemonic('d');
        duplicateSelectionItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T, ctrlDownMask));
        duplicateSelectionItem.addActionListener(event ->
                this.editorController.duplicateSelectionAction(this.getCurrentTextEditor()));
        editMenu.add(duplicateSelectionItem);

        JMenuItem reformatSelectionItem = new JMenuItem("Reformat");
        reformatSelectionItem.setMnemonic('o');
        reformatSelectionItem.setAccelerator(
                KeyStroke.getKeyStroke(KeyEvent.VK_L, ctrlDownMask | InputEvent.ALT_DOWN_MASK)
        );
        reformatSelectionItem.addActionListener(event ->
                this.editorController.reformatSelectionAction(this.getCurrentTextEditor()));
        editMenu.add(reformatSelectionItem);

        JMenu runMenu = new JMenu("Run");
        JMenuItem runMenuItem = new JMenuItem("Execute");
        runMenuItem.addActionListener(event -> this.controller.run(this.getCurrentViewModel()));
        runMenu.add(runMenuItem);

        JMenu toolsMenu = new JMenu("Tools");
        JMenuItem settingsMenuItem = new JMenuItem("Settings");
        settingsMenuItem.addActionListener(event ->
                new SettingsView(
                        this.viewModels.stream().map(ViewModel::getTextEditor).collect(Collectors.toList()),
                        this.settings
                ).show()
        );
        toolsMenu.add(settingsMenuItem);

        JMenu helpMenu = new JMenu("Help");
        JMenuItem aboutMenuItem = new JMenuItem("About");
        aboutMenuItem.addActionListener(event -> new AboutView(this.frame).show());
        helpMenu.add(aboutMenuItem);

        this.menuBar.add(fileMenu);
        this.menuBar.add(editMenu);
        this.menuBar.add(runMenu);
        this.menuBar.add(toolsMenu);
        this.menuBar.add(helpMenu);
        this.frame.setJMenuBar(this.menuBar);
    }

    private ViewModel getCurrentViewModel() {
        return this.viewModels.get(this.tabPane.getSelectedIndex());
    }

    private TextEditor getCurrentTextEditor() {
        return this.getCurrentViewModel().getTextEditor();
    }

    private JPanel createTab(boolean prepend) {
        String title;
        if (this.tabPane.getTabCount() > 1)
            title = String.format("Tab%d", tabCounter.incrementAndGet());
        else title = "Tab1";
        ViewModel model = new ViewModel();
        this.setupTab(model, title);

        JPanel tab = model.getTab();
        TextEditor textEditor = model.getTextEditor();
        if (!prepend)
            this.tabPane.addTab(title, tab);
        else this.tabPane.insertTab(title, null, tab, null, this.tabPane.getTabCount() - 1);

        this.setupClosableTabs(title);
        this.setupTextEditorAppearance(textEditor);
        this.controller.setupTextChangeListener(textEditor);
        this.viewModels.add(model);
        return tab;
    }

    private void setupTab(ViewModel model, String title) {
        model.getTab().setLayout(new BorderLayout());
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
        panel.add(new JScrollPane(model.getResultView()));
        panel.add(model.getStatusBar());
        DockFrontend controller = new DockFrontend(this.frame);
        SplitDockStation station = new SplitDockStation();
        controller.addRoot("root", station);
        RTextScrollPane scrollPane = new RTextScrollPane(model.getTextEditor());
        model.getTextEditor().setScrollPane(scrollPane);
        DefaultDockable textDock = Utils.createDockable(scrollPane, title);
        controller.addDockable("document", textDock);
        controller.setHideable(textDock, false);
        station.drop(textDock, new SplitDockProperty(0, 0, 1, .6));
        DefaultDockable dockable = Utils.createDockable(panel, "Results");
        dockable.setTitleIcon(FontIcon.of(FontAwesomeSolid.GLASSES, 11));
        controller.addDockable("results", dockable);
        controller.setHideable(dockable, false);
        station.drop(dockable, new SplitDockProperty(0, .75, 1, .4));
        model.getTab().add(station, BorderLayout.CENTER);
        model.getTextEditor().requestFocusInWindow();
    }

    private void openFile() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("JSH file", "jsh"));
        if (chooser.showOpenDialog(this.frame) == JFileChooser.APPROVE_OPTION) {
            this.openFile(chooser.getSelectedFile());
            this.closeAllDuplicateTabs();
        }
    }

    private JPanel openFile(File file) {
        ViewModel model = new ViewModel();
        try {
            String title = file.getName();
            this.setupTab(model, title);
            JPanel tab = model.getTab();
            TextEditor textEditor = model.getTextEditor();
            textEditor.setText(Files.readString(file.toPath()));
            this.setupTextEditorAppearance(textEditor);
            model.setBackingFile(file);
            this.tabPane.insertTab(title, null, tab, null, this.tabPane.getTabCount() - 1);
            this.tabPane.setSelectedComponent(tab);
            this.setupClosableTabs(title);
            this.controller.setupTextChangeListener(textEditor);
            this.viewModels.add(model);
            return tab;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void addTabButton() {
        this.tabPane.addTab(null, FontIcon.of(FontAwesomeSolid.PLUS, 12), new JPanel());
        this.tabPane.getModel().addChangeListener(this::tabStateChanged);
    }

    private void tabStateChanged(ChangeEvent e) {
        if (!this.ignore) {
            this.ignore = true;
            try {
                int selected = this.tabPane.getSelectedIndex();
                FontIcon icon = (FontIcon) this.tabPane.getIconAt(selected);
                if (icon != null && FontAwesomeSolid.PLUS.getCode() == icon.getIkon().getCode() &&
                        selected == this.tabPane.getTabCount() - 1) {
                    JPanel pane = this.createTab(false);
                    String tl = String.format("Tab%d", tabCounter.get());
                    this.tabPane.insertTab(tl, null, pane, null, this.tabPane.getTabCount() - 2);
                    this.tabPane.setSelectedComponent(pane);
                    this.setupClosableTabs(tl);
                }
            } finally {
                this.ignore = false;
            }
        }
    }

    private void saveFile() {
        ViewModel currentViewModel = this.getCurrentViewModel();
        File savedFile = this.editorController.saveFile(currentViewModel);
        if (savedFile != null) {
            this.openFile(savedFile);
            this.tabPane.removeTabAt(this.viewModels.indexOf(this.getCurrentViewModel()) - 1);
            this.setupClosableTabs(savedFile.getName());
            this.viewModels.remove(this.getCurrentViewModel());
        }
    }

    private void saveFileAs() {
        ViewModel currentViewModel = this.getCurrentViewModel();
        File savedFile = this.editorController.saveFileAs(currentViewModel);
        if (savedFile != null) {
            this.openFile(savedFile);
        }
    }

    private void setupClosableTabs(String title) {
        int index = this.tabPane.indexOfTab(title);
        JPanel pnlTab = new JPanel(new GridBagLayout());
        pnlTab.setOpaque(false);
        JLabel lblTitle = new JLabel(title);
        JButton btnClose = new JButton();
        btnClose.setIcon(FontIcon.of(FontAwesomeSolid.TIMES, 12));
        CompoundBorder border = BorderFactory.createCompoundBorder(BorderFactory
                .createEmptyBorder(), BorderFactory.createEmptyBorder(2, 2, 0, 2));
        btnClose.setBorder(border);
        btnClose.setContentAreaFilled(false);
        btnClose.setFocusable(false);
        btnClose.addActionListener(e -> removeCurrentTab());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.insets = new Insets(0, 10, 0, 1);

        pnlTab.add(lblTitle, gbc);

        gbc.gridx++;
        gbc.weightx = 0;
        pnlTab.add(btnClose, gbc);

        this.tabPane.setTabComponentAt(index, pnlTab);
    }

    private void setupMiddleMouseListener() {
        this.tabPane.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (tabPane.getBoundsAt(tabPane.getSelectedIndex()).contains(e.getPoint()) &&
                        SwingUtilities.isMiddleMouseButton(e))
                    removeCurrentTab();
            }
        });
    }

    private void removeCurrentTab() {
        if (this.tabPane.getTabCount() <= 2) return;
        this.viewModels.remove(this.tabPane.getSelectedIndex());
        this.tabPane.remove(this.tabPane.getSelectedComponent());
        this.tabPane.setSelectedComponent(this.tabPane.getComponentAt(this.tabPane.getTabCount() - 2));
    }

    private void closeAllDuplicateTabs() {
        String selectedTitle = this.tabPane.getTitleAt(this.tabPane.getSelectedIndex());
        Set<String> checker = new HashSet<>();
        for (int i = 0; i < this.tabPane.getTabCount(); i++) {
            if (!checker.add(this.tabPane.getTitleAt(i)))
                this.tabPane.removeTabAt(i);
        }
        checker.clear();
        for (int i = 0; i < this.viewModels.size(); i++) {
            File backingFile = this.viewModels.get(i).getBackingFile();
            if (backingFile != null && !checker.add(backingFile.getName()))
                this.viewModels.remove(i);
        }
        this.tabPane.setSelectedIndex(this.tabPane.indexOfTab(selectedTitle));
    }

    private boolean checkUnsaved() {
        List<ViewModel> unsavedViewModels = this.viewModels.stream()
                .filter(viewModel -> viewModel.getTextEditor().isDirty())
                .collect(Collectors.toList());

        try {
            if (!unsavedViewModels.isEmpty()) {
                List<String> unsavedFiles = new ArrayList<>();
                for (int i = 0; i < this.viewModels.size(); i++) {
                    ViewModel viewModel = this.viewModels.get(i);
                    File backingFile = viewModel.getBackingFile();
                    if (viewModel.getTextEditor().isDirty()) {
                        if (backingFile != null)
                            unsavedFiles.add(viewModel.getBackingFile().getPath());
                        else unsavedFiles.add(this.tabPane.getTitleAt(i));
                    }
                }
                switch (new SavePromptDialog(this.frame, unsavedFiles).getResult()) {
                    case JOptionPane.YES_OPTION:
                        for (ViewModel unsavedFile : unsavedViewModels) {
                            File backingFile = unsavedFile.getBackingFile();
                            if (backingFile != null)
                                Files.writeString(backingFile.toPath(), unsavedFile.getTextEditor().getText());
                            else this.editorController.saveFileAs(unsavedFile);
                        }
                        return true;
                    case JOptionPane.NO_OPTION:
                        return true;
                    case JOptionPane.CANCEL_OPTION:
                    case JOptionPane.CLOSED_OPTION:
                        return false;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return true;
    }

    private void exit() {
        if (this.checkUnsaved()) {
            this.controller.close();
            System.exit(0);
        }
    }

    class WindowClosingListener extends WindowAdapter {
        @Override
        public void windowClosing(WindowEvent e) {
            exit();
        }
    }

    class FrontendAdapter extends DockFrontendAdapter {
        private final JButton showFileTree;
        private final DefaultDockable fileTreeDockable;
        private final DockFrontend frontend;

        public FrontendAdapter(DefaultDockable fileTreeDockable, DockFrontend frontend) {
            this.fileTreeDockable = fileTreeDockable;
            this.frontend = frontend;
            this.showFileTree = new JButton(FontIcon.of(FontAwesomeSolid.WINDOW_RESTORE, 15));
            this.showFileTree.setToolTipText("Restore Default View");
            this.showFileTree.addActionListener(this::setupShowFileTree);
        }

        @Override
        public void hidden(DockFrontend dockFrontend, Dockable dockable) {
            if (dockable == this.fileTreeDockable)
                EspressoPadView.this.toolBar.add(this.showFileTree);
        }

        @Override
        public void shown(DockFrontend frontend, Dockable dockable) {
            if (dockable == this.fileTreeDockable) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        EspressoPadView.this.toolBar.remove(FrontendAdapter.this.showFileTree);
                        SwingUtilities.updateComponentTreeUI(EspressoPadView.this.toolBar);
                    }
                });
            }
        }

        private void setupShowFileTree(ActionEvent event) {
            if (this.frontend.isHidden(this.fileTreeDockable)) {
                this.frontend.show(this.fileTreeDockable);
            }
        }
    }
}
