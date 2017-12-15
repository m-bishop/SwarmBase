package psu.edu.rest;

import java.io.IOException;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.OptimisticLockException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriBuilder;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.wildfly.swarm.spi.runtime.annotations.ConfigurationValue;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import psu.edu.model.NID;

/**
 * 
 */
@Stateless
@Path("/nids")
public class NIDEndpoint {
	@PersistenceContext(unitName = "lionpath-persistence-unit")
	private EntityManager em;
	
	@Inject
    @ConfigurationValue("swarm.test.test-string")
    String s;
	
	@Inject
    @ConfigurationValue("credentials.CPR.ID")
    String id;
	
	@Inject
    @ConfigurationValue("credentials.CPR.secret")
    String secret;
	
	@Inject
    @ConfigurationValue("credentials.CPR.baseURL")
    String baseURL;

	@POST
	@Consumes("application/json")
	public Response create(NID entity) {
		em.persist(entity);
		return Response.created(
				UriBuilder.fromResource(NIDEndpoint.class)
						.path(String.valueOf(entity.getEmplid())).build()).build();
	}

	@DELETE
	@Path("/{id:[0-9][0-9]*}")
	public Response deleteById(@PathParam("id") Long id) {
		NID entity = em.find(NID.class, id);
		if (entity == null) {
			return Response.status(Status.NOT_FOUND).build();
		}
		em.remove(entity);
		return Response.noContent().build();
	}

	@GET
	@Path("/{id:[0-9][0-9]*}")
	@Produces("application/json")
	public Response findById(@PathParam("id") Long id) {
		
		NID nid = em.find(NID.class,String.valueOf(id));
		String ssn = "";
		
		try {
			ssn = retrieveSsn(String.valueOf(id));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		nid.setNationalId(ssn.replaceAll("-", ""));
		em.persist(nid);
	
		return Response.ok(ssn).build();
	}

	@GET
	@Produces("application/json")
	public List<NID> listAll(@QueryParam("start") Integer startPosition,
			@QueryParam("max") Integer maxResult) {
		TypedQuery<NID> findAllQuery = em.createQuery(
				"SELECT DISTINCT n FROM NID n ORDER BY n.id", NID.class);
		if (startPosition != null) {
			findAllQuery.setFirstResult(startPosition);
		}
		if (maxResult != null) {
			findAllQuery.setMaxResults(maxResult);
		}
		final List<NID> results = findAllQuery.getResultList();
		return results;
	}

	@PUT
	@Path("/{id:[0-9][0-9]*}")
	@Consumes("application/json")
	public Response update(@PathParam("id") Long id, NID entity) {
		if (entity == null) {
			return Response.status(Status.BAD_REQUEST).build();
		}
		if (id == null) {
			return Response.status(Status.BAD_REQUEST).build();
		}
		if (!id.equals(entity.getEmplid())) {
			return Response.status(Status.CONFLICT).entity(entity).build();
		}
		if (em.find(NID.class, id) == null) {
			return Response.status(Status.NOT_FOUND).build();
		}
		try {
			entity = em.merge(entity);
		} catch (OptimisticLockException e) {
			return Response.status(Response.Status.CONFLICT)
					.entity(e.getEntity()).build();
		}

		return Response.noContent().build();
	}
	


    private String retrieveSsn(String psuId) throws IOException, ParseException {
        OkHttpClient client = new OkHttpClient();
        okhttp3.Response response;
        String responseJSON;
        String token = retrieveToken(baseURL,id,secret);

        Request request = new Request.Builder()
                .url(baseURL+"/cidr-service/resources/identifiers/"+psuId)
                .get()
                .addHeader("authorization", "Bearer "+token)
                .addHeader("content-type", "application/vnd-psu.edu-v1+json")
                .addHeader("cache-control", "no-cache")
                .build();

        response = client.newCall(request).execute();
        //this.responseCode = response.code();
        responseJSON = response.body().string();
        
        //this.errInfo = responseJSON; // set errInfo to the full JSON for client side debugging.

        return parseSsnInfo(responseJSON);
        //return token;
    }

    private String parseSsnInfo(String responseJSON) throws ParseException {
        JSONParser jsonParser = new JSONParser();

        JSONObject jsonObject = (JSONObject) jsonParser.parse(responseJSON);

        return ((String) jsonObject.get("ssn"));

    }
    
    private String retrieveToken(String baseURL, String clientID, String clientSecret) throws IOException, ParseException {
        OkHttpClient client = new OkHttpClient();
        okhttp3.Response response;
        String responseJSON;
        String authURL = baseURL+"/oauth/api/token";


        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        RequestBody body = RequestBody.create(mediaType, "grant_type=client_credentials&client_id="+clientID+"&client_secret="+clientSecret);
        Request request = new Request.Builder()
                .url(authURL)
                .post(body)
                .addHeader("accept", "application/json")
                .addHeader("cache-control", "no-cache")
                .addHeader("content-type", "application/x-www-form-urlencoded")
                .build();

        response = client.newCall(request).execute();
        //this.responseCode = response.code();
        responseJSON = response.body().string();
        //this.errInfo = responseJSON; // set errInfo to the full JSON for client side debugging.

        return parseTokenInfo(responseJSON);
    }

    private String parseTokenInfo(String responseJSON) throws ParseException {
        JSONParser jsonParser = new JSONParser();

        JSONObject jsonObject = (JSONObject) jsonParser.parse(responseJSON);

        return (String) jsonObject.get("access_token");
    }
}
