package org.codetab.gotz.persistence;

import java.util.List;

import javax.inject.Inject;

import org.apache.commons.lang3.Validate;
import org.codetab.gotz.dao.DaoFactoryProvider;
import org.codetab.gotz.dao.IDaoFactory;
import org.codetab.gotz.dao.IDataSetDao;
import org.codetab.gotz.dao.ORM;
import org.codetab.gotz.exception.StepPersistenceException;
import org.codetab.gotz.model.DataSet;
import org.codetab.gotz.shared.ConfigService;
import org.codetab.gotz.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * Data Persistence methods.
 * @author Maithilish
 *
 */
public class DataSetPersistence {

    /**
     * logger.
     */
    private static final Logger LOGGER =
            LoggerFactory.getLogger(DataSetPersistence.class);

    /**
     * ConfigService.
     */
    @Inject
    private ConfigService configService;
    /**
     * DaoFactoryProvider.
     */
    @Inject
    private DaoFactoryProvider daoFactoryProvider;

    /**
     * <p>
     * Store dataSet.
     * @param dataSets
     *            dataSet to store, not null
     * @throws StepPersistenceException
     *             on persistence error
     */
    public void storeDataSet(final List<DataSet> dataSets) {
        Validate.notNull(dataSets, "dataSet must not be null");

        try {
            ORM orm = configService.getOrmType();
            IDaoFactory daoFactory = daoFactoryProvider.getDaoFactory(orm);
            IDataSetDao dao = daoFactory.getDataSetDao();
            dao.storeDataSets(dataSets);
        } catch (RuntimeException e) {
            String message = Util.buildString("unable to store data",
                    dataSets.get(0).getName(), ":", dataSets.get(0).getGroup());
            LOGGER.error("{}", e.getMessage());
            LOGGER.trace("", e);
            throw new StepPersistenceException(message, e);
        }
    }
}
