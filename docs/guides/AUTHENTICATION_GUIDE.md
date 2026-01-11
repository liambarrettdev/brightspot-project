# Auth Package

## Overview

The auth package provides a comprehensive authentication and authorization framework for the Brightspot CMS. It supports user registration, login, logout, session management, and token-based email verification. The package is designed to be extensible, allowing custom authentication methods to be implemented.

## Architecture

The authentication system consists of several key components:

### Core Components

1. **AbstractAuthenticator** - Base class for implementing custom authenticators
2. **PasswordAuthenticator** - Default password-based authentication implementation
3. **Session** - User session management and tracking
4. **Token** - Token-based email verification for registration
5. **AuthenticationFilter** - Servlet filter for authentication enforcement
6. **AuthenticationSettings** - Configuration for authentication behavior
7. **AuthenticationUser** - Interface for authenticatable users
8. **AuthenticationException** - Custom exception for authentication errors

### Authentication Process

The authentication process follows these steps:

1. **Request Filtering**: `AuthenticationFilter` intercepts requests and checks for authenticated sessions
2. **Session Validation**: Validates authentication cookies and retrieves session from database
3. **User Authentication**: Authenticator validates credentials and creates session
4. **Session Creation**: Session created with expiration date and stored in database
5. **Cookie/Header Setting**: Authentication cookie and header set in response
6. **Token Verification**: Registration tokens validated for email verification
7. **Session Cleanup**: Expired sessions automatically purged by background task

## AbstractAuthenticator

The `AbstractAuthenticator` class provides a framework for implementing custom authenticators. It extends `Record` and defines the contract for authentication methods.

### Key Methods to Override

When creating a custom authenticator, you must implement:

1. **`getCurrentSession(HttpServletRequest request)`** - Retrieves the current user session from the request
2. **`login(HttpServletRequest request, HttpServletResponse response)`** - Authenticates a user and creates a session
3. **`logout(HttpServletRequest request, HttpServletResponse response)`** - Terminates a user session

### Configuration Properties

#### Required Fields

- **from** (String): Email address for registration emails
- **subject** (String): Subject line for registration emails
- **template** (String): Rich text template for registration emails with `${link}` placeholder

### Base Functionality

The base class automatically handles:

- **Session Creation**: `addUserSession()` method creates sessions and sets cookies/headers
- **Cookie Management**: Manages signed authentication cookies (`bsp.auth`)
- **Token Headers**: Sets authentication tokens in response headers (`Native-Authorization`)
- **Email Template Processing**: Provides template with `${link}` placeholder substitution

### Constants

- `COOKIE_NAME` - Authentication cookie name: `"bsp.auth"`
- `KEY_LINK` - Template placeholder for registration links: `"${link}"`
- `AUTH_TOKEN_NAME` - Token cookie name: `"auth_token"`
- `AUTH_HEADER_NAME` - Response header name: `"Native-Authorization"`
- `AUTH_BEARER_TYPE` - Bearer token type: `"Bearer"`
- `DEFAULT_MAX_AGE` - Default cookie max age: `86400` (24 hours)

### Static Methods

- **`addUserSession(HttpServletRequest request, HttpServletResponse response, AuthenticationUser user)`** - Creates a session for a user and sets authentication cookies/headers

## PasswordAuthenticator

The `PasswordAuthenticator` class is the default implementation of `AbstractAuthenticator` that provides email/password-based authentication.

### Configuration Properties

#### Required Fields

- **name** (String): Display name for the authenticator (defaults to "Default")

### Authentication Flow

1. Validates email and password parameters from request
2. Queries user by email (site-scoped if applicable)
3. Validates user status (must be VERIFIED, not BLOCKED or null)
4. Verifies password using `PasswordUser.Data`
5. Creates session and sets authentication cookies/headers

### Request Parameters

- `email` - User email address
- `password` - User password

### Error Handling

Throws `AuthenticationException` for:
- Missing credentials
- User not found
- Pending verification status
- Blocked user status
- Missing password (requires reset)
- Invalid password

## Session

The `Session` class represents an active user authentication session. Sessions are stored in the database and tracked via cookies.

### Properties

- **user** (AuthenticationUser): The authenticated user (indexed, required)
- **start** (Date): Session start date (indexed, required)
- **end** (Date): Session end date (indexed, optional)

### Session Lifecycle

- **Creation**: Sessions are created with a start date and end date (1 month from creation)
- **Validation**: Sessions are considered valid if `end` is missing or in the future
- **Expiration**: Expired sessions are automatically filtered out

### Static Methods

- **`createSession(AuthenticationUser user)`** - Creates a new session for a user
- **`findSession(String id)`** - Finds a session by ID (only if not expired)
- **`findSession(AuthenticationUser user)`** - Finds an active session for a user

## Token

The `Token` class represents a temporary authentication token used for email verification during user registration. Tokens implement the `Expirable` interface for automatic cleanup.

### Properties

- **user** (AuthenticationUser): The user associated with the token (required)
- **expiryDate** (Date): Token expiration date (required, default: 7 days)

### Features

- **Automatic Expiration**: Tokens expire after 7 days
- **Auto-Deletion**: Expired tokens are automatically deleted (`shouldDeleteAfterExpiry()` returns `true`)
- **User Verification**: Used to verify user email addresses during registration

### Static Methods

- **`createToken(AuthenticationUser user)`** - Creates a new token for a user with 7-day expiration

## AuthenticationFilter

The `AuthenticationFilter` class is a servlet filter that enforces authentication on requests. It runs before `PageFilter` and checks for authenticated sessions.

### Request Attributes

- `auth.session` - Cached `Session` object
- `auth.entity` - Cached `AuthenticationUser` object

### Static Methods

- **`getAuthenticationSettings(HttpServletRequest request)`** - Gets authentication settings for the current site
- **`getAuthenticatedSession(HttpServletRequest request)`** - Gets the authenticated session (with error handling)
- **`getAuthenticatedUser(HttpServletRequest request)`** - Gets the authenticated user
- **`noAuthenticationRequired(String requestUri)`** - Checks if authentication is not required for a URI

### Excluded Paths

Authentication is not required for:
- `/_*` - Internal API paths
- `/cms*` - CMS admin paths
- `/_auth/login` - Login endpoint
- `/_auth/logout` - Logout endpoint

## AuthenticationSettings

The `AuthenticationSettings` class is an embedded record that configures authentication behavior for a site.

### Properties

- **authenticator** (AbstractAuthenticator): The authenticator to use (required)
- **authenticatedLandingPage** (AbstractPage): Page to redirect to after successful login
- **unauthenticatedLandingPage** (AbstractPage): Page to redirect to after logout

## AuthenticationUser

The `AuthenticationUser` interface is a marker interface that identifies objects that can be authenticated. Any `Recordable` object can implement this interface to participate in the authentication system.

## AuthenticationException

The `AuthenticationException` class extends `ServletException` and is thrown when authentication operations fail. It provides detailed error messages for debugging and user feedback.

## Servlets

### LoginServlet

Handles user login requests at `/_auth/login`.

**Features:**
- POST only (returns 404 for GET requests)
- Retrieves authenticator from site settings
- Creates session via authenticator
- Redirects to authenticated landing page on success
- Returns 401 on authentication failure

### LogoutServlet

Handles user logout requests at `/_auth/logout`.

**Features:**
- Accepts both GET and POST requests
- Terminates session via authenticator
- Clears authentication cookies
- Redirects to unauthenticated landing page or referer

### RegistrationServlet

Handles user registration and email verification at `/_auth/register`.

**Registration Flow (POST):**
1. Validates email and password parameters
2. Checks if user already exists
3. Creates new user with `PENDING` status
4. Sets password using `PasswordUser.Data`
5. Optionally sets first name and last name
6. Creates verification token (7-day expiration)
7. Sends registration email with verification link
8. Redirects to home page

**Verification Flow (GET):**
1. Validates token parameter
2. Looks up token in database
3. Verifies token is valid and not expired
4. Updates user status to `VERIFIED`
5. Deletes token
6. Creates session and logs user in
7. Redirects to authenticated landing page

**Request Parameters:**
- `email` - User email (POST, required)
- `password` - User password (POST, required)
- `forename` - User first name (POST, optional)
- `surname` - User last name (POST, optional)
- `token` - Verification token (GET, required)

**Email Template Processing:**
- Replaces `${link}` placeholder with verification URL
- Converts rich text to plain HTML
- Uses authenticator's configured from address and subject

## AuthenticationSiteSettings

The `AuthenticationSiteSettings` class is a site modification that stores authentication settings per site.

### Features

- **Site-Scoped**: Each site can have its own authentication configuration
- **Tab Organization**: Settings appear in "Authentication" tab
- **Embedded Settings**: Uses `AuthenticationSettings` embedded record

### Static Methods

- **`get(Site site, Function<AuthenticationSiteSettings, T> getter)`** - Gets a value from site settings

## SessionPurgeTask

The `SessionPurgeTask` class is a cron task that purges expired sessions from the database.

### Features

- **Automatic Cleanup**: Removes sessions where `end < current date`
- **Cron-Based**: Runs on a configurable schedule
- **Mutually Exclusive**: Ensures only one instance runs at a time
- **Force Run**: Supports manual execution via `runTask()`

## User Status Management

Users have a `Status` enum with the following values:

- **PENDING** - User registered but not yet verified (email verification required)
- **VERIFIED** - User verified and can log in
- **BLOCKED** - User blocked and cannot log in

## Creating a Custom Authenticator

### Step 1: Create the Authenticator Class

```java
public class MyCustomAuthenticator extends AbstractAuthenticator {
    
    @Recordable.Required
    @ToolUi.DisplayFirst
    private String name;
    
    @Override
    public String getLabel() {
        return "My Custom Authentication";
    }
    
    @Override
    public Session getCurrentSession(HttpServletRequest request) throws AuthenticationException {
        // Retrieve session from request (cookie, header, etc.)
        String sessionId = JspUtils.getSignedCookie(request, COOKIE_NAME);
        if (StringUtils.isNotBlank(sessionId)) {
            return Session.findSession(sessionId);
        }
        return null;
    }
    
    @Override
    public Session login(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        // Extract credentials from request
        String credential = request.getParameter("credential");
        
        // Validate credentials
        AuthenticationUser user = validateCredentials(credential);
        if (user == null) {
            throw new AuthenticationException("Invalid credentials");
        }
        
        // Create session
        return addUserSession(request, response, user);
    }
    
    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        CookieUtils.setCookie(request, response, COOKIE_NAME, "", 0);
        
        Session session = getCurrentSession(request);
        if (session != null) {
            session.setEnd(new Date());
            session.saveImmediately();
        }
    }
    
    private AuthenticationUser validateCredentials(String credential) {
        // Custom validation logic
        return null;
    }
}
```

### Step 2: Configure Email Templates

In the authenticator configuration:
1. Set `from` email address for registration emails
2. Set `subject` for registration emails
3. Set `template` with `${link}` placeholder for verification links

### Step 3: Register the Authenticator

Add the authenticator instance to the site's `AuthenticationSettings`:
1. Navigate to site settings
2. Open the "Authentication" tab
3. Select your custom authenticator
4. Configure landing pages if needed

## Best Practices

1. **Always use `Password.create()`** - Use Dari's `Password` class for password hashing, never store plain text passwords
2. **Set appropriate session expiration** - Balance security with user experience (default: 1 month)
3. **Provide clear error messages** - Give users helpful feedback without revealing sensitive information
4. **Use reasonable token expiration** - Default 7 days is appropriate for email verification
5. **Include clear instructions in emails** - Registration emails should clearly explain the verification process
6. **Consider site scoping** - Use site-scoped user queries for multi-site installations
7. **Manage user status properly** - Ensure proper transitions between PENDING, VERIFIED, and BLOCKED statuses
8. **Clean up expired sessions** - Ensure `SessionPurgeTask` is configured and running
9. **Secure cookie configuration** - Use HttpOnly and Secure flags in production
10. **Validate all inputs** - Always validate and sanitize user inputs before processing

## Error Handling

The authentication system uses `AuthenticationException` for authentication-specific errors. Common error scenarios:

- **Missing credentials**: Check that required parameters are provided
- **Invalid credentials**: Verify user exists and password is correct
- **User status issues**: Ensure user is VERIFIED and not BLOCKED
- **Session expiration**: Check that session has not expired
- **Token expiration**: Verify token is still valid (7-day expiration)
- **Configuration errors**: Ensure authenticator is configured in site settings

All errors are logged with context information to aid in debugging.

## Security Considerations

### Cookie Security

- **Signed Cookies**: Authentication cookies are signed using `JspUtils.signCookie()` to prevent tampering
- **HttpOnly**: Cookies should be configured as HttpOnly to prevent XSS attacks
- **Secure**: Cookies should be configured as Secure in production (HTTPS only)
- **Max Age**: Default 24-hour expiration balances security with user experience

### Token Security

- **Time-Limited**: Tokens expire after 7 days
- **Single Use**: Tokens are deleted after use
- **User-Scoped**: Tokens are tied to specific users
- **Database Storage**: Tokens stored in database for validation

### Password Security

- **Hashed Storage**: Passwords are stored using Dari's `Password` class (hashed, not plain text)
- **No Plain Text**: Passwords are never stored or transmitted in plain text
- **Password Verification**: Uses secure password checking via `Password.check()`

### Session Security

- **Expiration**: Sessions expire after 1 month
- **Database Storage**: Sessions stored in database for validation
- **Automatic Cleanup**: Expired sessions purged by background task
- **Indexed Queries**: User, start, and end dates are indexed for performance

## Troubleshooting

### Login Issues

- Verify authenticator is configured in site settings
- Check that user exists and email is correct
- Ensure user status is VERIFIED (not PENDING or BLOCKED)
- Verify password is correct using `Password.check()`
- Review logs for specific authentication errors

### Registration Issues

- Verify email template is configured with `${link}` placeholder
- Check that email service is configured and working
- Ensure token is not expired (7-day limit)
- Verify user doesn't already exist with that email
- Review registration servlet logs for errors

### Session Issues

- Check that session hasn't expired (1 month default)
- Verify authentication cookie is being set correctly
- Ensure `SessionPurgeTask` is not deleting active sessions
- Check that cookie signing is working correctly
- Review filter logs for session retrieval errors

### Token Issues

- Verify token hasn't expired (7-day limit)
- Check that token exists in database
- Ensure token is associated with correct user
- Verify token deletion after use
- Review registration flow logs

## Additional Resources

- See `PasswordAuthenticator` implementation for a complete example
- Review servlet implementations (`LoginServlet`, `LogoutServlet`, `RegistrationServlet`) for request handling patterns
- Check `AuthenticationFilter` for session validation logic
- Review Brightspot CMS authentication framework documentation
- Check email service configuration for registration email delivery
