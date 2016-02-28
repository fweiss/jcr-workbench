package com.uttama.jcr.workbench.view.help;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;

/**
 * Created by frankw on 2/22/2016.
 */
public class HelpAction
extends AbstractAction {
    private static final Logger log = LoggerFactory.getLogger(AbstractAction.class);
    private JEditorPane help;
    public HelpAction(String name) {
        super(name);
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        JDialog mydialog = new JDialog();
        mydialog.setSize(new Dimension(400, 500));
        mydialog.setTitle("JCR Workbench - Help");
        mydialog.setModalityType(Dialog.ModalityType.MODELESS);
        mydialog.setVisible(true);

        String url = HelpAction.class.getResource("quickStartHelp.html").toExternalForm();
        log.trace("loading help from: " + url);
        try {
            JEditorPane editorPane = new JEditorPane(url);
            JScrollPane scrolledContent = new JScrollPane(editorPane);
            mydialog.setContentPane(scrolledContent);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }
}
