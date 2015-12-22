package org.fairdom;

/**
 * Created by quyennguyen on 19/02/15.
 */
import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class OptionParserTest {

    @Test
    public void testType() throws Exception {
        String type = "Experiment";
        String[] args = new String[] { "-t", type };
        OptionParser p = new OptionParser(args);
        assertEquals(type, p.getType());
    }

    @Test
    public void testProperty() throws Exception {
        String property = "SEEK_STUDY_ID";
        String[] args = new String[] { "-p", property};
        OptionParser p = new OptionParser(args);
        assertEquals(property, p.getProperty());
    }

    @Test
    public void testPropertyValue() throws Exception {
        String propertyValue = "Study_1";
        String[] args = new String[] { "-pv", propertyValue};
        OptionParser p = new OptionParser(args);
        assertEquals(propertyValue, p.getPropertyValue());
    }

    @Test
    public void testUsername() throws Exception {
        String username = "test";
        String[] args = new String[] { "-u", username};
        OptionParser p = new OptionParser(args);
        assertEquals(username, p.getUsername());
    }

    @Test
    public void testPassword() throws Exception {
        String password = "test";
        String[] args = new String[] { "-pw", password};
        OptionParser p = new OptionParser(args);
        assertEquals(password, p.getPassword());
    }

    @Test
    public void testAsEndpoint() throws Exception {
        String endpoint = "http://example.com";
        String[] args = new String[] { "-ae", endpoint};
        OptionParser p = new OptionParser(args);
        assertEquals(endpoint, p.getAsEndpoint());
    }
    
    @Test
    public void testDssEndpoint() throws Exception {
        String endpoint = "http://example.com";
        String[] args = new String[] { "-de", endpoint};
        OptionParser p = new OptionParser(args);
        assertEquals(endpoint, p.getDssEndpoint());
    }

    @Test
    public void testMultipleArgs() throws Exception {
        String type = "Experiment";
        String property = "SEEK_STUDY_ID";
        String propertyValue = "Study_1";
        String username = "test";
        String password = "test";
        String asEndpoint = "http://as.example.com";
        String dssEndpoint = "http://dss.example.com";

        String[] args = new String[] { "-t", type, "-p", property, "-pv", propertyValue,
                                       "-u", username, "-pw", password, "-ae", asEndpoint, "-de", dssEndpoint};
        OptionParser p = new OptionParser(args);
        assertEquals(type, p.getType());
        assertEquals(property, p.getProperty());
        assertEquals(propertyValue, p.getPropertyValue());
        assertEquals(username, p.getUsername());
        assertEquals(password, p.getPassword());
        assertEquals(asEndpoint, p.getAsEndpoint());
        assertEquals(dssEndpoint, p.getDssEndpoint());
    }

    @Test(expected = InvalidOptionException.class)
    public void testInvalidArg() throws Exception {
        String[] args = new String[] { "-ss", "something" };
        new OptionParser(args);
    }

    @Test(expected = InvalidOptionException.class)
    public void testEmptyOptionValue() throws Exception {
        String[] args = new String[] { "-t", "", "-p", "   ", "-pv", null };
        new OptionParser(args);
    }
}