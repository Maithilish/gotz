package in.m.picks.poolold;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import in.m.picks.exception.AfieldNotFoundException;
import in.m.picks.model.Activity.Type;
import in.m.picks.model.Locator;
import in.m.picks.shared.MonitorService;
import in.m.picks.shared.StepService;
import in.m.picks.step.IStep;
import in.m.picks.util.AccessUtil;

public class LoaderPool extends Pool {

	final static Logger logger = LoggerFactory.getLogger(LoaderPool.class);

	public final static LoaderPool INSTANCE = new LoaderPool();

	private boolean seedFinished;

	private LoaderPool() {
		super("picks.loaderThreads");
		seedFinished = false;
	}

	public boolean submitTask(Locator locator) {
		try {
			String loaderClassName = AccessUtil.getStringValue(locator, "loader");
			IStep task = StepService.INSTANCE.getStep(loaderClassName);
			task.setInput(locator);
			submit(task);
			logger.trace("> [Loader] {}", locator);
			return true;
		} catch (AfieldNotFoundException e) {
			logger.warn("No loader afield found. Unable to create task {}", locator);
			logger.debug("{}", e);
			givenupTask(locator, e);
		} catch (ClassNotFoundException e) {
			givenupTask(locator, e);
		} catch (InstantiationException e) {
			givenupTask(locator, e);
		} catch (IllegalAccessException e) {
			givenupTask(locator, e);
		}
		return false;
	}

	public void givenupTask(Locator locator, Exception e) {
		MonitorService.INSTANCE.addActivity(Type.GIVENUP, locator.toString(), e);
	}

	public final boolean isSeedFinished() {
		return seedFinished;
	}

	public final void setSeedFinished(boolean seedFinished) {
		this.seedFinished = seedFinished;
	}

}
