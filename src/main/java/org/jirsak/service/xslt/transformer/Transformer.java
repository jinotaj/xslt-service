package org.jirsak.service.xslt.transformer;

import net.sf.saxon.s9api.SaxonApiException;

import javax.xml.transform.Source;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author Filip Jirsák
 */
public interface Transformer {
	void transform(Source source, OutputStream output) throws IOException, SaxonApiException;
}
