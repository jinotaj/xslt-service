package org.jirsak.service.xslt.saxon;

import io.micronaut.core.io.Writable;
import net.sf.saxon.s9api.Processor;
import net.sf.saxon.s9api.SAXDestination;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.Serializer;
import net.sf.saxon.s9api.XsltTransformer;
import org.apache.fop.apps.FOPException;
import org.apache.fop.apps.Fop;
import org.jirsak.service.xslt.fop.FopService;

import javax.inject.Singleton;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

@Singleton
public class SaxonService {
  private final Processor processor;
  private final File repository;
  private final FopService fopService;

  public SaxonService(FopService fopService) {
    this.fopService = fopService;
    this.processor = new Processor(false);
    this.repository = new File("xslt");
  }

  public byte[] transform(Source documentSource) throws IOException {
    try {
      StreamSource xsltSource = new StreamSource(new File(repository, "pdf.xslt"));
      XsltTransformer transformer = processor.newXsltCompiler()
              .compile(xsltSource)
              .load();
      ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
      Fop fop = fopService.fop(byteArrayOutputStream);
      SAXDestination destination = new SAXDestination(fop.getDefaultHandler());
      transformer.setSource(documentSource);
      transformer.setDestination(destination);
      transformer.transform();
      transformer.close();
      return byteArrayOutputStream.toByteArray();
    } catch (SaxonApiException | FOPException e) {
      throw new IOException(e);
    }
  }
}
