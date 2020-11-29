package org.jirsak.service.xslt;

import com.fasterxml.jackson.core.TreeNode;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Consumes;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.annotation.QueryValue;
import org.jirsak.service.xslt.saxon.SaxonService;
import org.jirsak.service.xslt.xml.XmlService;

import java.io.IOException;

@Controller("/pdf")
public class XsltController {
  private final SaxonService saxonService;
  private final XmlService xmlService;

  public XsltController(SaxonService saxonService, XmlService xmlService) {
    this.saxonService = saxonService;
    this.xmlService = xmlService;
  }

  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_PDF)
  @Post
  public byte[] fromJSON(TreeNode json, @QueryValue(value = "root", defaultValue = "json") String root) throws IOException {
    return saxonService.transform(xmlService.toSource(json, root));
  }

  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_XML)
  @Post("xml")
  public String jsonToXML(TreeNode json, @QueryValue(value = "root", defaultValue = "json") String root) throws IOException {
    return xmlService.toDocument(json, root).asXML();
  }

  @Consumes(MediaType.APPLICATION_XML)
  @Produces(MediaType.APPLICATION_PDF)
  @Post
  public byte[] fromXML(@Body byte[] buffer) throws IOException {
    return saxonService.transform(xmlService.toSource(buffer));
  }
}
