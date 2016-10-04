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
}
