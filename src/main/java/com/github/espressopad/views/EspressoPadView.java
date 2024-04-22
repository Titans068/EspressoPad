package com.github.espressopad.views;

import com.github.espressopad.controller.EspressoPadController;
import com.github.espressopad.controller.TextEditorController;
import com.github.espressopad.models.ViewModel;
import com.github.espressopad.utils.Utils;
import com.github.espressopad.views.components.FileTree;
import com.github.espressopad.views.components.TextEditor;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.swing.FontIcon;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class EspressoPadView extends JPanel {
    private final EspressoPadController controller = new EspressoPadController();
    private static final AtomicInteger tabCounter = new AtomicInteger(1);
    private final JTabbedPane tabPane = new JTabbedPane();
    private final JToolBar toolBar = new JToolBar();
    private final JMenuBar menuBar = new JMenuBar();
    private final List<ViewModel> viewModels = new ArrayList<>();
    private final TextEditorController editorController = new TextEditorController();
    private final JFrame frame;
    private boolean ignore = false;

    public EspressoPadView(JFrame frame) {
        this.frame = frame;
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(.5);
        splitPane.setResizeWeight(.3);
        FileTree fileTree = new FileTree(Utils.validateDefaultDirectory().toFile());
        fileTree.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                File file = controller.setupTreeMouseListener(fileTree, e);
                if (file != null)
                    openFile(file);
            }
        });
        splitPane.setLeftComponent(new JScrollPane(fileTree));
        splitPane.setRightComponent(this.tabPane);
        this.setLayout(new BorderLayout());
        this.add(splitPane, BorderLayout.CENTER);
        this.createTab(false);
        this.addTabButton();
        this.setupInterface();
        this.controller.setupMiddleMouseListener(this.tabPane);
    }

    private void setupInterface() {
        this.setupMenuBar();
        this.setupToolBar();
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
        saveFileBtn.addActionListener(event -> this.editorController.saveFile(this.getCurrentViewModel()));
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
        findBtn.addActionListener(event -> this.editorController.findAction(this.frame, this.getCurrentTextEditor(), this.getCurrentViewModel().getStatusBar()));
        this.toolBar.add(findBtn);

        this.toolBar.addSeparator();

        JButton runBtn = new JButton(FontIcon.of(FontAwesomeSolid.PLAY, 15));
        runBtn.setToolTipText("Run");
        runBtn.addActionListener(event -> this.controller.run(this.getCurrentViewModel()));
        this.toolBar.add(runBtn);

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
        saveFileItem.addActionListener(event -> this.editorController.saveFile(this.getCurrentViewModel()));
        fileMenu.add(saveFileItem);

        JMenuItem saveAsFileItem = new JMenuItem("Save File As");
        saveAsFileItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ctrlDownMask | InputEvent.SHIFT_DOWN_MASK));
        saveAsFileItem.setMnemonic('A');
        saveAsFileItem.addActionListener(event -> this.editorController.saveFileAs(this.getCurrentViewModel()));
        fileMenu.add(saveAsFileItem);

        fileMenu.add(new JSeparator());

        JMenuItem closeFileItem = new JMenuItem("Close File");
        closeFileItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, ctrlDownMask));
        closeFileItem.setMnemonic('C');
        closeFileItem.addActionListener(event -> this.controller.setupRemoveTab(this.tabPane));
        fileMenu.add(closeFileItem);

        JMenuItem clearAllSavedItem = new JMenuItem("Clear All Saved");
        clearAllSavedItem.setMnemonic('l');
        fileMenu.add(clearAllSavedItem);

        fileMenu.add(new JSeparator());

        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.setMnemonic('x');
        fileMenu.add(exitItem);

        JMenu editMenu = new JMenu("Edit");
        JMenuItem undoItem = new JMenuItem("Undo");
        undoItem.setMnemonic('z');
        undoItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, ctrlDownMask));
        undoItem.addActionListener(event -> this.editorController.undo(this.getCurrentTextEditor()));
        editMenu.add(undoItem);

        JMenuItem redoItem = new JMenuItem("Redo");
        redoItem.setMnemonic('y');
        redoItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, ctrlDownMask));
        redoItem.addActionListener(event -> this.editorController.redo(this.getCurrentTextEditor()));
        editMenu.add(redoItem);

        editMenu.add(new JSeparator());

        JMenuItem cutItem = new JMenuItem("Cut");
        cutItem.setMnemonic('x');
        cutItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, ctrlDownMask));
        cutItem.addActionListener(event -> this.editorController.cut(this.getCurrentTextEditor()));
        editMenu.add(cutItem);

        JMenuItem copyItem = new JMenuItem("Copy");
        copyItem.setMnemonic('c');
        copyItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ctrlDownMask));
        copyItem.addActionListener(event -> this.editorController.copy(this.getCurrentTextEditor()));
        editMenu.add(copyItem);

        JMenuItem pasteItem = new JMenuItem("Paste");
        pasteItem.setMnemonic('v');
        pasteItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, ctrlDownMask));
        pasteItem.addActionListener(event -> this.editorController.paste(this.getCurrentTextEditor()));
        editMenu.add(pasteItem);

        editMenu.add(new JSeparator());

        JMenuItem findItem = new JMenuItem("Find");
        findItem.setMnemonic('f');
        findItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, ctrlDownMask));
        findItem.addActionListener(event ->
                this.editorController.findAction(this.frame, this.getCurrentTextEditor(), this.getCurrentViewModel().getStatusBar()));
        editMenu.add(findItem);

        JMenuItem replaceItem = new JMenuItem("Replace");
        replaceItem.setMnemonic('r');
        replaceItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, ctrlDownMask));
        replaceItem.addActionListener(event ->
                this.editorController.replaceAction(this.frame, this.getCurrentTextEditor(), this.getCurrentViewModel().getStatusBar()));
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
        goToLineItem.addActionListener(event -> this.editorController.goToLineAction(this.frame, this.getCurrentTextEditor()));
        editMenu.add(goToLineItem);

        JMenuItem duplicateSelectionItem = new JMenuItem("Duplicate");
        duplicateSelectionItem.setMnemonic('t');
        duplicateSelectionItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T, ctrlDownMask));
        duplicateSelectionItem.addActionListener(event -> this.editorController.duplicateSelectionAction(this.getCurrentTextEditor()));
        editMenu.add(duplicateSelectionItem);

        JMenuItem reformatSelectionItem = new JMenuItem("Reformat");
        reformatSelectionItem.setMnemonic('e');
        reformatSelectionItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, ctrlDownMask | InputEvent.ALT_DOWN_MASK));
        reformatSelectionItem.addActionListener(event -> this.editorController.reformatSelectionAction(this.getCurrentTextEditor()));
        editMenu.add(reformatSelectionItem);

        JMenu runMenu = new JMenu("Run");
        JMenuItem runMenuItem = new JMenuItem("Execute");
        runMenuItem.addActionListener(event -> this.controller.run(this.getCurrentViewModel()));
        runMenu.add(runMenuItem);

        JMenu toolsMenu = new JMenu("Tools");
        JMenuItem settingsMenuItem = new JMenuItem("Settings");
        settingsMenuItem.addActionListener(event -> new SettingsView().show(this.frame));
        toolsMenu.add(settingsMenuItem);

        JMenu helpMenu = new JMenu("Help");

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
        JPanel tab = model.getTab();
        TextEditor textEditor = model.getTextEditor();
        if (!prepend)
            this.tabPane.addTab(title, tab);
        else this.tabPane.insertTab(title, null, tab, null, this.tabPane.getTabCount() - 1);

        this.controller.setupClosableTabs(this.tabPane, title);
        this.controller.setupTextChangeListener(textEditor);
        this.viewModels.add(model);
        return tab;
    }

    private void openFile() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("JSH file", "jsh"));
        if (chooser.showOpenDialog(this.frame) == JFileChooser.APPROVE_OPTION)
            this.openFile(chooser.getSelectedFile());
    }

    private JPanel openFile(File file) {
        try {
            String title = file.getName();
            ViewModel model = new ViewModel();
            JPanel tab = model.getTab();
            TextEditor textEditor = model.getTextEditor();
            textEditor.setText(Files.readString(file.toPath()));
            model.setBackingFile(file);
            this.tabPane.insertTab(title, null, tab, null, this.tabPane.getTabCount() - 1);
            this.tabPane.setSelectedComponent(tab);
            this.controller.setupClosableTabs(this.tabPane, title);
            this.controller.setupTextChangeListener(textEditor);
            this.viewModels.add(model);
            return tab;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void addTabButton() {
        tabPane.addTab("+", new JPanel());
        tabPane.getModel().addChangeListener(this::tabStateChanged);
    }

    private void tabStateChanged(ChangeEvent e) {
        if (!this.ignore) {
            this.ignore = true;
            try {
                int selected = this.tabPane.getSelectedIndex();
                String title = this.tabPane.getTitleAt(selected);
                if ("+".equals(title)) {
                    JPanel pane = this.createTab(false);
                    String tl = String.format("Tab%d", tabCounter.get());
                    this.tabPane.insertTab(tl, null, pane, null, this.tabPane.getTabCount() - 2);
                    this.tabPane.setSelectedComponent(pane);
                    this.controller.setupClosableTabs(this.tabPane, tl);
                }
            } finally {
                this.ignore = false;
            }
        }
    }
}
