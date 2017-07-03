package org.opensrp.service;

import com.google.gson.Gson;
import org.apache.http.HttpEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.MockitoAnnotations;
import org.opensrp.SpringApplicationContextProvider;
import org.opensrp.domain.Address;
import org.opensrp.domain.Client;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.any;
import static org.opensrp.service.OpenmrsIDService.CHILD_REGISTER_CARD_NUMBER;

public class OpenmrsIDServiceTest  extends SpringApplicationContextProvider{
    @Autowired
    OpenmrsIDService openmrsIDService;
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Before
    public void setUp() {
        String dropDbSql = "DROP TABLE IF EXISTS `unique_ids`;";
        jdbcTemplate.execute(dropDbSql);
        String  tableCreationString =
                "CREATE TABLE `unique_ids` (\n" +
                "  `_id` bigint(20) NOT NULL AUTO_INCREMENT,\n" +
                "  `created_at` datetime DEFAULT NULL,\n" +
                "  `location` varchar(255) DEFAULT NULL,\n" +
                "  `openmrs_id` varchar(255) DEFAULT NULL,\n" +
                "  `status` varchar(255) DEFAULT NULL,\n" +
                "  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,\n" +
                "  `used_by` varchar(255) DEFAULT NULL,\n" +
                "  PRIMARY KEY (`_id`)\n" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8;";
        jdbcTemplate.execute(tableCreationString);
    }

    @After
    public void tearDown() {
        String dropDbSql = "DROP TABLE IF EXISTS `unique_ids`;";
        jdbcTemplate.execute(dropDbSql);

    }

    public Client createClient(String baseEntityId, String firstName, String lastName, String gender, String childRegisterCardNumber) {
        DateTime dateOfBirth = new DateTime();
        Map<String, String> addressFields = new HashMap<>();
        addressFields.put("address4", "birthFacilityName");
        addressFields.put("address3", "resolvedResidentialAddress");
        addressFields.put("address2", "residentialAddress");
        addressFields.put("address1", "physicalLandmark");

        Address address = new Address("usual_residence", new DateTime(), new DateTime(), addressFields, null, null, null, "homeFacility", null);
        ArrayList<Address> addressList = new ArrayList<Address>();
        addressList.add(address);

        Client client = new Client(baseEntityId, firstName, "", lastName, dateOfBirth, null, false, false, gender, addressList, null, null);
        client.addAttribute(CHILD_REGISTER_CARD_NUMBER, childRegisterCardNumber);
        return client;
    }

    @Test
    public void testAssignOpenmrsIdToClient() throws SQLException {
        Client client = this.createClient("12345", "First", "Last", "Male", "454/16");

        openmrsIDService.assignOpenmrsIdToClient("12345-1", client);
        assertNotNull(client.getIdentifier(OpenmrsIDService.ZEIR_IDENTIFIER));
    }


    @Test
    public void testExistingClientsDoNotReceiveNewOpenmrsId() throws Exception {
        Client client = this.createClient("45678", "Jane", "Doe", "Female", "102/17");
        Client duplicateClient = this.createClient("45677", "Jane", "Doe", "Female", "102/17");

        openmrsIDService.assignOpenmrsIdToClient("12345-1", client);
        assertNotNull(client.getIdentifier(OpenmrsIDService.ZEIR_IDENTIFIER));

        openmrsIDService.assignOpenmrsIdToClient("12345-1", duplicateClient);
        assertTrue(openmrsIDService.checkIfClientExists(duplicateClient));
        assertNull(duplicateClient.getIdentifier(OpenmrsIDService.ZEIR_IDENTIFIER));
    }

}
