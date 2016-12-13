package db;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;        //���ݿ�����ʵ��
import java.sql.DriverManager;     //���ݿ����������࣬�����侲̬����getConnection���������ݿ��URL������ݿ�����ʵ��
import java.sql.Statement;         //�������ݿ�Ҫ�õ����࣬��Ҫ����ִ��SQL���
import java.util.ArrayList;
import java.util.Calendar;
import java.sql.ResultSet;         //���ݿ��ѯ�����
import java.sql.SQLException;

import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.text.SimpleDateFormat;

@SuppressWarnings("unused")

//12/01 ����Ķ���UID������int����ΪString
public class Database {
	
	private static String DRIVER_MYSQL = "com.mysql.jdbc.Driver";    //MySQL JDBC�����ַ���
    private static String URL = "jdbc:mysql://localhost:3306/USER";
    private static Statement stmt;
    private Connection connection = null;
    
    private int tableID  = 0;
    private int co_count = 0;
    
    /* 12/07�²���ͨ��
     * �������ݿ⣬ÿ��ʹ�ø��ֺ���֮ǰ������Ҫ���ñ�����
     */
	public String connect()                         
	{
		 try{
	            Class.forName(DRIVER_MYSQL);     //����JDBC����
	            connection = DriverManager.getConnection(URL,"root","statham+1s");   //�������ݿ����Ӷ���
	            stmt = connection.createStatement();       //����Statement����
	            return "connect!";
	     }
		 catch (Exception e){
			 e.printStackTrace();
		 }
		 return "Success";
	}
	
	/* 12/07�²���ͨ��
	 * �Ͽ����ݿ����� ͬ����Ҫ���е���
	 */
	public String close()
	{
		try{
			connection.close();
		}
		catch (Exception e){
			e.printStackTrace();
		}
		return "Success close";
	}
	
	//++++++++++ע�� ��½ ����
	//12/7 �²�������ͨ��
	
	/* 12/1 �����¹��� �ڲ����ͳ����ֵ�ĸ���
	 * ��½ �����û���������
	 * ���ɹ��򷵻��û�ID
	 * ʧ���򷵻�-1
	 */
	public int checkUser(String username,String password)
	{
		String date = getTime();
		String temp = username + "' and password = '" + password;
		//System.out.println(temp);
		int ID = findID(temp);

		return ID;
	}
	/* 12/1 ������ 
	 * �¸Ķ���ע��ʱ�ڲ��Զ�����4λ����
	 * ����ͨ��
	 * ����ע�� ���ע��ʧ���򷵻�-1�����򷵻����û���ID
	 */
	public int signUp(String username,String password)
	{
		String date = getTime();
		int count = findID(username);
		if(count != -1)
		{
			return -1;
		}
		int uid = count("user") + 1;    //��ѯ���е�user���ܸ���������õ����û���ID
		String insert_user = "insert user values(" + uid + ",'" + username + "','" + password + "')";   //�������û�
		
		int tableid = count("usertable") + 1 ; //�����µĴ��¼����ID
		String insert_table1 = "insert usertable values(" + tableid + "," + uid + ",'event" + uid + "','MyEvent',3)";
		String insert_table2 = "insert usertable values(" + (tableid+1) + "," + uid + ",'assoc" + uid + "','MyAssoc',3)"; 
		String insert_table3 = "insert usertable values(" + (tableid+2) + "," + uid + ",'stati" + uid + "','MyStati',2)"; 
		
		String create_table1 = "create table event" + uid+ "(EID int primary key not null,ETime Date,EName varchar(50))";
		String create_table2 = "create table assoc" + uid+ "(EID int not null, TID int not null,tableid int not null)";
		String create_table3 = "create table stati" + uid+ "(Time varchar(9) not null, Count int not null)";
		String insert_count = "insert stati" + uid + " values(" + date + ",0)";
		try{
			stmt.execute(insert_user);
			stmt.execute(insert_table1);
			stmt.execute(insert_table2);
			stmt.execute(insert_table3);
			
			stmt.execute(create_table1);
			stmt.execute(create_table2);
			stmt.execute(create_table3);
		}
		catch (SQLException e){
			e.printStackTrace();
		}
		String uID = uid + "";
		template(uID);
		return uid;
	}
	//++++++++++ע�� ��½ ����
	
	/* 12/07 �²���ͨ��
	 * 11/7 ����ͨ��
	 * ��鵱ǰ�û���ͼ�½��ı�������Ƿ�����ݿ��������˵����������� 
	 * û������ʱ�����Ϊ0
	 * 1���û������½�һ��Ϊmoney�ı������ʽΪ  
	 * 		1~Money
	 */
	public int checkTableName(String a)
	{
		String[] mes = a.split("~");
		String uid = mes[0];
		String tname = mes[1];
		
		ResultSet cnt = null;
		int n = 0;
		String temp = "select count(*) from usertable where UID = " + uid + " and uname='" + tname + "'" ;
    	try
        {
            cnt = stmt.executeQuery(temp);
            while(cnt.next())
            {
            	n = cnt.getInt(1);
            }
        } 
    	catch (SQLException e)
    	{
    		e.printStackTrace();
    	}		
		
		return n;     //nΪ0����ɹ�
	}
	
	
	/* 12/07 ����ͨ��
	 * 11/7 ����ͨ��
	 * ������ͬ����ʱ��ʹ�ñ�����������һ����
	 * �����û�ID���û��������������֣�������ÿ�е����֣�����
	 * ��1���û�Money���洢2�У���һ��Ϊ����Ϊ12��time���ڶ���Ϊ����Ϊ20��product
	 * 		1~Money~2~time~12~product~20
	 */
	public int createUserTable(String a)
	{
		String[] mes = a.split("~");
		
		String uid = mes[0];
		String uname = mes[1];
		int column_num = Integer.parseInt(mes[2]);
		
		//�����usertable��
		int cnt = count("usertable") + 1; 
		String temp_insert = "insert usertable values(" + cnt + "," + uid + ",'t" + cnt + "','" + uname + "'," + (column_num+1) + ")" ;
		
		
		String temp_create = "create table t" + cnt + "(";            //�����±����䣬�±���ΪtID,��usertable�е�10�ű�����Ϊt10
		 
		for(int i=0;i<column_num;i++)                                 //����Ҫ�����create���
		{
			temp_create = temp_create + mes[3+2*i] + " varchar(" + mes[4+2*i] + "),";
		}
		temp_create = temp_create + "TID int primary key) ";  //�������������û�����event��
		
		System.out.println(temp_create);
		try{
			stmt.execute(temp_insert);   //�����±��¼��usertable
			stmt.execute(temp_create);   //�����±�
			return 0;
		}
		catch (SQLException e){e.printStackTrace();return 1;}
	}
	
	
	/* 12/07 �²���ͨ��
	 * 11/7 ����ͨ��
	 * �����û�ID
	 * ����ֵ�Ǹ��û���table����~ÿ��table������
	 */
	public String findUserTable(String uid)
	{
		/* sqk's requirement 2016/10/30
		 * input userid
		 * return table_count ~ tablename(user)1 ~ tablename(user)2 ~ ������������������������ 
		 * use ~(temporary) 
		 */
		ResultSet res = null;
		String temp = "select * from usertable where UID =" + uid;
		String ret = "";
		
		int count = 0;
		try {
			res = stmt.executeQuery(temp);
			while(res.next())
			{
				if(count!=0 && count!=1 && count != 2)//�����ع�����ʹ��¼��� ,������ͳ�Ʊ�
				{
					ret = ret + "~" +res.getString(4);
					
				}
				count++;
			}
			ret = (count-3) + ret ;
		}
		catch (SQLException e){e.printStackTrace();}
		
		return ret;
	}
	
	/* 12/07 �²���ͨ��
	 * 11/7 ����ͨ��
	 * 11/14�޸Ĵ����� ͨ��
	 * �����û�id�ͱ��� ���ر�����ԣ�����ID��
	 */
	public String findTableColumn(String uid, String tablename)
	{
		/* sqk's requirement 2016/10/31
		 * in userid && tablename(user)
		 * return columncount ~ column1 ~ length1 ~ column2 ~ length2������������������������ 
		 * use ~(temporary) 
		 */
		ResultSet res = null;
	
		String ret = "";
		
		try {
			
			String dbtablename = getDBName(uid,tablename);
			ret = ret + co_count ;
			String temp = "desc " + dbtablename;
			
			res = null;
			
			res = stmt.executeQuery(temp);
			
			while(res.next())
			{
				ret = ret + "~" + res.getString(1) + "~" + res.getString(2);
			}
			ret = ret.replace("int(","");
			ret = ret.replace("varchar(","");
			ret = ret.replace(")","");
			
			return ret;
		}
		catch (SQLException e){e.printStackTrace();}
		return "";
	}
	
	/* 12/07 �²���ͨ��
	 * 12/1 ���޸�
	 * 11/14 ����ͨ��
	 * ����һ��С�¼�
	 * �����û�ID ���� ������Ϣ������ʹ��~�ָ
	 * ����ɹ�����1 ���򷵻�-1
	 * 
	 */
	public int insertSEvent(String uid,String tablename,String mes)
	{
		ResultSet res = null;

		int eid = 0;
		try {
			String dbtablename = getDBName(uid,tablename);//�����¼�¼�ı��ʵ������

			String temp = "select max(TID) from " + dbtablename ;
			res = stmt.executeQuery(temp);
			while(res.next())
			{
				eid = res.getInt(1) + 1;
			}
			mes = mes.replace("~","','");
			temp = "insert " + dbtablename + " values('" + mes +"'," + eid + ")" ;
			stmt.execute(temp);
			
			updateStati(uid);
			return 1;
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			return -1;
		}
	}
	
	/* 12/07 �²���ͨ��
	 * 11/14 ����ͨ��
	 * ��ȡһ���û�ID��һ�������������ظñ����������,Ĭ�Ϸ���ID
	 * ���ظ�ʽΪ ����~��һ���һ��~��һ��ڶ���~����~�ڶ����һ��~����
	 */
	public String findSEvent(String uid, String tablename)
	{
		ResultSet res = null; 
		String dbtablename = getDBName(uid,tablename);
		
		String mes= "";
		
		String temp_desc = "desc " + dbtablename;
		
		String temp_find = "select * from " + dbtablename ;
		
		//System.out.println("co_count = " + co_count);
		//System.out.println(temp_find);
		try
		{
			int cnt = 0;
			res = stmt.executeQuery(temp_find);
			while(res.next())
			{
				cnt++;
				//System.out.println("count = " + cnt);
				for(int i=1;i<=co_count;i++)
				{
					mes= mes + "~" + res.getString(i); 
					//System.out.println(i+mes);
				}
			}
			mes = cnt + mes;
			//System.out.println(mes);
			return mes;
		}
		catch(SQLException e)
		{
			e.printStackTrace();
			return "";
		}
	}
	
	/* 12/07 �²���ͨ��
	 * 11/15����ͨ��
	 * ��������Ϊһ���û�ID��һ��usertablename
	 * ���ر����������ӵ���������
	 * ����IDֵ�����������ݣ�
	 * �������迼�Ǹñ����ݲ������������ ����ֵͬ����findSEvent��ͬ
	 */
	public String findSEventN5(String uid, String tablename)
	{
		ResultSet res = null; 
		String dbtablename = getDBName(uid,tablename);
		
		String mes= "";
		
		String temp_desc = "desc " + dbtablename;
		
		String temp_find = "select * from " + dbtablename + " order by TID desc limit 5";
		
		try
		{
			int count = 0;
			res = stmt.executeQuery(temp_find);
			while(res.next())
			{
				count++;
				for(int i=1;i<=co_count;i++)
				{
					mes= mes + "~" + res.getString(i); 
				}
			}
			mes = count + mes;
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return mes;
	}
	
	/* 12/07����ͨ��
	 * Ϳ������1st
	 * 11/15 ������
	 * ����Ϊ�û�ID
	 * ���Ϊ���û����б��������¼����������⣩
	 */
	public String tableBrief(String uid)
	{
		ResultSet res = null;
		String temp = "select uname from usertable where UID= " + uid;
		String result = ""; 
		try{
			res = stmt.executeQuery(temp);
			while(res.next())
			{
				result = result + "~" + res.getString(1);
			}
			result = result.replace("~MyEvent~MyAssoc~MyStati",""); //ȥ���¼���͹�����,ͳ�Ʊ�
			if('~'==result.charAt(0))
			{
				result =result.substring(1);
			}
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		return result;
	}
	
	/* 12/07 ����ͨ��
	 * Ϳ������ڶ���
	 * 11/15 ������ ������DB���
	 * ���Խ�������Һ���һ�ξ�д�����ҶԴ˱�ʾ����
	 * ����Ϊ�û�ID
	 * ����Ϊ�¼�����~����С�¼�����^����
	 */
	public String eventBrief(String uid)
	{
		ResultSet res = null;
		String result = "" ;

		String temp = "select EName,count(*) from event" + uid + " e join assoc" + uid +" a where a.EID = e.EID group by EName";
		
		try{
			res = stmt.executeQuery(temp);
			while(res.next())
			{
				result = result + "^" + res.getString(1) + "~" + res.getString(2);
			}
			result = result.substring(1); //ȥ���¼���͹�����
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		return result;
	}
	
	/* 12/07����ͨ��
	 * 11/15 Ϳ�����������
	 * ����ͨ��
	 * ����Ϊ�û�id~����
	 * ���ΪС�¼���~С�¼�ȫ��ID^����
	 * С�¼�ȫ��ID�ɱ���IDƴ��С�¼���TIDƴ��
	 */
	public String tableContent(String uidPLUStname)
	{
		ResultSet res = null;
		String result = "";
		String[] mes = uidPLUStname.split("~");
		
		String uid = mes[0];
		String uname = mes[1];
		
		String dbtablename = getDBName(uid,uname);
		int tid = tableID;
		
		String temp = "select * from " + dbtablename ;
		try
		{
			res = stmt.executeQuery(temp);
			while(res.next())
			{
				result = result + "^" + res.getString(1) + "~" + tid + "_"  +res.getString("TID");
			}
			result = result.substring(1);
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		return result ;
	}
	
	/* Ϳ�������� old version
	 * 11/15�����
	 * ����Ϊ�û�id��key
	 * ���Ϊ����~ΨһID~С�¼�����
	 */
	public String searchRequest(String uid, String key)
	{
		ResultSet res = null;
		String result = "";
		String[] tables = tableBrief(uid).split("~");
		int cnt_tables = tables.length;
		
		for(int i=0;i<cnt_tables;i++)
		{
			String thistable = searchInTable(uid,tables[i],key);
			if(!("".equals(thistable)))
			{
				result += "^" + thistable;
			}
		}
		if(!("".equals(result)))
		{
			result = result.substring(1);
		}
		return result;
	}
	
	
	
	
	/* 12/07 ����ͨ��
	 * 11/15 Ϳ��������
	 * ����ͨ��
	 * ����Ϊ�û�ID~���¼�EName~ETime~С�¼�ȫ��ID~С�¼�ȫ��ID
	 * ���Ϊ���¼�ID
	 */
	public int submitEvent(String mes)
	{
		String[] mesin   = mes.split("~");
		String 	 uid     = mesin[0];
		String 	 EName   = mesin[1];
		String 	 ETime   = mesin[2];
		int 	 t_count = mesin.length;
		
		for(int i=3;i<t_count;i++)
		{
			if (!checkTableID(mesin[i],uid))
				return -1;
		}
		
		ResultSet res = null;
		String find_eid = "select max(EID) from event" + uid ;
		int eid = 0;
		try{
			res = stmt.executeQuery(find_eid);
			while(res.next())
			{
				eid = res.getInt(1) + 1;
			}
			String insert_e = "insert event" + uid + " values(" + eid + "," + ETime + ",'" + EName + "')";
			stmt.execute(insert_e);
			
			for(int j=3;j<t_count;j++)
			{
				String[] mestemp = mesin[j].split("_");
				String insert = "insert assoc" + uid + " values(" + eid + "," + mestemp[1] + "," + mestemp[0] + ")";
				stmt.execute(insert);
			}
			updateStati(uid);
			return eid;
		}
		catch(SQLException e)
		{
			e.printStackTrace();
			return -1;
		}
		
	}
	
	/* 12/07 �²���ͨ��
	 * 12/1 ������
	 * ����û�ID��������״ͼ���ѶϢ��ѶϢΪ�û�����~�޸�����
	 * 
	 */
	public String Bar(String uid)
	{
		complete(uid);
		String mes = "";
		String check = "select * from stati" + uid + " order by Time";
		try{
			ResultSet res = stmt.executeQuery(check);
			while (res.next())
			{
				mes = mes + "~" + res.getString(1) + "~" + res.getString(2);
			}
			if(mes.length()>1)
			{
				mes = mes.substring(1);
			}
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		return mes;
	}
	
	/* 12/11 �²���ͨ�� 
	 * �������ҵĽ�  Barͼ����ָ��ĳһʱ�����֪������
	 * 
	 * ����û�ID������,��������ȫ����ģ��������Ϳ���
	 * ������״ͼ�ڶ�Ӧ����ʱ��������ѶϢ��ѶϢΪ�û�����~�޸�����
	 */
	public String Bar(String uid,String days)
	{
		String mes = "";
		
		//Ƕ��order�ı�����Ϊ�˷����ٷ���õ�����
		String check = "select * from (select * from stati" + uid + " order by Time desc limit " + days + " ) a order by Time";
		
		try{
			ResultSet res = stmt.executeQuery(check);
			while (res.next())
			{
				mes = mes + "~" + res.getString(1) + "~" + res.getString(2);
			}
			if(mes.length()>1)
			{
				mes = mes.substring(1);
			}
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		return mes;
	}
	
	/* 12/07 �²���ͨ��
	 * 12/1 ������
	 * ����û�ID��������״ͼ���ѶϢ��ѶϢΪ�û���~ӵ�е���Ϣ�������������������¼���ͳ�Ʊ�
	 * 
	 */
	public String Pie(String uid)
	{
		String mes = "";
		String[] tables = findUserTable(uid).split("~");
		if(tables.length>1)
		{
			for(int i = 1; i< tables.length; i++)
			{
				String dbtname_i = getDBName(uid,tables[i]);
				int cn = count(dbtname_i);
				mes = mes + "~" + tables[i] + "~" + cn ; 
			}
			mes = mes.substring(1);
		}
		return mes;
	}
	
	
	//use by statham.
    public String statham_column(String uid,String tablename)
    {
    	String big=findTableColumn(uid, tablename);
    	String[] arr=big.split("~");
    	String littleString="";
    	littleString=arr[1];
    	for(int i=3;i<arr.length-2;i=i+2)
    	{
    		littleString=littleString+"~"+arr[i];
    	}
    	return littleString;
    }
    
    /* 12/07 �²���ͨ��
     * sqk����
     * ɾ��С�¼�
     * ������ȷ���û�ID,������С�¼�ID
     * ��ͳ�����ݲ���Ӱ��
     */
    public boolean deleteSEvent(String uid,String tablename,String TID)
    {
    	String dbtablename = getDBName(uid,tablename);
    	String delete = "delete from " + dbtablename + " where TID=" + TID;
    	String delete_event = "delete from assoc" + uid + " where TID=" + TID;
    	try
    	{
    		stmt.execute(delete);
    		stmt.execute(delete_event);
        	updateStati(uid);
    		return true;
    	}
    	catch(SQLException e)
		{
			e.printStackTrace();
			return false;
		}
    }
    
    /* 12/07 �²���ͨ��
     * sqk����
     * �޸�С�¼�
     * Ϊ�˷��㴦���˴������ݿ�����ı�����ɾ��������
     * ������ȷ���û�ID��������С�¼�ID����С�¼�������Ϣ(��~����)
     * ����true����ɹ�
     * 
     * ��ͳ�����ݲ���Ӱ��
     */
    public boolean updateSEvent(String uid,String tablename,String TID,String mesin)
    {
    	String[] mes = mesin.split("~");
    	
    	String dbtablename = getDBName(uid,tablename);
    	String delete = "delete from " + dbtablename + " where TID = " + TID ;
    	String insert = "insert "+ dbtablename + " values(" ;
    	for(int i=0;i<mes.length;i++)
    	{
    		insert = insert + "'" + mes[i] + "',"; 
    	}
    	insert = insert + TID + ")";
    	
    	try
    	{
    		stmt.execute(delete);
    		stmt.execute(insert);
    		updateStati(uid);
        	return true;
    	}
    	catch(SQLException e)
		{
			e.printStackTrace();
			return false;
		}
    }
    
    /* 12/07 ����������
     * Ϳ������
     * ɾ��һ�����¼�
     * �����û�ID�����¼�ID
     * ����true����ɹ�
     * 
     * ��ͳ�����ݲ���Ӱ��
     */
    public boolean deleteBEvent(String uid,String EID)
    {
    	String delete = "delete from event" + uid + " where EID=" + EID;
    	String delete2 = "delete from assoc" + uid + " where EID=" + EID;
    	try
    	{
    		stmt.execute(delete);
    		stmt.execute(delete2);
    		updateStati(uid);
    		return true;
    	}
    	catch(SQLException e)
		{
			e.printStackTrace();
			return false;
		}
    }
    
    /* 12/07 ����������
     * Ϳ������ 
     * eventBrief���װ�
     * ������������EID
     * ����Ϊ�û�ID
     * ���Ϊ���¼���~���¼�����С�¼�����~���¼�ID^����
     */
    public String eventBriefN(String uid)
    {    	
    	ResultSet res ;
		String result = "" ;

		String temp = "select EName,count(*),a.EID from event" + uid + " e join assoc" + uid +" a where a.EID = e.EID group by EName";
		
		try{
			res = stmt.executeQuery(temp);
			while(res.next())
			{
				result = result + "^" + res.getString(1) + "~" + res.getString(2) + "~" + res.getString(3);
			}
			result = result.substring(1); //��һ��^
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		return result;
    }
    
    /* 12/07 ����������
     * Ϳ������
     * �����û�ID�����¼�ID
     * ���ش��¼���~ʱ��~����С�¼�����^С�¼�1ȫ��ID~С�¼�1ID~С�¼�1��������~С�¼�1name^С�¼�2ȫ��ID~С�¼�2ID~С�¼�2��������~С�¼�2name����
     * ����Ϳ��ʹ��ȫ��ID����ʾС�¼�����������С�¼�name��������С�¼�ID������������SQK
     */
    public String showBEvent(String uid,String EID)
    {
    	String result = "";
    	String getmes = "select EName,ETime,count(*) from event" + uid + " e join assoc" + uid +" a where a.EID = e.EID and a.EID = "+ EID;
    	String select = "select TID,tableid,uname from assoc" + uid + " a join usertable u where a.tableid = u.ID and a.EID = " + EID;

    	try
    	{
    		ResultSet res = stmt.executeQuery(getmes);
    		while(res.next())
    		{
    			result = res.getString(1) + "~" + res.getString(2) + "~" + res.getString(3);
    		}
    	}
    	catch(SQLException e)
    	{
    		e.printStackTrace();
    	}
    	
    	try
    	{
    		ResultSet res = stmt.executeQuery(select);
    		while(res.next())
    		{
    			String tid = res.getString(1);
    			String tableid = res.getString(2);
    			String uname = res.getString(3);
    			String thingname = getSEName(tableid,tid);
    			result = result + "^" + tableid + "_" + tid + "~" + tid + "~" + uname + "~" + thingname;
    		}
    	}
    	catch(SQLException e)
		{
			e.printStackTrace();
		}
    	
    	return result;
    }
    
    /* 12/07 �²���ͨ��
     * sqk����
     * �����û�ID��������С�¼�ID
     * ���ظ�С�¼�ÿһ�У�����С�¼�ID
     */
	public String editRetOldInf(String uid,String tablename,String TID)
	{
		String dbtablename = getDBName(uid,tablename);
		String temp = "select * from " + dbtablename + " where TID =" + TID;
		
		ResultSet res = null;
		String mes = "";
		
		try
		{
			res = stmt.executeQuery(temp);
			while(res.next())
			{
				for (int i=1;i<=co_count;i++)
				{
					mes = mes + "~" + res.getString(i);
				}
			}
			if(mes.length()>1)
			{
				mes = mes.substring(1);
			}
			return mes;
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		return mes;
		
	}
	
	/* 12/12 Ϳ�����ϲ���ͨ��
	 * 12/08 ����������
	 * �û�����
	 * ����Ϊ�û�ID�������ַ���
	 * ����һ������ֵ��ʾ�Ƿ�ɹ�
	 */
	public boolean comment(String uid,String com)
	{
		boolean suc = false;
		com = com.replace("~","','");
		com = "'" + com + "'" ;
		
		String insert = "insert comment values(" + uid + "," + com + ")";
		
		try
		{
			stmt.execute(insert);
			suc = true;
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		return suc;
	}
	
	/* 12/08 �²���ͨ��
	 * ����csv�ļ�����
	 * ����ֵΪ�û�ID�������˵ı���
	 * ����ֵΪ�ַ��� ��ʽΪ �� �û�����^csv�ļ�����
	 */
	public String download(String uid,String uname)
	{
		ResultSet res = null;
		String dbtablename = getDBName(uid,uname);
		
		String result = "";
		String detail = "desc " + dbtablename;
		String select = "select * from "+ dbtablename ;
		
		try
		{
			res = stmt.executeQuery(detail);
			while(res.next())
			{
					result = result + "," + res.getString(1);
			}
			result = result.substring(1);
			result = result.replace(",TID", "");
			res = stmt.executeQuery(select);
			while(res.next())
			{
				result = result + "\n" ;
				for(int i=1;i<co_count;i++)
				{
					result = result + "," + res.getString(i);
				}
			}
			result = result.replace("\n,", "\n");
			result = uname + "^" + result;
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		return result;
	}
	
	/* 12/12 �°汾����ͨ��
	 * ���£��������������ݽ�����ȫ����룬����ֱ�Ӷ�ʧ
	 * 
	 * 12/08 �²���ͨ��
	 * ���ϴ���csv�ļ�������� ��ӽ����ݿ����б���
	 * �ɹ�ʱ���ظñ�����������е��ú���ʹ�ñ������û�ID�鿴�������
	 * 12/08�汾 ע�⣺��� ��ʵ��������3������3�е��н�ֻ����ǰ�������ݣ�����3���򲻱�������
	 * 
	 * ʧ�ܷ���"false"
	 */
	public String upload(String uid, String uname, File csv_file)
	{
		String dbtablename = getDBName(uid,uname);
		
		String str = "";
		try 
        {
            BufferedReader in_br = new BufferedReader(new FileReader(csv_file));
            
            while ((str = in_br.readLine()) != null) 
            {
                  String[] columns = str.split(",");
                  String insert = "";
                  if(columns.length<co_count-1)
                  {
                	  for(int x = columns.length ; x < co_count ; x++)
                	  {
                		  str = str + ", ";
                	  }
                	  columns = str.split(",");
                  }

               	  for(int i=0;i<co_count-1;i++)
                  {
                	  if("".equals(columns[i]))
                	  {
                		  columns[i] = " ";
                	  }
                	  insert = insert + "~" + columns[i];
                  }
                  insert = insert.substring(1);
                                  
                  insertSEvent(uid,uname,insert);
            }
            in_br.close();
        } 
        catch(IOException e) 
        {
            e.getStackTrace();
            return "false";
        }
		return uname;
	}
	
	
	/* 12/12 �ϴ��ļ��Ľ��װ� ������
	 * ���ף������ļ���һ��ȷ���������
	 * �������������������Ĳ�ȫ�ո񣬳�����ֻȡ�±��������������������
	 * �����û�id���������±������������ļ����������ļ�
	 * �����±��� �� false
	 * �����и����û�id������ʹ�ú����鿴�ñ�����
	 */
	public String uploadNewTable(String uid,String newTableName,File myfile)
	{
		String str = "";
		try 
        {
			BufferedReader in_br = new BufferedReader(new FileReader(myfile));
            
            int l_count = 0;      //��¼���ڶ�������
            int lines = 0;        //��¼�±������
            while ((str = in_br.readLine()) != null) 
            {
            	l_count++;
            	String[] columns = str.split(",");
            
            	//��ͷ��Ϣ
            	if(l_count ==1) 
                {
                	lines = columns.length;
                	str = uid + "~" + newTableName + "~" + lines + "~" + str.replace(",", "~50~")+ "~50";
                	createUserTable(str);
                	continue;
                }
                
                //��ȥ��ͷ��������
                String insert = "";
                if(columns.length<lines)   //����������ȫ������ ��ȫ
                {
                	for(int x = columns.length + 1; x <= lines ; x++)
                	{
                		str = str + ", ";
                	}
                	columns = str.split(",");
                	System.out.println(str);
                }
                
                for(int i=0;i<lines;i++)   //����������Զ����Զ��������
                {
                	if("".equals(columns[i]))
	               	{
                		columns[i] = " ";
	               	}
	               	insert = insert + "~" + columns[i];
	            }
	            insert = insert.substring(1);
                                  
                insertSEvent(uid,newTableName,insert);
            }
            in_br.close();
        } 
        catch(IOException e) 
        {
            e.getStackTrace();
            return "false";
        }
		return newTableName;
	}
    
	/* private���� ����ֱ��ʹ�û��޸ģ�
	 * ����ṩ��С�¼�ΨһID�Ƿ����
	 */
	private boolean checkTableID(String teid ,String uid)
	{		
		ResultSet res = null;
		String[] mes = teid.split("_");//tableid _ eventid
		String table_exist = " usertable where UID=" + uid + " and ID= " + mes[0] ; 
		
		int exist = count(table_exist);
		if(exist==-1)
		{
			return false;
		}
		exist = count(" t"+mes[0] + " where TID=" + mes[1]) ;
		if(exist == -1)
		{
			return false;
		}
		return true;
	}
	
	/* private���� ����ֱ��ʹ�û��޸ģ�
	 * ���溯������ת�� ����user��tablename �� ʵ�ʴ���DB�е�tablename
	 * ͬʱ��ѯ���ñ������
	 * private �� ��Ա����ֵ co_count �����޸�
	 * tablename����Ϊ����ֵ 
	 */
	private String getDBName(String uid, String user_name)
	{
		ResultSet res = null;
		String temp = "select * from usertable where UID = " + uid + " and uname = '"  + user_name + "'";
		String db_name = "";
		
		try {
			res = stmt.executeQuery(temp);
			while(res.next())
			{
				tableID  = res.getInt(1);
				db_name  = res.getString(3);
				co_count = res.getInt(5);
			}
		}
		catch (SQLException e){e.printStackTrace();}
		
		return db_name;
	}

	/* 11/7 ����ͨ��
	 * private���� ����ֱ��ʹ�û��޸ģ�����
	 * ���溯����������user�����ҵ������������û�����
	 * �������û���������ʱ���������Ӧ�ñ�ƴ��Ϊ username + "' and passeword='" + password 
	 * 		����֤root��1234�Ƿ���� root' and password = '1234 ע�����߸�ȱ��һ������
	 * �������û�������Ƿ���ͬ���û����ڣ�ֻ��Ҫ�����û��� 
	 * 		����֤root�Ƿ���ڣ�����root
	 * �û������벻ƥ����߲�����ͬ���û�ʱ ������-1������ʱ��Ϊ������
	 */
	private int findID(String username)
	{
		int ID = -1;
		String temp = "select ID from user where name='" + username + "'" ;
		ResultSet rs;
		try {
			rs = stmt.executeQuery(temp);
			while(rs.next())
	        {
	        	ID = rs.getInt(1);
	        }
		} 
		catch (SQLException e){e.printStackTrace();}
		
		return ID;
	}
	
	/* private���� ����ֱ��ʹ�û����޸ģ�����
	 * ������ѯ���� ���в������
	 * �������ֱ��Ϊ���� ������ڲ�ѯ��ǰ����Ԫ�����
	 * ��������������when����������ɸѡ
	 */
	private int count(String tname) {
		ResultSet cnt = null;
		int n = 0;
		String temp = "select count(*) from " + tname ;
    	try
        {
            cnt = stmt.executeQuery(temp);
            while(cnt.next())
            {
            	n = cnt.getInt(1);
            }
        } 
    	catch (SQLException e){e.printStackTrace();}
    	return n;
    }
	
	/* private���� ����ֱ��ʹ�û����޸ģ�����
	 * 12/07  ������
	 * ��ȡʱ�� Ϊ��λ yyyyMMdd
	 */
	private boolean updateStati(String uid)
	{
		String date = getTime();
		String update_ck = " stati" + uid+ " where Time = '" + date + "'";
		try{
			String update = null;
			int exist = count(update_ck);
			if (exist==0)
			{
				update = "insert stati" + uid + " values('" + date + "' ,1)";
			}
			else
			{
				update = "update stati" + uid + " set Count = Count+1 where Time ='" + date  + "'";
			}
			stmt.execute(update);
		}
		catch (Exception e){
			e.printStackTrace();
		}
		return true;
	}

	/* private���� ����ֱ��ʹ�û����޸ģ�����
	 * 12/07 ����ͨ��
	 * ��ȡʱ�� Ϊ��λ yyyyMMdd
	 */
	private String getTime() 
	{
		String date = new SimpleDateFormat("yyyyMMdd").format(Calendar.getInstance().getTime());
		return date;
	}
	
	private String getSEName(String tableid,String tid)
	{
		String result = "";
		try {
			Statement st = connection.createStatement();
			
			ResultSet rest = null;
			String sel = "select * from t" + tableid + " where TID=" + tid;
			rest = st.executeQuery(sel);
			while(rest.next())
			{
				result = rest.getString(1);
			}
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		return result;
	}
	
	
	/* 12/11 ����ͨ��
	 * ����private���� ѭ�����Լ���һ��ãȻ ����Ķ���ֱ�ӵ���
	 * ��ȫ����ͳ��0������
	 */
	private boolean complete(String uid)
	{
		int[] mday = {0,31,28,31,30,31,30,31,31,30,31,30,31};
		String temp = "select min(Time) from stati" + uid ;
		ResultSet res = null;
		String min = "";
		String max = getTime();
		try
		{
			res = stmt.executeQuery(temp);
			while(res.next())
			{
				min = res.getString(1);
			}
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		int maxy = Integer.parseInt(max.substring(0,4));
		int miny = Integer.parseInt(min.substring(0,4));
		int maxm = Integer.parseInt(max.substring(4, 6));
		int minm = Integer.parseInt(min.substring(4, 6));
		int maxd = Integer.parseInt(max.substring(6));
		int mind = Integer.parseInt(min.substring(6));
		
		if(maxy==miny)//ͬ������ ��ȫ
		{
			if(maxm==minm)//ͬ�� ֻ��ȫ����
			{
				for(int i = mind+1 ; i <= maxd ; i++)
				{
					String d = "";
					if(i<10)
					{
						d = min.substring(0,6) + "0" + i;
					}
					else{
						d = min.substring(0,6) + i;
					}
					dateInsert(uid,d);
				}
			}
			else//��ͬ�� ��ȫ�����µ�������Ϣ
			{
				for(int i = minm ; i <= maxm ; i++)
				{
					int days = mday[i];
					for(int j = 1 ; j <= days ; j++ )
					{
						String d = "";
						if(i<10 && j<10) 
						{
							d = min.substring(0, 4) + "0" + i + "0" + j; 
						}
						else if(i<10 && j>9)
						{
							d = min.substring(0, 4) + "0" + i + "" + j; 
						}
						else if(i>9 && j<10)
						{
							d = min.substring(0, 4) + "" + i + "0" + j; 
						}
						else
						{
							d = min.substring(0, 4) + "" + i + "" + j; 
						}
						
						dateInsert(uid,d);
					}
				}
			}
		}
		else//��ͬ��� ��ȫ������������ ûʲô�ð취 �ĺ���
		{
			for(int i = miny ; i <= maxy ;i++)
			{
				for(int j = 1 ; j < 12 ; j++ ) 
				{
					for(int k = 1; k <= mday[j] ;k++)
					{
						String d = "";
						if(j<10 && k<10) 
						{
							d = i + "0" + j + "0" + k; 
						}
						else if(j<10 && k>9)
						{
							d = i + "0" + j + "" + k; 
						}
						else if(i>9 && j<10)
						{
							d = i + "" + j + "0" + k; 
						}
						else
						{
							d = i + "" + j + "" + k; 
						}
						dateInsert(uid,d);
					}
				}
			}
		}
		
		//�����Ͽ�ʼɾ��
		String delete = "delete from stati" + uid + " where Time not between " +  min + " and " + max;
		try
		{
			stmt.execute(delete);
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		return true;
	}
	
	/* 12/11 �²���ͨ��
	 * ����һ�����ڵ�ͳ�������Ƿ���ڣ�����������Ӳ�����Ϊ0
	 * ���ھͲ����ˡ���
	 * ����private���� ����ֱ�ӵ��� Ҳ�벻Ҫ�޸�
	 */
	private boolean dateInsert(String uid, String tdate)
	{
		try {
			Statement stmt1 = connection.createStatement();

			String update_ck = " stati" + uid+ " where Time = '" + tdate + "'";
		
			String update = null;
			int exist = count(update_ck);
			if (exist==0)
			{
				update = "insert stati" + uid + " values('" + tdate + "' ,0)";
				stmt1.execute(update);
			}
		}
		catch (Exception e){
			e.printStackTrace();
		}
		return true;
	}
	
	/* 12/13 ���װ�
	 * private���� ����Ķ���ֱ��ʹ��
	 * ����һ������ ĳ���ؼ��ֵ�������Ϣ
	 * ��������Ϊ����~�����ID~���һ������
	 */
	private String searchInTable(String uid, String tname,String key)
	{
		Set<String> things = new HashSet<String>();
		ResultSet res = null;
		String dbtablename = getDBName(uid,tname);
		int lines = co_count;
		
		String tableid = dbtablename.replace("t", "");
		
		String line_names = "";
		
		String find_lines = "desc " + dbtablename;
		
		String result = "";
		
		try{
			res = stmt.executeQuery(find_lines);
			
			while(res.next())
			{
				line_names += "~" + res.getString(1);
			}
			line_names = line_names.substring(1);
			
			String[] lnames = line_names.split("~");
			
			for(int i = 1 ; i < lines ; i++)
			{
				String small_se = "select * from " + dbtablename + " where " + lnames[i-1] + " like '%" + key + "%'";
				res = stmt.executeQuery(small_se);
				if(res.wasNull())
				{
					continue;
				}
				while(res.next())
				{
					String ttemp = "^" + tname + "~" + tableid + "_" + res.getString(lines) + "~" + res.getString(1);
					things.add(ttemp);
				}
			}
			if(things.isEmpty())
			{
				return "";
			}
			Iterator<String> it=things.iterator();
			result = "";
		    while(it.hasNext())
	       {
	           result = result + it.next();
	       }
		   result =result.substring(1);
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		return result;
	}
	
	private void template(String uid)
	{
		String outlays = uid + "~Outlays~4~Name~50~Date~50~Amount~50~Comment~50";
		String income = uid + "~Income~4~Name~50~Date~50~Amount~50~Comment~50";
		String meals = uid + "~Meals~4~Date~50~Amount~50~Place~50~Food~50";
		String books = uid + "~Books~5~Name~50~Author~50~Date~50~TimeLimit~50~Place~50";
		String friends = uid + "~Friends~3~Name~50~Mobile~50~Birthday~50";
		
		createUserTable(outlays);
		createUserTable(income);
		createUserTable(meals);
		createUserTable(books);
		createUserTable(friends);
		
		String insert1 = "Dinner~2016-12-01~89~had dinner with Sam";
		String insert2 = "2016-12-01~KFC~89~Chips";
		String insert3 = "Sam~13xxxxxxxxx~19960101";
		
		insertSEvent(uid,"Outlays",insert1);
		insertSEvent(uid,"Meals",insert2);
		insertSEvent(uid,"Friends",insert3);
		
		String[] ids1 = searchRequest(uid,"KFC").split("~");
		String[] ids2 = searchRequest(uid,"Chips").split("~");
		String[] ids3 = searchRequest(uid,"1996").split("~");
		
		String id1 = ids1[1];
		String id2 = ids2[1];
		String id3 = ids3[1];
		
		String bigEvent = uid + "~KFC~20161201~"+id1+"~"+id2+"~"+id3;
		System.out.println(bigEvent);
		
		submitEvent(bigEvent);
	}
}
