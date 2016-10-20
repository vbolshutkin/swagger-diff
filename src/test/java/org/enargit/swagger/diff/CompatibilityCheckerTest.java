package org.enargit.swagger.diff;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import io.swagger.models.Swagger;
import io.swagger.parser.SwaggerParser;

import java.net.URISyntaxException;

import org.junit.Test;

public class CompatibilityCheckerTest {

	private static CompatibilityChecker checker = new CompatibilityChecker();
	
	private static Swagger readResource(String path) {
		try {
			return new SwaggerParser().read(CompatibilityCheckerTest.class.getResource(path).toURI().getPath().toString());
		} catch (URISyntaxException e) {
			throw new IllegalArgumentException(path);
		}
	}
	
	@Test
	public void testCut() throws URISyntaxException {
		Swagger swagger = readResource("/petstore/petstore-swagger.yaml");
		Swagger swaggerCut = readResource("/petstore/petstore-cut-swagger.yaml");
	
		assertFalse(checker.checkCompatibility(swagger, swaggerCut));
	}
	
	@Test
	public void testCutField() throws URISyntaxException {
		Swagger swagger = readResource("/petstore/jaxrs/add-field-lipsum.yaml");
		Swagger swaggerCut = readResource("/petstore/jaxrs/orig.yaml");
		
		assertFalse(checker.checkCompatibility(swagger, swaggerCut));
	}
	
	@Test
	public void testCutDeprecated() throws URISyntaxException {
		Swagger swagger = readResource("/petstore/jaxrs/orig.yaml");
		Swagger swaggerCut = readResource("/petstore/jaxrs/remove-deprecated-findByTags.yaml");
	
		assertTrue(checker.checkCompatibility(swagger, swaggerCut));
	}
	

	
	@Test
	public void testCutXDeprecatedField() throws URISyntaxException {
		Swagger swagger = readResource("/petstore/jaxrs/x-deprecate-field-lipsum.yaml");
		Swagger swaggerCut = readResource("/petstore/jaxrs/orig.yaml");
		
		assertTrue(checker.checkCompatibility(swagger, swaggerCut));
	}
	
	@Test
	public void testEnrich() {
		Swagger swagger = readResource("/petstore/petstore-swagger.yaml");
		Swagger swaggerEnrich = readResource("/petstore/petstore-enrich-swagger.yaml");
		
		assertTrue(checker.checkCompatibility(swagger, swaggerEnrich));
		
	}

}
