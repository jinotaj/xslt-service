package org.jirsak.service.xslt.fop;

import org.apache.fop.apps.FOPException;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.xml.sax.SAXException;

import javax.inject.Singleton;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

@Singleton
public class FopService {
  private final FopFactory fopFactory;

  public FopService() throws IOException, SAXException {
    this.fopFactory = FopFactory.newInstance(new File(".fop/fop.xconf"));
  }

  public Fop fop(OutputStream outputStream) throws FOPException {
    return fopFactory.newFop("application/pdf", outputStream);
  }
}
