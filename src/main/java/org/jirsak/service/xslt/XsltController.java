package org.jirsak.service.xslt;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micronaut.http.HttpHeaders;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import net.sf.saxon.s9api.SaxonApiException;
import org.dom4j.io.DocumentSource;
import org.jirsak.service.xslt.transformer.Transformer;
import org.jirsak.service.xslt.transformer.TransformerService;
import org.jirsak.service.xslt.xml.XmlService;

import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

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
  @Post("/{path}")
  public HttpResponse<byte[]> fromJSON(@PathVariable String path, TreeNode json, @QueryValue(value = "root", defaultValue = "json") String root) throws IOException, SaxonApiException {
    DocumentSource source = xmlService.toSource(json, root);
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    Transformer transformer = transformerService.getTransformer(path);
    transformer.transform(source, output);
    return HttpResponse.created(output.toByteArray())
        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"dokument.pdf\"");
  }

  @Consumes(MediaType.APPLICATION_XML)
  @Produces(MediaType.APPLICATION_PDF)
  @Post("/{path}")
  public HttpResponse<byte[]> fromXML(@PathVariable String path, @Body byte[] buffer) throws IOException, SaxonApiException {
    StreamSource source = xmlService.toSource(buffer);
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    Transformer transformer = transformerService.getTransformer(path);
    transformer.transform(source, output);
    return HttpResponse.created(output.toByteArray())
        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"dokument.pdf\"");
  }

  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  @Produces(MediaType.APPLICATION_PDF)
  @Post("/{path}")
  public HttpResponse<byte[]> fromJSONForm(@PathVariable String path, String type, String data, String root) throws IOException, SaxonApiException {
    ObjectMapper mapper = new ObjectMapper();
    JsonFactory factory = mapper.getFactory();
    JsonParser parser = factory.createParser(data);
    JsonNode json = mapper.readTree(parser);
    DocumentSource source = xmlService.toSource(json, root);
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    Transformer transformer = transformerService.getTransformer(path);
    transformer.transform(source, output);
    return HttpResponse.created(output.toByteArray())
        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"dokument.pdf\"");
  }

  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_XML)
  @Post("/to-xml")
  public String jsonToXML(TreeNode json, @QueryValue(value = "root", defaultValue = "json") String root) throws IOException {
    return xmlService.toDocument(json, root).asXML();
  }
}
