package il.dor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

import java.io.IOException;

public class GUI {

    private static final int MAX_ERRORS = 3;
    private Shell shell;
    private Label scoreLabel;
    private Composite questionPanel;
    private Font boldFont;
    private String lastAnswer = "";
    private String translations;
    private String strings;
    private String output;
    private String platform;

    public void open() {
        createShell();
        runApplication();
    }

    /**
     * Creates the widgets of the application main window
     */
    private void createShell() {
        Display display = Display.getDefault();
        shell = new Shell(display);
        shell.setText("Localization");

        // window style
        Rectangle monitor_bounds = shell.getMonitor().getBounds();
        shell.setSize(new Point(monitor_bounds.width / 3,
                monitor_bounds.height / 3));
        shell.setLayout(new GridLayout());

        FontData fontData = new FontData();
        fontData.setStyle(SWT.BOLD);
        boldFont = new Font(shell.getDisplay(), fontData);


        createFile1LoadingPanel();
        createFile2LoadingPanel();
        createTextField();
        createRadio();
        startButton();

    }

    /**
     * Creates the widgets of the form for trivia file selection
     */
    private void createFile1LoadingPanel() {
        final Composite fileSelection = new Composite(shell, SWT.NULL);
        fileSelection.setLayoutData(GUIUtils.createFillGridData(1));
        fileSelection.setLayout(new GridLayout(4, false));

        final Label label = new Label(fileSelection, SWT.NONE);
        label.setText("Enter translated strings file path: ");

        // text field to enter the file path
        final Text filePathField = new Text(fileSelection, SWT.SINGLE
                | SWT.BORDER);
        filePathField.setLayoutData(GUIUtils.createFillGridData(1));

        // "Browse" button
        final Button browseButton = new Button(fileSelection,
                SWT.PUSH);
        browseButton.setText("Browse");
        browseButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent selectionEvent) {
                filePathField.setText(GUIUtils.getFilePathFromFileDialog(shell));
                translations = filePathField.getText();

            }
        });
    }

    private void createFile2LoadingPanel() {
        final Composite fileSelection = new Composite(shell, SWT.NULL);
        fileSelection.setLayoutData(GUIUtils.createFillGridData(1));
        fileSelection.setLayout(new GridLayout(4, false));

        final Label label = new Label(fileSelection, SWT.NONE);
        label.setText("Enter English strings file path: ");

        // text field to enter the file path
        final Text filePathField = new Text(fileSelection, SWT.SINGLE
                | SWT.BORDER);
        filePathField.setLayoutData(GUIUtils.createFillGridData(1));

        // "Browse" button
        final Button browseButton = new Button(fileSelection,
                SWT.PUSH);
        browseButton.setText("Browse");
        browseButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent selectionEvent) {
                filePathField.setText(GUIUtils.getFilePathFromFileDialog(shell));
                strings = filePathField.getText();
            }
        });

    }

    private void createTextField() {
        final Composite fileSelection = new Composite(shell, SWT.NULL);
        fileSelection.setLayoutData(GUIUtils.createFillGridData(1));
        fileSelection.setLayout(new GridLayout(4, false));

        final Label label = new Label(fileSelection, SWT.NONE);
        label.setText("Enter the name of the output file");

        final Text filePathField = new Text(fileSelection, SWT.SINGLE
                | SWT.BORDER);
        filePathField.setLayoutData(GUIUtils.createFillGridData(1));
        filePathField.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent modifyEvent) {
                output = filePathField.getText();
            }
        });
    }

    private void createRadio() {
        final Composite fileSelection = new Composite(shell, SWT.NULL);
        fileSelection.setLayoutData(GUIUtils.createFillGridData(1));
        fileSelection.setLayout(new GridLayout(1, false));

        final Label label = new Label(fileSelection, SWT.NONE);
        label.setText("Platform");

        final Button radio1 = new Button(fileSelection, SWT.RADIO);
        radio1.setLayoutData(GUIUtils.createFillGridData(2));
        radio1.setText("iOS");
        radio1.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent selectionEvent) {
                platform = "iOS";
            }
        });

        final Button radio2 = new Button(fileSelection, SWT.RADIO);
        radio1.setLayoutData(GUIUtils.createFillGridData(1));
        radio2.setText("Android");
        radio2.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent selectionEvent) {
                platform = "Android";
            }
        });
    }

    private void startButton() {
        final Composite fileSelection = new Composite(shell, SWT.NULL);
        fileSelection.setLayoutData(GUIUtils.createFillGridData(1));
        fileSelection.setLayout(new GridLayout(4, false));

        // "Start" button
        final Button playButton = new Button(fileSelection, SWT.PUSH);
        playButton.setText("Translate");
        playButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent selectionEvent) {
                try {
                    int result = Manager.start(translations, strings, output, platform);
                    if (result == 0) {
                        GUIUtils.showInfoDialog(shell, "Success", "Translation is finished successfully");
                    } else if (result == 1) {
                        GUIUtils.showInfoDialog(shell, "Finished", "Translation is finished successfully.\n LP problems were fixed");
                    } else if (result == 2) {
                        GUIUtils.showInfoDialog(shell, "Finished", "Translation is finished successfully.\n Quotation marks problems were fixed");
                    } else if (result == 3) {
                        GUIUtils.showInfoDialog(shell, "Finished", "Translation is finished successfully.\n Quotation marks and LP problems were fixed");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });


    }

    /**
     * Opens the main window and executes the event loop of the application
     */
    private void runApplication() {
        shell.open();
        Display display = shell.getDisplay();
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch())
                display.sleep();
        }
        display.dispose();
        boldFont.dispose();
    }
}
