package net.antidot.api.upload;

import java.net.URI;
import java.net.URISyntaxException;

import net.antidot.api.common.Authentication;
import net.antidot.api.common.Authorities;
import net.antidot.api.common.Scheme;
import net.antidot.api.common.Service;

import org.junit.Assert;
import org.junit.Test;

public class URLHelperTest {

	@Test
	public void test() throws URISyntaxException {
		Authentication authentication = new Authentication("foo@bar+baz and bat", "foo@bar+baz and bat", Authorities.AFS_AUTH_ANTIDOT);
		Service service = new Service(42);
		URI url = URLHelper.buildUri(Scheme.AFS_SCHEME_HTTP, "localhost", authentication, service, "TEST", "FOO");
		Assert.assertEquals("http://localhost/bo-ws/service/42/instance/stable/paf/TEST/upload?afs%3Alogin=login%3A%2F%2Ffoo%2540bar%252Bbaz%2520and%2520bat%3Afoo%2540bar%252Bbaz%2520and%2520bat%40Antidot&comment=FOO", url.toString());
	}

}
