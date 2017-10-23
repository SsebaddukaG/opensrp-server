package org.opensrp.repository;

import java.util.List;

import org.ektorp.ComplexKey;
import org.ektorp.CouchDbConnector;
import org.ektorp.support.CouchDbRepositorySupport;
import org.ektorp.support.View;
import org.opensrp.common.AllConstants;
import org.opensrp.domain.viewconfiguration.ViewConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository
public class ViewConfigurationRepository extends CouchDbRepositorySupport<ViewConfiguration> {

	@Autowired
	protected ViewConfigurationRepository(@Qualifier(AllConstants.OPENSRP_DATABASE_CONNECTOR) CouchDbConnector db) {
		super(ViewConfiguration.class, db);
		initStandardDesignDocument();
	}

	@View(name = "all_view_configurations", map = "function(doc) { if (doc.type==='ViewConfiguration') { emit(doc.identifier); }}")
	public List<ViewConfiguration> findAllViewConfigurations() {
		return db.queryView(createQuery("all_view_configurations").includeDocs(true), ViewConfiguration.class);
	}

	@View(name = "view_configurations_by_version", map = "function(doc) { if (doc.type==='ViewConfiguration') { emit([doc.serverVersion], null); }}")
	public List<ViewConfiguration> findViewConfigurationsByVersion(Long lastSyncedServerVersion) {
		ComplexKey startKey = ComplexKey.of(lastSyncedServerVersion);
		ComplexKey endKey = ComplexKey.of(Long.MAX_VALUE);
		return db.queryView(
				createQuery("view_configurations_by_version").includeDocs(true).startKey(startKey).endKey(endKey),
				ViewConfiguration.class);
	}

}
