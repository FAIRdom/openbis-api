package org.fairdom.openseekapi;

import ch.ethz.sis.openbis.generic.asapi.v3.dto.dataset.DataSet;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.experiment.Experiment;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.sample.Sample;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.sample.SampleType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.IllegalFormatConversionException;
import java.util.List;
import java.util.Map;

import org.fairdom.openseekapi.datastore.DataStoreDownload;
import org.fairdom.openseekapi.datastore.DataStoreQuery;
import org.fairdom.openseekapi.facility.InvalidOptionException;
import org.fairdom.openseekapi.facility.JSONCreator;
import org.fairdom.openseekapi.general.Authentication;
import org.fairdom.openseekapi.general.AuthenticationException;
import org.fairdom.openseekapi.general.QueryType;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

/**
 * The entry point, containing the main method
 * 
 * @author Stuart Owen
 *
 */
public class OpenSeekEntry {

	private static final String IS_TEST_KEY = "is_test";
	
	public static final String APPLICATION_SERVER_KEY = "as";
	public static final String DATA_STORE_SERVER_KEY = "dss";
	
	public static final String SESSION_TOKEN = "sessionToken";
	
	private static final String ATTRIBUTE = "attribute";
	private static final String PROPERTY = "property";
	private static final String QUERY_TYPE = "queryType";
	private static final String ATTRIBUTE_VALUE = "attributeValue";
	
	public static final String EXPERIMENT = "Experiment";
	public static final String DATA_SET = "DataSet";
	public static final String SAMPLE = "Sample";
	
	private static final String LIST_EXPERIMENT_TYPES = "experimenttypes";
	private static final String LIST_DATASET_TYPES = "datasettypes";
	private static final String LIST_SAMPLE_TYPES = "sampletypes";
	
	public static final String EXPERIMENT_TYPE = "ExperimentType";
	public static final String DATA_SET_TYPE = "DataSetType";
	public static final String SAMPLE_TYPE = "SampleType";
	public static final String ENTITY_TYPE = "entityType";
	
	private static final String USERNAME = "username";
	private static final String PASSWORD = "password";

	private static final String FILE = "file";
	private static final String DEST_KEY = "dest";
	private static final String SOURCE_KEY = "source";

	private static final String PERM_ID = "permID";
	private static final String DOWNLOAD_TYPE = "downloadType";

	private static final String ERROR_MESSAGE_END = "[/MESSAGE]";
	private static final String ERROR_MESSAGE_BEGIN = "[MESSAGE]";

	public static void main(String[] args) throws InvalidOptionException {
		new OpenSeekEntry(args).execute();
	}

	private String[] args;
	final ObjectMapper mapper;
	public static boolean is_test = false;
	
	public OpenSeekEntry(String args[]) {
		this.args = args;
		this.mapper = new ObjectMapper();
	}

	public void execute() throws InvalidOptionException {
		OptionParser options = null;
		try {
			options = new OptionParser(args);
		} catch (InvalidOptionException e) {
			System.err.println("Invalid option: " + e.getMessage());
			System.exit(-1);
		} catch (ParseException pe) {
			System.out.println("position: " + pe.getPosition());
			System.out.println(pe);
			exit(-1);
		}
		// If in test, we allow http
		checkIfInTest(options);
			
		String json = "";
		try {
			switch (options.getAction()) {
			case LOGIN:
				json = doLogin(options);
				break;
			case AS_QUERY:
				json = doApplicationServerQuery(options);
				break;
			case DS_QUERY:
				json = doDataStoreQuery(options);
				break;
			case DOWNLOAD:
				json = doDownload(options);
				break;

			default:
				throw new InvalidOptionException("Unable to determine action");
			}
		} catch (Exception ex) {
			System.out.println(ERROR_MESSAGE_BEGIN+ex.getMessage()+ERROR_MESSAGE_END);
			ex.printStackTrace();
			exit(-1);
		}
		System.out.println(json);
		exit(0);
	}

	protected String doApplicationServerQuery(OptionParser options)
			throws InvalidOptionException, AuthenticationException, JsonProcessingException {
		JSONObject endpoints = options.getEndpoints();
		JSONObject query = options.getQuery();

		ApplicationServerQuery asQuery = new ApplicationServerQuery(endpoints.get(APPLICATION_SERVER_KEY).toString(),
				endpoints.get(SESSION_TOKEN).toString());
		List<? extends Object> result;

		QueryType queryType = QueryType.valueOf(query.get(QUERY_TYPE).toString());
		final Object query_entity_type = query.get(ENTITY_TYPE);
		
		switch (queryType) {
		case ALL:
			result = asQuery.allEntities(query_entity_type.toString());

			if (query_entity_type.equals(SAMPLE_TYPE)) {
				return mapToJsonString(LIST_SAMPLE_TYPES, result);
			}
			if (query_entity_type.equals(DATA_SET_TYPE)) {
				return mapToJsonString(LIST_DATASET_TYPES, result);
			}
			if (query_entity_type.equals(EXPERIMENT_TYPE)) {
				return mapToJsonString(LIST_EXPERIMENT_TYPES, result);
			}

			break;
		case PROPERTY:
			result = asQuery.query(query_entity_type.toString(), QueryType.PROPERTY,
					query.get(PROPERTY).toString(), query.get("propertyValue").toString());
			break;
		case ATTRIBUTE:
			List<String> attributeValues = options.constructAttributeValues(query.get(ATTRIBUTE_VALUE).toString());
			result = asQuery.query(query_entity_type.toString(), QueryType.ATTRIBUTE,
					query.get(ATTRIBUTE).toString(), attributeValues);

			if (query_entity_type.equals(SAMPLE_TYPE)) {
				return mapToJsonString(LIST_SAMPLE_TYPES, result);
			}
			if (query_entity_type.equals(DATA_SET_TYPE)) {
				return mapToJsonString(LIST_DATASET_TYPES, result);
			}
			if (query_entity_type.equals(EXPERIMENT_TYPE)) {
				return mapToJsonString(LIST_EXPERIMENT_TYPES, result);
			}

			break;
		case TYPE:
			if (query_entity_type.equals(SAMPLE)) {
				List<Sample> samples = asQuery.getSamples().byType(query);
				return new JSONCreator(samples).getJSON();
			} else if (query_entity_type.equals(DATA_SET)) {
				List<DataSet> sets = asQuery.getDataSets().byType(query);
				return new JSONCreator(sets).getJSON();
			} else if (query_entity_type.equals(EXPERIMENT)) {
				List<Experiment> exps = asQuery.getExperiments().byType(query);
				return new JSONCreator(exps).getJSON();
			} else {
				throw new InvalidOptionException("Type query for unsupported type: " + query_entity_type);
			}

		case SEMANTIC:
			if (query_entity_type.equals(SAMPLE_TYPE)) {
				List<SampleType> types = asQuery.getSampleTypes().bySemantic(query);
				return mapToJsonString(LIST_SAMPLE_TYPES, types);
			} else {
				throw new InvalidOptionException("Semantic query for unsupported type: " + query_entity_type);
			}
		default:
			throw new InvalidOptionException("Unrecognized query type: " + queryType);

		}

		return new JSONCreator(result).getJSON();
	}

	protected String doDataStoreQuery(OptionParser options) throws InvalidOptionException {
		JSONObject endpoints = options.getEndpoints();
		JSONObject query = options.getQuery();
		DataStoreQuery dssQuery = new DataStoreQuery(endpoints.get(DATA_STORE_SERVER_KEY).toString(),
				endpoints.get(SESSION_TOKEN).toString());
		List<? extends Object> result;
		if (query.get(QUERY_TYPE).toString().equals(QueryType.PROPERTY.toString())) {
			result = dssQuery.query(query.get(ENTITY_TYPE).toString(), QueryType.PROPERTY,
					query.get(PROPERTY).toString(), query.get("propertyValue").toString());
		} else {
			List<String> attributeValues = options.constructAttributeValues(query.get(ATTRIBUTE_VALUE).toString());
			result = dssQuery.query(query.get(ENTITY_TYPE).toString(), QueryType.ATTRIBUTE,
					query.get(ATTRIBUTE).toString(), attributeValues);
		}
		return new JSONCreator(result).getJSON();
	}

	private String doDownload(OptionParser options) throws IOException {
		JSONObject endpoints = options.getEndpoints();
		JSONObject download = options.getDownload();

		DataStoreDownload dssDownload = new DataStoreDownload(endpoints.get(DATA_STORE_SERVER_KEY).toString(),
				endpoints.get(SESSION_TOKEN).toString());

		String downloadType = download.get(DOWNLOAD_TYPE).toString();
		String permID = download.get(PERM_ID).toString();
		String source = download.get(SOURCE_KEY).toString();
		String dest = download.get(DEST_KEY).toString();

		String downloadInfo = "";
		if (downloadType.equals(FILE)) {
			dssDownload.downloadSingleFile(permID, source, dest);
			downloadInfo = downloadInfo + "Download file " + permID + "#" + source + " into " + dest;
		} else if (downloadType.equals("folder")) {
			dssDownload.downloadFolder(permID, source, dest);
			downloadInfo = downloadInfo + "Download folder " + permID + "#" + source + " into " + dest;
		} else if (downloadType.equals("dataset")) {
			dssDownload.downloadDataSetFiles(permID, dest);
			downloadInfo = downloadInfo + "Download dataset files of " + permID + " into " + dest;
		} else {
			downloadInfo = downloadInfo + "Invalid download type, nothing to download";
		}
		return ("{\"download_info\":" + "\"" + downloadInfo + "\"" + "}");
	}

	private String doLogin(OptionParser options) throws Exception {

		JSONObject account = options.getAccount();
		JSONObject endpoints = options.getEndpoints();

		Authentication au = new Authentication(endpoints.get(APPLICATION_SERVER_KEY).toString(), account.get(USERNAME).toString(),
				account.get(PASSWORD).toString());
		String sessionToken = au.sessionToken();
		return ("{\"token\":" + "\"" + sessionToken + "\"" + "}");
	}

	private boolean checkIfInTest(OptionParser options) throws InvalidOptionException {
		JSONObject endpoints = options.getEndpoints();
		if (!endpoints.containsKey(IS_TEST_KEY))
			return false;
		String is_test_txt = endpoints.get(IS_TEST_KEY).toString();

		try
		{
			is_test = Boolean.parseBoolean(is_test_txt);
		}
		catch (IllegalFormatConversionException e)
		{
			throw new InvalidOptionException("Is_test is not a proper boolean, please use only true and false (lowercase)" );
		}
		return is_test;
	}

	protected void exit(int code) {
		System.exit(code);
	}

	protected String mapToJsonString(String listName, List<?> objects) throws JsonProcessingException {

		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		// necessary to make writer as df is not thread safe so cannot be set globabaly

		ObjectWriter writer = mapper.writer(df);
		Map<String, List<?>> map = new HashMap<>();
		map.put(listName, objects);
		return writer.writeValueAsString(map);
	}

}
