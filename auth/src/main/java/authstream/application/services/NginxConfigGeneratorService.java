package authstream.application.services;

import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class NginxConfigGeneratorService {
    private final TemplateEngine templateEngine;
    public NginxConfigGeneratorService(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    public String generateNginxConfig(
            String yourPort,
            String yourDomain,
            String yourCallbackUrl,
            String yourAuthServer
        ) {
        Context context = new Context();
        context.setVariable("YOUR_PORT", yourPort);
        context.setVariable("YOUR_DOMAIN", yourDomain);
        context.setVariable("YOUR_CALLBACK_URL", yourCallbackUrl);
        context.setVariable("YOUR_AUTH_SERVER", yourAuthServer);

        return templateEngine.process("nginx.conf", context);
    }

    public void saveNginxConfig(String content, String outputPath) throws IOException {
        Path path = Paths.get(outputPath);
        try (FileWriter writer = new FileWriter(path.toFile())) {
            writer.write(content);
        }
    }
}