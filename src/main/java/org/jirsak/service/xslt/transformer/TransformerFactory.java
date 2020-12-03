package org.jirsak.service.xslt.transformer;

import net.sf.saxon.s9api.Destination;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.XsltExecutable;
import net.sf.saxon.s9api.XsltTransformer;

import javax.xml.transform.Source;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.function.Function;

/**
 * @author Filip Jirsák
 */
public class TransformerFactory {
	private final Deque<XsltExecutable> xslts = new LinkedList<>();
	private Function<OutputStream, Destination> destinationFactory;
	private String fileName;

	public Transformer create() {
		Iterator<XsltExecutable> executableIterator = xslts.iterator();
		XsltTransformer firstTransformer = executableIterator.next().load();
		XsltTransformer previousTransformer = firstTransformer;
		while (executableIterator.hasNext()) {
			XsltTransformer transformer = executableIterator.next().load();
			previousTransformer.setDestination(transformer);
			previousTransformer = transformer;
		}
		XsltTransformer lastTransformer = previousTransformer;
		return new Transformer(fileName, destinationFactory, firstTransformer, lastTransformer);
	}

	public void appendXslt(XsltExecutable xsltExecutable) {
		xslts.addLast(xsltExecutable);
	}

	public void setDestinationFactory(Function<OutputStream, Destination> destinationFactory) {
		this.destinationFactory = destinationFactory;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
}
