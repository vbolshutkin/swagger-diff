package org.enargit.swagger.diff;


import io.swagger.models.HttpMethod;
import io.swagger.models.Model;
import io.swagger.models.Operation;
import io.swagger.models.Path;
import io.swagger.models.Response;
import io.swagger.models.Swagger;
import io.swagger.models.properties.Property;
import io.swagger.models.properties.RefProperty;

import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CompatibilityChecker {

	// variable for checked DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES .
	// TODO use it
	private boolean  failOnUnknownProperties= false;
	private static final Logger log = LoggerFactory
			.getLogger(CompatibilityChecker.class);

	public boolean checkCompatibility(Swagger ref, Swagger target) {

		for (Map.Entry<String, Path> entry : ref.getPaths().entrySet()) {
			/**
			 * check for comparing paths.
			 */
			if (target.getPath(entry.getKey()) == null) {
				boolean allDeprecated = true;
				for (Operation operation : entry.getValue().getOperations()) {
					if (!(Boolean.TRUE == operation.isDeprecated())) allDeprecated = false;
				}

				if (allDeprecated) {
					log.info("Path: " + entry.getKey() + " - is deprecated");
				} else {
					log.error("Path: " + entry.getKey() + " - is missing");
					return false;
				}
			}
			
			for (Entry<HttpMethod,Operation> operationEntry : entry.getValue().getOperationMap().entrySet()) {
				if (target.getPath(entry.getKey()) == null) continue;
				if (!target.getPath(entry.getKey()).getOperationMap().containsKey(operationEntry.getKey())) {
					if (Boolean.TRUE == operationEntry.getValue().isDeprecated()) {
//						log.info("");
					} else {
						log.info("Operation " + entry.getKey()  + " is missing from Path " + entry.getKey() + "is missing");
						return false;
					}
				} else{

				}

				Operation refOp = operationEntry.getValue();
				Operation targetOp = target.getPath(entry.getKey()).getOperationMap().get(operationEntry.getKey());
				
				for (Entry<String, Response> respEntry : refOp.getResponses().entrySet()) {
					if (!targetOp.getResponses().containsKey(respEntry.getKey())) {
						// XXX missing response not necessarily should fail
						return false;
					}
					
					Property prop = respEntry.getValue().getSchema();
					Property targetProp = targetOp.getResponses().get(respEntry.getKey()).getSchema();
					
					for (Entry<String, Property> propEntry : getResponseSchemaProperties(ref, prop).entrySet()) {
						if (!getResponseSchemaProperties(target, targetProp).containsKey(propEntry.getKey())) {
							boolean isDeprecated = 
									StringUtils.contains(propEntry.getValue().getDescription(), "@Deprecated")
									|| propEntry.getValue().getVendorExtensions().containsKey("x-deprecated");
							if (isDeprecated) {
								System.out.println(propEntry.getKey() + " isDeprecated");
							} else {
								return false;
							}
						}
					}
				}
			}
		}
		return true;
	}
	
	private Map<String, Property> getResponseSchemaProperties(Swagger ref, Property prop) {
		if (prop instanceof RefProperty) {
			String defref = ((RefProperty)prop).get$ref();
			Model def = ref.getDefinitions().get(defref.replace("#/definitions/", ""));
			return def.getProperties();
		}
		return Collections.emptyMap();
	}

}
