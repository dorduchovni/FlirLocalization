package il.dor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

import java.io.IOException;
import java.util.*;
import java.util.List;

public class GUI {

    private Shell shell;
    private Font boldFont;
    private String translations;
    private String strings;
    private String output;
    private String platform;
    private boolean android = false;
    private boolean ios = false;
    private String excelFile;

    public void open() {
        createShell();
        runApplication();
    }

    /**
     * Creates the widgets of the application main window
     */
    private void createShell_original() {
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
        translateButton();
        sourceToExcelButton();

    }

// TEST TEST TEST TEST TEST
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
        createExcelLoadingPanel();
        createCheckBox();
        excelButton();


    }

    //TEST TEST TEST
    private void createNextShell() {
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
                String filePath = GUIUtils.getFilePathFromFileDialog(shell);
                if (filePath!=null) {
                    filePathField.setText(filePath);
                    translations = filePathField.getText();
                }
            }
        });
    }



    //TEST TEST TEST
    private void createExcelLoadingPanel() {
        final Composite fileSelection = new Composite(shell, SWT.NULL);
        fileSelection.setLayoutData(GUIUtils.createFillGridData(1));
        fileSelection.setLayout(new GridLayout(4, false));

        final Label label = new Label(fileSelection, SWT.NONE);
        label.setText("Enter the translation XSL file path: ");

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
                String filePath = GUIUtils.getFilePathFromFileDialog(shell);
                if (filePath!=null) {
                    filePathField.setText(filePath);
                    excelFile = filePathField.getText();
                }
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
                String filePath = GUIUtils.getFilePathFromFileDialog(shell);
                if (filePath!=null) {
                    filePathField.setText(filePath);
                    strings = filePathField.getText();
                }
            }
        });

    }

    private void createTextField() {
        final Composite fileSelection = new Composite(shell, SWT.NULL);
        fileSelection.setLayoutData(GUIUtils.createFillGridData(1));
        fileSelection.setLayout(new GridLayout(4, false));

        final Label label = new Label(fileSelection, SWT.NONE);
        label.setText("Enter the name of the output folder");

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

    // TEST TEST TEST
    private void createCheckBox() {
        final Composite fileSelection = new Composite(shell, SWT.NULL);
        fileSelection.setLayoutData(GUIUtils.createFillGridData(1));
        fileSelection.setLayout(new GridLayout(1, false));

        final Label label = new Label(fileSelection, SWT.NONE);
        label.setText("Platform");

        final Button radio1 = new Button(fileSelection, SWT.CHECK);
        radio1.setLayoutData(GUIUtils.createFillGridData(2));
        radio1.setText("iOS");
        radio1.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent selectionEvent) {
                ios = true;
            }
        });

        final Button radio2 = new Button(fileSelection, SWT.CHECK);
        radio1.setLayoutData(GUIUtils.createFillGridData(1));
        radio2.setText("Android");
        radio2.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent selectionEvent) {
                android = true;
            }
        });
    }


    // TEST TEST TEST
    private void excelButton() {
        final Composite fileSelection = new Composite(shell, SWT.NULL);
        fileSelection.setLayoutData(GUIUtils.createFillGridData(1));
        fileSelection.setLayout(new GridLayout(4, false));

        // "Start" button
        final Button playButton = new Button(fileSelection, SWT.PUSH);
        playButton.setText("Start");
        playButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent selectionEvent) {
                try {
                    LinkedHashMap<String, java.util.List<String>> sheetsAndLanguages = Manager.getSheetsAndLanguages(excelFile);
                    for (String sheet: sheetsAndLanguages.keySet()) {
                        sheetsAndLanguagesUI(sheet,sheetsAndLanguages.get(sheet));
                    }
                    shell.setMaximized(true);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });


    }

    private void sheetsAndLanguagesUI (String sheet, List<String> languages) {
        final Composite fileSelection = new Composite(shell, SWT.NULL);
        fileSelection.setLayoutData(GUIUtils.createFillGridData(1));
        fileSelection.setLayout(new GridLayout(4, false));

        final Label label = new Label(fileSelection, SWT.NONE);
        label.setText("Sheet name: " +sheet);

        final Label label2 = new Label(fileSelection, SWT.NONE);
        label2.setText("English strings path: ");
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
                String filePath = GUIUtils.getFilePathFromFileDialog(shell);
                if (filePath!=null) {
                    filePathField.setText(filePath);

                }
            }
        });

        for (String language:languages) {
            final Button radio1 = new Button(fileSelection, SWT.CHECK);
            radio1.setLayoutData(GUIUtils.createFillGridData(1));
            radio1.setText(language);
            radio1.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent selectionEvent) {

                }
            });

        }
        Label separator = new Label(shell, SWT.HORIZONTAL | SWT.SEPARATOR);
        separator.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    }



    private void translateButton() {
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
                    boolean[] results = Manager.startTranslation(translations, strings, output, platform);
                    String popupBody = "";
                    for (int i = 0; i<results.length; i++) {
                        if (results[i]) {
                            if (i == 0) {
                                popupBody = popupBody + "LP errors fixed" +"\n";
                            } else if (i == 1) {
                                popupBody = popupBody + "Quotation marks fixed" + "\n";
                            } else if (i == 2) {
                                popupBody = popupBody + "Apostrophe errors fixed\n";
                            } else if (i==3) {
                                popupBody = popupBody + "Additional strings generated";
                            }
                        }
                    }
                    if (popupBody.equals("")) {
                        popupBody = "No errors found";
                    }

                    GUIUtils.showInfoDialog(shell, "Finished", popupBody);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });


    }


    private void sourceToExcelButton() {
        final Composite fileSelection = new Composite(shell, SWT.NULL);
        fileSelection.setLayoutData(GUIUtils.createFillGridData(1));
        fileSelection.setLayout(new GridLayout(4, false));

        // "Start" button
        final Button playButton = new Button(fileSelection, SWT.PUSH);
        playButton.setText("Export source to excel");
        playButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent selectionEvent) {
                try {
                    boolean[] results = Manager.sourceToExcel(translations, output, platform);
                    String popupBody = "";
                    for (int i = 0; i<results.length; i++) {
                        if (results[i]) {
                            if (i == 0) {
                                popupBody = popupBody + "LP errors fixed" +"\n";
                            } else if (i == 1) {
                                popupBody = popupBody + "Quotation marks fixed" + "\n";
                            } else if (i == 2) {
                                popupBody = popupBody + "Apostrophe errors fixed\n";
                            } else if (i==3) {
                                popupBody = popupBody + "Additional strings generated";
                            }
                        }
                    }
                    if (popupBody.equals("")) {
                        popupBody = "No errors found";
                    }
                    GUIUtils.showInfoDialog(shell, "Finished", popupBody);
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
