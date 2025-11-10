# Authorization Model - EasyBank

## Overview

EasyBank implements **Role-Based Access Control (RBAC)** with support for custom authority exceptions. This document explains the authorization architecture and best practices.

---

## Architecture

### Three-Layer Authorization Model

```
┌────────────────────────────────────────────────────┐
│                  User Authentication               │
│                         ↓                          │
│  ┌──────────────────────────────────────────────┐  │
│  │ 1. Roles (High-Level Access)                 │  │
│  │    - Stored in: customer_roles table         │  │
│  │    - Examples: USER, ADMIN, MANAGER, SUPPORT │  │
│  └──────────────────────────────────────────────┘  │
│                         ↓                          │
│  ┌──────────────────────────────────────────────┐  │
│  │ 2. Role-Based Authorities (Implicit)         │  │
│  │    - Defined in: AuthorityService.java       │  │
│  │    - Computed at runtime                     │  │
│  │    - Examples: ACCOUNT:READ, CARD:WRITE      │  │
│  └──────────────────────────────────────────────┘  │
│                         ↓                          │
│  ┌──────────────────────────────────────────────┐  │
│  │ 3. Custom Authorities (Exceptions/Additions) │  │
│  │    - Stored in: customer_authorities table   │  │
│  │    - Use cases: Special permissions,         │  │
│  │                 Temporary access, Testing    │  │
│  └──────────────────────────────────────────────┘  │
│                         ↓                          │
│           Final GrantedAuthorities List            │
│        (Roles + Role-Based + Custom)               │
└────────────────────────────────────────────────────┘
```

---

## Role Definitions

### USER
**Purpose**: Standard customer access
**Default Authorities**:
- `ACCOUNT:READ` - View own account details
- `TRANSACTION:READ` - View transaction history
- `CARD:READ` - View card information
- `LOAN:READ` - View loan details
- `CONTACT:WRITE` - Submit support requests
- `NOTICE:READ` - View system notices

### MANAGER
**Purpose**: Branch/Department management
**Default Authorities**: All USER authorities PLUS:
- `ACCOUNT:WRITE` - Modify account details
- `TRANSACTION:WRITE` - Create transactions
- `TRANSACTION:APPROVE` - Approve transactions
- `CARD:WRITE` - Issue/modify cards
- `CARD:ACTIVATE`, `CARD:BLOCK` - Card management
- `LOAN:WRITE`, `LOAN:APPROVE` - Loan operations
- `USER:READ` - View customer profiles
- `CONTACT:READ` - View support tickets
- `NOTICE:WRITE` - Create notices
- `REPORT:GENERATE` - Generate reports

### SUPPORT
**Purpose**: Customer service operations
**Default Authorities**: All USER authorities PLUS:
- `CARD:ACTIVATE`, `CARD:BLOCK` - Card assistance
- `CONTACT:READ`, `CONTACT:WRITE` - Support ticket management
- `USER:READ` - View customer information

### ADMIN
**Purpose**: System administration
**Default Authorities**: ALL authorities including:
- `ACCOUNT:DELETE` - Delete accounts
- `USER:WRITE`, `USER:DELETE` - User management
- `REPORT:EXPORT` - Export sensitive data

---

## How It Works

### 1. User Login
```java
// EasyBankUserDetailService.loadUserByUsername()
Customer customer = customerRepository.findByEmail(email);

// Step 1: Load roles from database
Set<Role> roles = customer.getRoles();
// Example: [USER, MANAGER]

// Step 2: Compute role-based authorities
Set<Authority> roleAuthorities = authorityService.getDefaultAuthoritiesForRoles(roles);
// Example: [ACCOUNT:READ, ACCOUNT:WRITE, TRANSACTION:READ, ...]

// Step 3: Load custom authorities from database
Set<Authority> customAuthorities = customer.getAuthorities();
// Example: [REPORT:GENERATE] (special permission)

// Step 4: Combine all (deduplicated via Set)
Set<GrantedAuthority> finalAuthorities =
    roles + roleAuthorities + customAuthorities;
```

### 2. Endpoint Protection
```java
@PreAuthorize("hasAuthority('ACCOUNT:READ')")
public Account getAccount() { ... }

@PreAuthorize("hasAuthority('TRANSACTION:APPROVE')")
public void approveTransaction() { ... }

@PreAuthorize("hasRole('ADMIN')")
public void adminOperation() { ... }
```

---

## Database Schema

### customer_roles
```sql
customer_id | role
------------|--------
1           | USER
4           | USER
4           | MANAGER
5           | ADMIN
```
**Purpose**: Store user roles

### customer_authorities
```sql
customer_id | authority
------------|------------------
1           | REPORT:GENERATE
3           | CARD:ACTIVATE
```
**Purpose**: Store ONLY custom/exception authorities

**Key Point**: This table should be mostly EMPTY! Role-based authorities are computed, not stored.

---

## Best Practices

### ✅ DO

1. **Assign Roles, Not Individual Authorities**
   ```java
   // Good
   customer.setRoles(Set.of(Role.MANAGER));
   // Authorities computed automatically
   ```

2. **Use Custom Authorities Sparingly**
   ```sql
   -- Only for exceptions
   INSERT INTO customer_authorities VALUES (1, 'REPORT:GENERATE');
   ```

3. **Centralize Authority Definitions**
   - All role→authority mappings in `AuthorityService.java`
   - Single source of truth

4. **Use Descriptive Authority Names**
   - Format: `RESOURCE:ACTION`
   - Examples: `ACCOUNT:READ`, `LOAN:APPROVE`

### ❌ DON'T

1. **Don't Store Role-Based Authorities in Database**
   ```sql
   -- Bad: Redundant, hard to maintain
   INSERT INTO customer_authorities VALUES (1, 'ACCOUNT:READ');
   INSERT INTO customer_authorities VALUES (1, 'TRANSACTION:READ');
   -- These come from USER role automatically!
   ```

2. **Don't Mix Concerns**
   ```java
   // Bad: Checking authority AND role
   @PreAuthorize("hasRole('ADMIN') and hasAuthority('USER:DELETE')")

   // Good: ADMIN role already includes USER:DELETE
   @PreAuthorize("hasAuthority('USER:DELETE')")
   ```

3. **Don't Hardcode Authorities**
   ```java
   // Bad
   if (user.getAuthorities().contains("ACCOUNT:READ")) { ... }

   // Good: Use Spring Security
   @PreAuthorize("hasAuthority('ACCOUNT:READ')")
   ```

---

## Use Cases

### Scenario 1: New USER Registration
```java
// Controller
customer.setRoles(Set.of(Role.USER));
// NO need to set authorities - they're computed!

// At login: User gets ACCOUNT:READ, TRANSACTION:READ, etc.
```

### Scenario 2: Temporary Permission Grant
```sql
-- Alice (USER) needs temporary report access for audit
INSERT INTO customer_authorities VALUES (1, 'REPORT:GENERATE');

-- At login: Alice gets USER authorities + REPORT:GENERATE
```

### Scenario 3: Promote to Manager
```sql
-- Add MANAGER role
INSERT INTO customer_roles VALUES (4, 'MANAGER');

-- At next login: Diana gets USER + MANAGER authorities automatically
-- No need to update customer_authorities table!
```

### Scenario 4: Custom Permission Before Role Change
```sql
-- Charlie is testing manager features before promotion
INSERT INTO customer_authorities VALUES (3, 'TRANSACTION:APPROVE');

-- Charlie keeps USER role but can approve transactions
```

---

## Testing Authorization

### Check User Authorities
```java
Authentication auth = SecurityContextHolder.getContext().getAuthentication();
Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();

// Should see:
// [ROLE_USER, ACCOUNT:READ, TRANSACTION:READ, ...]
```

### Test Endpoint Protection
```bash
# As USER (should succeed)
curl -u alice@example.com:password1 http://localhost:8080/myAccount?id=1

# As USER trying admin operation (should fail with 403)
curl -u alice@example.com:password1 -X DELETE http://localhost:8080/admin/users/1
```

---

## Migration Guide

If you have existing individual authority assignments:

1. **Identify role-based authorities**
   - Compare stored authorities with AuthorityService definitions

2. **Delete redundant entries**
   ```sql
   -- Keep only custom authorities
   DELETE FROM customer_authorities
   WHERE (customer_id, authority) IN (
     SELECT c.customer_id, a.authority
     FROM customers c
     JOIN customer_roles cr ON c.customer_id = cr.customer_id
     JOIN role_authorities ra ON cr.role = ra.role
   );
   ```

3. **Restart application**
   - Authorities will be computed from roles

---

## Performance Considerations

### Why This Model is Efficient

1. **Reduced Database Storage**
   - Before: 1000 users × 6 authorities = 6000 rows
   - After: 1000 users × 0 authorities = 0 rows (for standard users)

2. **Faster Queries**
   - No need to join customer_authorities for most users
   - Authorities computed in-memory

3. **Centralized Updates**
   - Changing USER permissions: Update 1 line in AuthorityService
   - Before: Update 1000 database rows

---

## Common Questions

**Q: When should I store an authority in customer_authorities?**
A: Only when it's an exception to role defaults (temporary access, special permission, etc.)

**Q: Can a user have multiple roles?**
A: Yes! Authorities from all roles are combined. Example: USER + MANAGER

**Q: How do I revoke a role-based authority?**
A: This model doesn't support negative authorities. Either:
- Create a new role with fewer authorities, or
- Implement negative authority system (future enhancement)

**Q: Are authorities case-sensitive?**
A: Yes. Use uppercase consistently: `ACCOUNT:READ` not `account:read`

---

## References

- Spring Security Method Security: https://docs.spring.io/spring-security/reference/servlet/authorization/method-security.html
- RBAC Best Practices: https://en.wikipedia.org/wiki/Role-based_access_control
- Code Locations:
  - `AuthorityService.java` - Role→Authority mappings
  - `EasyBankUserDetailService.java` - Authority resolution
  - `Authority.java` - Authority enum definitions
  - `Role.java` - Role enum definitions
