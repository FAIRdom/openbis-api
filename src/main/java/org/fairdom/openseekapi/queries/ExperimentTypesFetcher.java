package org.fairdom.openseekapi.queries;

import java.util.Arrays;
import java.util.List;

import org.fairdom.openseekapi.ApplicationServerQuery;
import org.fairdom.openseekapi.facility.InvalidOptionException;
import org.json.simple.JSONObject;

import ch.ethz.sis.openbis.generic.asapi.v3.IApplicationServerApi;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.common.search.SearchResult;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.dataset.DataSet;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.dataset.fetchoptions.DataSetFetchOptions;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.dataset.search.DataSetSearchCriteria;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.experiment.ExperimentType;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.experiment.fetchoptions.ExperimentTypeFetchOptions;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.experiment.search.ExperimentTypeSearchCriteria;

public class ExperimentTypesFetcher{
	
	private static IApplicationServerApi as;
	private static String sessionToken;

	public void setOpenBisAccess(IApplicationServerApi as, String sessionToken) {
		ExperimentTypesFetcher.as = as;
		ExperimentTypesFetcher.sessionToken = sessionToken;
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
	

	public List<ExperimentType> all() {

		ExperimentTypeFetchOptions fetchOptions = new ExperimentTypeFetchOptions();
		// fetchOptions.withPropertyAssignments().withSemanticAnnotations();
		fetchOptions.withPropertyAssignments();

		ExperimentTypeSearchCriteria searchCriteria = new ExperimentTypeSearchCriteria();

		SearchResult<ExperimentType> types = as.searchExperimentTypes(sessionToken, searchCriteria, fetchOptions);
		return types.getObjects();

	}

	public List<ExperimentType> byCodes(List<String> codes) {

		ExperimentTypeFetchOptions fetchOptions = new ExperimentTypeFetchOptions();
		// fetchOptions.withPropertyAssignments().withSemanticAnnotations();
		fetchOptions.withPropertyAssignments();

		ExperimentTypeSearchCriteria searchCriteria = new ExperimentTypeSearchCriteria();
		searchCriteria.withCodes().thatIn(codes);
		SearchResult<ExperimentType> types = as.searchExperimentTypes(sessionToken, searchCriteria, fetchOptions);
		return types.getObjects();
	}
}
