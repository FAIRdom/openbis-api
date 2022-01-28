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
import ch.ethz.sis.openbis.generic.asapi.v3.dto.experiment.Experiment;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.experiment.fetchoptions.ExperimentFetchOptions;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.experiment.search.ExperimentSearchCriteria;

public class ExperimentsFetcher {

	private static final String TYPE_CODES = "typeCodes";
	private static final String TYPE_CODE = "typeCode";
	private static final String PERM_ID = "permId";
	
	private static IApplicationServerApi as;
	private static String sessionToken;
	
	private ApplicationServerQuery applicationServerQuery;
	
	public ExperimentsFetcher(ApplicationServerQuery applicationServerQuery) {
		this.applicationServerQuery = applicationServerQuery;
	}
	
	
	public void setOpenBisAccess(IApplicationServerApi as, String sessionToken) {
		ExperimentsFetcher.as = as;
		ExperimentsFetcher.sessionToken = sessionToken;
	}
	
	private ExperimentFetchOptions experimentFetchOptions() {
		ExperimentFetchOptions options = new ExperimentFetchOptions();
		options.withProperties();
		options.withSamples();
		options.withDataSets();
		options.withProject();
		options.withModifier();
		options.withRegistrator();
		options.withTags();
		options.withType();
		return options;
	}

	
	public List<Experiment> all() throws InvalidOptionException {

		return byAttribute(PERM_ID, "");
	}

	
	public List<Experiment> byAnyField(String searchTerm) {
		ExperimentFetchOptions options = experimentFetchOptions();

		ExperimentSearchCriteria criterion = new ExperimentSearchCriteria();
		criterion.withAnyField().thatContains(searchTerm);
		return as.searchExperiments(sessionToken, criterion, options).getObjects();
	}

	
	public List<Experiment> byAttribute(String attribute, List<String> values)
			throws InvalidOptionException {
		ExperimentFetchOptions options = experimentFetchOptions();

		ExperimentSearchCriteria criterion = new ExperimentSearchCriteria();
		criterion.withOrOperator();
		applicationServerQuery.updateCriterianForAttribute(criterion, attribute, values);

		return as.searchExperiments(sessionToken, criterion, options).getObjects();

	}

	
	public List<Experiment> byAttribute(String attribute, String value) throws InvalidOptionException {
		List<String> values = Arrays.asList(new String[] { value });
		return byAttribute(attribute, values);

	}
	
	public List<Experiment> byProperty(String property, String propertyValue) {
		ExperimentSearchCriteria criterion = new ExperimentSearchCriteria();
		criterion.withProperty(property).thatContains(propertyValue);

		ExperimentFetchOptions options = experimentFetchOptions();

		return as.searchExperiments(sessionToken, criterion, options).getObjects();
	}

	
	@SuppressWarnings("unchecked")
	public List<Experiment> byType(JSONObject query) throws InvalidOptionException {

		if (!query.containsKey(TYPE_CODE) && !query.containsKey(TYPE_CODES))
			throw new InvalidOptionException("Missing type code(s)");

		ExperimentFetchOptions options = experimentFetchOptions();
		ExperimentSearchCriteria criterion = new ExperimentSearchCriteria();

		if (query.containsKey(TYPE_CODES)) {
			List<String> codes = Arrays.asList(query.get(TYPE_CODES).toString().split(","));
			criterion.withType().withCodes().thatIn(codes);

		} else {
			String typeCode = (String) query.get(TYPE_CODE);
			criterion.withType().withCode().thatEquals(typeCode);
		}

		return as.searchExperiments(sessionToken, criterion, options).getObjects();
	}
}
