package org.magetech.paq.installer.client;

import org.magetech.paq.ResourceUtils;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class InstallerWindow extends JDialog {
    private final int buttonY = 380;
    private final int buttonScreenOffset = 140;

    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;

    private boolean _install;

    public InstallerWindow() throws IOException {
        super(null, ModalityType.TOOLKIT_MODAL);

        setupComponents();
        setIconImages(Images.getIcons());
        setMinimumSize(new Dimension(799, 599));
        setMaximumSize(new Dimension(799, 599));

        setUndecorated(true);
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

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

    private void onOK() {
// add your code here
        _install = true;
        dispose();
    }

    private void onCancel() {
// add your code here if necessary
        dispose();
    }

    public boolean shouldInstall() {
        return _install;
    }

    public static boolean run() throws IOException {
        InstallerWindow dialog = new InstallerWindow();
        dialog.pack();
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
        return dialog.shouldInstall();
    }

    private void setupComponents() throws IOException {
        BufferedImage image = ImageIO.read(ResourceUtils.getResourceAsStream(Images.BACKGROUND));
        ImageIcon icon = new ImageIcon(image);
        JLabel labelBackground = new JLabel(icon);
        labelBackground.setBounds(0, 0, 800, 600);

        contentPane = new JPanel(null);
        buttonOK = makeTriStateButton(Images.INSTALL, Images.INSTALL_HOVER, Images.INSTALL_PRESS);
        buttonCancel = makeTriStateButton(Images.EXIT, Images.EXIT_HOVER, Images.EXIT_PRESS);


        contentPane.add(buttonOK);
        contentPane.add(buttonCancel);
        contentPane.add(labelBackground);
        contentPane.setMinimumSize(new Dimension(800, 600));
        contentPane.setMaximumSize(new Dimension(800, 600));

        buttonOK.setOpaque(false);
        buttonOK.setContentAreaFilled(false);
        buttonOK.setBorderPainted(false);
        buttonOK.setBounds(buttonScreenOffset, buttonY, buttonOK.getWidth(), buttonOK.getHeight());

        buttonCancel.setOpaque(false);
        buttonCancel.setContentAreaFilled(false);
        buttonCancel.setBorderPainted(false);
        buttonCancel.setBounds(800 - buttonScreenOffset - buttonCancel.getWidth(), buttonY, buttonCancel.getWidth(), buttonCancel.getHeight());
    }

    private JButton makeTriStateButton(String idle, String hover, String press) throws IOException {
        BufferedImage idleImage = ImageIO.read(ResourceUtils.getResourceAsStream(idle));
        ImageIcon idleIcon = new ImageIcon(idleImage);

        BufferedImage hoverImage = ImageIO.read(ResourceUtils.getResourceAsStream(hover));
        ImageIcon hoverIcon = new ImageIcon(hoverImage);

        BufferedImage pressImage = ImageIO.read(ResourceUtils.getResourceAsStream(press));
        ImageIcon pressIcon = new ImageIcon(pressImage);

        JButton button = new JButton();
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setIcon(idleIcon);
        button.setRolloverIcon(hoverIcon);
        button.setPressedIcon(pressIcon);
        //button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        Dimension size = new Dimension(idleImage.getWidth(), idleImage.getHeight());
        button.setMinimumSize(size);
        button.setMaximumSize(size);
        button.setPreferredSize(size);
        button.setSize(size);

        return button;
    }
}
