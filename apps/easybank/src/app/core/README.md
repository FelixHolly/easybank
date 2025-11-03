# Core Module

The core module contains singleton services, guards, interceptors, and core models that are used application-wide.

## Structure

- **services/** - Application-wide singleton services (auth, api, storage, etc.)
- **guards/** - Route guards (auth guard, role guard, etc.)
- **interceptors/** - HTTP interceptors (auth token, error handling, logging)
- **models/** - Core data models and interfaces used across the app

## Rules

- Core module should be imported ONLY ONCE in the AppModule/AppConfig
- All services in this module should be provided in root
- Do not import CoreModule in feature modules
