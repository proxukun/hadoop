package org.robby;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;

import java.util.ArrayList;
import java.util.List;



public class HbaseCdrIf {


	HTablePool tablePool;
	public static HbaseCdrIf ghbase = null;
	
	
	public HbaseCdrIf(){
		tablePool = new HTablePool(HBaseConfiguration.create(), 1000);
	}
	
	public static HbaseCdrIf getInstance(){
		if(ghbase == null)
			ghbase = new HbaseCdrIf();
		return ghbase;
	}
	
	public List<CdrPro.SmCdr> getSmCdr(String addr) throws Exception{
		List<CdrPro.SmCdr> l = new ArrayList<CdrPro.SmCdr>();
		HTable tab = (HTable) tablePool.getTable("tab_cdr");
		
		Scan s = new Scan();
		s.setStartRow(addr.getBytes());
		byte[] end = addr.getBytes();
		end[end.length-1] += 1;
		s.setStopRow(end);
		
		ResultScanner rs = tab.getScanner(s);
		Get get = null;
		Post p = null;
		
		for (Result r : rs) {
			byte[] data = r.getValue(Bytes.toBytes("data"), null);
			
			CdrPro.SmCdr sm = CdrPro.SmCdr.parseFrom(data);
			l.add(sm);
		}
		
		return l;
	}
	
	public static void main(String[] args) throws Exception{
		// TODO Auto-generated method stub
		HbaseCdrIf hif = HbaseCdrIf.getInstance();
		List<CdrPro.SmCdr> l = hif.getSmCdr("1390000068");
		for(CdrPro.SmCdr sm:l){
			System.out.println(sm.getOaddr() + ":" + sm.getOareacode() + ":" + sm.getDaddr() + ":" + sm.getDareacode() + ":" + sm.getTimestamp() + ":" + sm.getType());
		}
	}

}
