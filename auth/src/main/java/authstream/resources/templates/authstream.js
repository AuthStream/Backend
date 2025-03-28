async function root(r) {
    try {
        const method = r.method;
        const contentType = r.headersIn['Content-Type'] || 'text/plain';
        let requestBody = r.requestBuffer ? r.requestBuffer.toString() : '';

        let bodyObj;
        if (contentType.includes('application/json') && requestBody) {
            try {
                bodyObj = JSON.parse(requestBody);
            } catch (e) {
                r.log(`Failed to parse JSON body: ${e.message}`);
                bodyObj = requestBody;
            }
        } else {
            bodyObj = requestBody;
        }

        // Your auth server endpoint: http://127.0.0.1:8080
        const authResponse = await ngx.fetch('[[$AUTH_SERVER]]', {
            method: method,
            headers: {
                "Content-Type": contentType
            },
            body: contentType.includes('application/json') && typeof bodyObj === 'object'
                ? JSON.stringify(bodyObj)
                : requestBody
        });

        if (200 <= authResponse.status && authResponse.status <= 299) {
            const authContentType = authResponse.headers.get('Content-Type') || 'text/plain';
            let authBody;
            if (authContentType.includes('application/json')) {
                authBody = await authResponse.json();
            } else {
                authBody = await authResponse.text();
            }
            let backendRequestBody;
            if (authContentType.includes('application/json') && typeof authBody === 'object') {
                backendRequestBody = JSON.stringify(authBody);
            } else {
                backendRequestBody = authBody;
            }

            // Your backend server endpoint: http://localhost:8081
            const backendResponse = await ngx.fetch('[[${APP_SERVER_DOMAIN}]]', {
                method: method,
                headers: {
                    "Content-Type": authContentType
                },
                body: backendRequestBody
            });

            let responseData;
            const backendContentType = backendResponse.headers.get('Content-Type') || 'text/plain';
            if (backendContentType.includes('application/json')) {
                responseData = await backendResponse.json();
                r.return(200, `Root got: ${JSON.stringify(responseData)}`);
            } else {
                responseData = await backendResponse.text();
                r.return(200, `Root got: ${responseData}`);
            }
        } else {
            r.return(200, 'Dell on roi');
        }
    } catch (error) {
        r.return(500, `Error: ${error.message}`);
    }
}

export default { root };