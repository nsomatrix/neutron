/*
 * Decompiled with CFR 0.152.
 */
package org.microemu.app.ui.swing;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.microemu.EmulatorContext;
import org.microemu.app.Common;
import org.microemu.app.Config;
import org.microemu.app.ui.Message;
import org.microemu.app.ui.swing.ExtensionFileFilter;
import org.microemu.app.ui.swing.SwingDialogPanel;
import org.microemu.app.util.DeviceEntry;
import org.microemu.app.util.IOUtils;
import org.microemu.device.Device;
import org.microemu.device.impl.DeviceImpl;

public class SwingSelectDevicePanel
extends SwingDialogPanel {
    private static final long serialVersionUID = 1L;
    private EmulatorContext emulatorContext;
    private JScrollPane spDevices;
    private JButton btAdd;
    private JButton btRemove;
    private JButton btDefault;
    private DefaultListModel lsDevicesModel;
    private JList lsDevices;
    private ActionListener btAddListener = new ActionListener(){
        private JFileChooser fileChooser = null;

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public void actionPerformed(ActionEvent ev) {
            if (this.fileChooser == null) {
                this.fileChooser = new JFileChooser();
                ExtensionFileFilter fileFilter = new ExtensionFileFilter("Device profile (*.jar, *.zip)");
                fileFilter.addExtension("jar");
                fileFilter.addExtension("zip");
                this.fileChooser.setFileFilter(fileFilter);
            }
            if (this.fileChooser.showOpenDialog(SwingSelectDevicePanel.this) == 0) {
                DeviceEntry entry;
                String manifestDeviceName = null;
                URL[] urls = new URL[1];
                ArrayList<String> descriptorEntries = new ArrayList<String>();
                JarFile jar = null;
                try {
                    jar = new JarFile(this.fileChooser.getSelectedFile());
                    Manifest manifest = jar.getManifest();
                    if (manifest != null) {
                        Attributes attrs = manifest.getMainAttributes();
                        manifestDeviceName = attrs.getValue("Device-Name");
                    }
                    Enumeration<JarEntry> en = jar.entries();
                    while (en.hasMoreElements()) {
                        String entry2 = en.nextElement().getName();
                        if (!entry2.toLowerCase().endsWith(".xml") && !entry2.toLowerCase().endsWith("device.txt") || entry2.toLowerCase().startsWith("meta-inf")) continue;
                        descriptorEntries.add(entry2);
                    }
                    urls[0] = this.fileChooser.getSelectedFile().toURL();
                }
                catch (IOException e) {
                    Message.error("Error reading file: " + this.fileChooser.getSelectedFile().getName() + ", " + Message.getCauseMessage(e), e);
                    return;
                }
                finally {
                    if (jar != null) {
                        try {
                            jar.close();
                        }
                        catch (IOException ignore) {}
                    }
                }
                if (descriptorEntries.size() == 0) {
                    Message.error("Cannot find any device profile in file: " + this.fileChooser.getSelectedFile().getName());
                    return;
                }
                if (descriptorEntries.size() > 1) {
                    manifestDeviceName = null;
                }
                ClassLoader classLoader = Common.createExtensionsClassLoader(urls);
                HashMap<String, DeviceImpl> devices = new HashMap<String, DeviceImpl>();
                Iterator it = descriptorEntries.iterator();
                while (it.hasNext()) {
                    String entryName = (String)it.next();
                    try {
                        devices.put(entryName, DeviceImpl.create(SwingSelectDevicePanel.this.emulatorContext, classLoader, entryName, class$org$microemu$device$j2se$J2SEDevice == null ? SwingSelectDevicePanel.class$("org.microemu.device.j2se.J2SEDevice") : class$org$microemu$device$j2se$J2SEDevice));
                    }
                    catch (IOException e) {
                        Message.error("Error parsing device profile, " + Message.getCauseMessage(e), e);
                        return;
                    }
                }
                Enumeration en = SwingSelectDevicePanel.this.lsDevicesModel.elements();
                while (en.hasMoreElements()) {
                    entry = (DeviceEntry)en.nextElement();
                    if (!devices.containsKey(entry.getDescriptorLocation())) continue;
                    devices.remove(entry.getDescriptorLocation());
                }
                if (devices.size() == 0) {
                    Message.info("Device profile already added");
                    return;
                }
                try {
                    File deviceFile = new File(Config.getConfigPath(), this.fileChooser.getSelectedFile().getName());
                    if (deviceFile.exists()) {
                        deviceFile = File.createTempFile("device", ".jar", Config.getConfigPath());
                    }
                    IOUtils.copyFile(this.fileChooser.getSelectedFile(), deviceFile);
                    entry = null;
                    Iterator it2 = devices.keySet().iterator();
                    while (it2.hasNext()) {
                        String descriptorLocation = (String)it2.next();
                        Device device = (Device)devices.get(descriptorLocation);
                        entry = manifestDeviceName != null ? new DeviceEntry(manifestDeviceName, deviceFile.getName(), descriptorLocation, false) : new DeviceEntry(device.getName(), deviceFile.getName(), descriptorLocation, false);
                        SwingSelectDevicePanel.this.lsDevicesModel.addElement(entry);
                        Config.addDeviceEntry(entry);
                    }
                    SwingSelectDevicePanel.this.lsDevices.setSelectedValue(entry, true);
                }
                catch (IOException e) {
                    Message.error("Error adding device profile, " + Message.getCauseMessage(e), e);
                    return;
                }
            }
        }
    };
    private ActionListener btRemoveListener = new ActionListener(){

        public void actionPerformed(ActionEvent ev) {
            DeviceEntry entry = (DeviceEntry)SwingSelectDevicePanel.this.lsDevices.getSelectedValue();
            boolean canDeleteFile = true;
            Enumeration en = SwingSelectDevicePanel.this.lsDevicesModel.elements();
            while (en.hasMoreElements()) {
                DeviceEntry test = (DeviceEntry)en.nextElement();
                if (test == entry || test.getFileName() == null || !test.getFileName().equals(entry.getFileName())) continue;
                canDeleteFile = false;
                break;
            }
            if (canDeleteFile) {
                File deviceFile = new File(Config.getConfigPath(), entry.getFileName());
                deviceFile.delete();
            }
            if (entry.isDefaultDevice()) {
                en = SwingSelectDevicePanel.this.lsDevicesModel.elements();
                while (en.hasMoreElements()) {
                    DeviceEntry tmp = (DeviceEntry)en.nextElement();
                    if (tmp.canRemove()) continue;
                    tmp.setDefaultDevice(true);
                    break;
                }
            }
            SwingSelectDevicePanel.this.lsDevicesModel.removeElement(entry);
            Config.removeDeviceEntry(entry);
        }
    };
    private ActionListener btDefaultListener = new ActionListener(){

        public void actionPerformed(ActionEvent ev) {
            DeviceEntry entry = (DeviceEntry)SwingSelectDevicePanel.this.lsDevices.getSelectedValue();
            Enumeration en = SwingSelectDevicePanel.this.lsDevicesModel.elements();
            while (en.hasMoreElements()) {
                DeviceEntry tmp = (DeviceEntry)en.nextElement();
                if (tmp == entry) {
                    tmp.setDefaultDevice(true);
                } else {
                    tmp.setDefaultDevice(false);
                }
                Config.changeDeviceEntry(tmp);
            }
            SwingSelectDevicePanel.this.lsDevices.repaint();
            SwingSelectDevicePanel.this.btDefault.setEnabled(false);
        }
    };
    ListSelectionListener listSelectionListener = new ListSelectionListener(){

        public void valueChanged(ListSelectionEvent ev) {
            DeviceEntry entry = (DeviceEntry)SwingSelectDevicePanel.this.lsDevices.getSelectedValue();
            if (entry != null) {
                if (entry.isDefaultDevice()) {
                    SwingSelectDevicePanel.this.btDefault.setEnabled(false);
                } else {
                    SwingSelectDevicePanel.this.btDefault.setEnabled(true);
                }
                if (entry.canRemove()) {
                    SwingSelectDevicePanel.this.btRemove.setEnabled(true);
                } else {
                    SwingSelectDevicePanel.this.btRemove.setEnabled(false);
                }
                SwingSelectDevicePanel.this.btOk.setEnabled(true);
            } else {
                SwingSelectDevicePanel.this.btDefault.setEnabled(false);
                SwingSelectDevicePanel.this.btRemove.setEnabled(false);
                SwingSelectDevicePanel.this.btOk.setEnabled(false);
            }
        }
    };
    static /* synthetic */ Class class$org$microemu$device$j2se$J2SEDevice;

    public SwingSelectDevicePanel(EmulatorContext emulatorContext) {
        this.emulatorContext = emulatorContext;
        this.setLayout(new BorderLayout());
        this.setBorder(new TitledBorder(new EtchedBorder(), "Installed devices"));
        this.lsDevicesModel = new DefaultListModel();
        this.lsDevices = new JList(this.lsDevicesModel);
        this.lsDevices.setSelectionMode(0);
        this.lsDevices.addListSelectionListener(this.listSelectionListener);
        this.spDevices = new JScrollPane(this.lsDevices);
        this.add((Component)this.spDevices, "Center");
        JPanel panel = new JPanel();
        this.btAdd = new JButton("Add...");
        this.btAdd.addActionListener(this.btAddListener);
        this.btRemove = new JButton("Remove");
        this.btRemove.addActionListener(this.btRemoveListener);
        this.btDefault = new JButton("Set as default");
        this.btDefault.addActionListener(this.btDefaultListener);
        panel.add(this.btAdd);
        panel.add(this.btRemove);
        panel.add(this.btDefault);
        this.add((Component)panel, "South");
        Enumeration e = Config.getDeviceEntries().elements();
        while (e.hasMoreElements()) {
            DeviceEntry entry = (DeviceEntry)e.nextElement();
            this.lsDevicesModel.addElement(entry);
            if (!entry.isDefaultDevice()) continue;
            this.lsDevices.setSelectedValue(entry, true);
        }
    }

    public DeviceEntry getSelectedDeviceEntry() {
        return (DeviceEntry)this.lsDevices.getSelectedValue();
    }
}

