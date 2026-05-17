export const environment = {
  production: false,
  // All requests go to Spring Cloud Gateway (port 8080)
  // Gateway then routes to correct microservice automatically
  apiUrl: 'http://localhost:8080'
};
