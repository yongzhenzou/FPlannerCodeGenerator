package com.lh.android.code.generator;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.event.*;

public class ConfigDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField rootPackageName;
    private JTextField layoutName;
    private JCheckBox ifGenerateLayout;
    private JTextField packageName;
    private JCheckBox ifGenerateActivity;
    private JCheckBox ifGenerateFragment;
    private JCheckBox ifGenerateViewModel;
    private JCheckBox ifGenerateDataBinding;
    private JComboBox sourceLanguage;
    private JTextField pageName;
    private DialogCallBack callBack;

    public ConfigDialog() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        setSize(900, 600);
        setLocationRelativeTo(null);
        buttonOK.setEnabled(false);
        ifGenerateActivity.setSelected(true);
        ifGenerateLayout.setSelected(true);
        ifGenerateLayout.setEnabled(false);
        ifGenerateViewModel.setSelected(true);
        ifGenerateDataBinding.setSelected(true);
        rootPackageName.setText("com.lhcredit.planner");
        packageName.setText(rootPackageName.getText() + ".module." + pageName.getText().toLowerCase());
        layoutName.setText("activity" + getLowerLetter(pageName.getText()));
        pageName.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateName();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateName();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateName();
            }
        });
        packageName.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateOkState();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateOkState();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateOkState();
            }
        });
        rootPackageName.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateOkState();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateOkState();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateOkState();
            }
        });
        layoutName.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateOkState();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateOkState();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateOkState();
            }
        });

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });
        ifGenerateActivity.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (ifGenerateActivity.isSelected()) {
                    ifGenerateFragment.setSelected(false);
                    layoutName.setText("activity" + getLowerLetter(pageName.getText()));
                }
            }
        });
        ifGenerateFragment.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (ifGenerateFragment.isSelected()) {
                    ifGenerateActivity.setSelected(false);
                    layoutName.setText("fragment" + getLowerLetter(pageName.getText()));
                }
            }
        });
        ifGenerateLayout.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
        ifGenerateDataBinding.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (ifGenerateDataBinding.isSelected()) {
                    ifGenerateLayout.setSelected(true);
                }
            }
        });
        ifGenerateViewModel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!ifGenerateFragment.isSelected() && !ifGenerateActivity.isSelected()) {
                    ifGenerateActivity.setSelected(true);
                }
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void updateName() {
        updateOkState();
        packageName.setText(rootPackageName.getText() + ".module." + pageName.getText().toLowerCase());
        layoutName.setText("activity" + getLowerLetter(pageName.getText()));
    }

    private void updateOkState() {
        if (pageName.getText() == null || pageName.getText().isEmpty() || layoutName.getText() == null || layoutName.getText().isEmpty() || packageName.getText() == null || packageName.getText().isEmpty() || rootPackageName.getText() == null || rootPackageName.getText().isEmpty()) {
            buttonOK.setEnabled(false);
        } else {
            buttonOK.setEnabled(true);
        }
    }

    private void onOK() {
        if (callBack != null) {
            callBack.onOkPress(pageName.getText(), rootPackageName.getText(), layoutName.getText(), ifGenerateLayout.isSelected(),
                    packageName.getText(), ifGenerateActivity.isSelected(), ifGenerateFragment.isSelected(), ifGenerateViewModel.isSelected(),
                    ifGenerateDataBinding.isSelected());
        }
        dispose();
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    public static void main(String[] args) {
        ConfigDialog dialog = new ConfigDialog();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }

    public void setCallBack(DialogCallBack callBack) {
        this.callBack = callBack;
    }

    public String getLowerLetter(String className) {
        String lowerLetter = "";
        char[] charArray = className.toCharArray();
        for (int i = 0; i < charArray.length; i++) {
            if (charArray[i] >= 'A' && charArray[i] <= 'Z') {
                lowerLetter += "_" + String.valueOf(charArray[i]).toLowerCase();
            } else {
                if (i == 0) {
                    lowerLetter += "_" + String.valueOf(charArray[i]).toLowerCase();
                } else {
                    lowerLetter += String.valueOf(charArray[i]).toLowerCase();
                }

            }
        }
        return lowerLetter;
    }

    interface DialogCallBack {
        void onOkPress(String pageName, String rootPackageName, String layoutName, Boolean ifGenerateLayout, String packageName,
                       boolean ifGenerateActivity, boolean ifGenerateFragment, boolean ifGenerateViewModel, boolean ifGenerateDataBinding);
    }


}
