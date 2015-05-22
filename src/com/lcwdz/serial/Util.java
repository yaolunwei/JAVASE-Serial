package com.lcwdz.serial;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.Enumeration;
import java.util.Properties;

public class Util {  
      
    private static String hexStr =  "0123456789ABCDEF";  
    private static String[] binaryArray =   
        {"0000","0001","0010","0011",  
        "0100","0101","0110","0111",  
        "1000","1001","1010","1011",  
        "1100","1101","1110","1111"};

    private static String promptHead = "提示：";
    
    public static String binDisplay = promptHead+"以二进制格式显示例如 10100001 ";
    
	public static String hexDisplay = promptHead+"以十六进制格式显示例如 AF FF 1D ";

	public static String clerRevContent = promptHead+"清空接收到的内容";

	public static String stopRev = promptHead+"暂停接收数据";
	
	public static String autoLine = promptHead+"选中：数据接收一次换行，" +
			"未选中：滚动鼠标滚轮增加接收到数据之间的空隙 下加 上减  ";  
          
    
    /**
     * 
     * @param b
     * @return 转换为二进制数
     */
    public  String byteToBinaryStr(byte b){
    	
    	String outStr = "";
    	int pos = 0;
    	
    	pos = (b&0xF0)>>4;
    	outStr += binaryArray[pos];
    	pos = b&0x0F;
    	outStr += binaryArray[pos];
  System.out.println(outStr);
    	return outStr;
    }   
    
    /** 
     *  
     * @param str 
     * @return 转换为二进制字符串 
     */  
    public  String bytes2BinaryStr(byte[] bArray){  
          
        String outStr = "";  
        int pos = 0;  
        for(byte b:bArray){  
            //高四位  
            pos = (b&0xF0)>>4;  
            outStr+=binaryArray[pos];  
            //低四位  
            pos=b&0x0F;  
            outStr+=binaryArray[pos];  
        }  
        
        System.out.println(outStr);
        return outStr;  
          
    }  
    /** 
     *  
     * @param bytes 
     * @return 将二进制转换为十六进制字符输出 
     */  
    public  String BinaryToHexString(byte[] bytes){  
          
        String result = "";  
        String hex = "";  
        for(int i=0;i<bytes.length;i++){  
            //字节高4位  
            hex = String.valueOf(hexStr.charAt((bytes[i]&0xF0)>>4));  
            //字节低4位  
            hex += String.valueOf(hexStr.charAt(bytes[i]&0x0F));  
            result +=hex+" ";  
        }  
        return result;  
    }  
    /** 
     *  
     * @param hexString 
     * @return 将十六进制转换为字节数组 
     */  
    public static byte[] HexStringToBinary(String hexString){  
        //hexString的长度对2取整，作为bytes的长度  
        int len = hexString.length()/2;  
        byte[] bytes = new byte[len];  
        byte high = 0;//字节高四位  
        byte low = 0;//字节低四位  
  
        for(int i=0;i<len;i++){  
             //右移四位得到高位  
             high = (byte)((hexStr.indexOf(hexString.charAt(2*i)))<<4);  
             low = (byte)hexStr.indexOf(hexString.charAt(2*i+1));  
             bytes[i] = (byte) (high|low);//高地位做或运算  
        }  
        return bytes;  
    }  
    
    /**
     *  byte[] ==>> Hex 
     * @param b
     * @return String
     */
    
    public static String bytes2Hex(byte[] b, int max, String empty)  
    {  
        String stmp="";  
        StringBuilder sb = new StringBuilder("");  
        for (int n=0;n<max;n++)  
        {  
        	
        	stmp = Integer.toHexString(b[n] & 0xFF);  
        	sb.append((stmp.length()==1)? "0"+stmp : stmp);  
        	
        	sb.append(empty);
        }  
        return sb.toString().toUpperCase();  
    }  
    
    
    /**  
     * 十六进制转换字符串 
     * @param String str Byte字符串(Byte之间无分隔符 如:[616C6B]) 
     * @return String 对应的字符串 
     */    
  /*  public static String hexStr2Str(String hexStr)  
    {    
        String str = "0123456789ABCDEF";    
        char[] hexs = hexStr.toCharArray();    
        byte[] bytes = new byte[hexStr.length() / 2];    
        int n;    
  
        for (int i = 0; i < bytes.length; i++)  
        {    
            n = str.indexOf(hexs[2 * i]) * 16;    
            n += str.indexOf(hexs[2 * i + 1]);    
            bytes[i] = (byte) (n & 0xff);    
        }    
        return new String(bytes);    
    }  */
    
    /**
     * 
     * @param b 
     * @return
     */
    /*public static String bytes2Hex(byte[] bf){
    	
    	StringBuffer sb = new StringBuffer();
    	
    	String[] s = new String[]{"0","1","2","3","4","5","6","7","8","9","A","B","C","D","E","F"};
    	
    	String t = new String(bf).trim().toUpperCase();
		char[] cs = t.toCharArray();
    	
    	for(int i=0; i<cs.length; i++){
    		
    		char c = cs[i];
  
    		switch(c){
    		case 65 : sb.append('A'); break;
    		case 66 : sb.append('B'); break;
    		case 67 : sb.append('C'); break;
    		case 68 : sb.append('D'); break;
    		case 69 : sb.append('E'); break;
    		case 70 : sb.append('F'); break;
    		default : 
    		
    		}
    		
    	}
    	
    	System.out.println(sb.toString()+"==");
    	
    	return sb.toString();
    }*/
    
    /**
     * byte ==>> 0000 0000
     * @param b
     * @return
     */
    public static String  bytes2bin(byte[] bArray, int max, String empty){
    	
    	String outStr = "";  
         int pos = 0;  
         for(int i=0; i<max; i++){  
             //高四位  
             pos = (bArray[i]&0xF0)>>4;  
             outStr+=binaryArray[pos];  
             //低四位  
             pos=bArray[i]&0x0F;  
             outStr += binaryArray[pos];  
             outStr += empty;
         }  
         
      //   System.out.println(outStr);
         return outStr;  
    }
/**
 * char ==>> Bin
 * @param sendData
 * @return
 */
	public static String char2Bin(String sendData) {
		// TODO 字符 串 转换为二进制 
		StringBuffer outStr = new StringBuffer();
		
		char[] cs = sendData.trim().toCharArray();
		
		for(int i=0; i<cs.length; i++){
			
			char c = cs[i];
			String hexStr = Integer.toHexString(c); // char 转换成 十六进制
			
			//outStr.append(binaryArray[hexStr.charAt(0)]);		
		}
		
			//System.out.println(outStr);
		
		return outStr.toString();
	}
    
	
	
	/**
	 * char ==>> Hex
	 * @param sendData
	 * @return
	 */
	public static String char2Hex(String sendData) {
		// TODO Auto-generated method stub
		StringBuffer outStr = new StringBuffer();
		
		char[] cs = sendData.trim().toCharArray();
		
		for(int i=0; i<cs.length; i++){
			
			char c = cs[i];
			String hexStr = Integer.toHexString(c); // char 转换成 十六进制
			outStr.append((hexStr.length()==1)? "0"+hexStr : hexStr);  
			outStr.append("  ");
		}
		
		//	System.out.println(outStr);	
			
		return outStr.toString().toUpperCase();
	}

	/**
	 * Hex ==>> char
	 * @param string
	 * @return
	 */
	public static String hex2Char(String string) {
		
		StringBuffer outStr = new StringBuffer();
		String str = "0123456789ABCDEF";
		String[] hexStr = string.trim().toUpperCase().split(" ");
		
		int n;
		for(int i=0; i<hexStr.length; i++){
			char[] cs = hexStr[i].toCharArray();
			
			n = str.indexOf(cs[0])*16;
			n += str.indexOf(cs[1]);
			
			outStr.append((char)n);
		}
        return outStr.toString();
	}
	
	public static String hexFormat(String hexStr){
		StringBuffer outStr = new StringBuffer();
		
		String[] strs = hexStr.trim().split(" ");
		for(int i=0; i<strs.length; i++){
			String str = strs[i];
			outStr.append((str.length()==1)? "0"+str : str);  
			System.out.println(str.length()+"==");
		}
		
		return outStr.toString().toUpperCase();
	}
	
	
	
    /** 
     * 获取key对应的value值 
     * @param filePath 
     * @param name 
     * @return 
     */  
    public static String readValue(String filePath, String name){  
     
    	Properties props = new Properties();
    	String value = "";
    	try {
			InputStream is = new FileInputStream(filePath);
			props.load(is);
			
			value = props.getProperty(name);
			is.close();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	return value; 
    }  
	
    /** 
     * 写资源文件 
     *  
     * @param filePath 
     *            文件路径 
     * @param encoding 
     *            文件编码 
     * @param name 
     *            名称 
     * @param value 
     *            值 
     */  
    public static void write(String filePath, String name, String value) {  
        try {  
            Properties props = new Properties(); 
            
            InputStream is = new FileInputStream(filePath);
            props.load(is);
            
            Enumeration<String> e = (Enumeration<String>) props.propertyNames();
            while(e.hasMoreElements()) { 
            	String key = e.nextElement();
            	props.setProperty(key, props.getProperty(key));
            }
            props.setProperty(name, value); 
          
            Writer osw = new OutputStreamWriter(new FileOutputStream(filePath));  
            // 以适合使用load方法加载到Properties表中的格式，将此Properties表中的属性列表（键和元素对）写入输出流   
  
            props.store(osw, "串口配置文件");  
            osw.close();  
        } catch (IOException e) {  
            // TODO Auto-generated catch block  
            e.printStackTrace();  
        }  
  
    }   
   
}  