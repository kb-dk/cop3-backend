package dk.kb.cop3.backend.crud.util;

import dk.kb.cop3.backend.crud.api.CreateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.transform.ErrorListener;
import javax.xml.transform.TransformerException;

public class TransformErrorListener implements ErrorListener  {

    private String schema;

    public TransformErrorListener(String schema) {
        this.schema = schema;
    }

    private static final Logger logger = LoggerFactory.getLogger(TransformErrorListener.class);
    @Override
    public void warning(TransformerException exception) throws TransformerException {
        logger.warn(schema + " Transform Warning: " + exception.getMessageAndLocation());
    }

    @Override
    public void error(TransformerException exception) throws TransformerException {
        logger.error(schema + " Transformer Error: " + exception.getMessageAndLocation());
    }

    @Override
    public void fatalError(TransformerException exception) throws TransformerException {
        // Handle fatal errors
        logger.error(schema + " Fatal Transform Error "+exception.getMessageAndLocation());
        throw exception;
    }

}
