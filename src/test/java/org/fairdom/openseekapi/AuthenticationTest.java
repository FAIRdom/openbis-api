package org.fairdom.openseekapi;

import static org.junit.Assert.assertTrue;

import org.fairdom.openseekapi.general.Authentication;
import org.fairdom.openseekapi.general.AuthenticationException;
import org.junit.Before;
import org.junit.Test;
import org.springframework.remoting.RemoteAccessException;

public class AuthenticationTest {

	@Test
	public void successfullyAuthenticated() throws Exception {
		Authentication au = new Authentication(Commons.TEST_OPENBIS_URL, Commons.TEST_OPENBIS_USER, Commons.TEST_OPENBIS_PASSWORD);
		String sessionToken = au.sessionToken();
		assertTrue(sessionToken.matches(Commons.TEST_OPENBIS_USER.concat("(.*)")));
	}

	@Test(expected = AuthenticationException.class)
	public void invalidAccount() throws Exception {
		String invalidUsername = new String("test1");
		String invalidPassword = new String("test");
		Authentication au = new Authentication(Commons.TEST_OPENBIS_URL, invalidUsername, invalidPassword);
		au.sessionToken();
	}

	@Test(expected = RemoteAccessException.class)
	public void invalidEndpoint() throws Exception {
		String invalidEndpoint = new String("https://example.com");
		Authentication au = new Authentication(invalidEndpoint, Commons.TEST_OPENBIS_USER, Commons.TEST_OPENBIS_PASSWORD);
		au.sessionToken();
	}

}
