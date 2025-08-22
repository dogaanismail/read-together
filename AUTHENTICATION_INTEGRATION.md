# Authentication Integration Guide

This document explains the completed authentication integration between the frontend and backend systems.

## Overview

The authentication system has been fully integrated with secure token storage, proper API endpoints, and a seamless user experience. The implementation follows security best practices for token management and provides a complete authentication flow.

## Key Features

### 🔐 Secure Token Storage
- **Access Tokens**: Stored in browser memory (React state/context) to prevent XSS attacks
- **Refresh Tokens**: Stored in HttpOnly cookies for persistent authentication
- **Automatic Refresh**: Tokens are automatically refreshed with a 5-minute buffer before expiration
- **Race Condition Protection**: Multiple simultaneous refresh requests are handled safely

### 📡 API Integration
All authentication endpoints are properly integrated:
- `POST /api/v1/users/login` - User login
- `POST /api/v1/users/register` - User registration  
- `POST /api/v1/users/logout` - User logout
- `GET /api/v1/users/current-user` - Get current user data
- `PUT /api/v1/users/profile` - Update user profile
- `POST /api/v1/users/profile/picture` - Upload profile picture
- `POST /api/v1/users/forgot-password` - Send password reset email

### 🎨 Frontend Pages
All authentication pages are fully functional:
- **Login Page**: Real authentication with proper error handling
- **Register Page**: Complete user registration flow
- **Forgot Password**: Integrated with notification service
- **Profile Page**: Displays real user data with loading states
- **Edit Profile**: Functional profile updates with file upload

### 🛡️ Security Features
- Authentication required for protected endpoints
- Input validation with meaningful error messages
- File upload validation (5MB limit, image types only)
- Secure token management prevents common vulnerabilities
- Email-based password reset functionality

## Usage

### Running the Application

1. **Start the Backend**:
   ```bash
   ./gradlew :read-together-backend:bootRun
   ```

2. **Start the Frontend**:
   ```bash
   cd read-together-client
   npm install  # First time only
   npm run dev
   ```

3. **Access the Application**:
   - Frontend: http://localhost:8080
   - Backend API: http://localhost:5006

### Authentication Flow

1. **User Registration**:
   - Visit `/register`
   - Fill in required information
   - User account is created in the backend
   - Redirected to login page

2. **User Login**:
   - Visit `/login`
   - Enter credentials
   - Access token stored in memory
   - Refresh token stored in HttpOnly cookie
   - Redirected to dashboard

3. **Profile Management**:
   - Visit `/profile` to view profile
   - Click "Edit Profile" to update information
   - Upload profile pictures
   - Changes are saved to backend

4. **Forgot Password**:
   - Visit `/forgot-password`
   - Enter email address
   - Password reset email sent via notification service

### Token Management

The authentication system automatically handles:
- Token expiration checking
- Automatic token refresh
- Secure token storage
- API request authentication
- Logout token cleanup

## Development Notes

### Frontend Architecture
- **AuthContext**: Manages global authentication state
- **AuthService**: Handles authentication logic and API calls
- **TokenStorage**: Secure token management utility
- **UserApiService**: Backend API integration

### Backend Architecture
- **UserController**: Authentication and profile endpoints
- **UserService**: Business logic for user operations
- **NotificationProviderService**: Email notifications including password reset
- **DTOs**: Request/response validation models

### Error Handling
- Network errors are handled gracefully
- User-friendly error messages
- Automatic retry for failed requests
- Proper loading states throughout the UI

## Security Considerations

1. **XSS Prevention**: Access tokens in memory cannot be stolen by malicious scripts
2. **CSRF Protection**: HttpOnly cookies with proper SameSite settings
3. **Token Expiration**: Short-lived access tokens with automatic refresh
4. **Input Validation**: All user inputs are validated on both frontend and backend
5. **File Upload Security**: Validation of file types and sizes

## Future Enhancements

- Password reset token generation and validation
- Two-factor authentication support
- Session management and device tracking
- Rate limiting for authentication endpoints
- OAuth integration for social login

## Testing

The integration has been tested for:
- ✅ Authentication flow (login/logout/register)
- ✅ Token storage and refresh
- ✅ Profile management
- ✅ Forgot password functionality
- ✅ Error handling and validation
- ✅ Security best practices

For manual testing, use the provided test script:
```bash
./test-integration.sh
```