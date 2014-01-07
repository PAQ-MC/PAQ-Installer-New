package org.magetech.paq.installer.client;

import org.magetech.paq.Out;
import org.magetech.paq.ResourceUtils;
import org.magetech.paq.installer.DialogInstallAdapter;
import org.magetech.paq.installer.Installer;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;

public class InstallerWindow extends JDialog {
    private final int buttonY = 380;
    private final int buttonScreenOffset = 140;

    private final String _mod;
    private final String _version;
    private final boolean _preview;

    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;

    private boolean _install;

    public InstallerWindow(String mod, String version, boolean preview) throws IOException {
        super(null, ModalityType.TOOLKIT_MODAL);


        _mod = mod;
        _version = version;
        _preview = preview;

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
        install();
    }

    private void onCancel() {
// add your code here if necessary
        dispose();
    }

    private void install() {
        final ProgressMonitor monitor = new ProgressMonitor(this, "Installing", null, 0, 1);
        monitor.setMillisToPopup(0);
        monitor.setMillisToDecideToPopup(0);

        final Installer installer = new Installer(new DialogInstallAdapter(this, "Installing") {

            @Override
            public File downloadManually(final String url, final String fileName) throws IOException, InvocationTargetException, InterruptedException {
                final Out<File> result = new Out<File>();
                final Out<IOException> exceptionOut = new Out<IOException>();
                final Out<Boolean> success = new Out<Boolean>(false);
                SwingUtilities.invokeAndWait(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            while(true) {
                                Desktop.getDesktop().browse(URI.create(url));
                                JOptionPane.showMessageDialog(InstallerWindow.this, fileName + " needs to be manually downloaded. A browser window just opened, please download the file and select it afterwords in the file browser.");

                                JFileChooser c = new JFileChooser();
                                for(FileFilter f : c.getChoosableFileFilters())
                                    c.removeChoosableFileFilter(f);

                                c.addChoosableFileFilter(new FileFilter() {
                                    @Override
                                    public boolean accept(File f) {
                                        return f.isDirectory() || f.getPath().endsWith(fileName);
                                    }

                                    @Override
                                    public String getDescription() {
                                        return fileName;
                                    }
                                });

                                //c.setSelectedFile(new File(fileName));
                                c.setDialogTitle("Select downloaded file");
                                c.grabFocus();
                                int retVal = c.showOpenDialog(InstallerWindow.this);
                                if(retVal == JFileChooser.APPROVE_OPTION) {
                                    File f = c.getSelectedFile();
                                    if(f.exists()) {
                                        result.setValue(f);
                                        success.setValue(true);
                                        return;
                                    }
                                    else {
                                        JOptionPane.showMessageDialog(InstallerWindow.this, "Non-existing file selected. Retrying.");
                                    }
                                }
                            }
                        } catch (IOException e) {
                            exceptionOut.setValue(e);
                            return;
                        }
                    }
                });
                if(success.getValue())
                    return result.getValue();

                throw exceptionOut.getValue();
            }
        });

        new Thread() {
            @Override
            public void run() {
                try {
                    installer.install(_mod, false, _version, _preview);
                    monitor.close();
                    JOptionPane.showMessageDialog(InstallerWindow.this, "Installation completed successfully", "Completed", JOptionPane.INFORMATION_MESSAGE);
                } catch (IOException | ClassNotFoundException | NoSuchMethodException | InvocationTargetException | IllegalAccessException | InterruptedException e) {
                    e.printStackTrace();
                }
                monitor.close();
                InstallerWindow.this.dispose();
            }
        }.start();
    }

    public static void run(final String mod, final String version, final boolean preview) throws IOException {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    InstallerWindow dialog = new InstallerWindow(mod, version, preview);
                    dialog.pack();
                    dialog.setLocationRelativeTo(null);
                    dialog.setVisible(true);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
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
