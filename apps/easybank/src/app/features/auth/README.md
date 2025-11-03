# Auth Feature

Complete authentication feature with login, registration, and password reset functionality.

## Structure

```
auth/
├── pages/
│   ├── login/                     ✅ Login page
│   │   ├── login.component.ts
│   │   ├── login.component.html
│   │   └── login.component.scss
│   ├── register/                  ✅ Registration page
│   │   ├── register.component.ts
│   │   ├── register.component.html
│   │   └── register.component.scss
│   └── forgot-password/           ✅ Password reset page
│       ├── forgot-password.component.ts
│       ├── forgot-password.component.html
│       └── forgot-password.component.scss
├── services/
│   └── auth.service.ts            ✅ Authentication service
└── auth.routes.ts                 ✅ Feature routing
```

## Components

### 1. Login Component
**Route:** `/auth/login`

**Features:**
- Email and password validation
- "Remember me" checkbox
- Password visibility toggle
- Loading state during authentication
- Error message display
- Link to registration and forgot password
- Redirects to dashboard after successful login

**Form Fields:**
- Email (required, must be valid email)
- Password (required, min 6 characters)
- Remember Me (optional)

### 2. Register Component
**Route:** `/auth/register`

**Features:**
- Full user registration form
- Password strength validation
- Password confirmation matching
- Terms and conditions checkbox
- Password visibility toggles
- Loading state
- Error message display
- Link back to login

**Form Fields:**
- Full Name (required, min 2 characters)
- Email (required, valid email)
- Password (required, min 8 characters)
- Confirm Password (required, must match password)
- Agree to Terms (required)

**Validation:**
- Uses custom `matchPasswordValidator` utility
- All fields have real-time validation
- Form-level password matching validation

### 3. Forgot Password Component
**Route:** `/auth/forgot-password`

**Features:**
- Email input for password reset
- Success message after submission
- Option to try different email
- Link back to login
- Loading state
- Security message (doesn't reveal if email exists)

**Form Fields:**
- Email (required, valid email)

**Flow:**
1. User enters email
2. System sends reset instructions (if account exists)
3. Success message shown
4. Form is disabled
5. User can try again with different email

## Auth Service

The `AuthService` provides:
- `login(credentials)` - Authenticate user
- `logout()` - Clear session and redirect
- `isAuthenticated()` - Check auth status
- `currentUser` signal - Reactive current user state
- Token storage management

## Usage Example

### In Routes (app.routes.ts)
```typescript
{
  path: 'auth',
  loadChildren: () =>
    import('./features/auth/auth.routes').then(m => m.authRoutes)
}
```

### Accessing Auth Service
```typescript
import { AuthService } from '@features/auth/services/auth.service';

constructor(private auth: AuthService) {
  // Check if user is logged in
  if (this.auth.isAuthenticated()) {
    // Access current user
    console.log(this.auth.currentUser());
  }
}
```

### Protecting Routes
```typescript
import { authGuard } from '@core/guards/auth.guard';

{
  path: 'dashboard',
  canActivate: [authGuard],
  component: DashboardComponent
}
```

## Styling

All components use a consistent design:
- Purple gradient background
- White card with shadow
- Smooth transitions and hover effects
- Responsive layout
- Loading spinners
- Error/success messages with proper styling

## Form Validation

**Common Validations:**
- Email format validation
- Required fields
- Minimum length requirements
- Password matching (register)
- Terms acceptance (register)

**User Feedback:**
- Red border on invalid fields (after touch)
- Error messages below fields
- Alert boxes for general errors/success
- Disabled submit during loading

## API Integration

**TODO:** Update when backend is ready

Currently using mock/simulated API calls. To connect to backend:

1. **Login:** Update `AuthService.login()` to call actual endpoint
2. **Register:** Uncomment API call in `RegisterComponent.onSubmit()`
3. **Forgot Password:** Uncomment API call in `ForgotPasswordComponent.onSubmit()`

Expected backend endpoints (configured in `api.config.ts`):
- `POST /api/auth/login` - Login
- `POST /api/auth/register` - Register
- `POST /api/auth/forgot-password` - Request password reset

## Security Features

- Passwords are never logged or exposed
- Auth tokens stored in localStorage
- Password visibility toggles for UX
- Form validation prevents invalid submissions
- Loading states prevent double submissions
- Error messages are user-friendly (don't expose system details)

## Accessibility

- Proper label associations
- ARIA labels for icon buttons
- Keyboard navigation support
- Focus management
- Clear error messaging

## Next Steps

1. Connect to backend API endpoints
2. Add email verification flow
3. Implement password reset token validation
4. Add social login (OAuth)
5. Implement two-factor authentication
6. Add rate limiting on frontend
7. Add password strength indicator
