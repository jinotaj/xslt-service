package org.jirsak.service.xslt;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.umd.cs.findbugs.annotations.Nullable;
import io.micronaut.core.io.Writable;
import io.micronaut.http.HttpHeaders;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import net.sf.saxon.s9api.SaxonApiException;
import org.dom4j.io.DocumentSource;
import org.jirsak.service.xslt.transformer.Transformer;
import org.jirsak.service.xslt.transformer.TransformerService;
import org.jirsak.service.xslt.xml.XmlService;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.nio.charset.Charset;

@Controller
public class XsltController {
  private final TransformerService transformerService;
  private final XmlService xmlService;

  public XsltController(TransformerService transformerService, XmlService xmlService) {
    this.transformerService = transformerService;
    this.xmlService = xmlService;
  }

  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_PDF)
  @Post("/{+path}")
  public HttpResponse<OutputStreamWritable> fromJSON(@PathVariable String path, @Body TreeNode json, @QueryValue(value = "root", defaultValue = "json") String root) throws IOException, SaxonApiException {
    DocumentSource source = xmlService.toSource(json, root);
    Transformer transformer = transformerService.getTransformer(path);
    return HttpResponse.created(new OutputStreamWritable(source, transformer))
        .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition(transformer.getFileName()));
  }

  @Consumes(MediaType.APPLICATION_XML)
  @Produces(MediaType.APPLICATION_PDF)
  @Post("/{+path}")
  public HttpResponse<OutputStreamWritable> fromXML(@PathVariable String path, @Body byte[] buffer) throws IOException, SaxonApiException {
    StreamSource source = xmlService.toSource(buffer);
    Transformer transformer = transformerService.getTransformer(path);
    return HttpResponse.created(new OutputStreamWritable(source, transformer))
        .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition(transformer.getFileName()));
  }

  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  @Produces(MediaType.APPLICATION_PDF)
  @Post("/{+path}")
  public HttpResponse<OutputStreamWritable> fromJSONForm(@PathVariable String path, String type, String data, String root) throws IOException, SaxonApiException {
    ObjectMapper mapper = new ObjectMapper();
    JsonFactory factory = mapper.getFactory();
    JsonParser parser = factory.createParser(data);
    JsonNode json = mapper.readTree(parser);
    DocumentSource source = xmlService.toSource(json, root);
    Transformer transformer = transformerService.getTransformer(path);
    return HttpResponse.created(new OutputStreamWritable(source, transformer))
        .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition(transformer.getFileName()));
  }

  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_XML)
  @Post("/to-xml")
  public String jsonToXML(@Body TreeNode json, @QueryValue(value = "root", defaultValue = "json") String root) throws IOException {
    return xmlService.toDocument(json, root).asXML();
  }

  private String contentDisposition(String filename) {
    StringBuilder builder = new StringBuilder();
    builder.append("attachment");
    if (filename != null) {
      builder.append("; filename=");
      builder.append('"');
      builder.append(filename);
      builder.append('"');
    }
    return builder.toString();
  }

  private class OutputStreamWritable implements Writable {
    private final Source source;
    private final Transformer transformer;

    private OutputStreamWritable(Source source, Transformer transformer) {
      this.source = source;
      this.transformer = transformer;
    }

    @Override
    public void writeTo(Writer out) throws IOException {
      throw new UnsupportedOperationException();
    }

    @Override
    public void writeTo(OutputStream outputStream, @Nullable Charset charset) throws IOException {
      try {
        transformer.transform(source, outputStream);
      } catch (SaxonApiException e) {
        throw new IOException(e);
      }
    }
  }
}
