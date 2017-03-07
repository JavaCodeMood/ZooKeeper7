package com.zookeeper;

import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

public class NewsWatcher {

	// ��zk��ע��Ļ����ڵ��б�
	public static SortedSet<String> servers;
	// ��zk�Զ���������Ľڵ�id
	public static String myNodeID;

	private ZooKeeper zk;
	private final Stat stat = new Stat();
	// newsӦ�õĶ���Ŀ¼
	private final String spath = "/NewsWatcher";
	// �ָ���
	private final String delimiter = "/";

	public NewsWatcher(String id) throws Exception {
		try {
			// ����һ���������������
			zk = new ZooKeeper("127.0.0.1:2181", 5000, new Watcher() {
				public void process(WatchedEvent event) {
					// System.out.println("node Change:" + event);
					// ���������spath�ڵ��µ��ӽڵ�仯�¼�, ����server�б�, ������ע�����
					if (event.getType() == EventType.NodeChildrenChanged && spath.equals(event.getPath())) {
						try {
							updateServerList();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			});
			createParentDirectory();
			createAppNode(id);
			updateServerList();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// ����һ����Ŀ¼�ڵ�
	public void createAppNode(String id) throws Exception {
		myNodeID = zk.create(spath + delimiter, id.getBytes("utf-8"), Ids.OPEN_ACL_UNSAFE,
				CreateMode.EPHEMERAL_SEQUENTIAL);
		myNodeID = myNodeID.substring(myNodeID.lastIndexOf('/') + 1);
	}

	// ����������򴴽�����Ŀ¼
	public void createParentDirectory() throws Exception {
		Stat stat = null;
		try {
			stat = zk.exists(spath, true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (stat == null) {
			zk.create(spath, "news watcher".getBytes("utf-8"), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
		}
	}

	// ���·������б�
	public void updateServerList() throws Exception {
		SortedSet<String> set = new TreeSet<String>();
		// ��ȡ������spath���ӽڵ�仯
		// watch����Ϊtrue, ��ʾ�����ӽڵ�仯�¼�.
		// ÿ�ζ���Ҫ����ע�����, ��Ϊһ��ע��, ֻ�ܼ���һ���¼�, �������������ּ���, ��������ע��
		List<String> subList = zk.getChildren(spath, true);
		for (String subNode : subList) {
			// ��ȡÿ���ӽڵ��¹�����server��ַ
			// byte[] data = zk.getData(spath + delimiter + subNode, false,
			// stat);
			// System.out.println(subNode + "\t" + stat);
			// String sdata = new String(data, "utf-8");
			set.add(subNode);
		}
		servers = set;
	}

	// �ر�����
	public void close() throws InterruptedException {
		zk.close();
	}
}