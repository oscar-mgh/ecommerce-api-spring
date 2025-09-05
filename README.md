
# E-commerce API

A Spring Boot-based REST API for managing products, categories, and orders with JWT authentication and role-based access control.
<p align="center">
  <img src="assets/sb.svg" alt="Spring Boot Logo" width="110" height="110"/>
</p>

## Features

- **Product Management**: CRUD operations for products with category association
- **Category Management**: Pre-defined categories with product relationships
- **User Authentication**: JWT-based authentication with admin role support
- **Order Management**: Order creation and management system
- **Security**: Role-based access control and input validation
- **Database**: MySQL database with JPA/Hibernate ORM

## Technology Stack

- **Java 21**
- **Spring Boot 3.5.5**
- **Spring Security** with JWT
- **Spring Data JPA** with Hibernate
- **MySQL Database**
- **Maven** for dependency management

## Prerequisites

- Java 21 or higher
- MySQL 8.0 or higher
- Maven 3.6 or higher

## Quick Start

### 1. Clone the Repository
```bash
git clone <repository-url>
cd ecommerce-api
```

### 2. Database Setup
Create a MySQL database named `ecommerce_db` or update the connection details in `application.properties`.

### 3. Build the Project
```bash
mvn clean install
```

### 4. Run the Application
```bash
# Development mode
mvn spring-boot:run

# Or using the JAR file
java -jar target/ecommerce-1.0.0.jar
```

The API will be available at `http://localhost:8080`

## API Endpoints

### Authentication
- `POST /api/auth/login` - User login

### Products
- `GET /api/products` - Get all products
- `GET /api/products/{id}` - Get product by ID
- `POST /api/products` - Create new product (Admin only)
- `PUT /api/products/{id}` - Update product (Admin only)
- `PATCH /api/products/{id}` - Partial update (Admin only)
- `DELETE /api/products/{id}` - Delete product (Admin only)

### Categories
- `GET /api/categories` - Get all categories

### Orders
- `GET /api/orders` - Get all orders
- `POST /api/orders` - Create new order

## API Usage Examples

### Creating a Product

```bash
POST /api/products
Authorization: Bearer <jwt-token>
Content-Type: application/json

{
  "name": "iPhone 16",
  "description": "Latest smartphone",
  "price": 999.99,
  "stock": 50,
  "category": "Electronics",
  "imgUrl": "https://example.com/images/iphone16.png"
}

Note: If the `category` provided in the product request does not exist, the API will automatically create that category.
```

### Updating a Product

```bash
PUT /api/products/1
Authorization: Bearer <jwt-token>
Content-Type: application/json

{
  "name": "iPhone 16 Pro",
  "description": "Latest premium smartphone",
  "price": 1199.99,
  "stock": 25,
  "category": "Electronics",
  "imgUrl": "https://example.com/images/iphone16pro.png"
}
```

### Partial Update

```bash
PATCH /api/products/1
Authorization: Bearer <jwt-token>
Content-Type: application/json

{
  "price": 899.99,
  "category": "Electronics",
  "imgUrl": "https://example.com/images/iphone16-sale.png"
}

Note: If the `category` provided in a PATCH request does not exist, the API will automatically create that category.
```

## Default Categories

The system comes with these pre-defined categories:
- Electronics
- Clothing
- Books
- Home & Garden
- Sports

## Authentication & Security

- **Read operations** (GET): No authentication required
- **Write operations** (POST, PUT, PATCH, DELETE): Admin role required
- JWT tokens are used for authentication
- Default admin credentials (change in production):
  - Username: `admin`
  - Password: `admin123`

## Production Deployment

### Environment Variables

When running the application in production, you **MUST** provide the following environment variables:

#### Required Variables

```bash
# Database Configuration
SPRING_DATASOURCE_URL=jdbc:mysql://your-db-host:3306/ecommerce_db
SPRING_DATASOURCE_USERNAME=your-db-username
SPRING_DATASOURCE_PASSWORD=your-db-password

# Admin User Configuration
ADMIN_USERNAME=your-admin-username
ADMIN_EMAIL=your-admin-email
ADMIN_PASSWORD=your-secure-admin-password

# JWT Configuration
JWT_SECRET=your-very-long-and-secure-jwt-secret-key
# Default JWT expiration time 24 hours = 86,400,000 milliseconds
JWT_EXPIRATION=86400000 

# Server Configuration
SERVER_PORT=8080
```

#### Running with Environment Variables

```bash
# Option 1: Using a properties file
java -jar target/ecommerce-1.0.0.jar \
  --spring.profiles.active=prod \
  --spring.config.location=file:./application-prod.properties

# Option 2: Inline environment variables
java -jar target/ecommerce-1.0.0.jar \
  --spring.profiles.active=prod \
  --spring.datasource.url="jdbc:mysql://your-db-host:3306/ecommerce_db" \
  --spring.datasource.username="your-db-username" \
  --spring.datasource.password="your-db-password" \
  --admin.username="your-admin-username" \
  --admin.email="your-admin-email" \
  --admin.password="your-secure-admin-password" \
  --jwt.secret="your-very-long-and-secure-jwt-secret-key" \
  --jwt.expiration="86400000" \
  --server.port="8080"
```

### Docker Deployment

```bash
# Build the Docker image
docker build -t ecommerce-api .

# Run with environment variables
docker run -d \
  -p 8080:8080 \
  -e SPRING_DATASOURCE_URL="jdbc:mysql://your-db-host:3306/ecommerce_db" \
  -e SPRING_DATASOURCE_USERNAME="your-db-username" \
  -e SPRING_DATASOURCE_PASSWORD="your-db-password" \
  -e ADMIN_USERNAME="your-admin-username" \
  -e ADMIN_EMAIL="your-admin-email" \
  -e ADMIN_PASSWORD="your-secure-admin-password" \
  -e JWT_SECRET="your-very-long-and-secure-jwt-secret-key" \
  -e JWT_EXPIRATION="86400000" \
  --name ecommerce-api \
  ecommerce-api
```

### Security Considerations

1. **Never use default passwords** in production
2. **Use strong, unique JWT secrets** (at least 256 bits)
3. **Secure database connections** with proper credentials
4. **Use HTTPS** in production environments
5. **Regularly rotate** JWT secrets and admin passwords
6. **Monitor application logs** for security events

## Development

### Running Tests
```bash
mvn test
```

### Building for Production
```bash
mvn clean package -DskipTests
```
