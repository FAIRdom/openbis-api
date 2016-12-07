package org.fairdom;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.ethz.sis.openbis.generic.asapi.v3.dto.dataset.DataSet;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.experiment.Experiment;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.sample.Sample;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.space.Space;
import ch.ethz.sis.openbis.generic.dssapi.v3.dto.datasetfile.DataSetFile;
import ch.systemsx.cisd.openbis.generic.shared.api.v1.json.GenericObjectMapper;

/**
 * 
 * @author Stuart Owen
 *
 */

public class JSONCreator {

	private String json;

	public JSONCreator(List<?> result) {
		Map<String, Object> map = new HashMap<String, Object>();
		for (Object item : result) {
			if (item instanceof Experiment) {
				if (!map.containsKey("experiments")) {
					map.put("experiments", new ArrayList<Object>());
				}
				((List) map.get("experiments")).add(jsonMap((Experiment) item));
			}
			if (item instanceof DataSet) {
				if (!map.containsKey("datasets")) {
					map.put("datasets", new ArrayList<Object>());
				}
				((List) map.get("datasets")).add(jsonMap((DataSet) item));
			}
			if (item instanceof Sample) {
				if (!map.containsKey("samples")) {
					map.put("samples", new ArrayList<Object>());
				}
				((List) map.get("samples")).add(jsonMap((Sample) item));
			}
			if (item instanceof Space) {
				if (!map.containsKey("spaces")) {
					map.put("spaces", new ArrayList<Object>());
				}
				((List) map.get("spaces")).add(jsonMap((Space) item));
			}
			if (item instanceof DataSetFile) {
				if (!map.containsKey("datasetfiles")) {
					map.put("datasetfiles", new ArrayList<Object>());
				}
				((List) map.get("datasetfiles")).add(jsonMap((DataSetFile) item));
			}
		}
		GenericObjectMapper mapper = new GenericObjectMapper();
		StringWriter sw = new StringWriter();
		try {
			mapper.writeValue(sw, map);
		} catch (Exception ex) {
			System.err.println(ex.getMessage());
		}
		json = sw.toString();
	}

	public String getJSON() {
		return json;
	}

	private Map<String, Object> jsonMap(DataSetFile datasetFile) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("dataset", datasetFile.getDataSetPermId().getPermId());
		map.put("filePermId", datasetFile.getPermId());
		map.put("path", datasetFile.getPath());
		map.put("isDirectory", datasetFile.isDirectory());
		map.put("fileLength", datasetFile.getFileLength());
		return map;
	}

	private Map<String, Object> jsonMap(DataSet dataset) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("permId", dataset.getPermId().getPermId());
		map.put("code", dataset.getCode());
		map.put("properties", dataset.getProperties());
		map.put("modificationDate", dataset.getModificationDate());
		map.put("registrationDate", dataset.getRegistrationDate());
		if (dataset.getModifier() != null)
			map.put("modifier", dataset.getModifier().getUserId());
		else
			map.put("modifier", null);

		map.put("registerator", dataset.getRegistrator().getUserId());
		map.put("experiment", dataset.getExperiment().getPermId().getPermId());
		map.put("tags", dataset.getTags());

		Map<String, String> dsType = new HashMap<String, String>();
		if (dataset.getType() != null) {
			if (dataset.getType().getDescription() != null) {
				dsType.put("description", dataset.getType().getDescription());
			} else {
				dsType.put("description", "");
			}
			if (dataset.getType().getCode() != null) {
				dsType.put("code", dataset.getType().getCode());
			} else {
				dsType.put("code", "");
			}
		}
		map.put("dataset_type", dsType);

		List<String> sampleIds = new ArrayList<String>();
		if (dataset.getSample() != null) {
			sampleIds.add(dataset.getSample().getPermId().getPermId());
		}
		map.put("samples", sampleIds);
		return map;
	}

	private Map<String, Object> jsonMap(Experiment experiment) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("permId", experiment.getPermId().getPermId());
		map.put("code", experiment.getCode());
		map.put("project", experiment.getProject().getPermId().getPermId());
		map.put("properties", experiment.getProperties());
		map.put("modificationDate", experiment.getModificationDate());
		map.put("registrationDate", experiment.getRegistrationDate());
		map.put("identifier", experiment.getIdentifier().getIdentifier()); // E.g.
																			// /API-SPACE/API-PROJECT/E2
		if (experiment.getModifier() != null)
			map.put("modifier", experiment.getModifier().getUserId());
		else
			map.put("modifier", null);

		map.put("tags", experiment.getTags());
		map.put("registerator", experiment.getRegistrator().getUserId());
		Map<String, String> expType = new HashMap<String, String>();
		if (experiment.getType() != null) {
			if (experiment.getType().getDescription() != null) {
				expType.put("description", experiment.getType().getDescription());
			} else {
				expType.put("description", "");
			}
			if (experiment.getType().getCode() != null) {
				expType.put("code", experiment.getType().getCode());
			} else {
				expType.put("code", "");
			}
		}
		map.put("experiment_type", expType);
		List<String> sampleIds = new ArrayList<String>();
		for (Sample sample : experiment.getSamples()) {
			sampleIds.add(sample.getPermId().getPermId());
		}
		map.put("samples", sampleIds);
		List<String> datasetIds = new ArrayList<String>();
		for (DataSet dataset : experiment.getDataSets()) {
			datasetIds.add(dataset.getPermId().getPermId());
		}
		map.put("datasets", datasetIds);
		return map;
	}

	private Map<String, Object> jsonMap(Sample sample) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("permId", sample.getPermId().getPermId());
		map.put("code", sample.getCode());
		map.put("properties", sample.getProperties());
		map.put("identifier", sample.getIdentifier().getIdentifier());
		map.put("modificationDate", sample.getModificationDate());
		map.put("registrationDate", sample.getRegistrationDate());
		if (sample.getModifier() != null)
			map.put("modifier", sample.getModifier().getUserId());
		else
			map.put("modifier", null);

		map.put("registerator", sample.getRegistrator().getUserId());

		Map<String, String> sampleType = new HashMap<String, String>();
		if (sample.getType() != null) {
			if (sample.getType().getDescription() != null) {
				sampleType.put("description", sample.getType().getDescription());
			} else {
				sampleType.put("description", "");
			}
			if (sample.getType().getCode() != null) {
				sampleType.put("code", sample.getType().getCode());
			} else {
				sampleType.put("code", "");
			}
			sampleType.put("code", sample.getType().getCode());
		}
		map.put("sample_type", sampleType);

		if (sample.getExperiment() != null) {
			map.put("experiment", sample.getExperiment().getPermId().getPermId());
		}
		map.put("tags", sample.getTags());
		List<String> datasetIds = new ArrayList<String>();
		for (DataSet dataset : sample.getDataSets()) {
			datasetIds.add(dataset.getPermId().getPermId());
		}
		map.put("datasets", datasetIds);
		return map;
	}

	private Map<String, Object> jsonMap(Space space) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("permId", space.getPermId().getPermId());
		map.put("code", space.getCode());
		map.put("description", space.getDescription());

		map.put("modificationDate", space.getModificationDate());
		map.put("registrationDate", space.getRegistrationDate());
		return map;
	}

}