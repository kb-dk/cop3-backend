package dk.kb.cop3.backend.crud.api;

import dk.kb.cop3.backend.constants.CopBackendProperties;
import org.apache.log4j.Logger;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

// The class binds to /solrizr
@Path("/solrizr")
public class SolrizrService {

	private static Logger logger = Logger.getLogger(SolrizrService.class);
	private CopBackendProperties consts = CopBackendProperties.getInstance();

	// TODO: do we need this
	@Context
	UriInfo uriInfo;

	@GET
	@Path("/editions/")
	public Response edition(@DefaultValue("") @QueryParam("solr_baseurl") String solr_baseurl,
				@DefaultValue("") @QueryParam("spotlight_exhibition") String spotlight_exhibition,
							@Context HttpServletRequest httpServletRequest,
							@Context ServletContext servletContext)
	{
		String source_data_url = this.consts.getConstants().getProperty("cop2_backend.internal.baseurl");
		source_data_url += "/editions/any/2009/jul/editions/da/?format=solr";
		String solr_doc = "";

		try {
			solr_doc = this.get_data(source_data_url);
		} catch(java.io.IOException io) {
			logger.warn(io.getMessage());
		}

		String destination_data_url = "";
		if("".equals(solr_baseurl)) {
			destination_data_url = this.consts.getConstants().getProperty("cop2_solr.baseurl")+"/update?softCommit=true";
		} else {
			destination_data_url = solr_baseurl + "/update?softCommit=true";
		}
		logger.debug("solr uri: " + destination_data_url);
		String result = "";

		try {
			result = this.post_data(destination_data_url,solr_doc);
			logger.debug(destination_data_url + " : " +  result );
			Response.ResponseBuilder res = Response.ok(result);
			return res.build();
		} catch(java.io.IOException ioProblem) {
			logger.warn(destination_data_url + " : " +  ioProblem.getMessage());
			return Response.status(500).build();
		}
	}


	@GET
	@Path("/{medium}/{collection}/{year}/{month}/{edition}/{id:(subject[0-9]+?|object[0-9]+?)?}{nn:(/)?}{lang:(da|en)?}")
	public Response get(@PathParam("medium") String medium,
						@PathParam("collection") String collection,
						@PathParam("year") int year,
						@PathParam("month") String month,
						@PathParam("edition") String edition,
						@PathParam("id") String id,
						@PathParam("lang") String language,
						@DefaultValue("") @QueryParam("solr_baseurl") String solr_baseurl,
			                        @DefaultValue("") @QueryParam("spotlight_exhibition") String spotlight_exhibition,
						@Context HttpServletRequest httpServletRequest,
						@Context ServletContext servletContext)
	{

		String cop_id          = "/" + medium + "/" + collection + "/" + year + "/" + month + "/" + edition + "/" + id;
		String source_data_url = this.consts.getConstants().getProperty("cop2_backend.internal.baseurl");

		String exhibition =  "".equals(spotlight_exhibition) ? "" : "&spotlight_exhibition=" + spotlight_exhibition;

		logger.debug("spotlight_exhibition argument: " + exhibition);
		if (id.startsWith("object"))
			source_data_url += "/syndication" + cop_id + "?format=solr" + exhibition;
		else
			source_data_url += "/navigation"  + cop_id + "?format=solr" + exhibition;

		String solr_doc = "";

		try {
			solr_doc = this.get_data(source_data_url);
		} catch(java.io.IOException io) {
			logger.warn(io.getMessage());
		}

		String destination_data_url = "";
		if("".equals(solr_baseurl)) {
			destination_data_url = this.consts.getConstants().getProperty("cop2_solr.baseurl")+"/update?softCommit=true";
		} else {
			destination_data_url = solr_baseurl + "/update?softCommit=true";
		}
		logger.debug("solr uri: " + destination_data_url);
		String result = "";

		try {
			result = this.post_data(destination_data_url,solr_doc);
			logger.debug(destination_data_url + " : " +  result );
			Response.ResponseBuilder res = Response.ok(result);
			return res.build();
		} catch(java.io.IOException ioProblem) {
			logger.warn(destination_data_url + " : " +  ioProblem.getMessage());
			return Response.status(500).build();
		}

	}

	private String get_data(String cop) throws java.io.IOException {

		org.apache.http.impl.client.CloseableHttpClient httpClient = null;
		org.apache.http.client.methods.CloseableHttpResponse response = null;
		String responseString = null;
		try {
			httpClient =
					org.apache.http.impl.client.HttpClientBuilder.create().setRedirectStrategy(
							new org.apache.http.impl.client.LaxRedirectStrategy()).build();
			logger.debug("Sending get_data request: " + cop);

			//create a method object
			org.apache.http.client.methods.HttpGet get_method =
					new org.apache.http.client.methods.HttpGet(cop);

			// execute
			response = httpClient.execute(get_method);

			// get content
			org.apache.http.HttpEntity entity = response.getEntity();
			responseString = org.apache.http.util.EntityUtils.toString(entity, "UTF-8");

			logger.info("get_data response status from " + cop + ": " + response.getStatusLine().toString());
			logger.debug("get_data response content: " + responseString);

		} finally {
			if (response != null)
				response.close();
			if (httpClient != null)
				httpClient.close();
		}
		return responseString ;

	}

	private String post_data(String solr, String xml_data) throws java.io.IOException {

		org.apache.http.impl.client.CloseableHttpClient httpClient = null;
		org.apache.http.client.methods.CloseableHttpResponse response = null;
		String responseString = null;
		try {
			httpClient =
					org.apache.http.impl.client.HttpClientBuilder.create().setRedirectStrategy(
							new org.apache.http.impl.client.LaxRedirectStrategy()).build();
			logger.debug("Sending post_data " );
			logger.debug("request uri: " + solr);

			// create a method object
			org.apache.http.client.methods.HttpPost post_method =
					new org.apache.http.client.methods.HttpPost (solr);

			logger.debug("made post method");

			// add request body

			logger.debug("body content=" +  xml_data);
			org.apache.http.entity.StringEntity params =new org.apache.http.entity.StringEntity(xml_data,"UTF-8");
			params.setContentType("text/xml");
			post_method.addHeader("Content-Type", "text/xml");

			logger.debug("made StringEntity");
			post_method.setEntity(params);

			logger.debug("attached StringEntity to post_method");

			response = httpClient.execute(post_method);

			logger.debug("executed");

			org.apache.http.HttpEntity response_entity = response.getEntity();
			responseString = org.apache.http.util.EntityUtils.toString(response_entity, "UTF-8");

			logger.info("post_data response status: " + response.getStatusLine().toString()  );
			logger.debug("post_data response content: " +  responseString );
		} finally {
			if (response != null)
				response.close();
			if (httpClient != null)
				httpClient.close();
		}

		return responseString ;

	}

}
