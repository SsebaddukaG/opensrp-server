package org.opensrp.connector.rapidpro;

import java.util.Map;

import org.opensrp.domain.Camp;
import org.opensrp.domain.Client;

public class WomanRemainderMessage implements Message {
	
	@Override
	public String message(Client client, Camp camp, Map<String, String> data) {
		
		String message = "Agamikal jodi apnar tikadaner tarikh hoi tahole Tika nite " + camp.getCampName()
		        + " te  oboshshoi chole ashben.";
		return message;
	}
}