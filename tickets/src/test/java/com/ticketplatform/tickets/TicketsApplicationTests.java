package com.ticketplatform.tickets;

import com.ticketplatform.tickets.config.TestSecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest
@Import(TestSecurityConfig.class)
class TicketsApplicationTests {

	@Test
	void contextLoads() {
	}

}
