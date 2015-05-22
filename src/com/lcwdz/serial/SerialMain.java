package com.lcwdz.serial;

import java.awt.EventQueue;

import javax.comm.SerialPort;




public class SerialMain {

	

	public static void main(String[] args) {
		
		
		
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					
					Serial serial = new Serial(
							9600, 
							SerialPort.DATABITS_8, 
							"1", 
							"NONE");	
				
					View view = new View();
					Control control = new Control();
					
					view.setControl(control);
					control.setViewAndContorl(serial, view);
					serial.setControl(control);
					
					control.init();
					
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
	}
}
