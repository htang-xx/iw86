package com.iw86.base;

import java.util.ArrayList;
import java.util.List;

import com.iw86.collection.Row;
import com.iw86.lang.JsonUtil;

import junit.framework.TestCase;

public class BaseTest extends TestCase {

	@Override
	protected void setUp() throws Exception {
		// TODO Auto-generated method stub
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		// TODO Auto-generated method stub
		super.tearDown();
	}
    
	public void test1(){
		Row row = new Row();
		row.put("aa", 11111);
		row.put("bb", 11111);
		row.put("cc", 11111);
		row.put("dd", 11111);
		System.out.println("row:"+JsonUtil.jsonInclude(row, new String[]{"bb","cc"}));
		List<Row> list = new ArrayList<Row>();
		list.add(row);
		System.out.println("list:"+JsonUtil.jsonInclude(list, new String[]{"aa","cc"}));
		Domain d = new Domain();
		d.setAa("1111");
		d.setBb("1111");
		d.setCc("11111");
		d.setDd("11111");
		System.out.println("domain:"+JsonUtil.jsonInclude(d, new String[]{"bb","cc"}));
		List<Domain> dlist = new ArrayList<Domain>();
		dlist.add(d);
		System.out.println("dlist:"+JsonUtil.jsonInclude(dlist, new String[]{"aa","cc"}));
	}
	
	class Domain{
		String aa;
		String bb;
		String cc;
		String dd;
		public String getAa() {
			return aa;
		}
		public void setAa(String aa) {
			this.aa = aa;
		}
		public String getBb() {
			return bb;
		}
		public void setBb(String bb) {
			this.bb = bb;
		}
		public String getCc() {
			return cc;
		}
		public void setCc(String cc) {
			this.cc = cc;
		}
		public String getDd() {
			return dd;
		}
		public void setDd(String dd) {
			this.dd = dd;
		}
	}
	
}
