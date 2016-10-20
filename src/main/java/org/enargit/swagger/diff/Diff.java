package org.enargit.swagger.diff;

import io.swagger.models.Swagger;
import io.swagger.parser.SwaggerParser;

public class Diff {

	public static void main(String[] args) {
		Swagger swagger = new SwaggerParser().read("http://petstore.swagger.io/v2/swagger.yaml");
	}

}
