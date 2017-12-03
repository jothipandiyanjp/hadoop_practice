package com.bizruntime.samza.custom.metrics;

import org.apache.samza.config.Config;
import org.apache.samza.metrics.MetricsReporter;
import org.apache.samza.metrics.MetricsReporterFactory;

public class Custom implements MetricsReporterFactory{

	@Override
	public MetricsReporter getMetricsReporter(String name,
			String containerName, Config config) {
		
		return null;
	}
}
