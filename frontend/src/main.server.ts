import { bootstrapApplication } from '@angular/platform-browser';
import type { BootstrapContext } from '@angular/platform-browser';
import { App } from './app/app';
import { config } from './app/app.config.server';

const bootstrap = (serverContext?: BootstrapContext) => {
	return bootstrapApplication(App, config, serverContext);
};

export default bootstrap;
