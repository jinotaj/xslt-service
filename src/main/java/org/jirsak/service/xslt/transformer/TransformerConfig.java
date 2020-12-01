package org.jirsak.service.xslt.transformer;

import io.micronaut.context.annotation.ConfigurationProperties;

import javax.validation.constraints.NotBlank;
import java.nio.file.Path;

/**
 * @author Filip Jirsák
 */
@ConfigurationProperties("service.transformer")
public interface TransformerConfig {
	@NotBlank
	Path getRoot();
}
