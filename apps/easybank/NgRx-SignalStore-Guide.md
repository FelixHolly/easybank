# NgRx SignalStore Guide for EasyBank

## Why NgRx SignalStore?

We've migrated from custom `createUiState()` to **NgRx SignalStore** for these benefits:

### ✅ Advantages
1. **Official Solution** - Maintained by the NgRx team, battle-tested
2. **DevTools Integration** - Time-travel debugging and state inspection
3. **Better Composition** - Easily combine features with `withMethods`, `withComputed`, `withEntities`
4. **RxJS Integration** - `rxMethod` for reactive streams
5. **Type Safety** - Excellent TypeScript inference
6. **Scalability** - Share state across components or keep it local
7. **Modern** - Built for Angular signals from the ground up

## Architecture

```
core/
  store/
    ui-state.store.ts       # Reusable UI state feature
features/
  account/
    store/
      account.store.ts      # Account-specific store
    pages/
      account.component.ts  # Component using the store
```

## Basic Pattern: withUiState Feature

### 1. Create a Store with UI State

```typescript
import { signalStore, withMethods } from '@ngrx/signals';
import { withUiState } from '../../../core/store/ui-state.store';
import { rxMethod } from '@ngrx/signals/rxjs-interop';

export const AccountStore = signalStore(
  { providedIn: 'root' }, // Injectable at root level

  // Add UI state management (loading, error, success, data)
  withUiState<Account>(),

  // Add custom methods
  withMethods((store) => {
    const apiService = inject(ApiService);

    return {
      loadAccount: rxMethod<void>(
        pipe(
          tap(() => patchState(store, { loading: true, error: null })),
          switchMap(() =>
            apiService.get<Account>('/account').pipe(
              tap({
                next: (account) => patchState(store, {
                  data: account,
                  loading: false,
                  success: true
                }),
                error: () => patchState(store, {
                  loading: false,
                  error: 'Failed to load'
                })
              })
            )
          )
        )
      )
    };
  })
);
```

### 2. Use in Component

```typescript
export class AccountComponent {
  private accountStore = inject(AccountStore);

  // Expose signals
  readonly account = this.accountStore.data;
  readonly loading = this.accountStore.loading;
  readonly error = this.accountStore.error;

  ngOnInit() {
    this.accountStore.loadAccount(); // Trigger loading
  }
}
```

### 3. Use in Template

```html
@if (loading()) {
  <div>Loading...</div>
} @else if (error()) {
  <div class="error">{{ error() }}</div>
} @else if (account(); as acc) {
  <div>{{ acc.name }}</div>
}
```

## Advanced Patterns

### Local Component State

For component-local state that doesn't need to be shared, create a local store with `withUiState()`:

```typescript
// Create a local store
const LocalDataStore = signalStore(
  withUiState<MyData>()
);

export class MyComponent {
  // Inject the local store instance
  private localStore = inject(LocalDataStore);

  readonly data = this.localStore.data;
  readonly loading = this.localStore.loading;

  loadData() {
    this.localStore.setLoading();
    // ... fetch data
    this.localStore.setSuccess(data);
  }
}
```

### List Management with Entities

For managing collections (like a list of transactions):

```typescript
import { withEntities, addEntity, updateEntity } from '@ngrx/signals/entities';

export const TransactionsStore = signalStore(
  { providedIn: 'root' },
  withEntities<Transaction>(),
  withUiState<Transaction[]>(),
  withMethods((store) => ({
    loadTransactions: rxMethod<void>(
      pipe(/* ... load and set entities ... */)
    ),
    addTransaction: (transaction: Transaction) => {
      patchState(store, addEntity(transaction));
    }
  }))
);
```

### Computed Values

Add derived state that automatically updates:

```typescript
export const CartStore = signalStore(
  { providedIn: 'root' },
  withState({ items: [] as CartItem[] }),
  withComputed(({ items }) => ({
    total: computed(() =>
      items().reduce((sum, item) => sum + item.price * item.quantity, 0)
    ),
    itemCount: computed(() => items().length)
  }))
);
```

### Effects for Side Effects

Use `rxMethod` for reactive side effects:

```typescript
withMethods((store) => {
  const notificationService = inject(NotificationService);

  return {
    // Reactive method that responds to signals
    autoSave: rxMethod<void>(
      pipe(
        debounceTime(1000),
        switchMap(() => apiService.save(store.data()))
      )
    )
  };
})
```

## Migration from Custom createUiState

### Before (Custom)
```typescript
const accountState = createUiState<Account>();

accountState.setLoading();
apiService.get('/account').subscribe({
  next: (data) => accountState.setSuccess(data),
  error: () => accountState.setError('Failed')
});
```

### After (NgRx SignalStore)
```typescript
const accountStore = inject(AccountStore);

accountStore.loadAccount(); // All logic in the store
```

## Best Practices

### 1. **Keep Logic in Stores**
- ✅ Business logic in stores
- ❌ Business logic in components

### 2. **Use rxMethod for Async Operations**
```typescript
// ✅ Good - Reactive and testable
loadData: rxMethod<void>(
  pipe(
    tap(() => patchState(store, { loading: true })),
    switchMap(() => apiService.get('/data'))
  )
)

// ❌ Avoid - Harder to test
loadData(){
  this.loading = true;
  apiService.get('/data').subscribe(/* ... */);
}
```

### 3. **Compose Features**
```typescript
export const FeatureStore = signalStore(
  { providedIn: 'root' },
  withUiState<Data>(),           // UI state
  withDevtools('feature'),       // DevTools
  withRequestStatus(),           // Request tracking
  withEntities<Item>(),          // Entity management
  withMethods(/* ... */)         // Custom logic
);
```

### 4. **Type Safety First**
```typescript
// ✅ Type-safe
export interface Account {
  id: number;
  name: string;
}

withUiState<Account>()

// ❌ Avoid any
withUiState<any>()
```

### 5. **Use Computed for Derived State**
```typescript
// ✅ Computed values update automatically
withComputed(({ data }) => ({
  isEmpty: computed(() => data() === null),
  displayName: computed(() => data()?.name ?? 'Unknown')
}))
```

## Testing

```typescript
describe('AccountStore', () => {
  it('should load account data', () => {
    const store = new AccountStore();

    store.loadAccount();

    expect(store.loading()).toBe(true);
    // ... test assertions
  });
});
```

## Real-World Example: Complete Store

```typescript
export const ProductsStore = signalStore(
  { providedIn: 'root' },

  // State
  withUiState<Product[]>(),
  withState({ selectedId: null as number | null }),

  // Computed
  withComputed(({ data, selectedId }) => ({
    selectedProduct: computed(() =>
      data()?.find(p => p.id === selectedId())
    ),
    productCount: computed(() => data()?.length ?? 0)
  })),

  // Methods
  withMethods((store, apiService = inject(ApiService)) => ({
    loadProducts: rxMethod<void>(
      pipe(
        tap(() => patchState(store, { loading: true })),
        switchMap(() =>
          apiService.get<Product[]>('/products').pipe(
            tap({
              next: (products) => patchState(store, {
                data: products,
                loading: false,
                success: true
              }),
              error: () => patchState(store, {
                loading: false,
                error: 'Failed to load products'
              })
            })
          )
        )
      )
    ),

    selectProduct: (id: number) => {
      patchState(store, { selectedId: id });
    },

    clearSelection: () => {
      patchState(store, { selectedId: null });
    }
  }))
);
```

## Resources

- [NgRx SignalStore Docs](https://ngrx.io/guide/signals/signal-store)
- [NgRx SignalStore Features](https://ngrx.io/guide/signals/signal-store/features)
- [rxMethod Documentation](https://ngrx.io/guide/signals/rxjs-integration)

## Summary

NgRx SignalStore provides:
- ✅ Production-ready state management
- ✅ Excellent developer experience
- ✅ Built for Angular signals
- ✅ Scales from simple to complex
- ✅ Full TypeScript support
- ✅ DevTools integration

Use it for all state management needs in EasyBank!
