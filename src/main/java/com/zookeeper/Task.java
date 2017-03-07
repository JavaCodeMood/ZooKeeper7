package com.zookeeper;

public class Task implements Runnable {

	public void run() {
		while (true) {
			try {
				mybussiness();
				Thread.sleep(10000l);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	// 寻找id最小的节点来做任务（可以改成寻找最小的2个机器来做）
	public void mybussiness() {
		System.out.println("all nodes:" + NewsWatcher.servers);
		String minNode = NewsWatcher.servers.first();
		System.out.println(NewsWatcher.myNodeID + "   " + minNode);
		if (NewsWatcher.myNodeID.equals(minNode)) {// 验证本机是否是最小节点
			System.out.println("i am the leader. i could do this job");
		} else {
			System.out.println("i am not the leader. i could not do this job");
		}
	}

}
