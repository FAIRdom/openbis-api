package org.fairdom.openseekapi.queries;

import java.util.Arrays;
import java.util.List;

import org.fairdom.openseekapi.ApplicationServerQuery;
import org.fairdom.openseekapi.facility.InvalidOptionException;
import org.json.simple.JSONObject;

import ch.ethz.sis.openbis.generic.asapi.v3.IApplicationServerApi;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.common.search.SearchResult;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.dataset.DataSet;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.dataset.DataSetType;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.dataset.fetchoptions.DataSetFetchOptions;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.dataset.fetchoptions.DataSetTypeFetchOptions;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.dataset.search.DataSetSearchCriteria;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.dataset.search.DataSetTypeSearchCriteria;

public class DataSetTypesFetcher{

	private static IApplicationServerApi as;
	private static String sessionToken;

	public void setOpenBisAccess(IApplicationServerApi as, String sessionToken) {
		DataSetTypesFetcher.as = as;
		DataSetTypesFetcher.sessionToken = sessionToken;
	}
	
	public List<DataSetType> all() {

		DataSetTypeFetchOptions fetchOptions = new DataSetTypeFetchOptions();
		// fetchOptions.withPropertyAssignments().withSemanticAnnotations();
		fetchOptions.withPropertyAssignments();

		DataSetTypeSearchCriteria searchCriteria = new DataSetTypeSearchCriteria();

		SearchResult<DataSetType> types = as.searchDataSetTypes(sessionToken, searchCriteria, fetchOptions);
		return types.getObjects();

	}
	
	public List<DataSetType> byCode(String code) {

		DataSetTypeFetchOptions fetchOptions = new DataSetTypeFetchOptions();

		fetchOptions.withPropertyAssignments();

		DataSetTypeSearchCriteria searchCriteria = new DataSetTypeSearchCriteria();

		searchCriteria.withCode().thatEquals(code);

		SearchResult<DataSetType> types = as.searchDataSetTypes(sessionToken, searchCriteria, fetchOptions);
		return types.getObjects();
	}	
}
