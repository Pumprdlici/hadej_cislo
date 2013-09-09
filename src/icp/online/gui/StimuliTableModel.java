package icp.online.gui;

import javax.swing.table.AbstractTableModel;

@SuppressWarnings("serial")
class StimuliTableModel extends AbstractTableModel {
	
	private static final String[] stimuliJTHeader = {"Number", "Score"};
	
	private Object[][] data;
	
	StimuliTableModel() {
		data = new Object[9][2];
		
		for (int i = 0; i < data.length; i++)
			data[i][0] = i+1;
	}
	
	public String getColumnName(int column) {
		return stimuliJTHeader[column];
	}
	
	@Override
	public int getColumnCount() {
		return stimuliJTHeader.length;
	}

	@Override
	public int getRowCount() {
		return data.length;
	}

	@Override
	public Object getValueAt(int row, int column) {
		return data[row][column];
	}
	
	public void setValueAt(Object value, int row, int column) {
		data[row][column] = value;
		fireTableCellUpdated(row, column);
	}
}
