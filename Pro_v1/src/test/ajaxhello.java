package test;

import com.opensymphony.xwork2.ActionSupport;

public class ajaxhello extends ActionSupport {

 private static final long serialVersionUID = 7443363719737618408L;
 private String name;
 private String inch;
 private String result;
 @Override
 public String execute() throws Exception {
  // TODO Auto-generated method stub
  if("����".equals(name)) {
   result = "�����֤ͨ��,���Ϊ" + inch;
  } else 
   result = "����������";
  return SUCCESS;
 }
 
 public String getName() {
  return name;
 }
 public void setName(String name) {
  this.name = name;
 }
 public String getInch() {
  return inch;
 }
 public void setInch(String inch) {
  this.inch = inch;
 }
 public String getResult() {
  return result;
 }

}