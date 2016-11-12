package db;

import java.sql.Connection;        //���ݿ�����ʵ��
import java.sql.DriverManager;     //���ݿ����������࣬�����侲̬����getConnection���������ݿ��URL������ݿ�����ʵ��
import java.sql.Statement;         //�������ݿ�Ҫ�õ����࣬��Ҫ����ִ��SQL���
import java.util.ArrayList;
import java.sql.ResultSet;         //���ݿ��ѯ�����
import java.sql.SQLException;

@SuppressWarnings("unused")

public class Database {
	/**
	 * disable this to recover database facility.
	 */
	private static final boolean ignoreDatabase = true;
	
	private static String DRIVER_MYSQL = "com.mysql.jdbc.Driver";    //MySQL JDBC�����ַ���
    private static String URL = "jdbc:mysql://localhost:3306/USER";
    private static Statement stmt;
    private Connection connection = null;
    
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
	
	
	/*
	 * ��½ �����û���������
	 * ���ɹ��򷵻��û�ID
	 * ʧ���򷵻�-1
	 */
	public int checkUser(String username,String password)
	{
		String temp = username + "' and password = '" + password;
		System.out.println(temp);
		int ID = findID(temp);
		return ID;
	}
	
	/*
	 * ����ע�� ���ע��ʧ���򷵻�-1�����򷵻����û���ID
	 */
	public int signUp(String username,String password)
	{
		int count = findID(username);
		if(count != -1)
		{
			return -1;
		}
		int uid = count("user") + 1;    //��ѯ���е�user���ܸ���������õ����û���ID
		String insert_user = "insert user values(" + uid + ",'" + username + "','" + password + "')";   //�������û�
		
		int tid = count("usertable") + 1 ; //�����µĴ��¼����ID
		String insert_table = "insert usertable values(" + tid + "," + uid + ",'event" + uid + "','MyEvent',3)";
		
		String create_table = "create table event" + uid+ "(EID int primary key not null,ETime Date,EName varchar(50))";
		
		try{
			stmt.execute(insert_user);
			stmt.execute(insert_table);
			stmt.execute(create_table);	
		}
		catch (SQLException e){
			e.printStackTrace();
		}
		
		return uid;
	}
	
	
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
		String temp_insert = "insert usertable values(" + cnt + "," + uid + ",'t" + cnt + "','" + uname + "'," + (column_num+2) + ")" ;
		
		
		String temp_create = "create table t" + cnt + "(";            //�����±����䣬�±���ΪtID,��usertable�е�10�ű�����Ϊt10
		 
		for(int i=0;i<column_num;i++)                                 //����Ҫ�����create���
		{
			temp_create = temp_create + mes[3+2*i] + " varchar(" + mes[4+2*i] + "),";
		}
		temp_create = temp_create + "TID int primary key, EID int, foreign key(EID) references event" + uid + "(EID) ) ";  //�������������û�����event��
		
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
	public String findUserTable(int uid)
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
				if(count!=0)
				{
					ret = ret + "~" +res.getString(4);
				}
				count++;
			}
			ret = count + ret ;
		}
		catch (SQLException e){e.printStackTrace();}
		
		return ret;
	}
	
	/* 11/7 ����ͨ��
	 * �����û�id�ͱ��� ���ر������
	 */
	public String findTableColumn(int uid, String tablename)
	{
		/* sqk's requirement 2016/10/31
		 * in userid && tablename(user)
		 * return columncount ~ column1 ~ length1 ~ column2 ~ length2������������������������ 
		 * use ~(temporary) 
		 */
		ResultSet res = null;
		
		String temp = "select * from usertable where UID = " + uid + " and uname = '"  + tablename + "'";
		System.out.println(temp);
		
		String dbtablename = "";
		int count = 0;
		String ret = "";
		
		try {
			res = stmt.executeQuery(temp);
			while(res.next())
			{
				dbtablename = res.getString(3);
				count = res.getInt(5);
			}
			
			ret = ret + count ;
			temp = "desc " + dbtablename;
			
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

	/* private���� ����ֱ��ʹ�û��޸ģ�����
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

}
