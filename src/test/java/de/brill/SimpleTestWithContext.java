package de.brill;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import de.brill.heartbeat.HeartBeat2;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = BrillWaageApplication.class)
@WebAppConfiguration
public class SimpleTestWithContext {

	@Test
	public void contextLoads() throws Exception {
		Thread.sleep(3000);
	}

}
