/**
 * 
 */
package de.cryxy.owntracks.recorder.api;

import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.StringTokenizer;


import de.cryxy.owntracks.recorder.config.Config;
import jakarta.inject.Inject;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

/**
 * Authorization Request Filter.
 * 
 * @author fabian
 *
 */
@jakarta.ws.rs.ext.Provider
public class AuthFilter implements jakarta.ws.rs.container.ContainerRequestFilter {

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
		Base64.Decoder base64Decoder = Base64.getDecoder();
		String usernameAndPassword = new String(base64Decoder.decode(encodedUserPassword.getBytes()));

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
