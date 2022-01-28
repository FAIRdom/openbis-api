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
import ch.ethz.sis.openbis.generic.asapi.v3.dto.sample.Sample;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.sample.fetchoptions.SampleFetchOptions;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.sample.search.SampleSearchCriteria;

public class SamplesFetcher{

	private static final String TYPE_CODES = "typeCodes";
	private static final String TYPE_CODE = "typeCode";
	private static final String PERM_ID = "permId";
	
	private static IApplicationServerApi as;
	private static String sessionToken;
	
	private ApplicationServerQuery applicationServerQuery;
	
	public SamplesFetcher(ApplicationServerQuery applicationServerQuery) {
		this.applicationServerQuery = applicationServerQuery;
	}
	
	public void setOpenBisAccess(IApplicationServerApi as, String sessionToken) {
		SamplesFetcher.as = as;
		SamplesFetcher.sessionToken = sessionToken;
	}
	

	private SampleFetchOptions sampleFetchOptions() {
		SampleFetchOptions options = new SampleFetchOptions();
		options.withProperties();
		options.withExperiment();
		options.withDataSets();
		options.withModifier();
		options.withRegistrator();
		options.withTags();
		options.withType();
		return options;
	}
	
	public List<Sample> all() throws InvalidOptionException {
		return byAttribute(PERM_ID, "");
	}
	
	public List<Sample> byAnyField(String searchTerm) {
		SampleFetchOptions options = sampleFetchOptions();

		SampleSearchCriteria criterion = new SampleSearchCriteria();
		criterion.withAnyField().thatContains(searchTerm);
		return as.searchSamples(sessionToken, criterion, options).getObjects();
	}

	public List<Sample> byAttribute(String attribute, List<String> values) throws InvalidOptionException {
		SampleFetchOptions options = sampleFetchOptions();

		SampleSearchCriteria criterion = new SampleSearchCriteria();

		criterion.withOrOperator();
		applicationServerQuery.updateCriterianForAttribute(criterion, attribute, values);

		return as.searchSamples(sessionToken, criterion, options).getObjects();
	}

	public List<Sample> byAttribute(String attribute, String value) throws InvalidOptionException {
		List<String> values = Arrays.asList(new String[] { value });
		return byAttribute(attribute, values);
	}

	public List<Sample> byProperty(String property, String propertyValue) {
		SampleSearchCriteria criterion = new SampleSearchCriteria();
		criterion.withProperty(property).thatContains(propertyValue);

		SampleFetchOptions options = sampleFetchOptions();

		return as.searchSamples(sessionToken, criterion, options).getObjects();
	}

	@SuppressWarnings("unchecked")
	public List<Sample> byType(JSONObject query) throws InvalidOptionException {

		if (!query.containsKey(TYPE_CODE) && !query.containsKey(TYPE_CODES))
			throw new InvalidOptionException("Missing type code(s)");

		SampleFetchOptions options = sampleFetchOptions();
		SampleSearchCriteria criterion = new SampleSearchCriteria();

		if (query.containsKey(TYPE_CODES)) {
			List<String> codes = Arrays.asList(query.get(TYPE_CODES).toString().split(","));
			criterion.withType().withCodes().thatIn(codes);
		} else {
			String typeCode = (String) query.get(TYPE_CODE);
			criterion.withType().withCode().thatEquals(typeCode);
		}

		return as.searchSamples(sessionToken, criterion, options).getObjects();
	}

}
