package org.opensrp.repository.lucene;

import static org.opensrp.common.AllConstants.BaseEntity.LAST_UPDATE;
import static org.opensrp.common.AllConstants.Client.BIRTH_DATE;
import static org.opensrp.common.AllConstants.Client.FIRST_NAME;
import static org.opensrp.common.AllConstants.Client.GENDER;
import static org.opensrp.common.AllConstants.Client.LAST_NAME;
import static org.opensrp.common.AllConstants.Client.MIDDLE_NAME;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.opensrp.domain.Client;
import org.opensrp.domain.Search;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.github.ldriscoll.ektorplucene.CouchDbRepositorySupportWithLucene;
import com.github.ldriscoll.ektorplucene.LuceneQuery;
import com.github.ldriscoll.ektorplucene.LuceneResult;
import com.github.ldriscoll.ektorplucene.designdocument.annotation.FullText;
import com.github.ldriscoll.ektorplucene.designdocument.annotation.Index;
import com.mysql.jdbc.StringUtils;

@FullText({ @Index(name = "by_all_criteria", index = "function(doc){ if(doc.type!=='Client') return null; var arr1=['firstName','middleName','lastName','gender']; var ret=new Document(); for(var i in arr1){ ret.add(doc[arr1[i]],{'field':arr1[i]}) } for (var key in doc.identifiers) { ret.add(doc.identifiers[key], {'field': key}); } for(var key in doc.attributes){ ret.add(doc.attributes[key],{'field':key}) } var bd=doc.birthdate.substring(0,19); ret.add(bd,{'field':'birthdate','type':'date'}); var crd=doc.dateCreated.substring(0,19); ret.add(crd,{'field':'lastEdited','type':'date'}); if(doc.dateEdited){ var led=doc.dateEdited.substring(0,19); ret.add(led,{'field':'lastEdited','type':'date'}) } return ret }") })
@Component
public class LuceneSearchRepository extends CouchDbRepositorySupportWithLucene<Search> {
	
	private LuceneDbConnector ldb;
	
	@Autowired
	protected LuceneSearchRepository(LuceneDbConnector db) {
		super(Search.class, db);
		this.ldb = db;
		initStandardDesignDocument();
	}
	
	public List<Client> getByCriteria(String firstName, String middleName, String lastName, String gender,
	                                  Map<String, String> identifiers, Map<String, String> attributes,
	                                  DateTime birthdateFrom, DateTime birthdateTo, DateTime lastEditFrom,
	                                  DateTime lastEditTo) {
		// create a simple query against the view/search function that we've
		// created
		LuceneQuery query = new LuceneQuery("Search", "by_all_criteria");
		
		Query qf = new Query(FilterType.AND);
		if (!StringUtils.isEmptyOrWhitespaceOnly(firstName)) {
			qf.likeWithWildCard(FIRST_NAME, firstName);
		}
		
		if (!StringUtils.isEmptyOrWhitespaceOnly(middleName)) {
			qf.likeWithWildCard(MIDDLE_NAME, middleName);
		}
		
		if (!StringUtils.isEmptyOrWhitespaceOnly(lastName)) {
			qf.likeWithWildCard(LAST_NAME, lastName);
		}
		
		if (!StringUtils.isEmptyOrWhitespaceOnly(gender)) {
			qf.eq(GENDER, gender);
		}
		
		if (identifiers != null && !identifiers.isEmpty()) {
			for (Map.Entry<String, String> entry : identifiers.entrySet()) {
				String identifierType = entry.getKey();
				String identifierValue = entry.getValue();
				if (!StringUtils.isEmptyOrWhitespaceOnly(identifierType)
				        && !StringUtils.isEmptyOrWhitespaceOnly(identifierValue)) {
					qf.eq(identifierType, identifierValue);
				}
			}
		}
		
		if (attributes != null && !attributes.isEmpty()) {
			for (Map.Entry<String, String> entry : attributes.entrySet()) {
				String attributeType = entry.getKey();
				String attributeValue = entry.getValue();
				if (!StringUtils.isEmptyOrWhitespaceOnly(attributeType)
				        && !StringUtils.isEmptyOrWhitespaceOnly(attributeValue)) {
					qf.eq(attributeType, attributeValue);
				}
			}
		}
		
		if (birthdateFrom != null && birthdateTo != null) {
			qf.between(BIRTH_DATE, birthdateFrom, birthdateTo);
		}
		
		if (lastEditFrom != null & lastEditTo != null) {
			qf.between(LAST_UPDATE, lastEditFrom, lastEditTo);
		}
		
		if (StringUtils.isEmptyOrWhitespaceOnly(qf.query())) {
			throw new RuntimeException("Atleast one search filter must be specified");
		}
		query.setQuery(qf.query());
		// stale must not be ok, as we've only just loaded the docs
		query.setStaleOk(false);
		query.setIncludeDocs(true);
		
		try {
			LuceneResult result = db.queryLucene(query);
			return ldb.asList(result, Client.class);
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public List<Client> getByCriteria(String query) {
		// create a simple query against the view/search function that we've
		// created
		LuceneQuery lq = new LuceneQuery("Search", "by_all_criteria");
		
		lq.setQuery(query);
		// stale must not be ok, as we've only just loaded the docs
		lq.setStaleOk(false);
		lq.setIncludeDocs(true);
		
		try {
			LuceneResult result = db.queryLucene(lq);
			return ldb.asList(result, Client.class);
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
