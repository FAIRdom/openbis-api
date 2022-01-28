package org.fairdom.openseekapi;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.fairdom.openseekapi.ApplicationServerQuery;
import org.fairdom.openseekapi.facility.InvalidOptionException;
import org.fairdom.openseekapi.facility.JSONCreator;
import org.fairdom.openseekapi.facility.SslCertificateHelper;
import org.fairdom.openseekapi.general.Authentication;
import org.fairdom.openseekapi.general.AuthenticationException;
import org.fairdom.openseekapi.general.QueryType;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.Before;
import org.junit.Test;

import ch.ethz.sis.openbis.generic.asapi.v3.dto.dataset.DataSet;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.dataset.DataSetType;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.experiment.Experiment;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.experiment.ExperimentType;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.sample.Sample;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.sample.SampleType;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.space.Space;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.junit.Ignore;

/**
 * @author Quyen Nugyen
 * @author Stuart Owen
 */
public class ApplicationServerQueryTest {
	
	private String endpoint;
	private String sessionToken;
	private ApplicationServerQuery query;

	@Before
	public void setUp() throws AuthenticationException {
		
		OpenSeekEntry.is_test = true;
		
            //SslCertificateHelper.addTrustedUrl("https://openbis-api.fair-dom.org/openbis/openbis");   
            SslCertificateHelper.addTrustedUrl(Commons.TEST_OPENBIS_URL);            
            
            //endpoint = "https://openbis-api.fair-dom.org/openbis/openbis";
            endpoint = Commons.TEST_OPENBIS_URL;
            //Authentication au = new Authentication(endpoint, "apiuser","apiuser");
            Authentication au = new Authentication(endpoint, Commons.TEST_OPENBIS_USER,Commons.TEST_OPENBIS_PASSWORD);
            sessionToken = au.sessionToken();
            query = new ApplicationServerQuery(endpoint, sessionToken);
	}
        
        protected ApplicationServerQuery localQuery() throws AuthenticationException {
                String localAs = Commons.TEST_OPENBIS_URL;
                Authentication au = new Authentication(localAs, Commons.TEST_OPENBIS_USER, Commons.TEST_OPENBIS_PASSWORD);
                return new ApplicationServerQuery(localAs, au.sessionToken());
        }

	@Test
        @Ignore
	public void queryBySpace() throws Exception {
		List<? extends Object> result = query.query("Space", QueryType.ATTRIBUTE, "permID", "");
		assertTrue(result.size() > 0);

		List<String> values = new ArrayList<>();
		values.add("");
		result = query.query("Space", QueryType.ATTRIBUTE, "permID", values);
		assertTrue(result.size() > 0);
	}

	@Test
        //@Ignore
	public void getSpacesByPermIDs() throws Exception {
		List<String> permids = new ArrayList<>();
		permids.add("SEEK");
		permids.add("MATERIALS");
		List<Space> spaces = query.spacesByAttribute("permId", permids);
		assertEquals(2, spaces.size());
		String json = new JSONCreator(spaces).getJSON();
		assertTrue(JSONHelper.isValidJSON(json));
	}

	@Test
	public void getSpacesByPermID() throws Exception {
		List<String> permids = new ArrayList<>();
		permids.add("SEEK");
		List<Space> spaces = query.spacesByAttribute("permId", permids);
		assertEquals(1, spaces.size());
		String json = new JSONCreator(spaces).getJSON();
		assertTrue(JSONHelper.isValidJSON(json));

		spaces = query.spacesByAttribute("permId", "SEEK");
		assertEquals(1, spaces.size());
		json = new JSONCreator(spaces).getJSON();
                assertTrue(JSONHelper.isValidJSON(json));
		JSONObject jsonObj = JSONHelper.processJSON(json);
		assertNotNull(jsonObj.get("spaces"));
		JSONObject space = (JSONObject) ((JSONArray) jsonObj.get("spaces")).get(0);
                
                /* Space should no longer contain experiments or sampels, too many entries and work
                to fetch them. And not used anywhere
		JSONArray experiments = (JSONArray) space.get("experiments");
		JSONArray datasets = (JSONArray) space.get("datasets");
		JSONArray projects = (JSONArray) space.get("projects");
		assertEquals(1, experiments.size());
		assertEquals("20151216143716562-2", (String) experiments.get(0));

		assertEquals(1, projects.size());
		assertEquals("20151216135152196-1", (String) projects.get(0));
		assertEquals(12, datasets.size());
		assertTrue(datasets.contains("20160210130359377-22"));
                */
	}
        
	@Test
	public void getSpacesByPermIDGivesOnlySpaceNoDetails() throws Exception {

		List<Space> spaces = query.spacesByAttribute("permId", "SEEK");
		assertEquals(1, spaces.size());
		String json = new JSONCreator(spaces).getJSON();
                assertTrue(JSONHelper.isValidJSON(json));
		JSONObject jsonObj = JSONHelper.processJSON(json);
		assertNotNull(jsonObj.get("spaces"));
		JSONObject space = (JSONObject) ((JSONArray) jsonObj.get("spaces")).get(0);

		JSONArray experiments = (JSONArray) space.get("experiments");
		JSONArray datasets = (JSONArray) space.get("datasets");
		JSONArray projects = (JSONArray) space.get("projects");
		JSONArray samples = (JSONArray) space.get("samples");

		assertEquals(0, experiments.size());
		assertEquals(0, datasets.size());
		assertEquals(0, projects.size());
		assertEquals(0, samples.size());
	}        
        
	@Test
        @Ignore("It should no longer fetch those")
	public void getSpacesByPermIDGetsAllDataSetsExperimentAndSamplesOnes() throws Exception {

		List<Space> spaces = query.spacesByAttribute("permId", "API-SPACE");
		assertEquals(1, spaces.size());
                
		String json = new JSONCreator(spaces).getJSON();
		JSONObject jsonObj = JSONHelper.processJSON(json);
		assertNotNull(jsonObj.get("spaces"));
		JSONObject space = (JSONObject) ((JSONArray) jsonObj.get("spaces")).get(0);
		JSONArray experiments = (JSONArray) space.get("experiments");
		JSONArray datasets = (JSONArray) space.get("datasets");
		JSONArray projects = (JSONArray) space.get("projects");
		assertEquals(1, experiments.size());
		assertEquals("20151216143716562-2", (String) experiments.get(0));

		assertEquals(1, projects.size());
		assertEquals("20151216135152196-1", (String) projects.get(0));
		assertEquals(12, datasets.size());
		assertTrue(datasets.contains("20160210130359377-22"));
		assertTrue(datasets.contains("20170907185702684-36"));
		assertTrue(datasets.contains("20171002190934144-40"));
	}        

	@Test
        //@Ignore
	public void getAllSpaces() throws Exception {
		List<Space> spaces = query.spacesByAttribute("permId", "");
		assertTrue(spaces.size() > 1);
		String json = new JSONCreator(spaces).getJSON();
		assertTrue(JSONHelper.isValidJSON(json));
	}
        
	@Test
	public void allSpacesGivesAll() throws Exception {
            List<Space> spaces = query.allSpaces();
            //spaces.forEach( s -> System.out.println(s.getCode()));
            assertEquals(7,spaces.size());
	}    
        
        @Test 
        public void allEntitiesWorks() throws InvalidOptionException {
            List<String> types = Arrays.asList("Sample","DataSet","Experiment","Space");
            for (String type : types)
                assertFalse(query.allEntities(type).isEmpty());
        }

	@Test
        @Ignore
	public void getExperimentsByPermID() throws Exception {
		List<String> permids = new ArrayList<>();
		permids.add("20151216143716562-2");
		List<Experiment> experiments = query.getExperiments().byAttribute("permId", permids);
		assertEquals(1, experiments.size());
		String json = new JSONCreator(experiments).getJSON();
		assertTrue(JSONHelper.isValidJSON(json));

		experiments = query.getExperiments().byAttribute("permId", "20151216143716562-2");
		assertEquals(1, experiments.size());
		json = new JSONCreator(experiments).getJSON();
		assertTrue(JSONHelper.isValidJSON(json));
	}

	@Test
        //@Ignore
	public void getExperimentsByPermIDs() throws Exception {
		List<String> permids = new ArrayList<>();
		//permids.add("20151216112932823-1");
		//permids.add("20151216143716562-2");
		permids.add("20180418141729157-47");
		permids.add("20180424181519696-54");
		List<Experiment> experiments = query.getExperiments().byAttribute("permId", permids);
		assertEquals(2, experiments.size());
		String json = new JSONCreator(experiments).getJSON();
		assertTrue(JSONHelper.isValidJSON(json));
	}

	@Test
        //@Ignore
	public void getAllExperiments() throws Exception {

		List<Experiment> experiments = query.getExperiments().byAttribute("permId", "");
		assertTrue(experiments.size() > 0);
		String json = new JSONCreator(experiments).getJSON();
		assertTrue(JSONHelper.isValidJSON(json));
	}
        
	@Test
	public void allExperimentsGetsAll() throws Exception {
            List<Experiment> experiments = query.getExperiments().all();
            assertEquals(24,experiments.size());
	}  
        
        @Test
        public void experimentsByMultipleTypeCodesWorks() throws AuthenticationException, InvalidOptionException {
            
            //local as it came from the new API
            query = localQuery();
            
            Map<String,Object> qMap = new HashMap<>();
            qMap.put("entityType", "Experiment");
            qMap.put("queryType",QueryType.TYPE.name());
            qMap.put("typeCodes","DEFAULT_EXPERIMENT,UNKNOWN");
            
            JSONObject crit = new JSONObject(qMap);
            
            
            List<Experiment> res = query.getExperiments().byType(crit);
            assertNotNull(res);
            assertEquals(4, res.size());
            
            List<String> exp = Arrays.asList("DEFAULT_EXPERIMENT","UNKNOWN");
            res.forEach( s -> {
                assertTrue(exp.contains(s.getType().getCode()));
            });            
            
            qMap.put("typeCodes","TZ_ASSAY_NOT_DEFINED");           
            crit = new JSONObject(qMap);
            res = query.getExperiments().byType(crit);
            
            assertTrue(res.isEmpty());
        }
        
        
        @Test
        public void allExprimentTypes() throws AuthenticationException {
                        
            List<ExperimentType> res = query.getExperimentTypes().all();
            assertNotNull(res);
            assertFalse(res.isEmpty());
            assertEquals(6,res.size());
            
        }
        
	@Test
	public void experimentTypesByCode() throws Exception {
            query = localQuery();
            List<ExperimentType> res = query.getExperimentTypes().byCodes(Arrays.asList("DEFAULT_EXPERIMENT"));
            assertEquals(1, res.size());
            assertEquals("DEFAULT_EXPERIMENT", res.get(0).getCode());
	}   
        
	@Test
	public void eperimentTypesByCodes() throws Exception {
            query = localQuery();
            List<ExperimentType> res = query.getExperimentTypes().byCodes(Arrays.asList("DEFAULT_EXPERIMENT","UNKNOWN"));
            assertEquals(2, res.size());
            assertEquals("DEFAULT_EXPERIMENT", res.get(0).getCode());
            assertEquals("UNKNOWN", res.get(1).getCode());
	}         

	@Test
	public void getAllSamples() throws Exception {
		List<Sample> samples = query.getSamples().byAttribute("permId", "");
		assertTrue(samples.size() > 0);
		String json = new JSONCreator(samples).getJSON();
		assertTrue(JSONHelper.isValidJSON(json));
	}
        
        @Test
	public void allSamplesGivesAll() throws Exception {
		List<Sample> samples = query.getSamples().all();
                assertEquals(16,samples.size());
	}        

	@Test
	public void getAllDatasets() throws Exception {
		List<DataSet> data = query.getDataSets().byAttribute("permId", "");
		assertTrue(data.size() > 0);
		String json = new JSONCreator(data).getJSON();
		assertTrue(JSONHelper.isValidJSON(json));
	}
        
        @Test
        public void allDatasetsGivesAll() throws Exception {
            List<DataSet> data = query.getDataSets().all();
            assertEquals(6,data.size());
        }
        


	@Test
        //@Ignore
	public void getDatasetByAttribute() throws Exception {
		//List<DataSet> data = query.dataSetsByAttribute("permId", "20151217153943290-5");
		List<DataSet> data = query.getDataSets().byAttribute("permId", "20180424181745930-58");
                
		String json = new JSONCreator(data).getJSON();
		assertTrue(JSONHelper.isValidJSON(json));
		assertEquals(1, data.size());
	}

	@Test
        //@Ignore
	public void getDatasetsByAttribute() throws Exception {
		List<String> values = new ArrayList<>();
		//values.add("20151217153943290-5");
		//values.add("20160210130359377-22");
		values.add("20180418142059396-52");
		values.add("20180424181745930-58");
		List<DataSet> data = query.getDataSets().byAttribute("permId", values);
		String json = new JSONCreator(data).getJSON();
		assertTrue(JSONHelper.isValidJSON(json));
		assertEquals(2, data.size());
	}
        
        @Test
        public void getsDataSetWithRichMetadata() throws Exception {
            
            //String setId = "20170907185702684-36";
            String setId = "20180424182903704-59";
            List<DataSet> sets = query.getDataSets().byAttribute("permId", setId);
            assertEquals(1,sets.size());
            
            DataSet set = sets.get(0);
            assertEquals(setId,set.getPermId().getPermId());
            
            //LocalDateTime reg = LocalDateTime.of(2017,9,7,17,57,3);
            LocalDateTime reg = LocalDateTime.of(2018,04,24,18,29,4);
           
            assertEquals(reg.toLocalDate(),
                    set.getRegistrationDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
            
            Map<String,String> props = set.getProperties();
            //System.out.println(properties);
            assertEquals("TOMEK test set",props.getOrDefault("NAME", "missing"));
            //assertTrue(props.getOrDefault("DESCRIPTION", "").contains("enhanced"));
            assertTrue(props.getOrDefault("NOTES", "").contains("enhanced"));
            
        }
        
        @Test
        public void getsSamplesWithRichMetadata() throws Exception {
            
            String perId = "20180424183252267-60";
            List<Sample> res = query.getSamples().byAttribute("permId", perId);
            assertEquals(1,res.size());
            
            Sample sam = res.get(0);
            assertEquals(perId,sam.getPermId().getPermId());
            
            //LocalDateTime reg = LocalDateTime.of(2017,10,2,16,21,11);
            LocalDateTime reg = LocalDateTime.of(2018,04,24,18,32,52);
           
            assertEquals(reg.toLocalDate(),
                    sam.getRegistrationDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
            
            Map<String,String> props = sam.getProperties();
            //System.out.println(properties);
            assertEquals("Purification",props.getOrDefault("NAME", "missing"));
            //assertTrue(props.getOrDefault("DESCRIPTION", "").contains("<head>"));
            assertTrue(props.getOrDefault("EXPERIMENTAL_GOALS", "").contains("<head>"));
            
        }   
        
        @Test
        public void samplesByTypeCodeWorks() throws AuthenticationException, InvalidOptionException {
            
            String typeN = "EXPERIMENTAL_STEP";
            Map<String,String> qMap = new HashMap<>();
            qMap.put("entityType", "Sample");
            qMap.put("queryType",QueryType.TYPE.name());
            qMap.put("typeCode",typeN);
            
            JSONObject crit = new JSONObject(qMap);
            
            
            List<Sample> res = query.getSamples().byType(crit);
            assertNotNull(res);
            assertEquals(8, res.size());
            
            res.forEach( s -> {
                assertEquals(typeN, s.getType().getCode());
            });            
            
            qMap.put("typeCode","TZ_ASSAY_NOT_DEFINED");           
            crit = new JSONObject(qMap);
            res = query.getSamples().byType(crit);
            
            assertTrue(res.isEmpty());
        }
        
        @Test
        public void samplesByMultipleTypeCodesWorks() throws AuthenticationException, InvalidOptionException {
            
            //local as it came from the new API
            query = localQuery();
            
            Map<String,Object> qMap = new HashMap<>();
            qMap.put("entityType", "Sample");
            qMap.put("queryType",QueryType.TYPE.name());
            qMap.put("typeCodes","UNKNOWN,EXPERIMENTAL_STEP");
            
            JSONObject crit = new JSONObject(qMap);
            
            
            List<Sample> res = query.getSamples().byType(crit);
            assertNotNull(res);
            assertEquals(8, res.size());
            
            List<String> exp = Arrays.asList("UNKNOWN","EXPERIMENTAL_STEP");
            res.forEach( s -> {
                assertTrue(exp.contains(s.getType().getCode()));
            });            
            
            qMap.put("typeCodes","TZ_ASSAY_NOT_DEFINED");           
            crit = new JSONObject(qMap);
            res = query.getSamples().byType(crit);
            
            assertTrue(res.isEmpty());
        }
        
        
        
        @Test
        public void allSampleTypes() throws AuthenticationException {
                        
            List<SampleType> res = query.getSampleTypes().all();
            assertNotNull(res);
            assertFalse(res.isEmpty());
            assertEquals(25,res.size());
            
        }
        
	@Test
	public void sampleTypesByCode() throws Exception {
            String typeN = "EXPERIMENTAL_STEP";
            List<SampleType> res = query.getSampleTypes().byCode(typeN);
            assertEquals(1, res.size());
            assertEquals(typeN, res.get(0).getCode());
	}   
        
	@Test
	public void sampleTypesByJoinedCodes() throws Exception {
            query = localQuery();
            List<SampleType> res = query.getSampleTypes().byCode("EXPERIMENTAL_STEP,UNKNOWN");
            assertEquals(2, res.size());
            assertEquals("EXPERIMENTAL_STEP", res.get(0).getCode());
            assertEquals("UNKNOWN", res.get(1).getCode());
	}        
        
	@Test
	public void sampleTypesByCodes() throws Exception {
            query = localQuery();
            List<SampleType> res = query.getSampleTypes().byCodes(Arrays.asList("EXPERIMENTAL_STEP","UNKNOWN"));
            assertEquals(2, res.size());
            assertEquals("EXPERIMENTAL_STEP", res.get(0).getCode());
            assertEquals("UNKNOWN", res.get(1).getCode());
	}        
        
        
        @Test
        @Ignore("Semantic not available")
        public void sampleTypesBySemanticSearchesUsingAllFields() throws AuthenticationException {
            
            query = localQuery();
            
            Map<String,String> qMap = new HashMap<>();
            qMap.put("entityType", "SampleType");
            qMap.put("queryType",QueryType.SEMANTIC.name());
            qMap.put("predicateOntologyId","po_id_t");
            qMap.put("predicateOntologyVersion","po_version_t");            
            qMap.put("predicateAccessionId","po_acc_t");
            qMap.put("descriptorOntologyId","do_id_t");            
            qMap.put("descriptorOntologyVersion","do_version_t");
            qMap.put("descriptorAccessionId","do_acc_t");            
            
            JSONObject crit = new JSONObject(qMap);
            
            
            List<SampleType> res = query.getSampleTypes().bySemantic(crit);
            assertNotNull(res);
            assertFalse(res.isEmpty());
            assertEquals("TZ_ASSAY",res.get(0).getCode());
            
            String[] fields = {"predicateOntologyId","predicateOntologyVersion","predicateAccessionId",
                                "descriptorOntologyId","descriptorOntologyVersion","descriptorAccessionId"};
            
            for (String field : fields) {
                HashMap wMap = new HashMap(qMap);
                wMap.put(field,"1");
                
                crit = new JSONObject(wMap);
                res = query.getSampleTypes().bySemantic(crit);
                assertNotNull(res);
                assertTrue(res.isEmpty());                
            }
        }
        
        @Test
        @Ignore("Semantic annotation not available in official release")
        public void sampleTypesBySemanticSearchesUsingOnlySetFields() throws AuthenticationException {
            
            query = localQuery();
            
            Map<String,String> qMap = new HashMap<>();
            qMap.put("entityType", "SampleType");
            qMap.put("queryType",QueryType.SEMANTIC.name());
            qMap.put("predicateOntologyId","po_id_t");
            qMap.put("predicateOntologyVersion","po_version_t");            
            qMap.put("predicateAccessionId","po_acc_t");
            qMap.put("descriptorOntologyId","do_id_t");            
            qMap.put("descriptorOntologyVersion","do_version_t");
            qMap.put("descriptorAccessionId","do_acc_t");            
            
            JSONObject crit = new JSONObject(qMap);
            
            
            List<SampleType> res = query.getSampleTypes().bySemantic(crit);
            assertNotNull(res);
            assertFalse(res.isEmpty());
            assertEquals("TZ_ASSAY",res.get(0).getCode());
            
            String[] fields = {"predicateOntologyId","predicateOntologyVersion","predicateAccessionId",
                                "descriptorOntologyId","descriptorOntologyVersion","descriptorAccessionId"};
            
            for (String field : fields) {
                HashMap wMap = new HashMap(qMap);
                wMap.put(field,null);
                
                crit = new JSONObject(wMap);
                res = query.getSampleTypes().bySemantic(crit);
                assertNotNull(res);
                assertFalse(res.isEmpty());
                assertEquals("TZ_ASSAY",res.get(0).getCode());              
            }
        }   
        
	@Test
	public void dataSetTypesByCode() throws Exception {
            String typeN = "RAW_DATA";
            List<DataSetType> res = query.getDataSetTypes().byCode(typeN);
            assertEquals(1, res.size());
            assertEquals(typeN, res.get(0).getCode());
	}    
        
        @Test
        public void allDataSetTypesGivesAll() throws AuthenticationException {
                        
            List<DataSetType> res = query.getDataSetTypes().all();
            assertNotNull(res);
            assertFalse(res.isEmpty());
            //assertEquals(30,res.size());
            assertEquals(6,res.size());
            
        }  
        
        @Test
        public void datasetsByTypeCodeWorks() throws AuthenticationException, InvalidOptionException {

            //String typeN = "TZ_FAIR_TEST";
            String typeN = "RAW_DATA";
            Map<String,String> qMap = new HashMap<>();
            qMap.put("entityType", "DataSet");
            qMap.put("queryType",QueryType.TYPE.name());
            qMap.put("typeCode",typeN);
            
            JSONObject crit = new JSONObject(qMap);
            
            
            List<DataSet> res = query.getDataSets().byType(crit);
            assertNotNull(res);
            //assertEquals(4, res.size());
            assertEquals(4, res.size());
            
            res.forEach( s -> {
                assertEquals(typeN, s.getType().getCode());
            });            
            
            qMap.put("typeCode","TZ_ASSAY_NOT_DEFINED");           
            crit = new JSONObject(qMap);
            res = query.getDataSets().byType(crit);
            
            assertTrue(res.isEmpty());
        }
        
        @Test
        public void datasetsByMultipleTypeCodesWorks() throws AuthenticationException, InvalidOptionException {
            
            //local as it came from the new API
            query = localQuery();
            
            Map<String,Object> qMap = new HashMap<>();
            qMap.put("entityType", "DataSet");
            qMap.put("queryType",QueryType.TYPE.name());
            qMap.put("typeCodes","RAW_DATA,UNKNOWN");
            
            JSONObject crit = new JSONObject(qMap);
            
            
            List<DataSet> res = query.getDataSets().byType(crit);
            assertNotNull(res);
            assertEquals(4, res.size());
            
            List<String> exp = Arrays.asList("RAW_DATA","UNKNOWN");
            res.forEach( s -> {
                assertTrue(exp.contains(s.getType().getCode()));
            });            
            
            qMap.put("typeCodes","TZ_ASSAY_NOT_DEFINED");           
            crit = new JSONObject(qMap);
            res = query.getDataSets().byType(crit);
            
            assertTrue(res.isEmpty());
        }
        
        

	@Test
        @Ignore
	public void getExperimentWithSeekStudyID() throws Exception {
		String property = "SEEK_STUDY_ID";
		String propertyValue = "Study_1";
		List<Experiment> experiments = query.getExperiments().byProperty(property, propertyValue);
		assertEquals(1, experiments.size());
		Experiment experiment = experiments.get(0);
		assertEquals(propertyValue, experiment.getProperties().get(property));
	}

	@Test
        @Ignore
	public void getExperimentWithSeekStudyIDNoResult() throws Exception {
		String property = "SEEK_STUDY_ID";
		String propertyValue = "SomeID";
		List<Experiment> experiments = query.getExperiments().byProperty(property, propertyValue);
		assertEquals(0, experiments.size());
	}

	@Test
        @Ignore
	public void getSampleWithSeekAssayID() throws Exception {
		String property = "SEEK_ASSAY_ID";
		String propertyValue = "Assay_1";
		List<Sample> samples = query.getSamples().byProperty(property, propertyValue);
		assertEquals(1, samples.size());
		Sample sample = samples.get(0);

		assertEquals(propertyValue, sample.getProperties().get(property));
		// SampleIdentifier identifier = new
		// SampleIdentifier("/API_TEST/SAMPLE_1");
		// assertEquals(identifier, sample.getIdentifier());
	}

	@Test
        @Ignore
	public void getSampleWithSeekAssayIDNoResult() throws Exception {
		String property = "SEEK_ASSAY_ID";
		String propertyValue = "SomeID";
		List<Sample> samples = query.getSamples().byProperty(property, propertyValue);
		assertEquals(0, samples.size());
	}

	@Test
        @Ignore
	public void getDataSetWithSeekDataFileID() throws Exception {
		String property = "SEEK_DATAFILE_ID";
		String propertyValue = "DataFile_1";
		List<DataSet> dataSets = query.getDataSets().byProperty(property, propertyValue);
		assertEquals(1, dataSets.size());
		DataSet dataSet = dataSets.get(0);

		assertEquals(propertyValue, dataSet.getProperties().get(property));
	}

	@Test
        @Ignore
	public void getDatasetWithSeekDataFileIDNoResult() throws Exception {
		String property = "SEEK_DATAFILE_ID";
		String propertyValue = "SomeID";
		List<DataSet> dataSets = query.getDataSets().byProperty(property, propertyValue);
		assertEquals(0, dataSets.size());
	}

	@Test
	public void jsonResultforExperiment() throws Exception {
		String type = "Experiment";
		String property = "NAME";
		String propertyValue = "Promoters modelling";
		List<? extends Object> result = query.query(type, QueryType.PROPERTY, property, propertyValue);
		String jsonResult = new JSONCreator(result).getJSON();
		
		assertTrue(jsonResult.matches("(.*)Promoters modelling(.*)"));
	}

	@Test(expected = InvalidOptionException.class)
	public void unrecognizedType() throws Exception {
		String type = "SomeType";
		String property = "SEEK_STUDY_ID";
		String propertyValue = "Study_1";
		query.query(type, QueryType.PROPERTY, property, propertyValue);
	}

	@Test
	public void expirementsByAnyField() throws Exception {
		// project
		//String searchTerm = "API-PROJECT";
                String searchTerm = "SEEK_INT";
		List<Experiment> experiments = query.getExperiments().byAnyField(searchTerm);
		assertTrue(experiments.size() > 0);

		// code
		searchTerm = "E1";
		experiments = query.getExperiments().byAnyField(searchTerm);
		assertTrue(experiments.size() > 0);

		// permID
		//searchTerm = "20151216143716562-2";
                searchTerm = "	20180418145822544-47";
		experiments = query.getExperiments().byAnyField(searchTerm);
		assertTrue(experiments.size() > 0);

		// type
		searchTerm = "DEFAULT_EXPERIMENT";
		experiments = query.getExperiments().byAnyField(searchTerm);
		assertTrue(experiments.size() > 0);

		// property
		searchTerm = "Low light diurnal";
		experiments = query.getExperiments().byAnyField(searchTerm);
		assertTrue(experiments.size() > 0);

		/*
                // tag
		searchTerm = "test_tag";
		experiments = query.experimentsByAnyField(searchTerm);
		assertTrue(experiments.size() > 0);

		// prefix
		searchTerm = "test_";
		experiments = query.experimentsByAnyField(searchTerm);
		assertTrue(experiments.size() > 0);

		// suffix
		searchTerm = "tag";
		experiments = query.experimentsByAnyField(searchTerm);
		assertTrue(experiments.size() > 0);

		// middle
		searchTerm = "st_ta";
		experiments = query.experimentsByAnyField(searchTerm);
		assertTrue(experiments.size() > 0);
                */
	}

	@Test
	public void samplesByAnyField() throws Exception {
		// code
		String searchTerm = "EXP6";
		List<Sample> samples = query.getSamples().byAnyField(searchTerm);
		assertTrue(samples.size() > 0);

		// permID
		//searchTerm = "20151216143743603-3";
		searchTerm = "20180424183252267-60";                
		samples = query.getSamples().byAnyField(searchTerm);
		assertTrue(samples.size() > 0);

		// type
		searchTerm = "EXPERIMENTAL_STEP";
		samples = query.getSamples().byAnyField(searchTerm);
		assertTrue(samples.size() > 0);

		// project -dont work
		// searchTerm = "API-PROJECT";
		// samples = query.samplesByAnyField(searchTerm);
		// assertTrue(samples.size() > 0);

		// experiment -dont work
		// searchTerm = "E2";
		// samples = query.samplesByAnyField(searchTerm);
		// assertTrue(samples.size() > 0);

		// SEEK_ASSAY_ID property
		//searchTerm = "Assay_1";
		//samples = query.samplesByAnyField(searchTerm);
		//assertTrue(samples.size() > 0);
	}

	@Test
	public void datasetsByAnyField() throws Exception {
		// permID
		String searchTerm = "20180424181745930-58";
		List<DataSet> datasets = query.getDataSets().byAnyField(searchTerm);
		assertTrue(datasets.size() > 0);

		// type
		searchTerm = "RAW_DATA";
		datasets = query.getDataSets().byAnyField(searchTerm);
		assertTrue(datasets.size() > 0);

		// Source type - dont work
		// searchTerm = "MEASUREMENT";
		// datasets = query.datasetsByAnyField(searchTerm);
		// assertTrue(datasets.size() > 0);

		// project - dont work
		// searchTerm = "API-PROJECT";
		// datasets = query.datasetsByAnyField(searchTerm);
		// assertTrue(datasets.size() > 0);

		// experiment - dont work
		// searchTerm = "E2";
		// datasets = query.datasetsByAnyField(searchTerm);
		// assertTrue(datasets.size() > 0);

		// datastore - dont work
		// searchTerm = "DSS1";
		// datasets = query.datasetsByAnyField(searchTerm);
		// assertTrue(datasets.size() > 0);

		// user - dont work
		// searchTerm = "apiuser";
		// datasets = query.datasetsByAnyField(searchTerm);
		// assertTrue(datasets.size() > 0);

		// registration date - dont work
		// searchTerm = "2016-02-15";
		// datasets = query.datasetsByAnyField(searchTerm);
		// assertTrue(datasets.size() > 0);

		// file type
		searchTerm = "PROPRIETARY";
		datasets = query.getDataSets().byAnyField(searchTerm);
		assertTrue(datasets.size() > 0);

		// SEEK_DATAFILE_ID property
		//searchTerm = "DataFile_9";
		//datasets = query.datasetsByAnyField(searchTerm);
		//assertTrue(datasets.size() > 0);
	}

}