package db;

public class DatabaseTest {
	
	public static void main(String[] args) {
		
		String temp = null;
		String result = null;
		
		int intr = 0;
		
		// TODO Auto-generated method stub
		Database db1 = new Database();
		result = db1.connect();
		
		System.out.println(result);
		
		/* 
		
		//�����ڵ��û����������¼
		intr = db1.checkUser("wangye", "1234");
		System.out.println("test1" + intr);
		//��Чע��
		intr = db1.signUp("wangye", "1234");
		System.out.println("test2" + intr);
		//��Чע��
		intr = db1.signUp("wangye", "5678");
		System.out.println("test3" + intr);
		//��Ч��½
		intr = db1.checkUser("wangye", "1234");
		System.out.println("test4" + intr);
		//���������½
		intr = db1.checkUser("wangye", "5678");
		System.out.println("test5" + intr);
		intr = db1.signUp("sqk", "1234");
		System.out.println("test6" + intr);
		
		//�����û���
		intr = db1.createUserTable("2~Money~3~Name~10~time~12~product~20");
		System.out.println("test7" + intr);
		//�������ͬ����
		intr = db1.checkTableName("2~Money");
		System.out.println("test8" + intr);
		//��������������û�ͬ���ı�
		intr = db1.checkTableName("1~Money");
		System.out.println("test9" + intr);
		//�ҵ�һ���û������б�
		result= db1.findUserTable(2);
		System.out.println("test10" +result);
		//�鿴һ����ľ�����Ϣ
		result= db1.findTableColumn(2, "Money");
		System.out.println("test11" +result);
		//��С�¼����м���һ����¼
		intr = db1.insertSEvent(2, "Money", "dinner~2016~lunch");
		System.out.println("test12 " + intr);
		intr = db1.insertSEvent(2, "Money", "snacks~2016~6yuan");
		System.out.println("test13 " + intr);
		//�鿴������������Ϣ
		result = db1.findSEvent(2,"Money");
		System.out.println("test14 " + result);
		//�鿴������������
		result = db1.findSEventN5(2,"Money");
		System.out.println("test15 " + result);
		//�����û���
		intr = db1.createUserTable("2~Eat~3~Name~10~time~12~place~20");
		System.out.println("addition7" + intr);
		//���Բ鿴�û����б�
		result = db1.tableBrief(2);
		System.out.println("test16 " + result);
		//�鿴С�¼�������� ������С�¼�Ψһid
		result = db1.tableContent("2~Money");
		System.out.println("test17 " + result);
		//������Ӵ��¼� ����Ϊ�û�ID~���¼�EName~ETime~С�¼�ȫ��ID~С�¼�ȫ��ID
		intr = db1.submitEvent("2~testagain~20161115~5_1~5_2");
		System.out.println("test18 " + intr);
		//���Բ鿴�û����д��¼�
		result = db1.eventBrief(2);
		System.out.println("test19 " + result);
		 */
		
		result = db1.searchRequest(2,"dinner");
		System.out.println("test20 " + result);
	
		
		
		result= db1.findTableColumn(2, "Money");
		System.out.println("test11" +result);
		
		
		System.out.println(db1.close());
		
	}

}
