package org.fairdom.openseekapi.queries;

import java.util.List;

import ch.ethz.sis.openbis.generic.asapi.v3.IApplicationServerApi;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.common.search.SearchResult;
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
