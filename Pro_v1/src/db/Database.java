package db;

import java.sql.Connection;        //���ݿ�����ʵ��
import java.sql.DriverManager;     //���ݿ����������࣬�����侲̬����getConnection���������ݿ��URL������ݿ�����ʵ��
import java.sql.Statement;         //�������ݿ�Ҫ�õ����࣬��Ҫ����ִ��SQL���
import java.util.ArrayList;
import java.sql.ResultSet;         //���ݿ��ѯ�����
import java.sql.SQLException;
public class Database {
	
	private static String DRIVER_MYSQL = "com.mysql.jdbc.Driver";    //MySQL JDBC�����ַ���
    private static String URL = "jdbc:mysql://localhost:3306/USER";
    private static Statement stmt;
    private Connection connection = null;
    
    public static void main()
    {
    	//new
    }
	
    public void connect()                           //�������ݿ�
	{
		 try
	        {
	            Class.forName(DRIVER_MYSQL);     //����JDBC����
	            connection = DriverManager.getConnection(URL,"root","1234");   //�������ݿ����Ӷ���
	            stmt = connection.createStatement();       //����Statement����
	        } 
		 catch (Exception e){e.printStackTrace();}
	}
	
    public ResultSet query(String sql)                     //sql query
    {
        ResultSet result = null;
        try
        {
            result = stmt.executeQuery(sql);
        } 
        catch (SQLException e){e.printStackTrace();}
        return result;
    }
    
    public void executeSql(String sql) {
    	try
        {
            stmt.execute(sql);
        } 
    	catch (SQLException e){e.printStackTrace();}
    }
    
	public void Update(String up)
	{
		try {
			stmt.executeUpdate(up);
		} 
		catch (SQLException e) {e.printStackTrace();}
	}
	
	public void close()
	{
		try {
			connection.close();
		}
		catch (Exception e){e.printStackTrace();}
		return ;
	}
	
	public int findID(String username)
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
	
	public int count(String tname) {
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
	
	public void newuser(int UID)
	{
		int tid = count("usertable") + 1;
		String temp = "insert usertable value(" + tid + "," + UID + ",'event" + UID + "','event',3)";            //��������event��ļ�¼
		try
		{
			stmt.execute(temp);
			temp = "create table event" + UID + "(EID int primary key not null,ETime Date,EName varchar(50))";   //�������û�event��
			stmt.execute(temp);
		}
		catch (SQLException e){e.printStackTrace();}
	}
	
	/*public void newtable(int UID, String uname, ArrayList<Table> request)    //UIDΪ�û�ID��unameΪ�û��Լ���ע������requestΪ�½�Ҫ��
	{
		int cnt = count("usertable") + 1;                             //�����½�����usertable���ID
		String temp_create = "create table t" + cnt + "(";            //�����±����䣬�±���ΪtID,��usertable�е�10�ű�����Ϊt10
		 
		int co_count = 0;                                             //�±�����
		for (Table a: request)                                        //����Ҫ�����create���
		{
			co_count ++;
			temp_create = temp_create + a.cname + "  " + a.ctype + ", ";
		}
		temp_create = temp_create + "TID int primary key, EID int, foreign key(EID) references event" + UID + "(EID) ) ";  //�������������û�����event��

		String temp_insert = "insert usertable values(" + cnt + "," + UID + ",'t" + cnt + "','" + uname + "'," + co_count + ")" ; //��usertable�����ӱ����¼
		try{
			stmt.execute(temp_insert);   //�����±��¼��usertable
			stmt.execute(temp_create);   //�����±�
		}
		catch (SQLException e){e.printStackTrace();}
	}*/
	
	public int checkTableName(String a)
	{
		
/*		String[] mes = a.split("~");
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
    	catch (SQLException e){e.printStackTrace();}

		return n;     //nΪ0����ɹ�
			*/	
		return 0;
	}
	
	public int createUserTable(String a)
	{
		String[] mes = a.split("~");
		
		String uid = mes[0];
		String uname = mes[1];
		int column_num = Integer.parseInt(mes[2]) + 2;
		
		//�����usertable��
		int cnt = count("usertable") + 1; 
		String temp_insert = "insert usertable values(" + cnt + "," + uid + ",'t" + cnt + "','" + uname + "'," + (column_num+2) + ")" ;
		
		
		String temp_create = "create table t" + cnt + "(";            //�����±����䣬�±���ΪtID,��usertable�е�10�ű�����Ϊt10
		 
		for(int i=0;i<column_num;i++)                                 //����Ҫ�����create���
		{
			temp_create = temp_create + mes[3+2*i] + "varchar(" + mes[4+2*i] + "',";
		}
		temp_create = temp_create + "TID int primary key, EID int, foreign key(EID) references event" + uid + "(EID) ) ";  //�������������û�����event��
		
		try{
			stmt.execute(temp_insert);   //�����±��¼��usertable
			stmt.execute(temp_create);   //�����±�
			return 0;
		}
		catch (SQLException e){e.printStackTrace();return 1;}
	}
}
