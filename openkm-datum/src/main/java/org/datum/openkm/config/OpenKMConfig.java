package org.datum.openkm.config;

import io.smallrye.config.ConfigMapping;

@ConfigMapping(prefix = "openkm.api")
public interface OpenKMConfig {
    String url();
    String username();
    String password();
    int timeout();
}
