package dk.kb.cop3.backend.crud.api;

import dk.kb.cop3.backend.solr.SolrHelper;

import org.apache.log4j.Logger;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

// The class binds to /solrizr
@Path("/solrizr")
public class SolrizrService {

	private static final Logger logger = Logger.getLogger(SolrizrService.class);

	// TODO: do we need this
	@Context
	UriInfo uriInfo;

	@GET
	@Path("/editions/")
	public Response solrizeAllEditions(@Context HttpServletRequest httpServletRequest,
							@Context ServletContext servletContext) {
		boolean updateWentOK = SolrHelper.solrizeEditions();
		if (updateWentOK) {
			return Response.ok().build();
		}
		return Response.serverError().build();
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
						@Context HttpServletRequest httpServletRequest,
						@Context ServletContext servletContext) {

		String cop_id = "/" + medium + "/" + collection + "/" + year + "/" + month + "/" + edition + "/" + id;
		String edition_id = "/" + medium + "/" + collection + "/" + year + "/" + month + "/" + edition;
		boolean updateWentOk;

		if (id.startsWith("object")) {
			updateWentOk = SolrHelper.updateCobjectInSolr(cop_id);
		} else {
			updateWentOk = SolrHelper.updateCategoriesInEditionInSolr(edition_id,id);
		}

		if (updateWentOk) {
			return Response.ok().build();
		} else {
			return Response.serverError().build();
		}

	}
}
