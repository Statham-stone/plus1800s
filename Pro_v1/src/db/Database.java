package db;

import java.sql.Connection;        //���ݿ�����ʵ��
import java.sql.DriverManager;     //���ݿ����������࣬�����侲̬����getConnection���������ݿ��URL������ݿ�����ʵ��
import java.sql.Statement;         //�������ݿ�Ҫ�õ����࣬��Ҫ����ִ��SQL���
import java.util.ArrayList;
import java.util.Calendar;
import java.sql.ResultSet;         //���ݿ��ѯ�����
import java.sql.SQLException;

import java.util.Date;
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
    
    /* 
     * �������ݿ⣬ÿ��ʹ�ø��ֺ���֮ǰ������Ҫ���ñ�����
     */
	public String connect()                         
	{
		 try{
	            Class.forName(DRIVER_MYSQL);     //����JDBC����
	            connection = DriverManager.getConnection(URL,"root","1234");   //�������ݿ����Ӷ���
	            stmt = connection.createStatement();       //����Statement����
	            return "connect!";
	     }
		 catch (Exception e){
			 e.printStackTrace();
		 }
		 return "Success";
	}
	
	/*
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
	
	/* 12/1 �����¹��� �ڲ����ͳ����ֵ�ĸ���
	 * ��½ �����û���������
	 * ���ɹ��򷵻��û�ID
	 * ʧ���򷵻�-1
	 */
	public int checkUser(String username,String password)
	{
		String date = getTime();
		String temp = username + "' and password = '" + password;
		System.out.println(temp);
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
		
		return uid;
	}
	//++++++++++ע�� ��½ ����
	
	/* 11/7 ����ͨ��
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
	
	
	/* 11/7 ����ͨ��
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
	
	
	/* 11/7 ����ͨ��
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
					count++;
				}
				
			}
			ret = count + ret ;
		}
		catch (SQLException e){e.printStackTrace();}
		
		return ret;
	}
	
	/* 11/7 ����ͨ��
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
	
	/* 12/1 ���޸�
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
	
	/* 11/14 ����ͨ��
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
	
	/* 11/15����ͨ��
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
	
	/* ����ͨ��
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
			result = result.replace("~MyEvent~MyAssoc",""); //ȥ���¼���͹�����
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
	
	/* Ϳ������ڶ���
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
	
	/* 11/15 Ϳ�����������
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
	
	/* Ϳ��������
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
		
		try
		{
			for(int i=0;i<cnt_tables;i++)
			{
				String dbtname = getDBName(uid,tables[i]);
				String temp = "select * from "+ dbtname + " where Name like '%" + key + "%'";
				System.out.println(temp);
				res = stmt.executeQuery(temp);
				while(res.next())
				{
					result = result + "^" + tables[i] + "~" + tableID+ "_" + res.getString("TID") + "~" + res.getString(1);
				}
			}
			if('^'==result.charAt(0))
			{
				result = result.substring(1);
			}
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return result;
	}
	
	/* 11/15 Ϳ��������
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
	
	/* 12/1 ������
	 * ����û�ID��������״ͼ���ѶϢ��ѶϢΪ�û�����~�޸�����
	 * 
	 */
	public String Bar(String uid)
	{
		String mes = null;
		String check = "select * from stati" + uid ;
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
	
	/* 12/1 ������
	 * ����û�ID��������״ͼ���ѶϢ��ѶϢΪ�û���~ӵ�е���Ϣ�������������������¼���ͳ�Ʊ�
	 * 
	 */
	public String Pie(String uid)
	{
		String mes = null;
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
    
    /* 12/07����������
     * sqk����
     * ɾ��С�¼�
     * ������ȷ���û�ID,������С�¼�ID
     * 
     * ��ͳ�����ݲ���Ӱ��
     */
    public boolean deleteSEvent(String uid,String tablename,String TID)
    {
    	String dbtablename = getDBName(uid,tablename);
    	String delete = "delete from " + dbtablename + " where TID=" + TID;
    	try
    	{
    		stmt.execute(delete);
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
    	for(int i=0;i<mesin.length();i++)
    	{
    		insert = insert + mes[i] + ","; 
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
    	String delete = "delete from stati" + uid + " where EID=" + EID;
    	try
    	{
    		stmt.execute(delete);
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
    	ResultSet res = null;
		String result = "" ;

		String temp = "select EName,count(*),a.EID from event" + uid + " e join assoc" + uid +" a where a.EID = e.EID group by EName";
		
		try{
			res = stmt.executeQuery(temp);
			while(res.next())
			{
				result = result + "^" + res.getString(1) + "~" + res.getString(2) + "~" + res.getString(3);
			}
			result = result.substring(1); //ȥ���¼���͹�����
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
    	String temp = null;
    	String getmes = "select EName,ETime,count(*) from event" + uid + " e join assoc" + uid +" a where a.EID = e.EID and a.EID = "+ EID;
    	String select = "select TID,tableid,uname from assoc" + uid + " a join usertable u where a.tableid = u.ID and a.EID = " + EID;
    	ResultSet res = null;
    	ResultSet rest = null;
    	try
    	{
    		res = stmt.executeQuery(getmes);
    		while(res.next())
    		{
    			temp = temp + res.getString(1) + "~" + res.getString(2) + "~" + res.getString(3);
    		}
    		res = stmt.executeQuery(select);
    		while(res.next())
    		{
    			String tid = res.getString(1);
    			String tableid = res.getString(2);
    			String uname = res.getString(3);
    			temp = temp + "^" + tableid + "_" + tid + "~" + tid + "~" + uname + "~";
    			
    			String getse = "select Name from t" + tableid + " where TID = " + tid ;
    			String event_name = null;
    			rest = stmt.executeQuery(getse);
    			while(rest.next())
    			{
    				event_name = rest.getString(1);
    			}
    			temp = temp + event_name;
    		}
    	}
    	catch(SQLException e)
    	{
    		e.printStackTrace();
    	}
    	String mes = null;
    	return mes;
    }
    
    /* 12/07 ����������
     * sqk����
     * �����û�ID��������С�¼�ID
     * ���ظ�С�¼�ÿһ�У�����С�¼�ID
     */
	public String editRetOldInf(String uid,String tablename,String TID)
	{
		String dbtablename = getDBName(uid,tablename);
		String temp = "select * from " + dbtablename + " where TID =" + TID;
		
		ResultSet res = null;
		String mes = null;
		
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
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		return mes;
		
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
		String update_ck = " stati" + uid+ " where Time = " + date;
		try{
			String update = null;
			int exist = count(update_ck);
			if (exist==0)
			{
				update = "insert stati" + uid + " values(date,1)";
			}
			else
			{
				update = "update stati" + uid + " set Count = Count+1 where Time =" + date ;
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
}
