package org.jirsak.service.xslt.transformer;

import net.sf.saxon.s9api.Destination;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.XsltTransformer;

import javax.xml.transform.Source;
import java.io.IOException;
import java.io.OutputStream;
import java.util.function.Function;

/**
 * @author Filip Jirsák
 */
public class Transformer {
	private final String fileName;
	private final Function<OutputStream, Destination> destinationFactory;
	private final XsltTransformer firstTransformer;
	private final XsltTransformer lastTransformer;

	public Transformer(String fileName, Function<OutputStream, Destination> destinationFactory, XsltTransformer firstTransformer, XsltTransformer lastTransformer) {
		this.fileName = fileName;
		this.destinationFactory = destinationFactory;
		this.firstTransformer = firstTransformer;
		this.lastTransformer = lastTransformer;
	}

		public void transform(Source source, OutputStream outputStream) throws IOException, SaxonApiException {
			Destination destination = destinationFactory.apply(outputStream);
			firstTransformer.setSource(source);
			lastTransformer.setDestination(destination);
			firstTransformer.transform();
		}

	public String getFileName() {
		return fileName;
	}
}
