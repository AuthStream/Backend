package authstream.application.services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;

import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.StringTemplateResolver;

@Service
public class NginxConfigGeneratorService {

    public NginxConfigGeneratorService() {
        StringTemplateResolver resolver = new StringTemplateResolver();
        resolver.setTemplateMode(TemplateMode.TEXT);

        TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(resolver);
        templateEngine.setDialects(Collections.emptySet());
    }

    public String generateJsConfig(String authServer, String appServerDomain) throws IOException {
        Path jsTemplatePath = Paths.get("src/main/java/authstream/resources/templates/authstream.js").toAbsolutePath();
        System.out.println("Using template: " + jsTemplatePath.toString());
        String jsContent = Files.readString(jsTemplatePath);

        return jsContent
                .replace("[[${AUTH_SERVER}]]", authServer)
                .replace("[[${APP_SERVER_DOMAIN}]]", appServerDomain);
    }

    public String generateNginxConfig(Integer nginxPort, String domainName) {
        String templateString = """
                load_module modules/ngx_http_js_module.so;
                load_module modules/ngx_stream_js_module.so;

                events {
                    worker_connections 1024;
                }

                http {
                    js_import main from authstream.js;
                    resolver 8.8.8.8;

                    server {
                        listen __NGINX_PORT__;s
                        server_name __DOMAIN_NAME__;

                        location / {
                            js_content main.root;
                        }
                    }
                }
                """;

        return templateString
                .replace("__NGINX_PORT__", String.valueOf(nginxPort))
                .replace("__DOMAIN_NAME__", domainName);
    }

    public static void main(String[] args) throws IOException {
        NginxConfigGeneratorService nginxConfigGeneratorService = new NginxConfigGeneratorService();

        String generatedConfig = nginxConfigGeneratorService.generateNginxConfig(8080, "localhost");
        System.out.println("Generated Nginx Config:");
        System.out.println(generatedConfig);
        String generatedJsConfig = nginxConfigGeneratorService.generateJsConfig("http://localhost:8080", "localhost");
        System.out.println("Generated JS Config:");
        System.out.println(generatedJsConfig);
    }
}