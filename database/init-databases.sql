-- Create additional databases for microservices
CREATE DATABASE nexhub_user;
CREATE DATABASE nexhub_blog;
CREATE DATABASE nexhub_product;
CREATE DATABASE nexhub_warranty;
CREATE DATABASE nexhub_notification;
CREATE DATABASE nexhub_language;

-- Grant privileges to nexhub user
GRANT ALL PRIVILEGES ON DATABASE nexhub_user TO nexhub;
GRANT ALL PRIVILEGES ON DATABASE nexhub_blog TO nexhub;
GRANT ALL PRIVILEGES ON DATABASE nexhub_product TO nexhub;
GRANT ALL PRIVILEGES ON DATABASE nexhub_warranty TO nexhub;
GRANT ALL PRIVILEGES ON DATABASE nexhub_notification TO nexhub;
GRANT ALL PRIVILEGES ON DATABASE nexhub_language TO nexhub;