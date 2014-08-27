package com.easyminning.mongodbclient2.driver;

import com.easyminning.mongodbclient2.constants.MongoConstants;
import com.mongodb.Mongo;
import com.mongodb.MongoOptions;
import com.mongodb.ServerAddress;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class MongoDBDriver {
	public void setConfigFile(String configFile) {
		this.configFile = configFile;
		Properties config = new Properties();
		try {
			config.load(MongoDBDriver.class.getClassLoader().getResourceAsStream(
					this.configFile));
			this.config = config;
		} catch (IOException e) {
			logger.error("无法加载配置文件:" + configFile, e);
		}
	}

	public void setConfig(Properties config) {
		this.config = config;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public void setConnectionsPerHost(String connectionsPerHost) {
		this.connectionsPerHost = connectionsPerHost;
	}

	public void setMinPoolsSize(String minPoolsSize) {
		this.minPoolsSize = minPoolsSize;
	}

	public void setThreadsAllowedToBlockForConnectionMultiplier(
			String threadsAllowedToBlockForConnectionMultiplier) {
		this.threadsAllowedToBlockForConnectionMultiplier = threadsAllowedToBlockForConnectionMultiplier;
	}

	public void setMaxWaitTime(String maxWaitTime) {
		this.maxWaitTime = maxWaitTime;
	}

	public void setConnectTimeout(String connectTimeout) {
		this.connectTimeout = connectTimeout;
	}

	public void setSocketTimeout(String socketTimeout) {
		this.socketTimeout = socketTimeout;
	}

	public void setAutoConnectRetry(String autoConnectRetry) {
		this.autoConnectRetry = autoConnectRetry;
	}
	public Mongo getMongo() {
		return mongo;
	}
	public void init() {
		if(config!=null)
			initFields(config);
		initMongoOptions();
		initMongo();
	}
	public void close() {
		mongo.close();
	}
	/**
	 * 为MongoDBDriver初始化属性
	 * 
	 * @param config
	 *          初始化属性值
	 */
	private void initFields(Properties config) {
		Class<?> c = MongoDBDriver.class;
		for (Field f : c.getDeclaredFields()) {
			String key = c.getName() + "." + f.getName();
			String value = config.getProperty(key);
			if (value != null && value.trim().length() > 0) {
				f.setAccessible(true);
				try {
					f.set(this, value);
				} catch (Exception e) {
					logger.error("初始化MongoDBDriver参数[" + f.getName() + "=" + value
							+ "]失败", e);
				}
			}
		}
	}

	private void initMongoOptions() {
		try {
			MongoOptions options = new MongoOptions();
			options.connectionsPerHost = Integer.valueOf(this.connectionsPerHost);
			options.threadsAllowedToBlockForConnectionMultiplier = Integer
					.valueOf(threadsAllowedToBlockForConnectionMultiplier);
			options.minPoolsSize = Integer.valueOf(this.minPoolsSize);

			// 2011-08-18 lishuai add
			options.maxWaitTime = Integer.valueOf(this.maxWaitTime);
			options.connectTimeout = Integer.valueOf(this.connectTimeout);
			options.socketTimeout = Integer.valueOf(this.socketTimeout);
			options.autoConnectRetry = Boolean.valueOf(this.autoConnectRetry);

			this.mongoOptions = options;
		} catch (Exception e) {
			logger.error("初始化initMongoOptions失败", e);
		}
	}

	/**
	 * 初始化mongo,database和collection实例
	 */
	private void initMongo() {
		try {
			if (!hostname.contains(",")) {
				ServerAddress address = new ServerAddress(hostname, Integer
						.valueOf(port));
				this.mongo = new Mongo(address, this.mongoOptions);
			} else {
				String[] hostNames = hostname.split(",");
				List<ServerAddress> addresses = new ArrayList<ServerAddress>();
				for (String host : hostNames) {
					addresses.add(new ServerAddress(host, Integer.valueOf(port)));
				}
				this.mongo = new Mongo(addresses, this.mongoOptions);
			}
		} catch (Exception e) {
			logger.error("初始化initMongo失败", e);
		}
	}

	/**
	 * Mongo对象
	 */
	private Mongo mongo = null;
	/**
	 * MongoOptions对象
	 */
	private MongoOptions mongoOptions = null;
	/**
	 * 配置文件名
	 */
	private String configFile;
	/**
	 * 配置内容
	 */
	private Properties config;
	/**
	 * 数据库地址
	 */
	private String hostname = MongoConstants.DEFAULT_MONGO_DB_HOSTNAME;
	/**
	 * 数据库端口
	 */
	private String port = MongoConstants.DEFAULT_MONGO_DB_PORT;
	/**
	 * Mongo的连接数
	 */
	private String connectionsPerHost = MongoConstants.DEFAULT_CONNECTIONS_PER_HOST;
	/**
	 * 连接池中最低连接数量
	 */
	private String minPoolsSize = MongoConstants.DEFAULT_MIN_POOL_SIZE;
	/**
	 * Mongo的Thread堵塞数
	 */
	private String threadsAllowedToBlockForConnectionMultiplier = MongoConstants.DEFAULT_THREADSALLOWEDTOBLOCK_FOR_CONNECTIONMULTIPLIER;

	private String maxWaitTime = MongoConstants.DEFAULT_MAX_WAIT_TIME;
	private String connectTimeout = MongoConstants.DEFAULT_CONNECT_TIMEOUT;
	private String socketTimeout = MongoConstants.DEFAULT_SOCKET_TIMEOUT;
	private String autoConnectRetry = MongoConstants.DEFAULT_AUTO_CONNECT_RETRY;
	public static final Log logger = LogFactory.getLog(MongoConstants.COMMON_MONGO);
}
