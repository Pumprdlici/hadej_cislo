package fi.javasom.gui;
//
//  Main panel for the Clusoe GUI.
//
//  Copyright (C) 2001-2004  Tomi Suuronen
//
//  This program is free software; you can redistribute it and/or modify
//  it under the terms of the GNU General Public License as published by
//  the Free Software Foundation; either version 2 of the License, or
//  (at your option) any later version.
//
//  This program is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU General Public License for more details.
//
//  You should have received a copy of the GNU General Public License
//  along with this program; if not, write to the Free Software
//  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
//

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;

import org.apache.log4j.Logger;
import org.apache.xerces.parsers.SAXParser;
import org.xml.sax.InputSource;

import fi.javasom.application.JSom;
import fi.javasom.application.JSomProgressListener;

//import java.io.System;

/**
 * Main panel for the Clusoe GUI.
 * <p>
 * Copyright (C) 2001-2004  Tomi Suuronen
 * <p>
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * @version 1.1
 * @since 1.0
 * @author Tomi Suuronen
*/
public class MainPanel extends JPanel implements ChangeListener {
    
	private ExecutePanel execute;
	private InstructionsPanel instructions;
	private JFrame parent;
	private PdfSettingsDialog pdfSettingsDialog;
	private Dimension screenSize;
	private JTabbedPane tabs;
	private boolean instructionSettings; //for checking if instructions values are accepted.
	private StringBuffer row; //for instruction information gathering.
	private StringBuffer area; //for notifying information gathering steps.
    private InputFileFilter filter;
    private boolean restore;  //marker for telling if restoring instructions.
	private String javasomDir;  //folder where javasom is run.
	
	/* Tab elements */
	private JTextArea textArea;
	private JTextField inputTF;
	private JTextField outputTF;
	private JTextField outputNameTF;
	private JCheckBox xmlCB;
	private JCheckBox svgCB;
	private JCheckBox pdfCB;
	private JFileChooser openFile;
	private JFileChooser saveFolder;
    private JFileChooser settingsFile;
    private JFileChooser restoreFile;
	private ButtonGroup latticeBG;
	private JRadioButton hexaRB;
	private JRadioButton rectRB;
	private ButtonGroup neighborBG;
	private JRadioButton stepRB;
	private JRadioButton gaussianRB;
	private JCheckBox normCB;
	private JTextField xDimTF;
	private JTextField yDimTF;
	private JTextField stepsTF;
	private JTextField lrateTF;
	private JTextField radiusTF;
	private JLabel totalL;
	private JLabel currentL;
	private ButtonGroup lrateTypeBG;
	private JRadioButton exponentialRB;
	private JRadioButton linearRB;
	private JRadioButton inverseRB;

	/* ordering elements*/
	private Vector steps;
	private Vector lrate;
	private Vector radius;
	private Vector lRateType;
	private int currentCounter;
	private int totalCounter;

	/* pdf paper settings */
	private JComboBox paperCB;
	private ButtonGroup pdfBG;
	private JRadioButton portraitRB;
	private JRadioButton landscapeRB;
	private JLabel textL;

	/*
	 * Main constructor.
	*/
	public MainPanel(JFrame parent, Dimension screenSize) {
		this.parent = parent;
		this.screenSize = screenSize;
		javasomDir = System.getProperty("user.dir");
		setLayout(new BorderLayout());
		tabs = new JTabbedPane();
		execute = new ExecutePanel();
		instructions = new InstructionsPanel();
        filter = new InputFileFilter();
        restore = false;
        
		tabs.addTab("Instructions", instructions);
		tabs.addTab("Execute", execute);
		add(tabs, "Center");

		// pdf paper settings
		paperCB = new JComboBox();
		paperCB.setEditable(false);
		paperCB.addItem("A4");
		paperCB.addItem("Letter");

		portraitRB = new JRadioButton("Portrait", true);
		landscapeRB = new JRadioButton("Landscape", false);

		// Focus listeners
		tabs.addChangeListener(this);
        
        //Initializes restore JFileChooser
        restoreFile = new JFileChooser();
        restoreFile.setDialogTitle("Restore settings");
        restoreFile.setFileFilter(filter);
        restoreFile.setFileSelectionMode(JFileChooser.FILES_ONLY);
        //Initializes settings JFileChooser
        settingsFile = new JFileChooser();
        settingsFile.setDialogTitle("Save settings");
        settingsFile.setFileFilter(filter);
        settingsFile.setFileSelectionMode(JFileChooser.FILES_ONLY);
        //Initializes PDF settingd Dialog
        pdfSettingsDialog = new PdfSettingsDialog(parent, screenSize);
	}

	/*
	 * activates the PDF settings dialog.
	*/
	public void activatePdfSettings() {
		if (pdfSettingsDialog == null) {
			pdfSettingsDialog = new PdfSettingsDialog(parent, screenSize);
		}
        if (!restore) {
		  pdfSettingsDialog.setVisible(true);
        }
	}

	/*
	 * Resets all the fields in the GUI.
	*/
	public void clearAllFields() {
		inputTF.setText("");
		xDimTF.setText("");
		yDimTF.setText("");
		stepsTF.setText("");
		lrateTF.setText("");
		radiusTF.setText("");
		outputTF.setText("");
		normCB.setSelected(true);
		hexaRB.setSelected(true);
		stepRB.setSelected(true);
		exponentialRB.setSelected(true);
		xmlCB.setSelected(false);
		svgCB.setSelected(false);
		pdfCB.setSelected(false);
		steps.removeAllElements();
		lrate.removeAllElements();
		radius.removeAllElements();
		lRateType.removeAllElements();
		steps.addElement("");
		lrate.addElement("");
		radius.addElement("");
		lRateType.addElement("exponential");
		totalL.setText("1");
		currentL.setText("1");
		currentCounter = 1;
		totalCounter = 1;
		if (openFile != null) {
            openFile.setSelectedFile(null);
		}
		if (saveFolder != null) {
			saveFolder.setSelectedFile(null);
		}
		if (pdfSettingsDialog != null) {
			textL.setText("(297mm x 210mm)");
			portraitRB.setSelected(true);
			paperCB.setSelectedIndex(0);
		}
		outputNameTF.setText("");
        execute.clear();
	}
    
    /*
	 * Restores the settings from a file.
	*/
	public void restoreSettings() {
        
        if (restoreFile.getSelectedFile() == null) {
            restoreFile.setSelectedFile(new File(System.getProperty("user.dir")+"/conf/*.xml"));
        }
        
        int returnVal = restoreFile.showDialog(parent, "Select");
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            if (restoreFile.getSelectedFile().getName().endsWith(".xml")) {
                restore = true;
                //parsing the settings file
                SAXParser parser = new org.apache.xerces.parsers.SAXParser();
		        SettingsHandler handler = new SettingsHandler();
                parser.setContentHandler(handler);
		        parser.setErrorHandler(handler);
		        try {
                    parser.parse(new InputSource(new BufferedReader(
                            new FileReader(restoreFile.getSelectedFile()))));
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
                //initialize openFile file chooser
                if (openFile == null) {
					openFile = new JFileChooser();
					openFile.setDialogTitle("Select a data file");
					openFile.setFileFilter(filter);
					openFile.setFileSelectionMode(JFileChooser.FILES_ONLY);
                    openFile.setSelectedFile(new File(System.getProperty("user.dir") + "/conf"));
				}
                //initialize saveFolder file chooser
                if (saveFolder == null) {
                    saveFolder = new JFileChooser();
				    saveFolder.setDialogTitle("Select an output folder");
				    saveFolder.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                }
                //placing the values
                openFile.setSelectedFile(handler.getInputFile());
                if (openFile.getSelectedFile() != null) {
                    inputTF.setText(openFile.getSelectedFile().getName());
                } else {
                    inputTF.setText("");
                }
                normCB.setSelected(handler.getNormalization());
                xDimTF.setText("" + handler.getXDim());
		        yDimTF.setText("" + handler.getYDim());
                if (handler.getLattice().equals("hexa")) {
                    hexaRB.setSelected(true);
                } else {
                    rectRB.setSelected(true);
                }
                if (handler.getNeighbourhood().equals("step")) {
                    stepRB.setSelected(true);
                } else {
                    gaussianRB.setSelected(true);
                }
                xmlCB.setSelected(handler.getXml());
                svgCB.setSelected(handler.getSvg());
                saveFolder.setSelectedFile(handler.getSaveFolder());
                if (saveFolder.getSelectedFile() != null) {
                    outputNameTF.setText(handler.getOutputFileName());
                    outputTF.setText(saveFolder.getSelectedFile().getAbsolutePath());
                } else {
                    outputNameTF.setText("");
                    outputTF.setText("");
                }
                if (pdfSettingsDialog != null) {
                    textL.setText("(297mm x 210mm)");
                    if (handler.getOrientation().equals("portrait")) {
                        portraitRB.setSelected(true);
                        if (handler.getPaper().equals("a4")) {
                            paperCB.setSelectedIndex(0);
                            textL.setText("(297mm x 210mm)");
				        } else {//Letter
                            paperCB.setSelectedIndex(1);
					        textL.setText("(279mm x 216mm)");
                        }
                    } else {
                        landscapeRB.setSelected(true);
                        if (handler.getPaper().equals("a4")) {
                            paperCB.setSelectedIndex(0);
                            textL.setText("(210mm x 297mm)");
				        } else {//Letter
                            paperCB.setSelectedIndex(1);
					        textL.setText("(216mm x 279mm)");
                        }
                    }                  
                }
                steps = handler.getSteps();
                lrate = handler.getLearningRates();
                radius = handler.getRadiuses();
                lRateType = handler.getLearningRateTypes();
                totalL.setText("" + steps.size());
                currentL.setText("1");
                currentCounter = 1;
                totalCounter = steps.size();
                stepsTF.setText(steps.elementAt(0).toString());
                lrateTF.setText(lrate.elementAt(0).toString());
                radiusTF.setText(radius.elementAt(0).toString());
                pdfCB.setSelected(handler.getPdf());
                restore = false;
            }
        }
    }
    
    /*
	 * Saves the current settings into a file.
	*/
	public void saveSettings() {
        // saves the current training view
		String button = "";
		steps.setElementAt(stepsTF.getText(), currentCounter - 1);
		lrate.setElementAt(lrateTF.getText(), currentCounter - 1);
		radius.setElementAt(radiusTF.getText(), currentCounter - 1);
		if (exponentialRB.isSelected()) {
			button = "exponential";
		} else if(linearRB.isSelected()) {
			button = "linear";
		} else {//inverse
		 	button = "inverse";
		}
		lRateType.setElementAt(button, currentCounter - 1);

		// some variables
		int number;
		float number2;
        StringBuffer row = new StringBuffer("");
        
        // Starts checking and construction of the instructions string
		row.append("<?xml version='1.0' encoding='UTF-8' ?>");
		row.append("<!DOCTYPE instructions [");
		try {
        	BufferedReader dtd = new BufferedReader(new FileReader(javasomDir+"/conf/instructions.dtd"));
        	String cache = "";
        	while(cache != null) {
        		row.append(cache);
        		cache = dtd.readLine();
        	}
        	dtd.close();
        } catch (Exception e) {}
        row.append("]>");
		row.append("<instructions>");
		if (openFile == null || openFile.getSelectedFile() == null) {
            row.append("<input><file></file></input>");
		} else {
		    row.append("<input><file>");
            row.append(openFile.getSelectedFile().getAbsolutePath());
            row.append("</file></input>");
		}
		row.append("<initialization>");
		row.append("<normalization used='"+normCB.isSelected()+"' />");
		try {
			number = Integer.valueOf(xDimTF.getText().trim()).intValue();
			row.append("<x.dimension>"+number+"</x.dimension>");
		} catch(Exception e1) {
            row.append("<x.dimension>NaN</x.dimension>");
		}
		try {
			number = Integer.valueOf(yDimTF.getText().trim()).intValue();
			row.append("<y.dimension>"+number+"</y.dimension>");
		} catch(Exception e2) {
            row.append("<y.dimension>NaN</y.dimension>");
		}
		if (hexaRB.isSelected())	{
			row.append("<lattice type='hexagonal' />");
		} else {//rectangular
			row.append("<lattice type='rectangular' />");
		}
		if (stepRB.isSelected()) {
			row.append("<neighbourhood type='step' />");
		} else {//gaussian
			row.append("<neighbourhood type='gaussian' />");
		}
			row.append("</initialization>");

		//training
		for (int j = 0; j < totalCounter; j++) {
			row.append("<training>");
            
            //steps
			try	{
				number = Integer.valueOf(steps.elementAt(j).toString().trim()).intValue();
				row.append("<steps>"+number+"</steps>");
			} catch(Exception e3) {
                row.append("<steps>NaN</steps>");
			}
			
            //learning-rate
			try {
				number2 = Float.valueOf(lrate.elementAt(j).toString().trim()).floatValue();
				row.append("<lrate type='"+lRateType.elementAt(j).toString().trim()+"'>"+number2+"</lrate>");
			} catch(Exception e4) {
                row.append("<lrate type='"+lRateType.elementAt(j).toString().trim()+"'>NaN</lrate>");
			}

			//radius
			try	{
				number = Integer.valueOf(radius.elementAt(j).toString().trim()).intValue();
				row.append("<radius>"+number+"</radius>");
			} catch(Exception e5) {
                    row.append("<radius>NaN</radius>");
			}

			row.append("</training>");
		}

		//paper format
		row.append("<output ");
		if (((String)paperCB.getSelectedItem()).equals("A4")) {
			if (portraitRB.isSelected()) {
				row.append("paper='a4' orientation='portrait'>");
			} else {//landscape
				row.append("paper='a4' orientation='landscape'>");
			}
		} else {//Letter
			if (portraitRB.isSelected()) {
				row.append("paper='letter' orientation='portrait'>");
			} else {//landscape
				row.append("paper='letter' orientation='landscape'>");
			}
		}

		//folder
		if (saveFolder == null || saveFolder.getSelectedFile() == null)	{
            row.append("<folder></folder>");
		} else {
			row.append("<folder>"+saveFolder.getSelectedFile().getAbsolutePath()+"</folder>");
		}
		
		//identifier
		if (outputNameTF.getText().trim().equals("") || outputNameTF.getText() == null) {
            row.append("<identifier></identifier>");
		} else {
			row.append("<identifier>"+outputNameTF.getText().trim()+"</identifier>");
		}

		//type
		if (!xmlCB.isSelected() && !svgCB.isSelected() && !pdfCB.isSelected()) {
			row.append("<type format='xml' />");
		} else {
			if (xmlCB.isSelected()) {
				row.append("<type format='xml' />");
			}
			if (svgCB.isSelected()) {
				row.append("<type format='svg' />");
			}
			if (pdfCB.isSelected()) {
				row.append("<type format='pdf' />");
			}
		}
		row.append("</output>");
		row.append("</instructions>");
        
        if (settingsFile.getSelectedFile() == null) {
            settingsFile.setSelectedFile(new File(System.getProperty("user.dir") + "/conf/*.xml"));
        }
        
        int returnVal = settingsFile.showDialog(parent, "Save");
        if(returnVal == JFileChooser.APPROVE_OPTION) {
            if (settingsFile.getSelectedFile().getName().endsWith(".xml")) {
                //save the settings (instructions)
                try {
                    FileWriter intoFile = new FileWriter(settingsFile.getSelectedFile());
                    intoFile.write(row.toString());
                    intoFile.flush();
                } catch (IOException ioe) {
                    System.out.println(ioe.getMessage());
                }    
		  }
        }
    }
    
	/**
	 * Selected Tab has changed.
	*/
	public void stateChanged(ChangeEvent ce) {
		if (execute == tabs.getSelectedComponent()) {
			/* Clears the field */
			textArea.setText("");
			area = new StringBuffer("");
			row = new StringBuffer("");
			String cache = "";
			instructionSettings = false;
            execute.clear();
            
			/* saves the current training view */
			String button = "";
			steps.setElementAt(stepsTF.getText(), currentCounter - 1);
			lrate.setElementAt(lrateTF.getText(), currentCounter - 1);
			radius.setElementAt(radiusTF.getText(), currentCounter - 1);
			if (exponentialRB.isSelected()) {
				button = "exponential";
			} else if(linearRB.isSelected()) {
				button = "linear";
			} else {//inverse
			 	button = "inverse";
			}
			lRateType.setElementAt(button, currentCounter - 1);

			/* some variables*/
			int number;
			float number2;

			/* Starts checking and construction of the instructions string and runs the JSOM */
			run_it:
			do {
				row.append("<?xml version='1.0' encoding='UTF-8' ?>");
				row.append("<!DOCTYPE instructions SYSTEM 'file:///");
                row.append(System.getProperty("user.dir"));
                row.append("/conf/instructions.dtd'>");
				row.append("<instructions>");
				if (openFile == null || openFile.getSelectedFile() == null) {
					textArea.setText("No input file selected. Construction not possible!");
					break;
				} else {
					area.append("Input data file: "+openFile.getSelectedFile().getAbsolutePath()+" \n");
					textArea.setText(area.toString());
				}
				row.append("<input><file>"+openFile.getSelectedFile().getAbsolutePath()+"</file></input>");
				row.append("<initialization>");
				row.append("<normalization used='"+normCB.isSelected()+"' />");
				if (normCB.isSelected()) {
					area.append("Normalization: yes \n");
					textArea.setText(area.toString());
				} else {
					area.append("Normalization: no \n");
					textArea.setText(area.toString());
				}
				try {
					number = Integer.valueOf(xDimTF.getText().trim()).intValue();
					row.append("<x.dimension>"+number+"</x.dimension>");
					area.append("X-dimension: "+number+" nodes \n");
					textArea.setText(area.toString());
				} catch(Exception e1) {
					area.append("X-dimension value is not a number. Construction not possible!");
					textArea.setText(area.toString());
					break;
				}
				try {
					number = Integer.valueOf(yDimTF.getText().trim()).intValue();
					row.append("<y.dimension>"+number+"</y.dimension>");
					area.append("Y-dimension: "+number+" nodes \n");
					textArea.setText(area.toString());
				} catch(Exception e2) {
					area.append("Y-dimension value is not a number. Construction not possible!");
					textArea.setText(area.toString());
					break;
				}
				if (hexaRB.isSelected())	{
					row.append("<lattice type='hexagonal' />");
					area.append("Lattice: Hexagonal \n");
					textArea.setText(area.toString());
				} else {//rectangular
					row.append("<lattice type='rectangular' />");
					area.append("Lattice: Rectangular \n");
					textArea.setText(area.toString());
				}
				if (stepRB.isSelected()) {
					row.append("<neighbourhood type='step' />");
					area.append("Neighbourhood: Step (bubble) \n");
					textArea.setText(area.toString());
				} else {//gaussian
					row.append("<neighbourhood type='gaussian' />");
					area.append("Neighbourhood: Gaussian \n");
					textArea.setText(area.toString());
				}
				row.append("</initialization>");

				//training
				for (int j = 0; j < totalCounter; j++) {
					row.append("<training>");
					area.append("Training set: "+(j+1)+" \n");
					textArea.setText(area.toString());

					//steps
					try	{
						number = Integer.valueOf(steps.elementAt(j).toString().trim()).intValue();
						row.append("<steps>"+number+"</steps>");
						area.append("   Steps: "+number+" \n");
						textArea.setText(area.toString());
					} catch(Exception e3) {
						area.append("   Steps value is not a number. Construction not possible!");
						textArea.setText(area.toString());
						break run_it;
					}

					//learning-rate
					try {
						number2 = Float.valueOf(lrate.elementAt(j).toString().trim()).floatValue();
						row.append("<lrate type='"+lRateType.elementAt(j).toString().trim()+"'>"+number2+"</lrate>");
						area.append("   Learning-rate: "+number2+" \n");
						if (lRateType.elementAt(j).toString().trim().equals("exponential")) {
							area.append("   Learning-rate type: Exponential \n");
						} else if(lRateType.elementAt(j).toString().trim().equals("linear")) {
							area.append("   Learning-rate type: Linear \n");
						} else {
							area.append("   Learning-rate type: Inverse-time \n");
						}
						textArea.setText(area.toString());
					} catch(Exception e4) {
						area.append("   Learning-rate value is not a number. Construction not possible!");
						textArea.setText(area.toString());
						break run_it;
					}

					//radius
					try	{
						number = Integer.valueOf(radius.elementAt(j).toString().trim()).intValue();
						row.append("<radius>"+number+"</radius>");
						area.append("   Radius: "+number+" \n");
						textArea.setText(area.toString());
					} catch(Exception e5) {
						area.append("   Radius value is not a number. Construction not possible!");
						textArea.setText(area.toString());
						break run_it;
					}

					row.append("</training>");
				}

				//paper format
				row.append("<output ");
				if (((String)paperCB.getSelectedItem()).equals("A4")) {
					if (portraitRB.isSelected()) {
						row.append("paper='a4' orientation='portrait'>");
						cache = "(A4 and Portrait)";
					} else {//landscape
						row.append("paper='a4' orientation='landscape'>");
						cache = "(A4 and Landscape)";
					}
				} else {//Letter
					if (portraitRB.isSelected()) {
						row.append("paper='letter' orientation='portrait'>");
						cache = "(Letter and Portrait)";
					} else {//landscape
						row.append("paper='letter' orientation='landscape'>");
						cache = "(Letter and Landscape)";
					}
				}

				//folder
				if (saveFolder == null || saveFolder.getSelectedFile() == null)	{
					area.append("No output folder selected. Construction not possible!");
					textArea.setText(area.toString());
					break;
				} else {
					area.append("Output folder: "+saveFolder.getSelectedFile().getAbsolutePath() +" \n");
					textArea.setText(area.toString());
				}
				row.append("<folder>"+saveFolder.getSelectedFile().getAbsolutePath()+"</folder>");

				//identifier
				if (outputNameTF.getText().trim().equals("") || outputNameTF.getText() == null) {
					area.append("No identifier set. Construction not possible!");
					textArea.setText(area.toString());
					break;
				} else {
					row.append("<identifier>"+outputNameTF.getText().trim()+"</identifier>");
					area.append("Identifier: "+outputNameTF.getText().trim()+" \n");
					textArea.setText(area.toString());
				}

				//type
				if (!xmlCB.isSelected() && !svgCB.isSelected() && !pdfCB.isSelected()) {
					area.append("No output format selected. Construction not possible!");
					textArea.setText(area.toString());
					break;
				} else {
					if (xmlCB.isSelected()) {
						row.append("<type format='xml' />");
						area.append("Output: "+outputNameTF.getText().trim()+".xml \n");
						textArea.setText(area.toString());
					}
					if (svgCB.isSelected()) {
						row.append("<type format='svg' />");
						area.append("Output: "+outputNameTF.getText().trim()+".svg \n");
						textArea.setText(area.toString());
					}
					if (pdfCB.isSelected()) {
						row.append("<type format='pdf' />");
						area.append("Output: "+outputNameTF.getText().trim()+".pdf "+cache+" \n");
						textArea.setText(area.toString());
					}
				}
				row.append("</output>");
				row.append("</instructions>");
				area.append("\n");
				area.append("Instruction values were accepted. Ready to construct a map \n");
				textArea.setText(area.toString());
				instructionSettings = true;
				break;
			}
			while(true);
		}
	}

	/**************************************************************************/

	/*
	 * Instructions Panel.
	*/
	private class InstructionsPanel extends JPanel implements ActionListener, ItemListener {
		
        private JButton inputBrowse;
		private JButton outputBrowse;
		private JButton prevB;
		private JButton nextB;
		private JButton addB;
		private JButton delB;
		private InputFileFilter filter;

		public InstructionsPanel() {
			setLayout(new BorderLayout());
			JPanel center = new JPanel();
			center.setLayout(new BorderLayout());
			add(center,"Center");
			Border etched = BorderFactory.createEtchedBorder();
			filter = new InputFileFilter();
			currentCounter = 1;
			totalCounter = 1;
			steps = new Vector(0, 1);
			lrate = new Vector(0, 1);
			radius = new Vector(0, 1);
			lRateType = new Vector(0, 1);
			steps.addElement("");
			lrate.addElement("");
			radius.addElement("");

			//input file
			JPanel iPanel = new JPanel();
			iPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
			Border inputTitled = BorderFactory.createTitledBorder(etched, "Input data");
			iPanel.setBorder(inputTitled);
			iPanel.add(new JLabel("File: "));
			inputTF = new JTextField(32);
			inputTF.setEditable(false);
			iPanel.add(inputTF);
			inputBrowse = new JButton("Browse");
			iPanel.add(inputBrowse);
			add(iPanel, "North");

			//initialization
			JPanel iniPanel = new JPanel();
			iniPanel.setLayout(new BorderLayout());
			center.add(iniPanel, "North");
			Border iniTitled = BorderFactory.createTitledBorder(etched, "Initialisation");
			iniPanel.setBorder(iniTitled);

			JPanel ini2Panel = new JPanel();
			ini2Panel.setLayout(new BorderLayout());
			iniPanel.add(ini2Panel, "Center");

			JPanel ini3Panel = new JPanel();
			ini3Panel.setLayout(new BorderLayout());
			iniPanel.add(ini3Panel, "South");

			//dimension + normalization
			JPanel dimPanel = new JPanel();
			dimPanel.setLayout(new BorderLayout());
			JPanel normPanel = new JPanel();
			normPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
			normCB = new JCheckBox("Use normalization",true);
			normPanel.add(normCB);
			JPanel xdimPanel = new JPanel();
			xdimPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
			xDimTF = new JTextField(5);
			xdimPanel.add(new JLabel("X-dimension: "));
			xdimPanel.add(xDimTF);
			xdimPanel.add(new JLabel(" (nodes)"));
			JPanel ydimPanel = new JPanel();
			ydimPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
			yDimTF = new JTextField(5);
			ydimPanel.add(new JLabel("Y-dimension: "));
			ydimPanel.add(yDimTF);
			ydimPanel.add(new JLabel(" (nodes)"));
			dimPanel.add(normPanel, "North");
			dimPanel.add(xdimPanel, "Center");
			dimPanel.add(ydimPanel, "South");
			ini3Panel.add(dimPanel, "North");

			//lattice
			JPanel laPanel = new JPanel();
			laPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
			latticeBG = new ButtonGroup();
			laPanel.add(new JLabel("Lattice type: "));
			hexaRB = new JRadioButton("Hexagonal", true);
			rectRB = new JRadioButton("Rectangular", false);
			latticeBG.add(hexaRB);
			latticeBG.add(rectRB);
			laPanel.add(hexaRB);
			laPanel.add(rectRB);
			ini3Panel.add(laPanel, "Center");

			//neighbourhood
			JPanel nePanel = new JPanel();
			nePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
			neighborBG = new ButtonGroup();
			nePanel.add(new JLabel("Neighbourhood function: "));
			stepRB = new JRadioButton("Step (bubble)", true);
			gaussianRB = new JRadioButton("Gaussian", false);
			neighborBG.add(stepRB);
			neighborBG.add(gaussianRB);
			nePanel.add(stepRB);
			nePanel.add(gaussianRB);
			ini3Panel.add(nePanel, "South");

			//ordering
			JPanel orderPanel = new JPanel();
			orderPanel.setLayout(new BorderLayout());
			JPanel leftoPanel = new JPanel();
			leftoPanel.setLayout(new GridLayout(3, 1));
			JPanel rightoPanel = new JPanel();
			rightoPanel.setLayout(new GridLayout(3, 1));
			Border orderTitled = BorderFactory.createTitledBorder(etched, "Training sets");
			orderPanel.setBorder(orderTitled);

			//steps
			JPanel stepsPanel = new JPanel();
			stepsPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
			stepsTF = new JTextField(5);
			stepsPanel.add(stepsTF);
			stepsPanel.add(new JLabel("Steps"));

			//lrate
			JPanel lratePanel = new JPanel();
			lratePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
			lrateTF = new JTextField(5);
			lratePanel.add(lrateTF);
			lratePanel.add(new JLabel("Learning-rate"));

			//radius
			JPanel radiusPanel = new JPanel();
			radiusPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
			radiusTF = new JTextField(5);
			radiusPanel.add(radiusTF);
			radiusPanel.add(new JLabel("Radius"));

			//learning-rate function type
			JPanel lrateTypePanel = new JPanel();
			lrateTypePanel.setLayout(new FlowLayout(FlowLayout.CENTER));
			lrateTypeBG = new ButtonGroup();
			exponentialRB = new JRadioButton("Exponential", true);
			linearRB = new JRadioButton("Linear", false);
			inverseRB = new JRadioButton("Inverse time", false);
			lrateTypeBG.add(exponentialRB);
			lrateTypeBG.add(linearRB);
			lrateTypeBG.add(inverseRB);
			lrateTypePanel.add(exponentialRB);
			lrateTypePanel.add(linearRB);
			lrateTypePanel.add(inverseRB);

			lRateType.addElement(lrateTypeBG.getSelection().getActionCommand());

			//buttons
			JPanel buttonsPanel = new JPanel();
			buttonsPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
			prevB = new JButton("<");
			nextB = new JButton(">");
			addB = new JButton("+");
			delB = new JButton("-");
			currentL = new JLabel("1");
			totalL = new JLabel("1");
			buttonsPanel.add(currentL);
			buttonsPanel.add(new JLabel("/"));
			buttonsPanel.add(totalL);
			buttonsPanel.add(new JLabel(" "));
			buttonsPanel.add(prevB);
			buttonsPanel.add(addB);
			buttonsPanel.add(delB);
			buttonsPanel.add(nextB);

			leftoPanel.add(stepsPanel);
			leftoPanel.add(lratePanel);
			leftoPanel.add(radiusPanel);
			rightoPanel.add(new JLabel(" "));
			rightoPanel.add(lrateTypePanel);
			rightoPanel.add(buttonsPanel);
			orderPanel.add(leftoPanel, "West");
			orderPanel.add(rightoPanel, "Center");
			center.add(orderPanel, "Center");

			//output filename
			JPanel oNamePanel = new JPanel();
			oNamePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
			oNamePanel.add(new JLabel("Identifier: "));
			outputNameTF = new JTextField(15);
			oNamePanel.add(outputNameTF);

			//ouput file
			JPanel oPanel = new JPanel();
			oPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
			oPanel.add(new JLabel("Folder: "));
			outputTF = new JTextField(30);
			outputTF.setEditable(false);
			oPanel.add(outputTF);
			outputBrowse = new JButton("Browse");
			oPanel.add(outputBrowse);

			//output type
			JPanel oTypePanel = new JPanel();
			xmlCB = new JCheckBox("XML", false);
			svgCB = new JCheckBox("SVG", false);
			pdfCB = new JCheckBox("PDF", false);
			oTypePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
			oTypePanel.add(new JLabel("Type: "));
			oTypePanel.add(xmlCB);
			oTypePanel.add(svgCB);
			oTypePanel.add(pdfCB);

			//output
			JPanel outputPanel = new JPanel();
			outputPanel.setLayout(new BorderLayout());
			outputPanel.add(oNamePanel, "North");
			outputPanel.add(oPanel, "Center");
			outputPanel.add(oTypePanel, "South");
			Border outputTitled = BorderFactory.createTitledBorder(etched, "Output");
			outputPanel.setBorder(outputTitled);
			add(outputPanel, "South");

			//action listener
			inputBrowse.addActionListener(this);
			outputBrowse.addActionListener(this);
			prevB.addActionListener(this);
			nextB.addActionListener(this);
			addB.addActionListener(this);
			delB.addActionListener(this);
			pdfCB.addItemListener(this);
		}

		public void itemStateChanged(ItemEvent ie) {
			Object cache = ie.getSource();
			if (pdfCB == cache) {
				if (pdfCB.isSelected()) {
					//popup the pdf settings dialog
					activatePdfSettings();
				}
			}
		}

		/*
		 * Action resolver.
		*/
		public void actionPerformed(ActionEvent e) {
			Object cache = e.getSource();

			if (inputBrowse == cache) {
				//open input browse window
				if (openFile == null) {
					openFile = new JFileChooser();
					openFile.setDialogTitle("Select a data file");
					openFile.setFileFilter(filter);
					openFile.setFileSelectionMode(JFileChooser.FILES_ONLY);
                    openFile.setSelectedFile(new File(System.getProperty("user.dir") + "/conf"));
				}
				int returnVal = openFile.showDialog(parent, "Select");
				
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    if (!openFile.getSelectedFile().getName().endsWith(".xml")) {
						inputTF.setText("");
						openFile.setSelectedFile(null);
					} else {
						inputTF.setText(openFile.getSelectedFile().getName());
                        outputTF.setText(openFile.getSelectedFile().getParent());
                        if (saveFolder == null) {
                            saveFolder = new JFileChooser();
                            saveFolder.setDialogTitle("Select an output folder");
                            saveFolder.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                            saveFolder.setSelectedFile(new File(openFile.getSelectedFile().getParent()));
                        } else if (saveFolder.getSelectedFile() == null) {
                            saveFolder.setSelectedFile(new File(openFile.getSelectedFile().getParent()));
                        }
                        
					}
                }
			}
            
			if (outputBrowse == cache) {
				//open output browse window
				if (saveFolder == null) {
					saveFolder = new JFileChooser();
					saveFolder.setDialogTitle("Select an output folder");
					saveFolder.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				}
				int returnVal = saveFolder.showDialog(parent, "Select");
				
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    outputTF.setText(saveFolder.getSelectedFile().getAbsolutePath());
				}
			}
            
			if (addB == cache) {
				totalCounter++;
				totalL.setText("" + totalCounter);
				save();
				steps.setSize(totalCounter);
				lrate.setSize(totalCounter);
				radius.setSize(totalCounter);
				lRateType.setSize(totalCounter);
				steps.insertElementAt("", currentCounter);
				lrate.insertElementAt("", currentCounter);
				radius.insertElementAt("", currentCounter);
				lRateType.setElementAt("exponential", currentCounter);
				currentCounter++;
				present();
			}
            
			if (delB == cache) {
				if (totalCounter >= 2) {
					totalCounter--;
					totalL.setText("" + totalCounter);
					steps.remove(currentCounter - 1);
					lrate.remove(currentCounter - 1);
					radius.remove(currentCounter - 1);
					lRateType.remove(currentCounter - 1);
					steps.setSize(totalCounter);
					lrate.setSize(totalCounter);
					radius.setSize(totalCounter);
					lRateType.setSize(totalCounter);
					if (currentCounter == (totalCounter + 1)) {
						currentCounter--;
					}
					present();
				}
			}
            
			if (nextB == cache) {
				if (currentCounter < totalCounter) {
					save();
					currentCounter++;
					present();
				}
			}
            
			if (prevB == cache) {
				if (currentCounter > 1) {
					save();
					currentCounter--;
					present();
				}
			}
		}

		/*
		 * Saves the current values into Vectors.
		*/
		private void save() {
			String button = "";
			steps.setElementAt(stepsTF.getText(), currentCounter - 1);
			lrate.setElementAt(lrateTF.getText(), currentCounter - 1);
			radius.setElementAt(radiusTF.getText(), currentCounter - 1);
			if (exponentialRB.isSelected()) {
				button = "exponential";
			} else if (linearRB.isSelected()) {
				button = "linear";
			} else {//inverse
			 	button = "inverse";
			}
			lRateType.setElementAt(button, currentCounter - 1);
		}

		/*
		 * Presents the next or previous values.
		*/
		private void present() {
			String button = lRateType.elementAt(currentCounter - 1).toString();
			stepsTF.setText(steps.elementAt(currentCounter - 1).toString());
			lrateTF.setText(lrate.elementAt(currentCounter - 1).toString());
			radiusTF.setText(radius.elementAt(currentCounter - 1).toString());
			if (button.equals("exponential")) {
				exponentialRB.setSelected(true);
			} else if (button.equals("linear")) {
				linearRB.setSelected(true);
			} else {//inverse
				inverseRB.setSelected(true);
			}

			currentL.setText("" + currentCounter);
		}
	}

	/**************************************************************************/

	/*
	 * Execute Panel.
	*/
	private class ExecutePanel extends JPanel implements ActionListener,
            JSomProgressListener {
		
		/** Log instance */
		private Logger _log = Logger.getLogger(ExecutePanel.class);
		
        private JProgressBar pMeter;
        private JButton pButton;
        private JLabel pLabel;
	    private Timer timer;
	    private int progress;
	    private volatile JSom sommi;

		/*
		 * Main constructor.
		*/
		public ExecutePanel() {
			setLayout(new BorderLayout());
			Border etched = BorderFactory.createEtchedBorder();
            JPanel sPanel = new JPanel();
            sPanel.setLayout(new GridLayout(1, 2));
            
			//proceed button
			JPanel bPanel = new JPanel();
			bPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
			pButton = new JButton("Proceed");
			bPanel.add(pButton, "North");
			sPanel.add(bPanel);
            
            //progress panel
			JPanel pPanel = new JPanel();
			pPanel.setLayout(new BorderLayout());
			pLabel = new JLabel("Progress:");
			pPanel.add(pLabel, "North");
			pMeter = new JProgressBar();
			pMeter.setMinimum(0);
			pMeter.setMaximum(100);
			pMeter.setStringPainted(true);
			pMeter.setString("0%");
			pPanel.add(pMeter, "West");
			sPanel.add(pPanel);
            add(sPanel,"South");
            
			//status text area
			textArea = new JTextArea("");
			JScrollPane tPanel = new JScrollPane(textArea);
			Border statusTitled = BorderFactory.createTitledBorder(etched, "Report");
			tPanel.setBorder(statusTitled);
			textArea.setEditable(false);
			textArea.setBackground(tPanel.getBackground());
			add(tPanel, "Center");

			//action listener
			pButton.addActionListener(this);
		}

		/*
		 * Action resolver.
		*/
		public void actionPerformed(ActionEvent e) {
			/* Proceed button */
			if (pButton == e.getSource()) {
				if (instructionSettings) {
					area.append("\n");
					area.append("Constructing the map, please wait! \n");
					textArea.setText(area.toString());

					/* Map creation */
				    ArrayList listeners = new ArrayList();
					listeners.add(this);
					timer = new Timer(1000, new ActionListener() {
					   public void actionPerformed(ActionEvent evt) {
					       pMeter.setValue(getProgress());
					       pMeter.setString(String.valueOf(getProgress()) + "%");
					       if (!sommi.isAlive()) {
					           Toolkit.getDefaultToolkit().beep();
					           endProcess();
                            }
						}
                    });
					timer.start();
					sommi = new JSom(row.toString(), listeners);
					pButton.setEnabled(false);
					sommi.start();
				}
			}
		}
        
        /*
         * Start batch.
        */
	    public void startBatch(String batchName) {
            progress = 0;
            pLabel.setText("Progress - " + batchName);
	    }

        /*
         * End batch.
        */
	    public void endBatch(String batchName) {
            progress = 100;
	    }

        /*
         * Progress of a batch.
        */
	    public void batchProgress(int count, int max) {
            progress = (count * 100/ max);
	    }

	    public void endProcess() {
            if(sommi.anyExceptions()) {
                area.append("\nAn error has occured! Construction aborted! \n");
                textArea.setText(area.toString());
            } else {
                area.append("\nMap creation succesful! \n");
                textArea.setText(area.toString());
                pLabel.setText("Progress: Complete");
            }
            pButton.setEnabled(true);
            timer.stop();
            sommi = null;
            
	    }
        
        /**
         * Returns the current progress.
         *
         * @return int - Progress.
        */
	    public int getProgress() {
            return progress;
        }
        
        /*
         * Clears all the fields.
        */
        public void clear() {
            textArea.setText("");
			pMeter.setValue(0);
            pMeter.setString("0%");
            pLabel.setText("Progress:");
        }
	}

	/**************************************************************************/

	/*
	 * FileFilter for Clusoe
	*/
	private class InputFileFilter extends FileFilter {

		/*
		 * Acception check.
		*/
		public boolean accept(File f) {
			if (f != null) {
                if (f.isDirectory()) {
					return true;
                }
                if(f.isFile()) {
					String extension = getExtension(f);
                    if(extension.equals("xml")) {
						return true;
                    }
                }
			}
			return false;
		}

		/*
		 * Returns human readable version of this filter.
		*/
		public String getDescription() {
			return "XML data Files (*.xml)";
		}

		/*
		 * Parses the extension from the file name.
		*/
		public String getExtension(File f) {
			if (f != null) {
				String filename = f.getName();
				int i = filename.lastIndexOf('.');
				if(i > 0 && i < (filename.length() - 1)) {
					return filename.substring(i+1).toLowerCase();
				}
			}
			return "";
        }
	}

	/**************************************************************************/

	/*
	 * PDF settings dialog.
	*/
	private class PdfSettingsDialog extends JDialog implements ActionListener {
		private JPanel mainP;
		private JButton closeB;

		/*
		 * Constructor.
		*/
		public PdfSettingsDialog(JFrame parent, Dimension screenSize) {
			super(parent,"PDF Paper Settings", true);
			setResizable(false);
			int width =275, height = 150;
			setSize(width, height);
			setLocation((screenSize.width-width) / 2, (screenSize.height-height) / 2);
			Border etched = BorderFactory.createEtchedBorder();

			//main panel
			mainP = new JPanel();
			mainP.setLayout(new GridLayout(1, 2));
			getContentPane().add(mainP, "Center");

			//paper
			JPanel paperP = new JPanel();
			paperP.setLayout(new GridLayout(2,1));
			Border paperTitled = BorderFactory.createTitledBorder(etched, "Size");
			paperP.setBorder(paperTitled);
			JPanel paperP2 = new JPanel();
			paperP2.setLayout(new FlowLayout(FlowLayout.LEFT));
			paperP2.add(paperCB);
			paperP.add(paperP2);
			textL = new JLabel("(297mm x 210 mm)");
			JPanel paperP3 = new JPanel();
			paperP3.setLayout(new FlowLayout(FlowLayout.LEFT));
			paperP3.add(textL);
			paperP.add(paperP3);
			mainP.add(paperP);

			//orientation
			JPanel orienP = new JPanel();
			orienP.setLayout(new GridLayout(2, 1));
			pdfBG = new ButtonGroup();
			pdfBG.add(portraitRB);
			pdfBG.add(landscapeRB);
			Border orienTitled = BorderFactory.createTitledBorder(etched, "Orientation");
			orienP.setBorder(orienTitled);
			JPanel orienP2 = new JPanel();
			orienP2.setLayout(new FlowLayout(FlowLayout.LEFT));
			orienP2.add(portraitRB);
			JPanel orienP3 = new JPanel();
			orienP3.setLayout(new FlowLayout(FlowLayout.LEFT));
			orienP3.add(landscapeRB);
			orienP.add(orienP2);
			orienP.add(orienP3);
			mainP.add(orienP);

			//close button panel
			JPanel buttonsP = new JPanel();
			buttonsP.setLayout(new FlowLayout(FlowLayout.CENTER));
			closeB = new JButton("Ok");
			buttonsP.add(closeB);
			getContentPane().add(buttonsP, "South");

			//action listeners
			closeB.addActionListener(this);
			portraitRB.addActionListener(this);
			landscapeRB.addActionListener(this);
			paperCB.addActionListener(this);
		}

		/*
		 * Actions.
		*/
		public void actionPerformed(ActionEvent ae) {
			Object cache = ae.getSource();
			if (closeB == cache) {
				setVisible(false);
			}
			if (portraitRB == cache) {
				if(((String)paperCB.getSelectedItem()).equals("A4")) {
					textL.setText("(297mm x 210mm)");
				} else {//Letter
					textL.setText("(279mm x 216mm)");
				}
			}
			if (landscapeRB == cache) {
				if(((String)paperCB.getSelectedItem()).equals("A4")) {
					textL.setText("(210mm x 297mm)");
				} else {//Letter 
					textL.setText("(216mm x 279mm)");
				}
			}
			if (paperCB == cache) {
				if (((String)paperCB.getSelectedItem()).equals("A4")) {
					if (portraitRB.isSelected()) {
						textL.setText("(297mm x 210mm)");
					} else {
						textL.setText("(210mm x 297mm)");
					}
				} else {//Letter
					if (portraitRB.isSelected()) {
						textL.setText("(279mm x 216mm)");
					} else {
						textL.setText("(216mm x 279mm)");
					}
				}
			}
		}
	}
}
