package org.codetab.gotz.dao.jdo;

import javax.inject.Inject;
import javax.jdo.PersistenceManagerFactory;

import org.codetab.gotz.dao.IDataDao;
import org.codetab.gotz.dao.IDataDefDao;
import org.codetab.gotz.dao.IDocumentDao;
import org.codetab.gotz.dao.ILocatorDao;
import org.codetab.gotz.exception.FatalException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class DaoFactory extends org.codetab.gotz.dao.DaoFactory {

    static final Logger LOGGER = LoggerFactory.getLogger(DaoFactory.class);

    private PersistenceManagerFactory pmfactory;

    private PMF pmf;

    @Inject
    public void setPmf(PMF pmf) throws FatalException {
        if (pmf.getFactory() == null) {
            pmf.init();
        }
        this.pmf = pmf;
        pmfactory = this.pmf.getFactory();
    }

    @Inject
    public DaoFactory() {
    }

    public PersistenceManagerFactory getFactory() {
        return pmfactory;
    }

    @Override
    public IDocumentDao getDocumentDao() {
        return new DocumentDao(pmfactory);
    }

    @Override
    public IDataDefDao getDataDefDao() {
        return new DataDefDao(pmfactory);
    }

    @Override
    public IDataDao getDataDao() {
        return new DataDao(pmfactory);
    }

    @Override
    public ILocatorDao getLocatorDao() {
        return new LocatorDao(pmfactory);
    }

}
