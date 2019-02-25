package com.dat3m.ui.editor;

import static java.lang.System.getProperty;
import static java.util.stream.Collectors.joining;
import static javax.swing.BorderFactory.createCompoundBorder;
import static javax.swing.BorderFactory.createEmptyBorder;
import static javax.swing.BorderFactory.createTitledBorder;
import static javax.swing.JFileChooser.APPROVE_OPTION;
import static javax.swing.JOptionPane.showMessageDialog;
import static javax.swing.border.TitledBorder.CENTER;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JEditorPane;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.dat3m.ui.Dat3M;

public class ProgramEditor extends JScrollPane implements IEditor {

	protected static final String PROGRAMLABEL = "Program";

	static List<String> extensions = new ArrayList<>();

	protected JMenu menu = null;
	protected static JEditorPane editor = new JEditorPane();

	protected int widht = 700;

	private String loadedFormat;

	public ProgramEditor(JMenu menu) {
		super(editor);

		this.menu = menu;
		this.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		this.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        this.setPreferredSize(new Dimension(widht / 3, 100));
        TitledBorder titledBorder = createTitledBorder(PROGRAMLABEL);
        titledBorder.setTitleJustification(CENTER);
		this.setBorder(createCompoundBorder(titledBorder, createEmptyBorder(5,5,5,5)));

        extensions.add("litmus");
        extensions.add("pts");
	    
	    createImporter();
	}

	@Override
	public void createImporter() {

		JMenuItem openItem = new JMenuItem(PROGRAMLABEL);
		menu.add(openItem);

		chooser.setCurrentDirectory(new File(getProperty("user.dir") + "/.."));
		for(String ext : extensions) {
			chooser.addChoosableFileFilter(new FileNameExtensionFilter("*." + ext, ext));			
		}
				
		openItem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent event){
				int r = chooser.showOpenDialog(null);
				if(r == APPROVE_OPTION){
					String path = chooser.getSelectedFile().getPath();
					loadedFormat = path.substring(path.lastIndexOf('.') + 1).trim();
					if(!extensions.contains(loadedFormat)) {
						showMessageDialog(null, "Please select a *." + extensions.stream().collect(joining(", *.")) + " file", "About", JOptionPane.INFORMATION_MESSAGE, Dat3M.dat3mIcon);	
						return;
					}
					try {
						editor.read(new InputStreamReader(new FileInputStream(path)), null);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		});
	}

	@Override
	public JEditorPane getEditor() {
		return editor;
	}
}