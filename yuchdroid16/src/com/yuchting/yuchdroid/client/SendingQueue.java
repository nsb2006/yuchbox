/**
 *  Dear developer:
 *  
 *   If you want to modify this file of project and re-publish this please visit:
 *  
 *     http://code.google.com/p/yuchberry/wiki/Project_files_header
 *     
 *   to check your responsibility and my humble proposal. Thanks!
 *   
 *  -- 
 *  Yuchs' Developer    
 *  
 *  
 *  
 *  
 *  尊敬的开发者：
 *   
 *    如果你想要修改这个项目中的文件，同时重新发布项目程序，请访问一下：
 *    
 *      http://code.google.com/p/yuchberry/wiki/Project_files_header
 *      
 *    了解你的责任，还有我卑微的建议。 谢谢！
 *   
 *  -- 
 *  语盒开发者
 *  
 */
package com.yuchting.yuchdroid.client;

import java.util.Arrays;
import java.util.Vector;

public class SendingQueue extends Thread{
	
	final class SendingQueueData{
		public int msgType;
		public byte[] msgData;
		
		public SendingQueueData(int _type,byte[] _data){
			msgType = _type;
			msgData = _data;
		}
	}
	
	Vector<SendingQueueData>	m_sendingData = new Vector<SendingQueueData>();
	ConnectDeamon				m_mainDeamon = null;
	
	boolean					m_destory	= false;
	
	public SendingQueue(ConnectDeamon _deamon){
		m_mainDeamon = _deamon;
		start();
	}
	
	public void destory(){
		m_destory = true;
		
		if(isAlive()){
			interrupt();
		}
	}
	
	public void connectNotify(){
		synchronized (m_sendingData) {
			m_sendingData.notify();
		}		
	}
	
	public boolean addSendingData(int _msgType ,byte[] _data,boolean _exceptSame)throws Exception{
		
		if(!m_mainDeamon.isDisconnectState()){
			m_mainDeamon.m_connect.SendBufferToSvr(_data,false);
		}else{
			synchronized (m_sendingData) {
				if(_exceptSame){
					for(int i = 0 ;i < m_sendingData.size();i++){
						SendingQueueData t_data = (SendingQueueData)m_sendingData.elementAt(i);
						if(t_data.msgType == _msgType && Arrays.equals(t_data.msgData,_data)){
							return false;
						}
					}
				}
				m_sendingData.addElement(new SendingQueueData(_msgType,_data));
				
				m_sendingData.notify();
			}		
		}
		
		return true;
	}
			
	public void run(){
		
		while(!m_destory){
			
			try{
				synchronized (m_sendingData) {
					
					m_sendingData.wait();
					
					if(m_mainDeamon.isDisconnectState()){
						continue;
					}

					for(int i = 0 ;i < m_sendingData.size();i++){
						SendingQueueData t_data = (SendingQueueData)m_sendingData.elementAt(i);
						m_mainDeamon.m_connect.SendBufferToSvr(t_data.msgData,false);
					}
					
					m_sendingData.removeAllElements();
				}
				
			}catch(Exception e){
				m_mainDeamon.m_mainApp.setErrorString("SQ:"+ e.getMessage() + " " + e.getClass().getName());
			}
		}
	}
	

}
