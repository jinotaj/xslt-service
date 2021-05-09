package org.jirsak.service.xslt.transformer;

import io.micronaut.http.MediaType;
import net.sf.saxon.s9api.Destination;
import net.sf.saxon.s9api.SAXDestination;
import org.apache.fop.apps.FOPException;
import org.apache.fop.apps.FOUserAgent;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.xml.sax.SAXException;

import javax.inject.Singleton;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;

@Singleton
public class FopService {
	private final FopFactory fopFactory;

	public FopService(TransformerConfig transformerConfig) throws IOException, SAXException {
		this.fopFactory = FopFactory.newInstance(transformerConfig.getRoot().resolve(".fop/fop.xconf").toFile());
	}

	public Fop createFop(OutputStream outputStream, Path basePath) throws FOPException {
		FOUserAgent userAgent = fopFactory.newFOUserAgent();
		return userAgent.newFop(MediaType.APPLICATION_PDF, outputStream);
	}

	public Destination createFopDestination(OutputStream outputStream, Path basePath) {
		try {
			Fop fop = createFop(outputStream, basePath);
			return new SAXDestination(fop.getDefaultHandler());
		} catch (FOPException e) {
			throw new RuntimeException(e);
		}
	}
}
