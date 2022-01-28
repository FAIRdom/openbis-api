package org.fairdom.openseekapi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ch.ethz.sis.openbis.generic.asapi.v3.IApplicationServerApi;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.common.search.AbstractEntitySearchCriteria;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.space.Space;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.space.fetchoptions.SpaceFetchOptions;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.space.search.SpaceSearchCriteria;
import ch.systemsx.cisd.common.spring.HttpInvokerUtils;
import java.util.Map;

import org.fairdom.openseekapi.facility.InvalidOptionException;
import org.fairdom.openseekapi.general.QueryType;
import org.fairdom.openseekapi.queries.DataSetTypesFetcher;
import org.fairdom.openseekapi.queries.DataSetsFetcher;
import org.fairdom.openseekapi.queries.ExperimentTypesFetcher;
import org.fairdom.openseekapi.queries.ExperimentsFetcher;
import org.fairdom.openseekapi.queries.SampleTypesFetcher;
import org.fairdom.openseekapi.queries.SamplesFetcher;

/**
 * @author Quyen Nguyen
 * @author Stuart Owen
 */
public class ApplicationServerQuery {
	private static final String PERM_ID = "permId";
	private static final String SPACE = "Space";
	private static final String CODE = "CODE";
	
	private static IApplicationServerApi as;
	private static String endpoint;
	private static String sessionToken;
	
	static final int TIMEOUT = 500000;

	private ExperimentsFetcher 	experiments;
	private SamplesFetcher 		samples;
	private DataSetsFetcher		dataSets;
	
	private ExperimentTypesFetcher 	experimentTypes;
	private SampleTypesFetcher 		sampleTypes;
	private DataSetTypesFetcher		dataSetTypes;
	
	public static IApplicationServerApi as(String endpoint) {
		return HttpInvokerUtils.createServiceStub(IApplicationServerApi.class,
				endpoint + IApplicationServerApi.SERVICE_URL, TIMEOUT);

	}

	public ApplicationServerQuery(String startEndpoint, String startSessionToken) {
		endpoint = startEndpoint;
		sessionToken = startSessionToken;
		as = ApplicationServerQuery.as(endpoint);
		
		experiments = new ExperimentsFetcher(this);
		experiments.setOpenBisAccess(as, sessionToken);
		
		samples = new SamplesFetcher(this);
		samples.setOpenBisAccess(as, sessionToken);
		
		dataSets = new DataSetsFetcher(this);
		dataSets.setOpenBisAccess(as, sessionToken);
		
		experimentTypes = new ExperimentTypesFetcher();
		experimentTypes.setOpenBisAccess(as, sessionToken);
		
		sampleTypes = new SampleTypesFetcher();
		sampleTypes.setOpenBisAccess(as, sessionToken);
		
		dataSetTypes = new DataSetTypesFetcher();
		dataSetTypes.setOpenBisAccess(as, sessionToken);
	}

	/**
	 * get the experiment fetcher.
	 * Fetcher is removed so the call can be natural: getExperiments().byType()
	 * @return
	 */
	public ExperimentsFetcher getExperiments() {
		return experiments;
	}
	
	/**
	 * get the sample fetcher.
	 * Fetcher is removed so the call can be natural: getSamples().byType()
	 * @return
	 */
	public SamplesFetcher getSamples() {
		return samples;
	}
	
	/**
	 * get the dataset fetcher.
	 * Fetcher is removed so the call can be natural: getDataSets().byType()
	 * @return
	 */
	public DataSetsFetcher getDataSets() {
		return dataSets;
	}

	public ExperimentTypesFetcher getExperimentTypes() {
		return experimentTypes;
	}

	public SampleTypesFetcher getSampleTypes() {
		return sampleTypes;
	}

	public DataSetTypesFetcher getDataSetTypes() {
		return dataSetTypes;
	}

	public List<? extends Object> allEntities(String type) throws InvalidOptionException {

		switch (type) {
		case OpenSeekEntry.EXPERIMENT:
			return experiments.all();
		case OpenSeekEntry.SAMPLE:
			return samples.all();
		case OpenSeekEntry.DATA_SET:
			return dataSets.all();
		case SPACE:
			return allSpaces();
		case OpenSeekEntry.SAMPLE_TYPE:
			return sampleTypes.all();
		case OpenSeekEntry.DATA_SET_TYPE:
			return dataSetTypes.all();
		case OpenSeekEntry.EXPERIMENT_TYPE:
			return experimentTypes.all();
		default:
			throw new InvalidOptionException("Unrecognised type: " + type);
		}
	}

	public List<? extends Object> query(String type, QueryType queryType, String key, List<String> values)
			throws InvalidOptionException {
		List<? extends Object> result = null;
		if (queryType == QueryType.ATTRIBUTE) {
			switch (type) {
			case OpenSeekEntry.EXPERIMENT:
				result = experiments.byAttribute(key, values);
				break;
			case OpenSeekEntry.SAMPLE:
				result = samples.byAttribute(key, values);
				break;
			case OpenSeekEntry.DATA_SET:
				result = dataSets.byAttribute(key, values);
				break;
			case SPACE:
				result = spacesByAttribute(key, values);
				break;
			case OpenSeekEntry.SAMPLE_TYPE:
				if (!CODE.equals(key))
					throw new InvalidOptionException("Unsupported attribute: " + key);
				result = sampleTypes.byCodes(values);
				break;
			case OpenSeekEntry.DATA_SET_TYPE:
				if (!CODE.equals(key))
					throw new InvalidOptionException("Unsupported attribute: " + key);
				result = dataSetTypes.byCode(values.get(0));
				break;
			case OpenSeekEntry.EXPERIMENT_TYPE:
				if (!CODE.equals(key))
					throw new InvalidOptionException("Unsupported attribute: " + key);
				result = experimentTypes.byCodes(values);
				break;
			default:
				throw new InvalidOptionException("Unrecognised type: " + type);
			}
		} else {
			throw new InvalidOptionException("It is only possible to query by ATTRIBUTE when using an array of values");
		}

		return result;
	}

	public List<? extends Object> query(String type, QueryType queryType, String key, String value)
			throws InvalidOptionException {
		List<? extends Object> result = null;
		if (null == queryType) {
			throw new InvalidOptionException("Unrecognised query type");
		} else
			switch (queryType) {
			case PROPERTY:
				switch (type) {
				case OpenSeekEntry.EXPERIMENT:
					result = experiments.byProperty(key, value);
					break;
				case OpenSeekEntry.SAMPLE:
					result = samples.byProperty(key, value);
					break;
				case OpenSeekEntry.DATA_SET:
					result = dataSets.byProperty(key, value);
					break;
				default:
					throw new InvalidOptionException("Unrecognised entity type: " + type);
				}
				break;
			case ATTRIBUTE:
				List<String> values = new ArrayList<>();
				values.add(value);
				result = query(type, queryType, key, values);
				break;
			default:
				throw new InvalidOptionException("Unrecognised query type");
			}

		return result;
	}

	public List<Space> allSpaces() throws InvalidOptionException {
		return spacesByAttribute(PERM_ID, "");
	}

	public List<Space> spacesByAttribute(String attribute, List<String> values) throws InvalidOptionException {
		SpaceFetchOptions options = new SpaceFetchOptions();
		// options.withProjects().withExperiments().withDataSets();
		// options.withSamples().withDataSets();
		SpaceSearchCriteria criterion = new SpaceSearchCriteria();

		criterion.withOrOperator();
		for (String value : values) {
			criterion.withPermId().thatContains(value);
		}

		return as.searchSpaces(sessionToken, criterion, options).getObjects();
	}

	public List<Space> spacesByAttribute(String attribute, String value) throws InvalidOptionException {
		List<String> values = Arrays.asList(new String[] { value });
		return spacesByAttribute(attribute, values);
	}

	protected <K> boolean notNullAtKey(Map map, K key) {
		return map.containsKey(key) && (map.get(key) != null);
	}

	public void updateCriterianForAttribute(AbstractEntitySearchCriteria<?> criteria, String key, List<String> values)
			throws InvalidOptionException {
		if (key.equalsIgnoreCase("permid")) {
			for (String value : values) {
				criteria.withPermId().thatContains(value);
			}
		} else {
			throw new InvalidOptionException("Invalid attribute name:" + key);
		}
	}

}
