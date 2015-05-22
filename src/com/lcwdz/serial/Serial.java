package com.lcwdz.serial;

import java.io.*;
import java.util.*;

import javax.comm.*;

public class Serial implements SerialPortEventListener,Runnable {


	private Control control;
	
	// 串口号 波特率 效验位 数据位 停止位
	private String serialName = "COM1";
	private int baud;
	private String checkBit;
	private int dataBit;
	private String stopBit;
	
	// 串口配置参数
	private ArrayList<String> serialNameList;
	private ArrayList<Integer> baudList;
	private ArrayList<String> checkBitList;
	private ArrayList<Integer> dataBitList;
	private ArrayList<String> stopBitLit;
	private StringBuffer revDatas ;
	
	long revDataCount = 0; // 接收到的数据 计数
	long sendDataCount = 0; 	// 发送的数据 计数
	
	private Boolean connectState = false;
	private InputStream read;
	private OutputStream write;
	private SerialPort serialPort;
	private byte[] receiveBuf;
	private DataOutputStream dos;
	private CommPortIdentifier portId;
	private String writeStr;
	private String hexRevData;
	private String charRevData;
	private String binRevData;
	
	
	public Serial(){
		
		serialInit();
	}
	
	public void setControl(Control control){
		this.control = control;
	}
	
	public Serial(int baud, int dataBit, String stopBit, String checkBit){
		
		
		this.serialName = Util.readValue("config.properties", "COM");
		
		
		this.baud = baud;
		this.checkBit = checkBit;
		this.dataBit = dataBit;
		this.stopBit = stopBit;
		
		revDatas = new StringBuffer();
		
		
		
		serialInit();
	}
	
	private void serialInit(){
		
		
		serialNameList = new ArrayList<String>();
		@SuppressWarnings("unchecked")
		Enumeration<CommPortIdentifier> portList = CommPortIdentifier.getPortIdentifiers(); // 获取端口列表(并口、串口)
		while(portList.hasMoreElements()){
			CommPortIdentifier portId = portList.nextElement();
			if(portId.getPortType() == CommPortIdentifier.PORT_SERIAL){
				serialNameList.add(portId.getName());
			}
			
		}
		
		baudList = new ArrayList<Integer>();
		Integer[] baudId =  new Integer[]{110, 300, 600, 1200, 2400, 4800, 9600, 14400, 19200, 38400, 57600, 115200, 128000, 256000};
		for(Integer i : baudId){
			baudList.add(i);
		}
		
		checkBitList = new ArrayList<String>(); 
		String[] checkBitId = {"None","Odd","Even","Mark","Space"};
		for(String s : checkBitId){

			checkBitList.add(s);
		}
		
		dataBitList = new ArrayList<Integer>();
		Integer[] dataBitId = {5,6,7,8};
		for(Integer i : dataBitId){
			dataBitList.add(i);
		}
		
		stopBitLit = new ArrayList<String>();
		String[] stopBitId = {"1","1.5","2"};
		for(String s : stopBitId){
			stopBitLit.add(s);
		}
		
	}
	
	public void open() throws SerialConnectionException{
		
		// 打开通信端口
		try {
			portId = 
					CommPortIdentifier.getPortIdentifier(serialName);
		} catch (NoSuchPortException e) {
			throw new SerialConnectionException("没有该端口：\r\n "+e.getMessage());
		}
		
		try {
			serialPort = (SerialPort)portId.open("Serial", 2000);
		} catch (PortInUseException e) {
			throw new SerialConnectionException("端口正在使用中：\r\n"+e.getMessage());
		}
		
		try {
			configurationSerialParameter(); // 串口参数配置
		} catch (UnsupportedCommOperationException e) {
			serialPort.close();
			throw new SerialConnectionException("串口参数不支持：\r\n"+e.getMessage());
		} 
		
		// 输入  输出流对象
		try {
			read = serialPort.getInputStream();
			write = serialPort.getOutputStream();	
		} catch (IOException e) {
			serialPort.close();
			throw new SerialConnectionException("打开I/O错误：\r\n"+e.getMessage());
		}
		
		try {
			serialPort.addEventListener(this);	// 注册事件监听
		} catch (TooManyListenersException e) {
			serialPort.close();
			throw new SerialConnectionException("注册串口监听器错误：\r\n"+e.getMessage());
		}	
		serialPort.notifyOnDataAvailable(true);	// 数据可用
		serialPort.notifyOnBreakInterrupt(true); // Set notifyOnBreakInterrup to allow event driven break handling.
		
		connectState = true;
		control.view.setSerialStateContent(this.toString());
		
	}
	
	private void configurationSerialParameter() throws UnsupportedCommOperationException {
		// TODO 串口参数设置
		 
		int stopBitTemp = 0;
		int checkBitTemp = 0;
		
		switch(stopBit){
		case "1" : stopBitTemp = SerialPort.STOPBITS_1; break;
		case "1.5" : stopBitTemp = SerialPort.STOPBITS_1_5; break;
		case "2" : stopBitTemp = SerialPort.STOPBITS_2; break;
		}
		
		switch(checkBit){
		case "None" : checkBitTemp = SerialPort.PARITY_NONE; break;
		case "Odd" : checkBitTemp = SerialPort.PARITY_ODD; break;
		case "Even" : checkBitTemp = SerialPort.PARITY_EVEN; break;
		case "Mark" : checkBitTemp = SerialPort.PARITY_MARK; break;
		case "Space" : checkBitTemp = SerialPort.PARITY_SPACE; break;
		}
		
		serialPort.setSerialPortParams(baud, dataBit, stopBitTemp, checkBitTemp); //串口初始化 设置
	}

	public void close() throws IOException{
		
		if(!connectState){
			return ;
		}
			
		if(portId != null){
			
			if(write != null){
				write.flush();
				write.close();
			}
			
			if(read != null){
				read.close();
			}
			
			serialPort.close();
			connectState = false;
			control.view.setSerialStateContent(this.toString());
		}
	}
	
	
	// 写
	
	public void write(String s) throws IOException{
		
		this.writeStr = s;
		new Thread(this).start();
		
	}

	
	// get set 方法
	public String getSerialName() {
		return serialName;
	}

	public void setSerialName(String serialName) {
		this.serialName = serialName;
	}

	public int getBaud() {
		return baud;
	}

	public void setBaud(int baud) {
		this.baud = baud;
	}

	public String getCheckBit() {
		return checkBit;
	}

	public void setCheckBit(String checkBit) {
		this.checkBit = checkBit;
	}

	public int getDataBit() {
		return dataBit;
	}

	public void setDataBit(int dataBit) {
		this.dataBit = dataBit;
	}

	public String getStopBit() {
		return stopBit;
	}

	public void setStopBit(String stopBit) {
		this.stopBit = stopBit;
	}
	

	@Override
	public void serialEvent(SerialPortEvent event){
		// TODO  串口事件
		switch(event.getEventType()){
		case SerialPortEvent.BI:	/*Break interrupt,通讯中断*/ 
        case SerialPortEvent.OE:	/*Overrun error，溢位错误*/ 
        case SerialPortEvent.FE:	/*Framing error，传帧错误*/ 
        case SerialPortEvent.PE:	/*Parity error，校验错误*/ 
        case SerialPortEvent.CD:	/*Carrier detect，载波检测*/ 
        case SerialPortEvent.CTS:	/*Clear to send，清除发送*/ 
        case SerialPortEvent.DSR:	/*Data set ready，数据设备就绪*/ 
        case SerialPortEvent.RI:	/*Ring indicator，响铃指示*/ 
        case SerialPortEvent.OUTPUT_BUFFER_EMPTY:	/*Output buffer is empty，输出缓冲区清空*/ 
        	break;
        case SerialPortEvent.DATA_AVAILABLE:	/*端口可用*/
        	receiveBuf = new byte[20];
        	int max = 0;
        	try {
        		while(read.available()>0){
        			max = read.read(receiveBuf);
        		}
        		
        		revDataCount += max;
        		revDatas.append(new String(receiveBuf).trim());
        		
        		binRevData = Util.bytes2bin(receiveBuf, max, "  ");
        		hexRevData = Util.bytes2Hex(receiveBuf, max, "  ");
        		charRevData = new String(receiveBuf, 0, max);
        		
        		
        		control.receiveToData(receiveBuf,max);
				
        	
        	} catch (IOException e) {
						System.out.println("接收数据失败："+e.getMessage());
					}
			break;
		}
		
	}
	
	public ArrayList<String> getSerialNameList() {
		return serialNameList;
	}

	public ArrayList<Integer> getBaudList() {
		return baudList;
	}

	public ArrayList<String> getCheckBitList() {
		return checkBitList;
	}

	public ArrayList<Integer> getDataBitList() {
		return dataBitList;
	}

	public ArrayList<String> getStopBitLit() {
		return stopBitLit;
	}

	public Boolean getConnectState() {
		return connectState;
	}

	public void setConnectState(Boolean connectState) {
		this.connectState = connectState;
	}

	public String getRevDatas() {
		return revDatas.toString();
	}

	public String getHexRevData() {
		return hexRevData;
	}
	
	public String getCharRevData() {
		return charRevData;
	}
	
	public String getBinRevData() {
		return binRevData;
	}
	
	
	@Override
	public String toString() {
	
		return "串口号:"+ serialName +
				"     波特率:"+ baud +
				"     数据位:"+ dataBit +
				"     停止位:"+ stopBit +
				"     校验位:"+ checkBit +
				"     打开状态:"+connectState;
	}

	@Override
	public void run() {
		// TODO 解决发送数据诸塞 问题 

		dos = new DataOutputStream(write);
		try {
			dos.writeUTF(writeStr);
			dos.close();
		} catch (IOException e) {
			control.view.showErrorMessage("send ERROR !");
		}
		
		
	}

	
}
