package com.zookeeper;

public class Test {
	public static void main(String[] args) throws Exception {
		String nodeid = "autoid";
		if (args.length == 1) {
			nodeid = args[0];
		}
		new NewsWatcher(nodeid);

		// 不让程序退出，而是每5秒轮询着做一个任务
		new Thread(new Task()).start();
	}
}