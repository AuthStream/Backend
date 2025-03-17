package authstream.utils;

public class ValidStringDb {


    public static boolean  checkUri(String uri) {
        if (uri == null || uri.trim().isEmpty()) {
            return false;
        }
    
        String trimmedUri = uri.trim();
    
        if (trimmedUri.startsWith("jdbc:")) {
            trimmedUri = trimmedUri.substring(5);
        }
    
        String[] protocolAndRest = trimmedUri.split("://", 2);
        if (protocolAndRest.length < 2 || protocolAndRest[0].isEmpty()) {
            return false; 
        }
    
        String hostPortDb = protocolAndRest[1];
    
        String[] hostPortDbParts = hostPortDb.split("/", 2);
        if (hostPortDbParts.length < 2 || hostPortDbParts[1].isEmpty()) {
            return false; 
        }
    
        String hostPort = hostPortDbParts[0];
    
        String[] hostPortParts = hostPort.split(":", 2);
        if (hostPortParts.length < 2) {
            return false;
        }
    
        String host = hostPortParts[0];
        String portStr = hostPortParts[1];
    
        if (host.isEmpty()) {
            return false; 
        }
    
        try {
            int port = Integer.parseInt(portStr);
            if (port <= 0 || port > 65535) {
                return false; 
            }
        } catch (NumberFormatException e) {
            return false; 
        }
    
        return true;
    }

    public static boolean checkConnectionString(String connectionString) {
        if (connectionString == null || connectionString.trim().isEmpty()) {
            return false;
        }
    
        String trimmedConnectionString = connectionString.trim();
    
        if (!trimmedConnectionString.startsWith("jdbc:")) {
            return false;
        }
    
        String[] uriAndParams = trimmedConnectionString.split("\\?", 2);
        if (uriAndParams.length == 0) {
            return false;
        }
    
        String uri = uriAndParams[0]; 
        String queryParams = uriAndParams.length > 1 ? uriAndParams[1] : null;
    
        if (!checkUri(uri)) {
            return false;
        }
    
        if (queryParams != null && !queryParams.isEmpty()) {
            String[] params = queryParams.split("&");
            for (String param : params) {
                String[] keyValue = param.split("=", 2);
                if (keyValue.length < 2 || keyValue[0].isEmpty() || keyValue[1].isEmpty()) {
                    return false; 
                }
            }
        }
    
        return true;
    }
}