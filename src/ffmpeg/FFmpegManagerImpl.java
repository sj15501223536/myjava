package ffmpeg;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map; 

/**
 * FFmpeg命令操作管理器
 * 
 * @author eguid
 * @since jdk1.7
 * @version 2017年10月13日
 */
public class FFmpegManagerImpl   {
	public static FFmpegConfig config=new FFmpegConfig();
	/**
	 * 任务持久化器
	 */
	private TaskDaoImpl taskDao = null;
	/**
	 * 任务执行处理器
	 */
	private TaskHandlerImpl taskHandler = null;
	/**
	 * 命令组装器
	 */
	private CommandAssemblyImpl commandAssembly = null;
	/**
	 * 任务消息处理器
	 */
	private DefaultOutHandlerMethod ohm = null;

	public FFmpegManagerImpl() {
		this(null);
	}

	public FFmpegManagerImpl(Integer size) {
		init(size);
	}

	/**
	 * 初始化，如果几个处理器未注入，则使用默认处理器
	 * 
	 * @param size
	 */
	public void init(Integer size) {
		if (config == null) {//借口里边的配置参数
			System.err.println("配置文件加载失败！配置文件不存在或配置错误");
			return;
		}
		if (size == null) {
			size = config.getSize() == null ? 10 : config.getSize();
		}
		if (this.ohm == null) {
			this.ohm = new DefaultOutHandlerMethod();
		}
		if (this.taskDao == null) {
			this.taskDao = new TaskDaoImpl(size);
		}
		if (this.taskHandler == null) {
			this.taskHandler = new TaskHandlerImpl(this.ohm);
		}
		if (this.commandAssembly == null) {
			this.commandAssembly = new CommandAssemblyImpl();
		}
	}
	public TaskDaoImpl getTaskDao() {
		return this.taskDao;
	}
	public void setTaskDao(TaskDaoImpl taskDao) {
		this.taskDao = taskDao;
	}

	public void setTaskHandler(TaskHandlerImpl taskHandler) {
		this.taskHandler = taskHandler;
	}
	public TaskHandlerImpl getTaskHandler() {
		return this.taskHandler;
	}
	public void setCommandAssembly(CommandAssemblyImpl commandAssembly) {
		this.commandAssembly = commandAssembly;
	}

	public void setOhm(DefaultOutHandlerMethod ohm) {
		this.ohm = ohm;
	}

	/**
	 * 是否已经初始化
	 * 
	 * @param 如果未初始化时是否初始化
	 * @return
	 */
	public boolean isInit(boolean b) {
		boolean ret = this.ohm == null || this.taskDao == null || this.taskHandler == null|| this.commandAssembly == null;
		if (ret && b) {
			init(null);
		}
		return ret;
	}

	
	public String start(String id, String command) {
		return start(id, command, false);
	}
	/**
	 * id  命令启动
	 */
	
	public String start(String id, String command, boolean hasPath) {
		if (isInit(true)) {
			System.err.println("执行失败，未进行初始化或初始化失败！");
			return null;
		}
		if (id != null && command != null) {
			TaskEntity tasker = taskHandler.process(id, hasPath ? command : config.getPath() + command);
			if (tasker != null) {
				int ret = taskDao.add(tasker);
				if (ret > 0) {
					return tasker.getId();
				} else {
					// 持久化信息失败，停止处理
					taskHandler.stop(tasker.getProcess(), tasker.getThread());
					if (config.isDebug())
						System.err.println("持久化失败，停止任务！");
				}
			}
		}
		return null;
	}
	/**
	 * map组装命令启动
	 */
	
	public String start(Map<String, String> assembly) {
		// ffmpeg环境是否配置正确
		if (config == null) {
			System.err.println("配置未正确加载，无法执行");
			return null;
		}
		// 参数是否符合要求
		if (assembly == null || assembly.isEmpty() || !assembly.containsKey("appName")) {
			System.err.println("参数不正确，无法执行");
			return null;
		}
		String appName = (String) assembly.get("appName");
		if (appName != null && "".equals(appName.trim())) {
			System.err.println("appName不能为空");
			return null;
		}
		assembly.put("ffmpegPath", config.getPath() + "ffmpeg");
		String command = commandAssembly.assembly(assembly);
		if (command != null) {
			return start(appName, command, true);
		}

		return null;
	}

	
	public boolean stop(String id) {
		if (id != null && taskDao.isHave(id)) {
			if (config.isDebug())
				System.out.println("正在停止任务：" + id);
			TaskEntity tasker = taskDao.get(id);
			if (taskHandler.stop(tasker.getProcess(), tasker.getThread())) {
				taskDao.remove(id);
				return true;
			}
		}
		System.err.println("停止任务失败！id=" + id);
		return false;
	}

	
	public int stopAll() {
		Collection<TaskEntity> list = taskDao.getAll();
		Iterator<TaskEntity> iter = list.iterator();
		TaskEntity tasker = null;
		int index = 0;
		while (iter.hasNext()) {
			tasker = iter.next();
			if (taskHandler.stop(tasker.getProcess(), tasker.getThread())) {
				taskDao.remove(tasker.getId());
				index++;
			}
		}
		if (config.isDebug())
			System.out.println("停止了" + index + "个任务！");
		return index;
	}

	
	public TaskEntity query(String id) {
		return taskDao.get(id);
	}

	
	public Collection<TaskEntity> queryAll() {
		return taskDao.getAll();
	}

	
}
