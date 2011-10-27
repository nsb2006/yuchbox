package com.yuchting.yuchdroid.client.mail;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yuchting.yuchdroid.client.R;

public class MailListAdapter extends BaseAdapter{
	
	public static class ItemHolder{
        View 		background;
        CheckBox	markBut;
        ImageView	groupFlag;
        TextView	subject;
        TextView	body;
        TextView	mailAddr;
        TextView	latestTime;
        
        int			preGroupId = -1;
        int			groupId;
        int			nextGroupId = -1;
        
        int			cursorPos; 
        
        public void updateView(int _groupFlag,String _subject,String _mailAddr,String _body,long _latestTime){
        	
        	setGroupFlag(_groupFlag);
            
            subject.setText(_subject);
            body.setText(_body);             
     		mailAddr.setText(_mailAddr);
     		
     		// time
     		//
     		sm_calendar.setTimeInMillis(System.currentTimeMillis());
     		final int t_currYear 		= sm_calendar.get(Calendar.YEAR);
     		
     		sm_calendar.setTimeInMillis(_latestTime);
     		final int t_year 		= sm_calendar.get(Calendar.YEAR);
     		
     		String t_time;
     		if(t_year == t_currYear){
     			t_time = sm_monthDayHourFormat.format(new Date(_latestTime));
     		}else{
     			t_time = sm_yearMonthDayFormat.format(new Date(_latestTime));
     		}
     		
     		latestTime.setText(t_time);
        }
        
        public void setGroupFlag(int _groupFlag){
        	
        	int t_backgroud = 0xf0f0f0;
        	if(_groupFlag == fetchMail.GROUP_FLAG_RECV_ATTACH
        		|| _groupFlag == fetchMail.GROUP_FLAG_RECV){
        		t_backgroud = 0xffffff;
        	}
        	background.setBackgroundColor(t_backgroud);
        	groupFlag.setImageResource(getMailFlagImageId(_groupFlag));
        }
    }
	
	public static int getMailFlagImageId(int _groupFlag){
		int t_id = R.drawable.mail_list_flag_recv;
    	switch(_groupFlag){
    	case fetchMail.GROUP_FLAG_RECV_ATTACH:
    		t_id = R.drawable.mail_list_flag_recv_attach;
    		break;
    	case fetchMail.GROUP_FLAG_RECV_READ:
    		t_id = R.drawable.mail_list_flag_recv_read;
    		break;
    	case fetchMail.GROUP_FLAG_RECV_READ_ATTACH:
    		t_id = R.drawable.mail_list_flag_recv_read_attach;
    		break;
    	case fetchMail.GROUP_FLAG_SEND_DRAFT:
    		t_id = R.drawable.mail_list_flag_send_draft;
    		break;
    	case fetchMail.GROUP_FLAG_SEND_ERROR:
    		t_id = R.drawable.mail_list_flag_send_error;
    		break;
    	case fetchMail.GROUP_FLAG_SEND_PADDING:
    		t_id = R.drawable.mail_list_flag_send_padding;
    		break;
    	case fetchMail.GROUP_FLAG_SEND_SENDING:
    		t_id = R.drawable.mail_list_flag_send_sending;
    		break;
    	case fetchMail.GROUP_FLAG_SEND_SENT:
    		t_id = R.drawable.mail_list_flag_send_sent;
    		break;
    	}
    	return t_id;
	}
	
	private int m_cursorIDIndex;
    private int m_cursorMarkIndex;
    private int m_cursorGroupFlagIndex;
    private int m_cursorSubjectIndex;
    private int m_cursorBodyIndex;
    private int m_cursorMailAddrIndex;
    private int m_cursorlatestTimeIndex;
	
	private LayoutInflater m_inflater;   	
	private Cursor			m_mainCursor;
	private Context		m_ctx;
	
	final static Calendar 	sm_calendar 	= Calendar.getInstance();
	final static Date		sm_timeDate 	= new Date();
	
    static SimpleDateFormat sm_yearMonthDayFormat = null;
	static SimpleDateFormat sm_monthDayHourFormat = null;
	
	public MailListAdapter(Context context,Cursor _cursor){
		m_ctx 			= context;
		m_inflater		= LayoutInflater.from(context);
		m_mainCursor	= _cursor;
		
		m_cursorIDIndex			= m_mainCursor.getColumnIndex(MailDbAdapter.KEY_ID);
		m_cursorMarkIndex		= m_mainCursor.getColumnIndex(MailDbAdapter.GROUP_ATTR_MARK);
		m_cursorGroupFlagIndex	= m_mainCursor.getColumnIndex(MailDbAdapter.GROUP_ATTR_GROUP_FLAG);
		m_cursorSubjectIndex	= m_mainCursor.getColumnIndex(MailDbAdapter.GROUP_ATTR_SUBJECT);
		m_cursorBodyIndex		= m_mainCursor.getColumnIndex(MailDbAdapter.GROUP_ATTR_LEATEST_BODY);
		m_cursorMailAddrIndex	= m_mainCursor.getColumnIndex(MailDbAdapter.GROUP_ATTR_ADDR_LIST);
		m_cursorlatestTimeIndex	= m_mainCursor.getColumnIndex(MailDbAdapter.GROUP_ATTR_LEATEST_TIME);

		
		if(sm_yearMonthDayFormat == null){
			sm_yearMonthDayFormat = new SimpleDateFormat("yyyy"+context.getString(R.string.mail_time_year)+
											"MM"+context.getString(R.string.mail_time_month)+
											"dd" + context.getString(R.string.mail_time_day));

			sm_monthDayHourFormat = new SimpleDateFormat("MM"+context.getString(R.string.mail_time_month)+
											"dd" + context.getString(R.string.mail_time_day) +
											" HH:mm");	
		}
		
	}
	

    public int getCount() {
        return m_mainCursor.getCount();
    }

    public Object getItem(int position) {
         return position;
    }

    public long getItemId(int position) {
        return position;
    }
    
    public View getView(int position, View convertView, ViewGroup parent) {

    	ItemHolder holder;
    	
        if(convertView == null) {
            convertView = m_inflater.inflate(R.layout.mail_list_item,new LinearLayout(m_ctx));
            
            holder = new ItemHolder();
            holder.background	= convertView.findViewById(R.id.mail_item);
            holder.groupFlag	= (ImageView)convertView.findViewById(R.id.mail_group_flag);
            holder.subject		= (TextView)convertView.findViewById(R.id.mail_subject);
            holder.body			= (TextView)convertView.findViewById(R.id.mail_body);
            holder.mailAddr		= (TextView)convertView.findViewById(R.id.mail_from_to);
            holder.latestTime	= (TextView)convertView.findViewById(R.id.mail_time);

            convertView.setTag(holder);
            
        }else{
            holder = (ItemHolder) convertView.getTag();
      
            holder.preGroupId = -1;
            holder.nextGroupId = -1;            
        }
        
        // fill the group data
        //
        m_mainCursor.moveToPosition(position);
        holder.groupId = m_mainCursor.getInt(m_cursorIDIndex);
        if(position != 0){
        	m_mainCursor.moveToPosition(position - 1);
        	holder.nextGroupId = m_mainCursor.getInt(m_cursorIDIndex);
        }
        
        if(position < m_mainCursor.getCount() - 1){
        	m_mainCursor.moveToPosition(position + 1);
        	holder.preGroupId = m_mainCursor.getInt(m_cursorIDIndex);
        }
        
        // fill the mail text
        //
        m_mainCursor.moveToPosition(position);
        
        holder.cursorPos = position;        
        holder.updateView(m_mainCursor.getInt(m_cursorGroupFlagIndex),
        				m_mainCursor.getString(m_cursorSubjectIndex), 
        				getShortAddrList(fetchMail.parseAddressList(m_mainCursor.getString(m_cursorMailAddrIndex).split(fetchMail.fsm_vectStringSpliter))), 
        				m_mainCursor.getString(m_cursorBodyIndex),
        				m_mainCursor.getLong(m_cursorlatestTimeIndex));
		
        return convertView;
    }
    
    public static String getShortAddrList(Address[] _list){
    	
    	StringBuffer t_display = new StringBuffer();
		int t_num = 0;
		for(int i = _list.length - 1;i >= 0 && t_num < 3;i--,t_num++){
			t_display.append(_list[i].m_name).append(fetchMail.fsm_vectStringSpliter);
		}
		t_display.append("(").append(_list.length).append(")");
		
		return t_display.toString();
    }
}

