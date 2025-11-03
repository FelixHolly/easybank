# Layouts

Layout components define the structure and common elements for different parts of the application.

## Current Layouts

- **main-layout/** - Main application layout (header, sidebar, footer)
- **auth-layout/** - Authentication pages layout (login, register)

## Layout Structure

Each layout typically includes:
- Header/Navigation
- Sidebar (if applicable)
- Content outlet (<router-outlet>)
- Footer

## Usage

Layouts are typically used in routing configuration:
```typescript
{
  path: '',
  component: MainLayoutComponent,
  children: [
    // Protected routes
  ]
}
```
