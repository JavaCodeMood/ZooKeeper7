package com.zookeeper;

public class Test {
	public static void main(String[] args) throws Exception {
		String nodeid = "autoid";
		if (args.length == 1) {
			nodeid = args[0];
		}
		new NewsWatcher(nodeid);

		// ���ó����˳�������ÿ5����ѯ����һ������
		new Thread(new Task()).start();
	}
}