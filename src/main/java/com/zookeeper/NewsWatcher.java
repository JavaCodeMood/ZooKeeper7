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

	// 在zk上注册的机器节点列表
	public static SortedSet<String> servers;
	// 由zk自动递增分配的节点id
	public static String myNodeID;

	private ZooKeeper zk;
	private final Stat stat = new Stat();
	// news应用的顶层目录
	private final String spath = "/NewsWatcher";
	// 分隔符
	private final String delimiter = "/";

	public NewsWatcher(String id) throws Exception {
		try {
			// 创建一个与服务器的连接
			zk = new ZooKeeper("127.0.0.1:2181", 5000, new Watcher() {
				public void process(WatchedEvent event) {
					// System.out.println("node Change:" + event);
					// 如果发生了spath节点下的子节点变化事件, 更新server列表, 并重新注册监听
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

	// 创建一个子目录节点
	public void createAppNode(String id) throws Exception {
		myNodeID = zk.create(spath + delimiter, id.getBytes("utf-8"), Ids.OPEN_ACL_UNSAFE,
				CreateMode.EPHEMERAL_SEQUENTIAL);
		myNodeID = myNodeID.substring(myNodeID.lastIndexOf('/') + 1);
	}

	// 如果不存在则创建顶层目录
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

	// 更新服务器列表
	public void updateServerList() throws Exception {
		SortedSet<String> set = new TreeSet<String>();
		// 获取并监听spath的子节点变化
		// watch参数为true, 表示监听子节点变化事件.
		// 每次都需要重新注册监听, 因为一次注册, 只能监听一次事件, 如果还想继续保持监听, 必须重新注册
		List<String> subList = zk.getChildren(spath, true);
		for (String subNode : subList) {
			// 获取每个子节点下关联的server地址
			// byte[] data = zk.getData(spath + delimiter + subNode, false,
			// stat);
			// System.out.println(subNode + "\t" + stat);
			// String sdata = new String(data, "utf-8");
			set.add(subNode);
		}
		servers = set;
	}

	// 关闭连接
	public void close() throws InterruptedException {
		zk.close();
	}
}