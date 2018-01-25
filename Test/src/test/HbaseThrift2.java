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
		// ���Ի���IP
		String host = "192.168.30.13";
		// ���Ի����˿�
		int port = 9097;
		// ��ʱʱ��
		int timeout = 10000;
		// boolean framed = false;
		// ThriftHBaseServiceHandler handler = createHandler();
		// ����ͨ������
		TTransport transport = new TSocket(host, port, timeout);
		// if (framed) {
		// transport = new TFramedTransport(transport);
		// }
		// ��ȡ������Э��
		TProtocol protocol = new TBinaryProtocol(transport);
		// ����thrift2�����ӿͻ���
		THBaseService.Iface client = new THBaseService.Client(protocol);
		// ��ͨ��
		transport.open();
		// ��ȡhbase�����ֽڻ���
		ByteBuffer table = ByteBuffer.wrap("wb_content".getBytes());

		// ���������ѯ
		single(transport, client, table);
		
		// ����ID��ѯ
//		scannerRows(transport, client, table);
	}

	/**
	 * д������
	 * 
	 * @param transport
	 * @param client
	 * @param table
	 */
	public static void single(TTransport transport, THBaseService.Iface client, ByteBuffer table) {
		try {
			// ������ɾ�����ݵķ���
//			TDelete delete=new TDelete();
//			delete.setRow("103".getBytes());
//			client.deleteSingle(table, delete);
			
			// ������������ݵķ���
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
			
			// ��ȡ���ݵķ���
			TGet get = new TGet();
			get.setRow("103".getBytes());
			// get�������ݵĲ�ѯ����
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
			// �ر�ͨ��
			transport.close();
		}

	}

	/**
	 * ������ȡhbase������
	 */
	public static void scannerRows(TTransport transport, THBaseService.Iface client, ByteBuffer table) {
		int scanId = 0;
		try {
			// ����ɨ����������ȡ���������������
			TScan scan = new TScan();
			List<TColumn> columns = new ArrayList<TColumn>();
			TColumn column = new TColumn();
			// ����
			column.setFamily("content".getBytes());
			// ����
			column.setQualifier("title".getBytes());
			columns.add(column);
			scan.setColumns(columns);
			scan.setStartRow("102".getBytes());

			// ��ȡɨ����ID
			scanId = client.openScanner(table, scan);
			// ����ɨ����ID ��ѯ10������
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
			// �ر�ɨ����
			client.closeScanner(scanId);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// �ر�ͨ��
			transport.close();
		}

	}
}
