# Features Module

Each feature represents a distinct business domain or functional area of the application.

## Current Features

- **auth/** - Authentication (login, register, forgot password)
- **account/** - User account management
- **loans/** - Loan management and viewing
- **cards/** - Card management
- **balance/** - Balance checking and history
- **contact/** - Contact form and information
- **notices/** - System notices and announcements

## Feature Module Structure

Each feature module should follow this structure:
```
feature-name/
├── components/          # Feature-specific components
├── services/           # Feature-specific services
├── models/             # Feature-specific models
├── feature-name.routes.ts  # Feature routes
└── pages/              # Smart/container components
    ├── feature-list/
    ├── feature-detail/
    └── feature-form/
```

## Rules

- Features should be lazy-loaded when possible
- Keep features independent and loosely coupled
- Feature modules can import SharedModule
- Features should not import other features directly
