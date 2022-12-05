package org.openmrs.module.authenticationui;

import org.junit.jupiter.api.Test;

public class AuthenticationUiModuleActivatorTest {

	@Test
	public void shouldStartup() {
		AuthenticationUiModuleActivator activator = new AuthenticationUiModuleActivator();
		activator.started();
	}

	@Test
	public void shouldShutdown() {
		AuthenticationUiModuleActivator activator = new AuthenticationUiModuleActivator();
		activator.stopped();
	}
}
