package icp.gui.result;


import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

@SuppressWarnings("serial")
public class ResultPanel extends JPanel
{
	private JTextPane resultTextJTP;
	
	private JList characterListJL;
	
	private DefaultListModel characterDLM;
	
	private List<?> resultCollection;
	
	public ResultPanel(List<?> resultCollection)
	{
		this.resultCollection = resultCollection;
		createInside();
	}
	
	private void createInside()
	{
		setLayout(new BorderLayout());
		JScrollPane resultJSP = new JScrollPane();
		resultJSP.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		resultJSP.setBorder(BorderFactory.createTitledBorder("Result"));
		resultTextJTP = new JTextPane();
		resultTextJTP.setEditable(false);
		resultJSP.setViewportView(resultTextJTP);
		
		add(resultJSP, BorderLayout.NORTH);
		
		characterDLM = new DefaultListModel();
		
		characterListJL = new JList(characterDLM);
		characterListJL.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		characterListJL.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		characterListJL.setVisibleRowCount(-1);
		characterListJL.addListSelectionListener(new ListSelectionListener()
		{
			@Override
			public void valueChanged(ListSelectionEvent event)
			{
				
			}
		});
		
		JScrollPane characterListJSP = new JScrollPane(characterListJL);
		characterListJSP.setPreferredSize(new Dimension(100, 80));
		
		add(characterListJSP, BorderLayout.WEST);
	}
	
	private void createCharacterDetailPanel()
	{
		
	}
}
