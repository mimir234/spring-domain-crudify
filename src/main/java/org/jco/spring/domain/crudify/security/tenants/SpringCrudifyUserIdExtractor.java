package org.jco.spring.domain.crudify.security.tenants;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(name = "spring.domain.crudify.security.extractUserId", havingValue = "enabled", matchIfMissing = true)
public class SpringCrudifyUserIdExtractor {

}
