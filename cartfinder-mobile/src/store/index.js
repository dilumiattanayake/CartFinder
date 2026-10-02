import { configureStore } from '@reduxjs/toolkit';
import { persistStore, persistReducer, FLUSH, REHYDRATE, PAUSE, PERSIST, PURGE, REGISTER } from 'redux-persist';
import AsyncStorage from '@react-native-async-storage/async-storage';
import { combineReducers } from 'redux';

import authReducer from './slices/authSlice';
import vendorReducer from './slices/vendorSlice';
import mapReducer from './slices/mapSlice';
import menuReducer from './slices/menuSlice';

const rootReducer = combineReducers({
  auth:   authReducer,
  vendor: vendorReducer,
  map:    mapReducer,
  menu:   menuReducer,
});

const persistConfig = {
  key:     'root',
  version: 1,
  storage: AsyncStorage,
  whitelist: ['auth'],  // Only persist auth state; map/vendor data is always fresh
};

const persistedReducer = persistReducer(persistConfig, rootReducer);

export const store = configureStore({
  reducer: persistedReducer,
  middleware: (getDefaultMiddleware) =>
    getDefaultMiddleware({
      serializableCheck: {
        ignoredActions: [FLUSH, REHYDRATE, PAUSE, PERSIST, PURGE, REGISTER],
      },
    }),
});

export const persistor = persistStore(store);
