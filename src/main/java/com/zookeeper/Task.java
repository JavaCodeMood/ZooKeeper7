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

	// Ѱ��id��С�Ľڵ��������񣨿��Ըĳ�Ѱ����С��2������������
	public void mybussiness() {
		System.out.println("all nodes:" + NewsWatcher.servers);
		String minNode = NewsWatcher.servers.first();
		System.out.println(NewsWatcher.myNodeID + "   " + minNode);
		if (NewsWatcher.myNodeID.equals(minNode)) {// ��֤�����Ƿ�����С�ڵ�
			System.out.println("i am the leader. i could do this job");
		} else {
			System.out.println("i am not the leader. i could not do this job");
		}
	}

}
