package dk.kb.cop3.backend.migrate;

import dk.kb.cop3.backend.constants.CopBackendProperties;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class TestSearch {

    private static final Logger logger = LoggerFactory.getLogger(TestSearch.class);
    public static void main(String[] args) throws SolrServerException, IOException {
        MigrationUtils.initializeMigration();
        String solr_url = CopBackendProperties.getSolrUrl();
        logger.debug("Solr url "+solr_url);
        HttpSolrClient solr;
        solr = new HttpSolrClient.Builder(solr_url).build();
        SolrQuery query = new SolrQuery();
        query.setQuery("*:*");
        QueryResponse solrResponse = solr.query(query);
        logger.info("Num found "+solrResponse.getResults().getNumFound());
    }
}
