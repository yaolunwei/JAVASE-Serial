package com.lcwdz.serial;


import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;




public class Control {

	Serial serial;
	View view;
	
	private String emptyCount = "  ";
	
	
	public void setViewAndContorl(Serial serial, View view){
		this.serial = serial;
		this.view = view;
		
	}
	

	private void loadDataToView(){
		// 初始化  视图
		
	//  serialName, baud, checkBit, dataBit, stopBit;
		
			Iterator<String> serialName = serial.getSerialNameList().iterator();
			while(serialName.hasNext()){
				view.getSerialName().addItem(serialName.next());
			}
			
			Iterator<Integer> baud = serial.getBaudList().iterator();
			while(baud.hasNext()){
				view.getBaud().addItem(baud.next()+"");
			}
			
			Iterator<String> checkBit = serial.getCheckBitList().iterator();
			while(checkBit.hasNext()){
				view.getCheckBit().addItem(checkBit.next()+"");
			}
			
			Iterator<Integer> dataBit = serial.getDataBitList().iterator();
			while(dataBit.hasNext()){
				view.getDataBit().addItem(dataBit.next()+"");
			}
			
			Iterator<String> stopBit = serial.getStopBitLit().iterator();
			while(stopBit.hasNext()){
				view.getStopBit().addItem(stopBit.next()+"");
			}
			
			view.getSerialName().setSelectedItem(serial.getSerialName());
			view.getBaud().setSelectedItem(serial.getBaud()+"");
			view.getCheckBit().setSelectedItem(serial.getCheckBit()+"");
			view.getDataBit().setSelectedItem(serial.getDataBit()+"");
			view.getStopBit().setSelectedItem(serial.getStopBit()+"");
			
			view.initCloseListenerEvent = true;
			
			try {
				serial.open();
			} catch (SerialConnectionException e) {
				serial.setConnectState(false);
				view.setConnectState(false);
				view.showErrorMessage(e.getMessage());
				return ;
			} 
			
			serial.setConnectState(true);
			view.setConnectState(true);
	}


	public void clickConnect() {
		// TODO 点击 连接 按钮 处理
		
		serial.revDataCount = 0;
		serial.sendDataCount = 0;
		
		Boolean connectState = serial.getConnectState();

		if(connectState){
			try {
				serial.close();
			} catch (IOException e) {
				serial.setConnectState(true);
				view.setConnectState(true);
				view.showErrorMessage("断开连接 失败 : "+"\r\n"+e.getMessage());
				return ;
			} 
			serial.setConnectState(false);
			view.setConnectState(false);
				
			
		}
		else {
			try {
				serial.open();
			} catch (SerialConnectionException e) {
				serial.setConnectState(false);
				view.setConnectState(false);
				view.showErrorMessage(e.getMessage());
				return ;
			}
			
			serial.setConnectState(true);
			view.setConnectState(true);
			
		}
	}


	public void serialSetChange(ActionEvent source) {
		// TODO 串口设置改变		name ==> serialName, baud, checkBit, dataBit, stopBit;
		
		@SuppressWarnings("unchecked")
		JComboBox<String> jcb = (JComboBox<String>)source.getSource();
		String selectName = jcb.getName();
		String setvalue = (String)jcb.getSelectedItem();
		
		switch(selectName){	// 设置串口参数
		
		case "serialName" : serial.setSerialName(setvalue); break;
		case "baud" : serial.setBaud(Integer.parseInt(setvalue)); break;
		case "checkBit" : serial.setCheckBit(setvalue); break;
		case "dataBit" : serial.setDataBit(Integer.parseInt(setvalue)); break;
		case "stopBit" : serial.setStopBit(setvalue); break;
		
		}
		
		if(serial.getConnectState()){
			try {
				serial.close();
				serial.open();
			} catch (IOException e) {
				serial.setConnectState(false);
				view.setConnectState(false);
				view.showErrorMessage("打开端口失败：\r\n"+e.getMessage());
				return ;
			} catch (SerialConnectionException e) {
				serial.setConnectState(false);
				view.setConnectState(false);
				view.showErrorMessage(e.getMessage());
				return ;
			}
			
			serial.setConnectState(true);
			view.setConnectState(true);
		}
		
		
	}

	public void init() {
		// TODO serial control 初始 之后 执行该函数 开始：
		view.initialize();
		loadDataToView();
		
		
		view.setVisible(true);
		
		
	}


	public void receiveToData(byte[] receiveBuf, int max) {
		// TODO 由串口事件直接调用  接收到数据
		
		JTextArea revContent = view.getSerialReceiveText();
		view.setRevDataCount(serial.revDataCount+max);
		
		
		if(view.getStandardTemplateOnOff()){
			
			
			if(view.ishexDisply()){
				revContent.append(Util.bytes2Hex(receiveBuf, max, emptyCount)); 
			}
			
			if(view.binDisplay.isSelected()){
				revContent.append(Util.bytes2bin(receiveBuf, max, emptyCount));
			}
			
			if(!(view.ishexDisply()||view.binDisplay.isSelected())){	
				revContent.append(new String(receiveBuf, 0, max)+emptyCount);
			}
			
			revContent.selectAll();
		
		}
		
		if(view.getVerifyDataOnOff()){
			// 判断 验证数据 接口 是否打开
			verifyData(receiveBuf, max);
		}
		
	}
	
	
	private void verifyData(byte[] receiveBuf, int max) {
		// TODO 验证数据处理
		
		JTable jt = view.getVerifyDataJtable();
//System.out.println(view.verifyDataMode);	
		if(view.verifyDataMode){	// 判断是自动验证 还是手动验证
	
			int seleceRow = jt.getSelectedRow();
			int rowCount = jt.getRowCount();
			int rowIndex = seleceRow;
			
			if(rowCount==0){	// 行数 为零  return
				return ;
			}
			if(seleceRow == -1){
				rowIndex = 0;
			}
			
			for( ; rowIndex<rowCount; rowIndex++){
				
				jt.setRowSelectionInterval(rowIndex, rowIndex);
			
				if(view.isHexVerify()){
					
					String revText = Util.bytes2Hex(receiveBuf, max, " ");
					jt.setValueAt(revText, rowIndex, 1);	
					String setText = ((String)jt.getValueAt(rowIndex, 0));

					if(setText != null){
						setText = setText.replaceAll(" ", "").replaceAll("0x", "").trim().toUpperCase();
						revText = revText.replaceAll(" ", "");
									
						if(setText.equals(revText)){
							jt.setValueAt("PASS", rowIndex, 2);
						}
						else {
							jt.setValueAt("## ERROR ##", rowIndex, 2);
						}
						
					}
					else {
						jt.setValueAt("## ERROR ##", rowIndex, 2);
					}
						
				}
				
				else {
					
					String revText = new String(receiveBuf, 0, max);
					jt.setValueAt(revText, rowIndex, 1);
					
					String setText = ((String)jt.getValueAt(rowIndex, 0));
					
					if((setText !=null) && revText.trim().equals(setText.trim())) {
						jt.setValueAt("PASS", rowIndex, 2);
					}
					else {
						jt.setValueAt("## ERROR ##", rowIndex, 2);
					}
				}
			}	
				
			}
			else {
			int selectRow = jt.getSelectedRow();
			
			if(selectRow == -1){
				return ;
			}
			
			if(view.isHexVerify()){
				
				String revText = Util.bytes2Hex(receiveBuf, max, " ");
				jt.setValueAt(revText, selectRow, 1);	
				String setText = ((String)jt.getValueAt(selectRow, 0));

				if(setText != null){
					setText = setText.replaceAll(" ", "").replaceAll("0x", "").trim().toUpperCase();
					revText = revText.replaceAll(" ", "");
								
					if(setText.equals(revText)){
						jt.setValueAt("PASS", selectRow, 2);
					}
					else {
						jt.setValueAt("## ERROR ##", selectRow, 2);
					}
					
				}
				else {
					jt.setValueAt("## ERROR ##", selectRow, 2);
				}
					
			}
			
			else {
				
				String revText = new String(receiveBuf, 0, max);
				jt.setValueAt(revText, selectRow, 1);
				
				String setText = ((String)jt.getValueAt(selectRow, 0));
				
				if((setText !=null) && revText.trim().equals(setText.trim())) {
					jt.setValueAt("PASS", selectRow, 2);
				}
				else {
					jt.setValueAt("## ERROR ##", selectRow, 2);
				}
			}	
		}
	}


	public void sendData(JTextArea sendArea,String sendData) {
		// TODO 点击发送 数据 实现
		
		if(!serial.getConnectState()){
			view.showErrorMessage("亲， 你还没有打开串口，\r\n 请先 打开串口 ，在进行发送。 ");
			return ;
		}

		String formatData = "";
		
		if(view.isSendBin()){
			
			formatData = Util.char2Bin(sendData);
			view.setFormatData(formatData);

		}
		
		if(view.isSendHex()){
			
			formatData = Util.char2Hex(sendData);
			view.setFormatData(formatData);
			formatData = formatData.replace(" ", "");
			
		}
		
		if( (!(view.isSendBin()||view.isSendHex()))){
			
			formatData = sendData.trim();
			view.setFormatData(formatData);
			
		}
		
		try {
			serial.write(formatData);
		} catch (IOException e) {
			view.showErrorMessage("send 错误！");
			return ;
		}
		
		view.setSendDataCount(serial.sendDataCount += formatData.trim().length());
		
	}


	public void loadVerifyData(String fileName) {
		// TODO 加载数据实现
		
		if(fileName.toLowerCase().endsWith(".txt")){
			
			try {
					
				byte idBuf[] = new  byte[14];
				FileInputStream fr = new FileInputStream(new File(fileName));
				fr.read(idBuf);
				
				if(new String(idBuf).equals("LCW_VerifyData")){
					
					StringBuffer verifyDataValues = new StringBuffer();
					byte[] tempbuf = new byte[20];
					
					while(fr.read(tempbuf)!=-1){
						verifyDataValues.append(new String(tempbuf));
						
					}
					
					String[] verifyValues = verifyDataValues.toString().split(",");
					JTable jt = view.getVerifyDataJtable();
						int a = jt.getRowCount();
						int b = verifyValues.length;
						int insertRow =  a>b ? -1 : b-a ;
						for(int j=0 ;j<insertRow; j++){
							view.getTableModel().addRow(new String[]{});
						}
					
					for(int i=1; i<verifyValues.length; i++){
						
						String value = verifyValues[i];
						
						jt.setValueAt(value, i-1, 0);
					}
					
				}else {
					view.showErrorMessage("数据错误: 不是有效的数据！");
				}
				
				} catch (FileNotFoundException e) {
					view.showErrorMessage("亲，文件没有找到！");
				}catch (IOException e) {
					view.showErrorMessage("亲，I/O错误  ！");
				}
			
		}else {
			view.showErrorMessage("选择的文件不正确,请选择以 .txt 结尾的文本文件");
		}
		
		
		
		
	}


	public void saveVerifyData(String verifyData, String FileName) {
		// TODO 保存数据实现
		
		String idString = "LCW_VerifyData"; //"\r\n"  换行
		
		File f = new File(FileName);
		
	
		try {
			
			if(f.createNewFile()){
				FileWriter fos = new FileWriter(f);
				fos.write(idString+verifyData);
				
				fos.flush();
				fos.close();
			}
			else{
				view.showErrorMessage("保存数据失败：该文件已经存在！");
			}		
			
		} catch (IOException e) {
			view.showErrorMessage("IO保存错误！");
		}
		
		
	}

	public void clerContent() {
		// TODO 清空 数据
		view.getSerialReceiveTextHex().setText("");
		serial.revDataCount = 0;
		view.setRevDataCount(serial.revDataCount);
		
	}


	public void clickAutoLine(JPanel setDisply, boolean autoLine, JTextField autoLineCount) {
		// TODO 点击自动 换行 设置
		
		if(autoLine){
			emptyCount = "\r\n";
			view.setWheelIndex(1);
		}
		else {
			emptyCount = "  ";
		}
		
	}


	public void clickSendContent(JTextArea serialSendText) {
		// TODO 发送 内容 清空
		
		serialSendText.setText("");
		view.setFormatData("");
		serial.sendDataCount = 0;
		view.setSendDataCount(serial.sendDataCount);
		
	}

	public void clickHexVerify(boolean selected, JTable verifyDataJtable) {
		// TODO 点击 hex 验证实现            >>  还为实现  未使用
	
		JTable jt = verifyDataJtable;
		int rowCount = jt.getRowCount();
		
		if(rowCount == 0){
			return ;
		}
		
	
		
		if(selected){
			
			for(int i=0; i<rowCount; i++){
				
				Object o = jt.getValueAt(i, 1);
				o = o==null ? new String() : o ;
				System.out.println(o.toString());
				jt.setValueAt(Util.char2Hex(o.toString()), i, 1);			
			}	
		}
		else {
			
			for(int j=0; j<rowCount; j++){
				//verifyDataJtable.setValueAt(Util.hex2Char(verifyData[i]),i,1);
			}
		
		}	
	}


	

	public void mouseWheel(JTextArea revArea, int wheelIndex, boolean autoLine) {
		// TODO 鼠标滚动实现
		
		if(!autoLine) {
			
			String emptyStrCount = " ";
			for(int i=0; i<=wheelIndex; i++){
				emptyStrCount += " ";
			}
			
			emptyCount = emptyStrCount;
		}
		
	}

	
}

