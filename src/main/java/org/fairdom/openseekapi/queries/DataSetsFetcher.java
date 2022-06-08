package org.fairdom.openseekapi.queries;

import java.util.Arrays;
import java.util.List;

import org.fairdom.openseekapi.ApplicationServerQuery;
import org.fairdom.openseekapi.facility.InvalidOptionException;
import org.json.simple.JSONObject;

import ch.ethz.sis.openbis.generic.asapi.v3.IApplicationServerApi;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.dataset.DataSet;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.dataset.fetchoptions.DataSetFetchOptions;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.dataset.search.DataSetSearchCriteria;

public class DataSetsFetcher{

	private static final String TYPE_CODES = "typeCodes";
	private static final String TYPE_CODE = "typeCode";
	private static final String PERM_ID = "permId";
	
	private static IApplicationServerApi as;
	private static String sessionToken;
	
	private ApplicationServerQuery applicationServerQuery;
	
	public DataSetsFetcher(ApplicationServerQuery applicationServerQuery) {
		this.applicationServerQuery = applicationServerQuery;
	}
	
	
	public void setOpenBisAccess(IApplicationServerApi as, String sessionToken) {
		DataSetsFetcher.as = as;
		DataSetsFetcher.sessionToken = sessionToken;
	}
	
	private DataSetFetchOptions dataSetFetchOptions() {
		DataSetFetchOptions options = new DataSetFetchOptions();
		options.withProperties();
		options.withSample();
		options.withExperiment();
		options.withModifier();
		options.withRegistrator();
		options.withTags();
		options.withType();
		return options;
	}
	
	
	public List<DataSet> all() throws InvalidOptionException {
		return byAttribute(PERM_ID, "");
	}

	
	public List<DataSet> byAnyField(String searchTerm) {
		DataSetFetchOptions options = dataSetFetchOptions();

		DataSetSearchCriteria criterion = new DataSetSearchCriteria();
		criterion.withAnyField().thatContains(searchTerm);
		return as.searchDataSets(sessionToken, criterion, options).getObjects();
	}

	
	public List<DataSet> byAttribute(String attribute, List<String> values) throws InvalidOptionException {
		DataSetFetchOptions options = dataSetFetchOptions();

		DataSetSearchCriteria criterion = new DataSetSearchCriteria();
		criterion.withOrOperator();
		applicationServerQuery.updateCriterianForAttribute(criterion, attribute, values);

		return as.searchDataSets(sessionToken, criterion, options).getObjects();

	}

	
	public List<DataSet> byAttribute(String attribute, String value) throws InvalidOptionException {
		List<String> values = Arrays.asList(new String[] { value });
		return byAttribute(attribute, values);
	}

	
	public List<DataSet> byProperty(String property, String propertyValue) {
		DataSetSearchCriteria criterion = new DataSetSearchCriteria();
		criterion.withStringProperty(property).thatContains(propertyValue);

		DataSetFetchOptions options = dataSetFetchOptions();

		return as.searchDataSets(sessionToken, criterion, options).getObjects();
	}

	protected List<DataSet> byCriteria(DataSetSearchCriteria criteria, DataSetFetchOptions fetchOptions) {

		return as.searchDataSets(sessionToken, criteria, fetchOptions).getObjects();
	}

	public List<DataSet> byType(JSONObject query) throws InvalidOptionException {

		if (!query.containsKey(TYPE_CODE) && !query.containsKey(TYPE_CODES))
			throw new InvalidOptionException("Missing type code(s)");

		DataSetFetchOptions options = dataSetFetchOptions();
		DataSetSearchCriteria criterion = new DataSetSearchCriteria();

		if (query.containsKey(TYPE_CODES)) {
			List<String> codes = Arrays.asList(query.get(TYPE_CODES).toString().split(","));
			criterion.withType().withCodes().thatIn(codes);
		} else {
			String typeCode = (String) query.get(TYPE_CODE);
			criterion.withType().withCode().thatEquals(typeCode);
		}

		return as.searchDataSets(sessionToken, criterion, options).getObjects();
	}

}
