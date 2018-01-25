package test;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.hbase.thrift2.generated.TColumn;
import org.apache.hadoop.hbase.thrift2.generated.TColumnValue;
import org.apache.hadoop.hbase.thrift2.generated.TGet;
import org.apache.hadoop.hbase.thrift2.generated.THBaseService;
import org.apache.hadoop.hbase.thrift2.generated.TIOError;
import org.apache.hadoop.hbase.thrift2.generated.TPut;
import org.apache.hadoop.hbase.thrift2.generated.TResult;
import org.apache.hadoop.hbase.thrift2.generated.TScan;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

public class HbaseThrift2 {
	public static void main(String[] args) throws TIOError, TException {
		// 测试环境IP
		String host = "192.168.30.13";
		// 测试环境端口
		int port = 9097;
		// 超时时间
		int timeout = 10000;
		// boolean framed = false;
		// ThriftHBaseServiceHandler handler = createHandler();
		// 创建通信连接
		TTransport transport = new TSocket(host, port, timeout);
		// if (framed) {
		// transport = new TFramedTransport(transport);
		// }
		// 获取二进制协议
		TProtocol protocol = new TBinaryProtocol(transport);
		// 创建thrift2的连接客户端
		THBaseService.Iface client = new THBaseService.Client(protocol);
		// 打开通道
		transport.open();
		// 获取hbase表名字节缓冲
		ByteBuffer table = ByteBuffer.wrap("wb_content".getBytes());

		// 单条插入查询
		single(transport, client, table);
		
		// 批量ID查询
//		scannerRows(transport, client, table);
	}

	/**
	 * 写入数据
	 * 
	 * @param transport
	 * @param client
	 * @param table
	 */
	public static void single(TTransport transport, THBaseService.Iface client, ByteBuffer table) {
		try {
			// 下面是删除数据的方法
//			TDelete delete=new TDelete();
//			delete.setRow("103".getBytes());
//			client.deleteSingle(table, delete);
			
			// 下面是添加数据的方法
			TPut put = new TPut();
			put.setRow("103".getBytes());
			TColumnValue columnValue = new TColumnValue();
			columnValue.setFamily("content".getBytes());
			columnValue.setQualifier("title".getBytes());
			columnValue.setValue("gaobowen1".getBytes());
			List<TColumnValue> columnValues = new ArrayList<TColumnValue>();
			columnValues.add(columnValue);
			put.setColumnValues(columnValues);
			client.put(table, put);
			
			// 获取数据的方法
			TGet get = new TGet();
			get.setRow("103".getBytes());
			// get单条数据的查询方法
			TResult tResult = client.get(table, get);
			if (tResult!=null) {
				System.out.print("row = " + new String(tResult.getRow()));
				for (TColumnValue resultColumnValue : tResult.getColumnValues()) {
					System.out.print(",family = " + new String(resultColumnValue.getFamily()));
					System.out.print(",qualifier = " + new String(resultColumnValue.getQualifier()));
					System.out.print(",value = " + new String(resultColumnValue.getValue()));
					System.out.print(",timestamp = " + resultColumnValue.getTimestamp());
					System.out.println();
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// 关闭通道
			transport.close();
		}

	}

	/**
	 * 批量获取hbase表数据
	 */
	public static void scannerRows(TTransport transport, THBaseService.Iface client, ByteBuffer table) {
		int scanId = 0;
		try {
			// 创建扫描器，批量取出列族下面的数据
			TScan scan = new TScan();
			List<TColumn> columns = new ArrayList<TColumn>();
			TColumn column = new TColumn();
			// 列族
			column.setFamily("content".getBytes());
			// 列名
			column.setQualifier("title".getBytes());
			columns.add(column);
			scan.setColumns(columns);
			scan.setStartRow("102".getBytes());

			// 获取扫描器ID
			scanId = client.openScanner(table, scan);
			// 根据扫描器ID 查询10条数据
			List<TResult> scannerRows = client.getScannerRows(scanId, 10);
			for (TResult tResult : scannerRows) {
				System.out.print("row = " + new String(tResult.getRow()));
				for (TColumnValue resultColumnValue : tResult.getColumnValues()) {
					System.out.print(",family = " + new String(resultColumnValue.getFamily()));
					System.out.print(",qualifier = " + new String(resultColumnValue.getQualifier()));
					System.out.print(",value = " + new String(resultColumnValue.getValue()));
					System.out.print(",timestamp = " + resultColumnValue.getTimestamp());
					System.out.println();
				}
			}
			// 关闭扫描器
			client.closeScanner(scanId);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// 关闭通道
			transport.close();
		}

	}
}
