/**
 * 
 */
package de.cryxy.owntracks.recorder.api;

import java.io.IOException;
import java.util.List;
import java.util.StringTokenizer;

import javax.inject.Inject;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.Provider;

import org.glassfish.jersey.internal.util.Base64;

import de.cryxy.owntracks.recorder.config.Config;

/**
 * Authorization Request Filter.
 * 
 * @author fabian
 *
 */
@Provider
public class AuthFilter implements ContainerRequestFilter {

	@Inject
	private Config config;

	private static final String AUTHORIZATION_PROPERTY = "Authorization";
	private static final String AUTHENTICATION_SCHEME = "Basic";

	// Response if user is unauthorized.
	private Response getUnauthorized() {
		return Response.status(Status.UNAUTHORIZED).header(HttpHeaders.WWW_AUTHENTICATE, "Basic realm=\"realm\"")
				.entity("Page requires login.").build();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.ws.rs.container.ContainerRequestFilter#filter(javax.ws.rs.container.
	 * ContainerRequestContext)
	 */
	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {

		// Get request headers
		final MultivaluedMap<String, String> headers = requestContext.getHeaders();

		// Fetch authorization header
		final List<String> authorization = headers.get(AUTHORIZATION_PROPERTY);

		// If no authorization information present; block access
		if (authorization == null || authorization.isEmpty()) {
			requestContext.abortWith(getUnauthorized());
			return;
		}

		// Get encoded username and password
		final String encodedUserPassword = authorization.get(0).replaceFirst(AUTHENTICATION_SCHEME + " ", "");

		// Decode username and password
		String usernameAndPassword = new String(Base64.decode(encodedUserPassword.getBytes()));

		// Split username and password tokens
		final StringTokenizer tokenizer = new StringTokenizer(usernameAndPassword, ":");
		final String username = tokenizer.nextToken();
		final String password = tokenizer.nextToken();

		// verify user access
		if (!(username.equals(config.getApiUsername()) && password.equals(config.getApiPassword()))) {
			requestContext.abortWith(getUnauthorized());
		}

		return;

	}

}
