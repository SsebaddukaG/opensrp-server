package org.opensrp.connector.openmrs.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.map.MultiValueMap;
import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.opensrp.api.domain.Event;
import org.opensrp.api.domain.Obs;
import org.opensrp.common.util.HttpResponse;
import org.opensrp.connector.HttpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mysql.jdbc.StringUtils;

@SuppressWarnings("unchecked")
@Service
public class EncounterService extends OpenmrsService{
	private static final String ENCOUNTER_URL = "ws/rest/v1/bahmnicore/bahmniencounter";
	private static final String ENCOUNTER__TYPE_URL = "ws/rest/v1/encountertype";
	private PatientService patientService;
	private OpenmrsUserService userService;
	private static Logger logger = LoggerFactory.getLogger(EncounterService.class.toString());
	@SuppressWarnings("rawtypes")
	static Map map=new HashMap();
	static{
		map.put("TT1","c8e8ed67-d125-4c77-a8c7-2c9a5cf2c46b");  
	    map.put("TT2","14f59334-bde6-4512-bfae-830d5bd7da87");
	    map.put("TT3","8197f5b8-328c-402d-8128-fd04d9de8a94"); 
	    map.put("TT4","4914c323-d32d-4412-905f-c8c90f92136f"); 
	    map.put("TT5","4dd71b8e-8109-4eb3-b919-186f8236edb1");
	    map.put("be200e2b-0469-4cb3-b3c3-eeda1c51b336","TT"); 
	    
	    map.put("bcg","1cf49965-5130-4648-8473-37238d24b826");
	    map.put("opv0","548a2d2d-4803-4d9a-8d78-8a31741baccc");
	    map.put("pcv1","e3340e49-df17-4b86-841e-6a207db40e58");
	    map.put("opv1","548a2d2d-4803-4d9a-8d78-8a31741baccc");
	    map.put("penta1","e601701f-a9c5-4cea-a06e-4e5abacaf6e4");
	    map.put("pcv2","27c360d3-ce24-492d-a004-1d551dfd0933");
	    map.put("opv2","548a2d2d-4803-4d9a-8d78-8a31741baccc");
	    map.put("penta2","9fbd9d94-0634-4880-954e-502c8be1c2d4");
	    map.put("pcv3","b0735577-6747-4c3e-bb52-b2d9b0d2419a");
	    map.put("opv3","548a2d2d-4803-4d9a-8d78-8a31741baccc");
	    map.put("penta3","8c0268d5-befe-47e3-a98d-d7d13f214f5b");
	    map.put("ipv","caffb48d-4155-47a5-a81a-ef669f0585d0");
	    map.put("measles1","9c23d2fe-e2db-48ea-98ac-cfb832f7c0b6");
	    map.put("measles2","d2c8d3fa-495a-4e5f-8e8b-3c598725c0c3");
	    
	    map.put("6eea05d1-c7bc-4ef3-b42f-46541606daed","opv");	    
	    map.put("8867aedf-21c4-4441-ac72-fa87510eff36","bcg");	    
	    map.put("ff6fed1f-e05a-4ba5-a3f2-fba744c04550","pcv");
	    map.put("dcddb74f-4bf0-4a2c-b52c-c530509c11f7","penta");
	    map.put("1c35cc34-4ee2-47cd-8a30-cb37eb803a43","measles");
	    map.put("79146dc6-82b0-4e07-9d8a-50f865c02667","ipv");
	    
	    map.put("parent","021e0705-953d-11e6-90c1-005056b01095");
	    
	    map.put("Birth Outcome","7e12169a-e42f-11e5-8c3e-08002715d519");
	    map.put("Child Vaccination Followup","7e0a6e63-c4d5-40aa-99b1-c9b3696e8b1b");
	    map.put("HH And Member Registration","7e12169a-e42f-11e5-8c3e-08002715d519");
	    map.put("New Member Registration","7e12169a-e42f-11e5-8c3e-08002715d519");
	    map.put("Woman TT Follow Up","7e0a6e63-c4d5-40aa-99b1-c9b3696e8b1b");
	    map.put("Pregnancy Status And Birth Notification Followup","03aecf69-953d-11e6-90c1-005056b01095");
	    
	}

	@Autowired
	public EncounterService(PatientService patientService, OpenmrsUserService userService) {
		this.patientService = patientService;
		this.userService = userService;
	}
	
	public EncounterService(String openmrsUrl, String user, String password) {
    	super(openmrsUrl, user, password);
	}
	
	public PatientService getPatientService() {
		return patientService;
	}

	public void setPatientService(PatientService patientService) {
		this.patientService = patientService;
	}

	public OpenmrsUserService getUserService() {
		return userService;
	}

	public void setUserService(OpenmrsUserService userService) {
		this.userService = userService;
	}
	
	
	@SuppressWarnings("rawtypes")
	public JSONObject createEncounter(Event e,String idGen) throws JSONException{
		
		JSONObject pt = patientService.getPatientByIdentifier(idGen);
		JSONObject enc = new JSONObject();
		JSONObject pr = userService.getPersonByUser(e.getProviderId());		
		enc.put("encounterDateTime", new DateTime());
		enc.put("visitType", "field");
		//enc.put("encounterUuid", "02f0939b-e307-4f63-bacb-e32ac4bc7283");
		// patient must be existing in OpenMRS before it submits an encounter. if it doesnot it would throw NPE
		if (pr.getString("uuid").isEmpty() || pr.getString("uuid")==null)
			System.out.println("Person or Patient does not exist or empty inside openmrs with identifier: " + pr.getString("uuid"));
		else 
			enc.put("patientUuid", pt.getString("uuid"));
		enc.put("encounterTypeUuid", map.get(e.getEventType()));
		enc.put("locationUuid", e.getLocationId());
		if (pr.getString("uuid").isEmpty() || pr.getString("uuid")==null){
			System.out.println("Person or Patient does not exist or empty inside openmrs with identifier: " + pr.getString("uuid"));
		}else{
			JSONObject providers = new JSONObject();
			JSONArray providerArray = new JSONArray();
			providers.put("uuid", "0da40551-08cb-4f13-94f9-04de483e3f6b");// uuid for sohel user
			providerArray.put(providers);
			enc.put("providers", providerArray);
			//enc.put("provider", pr.getString("uuid"));
		}
		
		
		try{			
			List<Obs> ol = e.getObs();			
			Map<String, List<JSONObject>> pc = new HashMap<>();
			MultiValueMap   obsMap = new MultiValueMap();
			enc.put("locationUuid", "4f2b8e02-f9b5-47b5-afdc-fe6ca7d50f7d");
			for (Obs obs : ol) {
				System.out.println("obs:"+obs.toString());
				//if no parent simply make it root obs				
					if(StringUtils.isEmptyOrWhitespaceOnly(obs.getParentCode())){					
						obsMap.put(obs.getFieldCode(), convertObsToJson(obs));
					}
					else {	
						obsMap.put(obs.getParentCode(), convertObsToJson(getOrCreateParent(ol, obs)));
						// find if any other exists with same parent if so add to the list otherwise create new list
						List<JSONObject> obl = pc.get(obs.getParentCode());					
						if(obl == null){
							obl = new ArrayList<>();
						}
						obl.add(convertObsToJson(obs));
						pc.put(obs.getParentCode(), obl);
					}
			}			
			
			JSONArray obar = new JSONArray();
			List<JSONObject> list;				
			Set <String> entrySet = obsMap.entrySet();	        
			Iterator it = entrySet.iterator();
			
	        while (it.hasNext()) {	        	
	            Map.Entry mapEntry = (Map.Entry) it.next();	           
	            list = (List) obsMap.get(mapEntry.getKey());
	            	JSONObject obo = list.get(0); 	            	
	                List<JSONObject> cob = pc.get(mapEntry.getKey());	               
	    			if(cob != null && cob.size() > 0) {	    				
	    				obo.put("groupMembers", new JSONArray(cob));
	    			}
	    			obar.put(obo);
	        }
	       
	        this.createVaccineEncounter(obar, enc);	
	       
	        return null;
		}catch(Exception ee){
			System.out.println(ee.getMessage());
		}
		
		if(!enc.has("observations")){
			System.out.println("Going to create Registry  Encounter: " + enc.toString());
			HttpResponse op = HttpUtil.post(HttpUtil.removeEndingSlash(OPENMRS_BASE_URL)+"/"+ENCOUNTER_URL, "", enc.toString(), OPENMRS_USER, OPENMRS_PWD);
			return new JSONObject(op.body());
			
		}else{
			 return null;
		}
				
	}
	
	public JSONObject createVaccineEncounter(JSONArray obar,JSONObject enc) throws JSONException{
		 HttpResponse op = null;
		 logger.info("obar :"+obar.toString());
		for ( int totalGroupCounter = 0; totalGroupCounter < obar.length(); totalGroupCounter++) {
			 try{
			
				 String groupMembers =  obar.get(totalGroupCounter).toString();			
				 JSONObject groupMembersObject = new JSONObject(groupMembers);
				// System.out.println(groupMembersObject.toString());
				 JSONArray groupMembersList =  (JSONArray) groupMembersObject.get("groupMembers");				
				 String concept =  groupMembersObject.get("concept").toString();				
				 for (int vaccinationInAGroupMemberCounter = 0; vaccinationInAGroupMemberCounter < groupMembersList.length(); vaccinationInAGroupMemberCounter+=2) {
					 JSONArray gruopMember = new JSONArray();			 	        
			 	        String vaccine =  map.get(concept).toString();// get vaccine origin name such as  TT,bcg,opv etc
			 	        String getDoseValue="";
			 	        String getDateValue="";
			 	       
			 	        JSONObject 	dateOfAvaccine = new JSONObject(groupMembersList.get(vaccinationInAGroupMemberCounter).toString());
			 	        try{			 	        	
			 	        	JSONObject doseOfAvaccine = new JSONObject(groupMembersList.get(vaccinationInAGroupMemberCounter+1).toString());
			 	        	 getDoseValue = doseOfAvaccine.get("value").toString();
			 	        	getDateValue = dateOfAvaccine.get("value").toString();			 	        	
			 	        	vaccine  = vaccine+doseOfAvaccine.get("value").toString();//concat dose number with vaccine such as  TT1,opv1
			 	        	gruopMember.put(groupMembersList.get(vaccinationInAGroupMemberCounter+1));		 	        	
			 	        }catch(Exception e){
			 	        	System.out.println("Dose not found");
			 	        	getDoseValue ="0";
			 	        	getDateValue = dateOfAvaccine.get("value").toString();
			 	        }
			 	    
			        	
			        	
			        	JSONObject observations = new JSONObject();
			    		JSONObject conceptTemplate = new JSONObject();
			    		conceptTemplate.put("dataType", "N/A");
			    		conceptTemplate.put("name", "Immunization Incident Template");
			    		conceptTemplate.put("uuid", "021cb967-953d-11e6-90c1-005056b01095");
			        	
			        	JSONArray innerGroupMember = new JSONArray();
			    		
			    		
			    		/// group memener vacine name
			    		JSONObject vaccineNameGroupMember = new JSONObject();
			    		JSONObject innerGroupMember1Concept = new JSONObject();
			    		//innerGroupMember1Concept.put("dataType", "Coded");
			    		innerGroupMember1Concept.put("name", "Immunization Incident Vaccine");
			    		innerGroupMember1Concept.put("uuid", "021e0705-953d-11e6-90c1-005056b01095");
			    		vaccineNameGroupMember.put("concept", innerGroupMember1Concept);
			    		
			    		
			    		JSONObject vaccineNameConcept = new JSONObject();
			    		vaccineNameConcept.put("name", vaccine);
			    		vaccineNameConcept.put("uuid", map.get(vaccine));
			    		vaccineNameConcept.put("value", vaccine);
			    		vaccineNameGroupMember.put("value", vaccineNameConcept);
			    		
			    		
			    		JSONObject vaccineDateConcept = new JSONObject();
			    		JSONObject vaccineDate = new JSONObject();
			    		vaccineDate.put("dataType", "Date");
			    		vaccineDate.put("name", "Immunization Incident Vaccination Date");
			    		vaccineDate.put("uuid", "021e8246-953d-11e6-90c1-005056b01095");
			    		vaccineDateConcept.put("concept", vaccineDate);
			    		
			    		vaccineDateConcept.put("value", getDateValue);
			    		
			    		
			    		/// group memener for dose
			    		JSONObject doseValueGroupMember = new JSONObject();
			    		JSONObject doseValueGroupMemberConcept = new JSONObject();
			    		doseValueGroupMemberConcept.put("dataType", "Numeric");
			    		doseValueGroupMemberConcept.put("name", "Immunization Incident Vaccination Dosage");
			    		doseValueGroupMemberConcept.put("uuid", "021efde7-953d-11e6-90c1-005056b01095");
			    		doseValueGroupMember.put("concept", doseValueGroupMemberConcept);
			    		
			    		doseValueGroupMember.put("value", getDoseValue);
			    		
			    		
			    		/// group memener for reported 
			    		JSONObject reportedGroupMember = new JSONObject();
			    		JSONObject reportedGroupMemberConcept = new JSONObject();
			    		reportedGroupMemberConcept.put("dataType", "Boolean");
			    		reportedGroupMemberConcept.put("name", "Immunization Incident Vaccination Reported");
			    		reportedGroupMemberConcept.put("uuid", "021f7622-953d-11e6-90c1-005056b01095");
			    		reportedGroupMember.put("concept", reportedGroupMemberConcept);		
			    		reportedGroupMember.put("value", true);			    		
			    		
			    		
			    		innerGroupMember.put(vaccineNameGroupMember);
			    		innerGroupMember.put(doseValueGroupMember);
			    		innerGroupMember.put(reportedGroupMember);
			    		innerGroupMember.put(vaccineDateConcept);
			    		
			    		// group member property
			    		JSONObject groupMember = new JSONObject();
			    		groupMember.put("groupMembers", innerGroupMember);
			    		groupMember.put("isObservation", true);
			    		JSONObject conceptInner = new JSONObject();
			    		conceptInner.put("dataType", "N/A");
			    		conceptInner.put("name", "Immunization Incident Group");
			    		conceptInner.put("uuid", "021d28cd-953d-11e6-90c1-005056b01095");
			    		groupMember.put("concept", conceptInner);
			    		
			    		
			    		
			    		// group member array property
			    		JSONArray groupMemberArray = new JSONArray();
			    		groupMemberArray.put(groupMember);
			    		
			    		JSONArray observationsArray = new JSONArray();
			    		observations.put("conceptSetName", "Immunization Incident Group");
			    		observations.put("concept", conceptTemplate);
			    		observations.put("groupMembers", groupMemberArray);
			    		observations.put("label", "Immunization Incident");
			    		observationsArray.put(observations);
			    		
			    		
			 	        enc.put("observations", observationsArray);
			 	       logger.info("Going to create Encounter: " + enc.toString());
			 	       op = HttpUtil.post(HttpUtil.removeEndingSlash(OPENMRS_BASE_URL)+"/"+ENCOUNTER_URL, "", enc.toString(), OPENMRS_USER, OPENMRS_PWD);
			 	       
				}
			 }
			catch(Exception e){
				logger.info("Message:"+e.getMessage());
			
			}
		}
		
		return new JSONObject(op.body());
		
	}
	private  JSONObject convertObsToJson(Obs o) throws JSONException{
		JSONObject obo = new JSONObject();
		obo.put("concept", o.getFieldCode());
		if(o.getValue() != null && !StringUtils.isEmptyOrWhitespaceOnly(o.getValue().toString())) {			
			if (o.getValue().toString().length() >= 19)
				obo.put("value", (o.getValue().toString().substring(0, 19)).replace("T", " "));
			else 
				obo.put("value", o.getValue());			
		}
		
		return obo;
	}
	
	private Obs getOrCreateParent(List<Obs> obl, Obs o){
		for (Obs obs : obl) {
			if(o.getParentCode().equalsIgnoreCase(obs.getFieldCode())){
				return obs;
			}
		}
		return new Obs("concept", o.getParentCode(), null, null, null, null);
	}
	
	
	
    public JSONObject convertEncounterToOpenmrsJson(String name, String description) throws JSONException {
		JSONObject a = new JSONObject();
		a.put("name", name);
		a.put("description", description);
		return a;
	}
	

    public JSONObject getEncounterType(String encounterType) throws JSONException
    {
    	// we have to use this ugly approach because identifier not found throws exception and 
    	// its hard to find whether it was network error or object not found or server error
    	JSONArray res = new JSONObject(HttpUtil.get(getURL()+"/"+ENCOUNTER__TYPE_URL, "v=full", 
    			OPENMRS_USER, OPENMRS_PWD).body()).getJSONArray("results");
    	for (int i = 0; i < res.length(); i++) {
			if(res.getJSONObject(i).getString("display").equalsIgnoreCase(encounterType)){
				return res.getJSONObject(i);
			}
		}
    	return null;
    }
    public JSONObject createEncounterType(String name, String description) throws JSONException{
		JSONObject o = convertEncounterToOpenmrsJson(name, description);
		return new JSONObject(HttpUtil.post(getURL()+"/"+ENCOUNTER__TYPE_URL, "", o.toString(), OPENMRS_USER, OPENMRS_PWD).body());
	}
}
