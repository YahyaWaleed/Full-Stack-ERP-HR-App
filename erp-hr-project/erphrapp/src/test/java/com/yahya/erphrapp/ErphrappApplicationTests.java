package com.yahya.erphrapp;

import org.junit.jupiter.api.Test;

// starting the context runs every Flyway migration and ddl-auto=validate, so this fails if entities and schema drift
class ErphrappApplicationTests extends AbstractIntegrationTest {

	@Test
	void contextLoads() {
	}

}
