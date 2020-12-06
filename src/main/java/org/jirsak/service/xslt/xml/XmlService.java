package org.jirsak.service.xslt.xml;

import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ValueNode;
import org.dom4j.Document;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.dom4j.QName;
import org.dom4j.io.DocumentSource;

import javax.inject.Provider;
import javax.inject.Singleton;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;
import java.util.Iterator;

@Singleton
public class XmlService {

  private final DocumentFactory documentFactory = DocumentFactory.getInstance();
  private final QName XSI_NIL = QName.get("nil", "xsi", "http://www.w3.org/2001/XMLSchema-instance");

  public StreamSource toSource(byte[] buffer) {
    return new StreamSource(new ByteArrayInputStream(buffer));
  }

  public Document toDocument(TreeNode json, String root) {
    Document document = documentFactory.createDocument();
    if (json.isArray()) {
      Element rootElement = document.addElement(root);
      processNode(json, () -> rootElement.addElement(root));
    } else {
      processNode(json, () -> document.addElement(root));
    }
    return document;
  }

  public DocumentSource toSource(TreeNode json, String root) {
    Document document = toDocument(json, root);
    return new DocumentSource(document);
  }

  private void processNode(TreeNode json, Provider<Element> elementProvider) {
    if (json.isArray()) {
      for (int i = 0; i < json.size(); i++) {
        processNode(json.get(i), elementProvider);
      }
    } else if (json.isObject()) {
      Element element = elementProvider.get();
      Iterator<String> names = json.fieldNames();
      while (names.hasNext()) {
        String name = names.next();
        TreeNode node = json.get(name);
        processNode(node, () -> element.addElement(name));
      }
    } else {
      Element element = elementProvider.get();
      ValueNode valueNode = (ValueNode) json;
      if (!(valueNode instanceof NullNode)) {
        element.setText(valueNode.asText());
      } else {
        element.addAttribute(XSI_NIL, "true");
      }
    }
  }
}
