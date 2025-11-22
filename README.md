# Brightspot Project

A comprehensive content management system built on the Brightspot CMS platform, featuring a modern Java-based backend with a styleguide-driven frontend architecture.

## Overview

This project is a full-featured web application built using Brightspot CMS 3.x, providing content management capabilities for articles, blog posts, events, galleries, forms, and more. The application follows a modular architecture with a Java backend and a component-based frontend using Handlebars templates and LESS stylesheets.

### Key Features

- **Content Management**: Articles, blog posts, events, galleries, FAQs, and rich media content
- **User Management**: Authentication system with user registration and session management
- **Media Handling**: Image and video processing with multiple provider support (YouTube, Vimeo)
- **Forms & Interactions**: Dynamic form builder with various input types and validation
- **SEO & Analytics**: Built-in SEO optimization and Google Analytics integration
- **Modular Design**: Component-based architecture with reusable modules and promotions
- **Responsive Design**: Mobile-first responsive design with modern UI components

### Technology Stack

- **Backend**: Java 8, Brightspot CMS 3.x, Dari Framework
- **Database**: MySQL 5.6+ with Solr 4.8.1+ for search
- **Frontend**: Handlebars templates, LESS stylesheets, JavaScript (ES5+)
- **Build Tools**: Maven, Grunt, NPM, Bower
- **Deployment**: Docker Compose with Tomcat 8.x

## Architecture

The project follows a layered architecture:

- **Model Layer**: Content types (Articles, Blog Posts, Events, etc.) with rich metadata
- **View Layer**: Handlebars templates with corresponding ViewModels
- **Service Layer**: Business logic, integrations, and utilities
- **API Layer**: RESTful endpoints and servlets for frontend interactions

## Local Dev Environment Setup

This is a standard Brightspot 3.x deployment. Versions for the various components are listed below. An 'x' signifies that the latest point release version should be used but an older version is fine too. A '^' signifies that at least that point release version **MUST** be used but a later version is fine too.

### Required Components
* Java 1.8.x
* Apache 2.2.x
* Tomcat 8.x
* MySQL 5.6.x
* Solr 4.8.1^

## Quick Start

### Using Docker (Recommended)
```bash
# Build the project
npm run build

# Start all services
npm run docker
```

### Manual Setup
```bash
# Install dependencies
npm install

# Build the project
mvn clean -P library verify

# Run styleguide development server
npm run styleguide

# Watch for changes (in separate terminal)
npm run watch
```

## Development Workflow

### Styleguide Development
The project uses a styleguide-driven development approach where UI components are developed in isolation before integration.

```bash
# Install grunt-cli globally (one-time setup)
npm install -g grunt-cli

# Start the styleguide server
npm run styleguide
# or
node/node node_modules/bsp-styleguide/bin/styleguide

# Watch for changes (auto-reload)
npm run watch
# or 
target/bin/grunt watch

# Navigate to styleguide
open http://localhost:3000
```

### Available NPM Scripts
- `npm run build` - Full Maven build with verification
- `npm run docker` - Start Docker Compose stack
- `npm run grunt` - Run Grunt tasks
- `npm run watch` - Watch for file changes
- `npm run styleguide` - Start styleguide development server

### Java
Java 8 is required to build and deploy your app as there are Java 8 specific features being used in the source code.

http://en.wikipedia.org/wiki/Java_version_history#Java_8_updates

### Apache
Not necessary for local environment deployments.

### Tomcat
Tomcat 8 is required to successfully run the project as we are using new features of the Servlet/JSP/EL specs deployed with this version.

http://tomcat.apache.org/whichversion.html


## Project Structure

```
├── src/main/java/com/brightspot/         # Java source code
│   ├── auth/                             # Authentication system
│   ├── integration/                      # Third-party integrations (Google, Stripe)
│   ├── model/                            # Content models and data structures
│   │   ├── article/                      # Article content type
│   │   ├── blog/                         # Blog and blog post models
│   │   ├── event/                        # Event management
│   │   ├── form/                         # Dynamic form builder
│   │   ├── gallery/                      # Image galleries
│   │   ├── page/                         # Page templates and layouts
│   │   └── ...                           # Other content types
│   ├── servlet/                          # HTTP endpoints and APIs
│   ├── task/                             # Background tasks and cron jobs
│   ├── tool/                             # CMS admin interface customizations
│   └── utils/                            # Utility classes and helpers
├── src/main/webapp/                      # Web application resources
├── styleguide/                           # Frontend component definitions
│   ├── model/                            # Component JSON definitions
│   └── _config.json                      # Styleguide configuration
├── docker-compose.yml                    # Docker development environment
└── pom.xml                               # Maven build configuration
```

## Content Types

The application supports various content types out of the box:

- **Articles**: Long-form content with rich text, images, and metadata
- **Blog Posts**: Hierarchical blog content with categories and tags
- **Events**: Calendar events with date/time and location information
- **Galleries**: Image collections with slideshow functionality
- **Forms**: Dynamic forms with various input types and validation
- **Pages**: Static and dynamic page templates
- **Promotions**: Reusable promotional content blocks
- **Navigation**: Site navigation and menu structures

## Integrations

- **Google Analytics**: Traffic and behavior tracking
- **Google Tag Manager**: Marketing tag management
- **Google reCAPTCHA**: Form spam protection
- **Stripe**: Payment processing capabilities
- **Solr**: Full-text search and indexing
- **Email**: SMTP email delivery system

## Code Standards

### Java
We use the built-in Dari code styles checker, based on Sun/Oracle conventions with migration toward Google Java Style Guide.

```bash
# Run with style checking
mvn clean -P library verify
```

### Frontend
- **CSS**: LESS preprocessor with BEM methodology
- **JavaScript**: ES5+ with jQuery and custom utilities
- **Templates**: Handlebars with ViewModel pattern

## Docker Services

The development environment includes:

- **Tomcat**: Application server (port 8080)
- **MySQL**: Database server (port 3306)
- **Solr**: Search engine (port 8081)
- **Apache**: Web server with image processing (ports 80/443)
- **GreenMail**: Email testing server (multiple ports)
