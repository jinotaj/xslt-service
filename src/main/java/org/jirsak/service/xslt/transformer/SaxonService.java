package org.jirsak.service.xslt.transformer;

import net.sf.saxon.s9api.*;

import javax.inject.Singleton;
import javax.xml.transform.stream.StreamSource;
import java.io.OutputStream;
import java.nio.file.Path;

@Singleton
public class SaxonService {
  private final Processor processor;

  public SaxonService() {
    this.processor = new Processor(false);
  }

  public XsltExecutable createXsltExecutable(Path path) throws SaxonApiException {
    StreamSource xsltSource = new StreamSource(path.toFile());
    return processor.newXsltCompiler().compile(xsltSource);
  }

  public Serializer createSerializer(OutputStream outputStream) {
    return processor.newSerializer(outputStream);
  }

  /*
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
  */
}
