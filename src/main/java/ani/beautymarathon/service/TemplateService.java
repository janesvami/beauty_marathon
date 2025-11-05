package ani.beautymarathon.service;

import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.stereotype.Service;

    @Service
    public class TemplateService {
        final private Configuration freeMarkerConfiguration;

        public TemplateService(Configuration freeMarkerConfiguration) {
            this.freeMarkerConfiguration = freeMarkerConfiguration;
        }

        public Template getTemplate(String name) throws Exception {
            return freeMarkerConfiguration.getTemplate(name);
        }
    }