package dk.kb.cop3.backend.crud.oai.server.crosswalk;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.transform.stream.StreamSource;
import java.io.IOException;
import java.io.InputStream;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.Source;
import java.lang.String;
import java.net.URL;

public class LocalUriResolver implements URIResolver
{

    private static final Logger logger = LoggerFactory.getLogger(MetadataCreator.class);

    public Source resolve(String fileName, String base) throws TransformerException
    {
	logger.info("Resolving " + fileName + " against base " + base);
        URL url = getClass().getClassLoader().getResource(fileName);
        StreamSource jarFileSS = new StreamSource();

        try
        {
            InputStream jarfileIS = url.openStream();
            jarFileSS.setInputStream(jarfileIS);
        }
        catch(IOException ioExp)
        {
	    logger.error("Error " + ioExp.getMessage());
            throw new TransformerException(ioExp);
        }
        return jarFileSS;
    }
}
