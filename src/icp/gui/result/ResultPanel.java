package icp.gui.result;


import icp.algorithm.mp.DetectionAlgorithm;
import icp.aplication.*;
import icp.gui.*;

import java.awt.*;
import java.util.*;
import java.util.List;

import javax.swing.*;
import javax.swing.event.*;


@SuppressWarnings("serial")
public class ResultPanel extends JPanel implements Observer
{
	private static final Dimension SIGNAL_VIEWER_DIMENSION = new Dimension(360, 140);
	
	private SignalViewer2 column1;
	
	private SignalViewer2 column2;
	
	private SignalViewer2 column3;
	
	private SignalViewer2 column4;
	
	private SignalViewer2 row1;
	
	private SignalViewer2 row2;
	
	private SignalViewer2 row3;
	
	private SignalViewer2 row4;
	
	private JTextPane resultTextJTP;
	
	private JList characterListJL;
	
	private DefaultListModel characterDLM;
	
	private List<Element> elements;
	
	private JTextPane characterInfoJTP;
	
	private List<DetectionAlgorithm> convolutions;
	
	private SessionManager app;
	
	private ResultDialog resultDialog;
	
	private WTDetectionDetailDialog wtDetectionDetail;
	
	public ResultPanel(ResultDialog resultDialog, List<Element> elements, SessionManager app, JFrame mainWindow)
	{
		super();
		this.app = app;
		this.resultDialog = resultDialog;
		wtDetectionDetail = new WTDetectionDetailDialog(resultDialog, mainWindow, resultDialog.getMainWindowProvider());
		
		if (elements != null && elements.size() > 0)
		{
			this.elements = elements;
			if (app.getMatchingPursuitDetectionAlgorithm() != null)
			{
				this.convolutions = app.getMatchingPursuitDetectionAlgorithm().getConvolutionResults();
			}
			else
			{
				this.convolutions = null;
			}
			
			createInside();
		}
	}
	
	private void createInside()
	{
		setLayout(new BorderLayout());
		JScrollPane resultJSP = new JScrollPane();
		resultJSP.setBorder(BorderFactory.createTitledBorder("Result"));
		resultTextJTP = new JTextPane();
		resultTextJTP.setEditable(false);
		resultJSP.setViewportView(resultTextJTP);
		
		add(resultJSP, BorderLayout.NORTH);
		
		characterDLM = new DefaultListModel();
		
		String resultString = "";
		
		for (int i = 0; i < elements.size(); i++)
		{
			characterDLM.addElement(elements.get(i).getDetectedChar());
			resultString += elements.get(i).getDetectedChar() + " ";
		}
		
		resultTextJTP.setText(resultString);
		
		characterListJL = new JList(characterDLM);
		characterListJL.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		characterListJL.setLayoutOrientation(JList.VERTICAL_WRAP);
		characterListJL.setVisibleRowCount(-1);
		characterListJL.setSelectedIndex(0);
		characterListJL.addListSelectionListener(new ListSelectionListener()
		{
			@Override
			public void valueChanged(ListSelectionEvent event)
			{
				showElement(characterListJL.getSelectedIndex());
			}
		});
		
		JScrollPane characterListJSP = new JScrollPane(characterListJL);
		characterListJSP.setBorder(BorderFactory.createTitledBorder("Characters"));
		characterListJSP.setPreferredSize(new Dimension(80, 1));
		
		add(characterListJSP, BorderLayout.WEST);
		
		createCharacterDetailPanel();
		showElement(0);
	}
	
	private void showElement(int index)
	{
		if (-1 < index && index < elements.size())
		{
			Element e = elements.get(index);
			characterInfoJTP.setText("Character: " + e.getDetectedChar() + "   Detected column: " + (e.getDetectedColumn() + 1) + " Detected row: " + (e.getDetectedRow() + 1));
			double[][] rowAndColumns = e.getRowsAndColumnsEpoch();
			//první 4 jsou øádky
			row1.setValues(rowAndColumns[0]);
			row2.setValues(rowAndColumns[1]);
			row3.setValues(rowAndColumns[2]);
			row4.setValues(rowAndColumns[3]);
			//další 4 jsou sloupce
			column1.setValues(rowAndColumns[4]);
			column2.setValues(rowAndColumns[5]);
			column3.setValues(rowAndColumns[6]);
			column4.setValues(rowAndColumns[7]);
			
			repaint();
		}
	}
	
	private void createCharacterDetailPanel()
	{
		JPanel signalBackgroundJP = new JPanel(new GridLayout(4, 4));
		
		JPanel column1BackJP = new JPanel(new GridLayout(1, 1));
		column1BackJP.setBorder(BorderFactory.createTitledBorder("Column 1"));
		column1BackJP.setPreferredSize(SIGNAL_VIEWER_DIMENSION);
		column1 = new SignalViewer2(4, column1BackJP, 0);
		column1BackJP.add(column1);
		JPanel column2BackJP = new JPanel(new GridLayout(1, 1));
		column2BackJP.setBorder(BorderFactory.createTitledBorder("Column 2"));
		column2BackJP.setPreferredSize(SIGNAL_VIEWER_DIMENSION);
		column2 = new SignalViewer2(5, column2BackJP, 0);
		column2BackJP.add(column2);
		JPanel column3BackJP = new JPanel(new GridLayout(1, 1));
		column3BackJP.setBorder(BorderFactory.createTitledBorder("Column 3"));
		column3BackJP.setPreferredSize(SIGNAL_VIEWER_DIMENSION);
		column3 = new SignalViewer2(6, column3BackJP, 0);
		column3BackJP.add(column3);
		JPanel column4BackJP = new JPanel(new GridLayout(1, 1));
		column4BackJP.setBorder(BorderFactory.createTitledBorder("Column 4"));
		column4BackJP.setPreferredSize(SIGNAL_VIEWER_DIMENSION);
		column4 = new SignalViewer2(7, column4BackJP, 0);
		column4BackJP.add(column4);
		JPanel row1BackJP = new JPanel(new GridLayout(1, 1));
		row1BackJP.setBorder(BorderFactory.createTitledBorder("Row 1"));
		row1BackJP.setPreferredSize(SIGNAL_VIEWER_DIMENSION);
		row1 = new SignalViewer2(0, row1BackJP, 0);
		row1BackJP.add(row1);
		JPanel row2BackJP = new JPanel(new GridLayout(1, 1));
		row2BackJP.setBorder(BorderFactory.createTitledBorder("Row 2"));
		row2BackJP.setPreferredSize(SIGNAL_VIEWER_DIMENSION);
		row2 = new SignalViewer2(1, row2BackJP, 0);
		row2BackJP.add(row2);
		JPanel row3BackJP = new JPanel(new GridLayout(1, 1));
		row3BackJP.setBorder(BorderFactory.createTitledBorder("Row 3"));
		row3BackJP.setPreferredSize(SIGNAL_VIEWER_DIMENSION);
		row3 = new SignalViewer2(2, row3BackJP, 0);
		row3BackJP.add(row3);
		JPanel row4BackJP = new JPanel(new GridLayout(1, 1));
		row4BackJP.setBorder(BorderFactory.createTitledBorder("Row 4"));
		row4BackJP.setPreferredSize(SIGNAL_VIEWER_DIMENSION);
		row4 = new SignalViewer2(3, row4BackJP, 0);
		row4BackJP.add(row4);
		
		
		signalBackgroundJP.add(column1BackJP);
		signalBackgroundJP.add(row1BackJP);
		signalBackgroundJP.add(column2BackJP);
		signalBackgroundJP.add(row2BackJP);
		signalBackgroundJP.add(column3BackJP);
		signalBackgroundJP.add(row3BackJP);
		signalBackgroundJP.add(column4BackJP);
		signalBackgroundJP.add(row4BackJP);
		
		SignalViewer2[] viewers = {column1, column2, column3, column4, row1, row2, row3, row4};
		
		for (int i = 0; i < viewers.length; i++)
			for (int j = 0; j < viewers.length; j++)
				if (i != j)
				{
					viewers[i].getComunicationProvider().addObserver(viewers[j].getComunicationProvider());
					viewers[i].getComunicationProvider().addObserver(this);
				}
		
		characterInfoJTP = new JTextPane();
		characterInfoJTP.setEditable(false);
		
		JPanel detailBackgroundJP = new JPanel(new BorderLayout());
		detailBackgroundJP.setBorder(BorderFactory.createTitledBorder("Character detail"));
		detailBackgroundJP.add(characterInfoJTP, BorderLayout.NORTH);
		detailBackgroundJP.add(signalBackgroundJP, BorderLayout.CENTER);
		
		
		add(detailBackgroundJP, BorderLayout.CENTER);
	}

	@Override
	public void update(Observable sender, Object msg)
	{
		if (msg instanceof Integer)
		{
			int id = ((Integer) msg).intValue();
			
			if (app.getLastUsedDetection() == app.WAVELET_DETECTION)
			{
				int elementIndex = characterListJL.getSelectedIndex();
				WaveletTransformDetectionAlgorithm wtda = app.getWTDetectionAlgorithm();
				
				wtDetectionDetail.setSignalData(
						app.getAveraging().getElements().get(elementIndex).getRowsAndColumnsEpoch()[id]);
				wtDetectionDetail.setWaveletCoeficients(
						wtda.getWaveletCoeficientsOfElements().get(elementIndex)[id]);
				wtDetectionDetail.setHighestCoeficients(
						wtda.getHighestCoeficientsOfElements().get(elementIndex)[id]);
				wtDetectionDetail.setElementIndex(elementIndex);
				wtDetectionDetail.setRowOrColumn(id);
				wtDetectionDetail.setCWT(wtda.getCwt());
				wtDetectionDetail.setStartIntervalDetection(wtda.getStartIntervalDetection());
				wtDetectionDetail.setEndIntervalDetection(wtda.getEndIntervalDetection());
				wtDetectionDetail.setHighestCoefInInterval(
						wtda.getHighestCoefInIntervalOfElements().get(elementIndex)[id]);
				wtDetectionDetail.setIndexHighestCoefInInterval(
						wtda.getIndexesHighestCoefInIntervalOfElements().get(elementIndex)[id]);
				wtDetectionDetail.setActualLocationAndVisibility();
			}
			else if (app.getLastUsedDetection() == app.MP_DETECTION)
			{
				Object[] options = {"Close"};
				JOptionPane.showOptionDialog(this,
				    new MPDetectionDetail(convolutions.get(((characterListJL.getSelectedIndex() * 8) + id)), elements.get(characterListJL.getSelectedIndex()).getRowsAndColumnsEpoch()[id]),
				    "Detection result detail", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE,
				    null, options, options[0]);
				
			}
			else
			{
				JOptionPane.showMessageDialog(null,
		    		    "Data can't be displayed. Run a detection at first.",
		    		    "No data",
		    		    JOptionPane.ERROR_MESSAGE);
			}
		}
	}
}
