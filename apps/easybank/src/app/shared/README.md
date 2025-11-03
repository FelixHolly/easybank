# Shared Module

The shared module contains reusable components, directives, pipes, and utilities that are used across multiple feature modules.

## Structure

- **components/** - Reusable UI components (buttons, cards, modals, forms, etc.)
- **directives/** - Custom directives (highlight, tooltip, etc.)
- **pipes/** - Custom pipes (date format, currency, etc.)
- **models/** - Shared data models and interfaces
- **utils/** - Utility functions and helpers

## Rules

- Components here should be generic and reusable
- Import SharedModule in feature modules that need these components
- Keep components stateless when possible
- Document component inputs/outputs clearly
